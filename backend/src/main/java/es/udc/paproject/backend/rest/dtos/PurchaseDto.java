package es.udc.paproject.backend.rest.dtos;

public class PurchaseDto {

    private Long id;
    private Long sessionId;
    private String movieTitle;
    private long date;
    private int tickets;
    private boolean delivered;

    public PurchaseDto() {}

    public PurchaseDto(Long id, Long sessionId, String movieTitle, long date, int tickets, boolean delivered) {
        this.id = id;
        this.sessionId = sessionId;
        this.movieTitle = movieTitle;
        this.date = date;
        this.tickets = tickets;
        this.delivered = delivered;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public int getTickets() { return tickets; }
    public void setTickets(int tickets) { this.tickets = tickets; }

    public boolean getDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }
}