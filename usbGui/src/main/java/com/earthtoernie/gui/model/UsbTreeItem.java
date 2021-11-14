package com.earthtoernie.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import javax.usb.UsbDevice;

/**
 * Holds all the info about the supported USB device that is being inspected
 */
public class UsbTreeItem<String>  extends TreeItem<String> {

    private ObservableList<? extends UsbDevice> usbDevices;

    public UsbTreeItem(String name) {
        super(name);
        this.usbDevices = FXCollections.observableArrayList();
    }

    public ObservableList<? extends UsbDevice> getUsbDevices() {
        return this.usbDevices;
    }

    public void addUsbDevice(UsbDevice usbDevice) {
        
    }
}
