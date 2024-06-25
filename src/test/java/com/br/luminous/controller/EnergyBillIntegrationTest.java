package com.br.luminous.controller;

import com.br.luminous.models.AuthenticationRequest;
import com.br.luminous.models.EnergyBillRequest;
import com.br.luminous.models.UserRequest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

//@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EnergyBillIntegrationTest {
    private String userToken;

    @BeforeEach()
    void setUp() {
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

    //RT007 - Create
    @Test
    void shouldNotCreateAEnergyBillWithInvalidAddressId() {
        //Arrange
        int addressId = 0;
        int billFileId = 4;
        String expectedMessage = "Address not found.";
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        //Act
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .body(request)
                .when()
                .post("energyBill/address/" + addressId + "/billFile/" + billFileId)
                .then()
                .log().all()
                //Assert
                .statusCode(404)
                .body("success", Matchers.equalTo(false))
                .body("message", Matchers.equalTo(expectedMessage))
                .body("data", Matchers.nullValue());
        } 

    @Test
    void shouldNotCreateAEnergyBillWithInvalidBillFileId() {
        //Arrange
        int addressId = 4;
        int billFileId = 0;
        String expectedMessage = "Bill File was not found";
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        //Act
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .body(request)
                .when()
                .post("energyBill/address/" + addressId + "/billFile/" + billFileId)
                .then()
                .log().all()
                //Assert
                .statusCode(404)
                .body("success", Matchers.equalTo(false))
                .body("message", Matchers.equalTo(expectedMessage))
                .body("data", Matchers.nullValue());
    }

    @Test
    void shouldCreateAEnergyBill() {
        //Arrange
        int addressId = 4;
        int billFileId = 4;
        int responseId = 1;
        String expectedMessage = "Energy bill created";
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(100.00);
        request.setEnergyConsumption_kWh(200.00);
        //Act
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .body(request)
                .when()
                .post("energyBill/address/" + addressId + "/billFile/" + billFileId)
                .then()
                .log().all()
                //Assert
                .statusCode(201)
                .body("success", Matchers.equalTo(true))
                .body("message", Matchers.equalTo(expectedMessage))
                .body("data", Matchers.equalTo(responseId));
    }
    //RT008 - Update
    @Test
    void shouldNotUpdateANotRegisteredEnergyBill() {
        int requestId = 0;
        String expectedMessage = "EnergyBill not found";
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(120.00);
        request.setEnergyConsumption_kWh(240.00);
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .body(request)
                .when()
                .put("energyBill/" + requestId)
                .then()
                .log().all()
                //Assert
                .statusCode(404)
                .body("success", Matchers.equalTo(false))
                .body("message", Matchers.equalTo(expectedMessage))
                .body("data", Matchers.nullValue());
    }

    @Test
    void shouldUpdateAEnergyBill() {
        int requestId = 8;
        String expectedMessage = "Energy Bill updated";
        EnergyBillRequest request = new EnergyBillRequest();
        request.setReferenceDate(LocalDate.of(2024, 04, 10));
        request.setDueDate(LocalDate.of(2024, 05, 10));
        request.setEnergyConsumptionReais(120.00);
        request.setEnergyConsumption_kWh(240.00);
        var response = RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .body(request)
                .when()
                .put("energyBill/" + requestId)
                .then()
                .log().all()
                //Assert
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .body("message", Matchers.equalTo(expectedMessage))
                .body("data", Matchers.notNullValue())
                .extract().response();
        var energyConsumptionReais = response.path("data.energyConsumptionReais");
        var energyConsumption_kWh = response.path("data.energyConsumption_kWh");
        var referenceDate = response.path("data.referenceDate");
        var dueDate = response.path("data.dueDate");
        Matchers.equalTo(request.getEnergyConsumptionReais()).matches(energyConsumptionReais);
        Matchers.equalTo(request.getEnergyConsumption_kWh()).matches(energyConsumption_kWh);
        Matchers.equalTo(request.getReferenceDate()).matches(referenceDate);
        Matchers.equalTo(request.getDueDate()).matches(dueDate);
    }

    //RT009 - Delete

    @Test
    void shouldNotDeleteAInvalidEnergyBill(){
        int requestId = 0;
        String expectedMessage = "EnergyBill not found";
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .when()
                .delete("energyBill/" + requestId)
                .then()
                .log().all()
                //Assert
                .statusCode(404)
                .body("success", Matchers.equalTo(false))
                .body("message", Matchers.equalTo(expectedMessage))
                .body("data", Matchers.nullValue());
    }

    @Test
    void shouldDeleteAEnergyBill(){
        int requestId = 8;
        String expectedMessage = "EnergyBill Deleted";
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .when()
                .delete("energyBill/" + requestId)
                .then()
                .log().all()
                //Assert
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .body("message", Matchers.equalTo(expectedMessage))
                .body("data", Matchers.nullValue());
    }

    //RT 010 - Get
    @Test
    void shouldGetAEnergyBill(){
        int requestId = 8;
        String expectedMesasge = "Energy Bill found";
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .when()
                .get("energyBill/" + requestId)
                .then()
                .log().all()
                //Assert
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .body("message", Matchers.equalTo(expectedMesasge))
                .body("data", Matchers.notNullValue());
    }

    @Test
    void shouldNotGetAInvalidEnergyBill(){
        int requestId = 0;
        String expectedMessage = "EnergyBill not found";
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer "
                        + this.userToken)
                .when()
                .get("energyBill/" + requestId)
                .then()
                .log().all()
                //Assert
                .statusCode(404)
                .body("success", Matchers.equalTo(false))
                .body("message", Matchers.equalTo(expectedMessage))
                .body("data", Matchers.nullValue());
    }

}
