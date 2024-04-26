package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import listener.AdminUser;
import listener.AdminUserResolver;
import models.swagger.FullUser;
import models.swagger.Info;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import services.UserService;

import java.util.List;

import static assertions.Conditions.hasMessage;
import static assertions.Conditions.hasStatusCode;
import static utils.RandomTestData.*;

@ExtendWith(AdminUserResolver.class)
@DisplayName("SwaggerApiTests")
public class SwaggerNewUserTests {
    private static UserService userService;
    private FullUser user;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://85.192.34.140:8080/api";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured());

        userService = new UserService();
    }

    @BeforeEach
    public void initTestUser() {
        user = getRandomUser();
    }

    @Test
    public void registerUserWithGames() {
        FullUser user = getRandomUserWithGames();
        Response response = userService.registerUser(user)
                .asResponse();
        Info info = response.jsonPath().getObject("info", Info.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response.statusCode()).as("Статус код не 201")
                .isEqualTo(201);
        softAssertions.assertThat(info.getMessage()).as("Сообщение неверное")
                .isEqualTo("User created");
        softAssertions.assertAll();

        Assertions.assertAll(
                () -> Assertions.assertEquals("User created", info.getMessage()),
                () -> Assertions.assertEquals(201, response.statusCode())
        );
    }

    @Test
    public void registerUserTest() {
        userService.registerUser(user)
                .should(hasStatusCode(201))
                .should(hasMessage("User created"));
    }

    @Test
    public void registerLoginExistsTest() {
        userService.registerUser(user);
        userService.registerUser(user)
                .should(hasStatusCode(400))
                .should(hasMessage("Login already exist"));
    }

    @Test
    public void registerNoPassTest() {
        user.setPass(null);
        userService.registerUser(user)
                .should(hasStatusCode(400))
                .should(hasMessage("Missing login or password"));
    }

    @Test
    public void adminAuthTest(@AdminUser FullUser admin) {
        String token = userService.auth(admin)
                .should(hasStatusCode(200))
                .asJwt();

        Assertions.assertNotNull(token);
    }

    @Test
    public void userAuthTest() {
        userService.registerUser(user);
        String token = userService.auth(user)
                .should(hasStatusCode(200))
                .asJwt();

        Assertions.assertNotNull(token);
    }

    @Test
    public void negativeAuthTest() {
        userService.auth(user)
                .should(hasStatusCode(401));
    }

    @Test
    public void positiveGetAdminUserInfoTest(@AdminUser FullUser admin) {
        String token = userService.auth(admin).asJwt();
        userService.getUserInfo(token)
                .should(hasStatusCode(200));
    }

    @Test
    public void negativeGetUserInfoInvalidJwtTest() {
        userService.getUserInfo("abracadabra")
                .should(hasStatusCode(401));
    }

    @Test
    public void negativeGetUserInfoWithoutJwtTest() {
        userService.getUserInfo()
                .should(hasStatusCode(401));
    }

    @Test
    public void positiveChangeUserPassTest() {
        String oldPass = user.getPass();
        userService.registerUser(user);
        String token = userService.auth(user).asJwt();
        String updatedPass = getRandomPassword();

        userService.updateUserPass(updatedPass, token)
                .should(hasStatusCode(200))
                .should(hasMessage("User password successfully changed"));

        user.setPass(updatedPass);
        token = userService.auth(user)
                .should(hasStatusCode(200))
                .asJwt();
        FullUser updatedUser = userService.getUserInfo(token).as(FullUser.class);

        Assertions.assertNotEquals(oldPass, updatedUser.getPass());
    }

    @Test
    public void negativeChangeAdminPassTest(@AdminUser FullUser admin) {
        String token = userService.auth(admin).asJwt();
        String updatedPass = "newUserPassword777";

        userService.updateUserPass(updatedPass, token)
                .should(hasStatusCode(400))
                .should(hasMessage("Cant update base users"));
    }

    @Test
    public void positiveDeleteUserTest() {
        userService.registerUser(user);
        String token = userService.auth(user).asJwt();
        userService.deleteUser(token)
                .should(hasStatusCode(200))
                .should(hasMessage("User successfully deleted"));
    }

    @Test
    public void negativeDeleteAdminTest(@AdminUser FullUser admin) {
        String token = userService.auth(admin).asJwt();

        userService.deleteUser(token)
                .should(hasStatusCode(400))
                .should(hasMessage("Cant delete base users"));
    }

    @Test
    public void positiveGetAllUsersLoginTest() {
        List<String> users = userService.getAllUsers()
                .should(hasStatusCode(200))
                .asList(String.class);

        Assertions.assertTrue(users.size() >= 3);
    }
}