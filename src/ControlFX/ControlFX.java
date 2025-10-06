/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ControlFX;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author Rasookhan
 */
public class ControlFX {
    public static void showSuccessNotifiction(String message){
        Notifications.create()
                .title("Success")
                .text(message)
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(3))
                .showInformation();
    }
    public static void showErrorNotification(String message){
        Notifications.create()
                .title("Error")
                .text(message)
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(3))
                .showError();
    }
}
