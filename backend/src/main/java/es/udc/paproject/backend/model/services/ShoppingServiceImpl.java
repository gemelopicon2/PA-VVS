package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@Service
@Transactional
public class ShoppingServiceImpl implements ShoppingService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private PurchaseDao purchaseDao;

    @Override
    public Purchase buyTickets(Long userId, Long sessionId, int numberOfTickets, String creditCardNumber)
            throws InstanceNotFoundException, SessionAlreadyStartedException, MaxTicketsExceededException {

        User user = userDao.findById(userId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.user", userId));

        Session session = sessionDao.findById(sessionId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.session", sessionId));

        LocalDateTime now = LocalDateTime.now();
        //Si la película ya empezó, no dejamos comprar
        if (!session.getDate().isAfter(now)) {
            throw new SessionAlreadyStartedException();
        }

        int availableSeats = session.getAvailableTickets();

        if (numberOfTickets > availableSeats) {
            throw new MaxTicketsExceededException(numberOfTickets, availableSeats);
        }

        //Restamos las entradas compradas a las disponibles
        session.setAvailableTickets(availableSeats - numberOfTickets);

        Purchase purchase = new Purchase(user, session, numberOfTickets, creditCardNumber, now);
        return purchaseDao.save(purchase);
    }

    @Override
    @Transactional(readOnly = true)
    public Block<Purchase> getPurchaseHistory(Long userId, int page, int size) throws InstanceNotFoundException {

        userDao.findById(userId).orElseThrow(() -> new InstanceNotFoundException("project.entities.user", userId));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        Slice<Purchase> slice = purchaseDao.findByUserIdOrderByDateDesc(userId, pageable);

        return new Block<>(slice.getContent(), slice.hasNext());
    }

    @Override
    public void deliverTickets(Long purchaseId, String creditCardNumber)
            throws InstanceNotFoundException, IncorrectCreditCardException,
            SessionAlreadyStartedException, TicketsAlreadyDeliveredException {

        Purchase purchase = purchaseDao.findById(purchaseId)
                .orElseThrow(() -> new InstanceNotFoundException("project.entities.purchase", purchaseId));

        if (!purchase.getCreditCard().equals(creditCardNumber)) {
            throw new IncorrectCreditCardException("creditCard");
        }

        LocalDateTime now = LocalDateTime.now();
        Session session = purchase.getSession();

        //Si la sesión ya ha comenzado, la práctica suele pedir que no se puedan entregar las entradas
        if (!session.getDate().isAfter(now)) {
            throw new SessionAlreadyStartedException();
        }

        if (purchase.isDelivered()) {
            throw new TicketsAlreadyDeliveredException("");
        }

        purchase.setDelivered(true);
    }
}