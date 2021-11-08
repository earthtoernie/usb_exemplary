package org.gradle.sample.app;

import com.earthtoernie.usb.UsbUtils;

public class MainUsbPrettyList {

    public static void main(String[] args) throws Exception {
        UsbUtils usbUtils = new UsbUtils();
        usbUtils.checkUsb4java();

//        System.out.println("ARGS:");
//        System.out.println(args);
//        System.out.println(args[0]);
//        System.out.println(args[1]);
//        System.out.println(args[2]);
//
//        Options options = new Options();
//        options.addOption("json", true, "data to parse");
//        options.addOption("debug", false, "prints module infos");
//        CommandLineParser parser = new DefaultParser();
//        CommandLine cmd = parser.parse(options, args);
//
//        System.out.println("OPTIONS:");
//        System.out.println(options);
//
//        if (cmd.hasOption("debug")) {
//            printModuleDebug(Main.class);
//            printModuleDebug(Gson.class);
//            printModuleDebug(StringUtils.class);
//            printModuleDebug(CommandLine.class);
//            printModuleDebug(BeanUtils.class);
//        }
//
//        String json = cmd.getOptionValue("json");
//        Message message = new Gson().fromJson(json == null ? "{}" : json, Message.class);
//
//        Object copy = BeanUtils.cloneBean(message);
//        System.out.println();
//        System.out.println("Original: " + copy.toString());
//        System.out.println("Copy:     " + copy.toString());

    }

    private static void printModuleDebug(Class<?> clazz) {
        System.out.println(clazz.getModule().getName() + " - " + clazz.getModule().getDescriptor().version().get());
    }

}
