package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 3. CLASE PADRE SERIALIZABLE (Semana 16): Para poder guardar la partida
public abstract class Personaje implements Serializable {
    protected String nombre;
    protected int vidaActual;
    protected Estadisticas stats; // Usamos el Record aquí

    // Dentro de la clase abstracta Personaje:
    protected List<Item> inventario; // Lista para aplicar Lambdas
    protected int nivel;
    protected int experiencia;
    protected int experienciaParaSiguienteNivel;

    // Modifica el constructor:
    public Personaje(String nombre, int vida, Estadisticas stats) {
        this.nombre = nombre;
        this.vidaActual = vida;
        this.stats = stats;
        this.inventario = new ArrayList<>(); // Inicializamos lista vacía
        this.nivel = 1;
        this.experiencia = 0;
        this.experienciaParaSiguienteNivel = 100;
    }

    // Método abstracto para polimorfismo
    public abstract void atacar(Personaje objetivo);

    // Getters necesarios...
    public boolean estaVivo() { return vidaActual > 0; }

    // Agrega estos métodos nuevos:
    public void ganarExperiencia(int exp) {
        this.experiencia += exp;
        System.out.println("✨ Ganaste " + exp + " XP.");
        if (this.experiencia >= this.experienciaParaSiguienteNivel) {
            subirNivel();
        }
    }

    public void subirNivel() {
        this.nivel++;
        this.experiencia = 0;
        this.experienciaParaSiguienteNivel *= 1.5; // Cada vez es más difícil subir
        this.vidaActual = 100 + (nivel * 10); // Recupera vida y aumenta el máximo

        // Mejoramos stats (creamos un nuevo Record con valores más altos)
        this.stats = new Estadisticas(stats.fuerza() + 5, stats.defensa() + 2, stats.velocidad() + 2);
        System.out.println("🆙 ¡SUBISTE AL NIVEL " + nivel + "! Tus estadísticas han mejorado.");
    }

    public void recibirCuracion(int cantidad) {
        this.vidaActual += cantidad;
        System.out.println("💚 Te curaste " + cantidad + " HP. Vida actual: " + vidaActual);
    }

    public List<Item> getInventario() { return inventario; }

    public void agregarItem(Item item) { inventario.add(item); }

    @Override
    public String toString() {
        return nombre + " (HP: " + vidaActual + ")";
    }
}
