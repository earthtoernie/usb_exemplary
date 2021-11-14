package com.earthtoernie.gui.controller;

import com.earthtoernie.usb.UsbUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;


/**
 * this class has reference to the usb stuff!
 */
public class FetchUsbService extends Service<Void> {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                fetchUsb();
                return null;
            }
        };
    }

    private void fetchGoTemp(){
        var devices = UsbUtils.getUsbDevicesAsStringList();
        int i = 42;

    }
}
