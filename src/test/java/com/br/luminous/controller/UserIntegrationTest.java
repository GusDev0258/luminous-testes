package com.br.luminous.controller;


import com.br.luminous.models.AuthenticationRequest;
import com.br.luminous.models.UserRequest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserIntegrationTest {

    private String userToken;

    @BeforeEach
    public void setUp() {
        RestAssured.basePath = "/api";
        var authRequest = new AuthenticationRequest();
        authRequest.setEmail("testador@test.com");
        authRequest.setPassword("123456");
        this.userToken = RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .body(authRequest)
                .when()
                .post("auth/authenticate")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    /*
     * CT025 - Atualizar um usuário não existente
     */
    @Test
    public void shouldNotUpdateANonExistingUser() {
        // Arrange
        var userId = 3;
        UserRequest userRequest = new UserRequest();
        userRequest.setName("João Silva");
        userRequest.setEmail("joao321@hotmail.com");
        userRequest.setPhone("988552233");
        userRequest.setUserName("joao_silva");
        userRequest.setPassword("ct012teste");
        userRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        String expectedMessage = "User not found.";
        String expectedReturned = "User does not exist in the system";

        //Act
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .body(userRequest)
                .when()
                .put("/user/" + userId)
                .then()
                .log().all()
                //Assert
                .statusCode(404)
                .body("message", Matchers.equalTo(expectedMessage))
                .body("returned", Matchers.equalTo(expectedReturned));
    }

    /*
     * CT026 - Atualizar um usuário existente
     */
    @Test
    public void shouldUpdateAnExistingUser() {

    }

    /*
     * CT027 - Deletar um usuário não existente
     */
    @Test
    public void shouldNotDeleteANonExistingUser() {
        // Arrange
        var userId = 3;

        String expectedMessage = "User not found.";
        String expectedReturned = "User does not exist in the system";

        //Act
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .when()
                .delete("/user/" + userId)
                .then()
                .log().all()
                //Assert
                .statusCode(404)
                .body("message", Matchers.equalTo(expectedMessage))
                .body("returned", Matchers.equalTo(expectedReturned));
    }

    /*
     * CT028 - Deletar um usuário existente
     */
    @Test
    public void shouldDeleteAnExistingUser() {

    }

}
