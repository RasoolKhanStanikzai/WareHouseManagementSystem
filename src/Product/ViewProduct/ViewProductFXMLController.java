/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Product.ViewProduct;

import ControlHelper.ControlHelper;
import DataModels.ProductModel;
import DatabaseOperations.CRUDOperations;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class ViewProductFXMLController implements Initializable {

    CRUDOperations operation=new CRUDOperations();
    @FXML
    private TableView<ProductModel> tblViewProduct;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colName;
    @FXML
    private TableColumn<?, ?> colCategory;
    @FXML
    private TableColumn<?, ?> colUnit;
    @FXML
    private TableColumn<?, ?> colCostPrice;
    @FXML
    private TableColumn<?, ?> colCurrency;
    @FXML
    private TableColumn<?, ?> colDate;
    @FXML
    private TextField txtSearch;
    @FXML
    private JFXButton btnExportExcel;

   
    private void loadProductData(){
           String query="select p.ProductID,p.Name,c.CategoryName as CategoryName,p.Unit,p.CostPrice,cu.Name as CurrencyName,p.CreatedAt from product p  JOIN category c ON p.CategoryID=c.CategoryID JOIN currency cu "
                + "ON p.CurrencyID=cu.CurrencyID where p.DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        ObservableList<ProductModel>product=FXCollections.observableArrayList();
        
        for(Map<String,Object> row:data){
            java.sql.Timestamp st=(java.sql.Timestamp)row.get("CreatedAt");
            LocalDateTime createdAt=st.toLocalDateTime();
            product.add(new ProductModel(Integer.parseInt(row.get("ProductID").toString()),
            row.get("Name").toString(),
            row.get("CategoryName").toString(),
            row.get("Unit").toString(),
            Integer.parseInt(row.get("CostPrice").toString()),
            row.get("CurrencyName").toString(),
            createdAt));
        }
        tblViewProduct.setItems(product);
        ControlHelper.setColumnsFactory(new Pair<>(colID,"id"),
                new Pair<>(colName,"name"),
                new Pair<>(colCategory,"category"),
                new Pair<>(colUnit,"unit"),
                new Pair<>(colCostPrice,"costPrice"),
                new Pair<>(colCurrency,"currency"),
                new Pair<>(colDate,"createdAt"));
        ControlHelper.enableUniversalSearch(txtSearch, tblViewProduct, product);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       loadProductData();
       btnExportExcel.setOnAction(event->{
           ControlHelper.exportToExcel(tblViewProduct, (Stage) tblViewProduct.getScene().getWindow());
       });
    }    
    
}
