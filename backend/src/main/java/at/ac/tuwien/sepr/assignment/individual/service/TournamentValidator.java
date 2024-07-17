package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao horseDao;

  public TournamentValidator(HorseDao horseDao) {
    this.horseDao = horseDao;
  }

  public void validateForCreate(TournamentDetailDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    List<String> validationErrors = new ArrayList<>();

    if (tournament.name() == null) {
      validationErrors.add("No name given");
    } else if (tournament.name().isBlank()) {
      validationErrors.add("Tournament name is given but blank");
    } else if (tournament.name().length() > 256) {
      validationErrors.add("Tournament name is too long: longer than 256 characters");
    }

    if (tournament.startDate() == null) {
      validationErrors.add("No start date set");
    } else if (tournament.endDate() == null) {
      validationErrors.add("No end date set");
    } else if (tournament.startDate().isAfter(tournament.endDate())) {
      validationErrors.add("Start date cannot be after end date");
    } else if (tournament.endDate().isBefore(tournament.startDate())) {
      validationErrors.add("End date cannot be before start date");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }
}
