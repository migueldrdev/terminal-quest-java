package vista;

import modelo.Personaje;
import modelo.Item;
import modelo.Pocion;
import modelo.Arma;
import java.util.Scanner;
import java.util.List;

public class InterfazConsola {
    private Scanner scanner;

    public InterfazConsola() {
        this.scanner = new Scanner(System.in);
    }

    public void mostrarTitulo() {
        System.out.println("\n======================================");
        System.out.println("       LA MAZMORRA ETERNA - v3.0 (Refactor)");
        System.out.println("======================================");
    }

    public String mostrarMenuPrincipal() {
        System.out.println("1. Nueva Partida");
        System.out.println("2. Cargar Partida");
        System.out.println("3. Ver Salón de la Fama");
        System.out.println("4. Salir");
        System.out.print("Elige una opción: ");
        return scanner.nextLine();
    }

    public String pedirNombreHeroe() {
        System.out.print("Introduce el nombre de tu Guerrero: ");
        return scanner.nextLine();
    }

    public String mostrarMenuJuego(int piso, String infoJugador, String infoArma) {
        System.out.println("\n---------------------------------");
        System.out.println(" 🏰 PISO ACTUAL: " + piso);
        System.out.println(" Héroe: " + infoJugador);
        System.out.println(" Arma: " + infoArma);
        System.out.println("---------------------------------");
        System.out.println("1. Avanzar (Combate)");
        System.out.println("2. Inventario");
        System.out.println("3. Guardar y Salir");
        System.out.print("Acción: ");
        return scanner.nextLine();
    }

    public String mostrarMenuCombate(String heroe, String enemigo) {
        System.out.println("\n--- TU TURNO (" + heroe + ") vs (" + enemigo + ") ---");
        System.out.println("1. Atacar");
        System.out.println("2. Usar Poción");
        System.out.println("3. Cambiar Arma");
        System.out.println("4. Huir");
        System.out.print("Decisión: ");
        return scanner.nextLine();
    }

    public int seleccionarItem(List<Item> items) {
        if (items.isEmpty()) {
            System.out.println("⚠️ No tienes items de este tipo.");
            return -1;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).toString());
        }
        System.out.println("0. Cancelar");
        System.out.print("Elige: ");
        try {
            return Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void mensaje(String msg) {
        System.out.println(msg);
    }

    public void cerrar() {
        scanner.close();
    }
}