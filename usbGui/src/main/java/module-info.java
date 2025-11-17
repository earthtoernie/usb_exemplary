open module usb_gui {
    requires javafx.fxml;
    requires javafx.controls;
    requires usb_middle;
    requires javax.usb.API;
    requires java.sql;
    requires com.earthtoernie.usbdb;
    exports com.earthtoernie.gui;
    //opens com.earthtoernie.gui.controller to javafx.fxml; // needed if not open module!!!
    //opens com.earthtoernie.gui to javafx.fxml; // needed if not open module!!!
}