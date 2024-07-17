package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.entity.TournamentStandings;
import at.ac.tuwien.sepr.assignment.individual.entity.TournamentStandingsTree;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TournamentJdbcDao implements TournamentDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "tournament";
  private static final String SQL_SELECT_SEARCH = "SELECT * FROM " + TABLE_NAME
      + " WHERE (:name IS NULL OR UPPER(name) LIKE UPPER('%'||:name||'%'))"
      + " AND (:startDate IS NULL OR :startDate <= date_of_finish)"
      + " AND (:endDate IS NULL OR :endDate >= date_of_start)";

  private static final String SQL_INSERT_TOURNAMENT = "INSERT INTO " + TABLE_NAME
      + " (name, date_of_start, date_of_finish) VALUES (?, ?, ?)";

  private static final String SQL_INSERT_PARTICIPANT = "INSERT INTO participant"
      + " (id_tournament, id_participant, entry_number, round_reached) VALUES (?, ?, ?, ?)";

  private static final String SQL_SELECT_PARTICIPANTS_BY_ID = "SELECT id_participant, horse.name, date_of_birth, entry_number, round_reached FROM ("
      + "((SELECT * FROM tournament WHERE id = ?) tmp JOIN participant on tmp.id = participant.id_tournament) "
      + "JOIN horse on id_participant = horse.id"
      + ")";

  private static final String SQL_SELECT_TOURNAMENT_NAME = "SELECT name FROM " + TABLE_NAME
      + " WHERE id = ?";

  private static final String SQL_UPDATE_ENTRY_NUM = "UPDATE participant"
      + " SET entry_number = ?"
      + " WHERE id_participant = ?"
      + " AND id_tournament = ?";

  private static final String SQL_UPDATE = "UPDATE participant"
      + " SET entry_number = ?"
      + " , round_reached = ?"
      + " WHERE id_participant = ?"
      + " AND id_tournament = ?";

  private static final String SQL_UPDATE_ROUND_REACHED = "UPDATE participant"
      + " SET round_reached = ?"
      + " WHERE id_participant = ?"
      + " AND id_tournament = ?";

  private static final String SQL_SELECT_PARTICIPANTS_OLD_TOURNAMENTS_BY_ID =
      "SELECT id_tournament, id_participant, horse.name, horse.date_of_birth, entry_number, round_reached, date_of_start "
          + "FROM (((SELECT * FROM tournament WHERE id != ?) tmp JOIN participant on tmp.id = participant.id_tournament) "
          + "JOIN horse on id_participant = horse.id) "
          + "WHERE date_of_start >= DATEADD(MONTH, -12, CAST((SELECT date_of_start FROM tournament WHERE id = ?) AS DATE)) "
          + "AND round_reached > 1";

  private static final String SQL_ALL_PARTICIPANTS = "SELECT * FROM participant";

  private int entryNum;
  private int roundReached;

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  public Collection<Long> allParticipants() {
    LOG.trace("allParticipants()");
    return jdbcTemplate.query(SQL_ALL_PARTICIPANTS, this::idMapRow);
  }

  private Long idMapRow(ResultSet resultSet, int i) throws SQLException {
    return resultSet.getLong("id_participant");
  }

  @Override
  public Collection<Tournament> search(TournamentSearchDto searchParams) {
    LOG.trace("search({})", searchParams);
    var params = new BeanPropertySqlParameterSource(searchParams);

    Collection<Tournament> toRet = jdbcNamed.query(SQL_SELECT_SEARCH, params, this::mapRow);
    return toRet.stream()
        .sorted(Comparator.comparing(Tournament::getStartDate))
        .collect(Collectors.toList());
  }

  @Override
  public Tournament create(TournamentDetailDto tournament) throws FatalException {
    LOG.trace("insert({})", tournament);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement stmt = connection.prepareStatement(SQL_INSERT_TOURNAMENT, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, tournament.name());
      stmt.setDate(2, java.sql.Date.valueOf(tournament.startDate()));
      stmt.setDate(3, java.sql.Date.valueOf(tournament.endDate()));
      return stmt;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key == null) {
      throw new FatalException("Could not extract key for newly created tournament. There is probably a programming error…");
    }

    int updated;
    for (int i = 0; i < tournament.participants().size(); i++) {
      updated = jdbcTemplate.update(SQL_INSERT_PARTICIPANT,
          key,
          tournament.participants().get(i).getId(),
          null, null);
      if (updated == 0) {
        throw new FatalException("Participant could not be created");
      }
    }
    return new Tournament()
        .setId(key.longValue())
        .setName(tournament.name())
        .setStartDate(tournament.startDate())
        .setFinishDate(tournament.endDate())
        ;
  }

  @Override
  public TournamentStandings getStandings(long id) {
    LOG.trace("getStandings({})", id);
    TournamentStandings ret = new TournamentStandings();
    ret.setId(id);
    List<Participant> participantsList = jdbcTemplate.query(SQL_SELECT_PARTICIPANTS_BY_ID, this::participantMapRow, id);
    Participant[] participants = new Participant[8];
    for (int i = 0; i < participantsList.size(); i++) {
      participants[i] = participantsList.get(i);
    }
    ret.setParticipants(participants);

    LinkedList<TournamentStandingsTree> treeList = new LinkedList<>();
    // first round
    for (int i = 8; i >= 1; i--) {
      for (int j = 0; j < participantsList.size(); j++) {
        Participant curr = participantsList.get(j);
        if (curr.getEntryNumber() > 0) {
          if (curr.getEntryNumber() == i) {
            treeList.add(new TournamentStandingsTree(curr));
            if (curr.getRoundReached() == 1) {
              participantsList.remove(curr);
            }
            break;
          }
        }
        if (j == participantsList.size() - 1) {
          treeList.add(new TournamentStandingsTree());
        }
      }
    }
    // second round
    for (int i = 8; i >= 2; i -= 2) {
      if (participantsList.size() == 0) {
        treeList.add(new TournamentStandingsTree());
      } else {
        for (int j = 0; j < participantsList.size(); j++) {
          Participant curr = participantsList.get(j);
          if (curr.getEntryNumber() > 0) {
            if (curr.getEntryNumber() == i | curr.getEntryNumber() == i - 1) {
              treeList.add(new TournamentStandingsTree(curr));
              if (curr.getRoundReached() == 2) {
                participantsList.remove(curr);
              }
              break;
            }
          }
          if (j == participantsList.size() - 1) {
            treeList.add(new TournamentStandingsTree());
          }
        }
      }
    }

    // third round
    int k = 4;
    int z = 9;
    for (int i = 0; i < 2; i++) {
      if (participantsList.size() == 0) {
        treeList.add(new TournamentStandingsTree());
      } else {
        for (int j = 0; j < participantsList.size(); j++) {
          Participant curr = participantsList.get(j);
          if (curr.getEntryNumber() > 0) {
            if (curr.getEntryNumber() > k && curr.getEntryNumber() < z) {
              treeList.add(new TournamentStandingsTree(curr));
              if (curr.getRoundReached() == 3) {
                participantsList.remove(curr);
              }
              break;
            }
          }
          if (j == participantsList.size() - 1) {
            treeList.add(new TournamentStandingsTree());
          }
        }
      }
      k -= 4;
      z -= 4;
    }

    // fourth round
    if (participantsList.size() == 0) {
      treeList.add(new TournamentStandingsTree());
    }
    for (int i = 0; i < participantsList.size(); i++) {
      Participant curr = participantsList.get(i);
      if (curr.getEntryNumber() > 0 && curr.getRoundReached() == 4) {
        treeList.add(new TournamentStandingsTree(curr));
        break;
      }
      if (i == participantsList.size() - 1) {
        treeList.add(new TournamentStandingsTree());
      }
    }

    ret.setTree(buildTree(treeList.pollLast(), treeList));

    ret.setName(jdbcTemplate.query(SQL_SELECT_TOURNAMENT_NAME, this::stringMapRow, id).get(0));
    return ret;
  }

  private TournamentStandingsTree buildTree(TournamentStandingsTree root, LinkedList<TournamentStandingsTree> treeList) {
    LOG.trace("buildTree");
    root.setBranches(new TournamentStandingsTree[] {treeList.pollLast(), treeList.pollLast()});
    root.getBranches()[0].setBranches(new TournamentStandingsTree[] {treeList.pollLast(), treeList.pollLast()});
    root.getBranches()[1].setBranches(new TournamentStandingsTree[] {treeList.pollLast(), treeList.pollLast()});
    root.getBranches()[0].getBranches()[0].setBranches(new TournamentStandingsTree[] {treeList.pollLast(), treeList.pollLast()});
    root.getBranches()[0].getBranches()[1].setBranches(new TournamentStandingsTree[] {treeList.pollLast(), treeList.pollLast()});
    root.getBranches()[1].getBranches()[0].setBranches(new TournamentStandingsTree[] {treeList.pollLast(), treeList.pollLast()});
    root.getBranches()[1].getBranches()[1].setBranches(new TournamentStandingsTree[] {treeList.pollLast(), treeList.pollLast()});
    return root;
  }

  @Override
  public TournamentStandings update(long id, TournamentStandingsDto standings) throws NotFoundException {
    LOG.trace("update({})", standings);
    this.entryNum = 1;
    Map<Long, TournamentDetailParticipantDto> participantsMap = new HashMap<>();
    for (int i = 0; i < standings.participants().length; i++) {
      participantsMap.put(standings.participants()[i].horseId(), standings.participants()[i]);
    }
    participantsMap = updateParticipantsEntryNum(id, standings.tree(), participantsMap);
    for (Map.Entry<Long, TournamentDetailParticipantDto> entry : participantsMap.entrySet()) {
      int updated = jdbcTemplate.update(SQL_UPDATE,
          null,
          null,
          entry.getKey(),
          id);
      if (updated == 0) {
        throw new NotFoundException("");
      }
    }

    updateParticipantsRoundReached(id, standings.tree());
    return getStandings(id);
  }

  @Override
  public TournamentStandings generateFirstRound(long id, TournamentStandingsDto standings) {
    LOG.trace("generateFirstRound({})", standings);
    List<Participant> participantsList = jdbcTemplate.query(SQL_SELECT_PARTICIPANTS_OLD_TOURNAMENTS_BY_ID, this::participantMapRow, id, id);
    Map<Long, Integer> participantPointsMap = new HashMap<>();
    for (TournamentDetailParticipantDto participant : standings.participants()) {
      participantPointsMap.put(participant.horseId(), 0);
    }
    int points = 0;
    int currPoints;
    for (Participant curr : participantsList) {
      if (curr.getRoundReached() == 2) {
        points = 1;
      } else if (curr.getRoundReached() == 3) {
        points = 3;
      } else if (curr.getRoundReached() == 4) {
        points = 5;
      }
      currPoints = participantPointsMap.get(curr.getHorseId());
      participantPointsMap.put(curr.getHorseId(), currPoints + points);
      points = 0;
    }
    LinkedList<Map.Entry<Long, Integer>> participantPointsList = new LinkedList<>(participantPointsMap.entrySet());
    participantPointsList.sort(Map.Entry.comparingByValue());
    LinkedList<TournamentStandingsTree> treeList = new LinkedList<>();
    Participant[] participants = new Participant[8];
    for (int i = 0; i < 8; i++) {
      Long curr;
      if (i % 2 == 0) {
        curr = participantPointsList.pollLast().getKey();
      } else {
        curr = participantPointsList.poll().getKey();
      }
      for (int j = 0; j < standings.participants().length; j++) {
        Participant p = new Participant()
            .setHorseId(standings.participants()[j].horseId())
            .setName(standings.participants()[j].name())
            .setDateOfBirth(standings.participants()[j].dateOfBirth())
            .setEntryNumber(i + 1)
            .setRoundReached(1);
        if (p.getHorseId().equals(curr)) {
          participants[i] = p;
          treeList.add(new TournamentStandingsTree(p));
          break;
        }
      }
    }
    Collections.reverse(treeList);
    for (int i = 0; i < 7; i++) {
      treeList.add(new TournamentStandingsTree());
    }
    TournamentStandings ret = new TournamentStandings();
    ret.setTree(buildTree(treeList.pollLast(), treeList));
    ret.setId(standings.id());
    ret.setName(standings.name());
    ret.setParticipants(participants);
    return ret;
  }

  private Map<Long, TournamentDetailParticipantDto> updateParticipantsEntryNum(long tournamentId, TournamentStandingsTreeDto curr,
                                                                               Map<Long, TournamentDetailParticipantDto> participantsMap)
      throws NotFoundException {
    LOG.trace("updateParticipantsEntryNum({}, {}, {})", tournamentId, curr, participantsMap);
    if (curr == null) {
      return participantsMap;
    }

    if (curr.branches() != null) {
      updateParticipantsEntryNum(tournamentId, curr.branches()[0], participantsMap);
      updateParticipantsEntryNum(tournamentId, curr.branches()[1], participantsMap);
    } else {
      if (curr.thisParticipant() != null) {
        participantsMap.remove(curr.thisParticipant().horseId());

        int updated = jdbcTemplate.update(SQL_UPDATE_ENTRY_NUM,
            curr.thisParticipant().entryNumber() == 0 ? entryNum : curr.thisParticipant().entryNumber(),
            curr.thisParticipant().horseId(),
            tournamentId);
        if (updated == 0) {
          throw new NotFoundException("");
        }

      }
      entryNum++;
    }
    return participantsMap;
  }

  private void updateParticipantsRoundReached(long tournamentId, TournamentStandingsTreeDto curr) throws NotFoundException {
    LOG.trace("updateParticipantsRoundReached({}, {})", tournamentId, curr);
    if (curr == null) {
      return;
    }

    if (curr.branches() != null) {
      updateParticipantsRoundReached(tournamentId, curr.branches()[0]);
      updateParticipantsRoundReached(tournamentId, curr.branches()[1]);
      roundReached++;
    } else {
      roundReached = 1;
    }

    if (curr.thisParticipant() != null) {
      int updated = jdbcTemplate.update(SQL_UPDATE_ROUND_REACHED,
          roundReached,
          curr.thisParticipant().horseId(),
          tournamentId);
      if (updated == 0) {
        throw new NotFoundException("");
      }
    }
  }

  private String stringMapRow(ResultSet result, int rownum) throws SQLException {
    LOG.trace("stringMapRow({}, {})", result, rownum);
    return result.getString("name");
  }

  private Tournament mapRow(ResultSet result, int rownum) throws SQLException {
    LOG.trace("mapRow({}, {})", result, rownum);
    return new Tournament()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setStartDate(result.getDate("date_of_start").toLocalDate())
        .setFinishDate(result.getDate("date_of_finish").toLocalDate())
        ;
  }

  private Participant participantMapRow(ResultSet result, int rownum) throws SQLException {
    LOG.trace("participantMapRow({}, {})", result, rownum);
    return new Participant()
        .setHorseId(result.getLong("id_participant"))
        .setName(result.getString("name"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setEntryNumber(result.getInt("entry_number"))
        .setRoundReached(result.getInt("round_reached"))
        ;
  }
}
