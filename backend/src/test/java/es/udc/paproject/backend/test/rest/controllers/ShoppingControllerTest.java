package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.paproject.backend.model.entities.Movie;
import es.udc.paproject.backend.model.entities.Purchase;
import es.udc.paproject.backend.model.entities.Room;
import es.udc.paproject.backend.model.entities.Session;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.services.Block;
import es.udc.paproject.backend.model.services.ShoppingService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShoppingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShoppingService shoppingService;

    // --- FUNC-4: Comprar entradas ---

    @Test
    @WithMockUser(roles = "USER")
    public void testBuyTicketsAsUserReturnsCreatedStatus() throws Exception {
        String requestBody = "{\"sessionId\": 1, \"tickets\": 2, \"creditCard\": \"1234567890123456\"}";
        
        User user = new User("user", "pass", "N", "L", "e@udc.es");
        Session session = new Session(new Movie("T", "S", 120), new Room("R", 50), LocalDateTime.now().plusDays(1), new BigDecimal("5"));
        Purchase mockPurchase = new Purchase(user, session, 2, "1234567890123456", LocalDateTime.now());
        mockPurchase.setId(1L);

        when(shoppingService.buyTickets(anyLong(), anyLong(), anyInt(), anyString())).thenReturn(mockPurchase);

        mockMvc.perform(post("/shopping/purchases")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void testBuyTicketsAsUnauthenticatedReturnsUnauthorized() throws Exception {
        String requestBody = "{\"sessionId\": 1, \"tickets\": 2, \"creditCard\": \"1234567890123456\"}";

        mockMvc.perform(post("/shopping/purchases")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }

    // --- FUNC-5: Histórico de compras ---

    @Test
    @WithMockUser(roles = "USER")
    public void testGetPurchaseHistoryAsUserReturnsOkStatus() throws Exception {
        Block<Purchase> emptyBlock = new Block<>(Collections.emptyList(), false);
        when(shoppingService.getPurchaseHistory(anyLong(), anyInt(), anyInt())).thenReturn(emptyBlock);

        mockMvc.perform(get("/shopping/purchases")
                .requestAttr("userId", 1L)
                .param("page", "0"))
                .andExpect(status().isOk());
    }

    // --- FUNC-6: Entregar entradas ---

    @Test
    @WithMockUser(roles = "SELLER")
    public void testDeliverTicketsAsSellerReturnsNoContent() throws Exception {
        String requestBody = "{\"creditCard\": \"1234567890123456\"}";
        doNothing().when(shoppingService).deliverTickets(anyLong(), anyString());

        mockMvc.perform(post("/shopping/purchases/1/deliver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeliverTicketsAsUserReturnsForbidden() throws Exception {
        String requestBody = "{\"creditCard\": \"1234567890123456\"}";

        mockMvc.perform(post("/shopping/purchases/1/deliver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }
}