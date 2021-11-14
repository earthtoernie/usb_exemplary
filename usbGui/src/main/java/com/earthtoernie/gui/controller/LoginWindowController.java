package com.earthtoernie.gui.controller;

import com.earthtoernie.gui.UsbManager;
import com.earthtoernie.gui.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class LoginWindowController extends BaseController implements Initializable {

    @FXML
    Pane aPane;

    @FXML
    private Label errorLabel;

    public LoginWindowController(UsbManager usbManager, ViewFactory viewFactory, String fxmlName) {
        super(usbManager, viewFactory, fxmlName);
    }

    @FXML
    void loginButtonAction(ActionEvent event) {
        getViewFactory().showMainWindow();
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        getViewFactory().closeStage(stage);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        aPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                loginButtonAction(null);
//            }
//        });
    }
}
