package com.br.luminous.controller;


import com.br.luminous.models.UserRequest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthenticationIntegrationTest {

    @BeforeEach
    public void setUp() {
        RestAssured.basePath = "/api";
    }

    /*
    * CT023 - Validar criação de usuário (todas as entradas válidas) pela API
    */
    @Test
    public void shouldCreateUserGivenAValidUserRequest() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setName("João Silva");
        userRequest.setEmail("joao321@hotmail.com");
        userRequest.setPhone("988552233");
        userRequest.setUserName("joao_silva321");
        userRequest.setPassword("ct010teste");
        userRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        //Act
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .body(userRequest)
                .when()
                .post("/auth/register")
                .then()
                .log().all()
                //Assert
                .statusCode(200)
                .body("id", Matchers.equalTo(1L));
    }

    /*
     * CT024 - Validar e-mail informado já existente
     */
    @Test
    public void shouldNotCreateUserGivenADuplicatedEmail() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setName("João Silva");
        userRequest.setEmail("joao321@hotmail.com");
        userRequest.setPhone("988552233");
        userRequest.setUserName("joao_silva321");
        userRequest.setPassword("ct010teste");
        userRequest.setBirthdate(LocalDate.of(2000, 1, 10));

        //Act
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .body(userRequest)
                .when()
                .post("/auth/register")
                .then()
                .log().all()
                //Assert
                .statusCode(409)
                .body("message", Matchers.equalTo("E-mail already exists."))
                .body("returned", Matchers.equalTo("Try another e-mail"));
    }
}
