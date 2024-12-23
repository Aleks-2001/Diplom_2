package api;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import model.User;

import static io.restassured.RestAssured.given;

public class RequestAPI {

    @Step("Send {method} request to {api} with user: {user}")
    public ValidatableResponse sendRequest(User user, String api, String method) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .body(user)
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request(method, api)
                .then();
    }


    @Step("Send authorization request}")
    public ValidatableResponse sendAuthorizationRequest(User user) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .body(user)
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request("POST", "auth/login")
                .then();
    }


    @Step("Send User Delete request")
    public ValidatableResponse sendUserDeleteRequest(String accessTokenValue) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessTokenValue)
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request("delete", "auth/user")
                .then();
    }


    @Step("Send logOut request}")
    public ValidatableResponse sendLogOutRequest(String refreshToken) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .body("{\"token\": " + "\"" + refreshToken + "\"" + "}") // Подставляем JSON с refreshToken
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request("POST", "auth/logout")
                .then();
    }


    @Step("Send change data of User request")
    public ValidatableResponse sendUserDataChangeRequest(String accessTokenValue, User user) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessTokenValue)
                .when()
                .body(user)
                .filter(new AllureRestAssured())
                .log().all()
                .when()
                .request("patch", "auth/user")
                .then();
    }


}
