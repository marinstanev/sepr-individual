package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class TournamentStandingsValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void validateForGenerateFirstRound(TournamentStandingsDto standings) throws ValidationException {
    LOG.trace("validateForGenerateFirstRound({})", standings);
    List<String> validationErrors = new ArrayList<>();

    for (int i = 0; i < 8; i++) {
      if (standings.participants()[i].entryNumber() != 0) {
        validationErrors.add("First round cannot be generated, because standings are already filled");
        break;
      }
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Generating first round failed", validationErrors);
    }
  }

  public void validateForUpdate(TournamentStandingsDto standings) throws ValidationException {
    LOG.trace("validateForUpdate({})", standings);
    List<String> validationErrors = iterateTree(standings.tree(), 4, new ArrayList<>());

    LinkedList<TournamentDetailParticipantDto> participants = new LinkedList<>();
    participants.add(standings.tree().branches()[0].branches()[0].branches()[0].thisParticipant());
    participants.add(standings.tree().branches()[0].branches()[0].branches()[1].thisParticipant());
    participants.add(standings.tree().branches()[0].branches()[1].branches()[0].thisParticipant());
    participants.add(standings.tree().branches()[0].branches()[1].branches()[1].thisParticipant());
    participants.add(standings.tree().branches()[1].branches()[0].branches()[0].thisParticipant());
    participants.add(standings.tree().branches()[1].branches()[0].branches()[1].thisParticipant());
    participants.add(standings.tree().branches()[1].branches()[1].branches()[0].thisParticipant());
    participants.add(standings.tree().branches()[1].branches()[1].branches()[1].thisParticipant());
    for (int i = 0; i < 8; i++) {
      TournamentDetailParticipantDto curr = participants.poll();
      if (curr != null && participants.contains(curr)) {
        validationErrors.add("Horse cannot participate more then once in the same tournament");
        break;
      }
      participants.addLast(curr);
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Updating standings failed", validationErrors);
    }
  }

  private List<String> iterateTree(TournamentStandingsTreeDto curr, int depth, List<String> validationErrors) {
    if (depth < 2) {
      return validationErrors;
    }
    TournamentStandingsTreeDto left = curr.branches()[0];
    TournamentStandingsTreeDto right = curr.branches()[1];
    if (curr.thisParticipant() != null) {
      if (left.thisParticipant() == null || right.thisParticipant() == null) {
        validationErrors.add("Previous round is not fully set");
        return validationErrors;
      }
    }
    iterateTree(curr.branches()[0], depth - 1, validationErrors);
    iterateTree(curr.branches()[1], depth - 1, validationErrors);
    return validationErrors;
  }
}
