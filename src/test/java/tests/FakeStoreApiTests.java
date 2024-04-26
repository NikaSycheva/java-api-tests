package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.fakeapiusers.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("fakestoreapi")
public class FakeStoreApiTests {
    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI = "https://fakestoreapi.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), //логируем все что отправляем и все что полчаем в консоль идеи
                new AllureRestAssured());
    }

    @Test
    @DisplayName("Получить список всех пользователей")
    public void getAllUsersTest() {
        given().get("/users")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Получаем информацию о пользователе по его id")
    public void getSingleUserTest() {
        int userId = 1;
        UserRoot response = given()
                .pathParam("userId", userId)
                .get("/users/{userId}")
                .then()
                .statusCode(200)
                .extract().as(UserRoot.class);

        Assertions.assertEquals(userId, response.getId());
        Assertions.assertTrue(response.getAddress().getZipcode().matches("\\d{5}-\\d{4}"));


        //если хочу получить только имя
        Name userName = given()
                .pathParam("userId", userId)
                .get("/users/{userId}")
                .then()
                .extract().jsonPath().getObject("name", Name.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    @DisplayName("Приходит столько пользователей, сколько указали в лимите")
    void getAllUsersWithLimitTest(int limitSize) {
        List<UserRoot> users = given().queryParam("limit", limitSize)
                .get("/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>(){});

        Assertions.assertEquals(limitSize, users.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 40})//негативные
    public void getAllUsersWithErrorsParams(int limitSize){
        List<UserRoot> users = given().queryParam("limit", limitSize)
                .get("/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<>(){});

        Assertions.assertNotEquals(limitSize, users.size());
    }

    @Test
    @DisplayName("Проверка сортировки списка пользователей по убыванию")
    void getAllUsersSortByDescTest() {
        String sortType = "desc";
        List<UserRoot> usersSorted = given().queryParam("sort", sortType)
                .get("/users")
                .then()
                .extract().as(new TypeRef<>(){});

        List<UserRoot> usersNotSorted = given()
                .get("/users")
                .then()
                .extract().as(new TypeRef<>(){});

        List<Integer> sortResponseIds = usersSorted.
                stream()
                .map(UserRoot::getId).toList();

        List<Integer> sortedByCode = usersNotSorted
                .stream()
                .map(UserRoot::getId)
                .sorted(Comparator.reverseOrder())
                .toList();

        Assertions.assertNotEquals(usersSorted, usersNotSorted);
        Assertions.assertEquals(sortResponseIds, sortedByCode);
    }

    @Test
    @DisplayName("Добавление нового пользователя")
    void addNewUserTest() {
        UserRoot user = getTestUser();
        Integer userId = given().body(user)
                .post("/users")
                .then()
                .statusCode(200)
                .extract().jsonPath().getInt("id");

        Assertions.assertNotNull(userId);
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUserTest() {
        UserRoot user = getTestUser();
        String oldPassword = user.getPassword();
        user.setPassword("newPasssword14569");

        UserRoot updateUser = given()
                .body(user)
                .pathParam("userId", user.getId())
                .put("/users/{userId}")
                .then().extract().as(UserRoot.class);

        Assertions.assertNotEquals(updateUser.getPassword(), oldPassword);
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUserTest(){
        int userId = 7;

        given()
                .pathParam("userId", userId)
                .delete("/users/{userId}")
                .then()
                .statusCode(200);

        List<UserRoot> allUsers = getAllUsers();
        boolean isPresent = allUsers.stream()
                .noneMatch(x -> x.getId() == userId);

        Assertions.assertTrue(isPresent);
    }

    @Test
    @DisplayName("Авторизация пользователя")
    void userLoginTest(){
        AuthData authData = new AuthData("johnd","m38rmF$");

        String token = given().contentType(ContentType.JSON)
                .body(authData)
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }


    private UserRoot getTestUser() {
        Random random = new Random();
        Name name = new Name("Thomas", "Anderson");
        Geolocation geolocation = new Geolocation("-31.563", "83.4775");

        Address address = Address.builder()
                .city("Moscow")
                .number(random.nextInt(100))
                .zipcode("45871-5231")
                .street("New Arbat, 12")
                .geolocation(geolocation).build();

        return UserRoot.builder()
                .name(name)
                .phone("78541259647")
                .email("thomas@gmail.com")
                .password("48Mimd{")
                .address(address).build();
    }

    private List<UserRoot> getAllUsers() {
        return given()
                .get("/users")
                .then()
                .extract().as(new TypeRef<>(){});
    }
}