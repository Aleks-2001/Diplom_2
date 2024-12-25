package model;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;

public class UserGenerator {

        @Step("Generate random user")
        public static User getRandomUser() {
            // Метод создания Юзера со случайным набором параметров
            Faker faker = new Faker(new java.util.Locale("en"));
            String email = faker.internet().emailAddress();
            String password = faker.internet().password(8, 16, true, true);
            String name = faker.name().fullName();
            return new User(email, password, name);
        }

    }
