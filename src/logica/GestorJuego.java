package logica;

import modelo.*;
import persistencia.Serializador;
import persistencia.GestorRanking;
import vista.InterfazConsola;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

public class GestorJuego {
    private EstadoJuego partida;
    private final InterfazConsola vista;
    private final Serializador<EstadoJuego> serializador;
    private final GestorRanking ranking;
    private boolean ejecutando;

    public GestorJuego() {
        this.vista = new InterfazConsola();
        this.serializador = new Serializador<>("partida_guardada.dat");
        this.ranking = new GestorRanking();
        this.ejecutando = true;
    }

    public void iniciar() {
        vista.mostrarTitulo();
        while (ejecutando) {
            String opcion = vista.mostrarMenuPrincipal();
            procesarOpcionPrincipal(opcion);
        }
        vista.cerrar();
    }

    private void procesarOpcionPrincipal(String opcion) {
        switch (opcion) {
            case "1":
                crearNuevaPartida();
                bucleJuego();
                break;
            case "2":
                cargarPartida();
                break;
            case "3":
                ranking.mostrarRanking();
                break;
            case "4":
                vista.mensaje("Saliendo...");
                ejecutando = false;
                break;
            default:
                vista.mensaje("Opción inválida.");
        }
    }

    private void crearNuevaPartida() {
        String nombre = vista.pedirNombreHeroe();
        Estadisticas stats = new Estadisticas(12, 4, 8);
        Guerrero heroe = new Guerrero(nombre, stats);
        heroe.agregarItem(new Pocion("Poción Inicio", 20));
        heroe.agregarItem(new Pocion("Poción Inicio", 20));
        heroe.agregarItem(new Arma("Daga Simple", 6));

        this.partida = new EstadoJuego(heroe);
        vista.mensaje("✨ ¡Bienvenido a la mazmorra, " + nombre + "!");
    }

    private void cargarPartida() {
        if (serializador.existeArchivo()) {
            EstadoJuego cargado = serializador.cargar();
            if (cargado != null) {
                this.partida = cargado;
                vista.mensaje("✅ Partida cargada correctamente.");
                bucleJuego();
            } else {
                vista.mensaje("⚠️ Error: Archivo incompatible.");
            }
        } else {
            vista.mensaje("❌ No hay partida guardada.");
        }
    }

    private void bucleJuego() {
        boolean enPartida = true;
        while (enPartida) {
            Guerrero g = (Guerrero) partida.getJugador();
            String op = vista.mostrarMenuJuego(partida.getPisoActual(), g.toString(), g.getArmaEquipada().getNombre());

            switch (op) {
                case "1":
                    procesarCombate();
                    if (!g.estaVivo()) {
                        ranking.guardarPuntaje(g.toString(), partida.getPisoActual());
                        enPartida = false;
                    }
                    break;
                case "2":
                    vista.mensaje("--- INVENTARIO ---");
                    g.getInventario().forEach(i -> vista.mensaje(i.toString()));
                    break;
                case "3":
                    serializador.guardar(partida);
                    ranking.guardarPuntaje(g.toString() + " (Guardado)", partida.getPisoActual());
                    enPartida = false;
                    break;
            }
        }
    }

    private void procesarCombate() {
        int piso = partida.getPisoActual();
        vista.mensaje("⚔️ Entrando al combate en Piso " + piso);

        // Generar enemigo 
        int vidaRnd = 40 + (piso * 10) + new Random().nextInt(30);
        Estadisticas statsM = new Estadisticas(8 + (piso*2), 2 + piso, 5);
        Monstruo enemigo = new Monstruo("Orco Nvl." + piso, vidaRnd, statsM);
        Personaje jugador = partida.getJugador();

        boolean combateActivo = true;
        while (combateActivo && jugador.estaVivo() && enemigo.estaVivo()) {
            String accion = vista.mostrarMenuCombate(jugador.toString(), enemigo.toString());
            boolean turnoExitoso = false;

            switch (accion) {
                case "1":
                    jugador.atacar(enemigo);
                    turnoExitoso = true;
                    break;
                case "2":
                    turnoExitoso = usarPocion((Guerrero) jugador);
                    break;
                case "3":
                    cambiarArma((Guerrero) jugador); // No gasta turno
                    break;
                case "4":
                    if (Math.random() > 0.6) {
                        vista.mensaje("🏃 ¡Escapaste!");
                        return;
                    } else {
                        vista.mensaje("🚫 Fallaste al huir.");
                        turnoExitoso = true;
                    }
                    break;
            }

            if (enemigo.estaVivo() && turnoExitoso) {
                vista.mensaje("🔻 Turno enemigo...");
                enemigo.atacar(jugador);
            } else if (!enemigo.estaVivo()) {
                vista.mensaje("🎉 VICTORIA!");
                jugador.ganarExperiencia(50 * piso);
                // Drop simple
                if(Math.random() > 0.5) jugador.agregarItem(new Pocion("Poción Drop", 30));
                partida.avanzarPiso();
                combateActivo = false;
            }
        }
    }

    private boolean usarPocion(Guerrero g) {
        // Filtramos con lambdas
        List<Item> pociones = g.getInventario().stream()
                .filter(i -> i instanceof Pocion).collect(Collectors.toList());

        // Usamos la vista para elegir
        int index = vista.seleccionarItem(pociones);
        if (index >= 0) {
            Pocion p = (Pocion) pociones.get(index);
            g.recibirCuracion(p.getCuracion());
            g.getInventario().remove(p);
            return true;
        }
        return false;
    }

    private void cambiarArma(Guerrero g) {
        List<Item> armas = g.getInventario().stream()
                .filter(i -> i instanceof Arma).collect(Collectors.toList());

        int index = vista.seleccionarItem(armas);
        if (index >= 0) {
            g.equiparArma((Arma) armas.get(index));
        }
    }
}