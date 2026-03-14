package es.udc.paproject.backend.rest.controllers;

import es.udc.paproject.backend.model.entities.Purchase;
import es.udc.paproject.backend.model.exceptions.*;
import es.udc.paproject.backend.model.services.ShoppingService;
import es.udc.paproject.backend.rest.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @Autowired
    private ShoppingService shoppingService;

    // FUNC-4: Comprar entradas
    @PostMapping("/purchases")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseDto buyTickets(
            @RequestAttribute Long userId,
            @Validated @RequestBody BuyTicketsParamsDto params)
            throws InstanceNotFoundException, SessionAlreadyStartedException, MaxTicketsExceededException {

        Purchase purchase = shoppingService.buyTickets(
                userId,
                params.getSessionId(),
                params.getTickets(),
                params.getCreditCard()
        );

        return ShoppingConversor.toPurchaseDto(purchase);
    }

    // FUNC-5: Histórico de compras
    @GetMapping("/purchases")
    public List<PurchaseDto> getPurchaseHistory(@RequestAttribute Long userId) throws InstanceNotFoundException {
        List<Purchase> purchases = shoppingService.getPurchaseHistory(userId);
        return ShoppingConversor.toPurchaseDtos(purchases);
    }

    // FUNC-6: Entregar entradas
    @PostMapping("/purchases/{purchaseId}/deliver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deliverTickets(
            @PathVariable Long purchaseId,
            @Validated @RequestBody DeliverTicketsParamsDto params)
            throws InstanceNotFoundException, IncorrectCreditCardException,
            SessionAlreadyStartedException, TicketsAlreadyDeliveredException {

        shoppingService.deliverTickets(purchaseId, params.getCreditCard());
    }
}