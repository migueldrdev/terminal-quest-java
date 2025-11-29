package util;

import java.io.*;

// <T> significa que esta clase puede guardar cualquier Tipo de dato
// siempre y cuando ese tipo 'T' extienda de Serializable.
public class Serializador<T extends Serializable> {

    private String nombreArchivo;

    public Serializador(String nombreArchivo) {
        // Aseguramos que el archivo tenga una extensión .dat
        this.nombreArchivo = nombreArchivo.endsWith(".dat") ? nombreArchivo : nombreArchivo + ".dat";
    }

    /**
     * Guarda el objeto en el archivo binario.
     * @param objeto El objeto a guardar (puede ser el Jugador o el Estado del Juego)
     * @return true si se guardó correctamente
     */
    public boolean guardar(T objeto) {
        // El "try-with-resources" cierra el flujo automáticamente (Semana 16/17)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            oos.writeObject(objeto);
            System.out.println("✅ Partida guardada exitosamente en " + nombreArchivo);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error al guardar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga el objeto desde el archivo binario.
     * @return El objeto recuperado o null si no existe.
     */
    @SuppressWarnings("unchecked") // Suprimimos la advertencia de cast, ya que controlamos el tipo
    public T cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
            return (T) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("ℹ️ No se encontró archivo de guardado previo.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error al cargar: " + e.getMessage());
            return null;
        }
    }

    // Método extra para verificar si existe partida guardada
    public boolean existeArchivo() {
        File f = new File(nombreArchivo);
        return f.exists() && !f.isDirectory();
    }
}
