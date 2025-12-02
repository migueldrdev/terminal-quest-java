package modelo;

public class Guerrero extends Personaje {
    private Arma armaEquipada;

    public Guerrero(String nombre, Estadisticas stats) {
        super(nombre, 100, stats);
        // Arma por defecto: Puños (Daño 0 o 1) o una Espada oxidada
        this.armaEquipada = new Arma("Espada Oxidada", 5);
    }

    public void equiparArma(Arma nuevaArma) {
        this.armaEquipada = nuevaArma;
        System.out.println("⚔️ Te has equipado: " + nuevaArma.getNombre());
    }

    public Arma getArmaEquipada() { return armaEquipada; }

    @Override
    public void atacar(Personaje objetivo) {
        // Daño = Fuerza del personaje + Daño del Arma - Defensa del enemigo
        int poderTotal = this.stats.fuerza() + this.armaEquipada.getDanioExtra();
        int defensaEnemigo = objetivo.stats.defensa();

        int danioReal = Math.max(0, poderTotal - defensaEnemigo);

        objetivo.vidaActual -= danioReal;

        System.out.println("⚔️ Atacas con " + armaEquipada.getNombre() + "!");
        System.out.println("   >> Daño: " + danioReal + " (Fuerza " + stats.fuerza() + " + Arma " + armaEquipada.getDanioExtra() + ")");
        System.out.println("   >> Vida restante de " + objetivo.nombre + ": " + objetivo.vidaActual);
    }
}