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
import com.jfoenix.controls.JFXButton;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Pair;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

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
    @FXML
    private JFXButton btnExportToExcel;
    @FXML
    private JFXButton btnPrintSales;
    @FXML
    private TableColumn<?, ?> colInvoiceId;

    private void loadSaleData(){
        //String query="Select s.SaleID ,c.Name as CustomerName,p.Name as ProductName,s.Quantity,s.PricePerUnit,cu.Name as CurrencyName,s.CreatedAt from Sale s JOIN Customer c ON s.CustomerID=c.CustomerID JOIN Product p ON s.ProductID=p.ProductID JOIN Currency cu ON s.CurrencyID=cu.CurrencyID";
      /*  String query="Select s.SaleID ,c.Name as CustomerName,p.Name as ProductName,s.Quantity,pur.PricePerQuantity as PurchasedPrice,s.PricePerUnit,cu.Name as CurrencyName,s.CreatedAt from Sale s "
                + "JOIN Customer c ON s.CustomerID=c.CustomerID "
                + "JOIN Product p ON s.ProductID=p.ProductID "
                + "JOIN Currency cu ON s.CurrencyID=cu.CurrencyID "
                + "JOIN Purchase pur ON S.PurchaseID=PUR.PurchaseID";*/
      String query="SELECT s.SaleID, s.InvoiceID, c.Name AS CustomerName, p.Name AS ProductName, s.Quantity, pur.PricePerQuantity AS PurchasedPrice, s.PricePerUnit, cu.Name AS CurrencyName, s.CreatedAt FROM Sale s"
              + " JOIN Customer c ON s.CustomerID = c.CustomerID "
              + "JOIN Product p ON s.ProductID = p.ProductID "
              + "JOIN Currency cu ON s.CurrencyID = cu.CurrencyID "
              + "JOIN Purchase pur ON s.PurchaseID = pur.PurchaseID";
        List<Map<String,Object>> data=operation.retrieve(query);
      //  int totalSales=0;
        for(Map<String,Object> row:data){
          //  int sale=Integer.parseInt(row.get("PricePerUnit").toString());
           // totalSales+=sale;
            java.sql.Timestamp st=(java.sql.Timestamp) row.get("CreatedAt");
            LocalDateTime createdAt=st.toLocalDateTime();
            saleList.add(new SaleModel(Integer.parseInt(row.get("SaleID").toString()),
                    Integer.parseInt(row.get("InvoiceID").toString()),
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
             new Pair<>(colInvoiceId,"invoiceId"),
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
       ObjectBinding<BigDecimal> totalSale=ControlHelper.totalSum(tblViewSale, SaleModel::getTotalPrice);
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
    private void printInvoiceById(int invoiceId)throws Exception{
        String query="SELECT  i.InvoiceID, c.Name AS CustomerName, p.Name AS ProductName, s.Quantity, s.PricePerUnit, cu.Name AS CurrencyName  FROM Sale s"
                + " JOIN Invoice i ON s.InvoiceID = i.InvoiceID  "
                + "  JOIN Customer c ON i.CustomerID = c.CustomerID  "
                + " JOIN Product p ON s.ProductID = p.ProductID   "
                + " JOIN Currency cu ON s.CurrencyID = cu.CurrencyID WHERE i.InvoiceID = ?";
        List<Map<String,Object>> data=operation.retrieve(query, invoiceId);
        if(data==null || data.isEmpty()){
            ControlHelper.showAlertMessage("No data found", Alert.AlertType.WARNING);
            return;
        }
        List<InvoiceDTO> invoiceItems=new ArrayList<>();
        for(Map<String,Object> row:data){
            invoiceItems.add(new InvoiceDTO(Integer.parseInt(row.get("InvoiceID").toString()),row.get("CustomerName").toString(),row.get("ProductName").toString(),
            Integer.parseInt(row.get("Quantity").toString()),
            Integer.parseInt(row.get("PricePerUnit").toString()),
            row.get("CurrencyName").toString()));
        }
        JRBeanCollectionDataSource ds=new JRBeanCollectionDataSource(invoiceItems);
        
        Map<String,Object> params=new HashMap<>();
        params.put("invoiceNumber", invoiceId);
        params.put("customerName", data.get(0).get("CustomerName"));
        params.put("ReportDataSource",invoiceItems);
        JasperPrint print=JasperFillManager.fillReport(getClass().getResourceAsStream("/Report/JASPERFiles/CartItemInvoice.jasper"),params,ds);
        JasperViewer.viewReport(print,false);
    }
//    private void printAllSales()throws Exception{
//        JRBeanCollectionDataSource ds=new JRBeanCollectionDataSource(tblViewSale.getItems());
//        Map<String,Object> params=new HashMap<>();
//        params.put("reportTitle", "All Sales Report");
//        JasperPrint print=JasperFillManager.fillReport(getClass().getResourceAsStream("/Report/JASPERFiles/CartItemInvoice.jasper"),params,ds);
//        JasperViewer.viewReport(print,false);
//    }
    @FXML
    private void printSales(){
        SaleModel selected=tblViewSale.getSelectionModel().getSelectedItem();
        try{
            if(selected!=null){
                printInvoiceById(selected.getInvoiceId());
            } //else{
//               //printAllSales(); 
//            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSaleData();
        totalQtyAndSale();
        btnExportToExcel.setOnAction(event->{
          ControlHelper.exportToExcel(tblViewSale, (Stage)tblViewSale.getScene().getWindow());
        });
    }    
    
}
