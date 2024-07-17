package at.ac.tuwien.sepr.assignment.individual.entity;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Represents a tournament in the persistent data store.
 */
public class Tournament {
  private Long id;
  private String name;
  private LocalDate startDate;
  private LocalDate finishDate;
  private ArrayList<Horse> participants;

  public Tournament setId(Long id) {
    this.id = id;
    return this;
  }

  public Tournament setName(String name) {
    this.name = name;
    return this;
  }

  public Tournament setStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  public Tournament setFinishDate(LocalDate finishDate) {
    this.finishDate = finishDate;
    return this;
  }

  public Tournament setParticipants(ArrayList<Horse> participants) {
    this.participants = participants;
    return this;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getFinishDate() {
    return finishDate;
  }

  public ArrayList<Horse> getParticipants() {
    return participants;
  }
}
