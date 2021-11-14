package com.earthtoernie.gui.controller;

import com.earthtoernie.gui.UsbManager;
import com.earthtoernie.gui.view.ViewFactory;

public class BaseController {

    private UsbManager usbManager;
    private ViewFactory viewFactory;
    private String fxmlName;


    public BaseController (UsbManager usbManager, ViewFactory viewFactory, String fxmlName) {
        this.usbManager = usbManager;
        this.fxmlName = fxmlName;
        this.viewFactory = viewFactory;

    }

    protected UsbManager getUsbManager () {
        return usbManager;
    }

    protected ViewFactory getViewFactory () {
        return viewFactory;
    }

    public String getFxmlName() {
        return fxmlName;
    }
}
