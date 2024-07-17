package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles({"test", "datagen"})
@SpringBootTest
public class BreedDaoTest extends TestBase {
  @Autowired
  BreedDao dao;

  @Test
  public void searchForOneExistingBreedSuccessfullyFindsBreed() {
    var foundBreeds = dao.findBreedsById(Set.of(-1L));
    assertNotNull(foundBreeds);
    // This assert is a bit verbose. Just matching the containing elements is enough
    assertAll(
        () -> assertThat(foundBreeds).isNotEmpty(),
        () -> assertThat(foundBreeds.size()).isEqualTo(1),
        () -> assertThat(foundBreeds)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder((new Breed()).setId(-1).setName("Andalusian"))
    );
  }


  @Test
  public void searchForThreeExistingBreedSuccessfullyFindsThreeBreeds() {
    var foundBreeds = dao.findBreedsById(Set.of(-1L, -3L, -11L));
    assertNotNull(foundBreeds);
    assertThat(foundBreeds)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Breed()).setId(-1).setName("Andalusian"),
            (new Breed()).setId(-3).setName("Arabian"),
            (new Breed()).setId(-11).setName("Lipizzaner"));
  }

  @Test
  public void searchForOneExistingAndOneNonexistingBreedSuccessfullyFindsExistingBreed() {
    var foundBreeds = dao.findBreedsById(Set.of(-1L, -99999L));
    assertNotNull(foundBreeds);
    assertThat(foundBreeds)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder((new Breed()).setId(-1).setName("Andalusian"));
  }

  @Test
  public void searchForOneNonexistingBreedSuccessfullyFindsNothing() {
    var foundBreeds = dao.findBreedsById(Set.of(-99999L));
    assertNotNull(foundBreeds);
    assertThat(foundBreeds).isEmpty();
  }

  @Test
  public void returnAllBreeds() {
    var foundBreeds = dao.allBreeds();
    assertNotNull(foundBreeds);
    assertThat(foundBreeds)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Breed()).setId(-1).setName("Andalusian"),
            (new Breed()).setId(-2).setName("Appaloosa"),
            (new Breed()).setId(-3).setName("Arabian"),
            (new Breed()).setId(-4).setName("Belgian Draft"),
            (new Breed()).setId(-5).setName("Connemara Pony"),
            (new Breed()).setId(-6).setName("Dartmoor Pony"),
            (new Breed()).setId(-7).setName("Friesian"),
            (new Breed()).setId(-8).setName("Haflinger"),
            (new Breed()).setId(-9).setName("Hanoverian"),
            (new Breed()).setId(-10).setName("Icelandic Horse"),
            (new Breed()).setId(-11).setName("Lipizzaner"),
            (new Breed()).setId(-12).setName("Oldenburg"),
            (new Breed()).setId(-13).setName("Paint Horse"),
            (new Breed()).setId(-14).setName("Quarter Horse"),
            (new Breed()).setId(-15).setName("Shetland Pony"),
            (new Breed()).setId(-16).setName("Tinker"),
            (new Breed()).setId(-17).setName("Trakehner"),
            (new Breed()).setId(-18).setName("Warmblood"),
            (new Breed()).setId(-19).setName("Welsh Cob"),
            (new Breed()).setId(-20).setName("Welsh Pony"));
  }

  @Test
  public void searchReturnsMatchingBreeds() {
    BreedSearchDto params = new BreedSearchDto("Lipizzaner", null);
    Collection<Breed> matchingBreeds = dao.search(params);
    assertThat(matchingBreeds).hasSize(1);
  }
}
