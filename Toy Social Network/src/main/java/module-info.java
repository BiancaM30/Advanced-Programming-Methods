module com.example.toysocialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires com.google.common;


    opens com.example.toysocialnetworkgui to javafx.fxml;
    exports com.example.toysocialnetworkgui;

    opens com.example.toysocialnetworkgui.domain to javafx.graphics, javafx.fxml, javafx.base;
}