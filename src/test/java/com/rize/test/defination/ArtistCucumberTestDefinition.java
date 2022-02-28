package com.rize.test.defination;

import com.rize.test.model.Artist;
import com.rize.test.model.Category;
import com.rize.test.respository.ArtistRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.util.StringUtils.*;
import static org.springframework.util.StringUtils.hasText;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArtistCucumberTestDefinition {

    private final static String BASE_URI = "http://localhost";

    @LocalServerPort
    private int port;

    @Autowired
    private ArtistRepository artistRepository;

    private ValidatableResponse response;

    private Integer artistId;

    private void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Before
    public void setUp() {
        artistRepository.deleteAll();
    }


    protected RequestSpecification requestSpecification() {
        configureRestAssured();
        return given();
    }

    @Given("Artist exist in system with firstName {string} lastName {string} category {string} dateOfBirth {string} and email {string}")
    public void artistExistInSystemWithFirstNameLastNameCategoryDateOfBirth(String firstName, String lastName, String category, String dateOfBirth, String email) throws ParseException {
        Artist artist = Artist.builder()
                .firstName(firstName)
                .lastName(lastName)
                .category(Category.valueOf(category))
                .birthday(new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirth))
                .email(email)
                .build();

        artistRepository.save(artist);
        artistId = artist.getId();
    }

    @Given("Artist exist in system")
    public void artistExistWithInSystem() {
        Artist artist = Artist.builder()
                .firstName("firstName")
                .lastName("lastName")
                .category(Category.ACTOR)
                .birthday(new Date())
                .email("test@test.com")
                .build();

        artistRepository.save(artist);
        artistId = artist.getId();
    }

    @When("I send a request to create valid artist with firstName {string}, middleName {string} lastName {string}, category {string}, birthday {string}, email {string} and notes {string}")
    @When("I send a request to create inValid artist with firstName {string}, middleName {string} lastName {string}, category {string}, birthday {string}, email {string} and notes {string}")
    public void iSendARequestToCreateValidArtistWithFirstNameMiddleNameLastNameCategoryBirthdayEmailAndNotes(String firstName, String middleName, String lastName, String category, String birthday, String email, String notes) {
        response = requestSpecification().contentType(ContentType.JSON)
                .body(artist(firstName, middleName, lastName, category, birthday, email, notes).toJSONString())
                .when().post("/artists").then();
    }

    @When("I send request to delete existing artist")
    public void iSendRequestToDeleteExistingArtist() {
        deleteArtist(artistId);
    }

    @When("I send request to delete non existing artist")
    public void iSendRequestToDeleteNonExistingArtist() {
        deleteArtist(200);
    }

    @When("I send request to find artist with category {string} birth-month {string} search {string}")
    public void iSendRequestToFindArtistWithCategoryBirthMonthBirthMonthSearch(String category, String birthMonth, String search) {
        findArtists("?" + buildSearchString(category, birthMonth, search));
    }

    @When("I send request to find all artists")
    public void iSendRequestToFindAllArtists() {
        findArtists("");
    }

    @When("I send request to find artist by id")
    public void iSendRequestToFindArtistById() {
        findArtists("/" + artistId);
    }

    @Then("the response will return status {int} and firstName {string}, middleName {string}, lastName {string}, category {string}, birthday {string}, email {string} and notes {string}")
    public void theResponseWillReturnStatusAndFirstNameMiddleNameLastNameCategoryBirthdayEmailAndNotes(
            int status, String firstName, String middleName, String lastName, String category, String birthday,
            String email, String notes) throws ParseException {
        Artist artist = response.assertThat()
                .statusCode(equalTo(status))
                .extract()
                .as(Artist.class);

        Artist expectedArtist = Artist.builder()
                .firstName(firstName)
                .middleName(isNull(middleName))
                .lastName(lastName)
                .category(Category.valueOf(category))
                .birthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday))
                .email(email)
                .notes(isNull(notes))
                .build();

        assertThat(artist).usingRecursiveComparison().ignoringFields("birthday").isEqualTo(expectedArtist);
        assertThat(toDate(artist.getBirthday())).isEqualTo(toDate(expectedArtist.getBirthday()));
    }

    @Then("the response will return status {int} and error contains {string}")
    public void theResponseWillReturnStatusAndErrorContains(int status, String message) {
        String error = response.assertThat()
                .statusCode(equalTo(status))
                .extract().path("errors[0]");

        assertThat(error).contains(message);
    }

    @Then("the response will return status {int}")
    public void theResponseWillReturnStatus(int status) {
        response.assertThat()
                .statusCode(equalTo(status))
                .extract();
    }

    @And("Artist table contain {int} rows")
    public void artistTableContainRows(int rows) {
        assertThat(artistRepository.count()).isEqualTo(rows);
    }

    @And("result will contain {int} entries")
    public void resultWillContainNumberOfRecordsEntries(int numberOfRecords) {
        Artist[] artists;
        try {
            artists = response.assertThat()
                    .extract()
                    .as(Artist[].class);
        } catch (Exception ex) {
            artists = new Artist[]{};
        }

        assertThat(artists.length).isEqualTo(numberOfRecords);
    }

    private String buildSearchString(String category, String birthMonth, String search) {
        String searchString = "";
        if (hasText(category)) {
            searchString = "category=" + category + "&";
        }

        if (hasText(birthMonth)) {
            searchString = searchString + "birthday-month=" + birthMonth + "&";
        }

        if (hasText(search)) {
            searchString = searchString + "search=" + search + "&";
        }
        return searchString;
    }

    private JSONObject artist(String firstName, String middleName, String lastName, String category, String birthday, String email, String notes) {
        JSONObject body = new JSONObject();
        body.put("firstName", firstName);
        body.put("middleName", isNull(middleName));
        body.put("lastName", lastName);
        body.put("category", category);
        body.put("birthday", birthday);
        body.put("email", email);
        body.put("notes", isNull(notes));

        return body;
    }

    private void deleteArtist(int artistId) {
        response = requestSpecification()
                .when().delete("/artists/" + artistId).then();
    }

    private void findArtists(String query) {
        response = requestSpecification()
                .when().get("/artists" + query).then();
    }

    private LocalDate toDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private String isNull(String value){
        return hasText(value) ? value : null;
    }
}
