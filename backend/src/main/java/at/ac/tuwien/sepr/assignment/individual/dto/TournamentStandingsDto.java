package at.ac.tuwien.sepr.assignment.individual.dto;

public record TournamentStandingsDto(
        Long id,
        String name,
        TournamentDetailParticipantDto[] participants,
        TournamentStandingsTreeDto tree
) {
}
