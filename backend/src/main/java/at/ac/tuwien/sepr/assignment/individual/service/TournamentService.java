package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

/**
 * Service for working with tournaments.
 */
public interface TournamentService {

  /**
   * Search for tournaments in the persistent data store matching all provided fields.
   *
   * @param searchParams the search parameters to use in filtering.
   * @return the tournaments where the given fields match the given condition.
   */
  Stream<TournamentListDto> search(TournamentSearchDto searchParams);

  /**
   * Create a new tournament in the system.
   *
   * @param tournament the horse to create
   * @return the created tournament
   * @throws FatalException      if an unexpected error in the layers below occurred
   * @throws ValidationException if the argument is semantically incorrect
   * @throws ConflictException   if the argument conflicts with the current state of the system
   * @throws NotFoundException   if the argument is linked to existing entity that is currently not present in the system
   */
  TournamentDetailDto create(TournamentDetailDto tournament) throws FatalException, ValidationException, ConflictException, NotFoundException;

  /**
   * Get the tournament-standings with the ID given.
   *
   * @param id the ID of the tournament-standings to get
   * @return the tournament-standings with the ID given
   */
  TournamentStandingsDto getStandings(long id);

  /**
   * Update persistent data linked to the tournament-standings with the ID given based on the data given in {@code standings}.
   *
   * @param id        the id of the tournament-standings to update
   * @param standings the tournament-standings to update
   * @return the updated tournament-standings
   * @throws ValidationException if the argument is semantically incorrect
   * @throws NotFoundException   if the horse with given ID does not exist in the persistent data store
   */
  TournamentStandingsDto update(long id, TournamentStandingsDto standings) throws NotFoundException, ValidationException;

  /**
   * Generate the first round of tournament-standings based on data stored in the system.
   *
   * @param id        the id of the tournament-standings which first round should be generated
   * @param standings the tournament-standings which first round should be generated
   * @return the updated tournament-standings
   * @throws ValidationException if the argument is semantically incorrect
   * @throws NotFoundException   if the arguments are linked to existing entity that is currently not present in the system
   */
  TournamentStandingsDto generateFirstRound(long id, TournamentStandingsDto standings) throws NotFoundException, ValidationException;
}
