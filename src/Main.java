import modelo.*;
import util.GestorRanking;
import util.Serializador;
import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static Serializador<EstadoJuego> guardado = new Serializador<>("partida_guardada.dat");
    static GestorRanking ranking = new GestorRanking();
    static EstadoJuego partidaActual;

    public static void main(String[] args) {
        boolean enMenuPrincipal = true;
        while (enMenuPrincipal) {
            System.out.println("\n======================================");
            System.out.println("       LA MAZMORRA ETERNA - v2.0");
            System.out.println("======================================");
            System.out.println("1. Nueva Partida");
            System.out.println("2. Cargar Partida (Último guardado)");
            System.out.println("3. Ver Salón de la Fama");
            System.out.println("4. Salir");
            System.out.print("Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    iniciarNuevaPartida();
                    menuJuego();
                    break;
                case "2":
                    if (guardado.existeArchivo()) {
                        EstadoJuego cargado = guardado.cargar();
                        if (cargado != null) {
                            partidaActual = cargado;
                            System.out.println("✅ Partida cargada. Piso: " + partidaActual.getPisoActual());
                            menuJuego();
                        } else {
                            System.out.println("⚠️ Archivo incompatible. Inicia nueva partida.");
                        }
                    } else {
                        System.out.println("❌ No hay partida guardada.");
                    }
                    break;
                case "3":
                    ranking.mostrarRanking();
                    break;
                case "4":
                    enMenuPrincipal = false;
                    break;
                default:
                    System.out.println("Opción incorrecta.");
            }
        }
    }

    private static void iniciarNuevaPartida() {
        System.out.print("Introduce el nombre de tu Guerrero: ");
        String nombre = scanner.nextLine();

        // Stats base equilibrados
        Estadisticas stats = new Estadisticas(12, 4, 8);
        Guerrero heroe = new Guerrero(nombre, stats);

        // Inventario inicial: 2 pociones y un arma básica extra
        heroe.agregarItem(new Pocion("Poción Pequeña", 20));
        heroe.agregarItem(new Pocion("Poción Pequeña", 20));
        heroe.agregarItem(new Arma("Daga de Bronce", 8)); // Un poco mejor que la oxidada

        partidaActual = new EstadoJuego(heroe);
    }

    private static void menuJuego() {
        boolean jugando = true;

        while (jugando) {
            int piso = partidaActual.getPisoActual();
            System.out.println("\n---------------------------------");
            System.out.println(" 🏰 ESTÁS EN EL PISO " + piso);
            System.out.println(" Héroe: " + partidaActual.getJugador().toString());
            Guerrero g = (Guerrero) partidaActual.getJugador();
            System.out.println(" Arma actual: " + g.getArmaEquipada().getNombre() + " (+" + g.getArmaEquipada().getDanioExtra() + " daño)");
            System.out.println("---------------------------------");
            System.out.println("1. Avanzar y Explorar (Combate)");
            System.out.println("2. Gestión de Inventario");
            System.out.println("3. Guardar y Salir al Menú");
            System.out.print("Acción: ");

            String op = scanner.nextLine();

            switch (op) {
                case "1":
                    bucleCombate(piso);
                    if (!partidaActual.getJugador().estaVivo()) {
                        System.out.println("💀 HAS MUERTO.");
                        ranking.guardarPuntaje(partidaActual.getJugador().toString() + " (Muerto en Piso " + piso + ")", piso);
                        jugando = false;
                    }
                    break;
                case "2":
                    gestionarInventario((Guerrero) partidaActual.getJugador());
                    break;
                case "3":
                    System.out.println("💾 Guardando partida...");
                    // Guardamos también en ranking como "Progreso actual"
                    ranking.guardarPuntaje(partidaActual.getJugador().toString() + " (Guardado)", piso);
                    guardado.guardar(partidaActual);
                    jugando = false;
                    break;
            }
        }
    }

    private static void bucleCombate(int dificultad) {
        System.out.println("\n⚔️ Adentrándose en la oscuridad del Piso " + dificultad + "...");

        // --- 1. Generación de Enemigo según Nivel ---
        int vidaBase = 40 + (dificultad * 10);
        int vidaAleatoria = vidaBase + new Random().nextInt(40); // Entre 40 y 80 al inicio
        int fuerzaEnemigo = 8 + (dificultad * 2);

        Estadisticas statsMonstruo = new Estadisticas(fuerzaEnemigo, 2 + dificultad, 5);
        String nombreMonstruo = (dificultad > 3) ? "Ogro de Cueva" : "Orco Explorador";

        Monstruo enemigo = new Monstruo(nombreMonstruo, vidaAleatoria, statsMonstruo);
        Personaje jugador = partidaActual.getJugador();

        System.out.println("¡Un " + enemigo.toString() + " bloquea el camino!");
        System.out.println("HP Enemigo: " + vidaAleatoria);

        boolean enCombate = true;

        while (enCombate && jugador.estaVivo() && enemigo.estaVivo()) {
            System.out.println("\n--- TU TURNO ---");
            System.out.println("1. Atacar");
            System.out.println("2. Usar Poción");
            System.out.println("3. Cambiar Arma");
            System.out.println("4. Huir");
            System.out.print("Decisión: ");

            String accion = scanner.nextLine();
            boolean turnoJugadorExitoso = false; // Bandera para controlar el turno

            switch (accion) {
                case "1":
                    jugador.atacar(enemigo);
                    turnoJugadorExitoso = true; // Gastaste turno atacando
                    break;
                case "2":
                    // Si usa poción devuelve true, si cancela devuelve false
                    turnoJugadorExitoso = usarPocion(jugador);
                    break;
                case "3":
                    // Cambiar arma NO gasta turno (decisión de diseño para hacerlo dinámico)
                    cambiarArma((Guerrero) jugador);
                    turnoJugadorExitoso = false;
                    break;
                case "4":
                    if (Math.random() > 0.6) {
                        System.out.println("🏃 ¡Lograste escapar!");
                        return;
                    } else {
                        System.out.println("🚫 ¡No pudiste escapar!");
                        turnoJugadorExitoso = true; // Fallaste al huir, pierdes turno
                    }
                    break;
                default:
                    System.out.println("Opción no válida.");
            }

            // --- TURNO DEL ENEMIGO ---
            // Solo ataca si el jugador hizo una acción válida y el enemigo sigue vivo
            if (enemigo.estaVivo() && turnoJugadorExitoso) {
                System.out.println("🔻 El enemigo contraataca...");
                enemigo.atacar(jugador);
            } else if (!enemigo.estaVivo()) {
                System.out.println("\n🎉 ¡ENEMIGO DERROTADO!");

                // --- Recompensas y Loot ---
                int expGanada = 30 * dificultad;
                jugador.ganarExperiencia(expGanada);

                // Drop de Poción (50% de la vida MAX del enemigo)
                // Calculamos curación basada en la vida inicial del enemigo (aprox)
                int curacionPocion = vidaAleatoria / 2;
                System.out.println("🎁 Botín: Encontraste una Poción de Sangre de Orco (+" + curacionPocion + " HP)");
                jugador.agregarItem(new Pocion("Poción de Orco", curacionPocion));

                // Drop de Arma (Probabilidad 30%)
                if (Math.random() < 0.3) {
                    int danioArma = 5 + (dificultad * 3); // Mejores armas en pisos altos
                    Arma armaDrop = new Arma("Espada del Piso " + dificultad, danioArma);
                    System.out.println("🎁 ¡INCREÍBLE! El enemigo soltó: " + armaDrop.getNombre());
                    jugador.agregarItem(armaDrop);
                }

                // Avanzamos de piso
                partidaActual.avanzarPiso();
                enCombate = false;
            }
        }
    }

    // --- MÉTODOS AUXILIARES CON LAMBDAS ---

    private static boolean usarPocion(Personaje jugador) {
        System.out.println("🎒 POCIONES DISPONIBLES:");
        List<Item> pociones = jugador.getInventario().stream()
                .filter(i -> i instanceof Pocion)
                .collect(Collectors.toList());

        if (pociones.isEmpty()) {
            System.out.println("⚠️ No tienes pociones.");
            return false; // No gastó turno
        }

        for (int i = 0; i < pociones.size(); i++) {
            System.out.println((i + 1) + ". " + pociones.get(i).toString());
        }
        System.out.println("0. Cancelar");

        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < pociones.size()) {
                Pocion p = (Pocion) pociones.get(idx);
                jugador.recibirCuracion(p.getCuracion());
                jugador.getInventario().remove(p);
                return true; // SÍ gastó turno
            }
        } catch (Exception e) {}
        return false; // Canceló
    }

    private static void cambiarArma(Guerrero jugador) {
        System.out.println("⚔️ ARMAS EN MOCHILA:");
        List<Item> armas = jugador.getInventario().stream()
                .filter(i -> i instanceof Arma)
                .collect(Collectors.toList());

        if (armas.isEmpty()) {
            System.out.println("⚠️ No tienes otras armas.");
            return;
        }

        for (int i = 0; i < armas.size(); i++) {
            System.out.println((i + 1) + ". " + armas.get(i).toString());
        }
        System.out.println("0. Cancelar");

        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < armas.size()) {
                Arma a = (Arma) armas.get(idx);
                jugador.equiparArma(a);
                // No la removemos del inventario, solo la equipamos
            }
        } catch (Exception e) {}
    }

    private static void gestionarInventario(Guerrero jugador) {
        System.out.println("--- INVENTARIO ---");
        jugador.getInventario().forEach(System.out::println);
        System.out.println("Arma equipada: " + jugador.getArmaEquipada());
        // Aquí podrías agregar lógica para tirar items si quisieras
    }
}