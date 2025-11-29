package modelo;

public final class Arma implements Item {
    private String nombre;
    private int danioExtra;

    public Arma(String nombre, int danioExtra) {
        this.nombre = nombre;
        this.danioExtra = danioExtra;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public int getDanioExtra() {
        return danioExtra;
    }

    @Override
    public String toString() {
        return nombre + " (+" + danioExtra + " Daño)";
    }
}