package com.br.luminous.controller;

import com.br.luminous.models.AddressRequest;
import com.br.luminous.models.AuthenticationRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReportIntegrationTest {
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
     * CT049 - TDD - Geração de relatórios de um endereço inexistente
     */
    @Test
    public void shouldNotGenerateReportWithInvalidAddress() {
        // Arrange
        var addressId = 189;

        //Act - cria device
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .when()
                .get("/report/address/" + addressId)
                .then()
                .log().all()
                .statusCode(400)
                .body("message", Matchers.equalTo("Address not found."))
                .body("returned", Matchers.equalTo("An error happened"));
    }

    /*
     * CT050 - TDD - Geração de relatórios sem dados
     */
    @Test
    public void shouldGenerateReportWithMessageOfNoData() {
        // Arrange
        var userId = 12;

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setCity("São Paulo");
        addressRequest.setCep("01200-000");
        addressRequest.setHouseNumber(203);
        addressRequest.setInputVoltage(110);
        addressRequest.setStreet("Rua Cafelândia");
        addressRequest.setNeighborhood("Sumaré");
        addressRequest.setEnergyProviderId(1L);
        addressRequest.setState("SP");
        addressRequest.setNickname("Minha casa");
        addressRequest.setMainAddress(false);

        //Act - cria endereço
        Response response = RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .body(addressRequest)
                .when()
                .post("address/user/" + userId)
                .then()
                .log().all()
                .statusCode(201)
                .extract().response();

        var addressId = response.path("data");

        //Act - cria device
        RestAssured.given()
                .log()
                .all()
                .contentType("application/json")
                .header("Authorization", " Bearer " + this.userToken)
                .when()
                .get("/report/address/" + addressId)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", Matchers.equalTo(false))
                .body("message", Matchers.equalTo("There is no data to show in the report."))
                .body("data", Matchers.equalTo(null));
    }

}
