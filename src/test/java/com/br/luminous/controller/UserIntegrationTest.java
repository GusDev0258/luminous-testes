package com.br.luminous.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserIntegrationTest {

    @BeforeEach
    public void setUp() {

    }

    /*
     * CT025 - Atualizar um usuário não existente
     */
    @Test
    public void shouldNotUpdateANonExistingUser() {

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

    }

    /*
     * CT028 - Deletar um usuário existente
     */
    @Test
    public void shouldDeleteAnExistingUser() {

    }

}
