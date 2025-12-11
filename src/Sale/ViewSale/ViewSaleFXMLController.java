/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Sale.ViewSale;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import DataModels.*;
import DatabaseOperations.CRUDOperations;
import ControlHelper.ControlHelper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class ViewSaleFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    private final ReadOnlyObjectWrapper totalQuantity=new ReadOnlyObjectWrapper();
    private final ReadOnlyObjectWrapper totalSales=new ReadOnlyObjectWrapper();
    private final ReadOnlyObjectWrapper totalProfit=new ReadOnlyObjectWrapper();
    ObservableList<SaleModel> saleList=FXCollections.observableArrayList();
    @FXML
    private TableView<SaleModel> tblViewSale;
    @FXML
    private TableColumn<?, ?> colSaleID;
    @FXML
    private TableColumn<?, ?> colCustomer;
    @FXML
    private TableColumn<?, ?> colProduct;
    @FXML
    private TableColumn<?, ?> colQuantity;
    @FXML
    private TableColumn<?, ?> colPPQuantity;
    @FXML
    private TableColumn<?, ?> colCurrency;
    @FXML
    private TableColumn<?, ?> colSaleDate;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblTotalSoldQuantities;
    @FXML
    private Label lblGrandTotal;
    @FXML
    private Label lblTotalProfit;
    @FXML
    private TableColumn<?, ?> colPurchasedPrice;

    private void loadSaleData(){
        //String query="Select s.SaleID ,c.Name as CustomerName,p.Name as ProductName,s.Quantity,s.PricePerUnit,cu.Name as CurrencyName,s.CreatedAt from Sale s JOIN Customer c ON s.CustomerID=c.CustomerID JOIN Product p ON s.ProductID=p.ProductID JOIN Currency cu ON s.CurrencyID=cu.CurrencyID";
        String query="Select s.SaleID ,c.Name as CustomerName,p.Name as ProductName,s.Quantity,pur.PricePerQuantity as PurchasedPrice,s.PricePerUnit,cu.Name as CurrencyName,s.CreatedAt from Sale s "
                + "JOIN Customer c ON s.CustomerID=c.CustomerID "
                + "JOIN Product p ON s.ProductID=p.ProductID "
                + "JOIN Currency cu ON s.CurrencyID=cu.CurrencyID "
                + "JOIN Purchase pur ON S.PurchaseID=PUR.PurchaseID";
        List<Map<String,Object>> data=operation.retrieve(query);
      //  int totalSales=0;
        for(Map<String,Object> row:data){
          //  int sale=Integer.parseInt(row.get("PricePerUnit").toString());
           // totalSales+=sale;
            java.sql.Timestamp st=(java.sql.Timestamp) row.get("CreatedAt");
            LocalDateTime createdAt=st.toLocalDateTime();
            saleList.add(new SaleModel(Integer.parseInt(row.get("SaleID").toString()),
            row.get("CustomerName").toString(),
            row.get("ProductName").toString(),
            (BigDecimal)row.get("Quantity"),
            Integer.parseInt(row.get("PurchasedPrice").toString()),
            (BigDecimal)row.get("PricePerUnit"),
            row.get("CurrencyName").toString(),
            createdAt));
        }
        tblViewSale.setItems(saleList);
        ControlHelper.setColumnsFactory(new Pair<>(colSaleID,"saleId"),
             new Pair<>(colCustomer,"customerName"),
             new Pair<>(colProduct,"productName"),
             new Pair<>(colQuantity,"quantity"),
             new Pair<>(colPurchasedPrice,"purchasePrice"),
             new Pair<>(colPPQuantity,"salePrice"),
             new Pair<>(colCurrency,"currencyName"),
             new Pair<>(colSaleDate,"createdAt"));
        ControlHelper.enableUniversalSearch(txtSearch, tblViewSale, saleList);
      //  DashboardModel.getInstance().setSalesCount(totalSales);
    }
    private void totalQtyAndSale(){
       ObjectBinding<BigDecimal> totalQty=ControlHelper.totalSum(tblViewSale,SaleModel::getQuantity);
       lblTotalSoldQuantities.textProperty().bind(totalQty.asString());
       ObjectBinding<BigDecimal> totalSale=ControlHelper.totalSum(tblViewSale, SaleModel::getSalePrice);
       lblGrandTotal.textProperty().bind(totalSale.asString());
       
       ObjectBinding<BigDecimal> totalProfitBinding=new ObjectBinding<>(){
               {
                   super.bind(tblViewSale.getItems());
               }
           @Override
           protected BigDecimal computeValue() {
              return tblViewSale.getItems().stream().map(sale->sale.getSalePrice().subtract(BigDecimal.valueOf(sale.getPurchasePrice()))
              .multiply(sale.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add);
           }
       };
       lblTotalProfit.textProperty().bind(totalProfitBinding.asString());
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSaleData();
        totalQtyAndSale();
    }    
    
}
