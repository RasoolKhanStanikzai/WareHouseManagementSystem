/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Purchase.ViewPurchase;

import DataModels.PurchaseModel;
import DatabaseOperations.CRUDOperations;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ControlHelper.ControlHelper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Pair;
/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class ViewPurchaseFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private TableView<PurchaseModel> tblViewPurchase;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colSupplier;
    @FXML
    private TableColumn<?, ?> colProduct;
    @FXML
    private TableColumn<?, ?> colQuantity;
    @FXML
    private TableColumn<?, ?> colPPQuantity;
    @FXML
    private TableColumn<?, ?> colTotalPrice;
    @FXML
    private TableColumn<?, ?> colCurrency;
    @FXML
    private TableColumn<?, ?> colDate;
    @FXML
    private TextField txtSearch;
    @FXML
    private JFXButton btnExportToExcel;

    private void loadPurcahse(){
        String query="Select pur.PurchaseID,S.Name as SupplierName,P.Name as ProductName,pur.Quantity,pur.PricePerQuantity,(pur.Quantity*pur.PricePerQuantity) as TotalPrice,cu.Name as CurrencyName,pur.CreatedAt "
                + "from purchase pur JOIN supplier s ON pur.SupplierID=s.SupplierID JOIN product p ON pur.ProductID=p.ProductID JOIN currency cu ON pur.CurrencyID=cu.CurrencyID where pur.DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        ObservableList<PurchaseModel> purchaseList=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            java.sql.Timestamp st=(java.sql.Timestamp) row.get("CreatedAt");
            LocalDateTime createdAt=st.toLocalDateTime();
            purchaseList.add(new PurchaseModel(Integer.parseInt(row.get("PurchaseID").toString()),
            row.get("SupplierName").toString(),
            row.get("ProductName").toString(),
            Integer.parseInt(row.get("Quantity").toString()),
            Integer.parseInt(row.get("PricePerQuantity").toString()),
            Integer.parseInt(row.get("TotalPrice").toString()),
            row.get("CurrencyName").toString(),
            createdAt));
        }
        tblViewPurchase.setItems(purchaseList);
        ControlHelper.setColumnsFactory(new Pair<>(colID,"purchaseId"),
                new Pair<>(colSupplier,"supplierName"),
                new Pair<>(colProduct,"productName"),
                new Pair<>(colQuantity,"quantity"),
                new Pair<>(colPPQuantity,"price"),
                new Pair<>(colTotalPrice,"totalPrice"),
                new Pair<>(colCurrency,"currencyName"),
                new Pair<>(colDate,"createdAt"));
        ControlHelper.enableUniversalSearch(txtSearch, tblViewPurchase, purchaseList);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPurcahse();
        btnExportToExcel.setOnAction(event->{
            ControlHelper.exportToExcel(tblViewPurchase, (Stage)tblViewPurchase.getScene().getWindow());
        });
    }    
    
}
