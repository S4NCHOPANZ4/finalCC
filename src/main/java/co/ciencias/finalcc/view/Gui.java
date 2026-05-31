package co.ciencias.finalcc.view;

import co.ciencias.finalcc.controller.GuiController;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * Ventana principal de la aplicación AutoRescate 24/7.
 *
 * <p>Carga la interfaz HTML y, una vez lista:</p>
 * <ol>
 *   <li>Inyecta {@code bridge} ({@link JSBridge}) en el contexto JS para
 *       recibir acciones del usuario (registrar, atender, terminar, reparar).</li>
 *   <li>Inicia el {@link GuiController} que empuja el estado de cada puesto
 *       al JS cada 500 ms llamando a {@code actualizarPuesto(id, datos)}.
 *       Los datos viajan como objeto JSON literal en el argumento de la función,
 *       nunca como string a parsear, eliminando los fallos silenciosos de
 *       WebEngine con caracteres especiales.</li>
 * </ol>
 */
public class Gui extends Application {

    private static WebEngine engine;
    private final GuiController controller = new GuiController();
    private final JSBridge bridge = new JSBridge(); // ← CAMPO DE INSTANCIA

    public static WebEngine getEngine() { return engine; }

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        engine = webView.getEngine();

        engine.load(
            getClass().getResource("/gui/interfaz.html").toExternalForm()
        );

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("bridge", bridge); // ← usa el campo, no un new anónimo

                controller.iniciarPolling();
                System.out.println("[SISTEMA] Interfaz lista. Polling activo desde Java.");
            }
        });

        stage.setOnCloseRequest(e -> controller.detener());
        stage.setTitle("Tablero de Control Operativo - AutoRescate 24/7");
        stage.setScene(new Scene(webView, 1300, 750));
        stage.show();
    }

    public static void launchApp(String[] args) {
        launch(args);
    }
}