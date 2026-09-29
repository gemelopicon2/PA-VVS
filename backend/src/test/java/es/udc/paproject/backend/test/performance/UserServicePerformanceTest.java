package es.udc.paproject.backend.test.performance;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.paproject.backend.model.services.UserService;
import es.udc.paproject.backend.model.entities.UserDao;

@SpringBootTest 
@ActiveProfiles("test")
@Transactional 
public class UserServicePerformanceTest {

    private static EtmMonitor monitor;

    @Autowired 
    private  UserService userService;

    @Autowired
    private UserDao userDao;

    @BeforeAll 
    public static void setUpEtm(){
        BasicEtmConfigurator.configure();
        monitor = EtmManager.getEtmMonitor();
        monitor.start();
    }

    @AfterAll 
    public static void finishEtm(){
        monitor.render(new SimpleTextRenderer());
        monitor.stop();
    }

    @Test 
    public void testLoginPerformance() throws Exception{

        //Usuario de prueba

        User user = new User("testuser","password123","Test","User","user@test.com");
        userService.signUp(user);

        for(int i = 0; i < 100; i++){
            var point = monitor.createPoint("UserService:login");
            try{
                User loggedUser = userService.login("testuser","password123");
                assertNotNull(loggedUser);
            }finally{
                point.collect();
            }

        }
    }
    
}
