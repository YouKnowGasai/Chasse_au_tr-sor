module com.example.chasse_au_tresor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.chasse_au_tresor to javafx.fxml;
    exports com.example.chasse_au_tresor;
}