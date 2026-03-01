package es.udc.paproject.backend.model.exceptions;

public class SessionAlreadyStartedException extends Exception {

    public SessionAlreadyStartedException() {
        super("Sesión ya comenzada. No se pueden comprar tickets");
    }
}