package modelo;

import java.io.Serializable;

// "sealed" restringe la herencia: solo Pocion y Arma pueden implementar esta interfaz.
public sealed interface Item extends Serializable permits Pocion, Arma {
    String getNombre();
}