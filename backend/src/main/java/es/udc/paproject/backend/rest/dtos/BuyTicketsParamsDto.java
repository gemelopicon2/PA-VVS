package es.udc.paproject.backend.rest.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BuyTicketsParamsDto {

    @NotNull
    private Long sessionId;

    @NotNull
    @Min(value = 1)
    @Max(value = 10)
    private Integer tickets;

    @NotNull
    @Size(min = 16, max = 16)
    private String creditCard;

    public BuyTicketsParamsDto() {}

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public Integer getTickets() { return tickets; }
    public void setTickets(Integer tickets) { this.tickets = tickets; }

    public String getCreditCard() { return creditCard; }
    public void setCreditCard(String creditCard) { this.creditCard = creditCard; }
}