package com.example.expensetracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private static final String EXPENSE =
            "{\"description\":\"Lunch\",\"amount\":12.50,\"category\":\"Food\",\"date\":\"2025-01-15\"}";

    private void register(String user) throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + user + "\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated());
    }

    private String login(String user, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + user + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("token").asText();
    }

    @Test
    void registerLoginAndCreateExpense() throws Exception {
        register("alice");
        String token = login("alice", "password123");
        mvc.perform(post("/api/expenses").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(EXPENSE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("Food"));
        mvc.perform(get("/api/expenses/summary").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Food").value(12.5));
    }

    @Test
    void duplicateUsernameReturnsConflict() throws Exception {
        register("bob");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"bob\",\"password\":\"password123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void wrongPasswordReturnsUnauthorized() throws Exception {
        register("carol");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"carol\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithoutTokenReturnsUnauthorized() throws Exception {
        mvc.perform(get("/api/expenses")).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidExpenseReturnsBadRequestWithFieldErrors() throws Exception {
        register("dave");
        String token = login("dave", "password123");
        mvc.perform(post("/api/expenses").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"\",\"amount\":-5,\"category\":\"Food\",\"date\":\"2025-01-15\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void userCannotAccessAnotherUsersExpense() throws Exception {
        register("erin");
        register("frank");
        String erin = login("erin", "password123");
        String frank = login("frank", "password123");
        String created = mvc.perform(post("/api/expenses").header("Authorization", "Bearer " + erin)
                .contentType(MediaType.APPLICATION_JSON).content(EXPENSE))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(created).get("id").asLong();
        mvc.perform(get("/api/expenses/" + id).header("Authorization", "Bearer " + frank))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/expenses/" + id).header("Authorization", "Bearer " + frank))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAndDeleteOwnExpense() throws Exception {
        register("gina");
        String token = login("gina", "password123");
        String created = mvc.perform(post("/api/expenses").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(EXPENSE))
                .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(created).get("id").asLong();
        mvc.perform(put("/api/expenses/" + id).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Dinner\",\"amount\":20.00,\"category\":\"Food\",\"date\":\"2025-01-16\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Dinner"));
        mvc.perform(delete("/api/expenses/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/expenses/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
