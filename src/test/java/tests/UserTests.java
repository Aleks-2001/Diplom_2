package tests;

import api.RequestAPI;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.User;
import model.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class UserTests {

    private User user;
    private RequestAPI requestAPI;

    @Before
    @Step("before setUp")
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api/";
        // Создаем новый экземпляр юзера
        user = UserGenerator.getRandomUser();
        // Логируем созданного юзера
        System.out.printf("email = %s, password = %s, name = %s", user.getEmail(), user.getPassword(), user.getName());
        requestAPI = new RequestAPI();
    }

    @After
    @Step("after cleanUp")
    public void cleanUp() {
        // Проверка авторизации с целью получения токена
        Allure.step("Check authorization");
        String accessTokenValue = requestAPI.sendAuthorizationRequest(user)
                .extract()
                .response()
                .jsonPath()
                .getString("accessToken");
        // логируем accessToken
        System.out.println("accessToken = " + accessTokenValue);

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
    @DisplayName("Check of create new User")
    public void createNewUserTest() {
        // вызываем метод отправки запроса для регистрации нового юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка успешной регистрации
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }


    @Test
    @DisplayName("Check of create second the same User")
    public void createTheSameUserTest() {
        // вызываем метод отправки запроса для регистрации юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));

        // вызываем метод отправки запроса для регистрации еще одного такого же юзера
        ValidatableResponse secondResponse = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка
        secondResponse.log().all()
                .assertThat().statusCode(403)
                .and().body("message", equalTo("User already exists"));
    }


    @Test
    @DisplayName("Check of create new User without email")
    public void createNewUserWithoutEmailTest() {
        String emailValue = user.getEmail();
        // У экземпляра User улаляем параметр email
        user.setEmail(null);
        // вызываем метод отправки запроса для регистрации  юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(403)
                .and().body("message", equalTo("Email, password and name are required fields"));
        // восстанавливаем удаленный параметр c целью избежать возможных ошибок далее в методе @After
        user.setEmail(emailValue);
    }

    @Test
    @DisplayName("Check of create new User with empty string email")
    public void createNewUserWithEmptyStringEmailTest() {
        String emailValue = user.getEmail();
        // У экземпляра User меняем параметр email на пустую строку
        user.setEmail("");
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(403)
                .and().body("message", equalTo("Email, password and name are required fields"));
        // восстанавливаем email c целью избежать возможных ошибок далее в методе @After
        user.setEmail(emailValue);
    }



    @Test
    @DisplayName("Check of create new User without password")
    public void createNewUserWithoutPasswordTest() {
        String passValue = user.getPassword();
        // У экземпляра User улаляем параметр password
        user.setPassword(null);
        // вызываем метод отправки запроса для регистрации  юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(403)
                .and().body("message", equalTo("Email, password and name are required fields"));
        // восстанавливаем удаленный параметр c целью избежать возможных ошибок далее в методе @After
        user.setPassword(passValue);
    }

    @Test
    @DisplayName("Check of create new User with empty string password")
    public void createNewUserWithEmptyStringPasswordTest() {
        String passValue = user.getPassword();
        // У экземпляра User меняем параметр email на пустую строку
        user.setPassword("");
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(403)
                .and().body("message", equalTo("Email, password and name are required fields"));
        // восстанавливаем удаленный параметр c целью избежать возможных ошибок далее в методе @After
        user.setPassword(passValue);
    }



    @Test
    @DisplayName("Check of create new User without name")
    public void createNewUserWithoutNameTest() {
        String nameValue = user.getName();
        // У экземпляра User улаляем параметр password
        user.setName(null);
        // вызываем метод отправки запроса для регистрации  юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(403)
                .and().body("message", equalTo("Email, password and name are required fields"));
        // восстанавливаем удаленный параметр c целью избежать возможных ошибок далее в методе @After
        user.setName(nameValue);
    }

    @Test
    @DisplayName("Check of create new User with empty string name")
    public void createNewUserWithEmptyStringNameTest() {
        String nameValue = user.getName();
        // У экземпляра User меняем параметр email на пустую строку
        user.setName("");
        // вызываем метод отправки запроса
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка
        response.log().all()
                .assertThat().statusCode(403)
                .and().body("message", equalTo("Email, password and name are required fields"));
        // восстанавливаем удаленный параметр c целью избежать возможных ошибок далее в методе @After
        user.setName(nameValue);
    }



    @Test
    @DisplayName("Check of authorization User")
    public void authorizationUserTest() {
        // вызываем метод отправки запроса для регистрации нового юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка успешной регистрации
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
        // Извлекаем из ответа сервера refreshToken
        String refreshTokenValue = response
                .extract()
                .response()
                .jsonPath()
                .getString("refreshToken");
        System.out.println("Извлечённый РефрешТокен:  " + refreshTokenValue);

        // выходим из системы после регистрации (logOut)
        ValidatableResponse responseLogOut = requestAPI.sendLogOutRequest(refreshTokenValue);
        // проверка успешного выхода
        responseLogOut.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));

        // снова авторизуемся после выхода
        ValidatableResponse responseAuth = requestAPI.sendAuthorizationRequest(user);
        // проверка успешной авторизации
        responseAuth.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
    }


    @Test
    @DisplayName("Check of authorization User with incorrect parameters")
    public void authorizationUserWithIncorrectDataTest() {
        // вызываем метод отправки запроса для регистрации нового юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка успешной регистрации
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
        // Извлекаем из ответа сервера refreshToken
        String refreshTokenValue = response
                .extract()
                .response()
                .jsonPath()
                .getString("refreshToken");

        // выходим из системы после регистрации (logOut)
        ValidatableResponse responseLogOut = requestAPI.sendLogOutRequest(refreshTokenValue);
        // проверка успешного выхода
        responseLogOut.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));

        // Меняем имя и пароль и пробуем авторизоваться
        String emailValue = user.getEmail();
        String passValue = user.getPassword();
        user.setEmail("Another" + user.getEmail());
        user.setPassword("Another" + user.getPassword());
        ValidatableResponse responseAuth = requestAPI.sendAuthorizationRequest(user);
        // проверка невозможности авторизации
        responseAuth.log().all()
                .assertThat().statusCode(401)
                .and().body("success", equalTo(false));
        // восстанавливаем измененные параметры c целью избежать возможных ошибок далее в методе @After
        user.setEmail(emailValue);
        user.setPassword(passValue);
    }


    @Test
    @DisplayName("Check of change data of User")
    public void dataOfUserChangeTest() {
        // вызываем метод отправки запроса для регистрации нового юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка успешной регистрации
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
        // Извлекаем из ответа сервера accessToken
        String accessTokenValue = response
                .extract()
                .response()
                .jsonPath()
                .getString("accessToken");

        // Меняем параметры юзера. Согласно заданию нужно "проверить, что любое поле можно изменить". Меняем сразу три.
        // Хотя, ничто не мешает сделать три отдельных теста на каждое поле в отдельности. Полагаю, что это лишнее.
        user.setEmail("new" + user.getEmail());
        user.setPassword("new" + user.getPassword());
        user.setName("new" + user.getName());

        // Отправка запроса с новыми параметрами на изменение данных пользователя.
        ValidatableResponse changeResponse = requestAPI.sendUserDataChangeRequest(accessTokenValue, user);
        // проверка внесенных изменений
        changeResponse.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
        System.out.println("Данные пользователя успешно изменены");
    }


    @Test
    @DisplayName("Check of change data of User for unauthorized user")
    public void dataOfUserChangeForUnauthorizedUserTest() {
        // вызываем метод отправки запроса для регистрации нового юзера
        ValidatableResponse response = requestAPI.sendRequest(user, "auth/register", "post");
        // проверка успешной регистрации
        response.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));
        // Извлекаем из ответа сервера refreshToken
        String refreshTokenValue = response
                .extract()
                .response()
                .jsonPath()
                .getString("refreshToken");

        //  После регистрации отправляем запрос на выход из системы (logOut)
        ValidatableResponse responseLogOut = requestAPI.sendLogOutRequest(refreshTokenValue);
        // проверка успешного выхода
        responseLogOut.log().all()
                .assertThat().statusCode(200)
                .and().body("success", equalTo(true));

        // Меняем у юзера имейл, имя, и пароль
        String emailValue = user.getEmail();
        String passValue = user.getPassword();
        String nameValue = user.getName();
        user.setEmail("Another" + user.getEmail());
        user.setPassword("Another" + user.getPassword());
        user.setName("Another" + user.getName());

        // Отправка запроса на изменение данных пользователя. В данном случае запрос отправляем без поля Authorization,
        // и без соответствующего токена. Считаем, что неавторизованный пользователь таким токеном не обладает.
        ValidatableResponse changeResponse = requestAPI.sendRequest(user, "auth/user", "patch");
        // проверка невозможности внесения изменений
        changeResponse.log().all()
                .assertThat().statusCode(401)
                .and().body("message", equalTo("You should be authorised"));
        // восстанавливаем измененные параметры c целью избежать возможных ошибок далее в методе @After
        user.setEmail(emailValue);
        user.setPassword(passValue);
        user.setName(nameValue);
    }

}
