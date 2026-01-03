/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Category.ViewCategory;

import DataModels.CategoryModel;
import DatabaseOperations.CRUDOperations;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ControlHelper.ControlHelper;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class ViewCategoryFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    @FXML
    private TableView<CategoryModel> tblViewCategory;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colName;
    @FXML
    private TableColumn<?, ?> colDescriptions;
    @FXML
    private TableColumn<?, ?> colDate;
    @FXML
    private TextField txtSearch;
    @FXML
    private JFXButton btnExportToExcel;

    private void loadCategory(){
        String query="select * from Category where DeletedAt IS NULL";
        List<Map<String,Object>> data=operation.retrieve(query);
        ObservableList<CategoryModel> categoryList=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            java.sql.Timestamp st=(java.sql.Timestamp) row.get("CreatedAt");
            LocalDateTime createdAt=st.toLocalDateTime();
            categoryList.add(new CategoryModel(Integer.parseInt(row.get("CategoryID").toString()),
            row.get("CategoryName").toString(),
            row.get("Description").toString(),
            createdAt));
        }
        tblViewCategory.setItems(categoryList);
     ControlHelper.setColumnsFactory(new Pair<>(colID,"id"),
             new Pair<>(colName,"name"),
             new Pair<>(colDescriptions,"descriptions"),
             new Pair<>(colDate,"createdAt"));
     ControlHelper.enableUniversalSearch(txtSearch, tblViewCategory, categoryList);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       loadCategory();
       btnExportToExcel.setOnAction(event->{
           ControlHelper.exportToExcel(tblViewCategory, (Stage)tblViewCategory.getScene().getWindow());
       });
    }    
    
}
