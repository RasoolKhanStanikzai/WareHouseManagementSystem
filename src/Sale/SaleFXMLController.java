/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Sale;
import DatabaseOperations.CRUDOperations;
import DataModels.*;
import ControlHelper.ControlHelper;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import DataValidations.TextFieldValidations;
import java.math.BigDecimal;
import javafx.scene.control.Alert;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class SaleFXMLController implements Initializable {
    UserModel currentUser=Session.getCurrentUser();
    private int selectedCustomerID=0;
    private int selectedProductID=0;
    private int selectedCurrencyID=0;
    private int selectedPurchaseID=0;
    private final ReadOnlyIntegerWrapper totalQuantity=new ReadOnlyIntegerWrapper();
    private final ReadOnlyIntegerWrapper totalSale=new ReadOnlyIntegerWrapper();
    private final ReadOnlyIntegerWrapper saleGrandTotal=new ReadOnlyIntegerWrapper();
    CRUDOperations operation=new CRUDOperations();
    ObservableList<SaleCartModel> cartList=FXCollections.observableArrayList();
    @FXML
    private ComboBox<CustomerModel> comboCustomer;
    @FXML
    private ComboBox<ProductModel> comboProduct;
    @FXML
    private TextField txtPurchasePrice;
    @FXML
    private TextField txtSalePrice;
    @FXML
    private TextField txtQuantity;
    @FXML
    private ComboBox<CurrencyModel> comboCurrency;
    @FXML
    private TableView<SaleCartModel> tblViewCart;
    @FXML
    private TableColumn<SaleCartModel, Void> colItem;
    @FXML
    private TableColumn<?, ?> colCustomer;
    @FXML
    private TableColumn<?, ?> colProduct;
    @FXML
    private TableColumn<?, ?> colPurchasePrice;
    @FXML
    private TableColumn<?, ?> colSalePrice;
    @FXML
    private TableColumn<?, ?> colQuantity;
    @FXML
    private TableColumn<?, ?> colCurrency;
    @FXML
    private TableColumn<SaleCartModel, Void> colAction;
    @FXML
    private Label lblTotalItems;
    @FXML
    private Label lblTotalSales;
    @FXML
    private Label lblNotification;
    @FXML
    private JFXButton btnRemoveAll;
    @FXML
    private JFXButton btnSale;
    @FXML
    private JFXButton btnSaleView;
    @FXML
    private JFXButton btnPrint;

    private void loadCustomer(){
        String query="select CustomerID,Name from customer Where DeletedAt IS NULL";
        ControlHelper.fillComboBox(comboCustomer, query,
                row->new CustomerModel((int)row.get("CustomerID"),(String)row.get("Name")),
                customer->customer.getCustomerName(),customer->selectedCustomerID=customer.getCustomerID());
        ControlHelper.makeComboBoxSearchable(comboCustomer);
    }
    private void loadProduct(){
       // String query="SELECT DISTINCT p.ProductID, p.Name FROM product p JOIN purchase pur ON pur.ProductID = p.ProductID WHERE p.DeletedAt IS NULL";
        String query="Select p.ProductID,p.Name from Stock s Join Product p ON s.ProductID=p.ProductID Where s.Quantity>0";
        ControlHelper.fillComboBox(comboProduct, query,
                row->new ProductModel((int)row.get("ProductID"),(String)row.get("Name")),
                product->product.getName(),product->selectedProductID=product.getId());
        
        
        comboProduct.setOnAction(event->{
            ProductModel selectedProduct=comboProduct.getSelectionModel().getSelectedItem();
            if(selectedProduct!=null){
                selectedProductID=selectedProduct.getId();
                //String purchaseQuery="select PricePerQuantity from Purchase where ProductID=?";
                String purchaseQuery="Select PurchaseID,PricePerQuantity from Purchase Where ProductID=? order by PurchaseID Desc Limit 1";
                var data=operation.retrieve(purchaseQuery, selectedProductID);
                
                if(data!=null && !data.isEmpty()){
                    selectedPurchaseID=(int) data.get(0).get("PurchaseID");
                    //String purchasePrice=data.get(0).get("PricePerQuantity").toString();
                    int purchasePrice=Integer.parseInt(data.get(0).get("PricePerQuantity").toString());
                    txtPurchasePrice.setText(String.valueOf(purchasePrice));
     
                } else{
                    txtPurchasePrice.setText("");
                    selectedPurchaseID=0;
                }
            }
        });
        ControlHelper.makeComboBoxSearchable(comboProduct);
    }
    private void loadCurrency(){
        String query="select CurrencyID,Name from currency where DeletedAt IS NULL";
        ControlHelper.fillComboBox(comboCurrency, query, 
                row->new CurrencyModel((int)row.get("CurrencyID"),(String)row.get("Name")),
                currency->currency.getName(),currency->selectedCurrencyID=currency.getId());
        ControlHelper.makeComboBoxSearchable(comboCurrency);
    }
    @FXML
    private void loadCart(){
        boolean isEmpty=TextFieldValidations.isTextFieldNotEmpty(comboCustomer,comboProduct,
                comboCurrency,txtPurchasePrice,txtSalePrice,txtQuantity);
        if(!isEmpty){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Required");
            return ;
        }
        ProductModel product=comboProduct.getSelectionModel().getSelectedItem();
        int quantity=Integer.parseInt(txtQuantity.getText());
        int stockQty=getStockQuantity(product.getId());
        int purchasePrice=Integer.parseInt(txtPurchasePrice.getText());
        int salePrice=Integer.parseInt(txtSalePrice.getText());
        if(salePrice<purchasePrice){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Sale Price should not be less then Purchase Price!");
            return ;
        }
        if(quantity>stockQty){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Only "+stockQty+" quantity avaiable in stock!");
            return ;
        }
        CustomerModel customer=comboCustomer.getSelectionModel().getSelectedItem();
        
        CurrencyModel currency=comboCurrency.getSelectionModel().getSelectedItem();
        
       // int quantity=Integer.parseInt(txtQuantity.getText());
        SaleCartModel item=new SaleCartModel(
         customer.getCustomerID(),
        customer.getCustomerName(),
        product.getId(),product.getName(),
        selectedPurchaseID,
        purchasePrice,salePrice,quantity,
        currency.getId(),currency.getName());
        cartList.add(item);
        tblViewCart.setItems(cartList);
        ControlHelper.clearFaileds(comboProduct,
                txtPurchasePrice,txtSalePrice,txtQuantity);
        comboCustomer.setDisable(true);
        comboCurrency.setDisable(true);
        btnRemoveAll.setDisable(false);
        btnSale.setDisable(false);
    }
    private void setupCartTable(){
        colItem.setCellFactory(col->new TableCell<SaleCartModel,Void>(){
            @Override
            protected void updateItem(Void item,boolean empty){
                super.updateItem(item, empty);
                if(empty){
                    setText(null);
                } else{
                    setText(String.valueOf(getIndex()+1));
                }
            }
        });
        ControlHelper.setColumnsFactory(new Pair<>(colCustomer,"customerName"),
                new Pair<>(colProduct,"productName"),
                new Pair<>(colPurchasePrice,"purchasePrice"),
                new Pair<>(colSalePrice,"salePrice"),
                new Pair<>(colQuantity,"quantity"),
                new Pair<>(colCurrency,"currencyName"));
    }
    private void totalOfQuanttyAndSale(){
          IntegerBinding totalItem=ControlHelper.totalSum(cartList, SaleCartModel::getQuantity);
          totalQuantity.bind(totalItem);
          IntegerBinding totalS=ControlHelper.totalSum(cartList, SaleCartModel::getSalePrice);
          totalSale.bind(totalS);
          
    }
    private void removeItem(){
        colAction.setCellFactory(col->new TableCell<SaleCartModel,Void>(){
            private final JFXButton btnRemove=new JFXButton("Remove");
                {
                    btnRemove.getStyleClass().add("btn-delete");
                    btnRemove.setButtonType(JFXButton.ButtonType.RAISED);
                    
                    btnRemove.setOnAction(event->{
                        SaleCartModel list=getTableView().getItems().get(getIndex());
                        getTableView().getItems().remove(list);
                        emptyCartList();
                    });
                }
            @Override
                protected void updateItem(Void item,boolean empty){
                    super.updateItem(item, empty);
                    if(empty){
                        setGraphic(null);
                    } else{
                        HBox hbox=new HBox(10,btnRemove);
                        hbox.setAlignment(Pos.CENTER);
                        setGraphic(hbox);
                    }
                }
        });
    }  
    @FXML
    private void removeAll(){
        boolean confirm=ControlHelper.showAlertMessage("Are You Sure to Empty the List", Alert.AlertType.CONFIRMATION);
        if(confirm){
        cartList.clear();
        ControlHelper.clearFaileds(comboProduct,
                txtPurchasePrice,txtSalePrice,txtQuantity);
        comboCustomer.setDisable(false);
        comboCurrency.setDisable(false);
        btnRemoveAll.setDisable(true);
        btnSale.setDisable(true);
        }
    }
    private int getStockQuantity(int productId){
        String query="Select Quantity from Stock Where ProductID=?";
        var data=operation.retrieve(query, productId);
        if(data!=null && !data.isEmpty()){
            return Integer.parseInt(data.get(0).get("Quantity").toString());
        }
        return 0;
    }
    @FXML
    private void saveSale(){
        if(cartList.isEmpty()){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Cart is Empty");
            return ;
        }
        boolean success=operation.excuteMultipleTransactions(conn->{
            int totalAmount=cartList.stream().mapToInt(item->item.getTotalPrice()).sum();
            String invoiceQry="insert into Invoice(CustomerID, InvoiceDate, TotalAmount, CreatedAt, CreatedBy) VALUES (?, NOW(), ?, NOW(), ?)";
            int invoiceId=operation.getInsertAndUpdateID(invoiceQry, selectedCustomerID,totalAmount,currentUser.getUserID());
            
            for(SaleCartModel item:cartList){
                int stockQty=getStockQuantity(item.getProductID());
                if(item.getQuantity()>stockQty){
                    lblNotification.getStyleClass().add("notification-warnning");
                    ControlHelper.showNotification(lblNotification, "Only "+stockQty+" quantity available in stock!");
                    return ;
                }
                String query="insert into Sale (InvoiceId,CustomerID,ProductID,Quantity,PurchaseID,PricePerUnit,CurrencyID,CreatedAt,CreatedBy) values(?,?,?,?,?,?,?,NOW(),?)";
                operation.insert(query,
                        invoiceId,
                       item.getCustomerID(),
                       item.getProductID(),
                       item.getQuantity(),
                       item.getPurchaseID(),
                       item.getSalePrice(),
                       item.getCurrencyID(),
                       currentUser.getUserID());
                String updateQuery="Update Stock set Quantity=Quantity-?,LastUpdated=NOW() Where ProductID=?";
                operation.update(updateQuery,
                        item.getQuantity(),
                        item.getProductID());
            }
            boolean confirmInvoice=ControlHelper.showAlertMessage("Print Invoice", Alert.AlertType.CONFIRMATION);
            if(confirmInvoice){
                printCustomerInvoice(invoiceId);
            }
        });
        if(success){
            lblNotification.getStyleClass().add("notification-success");
            ControlHelper.showNotification(lblNotification, "Sale Saved Successfully");
            cartList.clear();
            tblViewCart.refresh();
            comboCustomer.setDisable(false);
            comboCurrency.setDisable(false);
            btnRemoveAll.setDisable(true);
            btnSale.setDisable(true);
            ControlHelper.clearFaileds(comboProduct,
                txtPurchasePrice,txtSalePrice,txtQuantity,txtPurchasePrice);
        }
    }
    private void emptyCartList(){
        if(cartList.isEmpty() || cartList.size()<=0){
            btnSale.setDisable(true);
            btnRemoveAll.setDisable(true);
        } 
    }
    @FXML
    private void loadViewSaleFXML(){
        try{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/Sale/ViewSale/ViewSaleFXML.fxml"));
        Parent root=loader.load();
        Stage mainStage=new Stage();
        Scene scene=new Scene(root);
        mainStage.setMaximized(true);
        mainStage.setTitle("View Sales");
        mainStage.setScene(scene);
       // Stage currentStage=(Stage)btnSaleView.getScene().getWindow();
        mainStage.show();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private void loadSaleGrandTotal(){
        String query="select SUM(PricePerUnit) as TotalSales from Sale";
        List<Map<String,Object>> data=operation.retrieve(query);
        int total=0;
        if(data!=null && !data.isEmpty() && data.get(0).get("TotalSales")!=null){
            total=Integer.parseInt(data.get(0).get("TotalSales").toString());
        }
        DashboardModel.getInstance().setSalesCount(total);
    }
    @FXML
    private void printInvoice() throws Exception{
             JRBeanCollectionDataSource dataSource=new JRBeanCollectionDataSource(cartList);
             Map<String,Object> parameters=new HashMap<>();
             parameters.put("invoiceNumber", System.currentTimeMillis());
             parameters.put("customerName", comboCustomer.getValue().getCustomerName());
             parameters.put("ReportDataSource", cartList);
             
             JasperPrint print=JasperFillManager.fillReport(getClass().getResourceAsStream("/Report/CartItemInvoice.jasper"),parameters,dataSource);
             JasperViewer.viewReport(print,false);
    }
    private void printCustomerInvoice(int invoiceId)throws Exception{
        JRBeanCollectionDataSource dataSource=new JRBeanCollectionDataSource(cartList);
             Map<String,Object> parameters=new HashMap<>();
             parameters.put("invoiceNumber", invoiceId);
             parameters.put("customerName", comboCustomer.getValue().getCustomerName());
             parameters.put("ReportDataSource", cartList);
             
             JasperPrint print=JasperFillManager.fillReport(getClass().getResourceAsStream("/Report/CartItemInvoice.jasper"),parameters,dataSource);
             JasperViewer.viewReport(print,false);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCustomer();
        loadProduct();
        loadCurrency();
        setupCartTable();
        totalOfQuanttyAndSale();
        loadSaleGrandTotal();
        lblTotalItems.textProperty().bind(totalQuantity.asString());
        lblTotalSales.textProperty().bind(totalSale.asString());
        removeItem();
        emptyCartList();
    }    
    
}
