package dpoo.proyecto.consola;

import java.util.List;
import java.util.Map;

import dpoo.proyecto.app.MasterTicket;
import dpoo.proyecto.eventos.Evento;
import dpoo.proyecto.eventos.Localidad;
import dpoo.proyecto.eventos.Venue;
import dpoo.proyecto.tiquetes.Tiquete;
import dpoo.proyecto.tiquetes.TiqueteGeneral;
import dpoo.proyecto.tiquetes.TiqueteNumerado;
import dpoo.proyecto.tiquetes.TiqueteMultipleEntrada;
import dpoo.proyecto.tiquetes.TiqueteMultipleEvento;
import dpoo.proyecto.tiquetes.PaqueteDeluxe;
import dpoo.proyecto.usuarios.Organizador;

public class ConsolaOrganizador extends ConsolaBasica {

    private final MasterTicket sistemaBoleteria;
    private final Organizador organizador;

    public ConsolaOrganizador(MasterTicket sistemaBoleteria, Organizador organizador) {
        this.sistemaBoleteria = sistemaBoleteria;
        this.organizador = organizador;
    }

    public void showMenuOrganizador() {
        System.out.println("=== MENU ORGANIZADOR ===");
        System.out.println("1. Sugerir Venue (requiere aprobación)");
        System.out.println("2. Crear Evento (solo con venue aprobado)");
        System.out.println("3. Crear Localidad para un Evento");
        System.out.println("4. Mis Eventos");
        System.out.println("5. Establecer oferta (descuento) en Localidad");
        System.out.println("6. Crear Paquete de Entradas");
        System.out.println("7. Crear Paquete Multi-Evento");
        System.out.println("8. Crear Paquete Deluxe");
        System.out.println("0. Salir");
    }

    public boolean consolaOrganizador(String opcion) {
        try {
            switch (opcion) {
                case "1":
                    crearVenueFlow();
                    return true;
                case "2":
                    crearEventoFlow();
                    return true;
                case "3":
                    crearLocalidadFlow();
                    return true;
                case "4":
                    listarMisEventos();
                    return true;
                case "5":
                    ofertarLocalidadFlow();
                    return true;
                case "6":
                    crearPaqueteEntradasFlow();
                    return true;
                case "7":
                    crearPaqueteEventosFlow();
                    return true;
                case "8":
                    crearPaqueteDeluxeFlow();
                    return true;
                case "0":
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private void listarMisEventos() {
        List<Evento> eventos = organizador.getEventos();
        if (eventos == null || eventos.isEmpty()) {
            System.out.println("Aún no tienes eventos.");
            return;
        }
        System.out.println("=== MIS EVENTOS ===");
        for (Evento e : eventos) {
            viewEventoOrg(e);
        }
    }

    private void crearVenueFlow() {
        String nombre = pedirCadena("Nombre del venue").toUpperCase();
        String capacidadStr = pedirCadena("Capacidad (entero)");
        String ubicacion = pedirCadena("Ubicación");

        int capacidad = 0;
        try {
            capacidad = Integer.parseInt(capacidadStr);
        } catch (Exception e) {
            System.out.println("Capacidad inválida");
            return;
        }

        // Crear y enviar a aprobación
        Venue v = new Venue();
        v.setNombre(nombre);
        v.setCapacidad(capacidad);
        v.setUbicacion(ubicacion);
        v.setOrganizador(organizador);
        v.setAprobado(false);

        if (sistemaBoleteria.getVenues().get(nombre) != null) {
            System.out.println("Ya existe un venue aprobado con ese nombre.");
            return;
        }
        if (sistemaBoleteria.getVenuesPendientes().get(nombre) != null) {
            System.out.println("Ya hay un venue pendiente con ese nombre.");
            return;
        }
        sistemaBoleteria.proponerVenue(v);
        System.out.println("Venue sugerido. Queda pendiente de aprobación del administrador.");
    }

    private void crearEventoFlow() {
        String nombre = pedirCadena("Nombre del evento").toUpperCase();
        String tipoEvento = pedirCadena("Tipo de evento (e.g., CONCIERTO/TEATRO)");
        String tipoTiquetes = pedirCadena("Tipo de tiquetes general (texto)");
        String fecha = pedirCadena("Fecha (texto libre)");
        String venueNombre = pedirCadena("Nombre del venue APROBADO").toUpperCase();

        Map<String, Evento> eventos = sistemaBoleteria.getEventos();
        if (eventos.get(nombre) != null) {
            System.out.println("Ya existe un evento con ese nombre.");
            return;
        }

        Venue venue = sistemaBoleteria.getVenues().get(venueNombre);
        if (venue == null) {
            System.out.println("Venue no aprobado o inexistente. Pide aprobación o elige otro.");
            return;
        }
        if (!venue.isAprobado()) {
            System.out.println("El venue aún no ha sido aprobado por el administrador.");
            return;
        }

        // Regla: no 2 eventos el mismo día en el mismo venue
        for (Evento e : eventos.values()) {
            if (e.getVenue() != null && e.getVenue().getNombre() != null
                && e.getVenue().getNombre().equalsIgnoreCase(venueNombre)
                && e.getFecha() != null && e.getFecha().equalsIgnoreCase(fecha)) {
                System.out.println("Ya existe un evento ese día en este venue.");
                return;
            }
        }

        // Crear evento con 0 tiquetes; se asignan por localidades
        Evento evento = new Evento(nombre, tipoEvento, tipoTiquetes, 0, venue, fecha);
        evento.setOrganizador(organizador);
        evento.setVenue(venue);

        eventos.put(nombre, evento);
        organizador.addEvento(evento);

        System.out.println("Evento creado y registrado. Agrega localidades para emitir tiquetes.");
    }

    private void crearLocalidadFlow() {
        String nombreEvento = pedirCadena("Evento destino (nombre exacto)").toUpperCase();
        Evento evento = sistemaBoleteria.getEventos().get(nombreEvento);
        if (evento == null) {
            System.out.println("Evento no encontrado.");
            return;
        }
        // Validar que el evento es del organizador
        if (!organizador.getEventos().contains(evento)) {
            System.out.println("Solo puedes modificar tus propios eventos.");
            return;
        }

        String nombreLocalidad = pedirCadena("Nombre de la localidad").toUpperCase();
        String precioStr = pedirCadena("Precio de tiquete (número)");
        String numeradaStr = pedirCadena("¿Es numerada? (s/n)");
        String cantidadStr = pedirCadena("Cantidad de tiquetes a emitir en la localidad");

        double precio = 0.0;
        int cantidad = 0;
        try {
            precio = Double.parseDouble(precioStr);
            cantidad = Integer.parseInt(cantidadStr);
        } catch (Exception e) {
            System.out.println("Valores inválidos");
            return;
        }

        boolean esNumerada = numeradaStr != null && numeradaStr.trim().equalsIgnoreCase("s");

        Localidad localidad = new Localidad(nombreLocalidad, precio, esNumerada, evento);
        evento.addLocalidad(localidad);

        // Emisión de tiquetes de la localidad
        double emision = sistemaBoleteria.getCostoPorEmision();
        String fecha = evento.getFecha();
        int maxPorTx = 4;
        String tipo = esNumerada ? "NUMERADO" : "GENERAL";

        for (int i = 1; i <= cantidad; i++) {
            Tiquete t;
            if (esNumerada) {
                t = new TiqueteNumerado(precio, emision, fecha, "20:00", maxPorTx, tipo, i);
            } else {
                t = new TiqueteGeneral(precio, emision, fecha, "20:00", maxPorTx, tipo);
            }
            t.setEvento(evento);
            t.setLocalidad(localidad.getNombreLocalidad());
            t.setId(sistemaBoleteria.siguienteIdTiquete());
            localidad.addTiquete(t);
            evento.addTiquete(t);
            sistemaBoleteria.registrarTiquete(t);
        }

        // Actualizar contador total del evento
        evento.setCantidadTiquetesDisponibles(evento.getCantidadTiquetesDisponibles() + cantidad);

        System.out.println("Localidad creada, tiquetes emitidos y asociada al evento.");
    }

    private void ofertarLocalidadFlow() {
        String nombreEvento = pedirCadena("Evento (nombre exacto)").toUpperCase();
        Evento evento = sistemaBoleteria.getEventos().get(nombreEvento);
        if (evento == null || !organizador.getEventos().contains(evento)) {
            System.out.println("Evento inválido o no autorizado.");
            return;
        }
        String nombreLocalidad = pedirCadena("Localidad (nombre exacto)").toUpperCase();
        Localidad objetivo = null;
        for (Localidad l : evento.getLocalidades().values()) {
            if (l.getNombreLocalidad().equalsIgnoreCase(nombreLocalidad)) {
                objetivo = l;
                break;
            }
        }
        if (objetivo == null) {
            System.out.println("Localidad no encontrada.");
            return;
        }
        String descuentoStr = pedirCadena("Descuento en porcentaje (ej: 10 para 10%)");
        try {
            double pct = Double.parseDouble(descuentoStr);
            objetivo.setDescuento(pct);
            System.out.println("Oferta aplicada a la localidad.");
        } catch (Exception e) {
            System.out.println("Descuento inválido.");
        }
    }

    private void crearPaqueteEntradasFlow() {
        String nombreEvento = pedirCadena("Evento destino").toUpperCase();
        Evento evento = sistemaBoleteria.getEventos().get(nombreEvento);
        if (!validarEventoPropio(evento)) return;

        String localidad = pedirCadena("Localidad asociada").toUpperCase();
        Localidad loc = evento.getLocalidades().get(localidad);
        if (loc == null) {
            System.out.println("Localidad no encontrada.");
            return;
        }

        int entradas;
        double precio;
        int maximo;
        try {
            entradas = Integer.parseInt(pedirCadena("Cantidad de entradas dentro del paquete"));
            precio = Double.parseDouble(pedirCadena("Precio total del paquete"));
            maximo = Integer.parseInt(pedirCadena("Máximo paquetes por transacción"));
        } catch (NumberFormatException e) {
            System.out.println("Valores numéricos inválidos.");
            return;
        }

        TiqueteMultipleEntrada paquete = new TiqueteMultipleEntrada(precio, sistemaBoleteria.getCostoPorEmision(),
                evento.getFecha(), "20:00", maximo, "PAQUETE_ENTRADAS", evento.getNombre(), localidad, entradas);
        completarRegistroTiquete(paquete, evento, loc);
        System.out.println("Paquete de entradas creado.");
    }

    private void crearPaqueteEventosFlow() {
        int cantidadEventos;
        try {
            cantidadEventos = Integer.parseInt(pedirCadena("¿Cuántos eventos componen el paquete?"));
        } catch (NumberFormatException e) {
            System.out.println("Número inválido.");
            return;
        }
        if (cantidadEventos < 1) {
            System.out.println("Cantidad inválida.");
            return;
        }
        List<String> eventosIncluidos = new java.util.ArrayList<>();
        Evento eventoPrincipal = null;
        for (int i = 0; i < cantidadEventos; i++) {
            String nombreEvento = pedirCadena("Nombre del evento #" + (i + 1)).toUpperCase();
            Evento evento = sistemaBoleteria.getEventos().get(nombreEvento);
            if (!validarEventoPropio(evento)) {
                return;
            }
            eventosIncluidos.add(evento.getNombre());
            if (eventoPrincipal == null) {
                eventoPrincipal = evento;
            }
        }
        double precio;
        int maximo;
        try {
            precio = Double.parseDouble(pedirCadena("Precio total del paquete multi-evento"));
            maximo = Integer.parseInt(pedirCadena("Máximo paquetes por transacción"));
        } catch (NumberFormatException e) {
            System.out.println("Valores numéricos inválidos.");
            return;
        }
        if (eventoPrincipal == null) {
            System.out.println("No hay eventos válidos para crear el paquete.");
            return;
        }
        TiqueteMultipleEvento paquete = new TiqueteMultipleEvento(precio, sistemaBoleteria.getCostoPorEmision(),
                eventoPrincipal.getFecha(), "20:00", maximo, "PAQUETE_EVENTOS", eventosIncluidos);
        Localidad localidadPaquetes = obtenerOCrearLocalidadEspecial(eventoPrincipal, "PAQUETES");
        completarRegistroTiquete(paquete, eventoPrincipal, localidadPaquetes);
        System.out.println("Paquete multi-evento creado.");
    }

    private void crearPaqueteDeluxeFlow() {
        String nombreEvento = pedirCadena("Evento para Paquete Deluxe").toUpperCase();
        Evento evento = sistemaBoleteria.getEventos().get(nombreEvento);
        if (!validarEventoPropio(evento)) return;

        double precio;
        int maximo;
        int beneficiCount;
        try {
            precio = Double.parseDouble(pedirCadena("Precio del Paquete Deluxe"));
            maximo = Integer.parseInt(pedirCadena("Máximo paquetes por transacción"));
            beneficiCount = Integer.parseInt(pedirCadena("Número de beneficios adicionales"));
        } catch (NumberFormatException e) {
            System.out.println("Valores numéricos inválidos.");
            return;
        }
        java.util.List<String> beneficios = new java.util.ArrayList<>();
        for (int i = 0; i < beneficiCount; i++) {
            beneficios.add(pedirCadena("Beneficio #" + (i + 1)));
        }
        int cortesiasCount;
        try {
            cortesiasCount = Integer.parseInt(pedirCadena("Número de cortesías"));
        } catch (NumberFormatException e) {
            System.out.println("Valores numéricos inválidos.");
            return;
        }
        java.util.List<String> cortesias = new java.util.ArrayList<>();
        for (int i = 0; i < cortesiasCount; i++) {
            cortesias.add(pedirCadena("Cortesía #" + (i + 1)));
        }

        PaqueteDeluxe paquete = new PaqueteDeluxe(precio, sistemaBoleteria.getCostoPorEmision(),
                evento.getFecha(), "20:00", maximo, "PAQUETE_DELUXE", "Deluxe " + evento.getNombre(),
                beneficios, cortesias);
        Localidad localidadPaquetes = obtenerOCrearLocalidadEspecial(evento, "DELUXE");
        completarRegistroTiquete(paquete, evento, localidadPaquetes);
        System.out.println("Paquete Deluxe creado.");
    }

    private boolean validarEventoPropio(Evento evento) {
        if (evento == null) {
            System.out.println("Evento no encontrado.");
            return false;
        }
        if (!organizador.getEventos().contains(evento)) {
            System.out.println("Solo puedes operar sobre tus eventos.");
            return false;
        }
        return true;
    }

    private void completarRegistroTiquete(Tiquete tiquete, Evento evento, Localidad localidad) {
        if (tiquete == null || evento == null || localidad == null) return;
        tiquete.setEvento(evento);
        tiquete.setLocalidad(localidad.getNombreLocalidad());
        tiquete.setId(sistemaBoleteria.siguienteIdTiquete());
        localidad.addTiquete(tiquete);
        evento.addTiquete(tiquete);
        evento.setCantidadTiquetesDisponibles(evento.getCantidadTiquetesDisponibles() + 1);
        sistemaBoleteria.registrarTiquete(tiquete);
    }

    private Localidad obtenerOCrearLocalidadEspecial(Evento evento, String nombreBase) {
        String nombre = (nombreBase + "_" + evento.getNombre()).toUpperCase();
        Localidad localidad = evento.getLocalidades().get(nombre);
        if (localidad == null) {
            localidad = new Localidad(nombre, 0, false, evento);
            evento.addLocalidad(localidad);
        }
        return localidad;
    }
}
