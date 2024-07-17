package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.entity.TournamentStandings;
import at.ac.tuwien.sepr.assignment.individual.entity.TournamentStandingsTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class TournamentMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Convert a tournament entity object to a {@link TournamentListDto}.
   *
   * @param tournament the horse to convert
   * @return the converted {@link TournamentListDto}
   */
  public TournamentListDto entityToListDto(Tournament tournament) {
    LOG.trace("entityToListDto({})", tournament);
    if (tournament == null) {
      return null;
    }
    return new TournamentListDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getFinishDate()
    );
  }

  /**
   * Convert a tournament entity object to a {@link TournamentDetailDto}.
   *
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentDetailDto}
   */
  public TournamentDetailDto entityToDetailDto(Tournament tournament) {
    LOG.trace("entityToDetailDto({})", tournament);
    if (tournament == null) {
      return null;
    }
    return new TournamentDetailDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getFinishDate(),
        tournament.getParticipants()
    );
  }

  /**
   * Convert a tournament standings entity object to a {@link TournamentStandingsDto}.
   *
   * @param standings the tournament standings to convert
   * @return the converted {@link TournamentStandingsDto}
   */
  public TournamentStandingsDto entityToTournamentStandingsDto(TournamentStandings standings) {
    LOG.trace("entityToTournamentStandingsDto({})", standings);
    if (standings == null) {
      return null;
    }
    return new TournamentStandingsDto(
        standings.getId(),
        standings.getName(),
        entityToTournamentDetailParticipantDtoArr(standings.getParticipants()),
        entityToTournamentStandingsTreeDto(standings.getTree())
    );
  }

  /**
   * Convert a participant entity array object to a {@link TournamentDetailParticipantDto array object}.
   *
   * @param participants the participant entity array to convert
   * @return the converted {@link TournamentDetailParticipantDto array}
   */
  private TournamentDetailParticipantDto[] entityToTournamentDetailParticipantDtoArr(Participant[] participants) {
    LOG.trace("entityToTournamentDetailParticipantDtoArr({})", (Object) participants);
    if (participants == null) {
      return null;
    }
    TournamentDetailParticipantDto[] ret = new TournamentDetailParticipantDto[participants.length];
    for (int i = 0; i < participants.length; i++) {
      ret[i] = new TournamentDetailParticipantDto(participants[i].getHorseId(), participants[i].getName(),
          participants[i].getDateOfBirth(), participants[i].getEntryNumber(), participants[i].getRoundReached());
    }
    return ret;
  }

  /**
   * Convert a tournament standings tree entity object to a {@link TournamentStandingsTreeDto}.
   *
   * @param tree the tournament standings tree to convert
   * @return the converted {@link TournamentStandingsTreeDto}
   */
  public TournamentStandingsTreeDto entityToTournamentStandingsTreeDto(TournamentStandingsTree tree) {
    LOG.trace("entityToTournamentStandingsTreeDto({})", tree);
    if (tree == null) {
      return null;
    }
    TournamentDetailParticipantDto participant;
    if (tree.getThisParticipant() == null) {
      participant = null;
    } else {
      participant = new TournamentDetailParticipantDto(tree.getThisParticipant().getHorseId(), tree.getThisParticipant().getName(),
          tree.getThisParticipant().getDateOfBirth(), tree.getThisParticipant().getEntryNumber(), tree.getThisParticipant().getRoundReached());
    }
    return new TournamentStandingsTreeDto(
        participant,
        entityToTournamentStandingsTreeArr(tree.getBranches())
    );
  }

  /**
   * Convert a tournament standings tree entity array object to a {@link TournamentStandingsTreeDto array object}.
   *
   * @param branches the tournament standings tree array to convert
   * @return the converted {@link TournamentStandingsTreeDto array}
   */
  private TournamentStandingsTreeDto[] entityToTournamentStandingsTreeArr(TournamentStandingsTree[] branches) {
    LOG.trace("entityToTournamentStandingsTreeArr({})", (Object) branches);
    if (branches == null) {
      return null;
    }
    TournamentStandingsTreeDto[] ret = new TournamentStandingsTreeDto[branches.length];
    for (int i = 0; i < branches.length; i++) {
      ret[i] = entityToTournamentStandingsTreeDto(branches[i]);
    }
    return ret;
  }
}
