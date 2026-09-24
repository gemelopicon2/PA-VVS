package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.services.UserService;
import es.udc.paproject.backend.rest.common.JwtGenerator;
import es.udc.paproject.backend.rest.common.JwtInfo;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtGenerator jwtGenerator;

    @Test
    public void testSignUpReturnsCreatedStatus() throws Exception {
        String requestBody = "{\"userName\": \"user\", \"password\": \"pass\", \"firstName\": \"N\", \"lastName\": \"L\", \"email\": \"e@udc.es\"}";
        
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            u.setRole(User.RoleType.USER);
            return null;
        }).when(userService).signUp(any(User.class));
        
        when(jwtGenerator.generate(any(JwtInfo.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/users/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void testLoginReturnsOkStatus() throws Exception {
        String requestBody = "{\"userName\": \"user\", \"password\": \"pass\"}";
        User mockUser = new User("user", "pass", "N", "L", "e@udc.es");
        mockUser.setId(1L);
        mockUser.setRole(User.RoleType.USER);
        
        when(userService.login(anyString(), anyString())).thenReturn(mockUser);
        when(jwtGenerator.generate(any(JwtInfo.class))).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateProfileAsOwnerReturnsOkStatus() throws Exception {
        String requestBody = "{\"firstName\": \"NewN\", \"lastName\": \"NewL\", \"email\": \"new@udc.es\"}";
        User mockUser = new User("user", "pass", "NewN", "NewL", "new@udc.es");
        mockUser.setId(1L);
        mockUser.setRole(User.RoleType.USER); // Corrección del NullPointerException
        
        when(userService.updateProfile(anyLong(), anyString(), anyString(), anyString())).thenReturn(mockUser);

        mockMvc.perform(put("/users/1")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateProfileAsDifferentUserReturnsForbidden() throws Exception {
        String requestBody = "{\"firstName\": \"NewN\", \"lastName\": \"NewL\", \"email\": \"new@udc.es\"}";

        mockMvc.perform(put("/users/1")
                .requestAttr("userId", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden());
    }
}