package com.temp.junit.temptestcase;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SecondTestTest {

	
	@BeforeAll
    static void beforeAll() {
        System.out.println("Before all Second test methods");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before each Second test method");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After each Second test method");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all Second test methods");
    }

    @Test
    @DisplayName("Second test")
    void SecondTest() {
        System.out.println("Second test Second method");
    }

    @Test
    @DisplayName("Second test")
    void secondTest() {
        System.out.println("Second test Second method");
    }

}
