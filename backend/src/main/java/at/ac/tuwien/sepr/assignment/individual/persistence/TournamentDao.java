package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.entity.TournamentStandings;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding tournaments.
 */
public interface TournamentDao {
  Collection<Long> allParticipants();

  /**
   * Get the tournaments that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   *
   * @param searchParams the parameters to use in searching.
   * @return the tournaments where all given parameters match the given condition.
   */
  Collection<Tournament> search(TournamentSearchDto searchParams);

  /**
   * Create a new tournament and stores it in the persistent data store.
   *
   * @param tournament the horse to create
   * @return the created tournament
   * @throws FatalException if an unexpected exception in the data store occurred.
   */
  Tournament create(TournamentDetailDto tournament) throws FatalException;

  /**
   * Get the tournament-standings with the ID given from the persistent data store.
   *
   * @param id the ID of the tournament-standings to get
   * @return the tournament-standings with ID {@code id}
   */
  TournamentStandings getStandings(long id);

  /**
   * Update persistent data linked to the tournament-standings with the ID given based on the data given in {@code standings}.
   *
   * @param id        the id of the tournament-standings to update
   * @param standings the tournament-standings to update
   * @return the updated tournament standings
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   */
  TournamentStandings update(long id, TournamentStandingsDto standings) throws NotFoundException;

  /**
   * Generate the first round of tournament-standings based on data stored in the system
   * and change old data in the persistent data store.
   *
   * @param id        the id of the tournament-standings which first round should be generated
   * @param standings the tournament-standings which first round should be generated
   * @return the updated tournament-standings
   */
  TournamentStandings generateFirstRound(long id, TournamentStandingsDto standings);
}
