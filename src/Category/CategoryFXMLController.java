/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Category;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ControlHelper.ControlHelper;
import DataValidations.TextFieldValidations;
import DataModels.*;
import DatabaseOperations.CRUDOperations;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class CategoryFXMLController implements Initializable {
    UserModel currentUser=Session.getCurrentUser();
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private TextField txtCategoryName;
    @FXML
    private TextArea txtCategoryDescriptions;
    @FXML
    private Label lblNotification;

    
    @FXML
    private void insert(){
        boolean isValid=TextFieldValidations.isTextFieldNotEmpty(txtCategoryName,txtCategoryDescriptions);
        if(!isValid){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Required");
            return;
        }
        String query="insert into Category(CategoryName,Description,CreatedBy)values(?,?,?)";
        boolean insert=operation.insert(query, txtCategoryName.getText(),txtCategoryDescriptions.getText(),currentUser.getUserID());
        if(insert){
            lblNotification.getStyleClass().add("notification-success");
            ControlHelper.showNotification(lblNotification, "Record Inserted");
        }
        else{
            lblNotification.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblNotification, "Record Failed");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
