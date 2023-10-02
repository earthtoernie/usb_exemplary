package com.earthtoernie.usb;

import com.earthtoernie.usb.exceptions.UsbDeviceNotFoundException;
import com.earthtoernie.usb.model.SensorMeasurement;

import javax.usb.*;
//import javax.usb.util.UsbUtil;

import org.apache.commons.lang3.tuple.ImmutablePair;


public class SensorGoTemp {

    public static short toShort(byte msb, byte lsb) { return (short)((0xff00 & (short)(msb << 8)) | (0x00ff & (short)lsb)); }


    // -------------- INNER CLASSES ----------------------------------------

    private class GoTempUsbResource implements AutoCloseable {
        private ImmutablePair<UsbPipe, UsbIrp> pipes;
        private UsbPipe usbPipe;
        private UsbIrp usbIrp;
        private UsbInterface usbInterface;
        // private UsbDevice getGoTempDevice;

        GoTempUsbResource(){
            UsbDevice goTemp = null;//TODO goTemp can be null
            try {
                goTemp = UsbUtils.getGoTemp();
            } catch (UsbDeviceNotFoundException e) {
                throw new RuntimeException(e);
            }
            UsbConfiguration usbConfiguration = goTemp.getActiveUsbConfiguration();
            this.usbInterface = usbConfiguration.getUsbInterface((byte)0);
            UsbEndpoint usbEndpoint = usbInterface.getUsbEndpoint((byte)0x81 );
            this.usbPipe = usbEndpoint.getUsbPipe();
            this.usbIrp = usbPipe.createUsbIrp();
            this.pipes = ImmutablePair.of(usbPipe, usbIrp);
            try {
                usbInterface.claim(new UsbInterfacePolicy() {
                    @Override
                    public boolean forceClaim(UsbInterface usbInterface) {
                        return true;
                    }});
                usbPipe.open();
            } catch (UsbException e) {
                e.printStackTrace();
            }
        }

        /** returns opened pipes */
        ImmutablePair<UsbPipe, UsbIrp> getPipes (){
            return pipes;
        }


        @Override
        public void close()  {
            usbPipe.abortAllSubmissions();
            try {
                usbPipe.close();
                usbInterface.release();
            } catch (UsbException e) {
                e.printStackTrace();
            }

        }
    }

    // -------------- PRIVATE PROPERTIES ----------------------------------------
    private GoTempUsbResource goTempUsbResource;
    private ImmutablePair<UsbPipe, UsbIrp> pipes;

    // -------------- CONSTRUCTORS ----------------------------------------

    public SensorGoTemp () {
        this.goTempUsbResource = new GoTempUsbResource();
        this.pipes = this.goTempUsbResource.getPipes();

    }

    public void closeSensorGoTemp(){
        this.goTempUsbResource.close();
    }

    public double getNextMeasurement(){
        byte[] dataBuffer = new byte[8];
        this.pipes.getRight().setData(dataBuffer); //UsbIrp
        try {
            this.pipes.getLeft().syncSubmit(this.pipes.getRight());
        } catch (UsbException e) {
            e.printStackTrace();
        }
        this.pipes.getRight().setComplete(false);
        return SensorGoTemp.toShort(dataBuffer[3], dataBuffer[2]) / 128;
    }

//    /** Claim device and prep for measuring*/
//    private void setUpGoTemp(){
//        UsbDevice goTemp = UsbUtils.getGoTemp(); //TODO add doc for this method
//        UsbConfiguration usbConfiguration = goTemp.getActiveUsbConfiguration();
//
//        UsbInterface usbInterface = usbConfiguration.getUsbInterface((byte)0);
//
//
//        try {
//            usbInterface.claim(new UsbInterfacePolicy() {
//                @Override
//                public boolean forceClaim(UsbInterface usbInterface) {
//                    return true;
//                }
//            });
//        } catch (UsbException e) {
//            e.printStackTrace();
//        }
//
//        UsbEndpoint usbEndpoint = usbInterface.getUsbEndpoint((byte)0x81 );
//        UsbPipe usbPipe = usbEndpoint.getUsbPipe();
//        UsbIrp usbIrp = usbPipe.createUsbIrp();
//
//        return;
//    }
}
