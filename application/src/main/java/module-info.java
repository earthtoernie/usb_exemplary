module org.gradle.sample.app {
//    exports org.gradle.sample.app; // maybe add this back in!
//    opens org.gradle.sample.app.data; // allow Gson to access via reflection

//    requires javax.usb.API;
    requires usb_middle;
}
