package modelo;

public class Jugador extends Personaje {
    public Jugador(String nombre, int vida, Estadisticas stats) {
        super(nombre, vida, stats);
    }

    @Override
    public void atacar(Personaje objetivo) {
        int damage = this.stats.fuerza() - objetivo.stats.defensa();
        if (damage < 0) damage = 0;
        objetivo.vidaActual = Math.max(0, objetivo.vidaActual - damage);
        System.out.println(this.nombre + " ataca a " + objetivo.nombre + " causando " + damage + " daño.");
    }
}
