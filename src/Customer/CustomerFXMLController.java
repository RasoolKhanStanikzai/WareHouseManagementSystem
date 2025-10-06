/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Customer;

import DataValidations.TextFieldValidations;
import DatabaseOperations.CRUDOperations;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ControlHelper.ControlHelper;
import DataModels.CustomerModel;
import DataModels.DashboardModel;
import com.jfoenix.controls.JFXButton;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import DataModels.*;
/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class CustomerFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    DashboardModel dashboardModel=new DashboardModel();
    UserModel currentUser=Session.getCurrentUser();

    @FXML
    private AnchorPane customerVBOX;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private TextField txtCustomerPhone;
    @FXML
    private TextField txtCustomerEmail;
    @FXML
    private TextArea txtCustomerAddress;
    @FXML
    private Label lblNotification;
    @FXML
    private TableView<CustomerModel> tableView;
    @FXML
    private TableColumn<?, ?> customerIDColumn;
    @FXML
    private TableColumn<?, ?> customerNameColumn;
    @FXML
    private TableColumn<?, ?> customerPhoneColumn;
    @FXML
    private TableColumn<?, ?> customerEmailColumn;
    @FXML
    private TableColumn<?, ?> customerAddressColumn;
    @FXML
    private TableColumn<CustomerModel, Void> customerActionColumn;
    @FXML
    private Label lblCustomerID;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXButton btnSaveAndUpdate;
    @FXML
    private TextField txtSearch;
    
  
    /**
     * Initializes the controller class.
     */
    
    @FXML
    private void clickSaveButton(){
        if(btnSaveAndUpdate.getText().equals("Save")){
        String query="insert into Customer (Name,Phone,Email,Address,CreatedBy) values(?,?,?,?,?)";
        boolean isValid=TextFieldValidations.isTextFieldNotEmpty( txtCustomerName,txtCustomerPhone,txtCustomerEmail,txtCustomerAddress);
        boolean isContainNumber=TextFieldValidations.validateNoNumber(txtCustomerName);
        boolean isValidEmail=TextFieldValidations.validateEmail(txtCustomerEmail);
        boolean isValidPhone=TextFieldValidations.isPhoneNumber(txtCustomerPhone);
        if(!isValid){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Required");
            return ;
        }
        if(!isContainNumber){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Number are not allowed");
            return ;
        }
        if(!isValidEmail){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Email is invalid!");
            return ;
        }
        if(!isValidPhone){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Phone Number is invalid");
            return ;
        }
        //Inserting date into customer table
         boolean isInserted=operation.insert(query, txtCustomerName.getText(),
                    txtCustomerPhone.getText(),txtCustomerEmail.getText(),
                    txtCustomerAddress.getText(),currentUser.getUserID());
        if(isInserted){
                lblNotification.getStyleClass().add("notification-success");
                ControlHelper.showNotification(lblNotification, "Record inserted"); 
                clearFields();
                loadCustomerData();
            }
            else{
                lblNotification.getStyleClass().add("notification-error");
                 ControlHelper.showNotification(lblNotification, "Record Failed");
            }
        }
        else{
            
            String query="update Customer Set Name=?,Phone=?,Email=?,Address=?,UpdatedBy=?,UpdatedAT=NOW() where CustomerID=?";
            operation.update(query, txtCustomerName.getText(),txtCustomerPhone.getText()
                    ,txtCustomerEmail.getText(),txtCustomerAddress.getText(),
                    currentUser.getUserID(),Integer.parseInt(lblCustomerID.getText()));
            lblNotification.getStyleClass().add("notification-info");
            ControlHelper.showNotification(lblNotification, "Record Successfully updated.");
            clearFields();
            loadCustomerData();   
            lblCustomerID.setVisible(false);
            btnSaveAndUpdate.setText("Save");
            btnCancel.setVisible(false);
        }
    }
    
    // Loading data from database to table view
    private void loadCustomerData(){
        String query="select * from Customer where DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        
        // Converting List<Map> to observablelist<Customer>
        ObservableList<CustomerModel> customerList=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            customerList.add(new CustomerModel(Integer.parseInt( row.get("CustomerID").toString()),
            row.get("Name").toString(),
            row.get("Phone").toString(),
            row.get("Email").toString(),
            row.get("Address").toString()));
        }
        ControlHelper.setColumnFactory(customerIDColumn, "customerID");
        ControlHelper.setColumnFactory(customerNameColumn, "CustomerName");
        ControlHelper.setColumnFactory(customerPhoneColumn, "CustomerPhone");
        ControlHelper.setColumnFactory(customerEmailColumn, "CustomerEmail");
        ControlHelper.setColumnFactory(customerAddressColumn, "CustomerAddress");
        tableView.setItems(customerList);
        // Searching in text field
        ControlHelper.enableUniversalSearch(txtSearch, tableView, customerList);
        
       DashboardModel.getInstance().setCustomerCount(customerList.size());
    }
    private void clearFields(){
        lblCustomerID.setText("");
        txtCustomerName.setText("");
        txtCustomerPhone.setText("");
        txtCustomerEmail.setText("");
        txtCustomerAddress.setText("");
    }
    
    // Loading Delete and Update Button in the Table View for Action Column
    private void loadActionButtons(){
         // Update and Delete Buttons for each row in the table view
        customerActionColumn.setCellFactory(col->new TableCell<CustomerModel,Void>(){
            private final JFXButton updateBtn=new JFXButton("Update");
            private final JFXButton deleteBtn=new JFXButton("Delete");
                {
                    updateBtn.setButtonType(JFXButton.ButtonType.RAISED);
                    deleteBtn.setButtonType(JFXButton.ButtonType.RAISED);
                    updateBtn.getStyleClass().add("btn-update");
                    deleteBtn.getStyleClass().add("btn-delete");
                    
                    deleteBtn.setOnAction(event->{
                        CustomerModel customer=getTableView().getItems().get(getIndex());
                        boolean confirmed=ControlHelper.showAlertMessage("Are you sure to delete the record!", AlertType.CONFIRMATION);
                        if(confirmed){
                            String query="update customer set DeletedBy=?,DeletedAt=NOW() WHERE CustomerID=?";
                       // String query="Delete  from customer where CustomerID=?";
                        boolean success=operation.delete(query,currentUser.getUserID() ,customer.getCustomerID());
                        if(success){
                             loadCustomerData();
                            getTableView().getItems().remove(customer);
                            ControlHelper.showNotification(lblNotification, "Record Deleted Successfully");
                            lblNotification.getStyleClass().add("notification-success");
                            loadCustomerData();
                        }else{
                            ControlHelper.showNotification(lblNotification, "Delete Failed!");
                            lblNotification.getStyleClass().add("notification-info");
                        }
                        }

                    });
                    updateBtn.setOnAction(event->{
                        CustomerModel customer=getTableView().getItems().get(getIndex());
                       lblCustomerID.setVisible(true);
                        lblCustomerID.setText(String.valueOf(customer.getCustomerID()));
                        txtCustomerName.setText(customer.getCustomerName());
                        txtCustomerPhone.setText(customer.getCustomerPhone());
                        txtCustomerEmail.setText(customer.getCustomerEmail());
                        txtCustomerAddress.setText(customer.getCustomerAddress());
                        btnCancel.setVisible(true);
                        btnSaveAndUpdate.setText("Update");
                    });
 
                }
            @Override
                protected void updateItem(Void item, boolean empty){
                    super.updateItem(item, empty);
                    
                    if(empty){
                        setGraphic(null);
                        
                    }else{
                        HBox hbox=new HBox(10,updateBtn,deleteBtn);
                        hbox.setAlignment(Pos.CENTER);
                        setGraphic(hbox);
                    }
                }
        }); 
    }
    @FXML
    private void btnCancel(){
        clearFields();
        
        lblCustomerID.setVisible(false);
        btnSaveAndUpdate.setText("Save");
    }
    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadCustomerData();
        loadActionButtons();
        
    }    
    
}
