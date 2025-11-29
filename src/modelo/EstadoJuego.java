package modelo;
import java.io.Serializable;
import java.util.List;

public class EstadoJuego implements Serializable {
    private Personaje jugador;
    private int nivelActual;
    private List<Item> inventarioGlobal; // Tu lista con lambdas

    public EstadoJuego(Personaje jugador) {
        this.jugador = jugador;
        this.nivelActual = 1;
    }

    // Getters y Setters...
    public Personaje getJugador() { return jugador; }
}