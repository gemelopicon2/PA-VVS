package es.udc.paproject.backend.model.exceptions;

public class MaxTicketsExceededException extends RuntimeException {
    private final int requested;
    private final int available;

    public MaxTicketsExceededException(int requested, int available) {
        super("No se pueden comprar " + requested + " tickets; Solo se pueden comprar hasta " + available + " tickets.");
        this.requested = requested;
        this.available = available;
    }
    public int getRequested() {
        return requested;
    }
    public int getAvailable() {
        return available;
    }
}
