package dpoo.proyecto.app;

import org.json.JSONObject;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Usuario;

public class SolicitudReembolso {

    private int id;
    private Tiquete tiquete;
    private Usuario solicitante;

    private String motivo;

    private String estado; // PENDIENTE, APROBADA, RECHAZADA
    private double montoSolicitado;
    private double montoAprobado;
    private String observacionAdmin;

    public SolicitudReembolso(int id, Tiquete tiquete, Usuario solicitante, String motivo) {

        this.id = id;
        this.tiquete = tiquete;
        this.solicitante = solicitante;
        this.motivo = motivo;
        this.estado = "PENDIENTE";
    }

    public int getId() {
        return id;
    }

    public Tiquete getTiquete() {
        return tiquete;
    }

    public void setTiquete(Tiquete tiquete) {
        this.tiquete = tiquete;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(double montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    public double getMontoAprobado() {
        return montoAprobado;
    }

    public void setMontoAprobado(double montoAprobado) {
        this.montoAprobado = montoAprobado;
    }

    public String getObservacionAdmin() {
        return observacionAdmin;
    }

    public void setObservacionAdmin(String observacionAdmin) {
        this.observacionAdmin = observacionAdmin;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("motivo", this.motivo);
        json.put("estado", this.estado);
        json.put("montoSolicitado", this.montoSolicitado);
        json.put("montoAprobado", this.montoAprobado);
        json.put("observacionAdmin", this.observacionAdmin);
        if (this.tiquete != null) {
            json.put("tiqueteId", this.tiquete.getId());
            if (this.tiquete.getEvento() != null) {
                json.put("eventoNombre", this.tiquete.getEvento().getNombre());
            }
        }
        if (this.solicitante != null) {
            json.put("solicitanteLogin", this.solicitante.getLogin());
        }
        return json;
    }

    public static SolicitudReembolso fromJSON(JSONObject json) {
        int id = json.getInt("id");
        String motivo = json.optString("motivo", "");
        String estado = json.optString("estado", "PENDIENTE");
        SolicitudReembolso s = new SolicitudReembolso(id, null, null, motivo);
        s.setEstado(estado);
        s.setMontoSolicitado(json.optDouble("montoSolicitado", 0.0));
        s.setMontoAprobado(json.optDouble("montoAprobado", 0.0));
        s.setObservacionAdmin(json.optString("observacionAdmin", null));
        return s;
    }
}
