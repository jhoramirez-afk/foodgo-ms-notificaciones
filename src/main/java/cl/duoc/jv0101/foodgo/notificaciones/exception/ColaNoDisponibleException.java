package cl.duoc.jv0101.foodgo.notificaciones.exception;

public class ColaNoDisponibleException extends RuntimeException {
    public ColaNoDisponibleException() {
        super("La cola de notificaciones no esta disponible. Intente nuevamente.");
    }
}
