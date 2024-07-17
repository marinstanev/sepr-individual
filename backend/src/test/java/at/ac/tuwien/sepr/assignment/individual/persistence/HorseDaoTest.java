package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest extends TestBase {

  @Autowired
  HorseDao horseDao;

  @Test
  public void searchByBreedWelFindsThreeHorses() {
    var searchDto = new HorseSearchDto(null, null, null, null, "Wel", null);
    var horses = horseDao.search(searchDto);
    assertNotNull(horses);
    assertThat(horses)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Horse())
                .setId(-32L)
                .setName("Luna")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2018, 10, 10))
                .setHeight(1.62f)
                .setWeight(670)
                .setBreedId(-19L),
            (new Horse())
                .setId(-21L)
                .setName("Bella")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2003, 7, 6))
                .setHeight(1.50f)
                .setWeight(580)
                .setBreedId(-19L),
            (new Horse())
                .setId(-2L)
                .setName("Hugo")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2020, 2, 20))
                .setHeight(1.20f)
                .setWeight(320)
                .setBreedId(-20L));
  }

  @Test
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() {
    var searchDto = new HorseSearchDto(null, null,
        LocalDate.of(2017, 3, 5),
        LocalDate.of(2018, 10, 10),
        null, null);
    var horses = horseDao.search(searchDto);
    assertNotNull(horses);
    assertThat(horses)
        .hasSize(4)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Horse())
                .setId(-24L)
                .setName("Rocky")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2018, 8, 19))
                .setHeight(1.42f)
                .setWeight(480)
                .setBreedId(-6L),
            (new Horse())
                .setId(-26L)
                .setName("Daisy")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2017, 12, 1))
                .setHeight(1.28f)
                .setWeight(340)
                .setBreedId(-9L),
            (new Horse())
                .setId(-31L)
                .setName("Leo")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2017, 3, 5))
                .setHeight(1.70f)
                .setWeight(720)
                .setBreedId(-8L),
            (new Horse())
                .setId(-32L)
                .setName("Luna")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2018, 10, 10))
                .setHeight(1.62f)
                .setWeight(670)
                .setBreedId(-19L));
  }

  @Test
  public void getByIdReturnsNotFoundException() {
    Assertions.assertThrows(NotFoundException.class, () -> horseDao.getById(5));
  }

  @Test
  public void update() throws NotFoundException {
    HorseDetailDto toUpdate =
        new HorseDetailDto(-32L, "Test Update", Sex.MALE, LocalDate.of(2000, 5, 22), 1.22f, 450, null);
    horseDao.update(toUpdate);
    Assertions.assertEquals(horseDao.getById(-32).getName(), "Test Update");
  }

  @Test
  public void searchReturnsMatchingHorses() {
    HorseSearchDto params = new HorseSearchDto("Mist", null, null, null, null, null);
    Collection<Horse> matchingHorses = horseDao.search(params);
    assertThat(matchingHorses).hasSize(2);
    Horse matchingHorse = matchingHorses.iterator().next();
    assertThat(matchingHorse.getId()).isEqualTo(-23L);
    assertThat(matchingHorse.getName()).isEqualTo("Misty");
    assertThat(matchingHorse.getSex()).isEqualTo(Sex.FEMALE);
    assertThat(matchingHorse.getHeight()).isEqualTo(1.32f);
    assertThat(matchingHorse.getWeight()).isEqualTo(360);
    assertThat(matchingHorse.getBreedId()).isEqualTo(-7L);
  }


  @Test
  public void delete() throws NotFoundException {
    long id = -6L;
    horseDao.delete(id);
    Assertions.assertThrows(NotFoundException.class, () -> horseDao.getById(id));
  }
}
