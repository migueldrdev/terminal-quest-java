package modelo;

import java.io.Serializable;

// 1. RECORD (Semana 17): Para datos inmutables como las coordenadas o stats base
// Usamos esto en lugar de una clase con 20 getters y setters.
public record Estadisticas(int fuerza, int defensa, int velocidad) implements Serializable {}