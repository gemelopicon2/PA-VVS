package es.udc.paproject.backend.rest.dtos;

import es.udc.paproject.backend.model.entities.Session;
import java.time.ZoneId;

public class SessionConversor {

    private SessionConversor() {}

    public static final SessionDto toSessionDto(Session session) {
        return new SessionDto(
                session.getId(),
                session.getMovie().getId(),
                session.getMovie().getTitle(),
                session.getRoom().getId(),
                session.getRoom().getName(),
                session.getDate(),
                session.getPrice()
        );
    }
}