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
            System.out.println("       LA MAZMORRA ETERNA (RPG)");
            System.out.println("======================================");
            System.out.println("1. Nueva Partida");
            System.out.println("2. Cargar Partida");
            System.out.println("3. Ver Salón de la Fama");
            System.out.println("4. Salir");
            System.out.print("Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    iniciarNuevaPartida();
                    menuJuego(); // Entramos al juego
                    break;
                case "2":
                    if (guardado.existeArchivo()) {
                        EstadoJuego cargado = guardado.cargar(); // Cargamos en una variable temporal

                        if (cargado != null) {
                            partidaActual = cargado; // Si cargó bien, actualizamos la partida
                            System.out.println("✅ ¡Bienvenido de vuelta, " + partidaActual.getJugador().toString() + "!");
                            menuJuego();
                        } else {
                            // Si cargado es null (porque hubo error de versión), avisamos y no hacemos nada
                            System.out.println("⚠️ El archivo de guardado pertenece a una versión anterior y no es compatible.");
                            System.out.println("   Por favor, inicia una Nueva Partida para generar un archivo nuevo.");
                        }
                    } else {
                        System.out.println("❌ No existe ningún archivo de guardado.");
                    }
                    break;
                case "3":
                    ranking.mostrarRanking();
                    break;
                case "4":
                    System.out.println("Saliendo del juego...");
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
        Estadisticas stats = new Estadisticas(15, 5, 10);
        Guerrero heroe = new Guerrero(nombre, stats);

        // ¡Le regalamos 2 pociones al empezar!
        heroe.agregarItem(new Pocion("Poción Salud Menor", 20));
        heroe.agregarItem(new Pocion("Poción Salud Menor", 20));

        partidaActual = new EstadoJuego(heroe);
    }

    private static void menuJuego() {
        boolean jugando = true;
        Random random = new Random();

        while (jugando) {
            // Simulamos coordenadas aleatorias para darle ambiente
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            System.out.println("\n---------------------------------");
            System.out.println(" Te encuentras en la zona segura [" + x + "," + y + "]");
            System.out.println(" Vida: " + partidaActual.getJugador().toString());
            System.out.println("---------------------------------");
            System.out.println("1. Explorar (Buscar Combate)");
            System.out.println("2. Ver Inventario Completo");
            System.out.println("3. Guardar y Volver al Menú Principal");
            System.out.print("Acción: ");

            String op = scanner.nextLine();

            switch (op) {
                case "1":
                    bucleCombate(x, y);
                    // Si el jugador muere en combate, sale del bucle
                    if (!partidaActual.getJugador().estaVivo()) {
                        System.out.println("💀 HAS MUERTO. Fin del juego.");
                        // Guardamos en ranking antes de salir
                        ranking.guardarPuntaje(partidaActual.getJugador().toString(), 1); // Aquí podrías pasar el nivel real
                        jugando = false;
                    }
                    break;
                case "2":
                    // Uso de Lambda: Mostrar todo
                    partidaActual.getJugador().getInventario().forEach(System.out::println);
                    break;
                case "3":
                    guardado.guardar(partidaActual);
                    jugando = false;
                    break;
            }
        }
    }

    private static void bucleCombate(int x, int y) {
        System.out.println("\n⚔️ Entrando a zona peligrosa [" + x + "," + y + "]...");

        // Creamos enemigo aleatorio
        Estadisticas statsMonstruo = new Estadisticas(10 + new Random().nextInt(5), 2, 5);
        Monstruo enemigo = new Monstruo("Orco Salvaje", 40, statsMonstruo);
        Personaje jugador = partidaActual.getJugador();

        System.out.println("¡Un " + enemigo + " aparece frente a ti!");

        boolean enCombate = true;
        while (enCombate && jugador.estaVivo() && enemigo.estaVivo()) {
            System.out.println("\n--- TU TURNO (Vida: " + jugador.toString() + ") vs (Enemigo: " + enemigo.toString() + ") ---");
            System.out.println("1. Atacar");
            System.out.println("2. Usar Objeto (Pociones)");
            System.out.println("3. Intentar Huir");
            System.out.print("Decisión: ");

            String accion = scanner.nextLine();

            switch (accion) {
                case "1":
                    jugador.atacar(enemigo);
                    break;
                case "2":
                    usarPocionEnCombate(jugador); // Llamamos al método con Lambdas
                    break;
                case "3":
                    if (Math.random() > 0.5) { // 50% chance de huir
                        System.out.println("🏃 ¡Lograste escapar con éxito!");
                        return; // Sale del método combate
                    } else {
                        System.out.println("🚫 ¡Fallaste al huir! El enemigo aprovecha tu descuido.");
                    }
                    break;
                default:
                    System.out.println("Pierdes el turno por dudar.");
            }

            // Turno del Enemigo (si sigue vivo)
            if (enemigo.estaVivo()) {
                System.out.println("🔻 Turno del enemigo...");
                enemigo.atacar(jugador);
            } else {
                System.out.println("\n🎉 ¡ENEMIGO DERROTADO!");
                // Recompensa
                jugador.ganarExperiencia(50);
                if (Math.random() > 0.7) { // 30% probabilidad de encontrar poción (loot)
                    System.out.println("🎁 ¡El enemigo soltó una Poción!");
                    jugador.agregarItem(new Pocion("Poción Salud Menor", 20));
                }
                enCombate = false;
            }
        }
    }

    // AQUI ESTÁ LA LAMBDA QUE TE PIDE EL PROFESOR
    private static void usarPocionEnCombate(Personaje jugador) {
        System.out.println("🎒 MOCHILA (Filtrando solo pociones):");

        // 1. Filtramos usando STREAMS y LAMBDAS
        List<Item> pociones = jugador.getInventario().stream()
                .filter(item -> item instanceof Pocion) // Solo queremos pociones
                .collect(Collectors.toList());

        if (pociones.isEmpty()) {
            System.out.println("⚠️ No tienes pociones.");
            return;
        }

        // 2. Mostramos las opciones
        for (int i = 0; i < pociones.size(); i++) {
            System.out.println((i + 1) + ". " + pociones.get(i).getNombre());
        }
        System.out.println("0. Cancelar");

        try {
            System.out.print("Elige poción: ");
            int idx = Integer.parseInt(scanner.nextLine()) - 1;

            if (idx >= 0 && idx < pociones.size()) {
                Pocion p = (Pocion) pociones.get(idx);
                jugador.recibirCuracion(p.getCuracion());

                // Remover del inventario real
                jugador.getInventario().remove(p);
                System.out.println("Has bebido " + p.getNombre());
            }
        } catch (NumberFormatException e) {
            System.out.println("Selección inválida.");
        }
    }
}