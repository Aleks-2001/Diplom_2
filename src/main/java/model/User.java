package model;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    // Поля класса
    private String email;
    private String password;
    private String name;

}