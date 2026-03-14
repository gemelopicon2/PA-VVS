package es.udc.paproject.backend.rest.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DeliverTicketsParamsDto {

    @NotNull
    @Size(min = 16, max = 16, message = "La tarjeta de crédito debe tener exactamente 16 caracteres")
    private String creditCard;

    public DeliverTicketsParamsDto() {}

    public String getCreditCard() { return creditCard; }
    public void setCreditCard(String creditCard) { this.creditCard = creditCard; }
}