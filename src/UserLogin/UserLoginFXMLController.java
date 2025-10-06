/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package UserLogin;

import DataValidations.TextFieldValidations;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ControlHelper.ControlHelper;
import DataModels.AuthService;
import DataModels.Session;
import DataModels.UserModel;
import java.util.List;
import java.util.Map;
import DatabaseOperations.CRUDOperations;
import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class UserLoginFXMLController implements Initializable {
    
    CRUDOperations operation=new CRUDOperations();
    

    @FXML
    private Label lblMessage;
    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField txtUserPassword;
    @FXML
    private JFXCheckBox checkBoxShowPassword;
    @FXML
    private JFXButton btnLogin;

    
    @FXML
    private void handleLogin(){
        try{
            if(!TextFieldValidations.isTextFieldNotEmpty(txtUserName,txtUserPassword)){
            //    lblMessage.getStyleClass().add("notification-warning");
                ControlHelper.showNotification(lblMessage, "username and password are required");
                return;
            }
            String userName=txtUserName.getText().trim();
            String userPassword=txtUserPassword.getText().trim();
            
            String query="select UserID,UserName,PasswordHash,Salt,Role,FullName,Email,Photo,CreatedAt,CreatedBy,UpdatedAt,UpdatedBy,DeletedAt,DeletedBy "
                    + "from User where UserName=? AND DeletedAt IS NULL";
            List<Map<String, Object>> result=operation.retrieve(query, userName);
            
            if(result.isEmpty()){
               // lblMessage.getStyleClass().add("notification-error");
                ControlHelper.showNotification(lblMessage, "Invalid User Name or Password");
                return;
            }
            
            Map<String, Object> userData=result.get(0);
            
            String storedHash=(String) userData.get("PasswordHash");
            String storedSalt=(String) userData.get("Salt");
            boolean isValidPassword=AuthService.verifyPassword(userPassword, storedHash, storedSalt);
            if(!isValidPassword){
              //  lblMessage.getStyleClass().add("notification-error");
                ControlHelper.showNotification(lblMessage, "Invalid User Name or Password");
                return;
            }
            UserModel user=new UserModel(
            (Integer) userData.get("UserID"),
                    (String) userData.get("UserName"),
                    (String) userData.get("PasswordHash"),
                    (String) userData.get("Salt"),
                    (String) userData.get("Role"),
                    (String) userData.get("FullName"),
                    (byte[]) userData.get("Photo"),
                    (java.sql.Timestamp) userData.get("CreatedAt"),
                    (String) userData.get("CreatedBy"),
                    (java.sql.Timestamp) userData.get("UpdatedAt"),
                    (String) userData.get("UpdatedBy"),
                    (java.sql.Timestamp) userData.get("DeletedAt"),
                    (String) userData.get("DeletedBy")
            );
            Session.setCurrentUser(user);
            ControlHelper.showNotification(lblMessage, "Login Successful!");
           
             redirectToMainApp();
            
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException e){
          lblMessage.getStyleClass().add("notification-error");
          ControlHelper.showNotification(lblMessage, "Authentication Error: "+e.getMessage());
          e.printStackTrace();
        }
        catch(Exception ex){
          //  lblMessage.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblMessage, "Login Failed: "+ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void redirectToMainApp(){
        lblMessage.getStyleClass().clear();
        //lblMessage.getStyleClass().add("notification-success");
        ControlHelper.showNotification(lblMessage, "Authenticating System...");
        btnLogin.setDisable(true);
        PauseTransition pT=new PauseTransition(Duration.seconds(0.5));
        pT.setOnFinished(event->{
            try{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/warehousemanagementsystem/FXMLDocument.fxml"));
            Parent root=loader.load();
            Stage mainStage=new Stage();
            Scene myScene=new Scene(root);
            mainStage.setTitle("Warehouse Management System");
            Session.getCurrentUser().getUserFullName();
            mainStage.setMaximized(true);
            mainStage.setScene(myScene);
            Stage currentStage=(Stage)  btnLogin.getScene().getWindow();
            currentStage.close();
            mainStage.show();
            }
            catch (IOException ex) {
            ex.printStackTrace();
        //    lblMessage.getStyleClass().clear();
         //   lblMessage.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblMessage, "Failed to load main application.");
        }
            
        });
        pT.play();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }   
    
    
}
