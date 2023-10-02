open module usb_gui {
    requires javafx.fxml;
    requires javafx.controls;
    requires usb_middle;
//    requires usb.api; !! this is turned into ... javax.usb.API with ExtraModuleInfo
    requires javax.usb.API;
//    requires com.earthtoernie.usb;
//    requires usb.api;
    exports com.earthtoernie.gui;
    //opens com.earthtoernie.gui.controller to javafx.fxml; // needed if not open module!!!
    //opens com.earthtoernie.gui to javafx.fxml; // needed if not open module!!!
}