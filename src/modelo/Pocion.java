package modelo;

public final class Pocion implements Item {
    private String nombre;
    private int curacion;

    public Pocion(String nombre, int curacion) {
        this.nombre = nombre;
        this.curacion = curacion;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public int getCuracion() {
        return curacion;
    }

    @Override
    public String toString() {
        return nombre + " (+" + curacion + " HP)";
    }
}