package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents tournament standings in the persistent data store.
 */
public class TournamentStandings {
  private Long id;
  private String name;
  private Participant[] participants;
  private TournamentStandingsTree tree;

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParticipants(Participant[] participants) {
    this.participants = participants;
  }

  public void setTree(TournamentStandingsTree tree) {
    this.tree = tree;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Participant[] getParticipants() {
    return participants;
  }

  public TournamentStandingsTree getTree() {
    return tree;
  }
}
