module usb_middle {

    requires javax.usb.API; //    requires usb.api; (maybe it should be?)
    requires usb4java.IMPL;
    requires org.apache.commons.lang3;
    requires usb_db;
//    requires org.usb4java.javax;

    exports com.earthtoernie.usb;
}
