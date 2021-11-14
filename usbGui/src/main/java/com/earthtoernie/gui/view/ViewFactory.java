package com.earthtoernie.gui.view;

import com.earthtoernie.gui.UsbManager;
import com.earthtoernie.gui.controller.BaseController;
import com.earthtoernie.gui.controller.LoginWindowController;
import com.earthtoernie.gui.controller.MainWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ViewFactory {

    private UsbManager usbManager;
    private ArrayList<Stage> activeStages = new ArrayList<>();
    private Stage primaryStage;


    /**
     * @param usbManager
     * @param primaryStage passed to have a reference to the primary stage, as a convenience
     * */
    public ViewFactory(UsbManager usbManager, Stage primaryStage) {
        this.usbManager = usbManager;
        this.primaryStage = primaryStage;
    }

    public void showLoginWindow(){
        System.out.println("show login window called");

        BaseController controller = new LoginWindowController(usbManager, this, "LoginWindow.fxml" );
        initializeStage(controller, true);


//        primaryStage.setOnShowing(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                closeStage(primaryStage);
//            }
//        });


    }

    private void initializeStage(BaseController baseController, boolean closeImmediately){

        Parent parent;
        try {
//            URL url = Paths.get("./usbGui/src/main/resources/fxml/" + baseController.getFxmlName()).toUri().toURL();
            URL url = ViewFactory.class.getResource("/fxml/" + baseController.getFxmlName());
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            fxmlLoader.setController(baseController);
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        activeStages.add(stage);
        if (closeImmediately) {
            closeStage(stage);
            showMainWindow();
        }
    }

    public void showMainWindow() {
        BaseController controller = new MainWindowController(usbManager, this, "MainWindow.fxml" );
        initializeStage(controller, false);

    }

    public void closeStage(Stage stageToClose){
        stageToClose.close();
        activeStages.remove(stageToClose);
    }
}
