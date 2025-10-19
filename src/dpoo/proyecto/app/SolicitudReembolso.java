package dpoo.proyecto.app;

import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.usuarios.Usuario;

public class SolicitudReembolso {

    private int id;
    private Tiquete tiquete;
    private Usuario solicitante;

    private String motivo;

    private String estado; // PENDIENTE, APROBADA, RECHAZADA

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

    public Usuario getSolicitante() {
        return solicitante;
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
}

