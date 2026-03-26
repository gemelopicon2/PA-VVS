package es.udc.paproject.backend.model.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "movieId")
    private Movie movie;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId")
    private Room room;

    private LocalDateTime date;
    private BigDecimal price;

    private int availableTickets;

    public Session(){}

    public Session(Movie movie, Room room, LocalDateTime date, BigDecimal price) {
        this.movie = movie;
        this.room = room;
        this.date = date;
        this.price = price;
        this.availableTickets = room.getCapacity();
    }

    public Long getId(){ return id;}
    public void setId(Long id){ this.id = id;}

    public Movie getMovie(){ return movie;}
    public void setMovie(Movie movie){ this.movie = movie;}

    public Room getRoom(){ return room;}
    public void setRoom(Room room){ this.room = room;}

    public LocalDateTime getDate(){ return date;}
    public void setDate(LocalDateTime date){ this.date = date;}

    public BigDecimal getPrice(){ return price;}
    public void setPrice(BigDecimal price){ this.price = price;}
}