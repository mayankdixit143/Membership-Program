package com.firstclub.membership;

import com.firstclub.membership.dto.*;
import com.firstclub.membership.enums.PlanType;
import com.firstclub.membership.enums.TierType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  
class MembershipIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    
    Long userId;
    Long subscriptionId;

    @Test @Order(1)
    void getPlans_shouldReturnThreePlans() throws Exception {
        mockMvc.perform(get("/api/v1/membership/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test @Order(2)
    void getTiers_shouldReturnThreeTiers() throws Exception {
        mockMvc.perform(get("/api/v1/membership/tiers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test @Order(3)
    void createUser_shouldSucceed() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setName("Test User");
        req.setEmail("test_" + System.currentTimeMillis() + "@example.com");

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())  
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        System.out.println(">>> createUser response body: " + body);   

        com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(body);
        System.out.println(">>> data node: " + root.path("data"));     
        System.out.println(">>> id node: " + root.path("data").path("id"));  

        userId = root.path("data").path("id").asLong();
        System.out.println(">>> captured userId: " + userId);          

        Assertions.assertTrue(userId > 0, "userId must be > 0 but was: " + userId);
    }

    @Test @Order(4)
    void subscribe_shouldSucceed() throws Exception {
        Assertions.assertNotNull(userId, "userId is null");
        Assertions.assertTrue(userId > 0, "userId is 0 or negative: " + userId);

        SubscribeRequest req = new SubscribeRequest();
        req.setPlanType(PlanType.MONTHLY);
        req.setTierType(TierType.SILVER);

        MvcResult result = mockMvc.perform(post("/api/v1/users/" + userId + "/subscription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.tierType").value("SILVER"))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        subscriptionId = objectMapper.readTree(body).path("data").path("subscriptionId").asLong();
        Assertions.assertTrue(subscriptionId > 0, "subscriptionId must be > 0 but was: " + subscriptionId);
    }

    @Test @Order(5)
    void upgradeTier_toGold_shouldSucceed() throws Exception {
        Assertions.assertNotNull(userId, "userId is null — earlier test must have failed");

        ChangeTierRequest req = new ChangeTierRequest();
        req.setNewTier(TierType.GOLD);

        mockMvc.perform(patch("/api/v1/users/" + userId + "/subscription/upgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tierType").value("GOLD"));
    }

    @Test @Order(6)
    void downgradeTier_toSilver_shouldSucceed() throws Exception {
        Assertions.assertNotNull(userId, "userId is null — earlier test must have failed");

        ChangeTierRequest req = new ChangeTierRequest();
        req.setNewTier(TierType.SILVER);

        mockMvc.perform(patch("/api/v1/users/" + userId + "/subscription/downgrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tierType").value("SILVER"));
    }

    @Test @Order(7)
    void getSubscription_shouldReturnActive() throws Exception {
        Assertions.assertNotNull(userId, "userId is null — earlier test must have failed");

        mockMvc.perform(get("/api/v1/users/" + userId + "/subscription"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test @Order(8)
    void cancelSubscription_shouldSucceed() throws Exception {
        Assertions.assertNotNull(userId, "userId is null — earlier test must have failed");

        mockMvc.perform(delete("/api/v1/users/" + userId + "/subscription"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test @Order(9)
    void subscriptionHistory_shouldHaveEntries() throws Exception {
        Assertions.assertNotNull(userId, "userId is null — earlier test must have failed");
        Assertions.assertNotNull(subscriptionId, "subscriptionId is null — subscribe (Order 4) must have failed");

        mockMvc.perform(get("/api/v1/users/" + userId + "/subscription/" + subscriptionId + "/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }
}