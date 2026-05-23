module org.ContactManager {
    requires atlantafx.base;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.github.kwhat.jnativehook;

    exports org.ContactManager;
    opens org.ContactManager to javafx.fxml;
    exports org.lib;
    opens org.lib to javafx.fxml;
}