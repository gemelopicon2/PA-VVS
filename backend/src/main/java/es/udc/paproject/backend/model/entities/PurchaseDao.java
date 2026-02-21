package es.udc.paproject.backend.model.entities;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseDao extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUser(User user);

    List<Purchase> findByUserId(Long userId);

    List<Purchase> findBySession(Session session);

    List<Purchase> findBySessionId(Long sessionId);

    List<Purchase> findByCreditCard(String creditCard)

    List<Purchase> findByDateAfter(LocalDateTime date);

    List<Purchase> findByDateBefore(LocalDateTime date);

    List<Purchase> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Purchase> findByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    List<Purchase> findByDeliveredFalse();

    List<Purchase> findByDeliveredTrue();

    List<Purchase> findByUserAndDeliveredFalse(User user);

    List<Purchase> findBySessionAndTicketsGreaterThan(Session session, int minTickets);

    List<Purchase> findByUserOrderByDateDesc(User user);

}