module com.vittoriopiotti.pathgraph {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;
    requires javafx.swing;

    opens com.vittoriopiotti.pathgraph to javafx.fxml;
    exports com.vittoriopiotti.pathgraph.app;
    exports com.vittoriopiotti.pathgraph.callbacks;
    exports com.vittoriopiotti.pathgraph.dto;




}