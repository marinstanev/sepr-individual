package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import java.util.Collection;
import java.util.Set;

public interface BreedDao {
  /**
   * Retrieve all breeds from the persistent data store.
   *
   * @return a stream of all stored breeds.
   */
  Collection<Breed> allBreeds();

  /**
   * Retrieve all breeds from the persistent data store, that have one of the given IDs.
   * Note that if for one ID no breed is found, this method does not throw an error.
   *
   * @param breedIds the set of IDs to find breeds for.
   * @return a stream of all found breeds with an ID in {@code breedIds}
   */
  Collection<Breed> findBreedsById(Set<Long> breedIds);

  /**
   * Retrieve all breeds from the persistent data store, that match the given parameters.
   * The parameters may include a limit on the amount of results to return.
   *
   * @param searchParams parameters to search breeds by
   * @return a stream of breeds matching the parameters
   */
  Collection<Breed> search(BreedSearchDto searchParams);
}
