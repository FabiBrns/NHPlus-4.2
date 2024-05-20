package de.hitec.nhplus.controller;

import de.hitec.nhplus.Main;
import de.hitec.nhplus.datastorage.DaoFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AuthenticationController {

    @FXML
    public TextField textFieldUsername;
    @FXML
    public PasswordField textFieldPassword;
    public Button buttonLogin;

    @FXML
    public void handleAuthentication(ActionEvent event) throws SQLException {
        var users = DaoFactory.getDaoFactory().createUserDAO().readAll();
        for (var user : users) {
            if (user.getUsername().equals(textFieldUsername.getText()) && user.getPassword().equals(textFieldPassword.getText())) {
                Main.mainWindow();
                System.out.println("Erfolgreicher Login!");
                return;
            } else {
                System.out.println("Falsche Login-Daten!");
            }
        }
    }
}