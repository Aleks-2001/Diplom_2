package model;
import api.RequestAPI;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.ValidatableResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.hamcrest.Matchers.equalTo;

// В генераторе заказов предусмотрено получение от сервера каждый раз -
// актуального списка _id ингредиентов, так как они, очевидно, могут изменяться -
// и далее формируется случайный набор ингредиентов для заказа.

public class OrderGenerator {

    @Step("Generate order with random ingredients")
    public static Order getRandomOrder() throws Exception {
        List<String> ingredients = generateOrderData();
        return new Order(ingredients);
    }


    @Step("Generate order without ingredients")
    public static Order getOrderWithoutIngredients() {
        List<String> ingredients = new ArrayList<>();
        return new Order(ingredients);
    }


    @Step("Generate order with ingredients with an incorrect hash")
    public static Order getIncorrectHashOrder() throws Exception {
        List<String> ingredients = generateOrderData();
        ingredients.set(0, "anotherHash");
        System.out.println(ingredients);
        return new Order(ingredients);
    }


    @Step("Get actually order data (hash of ingredients)")
    // Метод, возвращающий случайный набор из _id ингредиентов (в количестве от 1 до 8)
    private static List<String> generateOrderData() throws Exception {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
        RequestAPI requestAPI = new RequestAPI();
        requestAPI.getIngredientsRequest();
        ValidatableResponse response = requestAPI.getIngredientsRequest();
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
            // Преобразуем тело ответа в строку
            String responseBody = response.extract().response().getBody().asString();
            //   Парсим JSON, извлекаем все _id, создаём случайный набор _id
            return(getRandomIds(extractIds(responseBody)));
    }


    @Step("Extract list of _id from response")
    // Метод для извлечения списка _id
    private static List<String> extractIds(String jsonResponse) throws Exception {
        List<String> ids = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        // Парсим JSON в объект Jackson
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        // Проверяем, что success == true
        if (rootNode.get("success").asBoolean()) {
            // Извлекаем массив data
            JsonNode dataArray = rootNode.get("data");

            // Идем по каждому элементу массива и получаем _id
            for (JsonNode item : dataArray) {
                ids.add(item.get("_id").asText());
            }
        } else { throw new Exception("API response does not indicate success");
        }
        return ids;
    }


    @Step("Create random list of _id for make order")
    // Метод для формирования случайного списка _id в количестве от 1 до 8 элементов
    private static List<String> getRandomIds(List<String> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        Random random = new Random();
        int count = random.nextInt(8) + 1; // Генерируем случайное количество от 1 до 8

        List<String> randomIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // Случайно выбираем элемент из исходного списка
            String randomId = ids.get(random.nextInt(ids.size()));
            randomIds.add(randomId); // Добавляем его в результат
        }
        return randomIds;
    }
}