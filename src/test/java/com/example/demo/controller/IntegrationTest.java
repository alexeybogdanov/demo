package com.example.demo.controller;

import com.example.demo.DemoApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {DemoApplication.class})
@WebAppConfiguration
@SpringBootTest
class IntegrationTest {

    private MockMvc mockMvc;
    private String productsJson;
    private String inventoryJson;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        String products = "src/test/resources/products.json";
        String inventory = "src/test/resources/inventory.json";
        productsJson = readFileAsString(products);
        inventoryJson = readFileAsString(inventory);
    }

    @Test
    void addProductsAndInventory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/inventory")
                .content(inventoryJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/products")
                .content(productsJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/products/list")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].name", is("Dining Chair")))
                .andExpect(jsonPath("$[0].quantity", is(2)))
                .andExpect(jsonPath("$[0].contain_articles.length()", is(3)))
                .andExpect(jsonPath("$[1].name", is("Dinning Table")))
                .andExpect(jsonPath("$[1].quantity", is(1)))
                .andExpect(jsonPath("$[0].contain_articles.length()", is(3)));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/products/sell/1")
                .content(productsJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                .get("/products/list")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].name", is("Dining Chair")))
                .andExpect(jsonPath("$[0].quantity", is(1)))
                .andExpect(jsonPath("$[0].contain_articles.length()", is(3)))
                .andExpect(jsonPath("$[1].name", is("Dinning Table")))
                .andExpect(jsonPath("$[1].quantity", is(1)))
                .andExpect(jsonPath("$[0].contain_articles.length()", is(3)));
    }

    public static String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}