package modelo;
import java.io.Serializable;

public class EstadoJuego implements Serializable {
    private Personaje jugador;
    private int pisoActual;

    public EstadoJuego(Personaje jugador) {
        this.jugador = jugador;
        this.pisoActual = 1; // Empezamos en el piso 1
    }

    public Personaje getJugador() { return jugador; }

    public int getPisoActual() { return pisoActual; }

    public void avanzarPiso() { this.pisoActual++; }
}