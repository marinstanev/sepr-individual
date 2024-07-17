package at.ac.tuwien.sepr.assignment.individual.entity;

import java.time.LocalDate;

/**
 * Represents a participant in the persistent data store.
 */
public class Participant {
  private Long horseId;
  private String name;
  private LocalDate dateOfBirth;
  private Integer entryNumber;
  private Integer roundReached;

  public Participant setHorseId(Long horseId) {
    this.horseId = horseId;
    return this;
  }

  public Participant setName(String name) {
    this.name = name;
    return this;
  }

  public Participant setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    return this;
  }

  public Participant setEntryNumber(Integer entryNumber) {
    this.entryNumber = entryNumber;
    return this;
  }

  public Participant setRoundReached(Integer roundReached) {
    this.roundReached = roundReached;
    return this;
  }

  public Long getHorseId() {
    return horseId;
  }

  public Integer getEntryNumber() {
    return entryNumber;
  }

  public Integer getRoundReached() {
    return roundReached;
  }

  public String getName() {
    return name;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }
}
