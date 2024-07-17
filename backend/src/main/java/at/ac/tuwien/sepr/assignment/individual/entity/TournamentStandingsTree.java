package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents a tournament standings tree in the persistent data store.
 */
public class TournamentStandingsTree {
  private Participant thisParticipant;
  private TournamentStandingsTree[] branches;

  public TournamentStandingsTree() {
  }

  public TournamentStandingsTree(Participant thisParticipant) {
    this.thisParticipant = thisParticipant;
  }

  public void setBranches(TournamentStandingsTree[] branches) {
    this.branches = branches;
  }

  public Participant getThisParticipant() {
    return thisParticipant;
  }

  public TournamentStandingsTree[] getBranches() {
    return branches;
  }
}
