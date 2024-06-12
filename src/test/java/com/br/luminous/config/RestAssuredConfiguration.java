package com.br.luminous.config;

import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestAssuredConfiguration {

    public RestAssuredConfiguration(@Value("${app.base-uri}") String baseUri) {
        RestAssured.baseURI = baseUri;
    }
}
