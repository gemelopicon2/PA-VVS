package es.udc.paproject.backend.model.services;

import es.udc.paproject.backend.model.entities.Purchase;
import es.udc.paproject.backend.model.exceptions.*;

import java.util.List;

public class ShoppingServiceImpl implements  ShoppingService {
    @Override
    public Purchase buyTickets(Long userId, Long sessionId, int numberOfTickets, String creditCardNumber)
            throws InstanceNotFoundException, SessionAlreadyStartedException, MaxTicketsExceededException{
        return null;
    }
    @Override
    public List<Purchase> getPurchaseHistory(Long userId) throws InstanceNotFoundException {
        return null;
    }
    @Override
    public void deliverTickets(Long purchaseId, String creditCardNumber)
            throws InstanceNotFoundException, IncorrectCreditCardException,
            SessionAlreadyStartedException, TicketsAlreadyDeliveredException {
        return;
    }
}
