/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Inventory;

import DataModels.StockModel;
import ControlHelper.ControlHelper;
import DataModels.DashboardModel;
import DatabaseOperations.CRUDOperations;
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
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class InventoryFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private TableView<StockModel> tblViewStock;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colProductName;
    @FXML
    private TableColumn<?, ?> colQuantity;
    @FXML
    private TableColumn<?, ?> colDate;
    @FXML
    private TextField txtSearch;

    
    private void loadStock(){
        String query="select s.StockID,p.Name as ProductName,s.Quantity,s.LastUpdated from Stock s JOIN Product p ON s.ProductID=p.ProductID";
        List<Map<String,Object>> data=operation.retrieve(query);
        ObservableList<StockModel> stock=FXCollections.observableArrayList();
        int totalStock=0;
        for(Map<String,Object> row:data){
            int qty=Integer.parseInt(row.get("Quantity").toString());
            totalStock+=qty;
            stock.add(new StockModel(Integer.parseInt(row.get("StockID").toString()),
            row.get("ProductName").toString(),
            Integer.parseInt(row.get("Quantity").toString()),
            (LocalDateTime)row.get("LastUpdated")));
            
        }
        tblViewStock.setItems(stock);
        ControlHelper.setColumnsFactory(new Pair<>(colID,"id"),
                new Pair<>(colProductName,"name"),
                new Pair<>(colQuantity,"quantity"),
                new Pair<>(colDate,"date"));
        DashboardModel.getInstance().setStockCount(totalStock);
        ControlHelper.enableUniversalSearch(txtSearch, tblViewStock, stock);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadStock();
       
    }    
    
}
