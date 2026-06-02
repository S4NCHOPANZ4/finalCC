package co.ciencias.finalcc.view;

import co.ciencias.finalcc.controller.GuiController;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class Gui extends Application {

    private static WebEngine engine;
    private final GuiController controller = new GuiController();
    private final JSBridge bridge = new JSBridge(controller);

    public static WebEngine getEngine() { return engine; }

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        engine = webView.getEngine();

        engine.setConfirmHandler(mensaje -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacion");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            java.util.Optional<javafx.scene.control.ButtonType> resultado = alert.showAndWait();
            return resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.OK;
        });

        engine.load(getClass().getResource("/gui/interfaz.html").toExternalForm());

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("bridge", bridge);
                controller.iniciarPolling();
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