package modelo;

public class Monstruo extends Personaje {

    // Constructor simple
    public Monstruo(String nombre, int vida, Estadisticas stats) {
        super(nombre, vida, stats);
    }

    @Override
    public void atacar(Personaje objetivo) {
        // El monstruo ataca un poco diferente (quizás ignora un poco de defensa)
        int danio = this.stats.fuerza() - (objetivo.stats.defensa() / 2);
        int danioReal = Math.max(0, danio);

        objetivo.vidaActual -= danioReal;

        System.out.println("🔥 " + this.nombre + " te muerde/araña!");
        System.out.println("   >> Te hizo " + danioReal + " de daño.");
    }
}