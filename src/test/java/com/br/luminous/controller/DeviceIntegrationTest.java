package com.br.luminous.controller;

import com.br.luminous.models.AuthenticationRequest;
import com.br.luminous.models.DeviceRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DeviceIntegrationTest {
    private String userToken;

    @BeforeEach
    public void setUp() {
        // Integração TDD
        RestAssured.basePath = "/api/";
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
     * CT045 - TDD - Cadastro de Equipamento duplicado com base no nome
     */
    @Test
    public void shouldNotRegisterDeviceGivenDuplicatedDevice() {
        // Arrange
        var addressId = "4";

        DeviceRequest request1 = new DeviceRequest();
        request1.setName("Computador Desktop");
        request1.setPower(700);
        request1.setUsageTime(LocalTime.of(8,0));

        // Duplicado
        DeviceRequest request2 = new DeviceRequest();
        request2.setName("Computador Desktop");
        request2.setPower(700);
        request2.setUsageTime(LocalTime.of(8,0));


        //Act
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .body(request1)
                .when()
                .post("device/address/" + addressId)
                .then()
                .log().all()
                //Assert
                .statusCode(201);

        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .body(request2)
                .when()
                .post("device/address/" + addressId)
                .then()
                .log().all()
                //Assert
                .statusCode(400)
                .body("message", Matchers.equalTo("Device already exists with name provided."))
                .body("returned", Matchers.equalTo("An error happened"));
    }

    /*
     * CT046 - TDD - Alteração de equipamento para nome em branco
     */
    @Test
    public void shoulNotUpdateDeviceGivenABlankName() {
        // Arrange
        var addressId = "4";

        DeviceRequest requestCriacao = new DeviceRequest();
        requestCriacao.setName("Computador Desktop");
        requestCriacao.setPower(700);
        requestCriacao.setUsageTime(LocalTime.of(8,0));

        DeviceRequest requestAlteracao = new DeviceRequest();
        requestAlteracao.setName("");
        requestAlteracao.setPower(700);
        requestAlteracao.setUsageTime(LocalTime.of(8,0));

        //Act - cria device
        Response response = RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .body(requestCriacao)
                .when()
                .post("device/address/" + addressId)
                .then()
                .extract().response();

        String createdDeviceId = response.path("data").toString();

        // Act - altera device para nome em branco
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .body(requestAlteracao)
                .when()
                .put("device/" + createdDeviceId + "/address/" + addressId)
                .then()
                .log().all()
                //Assert
                .statusCode(400)
                .body("message", Matchers.equalTo("The given name is blank."))
                .body("returned", Matchers.equalTo("An error happened"));

    }

    /*
     * CT047 - TDD - Cadastro de equipamento com potência inválida
    */
    @Test
    public void shouldNotCreateDeviceWithGivenInvalidPower() {
        // Arrange
        var addressId = "4";

        DeviceRequest requestCriacao = new DeviceRequest();
        requestCriacao.setName("Ventilador de Teto");
        requestCriacao.setPower(-100);
        requestCriacao.setUsageTime(LocalTime.of(8,0));

        //Act - cria device
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .body(requestCriacao)
                .when()
                .post("device/address/" + addressId)
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("Invalid power was given: " + requestCriacao.getPower() + "w."))
                .body("returned", Matchers.equalTo("An error happened"));
    }

    /*
     * CT048 - TDD - Cadastro de equipamento com Uso em horas inválido
     */
    @Test
    public void shouldNotCreateDeviceWithGivenInvalidUsageTime() {
        // Arrange
        var addressId = "4";

        DeviceRequest requestCriacao = new DeviceRequest();
        requestCriacao.setName("Ventilador de Teto");
        requestCriacao.setPower(100);
        requestCriacao.setUsageTime(LocalTime.of(0,0));

        //Act - cria device
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .body(requestCriacao)
                .when()
                .post("device/address/" + addressId)
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("Invalid usage time was given: " + requestCriacao.getUsageTime().getHour() + "h."))
                .body("returned", Matchers.equalTo("An error happened"));
    }

}
