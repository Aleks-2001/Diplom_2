package tests;

import api.RequestAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.OrderGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class UnauthorizedUserOrderTests {

    private Order order;
    private RequestAPI requestAPI;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
        requestAPI = new RequestAPI();
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
        // с намеренно невалидным хеш у одного из ингредиентов.
        order = OrderGenerator.getIncorrectHashOrder();
        // вызываем метод отправки запроса для создания заказа
        ValidatableResponse response = requestAPI.createOrderRequest(order);
        // проверка создания заказа
        response.log().all()
                .assertThat().statusCode(500);
    }


    @Test
    @DisplayName("Get orders of specific without authorization user")
    public void GetOrdersOfSpecificUserWithoutAuthorizationTest() throws Exception {
        // вызываем метод отправки запроса на получение списка заказов без accessToken (неавторизованный пользователь)
        ValidatableResponse response = requestAPI.GetOrdersOfSpecificUserRequest("");
        // проверка создания заказа
        response.log().all()
                .assertThat().statusCode(401)
                .and().body("message", equalTo("You should be authorised"));
    }


}
