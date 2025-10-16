/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Category;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ControlHelper.ControlHelper;
import DataValidations.TextFieldValidations;
import DataModels.*;
import DatabaseOperations.CRUDOperations;
import com.jfoenix.controls.JFXButton;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class CategoryFXMLController implements Initializable {
    UserModel currentUser=Session.getCurrentUser();
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private TextField txtCategoryName;
    @FXML
    private TextArea txtCategoryDescriptions;
    @FXML
    private Label lblNotification;
    @FXML
    private TableColumn<?, ?> columnCategoryID;
    @FXML
    private TableColumn<?, ?> columnCategoryName;
    @FXML
    private TableColumn<?, ?> columnCategoryDescriptions;
    @FXML
    private TableColumn<CategoryModel,Void> columnCatAction;
    @FXML
    private TableView<CategoryModel> tblViewCategory;
    @FXML
    private JFXButton btnSaveAndUpdate;
    @FXML
    private Label lblCategoryID;

    
    @FXML
    private void insert(){
        boolean isValid=TextFieldValidations.isTextFieldNotEmpty(txtCategoryName,txtCategoryDescriptions);
        if(!isValid){
            lblNotification.getStyleClass().add("notification-warnning");
            ControlHelper.showNotification(lblNotification, "Required");
            return;
        }
        if(btnSaveAndUpdate.getText().equals("Save")){
        String query="insert into Category(CategoryName,Description,CreatedBy)values(?,?,?)";
        boolean insert=operation.insert(query, txtCategoryName.getText(),txtCategoryDescriptions.getText(),currentUser.getUserID());
        
        if(insert){
            lblNotification.getStyleClass().add("notification-success");
            ControlHelper.showNotification(lblNotification, "Record Inserted");
        }
        else{
            lblNotification.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblNotification, "Record Failed");
            }
        } else{
            String query="UPDATE Category set CategoryName=?,Description=?, UpdatedBy=?,UpdatedAT=NOW() where CategoryID=?";
            operation.update(query, txtCategoryName.getText(),txtCategoryDescriptions.getText(),currentUser.getUserID(),Integer.parseInt(lblCategoryID.getText()));
            lblNotification.getStyleClass().add("notification-success");
            ControlHelper.showNotification(lblNotification, "Record Updated");
            ControlHelper.clearFaileds(lblCategoryID,txtCategoryName,txtCategoryDescriptions);
            btnSaveAndUpdate.setText("Save");
            lblCategoryID.setVisible(false);
            loadCategoryData();
        }
    }
    
    private void loadCategoryData(){
        String query="select * from category where DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        
        ObservableList<CategoryModel> categoryList=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            categoryList.add(new CategoryModel(
                    Integer.parseInt(row.get("CategoryID").toString()),
                    row.get("CategoryName").toString(),
                    row.get("Description").toString()));
        }
        ControlHelper.setColumnFactory(columnCategoryID, "id");
        ControlHelper.setColumnFactory(columnCategoryName, "name");
        ControlHelper.setColumnFactory(columnCategoryDescriptions, "descriptions");
        tblViewCategory.setItems(categoryList);
    }
    private void loadActionButtons(){
        columnCatAction.setCellFactory(cell->new TableCell<CategoryModel,Void>(){
            private final JFXButton update=new JFXButton("Update");
            private final JFXButton delete=new JFXButton("Delete");
            {
                update.getStyleClass().add("btn-update");
                update.setButtonType(JFXButton.ButtonType.RAISED);
                delete.getStyleClass().add("btn-delete");
                delete.setButtonType(JFXButton.ButtonType.RAISED);
                
                update.setOnAction(event->{
                    CategoryModel cat=getTableView().getItems().get(getIndex());
                    lblCategoryID.setVisible(true);
                    lblCategoryID.setText(String.valueOf(cat.getId()));
                    txtCategoryName.setText(cat.getName());
                    txtCategoryDescriptions.setText(cat.getDescriptions());
                    btnSaveAndUpdate.setText("Update");
                });
                
                delete.setOnAction(event->{
                    CategoryModel cat=getTableView().getItems().get(getIndex());
                    boolean confirm=ControlHelper.showAlertMessage("Are You Sure to Delete the Record", Alert.AlertType.CONFIRMATION);
                    if(confirm){
                        String query="update Category set DeletedAt=NOW() , DeletedBy=? WHERE CategoryID=?";
                        boolean success=operation.delete(query,currentUser.getUserID(),cat.getId());
                        if(success){
                            loadCategoryData();
                            getTableView().getItems().remove(cat);
                            lblNotification.getStyleClass().add("notification-success");
                            ControlHelper.showNotification(lblNotification, "Record Deleted");
                            loadCategoryData(); 
                        } else{
                            lblNotification.getStyleClass().add("notification-error");
                            ControlHelper.showNotification(lblNotification, "Deleted Failed");
                        }
                    }
                    
                });
                
            }
            
            @Override
            protected void updateItem(Void item,boolean empty){
                super.updateItem(item,empty);
                if(empty){
                    setGraphic(null);
                } else{
                    HBox box=new HBox(10,update,delete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
                
            }
        });
        
       
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCategoryData();
        loadActionButtons();
    }    
    
}
