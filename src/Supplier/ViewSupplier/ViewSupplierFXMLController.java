/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Supplier.ViewSupplier;

import DataModels.SupplierModel;
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
import ControlHelper.ControlHelper;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class ViewSupplierFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private TableView<SupplierModel> tblViewSupplier;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colName;
    @FXML
    private TableColumn<?, ?> colPhone;
    @FXML
    private TableColumn<?, ?> colAddress;
    @FXML
    private TableColumn<?, ?> colDate;
    @FXML
    private TextField txtSearch;
    @FXML
    private JFXButton btnExportToExcel;
    @FXML
    private TableColumn<?, ?> colEmail;

    private void loadSupplier(){
        String query="select * from supplier where DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        ObservableList<SupplierModel> supplierList=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            java.sql.Timestamp st=(java.sql.Timestamp) row.get("CreatedAt");
            LocalDateTime createdAt=st.toLocalDateTime();
            supplierList.add(new SupplierModel(Integer.parseInt(row.get("SupplierID").toString()),
            row.get("Name").toString(),
            row.get("Phone").toString(),
            row.get("Email").toString(),
            row.get("Address").toString(),
            createdAt)); 
        }
        ControlHelper.setColumnsFactory(new Pair<>(colID,"id"),
                new Pair<>(colName,"name"),
                new Pair<>(colPhone,"phone"),
                new Pair<>(colEmail,"email"),
                new Pair<>(colAddress,"address"),
                new Pair<>(colDate,"createdAt"));
        ControlHelper.enableUniversalSearch(txtSearch, tblViewSupplier, supplierList);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSupplier();
        btnExportToExcel.setOnAction(event->{
            ControlHelper.exportToExcel(tblViewSupplier, (Stage)tblViewSupplier.getScene().getWindow());
        });
    }    
    
}
