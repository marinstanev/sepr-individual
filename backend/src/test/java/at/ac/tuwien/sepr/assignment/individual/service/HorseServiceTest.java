package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseServiceTest extends TestBase {

  @Autowired
  HorseService horseService;

  @Test
  public void searchByBreedWelFindsThreeHorses() {
    var searchDto = new HorseSearchDto(null, null, null, null, "Wel", null);
    var horses = horseService.search(searchDto);
    assertNotNull(horses);
    // We don't have height and weight of the horses here, so no reason to test for them.
    assertThat(horses)
        .extracting("id", "name", "sex", "dateOfBirth", "breed.name")
        .as("ID, Name, Sex, Date of Birth, Breed Name")
        .containsOnly(
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10), "Welsh Cob"),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6), "Welsh Cob"),
            tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20), "Welsh Pony")
        );
  }

  @Test
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() {
    var searchDto = new HorseSearchDto(null, null,
        LocalDate.of(2017, 3, 5),
        LocalDate.of(2018, 10, 10),
        null, null);
    var horses = horseService.search(searchDto);
    assertNotNull(horses);
    assertThat(horses)
        .hasSize(4)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, (h) -> h.breed().name())
        .containsExactlyInAnyOrder(
            tuple(-24L, "Rocky", Sex.MALE, LocalDate.of(2018, 8, 19),
                "Dartmoor Pony"),
            tuple(-26L, "Daisy", Sex.FEMALE, LocalDate.of(2017, 12, 1),
                "Hanoverian"),
            tuple(-31L, "Leo", Sex.MALE, LocalDate.of(2017, 3, 5),
                "Haflinger"),
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10),
                "Welsh Cob"));
  }

  @Test
  public void getByIdReturnsNotFoundException() {
    Assertions.assertThrows(NotFoundException.class, () -> horseService.getById(5));
  }

  @Test
  public void update() throws NotFoundException, ValidationException, ConflictException {
    HorseDetailDto toUpdate =
        new HorseDetailDto(-4L, "Updated", Sex.MALE, LocalDate.of(2000, 5, 22), 1.22f, 355, null);
    horseService.update(toUpdate);
    Assertions.assertEquals(horseService.getById(-4L).name(), "Updated");
  }

  @Test
  public void searchReturnsMatchingHorses() {
    HorseSearchDto params = new HorseSearchDto("Mist", null, null, null, null, null);
    List<HorseListDto> matchingHorses = horseService.search(params).collect(Collectors.toList());
    assertThat(matchingHorses).hasSize(2);
    HorseListDto matchingHorse = matchingHorses.get(1);
    assertThat(matchingHorse.id()).isEqualTo(-19L);
    assertThat(matchingHorse.name()).isEqualTo("Misty");
    assertThat(matchingHorse.sex()).isEqualTo(Sex.FEMALE);
  }


  @Test
  public void delete() throws NotFoundException, ValidationException, ConflictException {
    long id = -5L;
    horseService.delete(id);
    Assertions.assertThrows(NotFoundException.class, () -> horseService.getById(id));
  }
}
