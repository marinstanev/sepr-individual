package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {
  static final String BASE_PATH = "/tournaments";
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final TournamentService service;

  public TournamentEndpoint(TournamentService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<TournamentListDto> searchTournaments(TournamentSearchDto searchParams) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParams);
    return service.search(searchParams);
  }

  @PostMapping
  public TournamentDetailDto create(@RequestBody TournamentDetailDto toCreate) throws ValidationException, ConflictException {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Body of request:\n{}", toCreate);
    try {
      return service.create(toCreate);
    } catch (FatalException | NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament to add not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping("standings/{id}")
  public TournamentStandingsDto getStandingsTree(@PathVariable long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getStandings(id);
    } catch (FatalException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament standings to get not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PutMapping("standings/{id}")
  public TournamentStandingsDto update(@PathVariable long id, @RequestBody TournamentStandingsDto standings) throws NotFoundException, ValidationException {
    LOG.info("PUT " + BASE_PATH + "/{}", standings);
    LOG.debug("Body of request:\n{}", standings);
    try {
      return service.update(id, standings);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament standings to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PutMapping("firstRound/{id}")
  public TournamentStandingsDto generateFirstRound(@PathVariable long id, @RequestBody TournamentStandingsDto standings)
      throws NotFoundException, ValidationException {
    LOG.info("PUT " + BASE_PATH + "/{}", standings);
    LOG.debug("Body of request:\n{}", standings);
    try {
      return service.generateFirstRound(id, standings);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament standings to generate first round for not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
