package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.tiquetes.TiqueteGeneral;
import dpoo.proyecto.tiquetes.TiqueteNumerado;
import dpoo.proyecto.usuarios.Administrador;
import dpoo.proyecto.usuarios.Natural;
import dpoo.proyecto.usuarios.Organizador;
import dpoo.proyecto.usuarios.Usuario;
import dpoo.proyecto.usuarios.UsuarioGenerico;

public class PersistenciaMasterticket implements IPersistenciaMasterticket {

    @Override
    public void salvarMasterTicket(String archivo, MasterTicket masterticket) {
        if (archivo == null || masterticket == null) return;
        try {
            Path path = Paths.get(archivo);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            JSONObject json = masterticket.toJSON();
            String content = json.toString(2);
            Files.write(
                path,
                content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MasterTicket cargarMasterTicket(String archivo) {
        if (archivo == null) return new MasterTicket();
        try {
            Path path = Paths.get(archivo);
            if (!Files.exists(path)) return new MasterTicket();
            String content = Files.readString(path, StandardCharsets.UTF_8);
            if (content == null || content.isEmpty()) return new MasterTicket();

            JSONObject root = new JSONObject(content);
            MasterTicket m = new MasterTicket();
            m.setCostoPorEmision(root.optDouble("costoPorEmision", 0.0));

            // Usuarios
            Map<String, UsuarioGenerico> usuarios = new HashMap<>();
            JSONArray ju = root.optJSONArray("usuarios");
            if (ju != null) {
                for (int i = 0; i < ju.length(); i++) {
                    JSONObject uo = ju.optJSONObject(i);
                    if (uo == null) continue;
                    UsuarioGenerico u = usuarioFromJSON(uo);
                    if (u != null) {
                        String login = uo.optString("login", null);
                        if (login != null) usuarios.put(login, u);
                    }
                }
            }
            m.setUsuarios(usuarios);

            // Venues
            Map<String, Venue> venues = new HashMap<>();
            JSONArray jv = root.optJSONArray("venues");
            if (jv != null) {
                for (int i = 0; i < jv.length(); i++) {
                    JSONObject vo = jv.optJSONObject(i);
                    if (vo == null) continue;
                    Venue v = Venue.fromJSON(vo);
                    String orgLogin = vo.optString("organizadorLogin", null);
                    if (orgLogin != null && usuarios.get(orgLogin) instanceof Organizador) {
                        v.setOrganizador((Organizador) usuarios.get(orgLogin));
                    }
                    String key = safeUpper(v.getNombre());
                    venues.put(key, v);
                }
            }
            m.setVenues(venues);

            // Venues pendientes
            Map<String, Venue> venuesPendientes = new HashMap<>();
            JSONArray jvp = root.optJSONArray("venuesPendientes");
            if (jvp != null) {
                for (int i = 0; i < jvp.length(); i++) {
                    JSONObject vo = jvp.optJSONObject(i);
                    if (vo == null) continue;
                    Venue v = Venue.fromJSON(vo);
                    String orgLogin = vo.optString("organizadorLogin", null);
                    if (orgLogin != null && usuarios.get(orgLogin) instanceof Organizador) {
                        v.setOrganizador((Organizador) usuarios.get(orgLogin));
                    }
                    String key = safeUpper(v.getNombre());
                    venuesPendientes.put(key, v);
                }
            }
            m.setVenuesPendientes(venuesPendientes);

            // Eventos (con localidades y tiquetes)
            Map<String, Evento> eventos = new HashMap<>();
            Map<Integer, Tiquete> indiceTiquetes = new HashMap<>();
            JSONArray je = root.optJSONArray("eventos");
            if (je != null) {
                for (int i = 0; i < je.length(); i++) {
                    JSONObject eo = je.optJSONObject(i);
                    if (eo == null) continue;
                    Evento e = Evento.fromJSON(eo);

                    // Re-vincular venue y organizador
                    String venueName = eo.optString("venueName", null);
                    if (venueName != null) {
                        Venue v = venues.get(safeUpper(venueName));
                        if (v != null) e.setVenue(v);
                    }
                    String orgLogin = eo.optString("organizadorLogin", null);
                    if (orgLogin != null && usuarios.get(orgLogin) instanceof Organizador) {
                        e.setOrganizador((Organizador) usuarios.get(orgLogin));
                    }

                    // Localidades
                    JSONArray locs = eo.optJSONArray("localidades");
                    if (locs != null) {
                        for (int j = 0; j < locs.length(); j++) {
                            JSONObject lo = locs.optJSONObject(j);
                            if (lo == null) continue;
                            Localidad l = Localidad.fromJSON(lo, e);
                            e.addLocalidad(l);
                        }
                    }

                    // Tiquetes disponibles
                    JSONArray tiqs = eo.optJSONArray("tiquetes");
                    if (tiqs != null) {
                        for (int j = 0; j < tiqs.length(); j++) {
                            JSONObject to = tiqs.optJSONObject(j);
                            if (to == null) continue;
                            Tiquete t = tiqueteFromJSON(to);
                            if (t == null) continue;
                            t.setEvento(e);
                            String cli = to.optString("clienteLogin", null);
                            if (cli != null && usuarios.get(cli) instanceof Usuario) {
                                t.setCliente((Usuario) usuarios.get(cli));
                            }
                            e.addTiquete(t);
                            indiceTiquetes.put(t.getId(), t);
                        }
                    }

                    // Tiquetes vendidos
                    JSONArray vend = eo.optJSONArray("tiquetesVendidos");
                    if (vend != null) {
                        Map<Integer, Tiquete> vendidos = e.getTiquetesVendidos();
                        for (int j = 0; j < vend.length(); j++) {
                            JSONObject to = vend.optJSONObject(j);
                            if (to == null) continue;
                            Tiquete t = tiqueteFromJSON(to);
                            if (t == null) continue;
                            t.setEvento(e);
                            String cli = to.optString("clienteLogin", null);
                            if (cli != null && usuarios.get(cli) instanceof Usuario) {
                                t.setCliente((Usuario) usuarios.get(cli));
                            }
                            vendidos.put(t.getId(), t);
                            indiceTiquetes.put(t.getId(), t);
                        }
                    }

                    eventos.put(safeUpper(e.getNombre()), e);
                }
            }
            m.setEventos(eventos);

            // Solicitudes de reembolso
            Map<Integer, SolicitudReembolso> solicitudesMap = new HashMap<>();
            JSONArray js = root.optJSONArray("solicitudesReembolso");
            if (js != null) {
                for (int i = 0; i < js.length(); i++) {
                    JSONObject so = js.optJSONObject(i);
                    if (so == null) continue;
                    SolicitudReembolso sr = SolicitudReembolso.fromJSON(so);
                    int tiqueteId = so.optInt("tiqueteId", -1);
                    if (tiqueteId != -1) {
                        Tiquete t = indiceTiquetes.get(tiqueteId);
                        if (t != null) {
                            sr.setTiquete(t);
                        }
                    }
                    String solicitanteLogin = so.optString("solicitanteLogin", null);
                    if (solicitanteLogin != null && usuarios.get(solicitanteLogin) instanceof Usuario) {
                        sr.setSolicitante((Usuario) usuarios.get(solicitanteLogin));
                    }
                    solicitudesMap.put(sr.getId(), sr);
                }
            }
            m.setSolicitudesReembolso(solicitudesMap);

            return m;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return new MasterTicket();
        } catch (Exception e) {
            e.printStackTrace();
            return new MasterTicket();
        }
    }

    private static String safeUpper(String s) {
        return (s == null) ? null : s.toUpperCase();
    }

    private static UsuarioGenerico usuarioFromJSON(JSONObject json) {
        String type = json.optString("type", "");
        switch (type) {
            case "Administrador":
                return Administrador.fromJSON(json);
            case "Organizador":
                return Organizador.fromJSON(json);
            case "Natural":
            default:
                return Natural.fromJSON(json);
        }
    }

    private static Tiquete tiqueteFromJSON(JSONObject json) {
        if (json == null) return null;
        String type = json.optString("type", "TiqueteGeneral");
        Tiquete t;
        if ("TiqueteNumerado".equals(type)) {
            t = TiqueteNumerado.fromJSON(json);
        } else {
            // Reconstruccion basica de TiqueteGeneral
            double precio = json.optDouble("precioOriginal", 0.0);
            double emision = json.optDouble("cuotaAdicionalEmision", 0.0);
            String fecha = json.optString("fecha", "");
            String hora = json.optString("hora", "");
            int max = json.optInt("maximoTiquetesPorTransaccion", 0);
            String tipo = json.optString("tipo", "");
            t = new TiqueteGeneral(precio, emision, fecha, hora, max, tipo);
        }
        // Completar campos comunes
        t.setId(json.optInt("id", t.getId()));
        t.setUsado(json.optBoolean("usado", false));
        return t;
    }
}
