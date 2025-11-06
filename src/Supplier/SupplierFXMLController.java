/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Supplier;

import DatabaseOperations.CRUDOperations;
import DataValidations.TextFieldValidations;
import ControlHelper.ControlHelper;
import DataModels.*;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class SupplierFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    UserModel currentUser=Session.getCurrentUser();
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextArea txtAdd;
    @FXML
    private JFXButton btnSaveAndUpdate;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private TableView<SupplierModel> tblViewSupplier;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colName;
    @FXML
    private TableColumn<?, ?> colPhone;
    @FXML
    private TableColumn<?, ?> colEmail;
    @FXML
    private TableColumn<?, ?> colAddress;
    @FXML
    private Label lblNotification;
    @FXML
    private TableColumn<SupplierModel, Void> colAction;
    @FXML
    private Label lblSupplierID;
    
    
    @FXML
    private void saveAndUpdate(){
        if(btnSaveAndUpdate.getText().equals("Save")){
        boolean isEmpty=TextFieldValidations.isTextFieldNotEmpty(txtName,txtPhone,txtEmail,txtAdd);
        boolean isPhoneNO=TextFieldValidations.isPhoneNumber(txtPhone);
        boolean isEmail=TextFieldValidations.validateEmail(txtEmail);
        String query="insert into Supplier(Name,Phone,Email,Address,CreatedBy)"
                + "values(?,?,?,?,?)";
        boolean isInsert=operation.insert(query, txtName.getText(),txtPhone.getText(),
                txtEmail.getText(),txtAdd.getText(),currentUser.getUserID());
        if(!isEmpty){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Required");
            return;
        }
        if(!isPhoneNO){
            lblNotification.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblNotification, "Not a Phone Number");
            return;
        }
        if(!isEmail){
            lblNotification.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblNotification, "Not an Email Type");
            return;
        }
        
        if(isInsert){
            lblNotification.getStyleClass().add("notification-success");
            ControlHelper.showNotification(lblNotification, "Record Successfully Saved");
            ControlHelper.clearFaileds(txtName,txtPhone,txtEmail,txtAdd);
            loadSupplierData();
        } else{
            lblNotification.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblNotification, "Record Failed");
        }
    }
      else{
            String query="Update Supplier set name=?,phone=?,email=?,address=?,UpdatedBy=?,UpdatedAt=NOW() where SupplierID=?";
            boolean isUpdated=operation.update(query, txtName.getText(),
                    txtPhone.getText(),txtEmail.getText(),txtAdd.getText(),currentUser.getUserID());
            if(isUpdated){
                lblNotification.getStyleClass().add("notification-success");
                ControlHelper.showNotification(lblNotification, "Record Updated Successfully");
                ControlHelper.clearFaileds(txtName,txtPhone,txtEmail,txtAdd,lblSupplierID);
                btnSaveAndUpdate.setText("Save");
                loadSupplierData();
                
            }
        }
    }
    
    private void loadSupplierData(){
        String query="select * from Supplier Where DeletedAt IS NULL";
        List<Map<String,Object>>data=operation.retrieve(query);
        ObservableList<SupplierModel>supplierList=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            supplierList.add(new SupplierModel(Integer.parseInt(row.get("SupplierID").toString()
                    ),row.get("Name").toString(),
            row.get("Phone").toString(),
            row.get("Email").toString(),
            row.get("Address").toString()));          
        }
        
        ControlHelper.setColumnsFactory(new Pair<>(colID,"id"),
                new Pair<>(colName,"name"),
                new Pair<>(colPhone,"phone"),
                new Pair<>(colEmail,"email"),
                new Pair<>(colAddress,"address")
        );
        tblViewSupplier.setItems(supplierList);
       DashboardModel.getInstance().setSupplierCount(supplierList.size());
    }
    
    private void loadActionButtons(){
        colAction.setCellFactory(col->new TableCell<SupplierModel,Void>(){
        JFXButton btnUpdate=new JFXButton("Update");
        JFXButton btnDelete=new JFXButton("Delete");
        {
            btnUpdate.setButtonType(JFXButton.ButtonType.RAISED);
            btnDelete.setButtonType(JFXButton.ButtonType.RAISED);
            btnUpdate.getStyleClass().add("btn-update");
            btnDelete.getStyleClass().add("btn-delete");
            
            btnUpdate.setOnAction(event->{
                SupplierModel supplier=getTableView().getItems().get(getIndex());
                lblSupplierID.setText(String.valueOf(supplier.getId()));
                txtName.setText(supplier.getName());
                txtPhone.setText(supplier.getPhone());
                txtEmail.setText(supplier.getEmail());
                txtAdd.setText(supplier.getAddress());
                btnSaveAndUpdate.setText("Update");
                
            });
            btnDelete.setOnAction(event->{
                SupplierModel supplier=getTableView().getItems().get(getIndex());
                boolean confirm=ControlHelper.showAlertMessage("Are You sure to delete", Alert.AlertType.WARNING);
                if(confirm){
                    String query="update supplier set DeletedBy=?,DeletedAt=NOW() Where SupplierID=?";
                    boolean success=operation.update(query,currentUser.getUserID(),supplier.getId());
                    if(success){
                        getTableView().getItems().remove(supplier);
                        lblNotification.getStyleClass().add("notification-success");
                        ControlHelper.showNotification(lblNotification, "Record Deleted Successfully");
                        loadSupplierData();
                    } else{
                        lblNotification.getStyleClass().add("notification-error");
                        ControlHelper.showNotification(lblSupplierID, "Delete Failed");
                        
                    } 
                }    
            });
        }
        @Override
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
    @FXML
    private void cancel(){
        btnSaveAndUpdate.setText("Save");
        ControlHelper.clearFaileds(txtName,txtPhone,txtEmail,txtAdd,lblSupplierID);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSupplierData();
        loadActionButtons();
    }    
    
}
