package es.udc.paproject.backend.model.exceptions;

public class IncorrectCreditCardException extends RuntimeException {
    public IncorrectCreditCardException(String message) {
        super("El número de tarjeta de crédito es incorrecto: ");
    }
}
