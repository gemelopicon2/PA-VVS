package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Purchase;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class ShoppingConversor {

    private ShoppingConversor() {}

    public static final PurchaseDto toPurchaseDto(Purchase purchase) {
        return new PurchaseDto(
                purchase.getId(),
                purchase.getSession().getId(),
                purchase.getSession().getMovie().getTitle(),
                purchase.getDate(),
                purchase.getSession().getDate(),
                purchase.getTickets(),
                purchase.getSession().getPrice().multiply(new BigDecimal(purchase.getTickets())),
                purchase.isDelivered()
        );
    }

    public static final List<PurchaseDto> toPurchaseDtos(List<Purchase> purchases) {
        return purchases.stream().map(p -> toPurchaseDto(p)).collect(Collectors.toList());
    }
}