package dpoo.proyecto.marketplace;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Usuario;

public class OfertaReventa {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int id;
    private final Tiquete tiquete;
    private final Usuario vendedor;
    private final double precioBase;
    private final LocalDateTime fechaCreacion;
    private final Map<Integer, ContraofertaReventa> contraofertas;

    private boolean activa;
    private Usuario compradorFinal;
    private double precioFinal;
    private String motivoCierre;

    public OfertaReventa(int id, Tiquete tiquete, Usuario vendedor, double precioBase) {
        this(id, tiquete, vendedor, precioBase, LocalDateTime.now(), true);
    }

    public OfertaReventa(int id, Tiquete tiquete, Usuario vendedor, double precioBase,
            LocalDateTime fechaCreacion, boolean activa) {
        this.id = id;
        this.tiquete = tiquete;
        this.vendedor = vendedor;
        this.precioBase = precioBase;
        this.fechaCreacion = fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
        this.contraofertas = new TreeMap<>();
        this.activa = activa;
    }

    public int getId() {
        return id;
    }

    public Tiquete getTiquete() {
        return tiquete;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public boolean estaActiva() {
        return activa;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Usuario getCompradorFinal() {
        return compradorFinal;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public String getMotivoCierre() {
        return motivoCierre;
    }

    public void marcarCerrada(String motivo) {
        this.activa = false;
        this.motivoCierre = motivo;
    }

    public void marcarVendida(Usuario comprador, double precioFinal) {
        this.activa = false;
        this.compradorFinal = comprador;
        this.precioFinal = precioFinal;
        this.motivoCierre = "VENDIDA";
    }

    public void agregarContraoferta(ContraofertaReventa contraoferta) {
        if (contraoferta != null) {
            this.contraofertas.put(contraoferta.getId(), contraoferta);
        }
    }

    public ContraofertaReventa getContraoferta(int id) {
        return this.contraofertas.get(id);
    }

    public Map<Integer, ContraofertaReventa> getContraofertas() {
        return Collections.unmodifiableMap(contraofertas);
    }

    public List<ContraofertaReventa> getContraofertasPendientes() {
        List<ContraofertaReventa> pendientes = new ArrayList<>();
        for (ContraofertaReventa c : contraofertas.values()) {
            if (ContraofertaReventa.PENDIENTE.equals(c.getEstado())) {
                pendientes.add(c);
            }
        }
        return pendientes;
    }

    public String descripcionBasica() {
        Evento evento = tiquete != null ? tiquete.getEvento() : null;
        String eventoNombre = evento != null ? evento.getNombre() : "N/A";
        return "[" + id + "] Tiquete #" + (tiquete != null ? tiquete.getId() : -1)
                + " - Evento: " + eventoNombre
                + " - Precio: " + precioBase
                + " - Vend: " + (vendedor != null ? vendedor.getLogin() : "N/A")
                + " - Creada: " + FORMATTER.format(fechaCreacion)
                + (activa ? " - ACTIVA" : " - INACTIVA");
    }

    @Override
    public String toString() {
        Evento evento = tiquete != null ? tiquete.getEvento() : null;
        String eventoNombre = evento != null ? evento.getNombre() : "N/A";
        return "#" + id + " | Tiquete " + (tiquete != null ? tiquete.getId() : "?") + " | " + eventoNombre
                + " | $" + precioBase + (activa ? " (ACTIVA)" : " (" + (motivoCierre != null ? motivoCierre : "INACTIVA") + ")");
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("tiqueteId", tiquete != null ? tiquete.getId() : -1);
        json.put("precioBase", precioBase);
        json.put("fechaCreacion", FORMATTER.format(fechaCreacion));
        json.put("activa", activa);
        if (vendedor != null) {
            json.put("vendedorLogin", vendedor.getLogin());
        }
        if (compradorFinal != null) {
            json.put("compradorFinal", compradorFinal.getLogin());
        }
        json.put("precioFinal", precioFinal);
        if (motivoCierre != null) {
            json.put("motivoCierre", motivoCierre);
        }
        JSONArray contra = new JSONArray();
        for (ContraofertaReventa c : contraofertas.values()) {
            contra.put(c.toJSON());
        }
        json.put("contraofertas", contra);
        return json;
    }

    public static OfertaReventa fromJSON(JSONObject json, Tiquete tiquete, Usuario vendedor,
            Map<String, Usuario> mapaUsuarios) {
        if (json == null || tiquete == null || vendedor == null) {
            return null;
        }
        if (mapaUsuarios == null) {
            mapaUsuarios = Collections.emptyMap();
        }
        int id = json.optInt("id", 0);
        double precioBase = json.optDouble("precioBase", 0.0);
        String fechaStr = json.optString("fechaCreacion", "");
        LocalDateTime fecha = fechaStr.isEmpty()
                ? LocalDateTime.now()
                : LocalDateTime.parse(fechaStr, FORMATTER);
        boolean activa = json.optBoolean("activa", true);
        OfertaReventa oferta = new OfertaReventa(id, tiquete, vendedor, precioBase, fecha, activa);
        oferta.precioFinal = json.optDouble("precioFinal", 0.0);
        oferta.motivoCierre = json.optString("motivoCierre", null);

        String compradorFinalLogin = json.optString("compradorFinal", null);
        if (compradorFinalLogin != null && mapaUsuarios.containsKey(compradorFinalLogin)) {
            oferta.compradorFinal = mapaUsuarios.get(compradorFinalLogin);
        }
        JSONArray contra = json.optJSONArray("contraofertas");
        if (contra != null) {
            for (int i = 0; i < contra.length(); i++) {
                JSONObject cj = contra.optJSONObject(i);
                if (cj == null) continue;
                String compradorLogin = cj.optString("compradorLogin", null);
                Usuario comprador = compradorLogin != null ? mapaUsuarios.get(compradorLogin) : null;
                ContraofertaReventa c = ContraofertaReventa.fromJSON(cj, comprador);
                if (c != null) {
                    oferta.agregarContraoferta(c);
                }
            }
        }
        return oferta;
    }
}
