package es.udc.paproject.backend.model.services;
import es.udc.paproject.backend.model.entities.Purchase;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import es.udc.paproject.backend.model.exceptions.MaxTicketsExceededException;
import es.udc.paproject.backend.model.exceptions.IncorrectCreditCardException;
import es.udc.paproject.backend.model.exceptions.TicketsAlreadyDeliveredException;

import java.util.List;

public interface ShoppingService {
    Purchase buyTickets(Long userId, Long sessionId, int numberOfTickets, String creditCardNumber)
            throws InstanceNotFoundException, SessionAlreadyStartedException, MaxTicketsExceededException;
    List<Purchase> getPurchaseHistory(Long userId) throws InstanceNotFoundException;
    void deliverTickets(Long purchaseId, String creditCardNumber)
            throws InstanceNotFoundException, IncorrectCreditCardException,
            SessionAlreadyStartedException, TicketsAlreadyDeliveredException;
}
