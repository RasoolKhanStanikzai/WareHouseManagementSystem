/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Report;

import DataModels.SaleModel;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class ReportsFXMLController implements Initializable {

    private Map<String,Parent> pages=new HashMap<>();
    private Map<String,Object> controller=new HashMap<>();
    ObservableList<SaleModel> saleList=FXCollections.observableArrayList();
    @FXML
    private void viewProduct(){
        loadStage("/Product/ViewProduct/ViewProductFXML.fxml","View Product");
    }
    @FXML
    private void viewCustomer(){
        loadStage("/Customer/ViewCustomer/ViewCustomerFXML.fxml","View Customer");
    }
    @FXML
    private void viewSupplier(){
        loadStage("/Supplier/ViewSupplier/ViewSupplierFXML.fxml","View Supplier");
    }
    @FXML
    private void viewCategory(){
        loadStage("/Category/ViewCategory/ViewCategoryFXML.fxml","View Category");
    }
    @FXML
    private void viewPurchase(){
        loadStage("/Purchase/ViewPurchase/ViewPurchaseFXML.fxml","View Purchase");
    }
    @FXML
    private void viewSaleInvoice(ActionEvent event)throws Exception {
        loadStage("/Sale/ViewSale/ViewSaleFXML.fxml","View Sales");
    }
    
    private void loadStage(String fxmlPath,String title){
        try{
            FXMLLoader loader=new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root=loader.load();
            Stage stage=new Stage();
            Scene scene=new Scene(root);
            stage.setMaximized(true);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

  
}
