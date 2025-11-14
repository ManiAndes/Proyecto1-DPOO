open module masterticket.app {
    requires transitive org.json;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    exports dpoo.proyecto.app;
    exports dpoo.proyecto.consola;
    exports dpoo.proyecto.eventos;
    exports dpoo.proyecto.tiquetes;
    exports dpoo.proyecto.usuarios;
    exports persistencia;
}