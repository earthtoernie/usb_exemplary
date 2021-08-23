module usb_middle {

    requires javax.usb.API; //    requires usb.api; (maybe it should be?)
    requires usb4java.IMPL;
//    requires org.usb4java.javax;

    exports com.earthtoernie.usb;
}
