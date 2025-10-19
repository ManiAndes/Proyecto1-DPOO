package dpoo.proyecto.usuarios;

import java.util.ArrayList;
import java.util.List;

import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.app.MasterTicket;

public class Organizador extends Usuario {
    
    private List<Evento> eventos = new ArrayList<Evento>();

    public Organizador(String login, String password) {
        super(login, password);
    }
    
    

    public List<Evento> getEventos() {
        return eventos;
    }

    public void addEvento(Evento evento) {
        if (!this.eventos.contains(evento)) {
            this.eventos.add(evento);
        }
    }

    public Evento crearEvento(MasterTicket sistema, String nombre, String tipoEvento, String tipoTiquetes,
                               int cantidadTiquetesDisponibles, Venue venue, String fecha) {
        Evento evento = new Evento(nombre, tipoEvento, tipoTiquetes, cantidadTiquetesDisponibles, venue, fecha);
        evento.setOrganizador(this);
        evento.setVenue(venue);
        sistema.getEventos().put(nombre.toUpperCase(), evento);
        addEvento(evento);
        return evento;
    }

    public Venue crearVenue(MasterTicket sistema, String nombre, int capacidad, String ubicacion) {
        Venue v = new Venue();
        v.setNombre(nombre.toUpperCase());
        v.setCapacidad(capacidad);
        v.setUbicacion(ubicacion);
        v.setOrganizador(this);
        sistema.getVenues().put(v.getNombre(), v);
        return v;
    }

    public Localidad crearLocalidad(Evento evento, String nombreLocalidad, double precio, boolean esNumerada) {
        Localidad l = new Localidad(nombreLocalidad.toUpperCase(), precio, esNumerada, evento);
        evento.addLocalidad(l);
        return l;
    }

}
