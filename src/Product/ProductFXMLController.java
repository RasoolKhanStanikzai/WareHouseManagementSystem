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
import DataModels.ProductModel;
import DataModels.Session;
import DataModels.UserModel;
import com.jfoenix.controls.JFXButton;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import javafx.util.StringConverter;
/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class ProductFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    UserModel currentUser=Session.getCurrentUser();
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

   private int selectedComboCategoryID;
   private int selectedComboCurrencyID;
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
    private TableColumn<?, ?> colCost;
    @FXML
    private TableColumn<?, ?> colSale;
    @FXML
    private TableColumn<?, ?> colCurrency;
    @FXML
    private TableColumn<ProductModel, Void> colAction;
    @FXML
    private JFXButton btnSaveAndUpdate;
    @FXML
    private Label lblProductID;
    
    @FXML
    private void saveRecord(){
        if(btnSaveAndUpdate.getText().equals("Save")){
            String query="insert into Product(Name,CategoryID,Unit,CostPrice,SalePrice,CurrencyID,CreatedBy)values(?,?,?,?,?,?,?)";
             boolean insert=operation.insert(query, txtProductName.getText(),selectedComboCategoryID,txtUnitPrice.getText(),
                txtCostPrice.getText(),txtSalePrice.getText(),selectedComboCurrencyID,currentUser.getUserID());
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
        else{
            String query="update Product set Name=?,CategoryID=?,Unit=?,CostPrice=?,SalePrice=?,CurrencyID=?,UpdatedBy=?,UpdatedAt=NOW() Where ProductID=?";
            operation.update(query, txtProductName.getText(),selectedComboCategoryID,
                    txtUnitPrice.getText(),txtCostPrice.getText(),txtSalePrice.getText(),selectedComboCurrencyID,currentUser.getUserID()
                    );
            lblNotification.getStyleClass().add("notification-info");
            ControlHelper.showNotification(lblNotification, "Record Updated");
            ControlHelper.clearFaileds(txtProductName,comboCategory,
                    txtUnitPrice,txtCostPrice,txtSalePrice,comboCurrency);
            btnSaveAndUpdate.setText("Save");
            lblProductID.setVisible(false);
            loadProductData();
        }
    }
    
    // loading combo box category
    private void loadCategoryComboBox(){
       comboCategory.getItems().clear();
       
        ControlHelper.fillComboBox(comboCategory, 
                "Select CategoryID,CategoryName,description from Category where DeletedAT IS NULL",
                row-> new CategoryModel((int) row.get("CategoryID"),
                        (String) row.get("CategoryName"),
                        (String)row.get("description")),
                cat->cat.getName(),cat->selectedComboCategoryID=cat.getId()); 
         ControlHelper.makeComboBoxSearchable(comboCategory);  
    }
    private void loadCurrency(){
        String query="select CurrencyID,Code,Name,Symbol,Country,DecimalPlaces,Status,Rate from Currency where DeletedAt IS NULL";
        ControlHelper.fillComboBox(comboCurrency, query, row->new CurrencyModel((int)row.get("CurrencyID"),(String)row.get("Code"),(String)row.get("Name"),
                (String)row.get("Symbol"),(String)row.get("Country"),(int)row.get("DecimalPlaces"),
                (String)row.get("Status"),(double)row.get("Rate")),
                currency->currency.getName(),currency->selectedComboCurrencyID=currency.getId());
        ControlHelper.makeComboBoxSearchable(comboCurrency);
    }
    
    private void loadProductData(){
        String query="select p.ProductID,p.Name,c.CategoryName as CategoryName,p.Unit,p.CostPrice,p.SalePrice,cu.Name as CurrencyName from product p  JOIN category c ON p.CategoryID=c.CategoryID JOIN currency cu "
                + "ON p.CurrencyID=cu.CurrencyID where p.DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        ObservableList<ProductModel>product=FXCollections.observableArrayList();
        
        for(Map<String,Object> row:data){
            product.add(new ProductModel(Integer.parseInt(row.get("ProductID").toString()),
            row.get("Name").toString(),
            row.get("CategoryName").toString(),
            Integer.parseInt(row.get("Unit").toString()),
            Integer.parseInt(row.get("CostPrice").toString()),
            Integer.parseInt(row.get("SalePrice").toString()),
            row.get("CurrencyName").toString()));
        }
        tblViewProduct.setItems(product);
        ControlHelper.setColumnsFactory(new Pair<>(colID,"id"),
                new Pair<>(colName,"name"),
                new Pair<>(colCategory,"category"),
                new Pair<>(colUnit,"unit"),
                new Pair<>(colCost,"costPrice"),
                new Pair<>(colSale,"salePrice"),
                new Pair<>(colCurrency,"currency"));
    }
    private void loadBtnAction(){
       colAction.setCellFactory(col->new TableCell<ProductModel,Void>(){
            JFXButton btnUpdate=new JFXButton("Update");
            JFXButton btnDelete=new JFXButton("Delete");
                 {
                       btnUpdate.setButtonType(JFXButton.ButtonType.RAISED);
                       btnDelete.setButtonType(JFXButton.ButtonType.RAISED);
                       btnUpdate.getStyleClass().add("btn-update");
                       btnDelete.getStyleClass().add("btn-delete");
                       
                       btnUpdate.setOnAction(event->{
                         ProductModel product=getTableView().getItems().get(getIndex());
                            lblProductID.setVisible(true);
                            lblProductID.setText(String.valueOf(product.getId()));
                            txtProductName.setText(product.getName());
                            comboCategory.getEditor().setText(product.getCategory());
                            txtUnitPrice.setText(String.valueOf(product.getUnit()));
                            txtSalePrice.setText(String.valueOf(product.getSalePrice()));
                            txtCostPrice.setText(String.valueOf(product.getSalePrice()));
                            comboCurrency.getEditor().setText(product.getCurrency());
                            btnSaveAndUpdate.setText("Update");
                       });
                       btnDelete.setOnAction(event->{
                           ProductModel product=getTableView().getItems().get(getIndex());
                           boolean confirm=ControlHelper.showAlertMessage("Are You sure to delete", Alert.AlertType.WARNING);
                           if(confirm){
                               String query="update Product set DeletedBy=?,DeletedAt=NOW() WHERE ProductID=?";
                               boolean success=operation.delete(query,currentUser.getUserID(),product.getId());
                               if(success){
                                   loadProductData();
                                   getTableView().getItems().remove(product);
                                   lblNotification.getStyleClass().add("notification-success");
                                    ControlHelper.showNotification(lblNotification, "Record Deleted Successfully");
                                    loadProductData();
                               } else{
                                    lblNotification.getStyleClass().add("notification-error");
                                     ControlHelper.showNotification(lblNotification, "Deleted Failed");
                               } 
                           }
                       });
                       
                 }
            @Override
                 protected void updateItem(Void item,boolean empty){
                     super.updateItem(item, empty);
                     if(empty)
                         setGraphic(null);
               else{
                         HBox hbox=new HBox(10,btnUpdate,btnDelete);
                         hbox.setAlignment(Pos.CENTER);
                         setGraphic(hbox);
                     }
                         
                 }
       });
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnClear.setOnAction(event->{
            ControlHelper.clearFaileds(txtProductName,comboCategory,txtUnitPrice,txtSalePrice,comboCurrency,txtCostPrice,lblProductID);  
        });
        loadCategoryComboBox();
        loadCurrency();
        loadProductData();
        loadBtnAction();
    }    
}
