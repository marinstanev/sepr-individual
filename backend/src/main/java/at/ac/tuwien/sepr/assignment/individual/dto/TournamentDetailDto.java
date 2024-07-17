package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.entity.Horse;

import java.time.LocalDate;
import java.util.ArrayList;

public record TournamentDetailDto(
    long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    ArrayList<Horse> participants
) {
}
