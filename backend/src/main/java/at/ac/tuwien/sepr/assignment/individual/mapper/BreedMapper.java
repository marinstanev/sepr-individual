package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class BreedMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Convert a breed entity object to a {@link BreedDto}.
   *
   * @param breed the breed standings to convert
   * @return the converted {@link BreedDto}
   */
  public BreedDto entityToDto(Breed breed) {
    LOG.trace("entityToDto({})", breed);
    return new BreedDto(breed.getId(), breed.getName());
  }
}
