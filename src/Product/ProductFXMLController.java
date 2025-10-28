/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Product;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import DatabaseOperations.*;
import ControlHelper.ControlHelper;
import DataModels.CategoryModel;
import DataModels.CurrencyModel;
import com.jfoenix.controls.JFXButton;
import javafx.scene.control.ComboBox;
/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class ProductFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private Label lblNotification;
    @FXML
    private TextField txtProductName;
    @FXML
    private ComboBox<CategoryModel> comboCategory;
    @FXML
    private TextField txtUnitPrice;
    @FXML
    private TextField txtCostPrice;
    @FXML
    private TextField txtSalePrice;
    @FXML
    private ComboBox<CurrencyModel> comboCurrency;
    @FXML
    private JFXButton btnClear;

   
    
    private void saveRecord(){
        String query="insert into Product(Name,CategoryID,Unit,CostPrice,SalePrice,CurrencyID)values(?,?,?,?,?,?)";
        boolean insert=operation.insert(query, txtProductName.getText(),comboCategory.getValue(),txtUnitPrice.getText(),
                txtCostPrice.getText(),txtSalePrice.getText(),comboCurrency.getValue());
        if(insert){
            lblNotification.getStyleClass().add("notification-success");
            ControlHelper.showNotification(lblNotification, "Record inserted");
            ControlHelper.clearFaileds(txtProductName,comboCategory,txtUnitPrice,txtCostPrice,
                    txtSalePrice,comboCurrency);
        }else{
            lblNotification.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblNotification, "Record Failed");
        }
    }
    
    // loading combo box category
    private void loadCategoryComboBox(){
       
        ControlHelper.fillComboBox(comboCategory, "Select CategoryID,CategoryName,description from Category where DeletedAT IS NULL",
                row-> new CategoryModel((int) row.get("CategoryID"),(String) row.get("CategoryName"),(String)row.get("description"))); 
         ControlHelper.makeComboBoxSearchable(comboCategory);
    }
    private void loadCurrency(){
        String query="select CurrencyID,Code,Name,Symbol,Country,DecimalPlaces,Status,Rate from Currency where DeletedAt IS NULL";
        ControlHelper.fillComboBox(comboCurrency, query, row->new CurrencyModel((int)row.get("CurrencyID"),(String)row.get("Code"),(String)row.get("Name"),
                (String)row.get("Symbol"),(String)row.get("Country"),(int)row.get("DecimalPlaces"),
                (String)row.get("Status"),(double)row.get("Rate")));
        ControlHelper.makeComboBoxSearchable(comboCurrency);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnClear.setOnAction(event->{
            ControlHelper.clearFaileds(txtProductName,comboCategory,txtUnitPrice,txtSalePrice,comboCurrency);  
        });
        loadCategoryComboBox();
        loadCurrency();
    }    
}
