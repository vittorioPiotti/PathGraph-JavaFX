module com.vittoriopiotti.pathgraph {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.logging;
    requires javafx.swing;

    opens com.vittoriopiotti.pathgraph to javafx.fxml;
    exports com.vittoriopiotti.pathgraph;




}