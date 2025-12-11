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
import DataModels.DashboardModel;
import DataModels.ProductModel;
import DataModels.PurchaseModel;
import DataModels.Session;
import DataModels.SupplierModel;
import DataModels.UserModel;
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
import DataValidations.TextFieldValidations;
import javafx.beans.binding.IntegerBinding;
/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class PurchaseFXMLController implements Initializable {
    private int selectedProductID=0;
    private int selectedCurrencyID=0;
    private int selectedSupplierID=0;
    
    CRUDOperations operation=new CRUDOperations();
    UserModel currentUser=Session.getCurrentUser();
    ObservableList<PurchaseModel> purchase=FXCollections.observableArrayList();
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
    private TableColumn<?, ?> colTotalPrice;
    @FXML
    private TextField txtSearchPurchase;
    @FXML
    private Label lblTotalQuantities;
    @FXML
    private Label lblGrandTotal;


   
    @FXML
    private void saveAndUpdate(){
        if(btnSaveAndUpdate.getText().equals("Save")){
            boolean isEmpty=TextFieldValidations.isTextFieldNotEmpty(comboSupplier,comboProduct,txtQuantity,
                    txtPrice,comboCurrency);
            if(!isEmpty){
                lblNotification.getStyleClass().add("notification-warnning");
                ControlHelper.showNotification(lblNotification, "Required");
                return ;
            }
            String query="insert into Purchase(SupplierID,ProductID,Quantity,"
                    + "PricePerQuantity,CurrencyID,CreatedBy) values(?,?,?,?,?,?)";
           int qty=Integer.parseInt(txtQuantity.getText());
           int price=Integer.parseInt(txtPrice.getText());
           int insertedPurchasedId=operation.getInsertAndUpdateID(query, selectedSupplierID,
                    selectedProductID,qty,price,
           selectedCurrencyID,currentUser.getUserID());  
           
           if(insertedPurchasedId<=0){
               lblNotification.getStyleClass().setAll("notification-warnning");
               ControlHelper.showNotification(lblNotification, "Purchase Failed");
               return ;
           }
           String stockQuery="insert into Stock(ProductID,Quantity,LastUpdated) values(?,?,NOW()) ON DUPLICATE KEY UPDATE Quantity=Quantity+VALUES(Quantity),LastUpdated=NOW()";
           boolean stockUpdated=operation.update(stockQuery, selectedProductID,qty);
           if(!stockUpdated){
               lblNotification.getStyleClass().setAll("Purchase saved, But stock not updated");
               return ;
           }
           // if success
           purchase.clear();
           loadPuchaseData();
           ControlHelper.clearFaileds(comboSupplier,comboProduct,txtQuantity,txtPrice,comboCurrency);   
           lblNotification.getStyleClass().add("notification-success");
           ControlHelper.showNotification(lblNotification, "Product purchaed Successfully.");
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
        ControlHelper.fillComboBox(comboSupplier, query,
                row->new SupplierModel((int)row.get("SupplierID"),
        (String)row.get("Name")),supplier->supplier.getName(),
        supplier->selectedSupplierID=supplier.getId());
        ControlHelper.makeComboBoxSearchable(comboSupplier);
    }
    private void loadPuchaseData(){
        String query="Select pur.PurchaseID,S.Name as SupplierName,P.Name as ProductName,pur.Quantity,pur.PricePerQuantity,(pur.Quantity*pur.PricePerQuantity) as TotalPrice,cu.Name as CurrencyName "
                + "from purchase pur JOIN supplier s ON pur.SupplierID=s.SupplierID JOIN product p ON pur.ProductID=p.ProductID JOIN currency cu ON pur.CurrencyID=cu.CurrencyID where pur.DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        int totalPurchases=0;
        for(Map<String,Object> row:data){
            int quantity=Integer.parseInt(row.get("Quantity").toString());
            int purchases=Integer.parseInt(row.get("PricePerQuantity").toString());
            totalPurchases+=quantity*purchases;
            purchase.add(new PurchaseModel(Integer.parseInt(row.get("PurchaseID").toString()),
            row.get("SupplierName").toString(),
            row.get("ProductName").toString(),
            Integer.parseInt(row.get("Quantity").toString()),
            Integer.parseInt(row.get("PricePerQuantity").toString()),
            Integer.parseInt(row.get("TotalPrice").toString()),
            row.get("CurrencyName").toString()));
        }
        tblViewPurchase.setItems(purchase);
        ControlHelper.setColumnsFactory(new Pair<>(colPurchaseID,"purchaseId"),
                new Pair<>(colSupplier,"supplierName"),
                new Pair<>(colProduct,"productName"),
                new Pair<>(colQuantity,"quantity"),
                new Pair<>(colPrice,"price"),
                new Pair<>(colTotalPrice,"totalPrice"),
                new Pair<>(colCurrency,"currencyName"));
        DashboardModel.getInstance().setPurchaseCount(totalPurchases);
        ControlHelper.enableUniversalSearch(txtSearchPurchase, tblViewPurchase, purchase);
        
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
    private void calculateQtyAndSale(){
        IntegerBinding totalQty=ControlHelper.totalSum(tblViewPurchase, PurchaseModel::getQuantity);
        lblTotalQuantities.textProperty().bind(totalQty.asString());
        IntegerBinding totalPurchase=ControlHelper.totalSum(tblViewPurchase, PurchaseModel::getTotalPrice);
        lblGrandTotal.textProperty().bind(totalPurchase.asString()); 
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       loadProductComboBox();
       loadCurrency();
       loadSupplier();
       loadPuchaseData();
       loadActionButtons();
       calculateQtyAndSale();
    }    
    
}
