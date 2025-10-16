/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ControlHelper;

import com.jfoenix.controls.JFXComboBox;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import DatabaseOperations.CRUDOperations;
import java.sql.*;
import java.util.Map;

/**
 *
 * @author Rasookhan
 */
public class ControlHelper {
    public static void showNotification(Label label,String message){
        label.setVisible(true);
        label.setText(message);
        PauseTransition pauseTransition=new PauseTransition(Duration.seconds(3));
        pauseTransition.setOnFinished(event->label.setVisible(false));
        pauseTransition.play();
    }
    
    public static <S,T> void setColumnFactory(TableColumn<S,T> columnName,String propertyName){
        columnName.setCellValueFactory(new PropertyValueFactory<>(propertyName));
    }
    
    // Alert Message
    public static boolean showAlertMessage(String message, Alert.AlertType type){
        Alert alert=new Alert(type,message,ButtonType.YES,ButtonType.NO);
        alert.setTitle(type==Alert.AlertType.CONFIRMATION ? "Confirmation" : "Message");
        alert.setHeaderText("Warehouse Managemenet System");
        return alert.showAndWait().filter(response->response==ButtonType.YES).isPresent();
    }
    
    // Searching method for any thpe of column
    public static <T> void enableUniversalSearch(TextField searchField,TableView<T> tableView,ObservableList<T> originalData){
        FilteredList<T> filterData=new FilteredList<>(originalData,p->true);
        searchField.textProperty().addListener((obs,oldVal,newVal)->{
            String lowerCaseFilter=(newVal==null)? "" : newVal.toLowerCase();
            filterData.setPredicate(item->{
                if(lowerCaseFilter.isEmpty()){
                    return true;
                }
                for(TableColumn<T,?> col: tableView.getColumns()){
                   Object cellValue=(col.getCellObservableValue(item)!=null) ? col.getCellObservableValue(item).getValue():null;
                   if(cellValue!=null && cellValue.toString().toLowerCase().contains(lowerCaseFilter)){
                       return true;
                   }
                }
                return false;
            });
        });
        // Keep sorting functionality intact
        SortedList<T> sortedData=new SortedList<>(filterData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }
    
    public static void clearFaileds(Object...params){
        for(Object node:params){
            if(node instanceof TextInputControl){
                TextInputControl cont=(TextInputControl) node;
                cont.setText("");
            }
            if(node instanceof JFXComboBox){
                JFXComboBox combo=(JFXComboBox) node;
                combo.getTooltip().getText();
            }
        }
    }
    // Generic method to fill combo box with any data type not from database
    public static <T> void fillComboBox(ComboBox<T> comboBox,List<T> item){
        ObservableList<T> obList=FXCollections.observableArrayList(item);
        comboBox.setItems(obList);
    }
    
    // Generic  method to fil combo box with any data type from database
    public static <T> void fillComboBox(ComboBox<T> comboBox,String query,RowMapper<T> mapper,Object...params){
        CRUDOperations operations=new CRUDOperations();
        List<Map<String,Object>> data=operations.retrieve(query, params);
        ObservableList<T> items=FXCollections.observableArrayList();
        
        for(Map<String,Object> row:data){
            T item = mapper.mapRow(row);
            if(items!=null){
                items.add(item);
            }
        }
        comboBox.setItems(items);
    }
    
    
    public interface RowMapper<T>{
        T mapRow(Map<String,Object> row);
    }
}
