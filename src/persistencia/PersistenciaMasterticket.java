package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import dpoo.proyecto.app.AuditoriaMarketplaceEntry;
import dpoo.proyecto.app.Contraoferta;
import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.app.OfertaReventa;
import dpoo.proyecto.app.SolicitudReembolso;
import dpoo.proyecto.app.TransaccionReventa;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.tiquetes.TiqueteGeneral;
import dpoo.proyecto.tiquetes.TiqueteNumerado;
import dpoo.proyecto.tiquetes.TiqueteMultipleEntrada;
import dpoo.proyecto.tiquetes.TiqueteMultipleEvento;
import dpoo.proyecto.tiquetes.PaqueteDeluxe;
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
            m.setSecuenciaTiquetes(root.optInt("secuenciaTiquetes", 1000));
            m.setSecuenciaSolicitudes(root.optInt("secuenciaSolicitudes", 1));
            m.setSecuenciaOfertas(root.optInt("secuenciaOfertas", 1));
            m.setSecuenciaContraofertas(root.optInt("secuenciaContraofertas", 1));
            m.setSecuenciaTransacciones(root.optInt("secuenciaTransacciones", 1));
            m.setSecuenciaLogMarketplace(root.optInt("secuenciaLogMarketplace", 1));

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
            Map<String, Venue> pendientes = new HashMap<>();
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
            JSONArray jvp = root.optJSONArray("venuesPendientes");
            if (jvp != null) {
                for (int i = 0; i < jvp.length(); i++) {
                    JSONObject vo = jvp.optJSONObject(i);
                    if (vo == null) continue;
                    Venue v = Venue.fromJSON(vo);
                    v.setAprobado(false);
                    String orgLogin = vo.optString("organizadorLogin", null);
                    if (orgLogin != null && usuarios.get(orgLogin) instanceof Organizador) {
                        v.setOrganizador((Organizador) usuarios.get(orgLogin));
                    }
                    String key = safeUpper(v.getNombre());
                    venues.put(key, v);
                    pendientes.put(key, v);
                }
            }
            m.setVenues(venues);
            m.setVenuesPendientes(pendientes);

            // Eventos (con localidades y tiquetes)
            Map<String, Evento> eventos = new HashMap<>();
            Map<Integer, Tiquete> indiceTiquetes = new HashMap<>();
            int maxId = m.getSecuenciaTiquetes();
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
                                Usuario u = (Usuario) usuarios.get(cli);
                                t.setCliente(u);
                                u.agregarTiquete(t);
                            }
                            e.addTiquete(t);
                            Localidad loc = lFromTiquete(e, t);
                            if (loc != null) {
                                loc.addTiquete(t);
                            }
                            indiceTiquetes.put(t.getId(), t);
                            if (t.getId() > maxId) {
                                maxId = t.getId();
                            }
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
                                Usuario u = (Usuario) usuarios.get(cli);
                                t.setCliente(u);
                                u.agregarTiquete(t);
                            }
                            vendidos.put(t.getId(), t);
                            Localidad loc = lFromTiquete(e, t);
                            if (loc != null) {
                                loc.addTiqueteVendido(t);
                            }
                            indiceTiquetes.put(t.getId(), t);
                            if (t.getId() > maxId) {
                                maxId = t.getId();
                            }
                        }
                    }

                    e.setCantidadTiquetesDisponibles(e.getTiquetes().size());
                    eventos.put(safeUpper(e.getNombre()), e);
                }
            }
            m.setEventos(eventos);
            m.setIndiceTiquetes(indiceTiquetes);
            m.setSecuenciaTiquetes(Math.max(m.getSecuenciaTiquetes(), maxId));

            // Solicitudes de reembolso
            Map<Integer, SolicitudReembolso> solicitudesMap = new HashMap<>();
            Map<Integer, SolicitudReembolso> solicitudesProcesadas = new HashMap<>();
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
            JSONArray jsProc = root.optJSONArray("solicitudesReembolsoProcesadas");
            if (jsProc != null) {
                for (int i = 0; i < jsProc.length(); i++) {
                    JSONObject so = jsProc.optJSONObject(i);
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
                    solicitudesProcesadas.put(sr.getId(), sr);
                }
            }
            m.setSolicitudesReembolsoProcesadas(solicitudesProcesadas);

            // Marketplace: ofertas
            Map<Integer, OfertaReventa> ofertasMap = new HashMap<>();
            JSONArray jOfertas = root.optJSONArray("ofertasReventa");
            int maxOfertaId = 0;
            if (jOfertas != null) {
                for (int i = 0; i < jOfertas.length(); i++) {
                    JSONObject of = jOfertas.optJSONObject(i);
                    if (of == null) continue;
                    OfertaReventa oferta = OfertaReventa.fromJSON(of);
                    ofertasMap.put(oferta.getId(), oferta);
                    if (oferta.getId() > maxOfertaId) {
                        maxOfertaId = oferta.getId();
                    }
                }
            }
            m.setOfertasReventa(ofertasMap);
            m.setSecuenciaOfertas(Math.max(m.getSecuenciaOfertas(), maxOfertaId));

            // Marketplace: contraofertas
            Map<Integer, Contraoferta> contraMap = new HashMap<>();
            JSONArray jContra = root.optJSONArray("contraofertas");
            int maxContraId = 0;
            if (jContra != null) {
                for (int i = 0; i < jContra.length(); i++) {
                    JSONObject co = jContra.optJSONObject(i);
                    if (co == null) continue;
                    Contraoferta contra = Contraoferta.fromJSON(co);
                    contraMap.put(contra.getId(), contra);
                    if (contra.getId() > maxContraId) {
                        maxContraId = contra.getId();
                    }
                }
            }
            m.setContraofertas(contraMap);
            m.setSecuenciaContraofertas(Math.max(m.getSecuenciaContraofertas(), maxContraId));

            // Marketplace: transacciones
            Map<Integer, TransaccionReventa> transMap = new HashMap<>();
            JSONArray jTrans = root.optJSONArray("transaccionesReventa");
            int maxTransId = 0;
            if (jTrans != null) {
                for (int i = 0; i < jTrans.length(); i++) {
                    JSONObject to = jTrans.optJSONObject(i);
                    if (to == null) continue;
                    TransaccionReventa tx = TransaccionReventa.fromJSON(to);
                    transMap.put(tx.getId(), tx);
                    if (tx.getId() > maxTransId) {
                        maxTransId = tx.getId();
                    }
                }
            }
            m.setTransaccionesReventa(transMap);
            m.setSecuenciaTransacciones(Math.max(m.getSecuenciaTransacciones(), maxTransId));

            // Marketplace: log de auditoría
            Map<Integer, AuditoriaMarketplaceEntry> logMap = new LinkedHashMap<>();
            List<Integer> logOrden = new ArrayList<>();
            JSONArray jLog = root.optJSONArray("logMarketplace");
            int maxLogId = 0;
            if (jLog != null) {
                for (int i = 0; i < jLog.length(); i++) {
                    JSONObject lo = jLog.optJSONObject(i);
                    if (lo == null) continue;
                    AuditoriaMarketplaceEntry entry = AuditoriaMarketplaceEntry.fromJSON(lo);
                    logMap.put(entry.getId(), entry);
                    logOrden.add(entry.getId());
                    if (entry.getId() > maxLogId) {
                        maxLogId = entry.getId();
                    }
                }
            }
            m.setAuditoriaMarketplace(logMap);
            m.setAuditoriaOrden(logOrden);
            m.setSecuenciaLogMarketplace(Math.max(m.getSecuenciaLogMarketplace(), maxLogId));

            // Organizadores pendientes
            Set<String> orgPend = new LinkedHashSet<>();
            JSONArray jOrgPend = root.optJSONArray("organizadoresPendientes");
            if (jOrgPend != null) {
                for (int i = 0; i < jOrgPend.length(); i++) {
                    String login = jOrgPend.optString(i, null);
                    if (login != null) {
                        orgPend.add(login);
                    }
                }
            }
            m.setOrganizadoresPendientes(orgPend);

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
        switch (type) {
            case "TiqueteNumerado":
                t = TiqueteNumerado.fromJSON(json);
                break;
            case "TiqueteMultipleEntrada":
                t = TiqueteMultipleEntrada.fromJSON(json);
                break;
            case "TiqueteMultipleEvento":
                t = TiqueteMultipleEvento.fromJSON(json);
                break;
            case "PaqueteDeluxe":
                t = PaqueteDeluxe.fromJSON(json);
                break;
            case "TiqueteGeneral":
            default:
                t = TiqueteGeneral.fromJSON(json);
                break;
        }
        t.setId(json.optInt("id", t.getId()));
        t.setUsado(json.optBoolean("usado", false));
        t.setReembolsado(json.optBoolean("reembolsado", false));
        t.setTransferible(json.optBoolean("transferible", true));
        t.setMontoPagado(json.optDouble("montoPagado", 0.0));
        t.setEstado(json.optString("estado", "ACTIVO"));
        t.setLocalidad(json.optString("localidad", null));
        return t;
    }

    private static Localidad lFromTiquete(Evento evento, Tiquete tiquete) {
        if (evento == null || tiquete == null || tiquete.getLocalidad() == null) {
            return null;
        }
        return evento.getLocalidades().get(tiquete.getLocalidad());
    }
}
