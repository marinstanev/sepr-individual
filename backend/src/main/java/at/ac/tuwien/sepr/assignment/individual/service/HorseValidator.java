package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.persistence.BreedDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final BreedDao breedDao;
  private final TournamentDao tournamentDao;

  public HorseValidator(BreedDao breedDao, TournamentDao tournamentDao) {
    this.breedDao = breedDao;
    this.tournamentDao = tournamentDao;
  }

  public void validateForUpdate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validate(horse, validationErrors);

    List<Breed> breeds = breedDao.allBreeds().stream().toList();
    int count = 0;
    for (Breed breed : breeds) {
      if (horse.breed() != null && !horse.breed().equals(new BreedDto(breed.getId(), breed.getName()))) {
        count++;
      }
    }
    if (count == breeds.size()) {
      validationErrors.add("The chosen breed does not exist in the persistent data store");
    }

    if (horse.sex() == null) {
      validationErrors.add("Sex must be set");
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("No date of birth set");
    } else if (horse.dateOfBirth().isAfter(LocalDate.now())) {
      validationErrors.add("Horse cannot be born in the future");
    }

    if (horse.height() <= 0) {
      validationErrors.add("Height cannot be negative or zero");
    }

    if (horse.weight() <= 0) {
      validationErrors.add("Weight cannot be negative or zero");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  private void validate(HorseDetailDto horse, List<String> validationErrors) {
    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    if (horse.name() == null) {
      validationErrors.add("No name given");
    } else if (horse.name().isBlank()) {
      validationErrors.add("Horse name is given but blank");
    } else if (horse.name().length() > 256) {
      validationErrors.add("Horse name is too long: longer than 256 characters");
    }
  }

  public void validateForCreate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    validate(horse, validationErrors);

    if (horse.sex() == null) {
      validationErrors.add("Sex must be set");
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("No date of birth set");
    } else if (horse.dateOfBirth().isAfter(LocalDate.now())) {
      validationErrors.add("Horse cannot be born in the future");
    }

    if (horse.breed() != null) {
      List<Breed> breeds = breedDao.allBreeds().stream().toList();
      int count = 0;
      for (Breed breed : breeds) {
        if (!horse.breed().equals(new BreedDto(breed.getId(), breed.getName()))) {
          count++;
        }
      }
      if (count == breeds.size()) {
        validationErrors.add("The chosen breed does not exist in the persistent data store");
      }
    }

    if (horse.height() <= 0) {
      validationErrors.add("Height cannot be negative or zero");
    }

    if (horse.weight() <= 0) {
      validationErrors.add("Weight cannot be negative or zero");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

  public void validateForDelete(long id) throws ValidationException {
    LOG.trace("validateForDelete({})", id);
    List<String> validationErrors = new ArrayList<>();

    LinkedList<Long> participants = new LinkedList<>(tournamentDao.allParticipants());
    for (Long participantId : participants) {
      if (Objects.equals(id, participantId)) {
        validationErrors.add("The horse cannot be deleted, because it participates in a tournament");
        break;
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }
}
