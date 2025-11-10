/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Purchase;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ControlHelper.ControlHelper;
import DataModels.CurrencyModel;
import DataModels.ProductModel;
import DataModels.PurchaseModel;
import DataModels.SupplierModel;
import DatabaseOperations.CRUDOperations;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class PurchaseFXMLController implements Initializable {
    private int selectedProductID;
    private int selectedCurrencyID;
    private int selectedSupplierID;
    
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private ComboBox<SupplierModel> comboSupplier;
    @FXML
    private ComboBox<ProductModel> comboProduct;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtPrice;
    @FXML
    private ComboBox<CurrencyModel> comboCurrency;
    @FXML
    private JFXButton btnSaveAndUpdate;
    @FXML
    private TableView<PurchaseModel> tblViewPurchase;
    @FXML
    private TableColumn<?, ?> colPurchaseID;
    @FXML
    private TableColumn<?, ?> colSupplier;
    @FXML
    private TableColumn<?, ?> colProduct;
    @FXML
    private TableColumn<?, ?> colQuantity;
    @FXML
    private TableColumn<?, ?> colPrice;
    @FXML
    private TableColumn<?, ?> colCurrency;
    @FXML
    private TableColumn<PurchaseModel, Void> colAction;
    @FXML
    private Label lblNotification;

   
    @FXML
    private void saveAndUpdate(){
        if(btnSaveAndUpdate.getText().equals("Save")){
            String query="insert into Purchase(SupplierID,ProductID,Quantity,"
                    + "PricePerQuantity,CurrencyID) values(?,?,?,?,?)";
           int insertedPurchasedId=operation.getInsertAndUpdateID(query, selectedSupplierID,
                    selectedProductID,txtQuantity.getText(),txtPrice.getText(),
           selectedCurrencyID);
            
            if(insertedPurchasedId>0){
                String stockQry="Update Stock set Quantity=Quantity+?,LastUpdated=NOW() Where ProductID=?";
                operation.update(stockQry, txtQuantity.getText(),selectedProductID);
                ControlHelper.showNotification(lblNotification, "Record Purchased");
                loadPuchaseData();
            } else{
                ControlHelper.showNotification(lblNotification, "Purchase Failed");
            }
            
        }
        
    }
    private void loadProductComboBox(){
        String query="select ProductID,Name from Product where DeletedAt IS NULL";
       ControlHelper.fillComboBox(comboProduct, query, row->new ProductModel((int) row.get("ProductID"),
       (String)row.get("Name")),product->product.getName(),product->selectedProductID=product.getId());
       ControlHelper.makeComboBoxSearchable(comboProduct);
    }
     private void loadCurrency(){
        String query="select CurrencyID,Code,Name,Symbol,Country,DecimalPlaces,Status,Rate from Currency where DeletedAt IS NULL";
        ControlHelper.fillComboBox(comboCurrency, query, row->new CurrencyModel((int)row.get("CurrencyID"),(String)row.get("Code"),(String)row.get("Name"),
                (String)row.get("Symbol"),(String)row.get("Country"),(int)row.get("DecimalPlaces"),
                (String)row.get("Status"),(double)row.get("Rate")),
                currency->currency.getName(),currency->selectedCurrencyID=currency.getId());
        ControlHelper.makeComboBoxSearchable(comboCurrency);
    }
    private void loadSupplier(){
        String query="select SupplierID,Name from supplier where DeletedAt IS NULL";
        ControlHelper.fillComboBox(comboSupplier, query, row->new SupplierModel((int)row.get("SupplierID"),
        (String)row.get("Name")),supplier->supplier.getName(),supplier->selectedSupplierID=supplier.getId());
        ControlHelper.makeComboBoxSearchable(comboSupplier);
    }
    private void loadPuchaseData(){
        String query="Select pur.PurchaseID,S.Name as SupplierName,P.Name as ProductName,pur.Quantity,pur.PricePerQuantity,cu.Name as CurrencyName "
                + "from purchase pur JOIN supplier s ON pur.SupplierID=s.SupplierID JOIN product p ON pur.ProductID=p.ProductID JOIN currency cu ON pur.CurrencyID=cu.CurrencyID where pur.DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        ObservableList<PurchaseModel> purchase=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            purchase.add(new PurchaseModel(Integer.parseInt(row.get("PurchaseID").toString()),
            row.get("SupplierName").toString(),
            row.get("ProductName").toString(),
            Integer.parseInt(row.get("Quantity").toString()),
            Integer.parseInt(row.get("PricePerQuantity").toString()),
            row.get("CurrencyName").toString()));
        }
        tblViewPurchase.setItems(purchase);
        ControlHelper.setColumnsFactory(new Pair<>(colPurchaseID,"purchaseId"),
                new Pair<>(colSupplier,"supplierName"),
                new Pair<>(colProduct,"productName"),
                new Pair<>(colQuantity,"quantity"),
                new Pair<>(colPrice,"price"),
                new Pair<>(colCurrency,"currencyName"));
        
    }
    private void loadActionButtons(){
        colAction.setCellFactory(col->new TableCell<PurchaseModel,Void>(){
            JFXButton btnUpdate=new JFXButton("Update");
            JFXButton btnDelete=new JFXButton("Delete");
                {
                    btnUpdate.setButtonType(JFXButton.ButtonType.RAISED);
                    btnDelete.setButtonType(JFXButton.ButtonType.RAISED);
                    btnUpdate.getStyleClass().add("btn-update");
                    btnDelete.getStyleClass().add("btn-delete");
                    
                    
                }
                protected void updateItem(Void item,boolean empty){
                    super.updateItem(item, empty);
                    if(empty){
                        setGraphic(null);
                    } else{
                        HBox hbox=new HBox(10,btnUpdate,btnDelete);
                        hbox.setAlignment(Pos.CENTER);
                        setGraphic(hbox);
                    }
                }
        });
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       loadProductComboBox();
       loadCurrency();
       loadSupplier();
       loadPuchaseData();
       loadActionButtons();
    }    
    
}
