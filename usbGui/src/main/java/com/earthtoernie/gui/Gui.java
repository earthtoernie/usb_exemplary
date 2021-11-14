package com.earthtoernie.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Gui extends Application{

//    public static void main(String[] args){
//        launch(args);
//    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Go!Temp");

        // Buttons
        Button exitButton = new Button("exit");
        Button usbButton = new Button("get usb");
        Button moduleButton = new Button("get modules");

        // frame (Box) for buttons
        HBox buttonBox = new HBox();

        // Configure box and add buttons
        buttonBox.setPadding(new Insets(15, 12, 15, 12));
        buttonBox.setSpacing(10);
        buttonBox.setStyle("-fx-background-color: #336699;");
        buttonBox.getChildren().addAll(usbButton, moduleButton, exitButton);

        usbButton.setOnAction((event) -> {
            System.out.println(Runtime.version());

//            Usb.listDevices();
        });

        StackPane layout = new StackPane();
        layout.getChildren().add(buttonBox);

        Scene scene = new Scene(layout, 300,250);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set actions for buttons



    }
}
