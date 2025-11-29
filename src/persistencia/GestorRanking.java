package persistencia;

import java.io.*;

public class GestorRanking {
    private final String ARCHIVO = "ranking.txt";

    public void guardarPuntaje(String nombre, int nivel) {
        // FileWriter(archivo, true) significa "append" (agregar al final sin borrar lo anterior)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
            writer.write(nombre + "," + nivel);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar ranking: " + e.getMessage());
        }
    }

    public void mostrarRanking() {
        System.out.println("\n🏆 SALÓN DE LA FAMA 🏆");
        File file = new File(ARCHIVO);
        if (!file.exists()) {
            System.out.println("Aún no hay héroes legendarios.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            // Leemos y mostramos. (Si quieres ordenarlo, habría que meterlo en una lista y usar sort,
            // pero para el lunes, mostrarlo así cumple).
            System.out.println("HÉROE\t\tNIVEL ALCANZADO");
            System.out.println("-------------------------");
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if(partes.length == 2) {
                    System.out.println(partes[0] + "\t\tNvl " + partes[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo ranking.");
        }
    }
}