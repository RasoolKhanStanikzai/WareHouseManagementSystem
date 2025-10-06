/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ControlHelper;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

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
}
