package com.earthtoernie.gui;

import com.earthtoernie.gui.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    static {
        // https://stackoverflow.com/questions/29893594/javafx-application-throws-nullpointerexception-at-startup
        // https://stackoverflow.com/questions/44684605/javafx-applications-throw-nullpointerexceptions-but-run-anyway
//        System.setProperty("javafx.sg.warn", "true");
    }

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ViewFactory viewFactory = new ViewFactory(new UsbManager(), primaryStage);
        viewFactory.showLoginWindow();
    }
}