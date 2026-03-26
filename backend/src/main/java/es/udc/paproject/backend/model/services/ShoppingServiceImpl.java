package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@Service
@Transactional
public class ShoppingServiceImpl implements  ShoppingService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private PurchaseDao purchaseDao;

    @Override
    public Purchase buyTickets(Long userId, Long sessionId, int numberOfTickets, String creditCardNumber)
            throws InstanceNotFoundException, SessionAlreadyStartedException, MaxTicketsExceededException{

        User user = userDao.findById(userId)
                .orElseThrow(() -> new InstanceNotFoundException("User", userId));

        Session session = sessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("Session", sessionId));

        LocalDateTime now = LocalDateTime.now();
        if (session.getDate().isBefore(now) || session.getDate().isEqual(now)) {
            throw new SessionAlreadyStartedException();
        }

        int capacity = session.getRoom().getCapacity();
        List<Purchase> purchases = purchaseDao.findBySessionId(sessionId);
        int ticketsSold = purchases.stream().mapToInt(Purchase::getTickets).sum();
        int availableSeats = capacity - ticketsSold;

        if (numberOfTickets > availableSeats) {
            throw new MaxTicketsExceededException(numberOfTickets, availableSeats);
        }

        Purchase purchase = new Purchase(user, session, numberOfTickets, creditCardNumber, now);
        return purchaseDao.save(purchase);    }

    @Override
    public Block<Purchase> getPurchaseHistory(Long userId, int page, int size) throws InstanceNotFoundException {
        User user = userDao.findById(userId).orElseThrow(() -> new InstanceNotFoundException("User", userId));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Slice<Purchase> slice = purchaseDao.findByUserIdOrderByDateDesc(user, pageable);
        return new Block<>(slice.getContent(), slice.hasNext());
    }

    @Override
    public void deliverTickets(Long purchaseId, String creditCardNumber)
            throws InstanceNotFoundException, IncorrectCreditCardException,
            SessionAlreadyStartedException, TicketsAlreadyDeliveredException {

        Purchase purchase = purchaseDao.findById(purchaseId)
                .orElseThrow(() -> new InstanceNotFoundException("Purchase", purchaseId));

        if (!purchase.getCreditCard().equals(creditCardNumber)) {
            throw new IncorrectCreditCardException("purchaseId");
        }

        LocalDateTime now = LocalDateTime.now();
        Session session = purchase.getSession();
        if (session.getDate().isBefore(now) || session.getDate().isEqual(now)) {
            throw new SessionAlreadyStartedException();
        }

        if (purchase.isDelivered()) {
            throw new TicketsAlreadyDeliveredException("");
        }

        purchase.setDelivered(true);
    }
}
