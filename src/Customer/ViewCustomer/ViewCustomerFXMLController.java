/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Customer.ViewCustomer;

import DataModels.CustomerModel;
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
public class ViewCustomerFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private TableView<CustomerModel> tblViewCustomer;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colName;
    @FXML
    private TableColumn<?, ?> colPhone;
    @FXML
    private TableColumn<?, ?> colEmail;
    @FXML
    private TableColumn<?, ?> colAddress;
    @FXML
    private TableColumn<?, ?> colDate;
    @FXML
    private TextField txtSearch;
    @FXML
    private JFXButton btnExportToExcel;

    
    private void loadCustomer(){
        String query="select * from customer where DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        
        // Converting List<Map> to observablelist<Customer>
        ObservableList<CustomerModel> customerList=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            java.sql.Timestamp st=(java.sql.Timestamp)row.get("CreatedAt");
            LocalDateTime createdAt=st.toLocalDateTime();
            
            customerList.add(new CustomerModel(Integer.parseInt( row.get("CustomerID").toString()),
            row.get("Name").toString(),
            row.get("Phone").toString(),
            row.get("Email").toString(),
            row.get("Address").toString(),
            createdAt));
            tblViewCustomer.setItems(customerList);
            ControlHelper.setColumnsFactory(new Pair<>(colID,"customerID"),
                    new Pair<>(colName,"customerName"),
                    new Pair<>(colPhone,"customerPhone"),
                    new Pair<>(colEmail,"customerEmail"),
                    new Pair<>(colAddress,"customerAddress"),
                    new Pair<>(colDate,"createdAt"));
            ControlHelper.enableUniversalSearch(txtSearch, tblViewCustomer, customerList);
        }
                
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCustomer();
        btnExportToExcel.setOnAction(event->{
            ControlHelper.exportToExcel(tblViewCustomer, (Stage)tblViewCustomer.getScene().getWindow());
        });
    }    
    
}
