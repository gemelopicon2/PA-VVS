package es.udc.paproject.backend.rest.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseDto {

    private Long id;
    private Long sessionId;
    private String movieTitle;
    private LocalDateTime date;
    private LocalDateTime sessionDate;
    private int tickets;
    private BigDecimal totalPrice;
    private boolean delivered;

    public PurchaseDto() {}

    public PurchaseDto(Long id, Long sessionId, String movieTitle, LocalDateTime date, LocalDateTime sessionDate, int tickets, BigDecimal totalPrice, boolean delivered) {
        this.id = id;
        this.sessionId = sessionId;
        this.movieTitle = movieTitle;
        this.date = date;
        this.sessionDate = sessionDate;
        this.tickets = tickets;
        this.totalPrice = totalPrice;
        this.delivered = delivered;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public LocalDateTime getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDateTime sessionDate) { this.sessionDate = sessionDate; }

    public int getTickets() { return tickets; }
    public void setTickets(int tickets) { this.tickets = tickets; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public boolean getDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }
}