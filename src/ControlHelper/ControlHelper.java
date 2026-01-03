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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 *
 * @author Rasookhan
 */
public class ControlHelper {
    private static boolean update=false;// when updating searching in combo box 
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
    
    // setting column factory for mulitple columns to be called at once
    public static <Model> void setColumnsFactory(Pair<? extends TableColumn<?,?>,String>...columns){
        for(Pair<? extends TableColumn<?,?>,String> pair:columns){
            pair.getKey().setCellValueFactory(new PropertyValueFactory<>(pair.getValue()));
        }
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
                combo.getSelectionModel().clearSelection();
                combo.getEditor().clear();
            }
            if(node instanceof ComboBox){
                ComboBox combo=(ComboBox) node;
                combo.getSelectionModel().clearSelection();
                combo.getEditor().clear();
            }
            if(node instanceof Label){
                Label lbl=(Label) node;
                lbl.setText("");
            }
        }
    }
    // Generic method to fill combo box with any data type not from database
    public static <T> void fillComboBox(ComboBox<T> comboBox,List<T> item){
        ObservableList<T> obList=FXCollections.observableArrayList(item);
        comboBox.setItems(obList);
    }
    
    // Generic  method to fil combo box with any data type from database
    public static <T> void fillComboBox(ComboBox<T> comboBox,String query,RowMapper<T> mapper,Function<T,String> displayText,Consumer<T> onSelect,Object...params){
        CRUDOperations operations=new CRUDOperations();
        List<Map<String,Object>> data=operations.retrieve(query, params);
        ObservableList<T> items=FXCollections.observableArrayList();
        
        for(Map<String,Object> row:data){
            T item = mapper.mapRow(row);
            if(item!=null){ // small change items changed to item
                items.add(item);
            }
        }
         comboBox.setItems(items);
        comboBox.setConverter(new StringConverter<T>(){
            @Override
            public String toString(T object) {
                   return object !=null ? displayText.apply(object) : "";
            }

            @Override
            public T fromString(String string) {
              return null;
            }
            
        });
       comboBox.valueProperty().addListener((obs,oldValue,newValue)->{
           if(newValue!=null && onSelect!=null)
               onSelect.accept(newValue);
       });
       
        
    }
    
    public interface RowMapper<T>{
        T mapRow(Map<String,Object> row);
    }
    // Searching in the combo boxes when the user type letter
    public static <T> void makeComboBoxSearchable(ComboBox<T> comboBox) {
    if (comboBox.getItems().isEmpty()) return; // prevent crash on empty combo

    ObservableList<T> originalItems = FXCollections.observableArrayList(comboBox.getItems());
    FilteredList<T> filteredItems = new FilteredList<>(originalItems, p -> true);
    comboBox.setEditable(true);

    comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
        if (update) return;
        update = true;

        String lower = newValue.toLowerCase();
        filteredItems.setPredicate(item -> item != null && item.toString().toLowerCase().contains(lower));

        ObservableList<T> newItems = FXCollections.observableArrayList(filteredItems);
        comboBox.setItems(newItems);

        if (!newItems.isEmpty()) {
            comboBox.show();
        } else {
            comboBox.hide();
        }
        update = false;
    });
    comboBox.focusedProperty().addListener((obs, oldValue, nowFocused) -> {
        if (update) return;
        update = true;

        if (!nowFocused) {
            String enteredText = comboBox.getEditor().getText();
            if (comboBox.getValue() == null || !comboBox.getValue().toString().equalsIgnoreCase(enteredText)) {
                boolean matchFound = originalItems.stream()
                        .anyMatch(item -> item.toString().equalsIgnoreCase(enteredText));

                if (matchFound) {
                    for (T item : originalItems) {
                        if (item.toString().equalsIgnoreCase(enteredText)) {
                            comboBox.setValue(item);
                            break;
                        }
                    }
                } else {
                    comboBox.getEditor().setText("");
                    comboBox.setValue(null);
                }
            }
            comboBox.setItems(originalItems);
        }
        update = false;
    });
}

     // Searching in the JFXcombo boxes when the user type letter
    public static <T> void makeJFXComboBoxSearchable(JFXComboBox<T> comboBox){
        if (comboBox.getItems().isEmpty()) return; // prevent crash on empty combo

    ObservableList<T> originalItems = FXCollections.observableArrayList(comboBox.getItems());
    FilteredList<T> filteredItems = new FilteredList<>(originalItems, p -> true);
    comboBox.setEditable(true);

    comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
        if (update) return;
        update = true;

        String lower = newValue.toLowerCase();
        filteredItems.setPredicate(item -> item != null && item.toString().toLowerCase().contains(lower));

        ObservableList<T> newItems = FXCollections.observableArrayList(filteredItems);
        comboBox.setItems(newItems);

        if (!newItems.isEmpty()) {
            comboBox.show();
        } else {
            comboBox.hide();
        }
        update = false;
    });
    comboBox.focusedProperty().addListener((obs, oldValue, nowFocused) -> {
        if (update) return;
        update = true;

        if (!nowFocused) {
            String enteredText = comboBox.getEditor().getText();
            if (comboBox.getValue() == null || !comboBox.getValue().toString().equalsIgnoreCase(enteredText)) {
                boolean matchFound = originalItems.stream()
                        .anyMatch(item -> item.toString().equalsIgnoreCase(enteredText));

                if (matchFound) {
                    for (T item : originalItems) {
                        if (item.toString().equalsIgnoreCase(enteredText)) {
                            comboBox.setValue(item);
                            break;
                        }
                    }
                } else {
                    comboBox.getEditor().setText("");
                    comboBox.setValue(null);
                }
            }
            comboBox.setItems(originalItems);
        }
        update = false;
    });
    }
    
    // Intege Binding for total Sum on lables 
    public static <T> IntegerBinding totalSum(ObservableList<T> list,ToIntFunction<T> valueExtractor){
        return Bindings.createIntegerBinding(()->list.stream().mapToInt(valueExtractor).sum(),list);
    }
    // Integer Binding for total Su on lables for the current item in the table
    public static <T> IntegerBinding totalSum(TableView<T> table,ToIntFunction<T> valueExtractor){
        return Bindings.createIntegerBinding(()->table.getItems().stream().mapToInt(valueExtractor).sum(),table.getItems());
    }
    //Object Binding for total Sum on lables
    public static<T> ObjectBinding<BigDecimal> totalSum(ObservableList<T> list,Function<T,BigDecimal> valueExtractor){
        return Bindings.createObjectBinding(()->list.stream().map(valueExtractor).reduce(BigDecimal.ZERO,BigDecimal::add),list);
    }
    //Object Binding for total Sum on lables for the current item in the table
    public static<T> ObjectBinding<BigDecimal> totalSum(TableView<T> table,Function<T,BigDecimal> valueExtratctor){
        return Bindings.createObjectBinding(()->table.getItems().stream().map(valueExtratctor).reduce(BigDecimal.ZERO, BigDecimal::add),table.getItems());
    }
    
    // Generic method for exporting table view data into microsoft excel
    public static <T> void exportToExcel(TableView<T> tableView, Stage stage) {
    // Opening file chooser
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Excel file");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
    File file = fileChooser.showSaveDialog(stage);

    if (file != null) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Sheet1");

            // Creating header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < tableView.getColumns().size(); col++) {
                TableColumn<T, ?> column = tableView.getColumns().get(col);
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(column.getText());
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            for (int rowIndex = 0; rowIndex < tableView.getItems().size(); rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                T item = tableView.getItems().get(rowIndex);

                for (int col = 0; col < tableView.getColumns().size(); col++) {
                    TableColumn<T, ?> column = tableView.getColumns().get(col);
                    Object cellValue = column.getCellData(item);
                    Cell cell = row.createCell(col);

                    if (cellValue != null) {
                        if (cellValue instanceof Number) {
                            cell.setCellValue(((Number) cellValue).doubleValue());
                        } else if (cellValue instanceof Date) {
                            CellStyle dateStyle = workbook.createCellStyle();
                            CreationHelper createHelper = workbook.getCreationHelper();
                            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
                            cell.setCellStyle(dateStyle);
                            cell.setCellValue((Date) cellValue);
                        } else {
                            cell.setCellValue(cellValue.toString());
                        }
                    } else {
                        cell.setCellValue("");
                    }
                }
            }

            // Auto size columns
            for (int col = 0; col < tableView.getColumns().size(); col++) {
                sheet.autoSizeColumn(col);
            }

            // Writing workbook to file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }

            System.out.println("Excel file exported successfully: " + file.getAbsolutePath());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
 
}
