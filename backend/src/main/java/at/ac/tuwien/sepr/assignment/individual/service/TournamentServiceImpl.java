package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {
  private final TournamentDao dao;
  private final TournamentMapper mapper;
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentStandingsValidator standingsValidator;
  private final TournamentValidator validator;

  public TournamentServiceImpl(TournamentDao dao, TournamentMapper mapper, TournamentStandingsValidator standingsValidator, TournamentValidator validator) {
    this.dao = dao;
    this.mapper = mapper;
    this.standingsValidator = standingsValidator;
    this.validator = validator;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParams) {
    LOG.trace("search({})", searchParams);
    var tournaments = dao.search(searchParams);

    return tournaments.stream()
        .map(mapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentDetailDto toCreate) throws FatalException, ValidationException, ConflictException {
    LOG.trace("create({})", toCreate);
    validator.validateForCreate(toCreate);
    var created = dao.create(toCreate);
    return mapper.entityToDetailDto(created);
  }

  @Override
  public TournamentStandingsDto getStandings(long id) {
    LOG.trace("getStandings({})", id);
    return mapper.entityToTournamentStandingsDto(dao.getStandings(id));
  }

  @Override
  public TournamentStandingsDto update(long id, TournamentStandingsDto standings) throws NotFoundException, ValidationException {
    LOG.trace("update({})", standings);
    standingsValidator.validateForUpdate(standings);
    return mapper.entityToTournamentStandingsDto(dao.update(id, standings));
  }

  @Override
  public TournamentStandingsDto generateFirstRound(long id, TournamentStandingsDto standings) throws NotFoundException, ValidationException {
    LOG.trace("generateFirstRound({})", standings);
    standingsValidator.validateForGenerateFirstRound(standings);
    TournamentStandingsDto ret = mapper.entityToTournamentStandingsDto(dao.generateFirstRound(id, standings));
    update(ret.id(), ret);
    return ret;
  }
}
