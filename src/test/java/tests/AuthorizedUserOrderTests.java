package tests;

import api.RequestAPI;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.OrderGenerator;
import model.User;
import model.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class AuthorizedUserOrderTests {

    // Поля класса
    private Order order;
    private RequestAPI requestAPI;
    private User user;


    @Before
    @Step("before setUp")
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
        // Создаем новый экземпляр юзера
        user = UserGenerator.getRandomUser();
        requestAPI = new RequestAPI();
        // вызываем метод отправки запроса для регистрации нового юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка успешной регистрации
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }

    @After
    @Step("after cleanUp")
    public void cleanUp() {
        // Проверка авторизации с целью получения токена для дальнейшего удаления юзера
        Allure.step("Check authorization");
        String accessTokenValue = requestAPI.sendAuthorizationRequest(user)
                .extract()
                .response()
                .jsonPath()
                .getString("accessToken");

        // удаление юзера после теста (если токена нет - удаление не требуется).
        if (accessTokenValue != null) {
            Allure.step("Delete, if \"accessToken\" is not null");
            ValidatableResponse response = requestAPI.sendUserDeleteRequest(accessTokenValue);
            // проверка удаления юзера
            response.log().all()
                    .assertThat().statusCode(202)
                    .and().body("message", equalTo("User successfully removed"));
            System.out.println("User успешно удалён.");
        }
    }


    @Test
    @DisplayName("CreateNewOrder")
    public void createNewOrderTest() throws Exception {
        // создаем экземпляр Order со случайным набором ингредиентов
        order = OrderGenerator.getRandomOrder();
        // вызываем метод отправки запроса для создания заказа
        ValidatableResponse response = requestAPI.createOrderRequest(order);
        // проверка создания заказа
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }


    @Test
    @DisplayName("CreateEmptyOrder")
    public void createNewOrderWithoutIngredientsTest() throws Exception {
        // создаем экземпляр Order без ингредиентов
        order = OrderGenerator.getOrderWithoutIngredients();
        // вызываем метод отправки запроса для создания заказа
        ValidatableResponse response = requestAPI.createOrderRequest(order);
        // проверка создания заказа
        response.log().all()
                .assertThat().statusCode(400)
                .and().body("message", equalTo("Ingredient ids must be provided"));
    }


    @Test
    @DisplayName("CreateNewOrder")
    public void createIncorrectHashOrderTest() throws Exception {
        // создаем экземпляр Order со случайным набором ингредиентов -
        // с намеренно невалидным хешем у одного из ингредиентов.
        order = OrderGenerator.getIncorrectHashOrder();
        // вызываем метод отправки запроса для создания заказа
        ValidatableResponse response = requestAPI.createOrderRequest(order);
        // проверка создания заказа
        response.log().all()
                .assertThat().statusCode(500);
    }


    @Test
    @DisplayName("Get orders of specific user")
    public void GetOrdersOfSpecificUserTest() throws Exception {
        // Получаем accessToken для вставки в запрос
        String accessTokenValue = requestAPI.sendAuthorizationRequest(user)
                .extract()
                .response()
                .jsonPath()
                .getString("accessToken");
        // вызываем метод отправки запроса на получение списка заказов (авторизованный пользователь)
        ValidatableResponse response = requestAPI.GetOrdersOfSpecificUserRequest(accessTokenValue);
        // проверка создания заказа
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }


}
