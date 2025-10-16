/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Currency;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import DataValidations.TextFieldValidations;
import DatabaseOperations.*;
import javafx.scene.control.Label;
import ControlHelper.ControlHelper;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class CurrnencyFXMLController implements Initializable {
    CRUDOperations operations=new CRUDOperations();
    
    @FXML
    private TextField txtCurrencyCode;
    @FXML
    private TextField txtCurrencyName;
    @FXML
    private TextField txtCurrencySymbol;
    @FXML
    private TextField txtCurrencyCountry;
    @FXML
    private TextField txtDecimalParts;
    @FXML
    private ComboBox<String> comboStatus;
    @FXML
    private TextField txtExchangeRate;
    
    ObservableList<String> statusList=FXCollections.observableArrayList("Active","Deactivate");
    @FXML
    private Label lblNotification;
    
    @FXML
    private void saveCurrency(){
        String query="insert into Currency (Code,Name,Symbol,Country,DecimalPlaces,Status,Rate) values(?,?,?,?,?,?,?)";
        boolean inserted=operations.insert(query,txtCurrencyCode.getText(),
                txtCurrencyName.getText(),txtCurrencySymbol.getText(),
                txtCurrencyCountry.getText(),txtDecimalParts.getText(),
                comboStatus.getValue(),txtExchangeRate.getText());
        if(inserted){
           lblNotification.getStyleClass().add("notification-success");
           ControlHelper.showNotification(lblNotification, "Recrod Inserted");
           ControlHelper.clearFaileds(txtCurrencyCode,txtCurrencyName,
                   txtCurrencySymbol,txtCurrencyCountry,txtDecimalParts,
                   comboStatus,txtExchangeRate);
           
        }
   }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboStatus.getItems().addAll(statusList);
    }    
    
}
