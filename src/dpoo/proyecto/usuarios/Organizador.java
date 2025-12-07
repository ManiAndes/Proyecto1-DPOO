package dpoo.proyecto.usuarios;

import java.util.ArrayList;
import java.util.List;

import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.app.MasterTicket;
import org.json.JSONArray;
import org.json.JSONObject;

public class Organizador extends Usuario {
    
    private List<Evento> eventos = new ArrayList<Evento>();
    private boolean aprobado = true;

    public Organizador(String login, String password) {
        super(login, password);
    }

    public Organizador(String login, String password, List<Evento> eventos) {
        super(login, password);
        this.eventos = eventos;
    }
    
    

    public List<Evento> getEventos() {
        return eventos;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
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

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("type", this.getClass().getSimpleName());
        json.put("login", getLogin());
        json.put("password", getPassword());
        json.put("saldoVirtual", getSaldoVirtual());
        JSONArray enVenta = new JSONArray();
        for (Integer id : getTiquetesEnReventa()) {
            enVenta.put(id);
        }
        json.put("tiquetesEnReventa", enVenta);
        return json;
    }

    public static Organizador fromJSON(JSONObject json) {
        String login = json.getString("login");
        String password = json.getString("password");
        double saldo = json.optDouble("saldoVirtual", 0.0);

        List<Evento> eventos = new ArrayList<>();
        JSONArray je = json.optJSONArray("eventos",null);
        if (je != null) {
            for (int i = 0; i < je.length(); i++) {
                JSONObject eo = je.optJSONObject(i);
                if (eo == null) continue;
                Evento e = Evento.fromJSON(eo);
                String orgLogin = eo.optString("organizadorLogin", null);
                if (orgLogin != null && login.equals(orgLogin)) {
                    eventos.add(e);
                }
            }
        }
        
        
        Organizador o = new Organizador(login, password, eventos);
        o.setSaldoVirtual(saldo);
        JSONArray enVenta = json.optJSONArray("tiquetesEnReventa");
        if (enVenta != null) {
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < enVenta.length(); i++) {
                ids.add(enVenta.optInt(i));
            }
            o.setTiquetesEnReventa(ids);
        }
        return o;
    }
}
