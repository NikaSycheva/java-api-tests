package services;

import assertions.AssertableResponse;
import io.restassured.http.ContentType;
import models.swagger.FullUser;
import models.swagger.Info;
import models.swagger.JwtAuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class UserService {
    //Регистрация пользователя
    public AssertableResponse registerUser(FullUser user){
        return new AssertableResponse(given().contentType(ContentType.JSON)
                .body(user)
                .post("/signup")
                .then());
    }

    //Авторизация пользователя
    public AssertableResponse auth(FullUser fullUser){
        JwtAuthData authData = new JwtAuthData(fullUser.getLogin(), fullUser.getPass());
        return new AssertableResponse(given().contentType(ContentType.JSON)
                .body(authData)
                .post("/login")
                .then());
    }

    //Получить информацию о пользователе по jwt-токену
    public AssertableResponse getUserInfo(String token){
        return new AssertableResponse(given().auth().oauth2(token)
                .get("/user")
                .then());
    }

    //для негативной проверки авторизации без токена
    public AssertableResponse getUserInfo(){
        return new AssertableResponse(given()
                .get("/user")
                .then());
    }

    //Обновление пароля пользоваетеля
    public AssertableResponse updateUserPass(String newPass, String token){
        Map<String,  String> newPassModel = new HashMap<>();
        newPassModel.put("password", newPass);

        return new AssertableResponse(given().contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(newPassModel)
                .put("/user")
                .then());
    }

    //Удаление пользователя
    public AssertableResponse deleteUser(String token){
        return new AssertableResponse(given().auth().oauth2(token)
                .delete("/user")
                .then());
    }

    //Получить логины всех пользователей
    public AssertableResponse getAllUsers(){
        return new AssertableResponse(given()
                .get("/users")
                .then());
    }

}
