package com.earthtoernie.usb.exceptions;

// For any usb device that is not found
public class UsbDeviceNotFoundException extends Exception {
    public UsbDeviceNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
