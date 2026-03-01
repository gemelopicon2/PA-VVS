package es.udc.paproject.backend.model.exceptions;

public class TicketsAlreadyDeliveredException extends RuntimeException {
    public TicketsAlreadyDeliveredException(String message) {
        super("Los tickets de esta compra ya han sido entregados");
    }
}
