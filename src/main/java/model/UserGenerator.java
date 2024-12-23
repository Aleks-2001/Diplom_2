package model;

import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {

        @Step("Generate random user")
        public static User getRandomUser() {
            String email = RandomStringUtils.random(10, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&'*+-/=?^_`{|}~") + "@someMail.com";
            String password = RandomStringUtils.randomAlphabetic(8);
            String name = "Name_" + RandomStringUtils.randomAlphabetic(4);
            return new User(email, password, name);
        }

    }
