
package CurrencyConverter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ControlHelper.ControlHelper;
import DataModels.CountryItemModel;
import DataModels.CurrencyModel;
import DataModels.Session;
import DataModels.UserModel;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
import DataValidations.TextFieldValidations;
import DatabaseOperations.CRUDOperations;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

public class CurrencyConverterController implements Initializable {
    CurrencyModel currencyModel;
    CRUDOperations operations=new CRUDOperations();
    UserModel currentUser=Session.getCurrentUser();
    @FXML
    private JFXComboBox<String> comboBoxBaseAddress;
    @FXML
    private JFXComboBox<String> comboBoxTargetAddress;
    @FXML
    private Label lblCurrencyRate;
    
    // Using a different free API
    private static final String SYMBOLS_URL = "https://open.er-api.com/v6/latest/USD";
    private static final String CONVERT_URL = "https://open.er-api.com/v6/latest/";
    
    private String latestConvertedValue;
    
    @FXML
    private TextField txtAmount;
    @FXML
    private JFXButton btnGetRate;
    
    // Common currency codes as fallback
   private static final String[] COMMON_CURRENCIES = {
        "USD","AFN", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", 
        "INR", "BRL", "RUB", "MXN", "KRW", "TRY", "SAR", "AED"
    };
    @FXML
    private Label lblNotification;
    @FXML
    private Tab tabInternationExchange;
    @FXML
    private Tab tabLocalExchange;
    @FXML
    private AnchorPane anchorPaneLocalExchange;
    @FXML
    private Label lblSSMessage;
    @FXML
    private JFXComboBox<String> comboManuallBaseAdd;
    @FXML
    private Label lblManullCurrencyRate;
    @FXML
    private JFXComboBox<String> comboManuallTargetAdd;
    @FXML
    private TextField txtManuallExchangeRate;
    @FXML
    private TextField txtManuallAmount;
    @FXML
    private JFXButton btnGetManuallRate;
    @FXML
    private TextField txtCode;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSymbol;
    @FXML
    private ComboBox<CountryItemModel> comboCountry;
    @FXML
    private TextField txtDP;
    @FXML
    private ComboBox<String> comboStatus;
    @FXML
    private TextField txtExchangeRate;
    @FXML
    private TableView<CurrencyModel> tblViewCurrency;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colCode;
    @FXML
    private TableColumn<?, ?> colName;
    @FXML
    private TableColumn<?, ?> colSymbol;
    @FXML
    private TableColumn<?, ?> colCountry;
    @FXML
    private TableColumn<?, ?> colDP;
    @FXML
    private TableColumn<?, ?> colStatus;
    @FXML
    private TableColumn<?, ?> colERate;
    @FXML
    private TableColumn<?, ?> colDate;
    @FXML
    private TableColumn<CurrencyModel, Void> colAction;
    
    
    private void fetchSymbols() {
        Task<Void> task = new Task<Void>() {
            @Override
    protected Void call() throws Exception {
        try {
            URL url = new URL(SYMBOLS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                
            int code = conn.getResponseCode();
            if (code != 200) {
                // If API fails, use fallback currencies
                useFallbackCurrencies();
                return null;
            }
            
            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                
                if (root.has("result") && root.get("result").getAsString().equals("success")) {
                    JsonObject rates = root.getAsJsonObject("rates");
                    List<String> currencyCodes = new ArrayList<>();
                    
                    for (Map.Entry<String, JsonElement> e : rates.entrySet()) {
                        currencyCodes.add(e.getKey());
                    }
                    Collections.sort(currencyCodes);
                    
                    Platform.runLater(() -> {
                        updateCurrencyBoxes(currencyCodes);
                    });
                } else {
                    useFallbackCurrencies();
                }
            }
        } catch (Exception e) {
            useFallbackCurrencies();
        }
        return null;
    }
        };
        
        task.setOnFailed(e -> Platform.runLater(() -> {
            useFallbackCurrencies();
        }));
        
        new Thread(task).start();
    }
    
    private void useFallbackCurrencies() {
        Platform.runLater(() -> {
            List<String> fallbackCurrencies = new ArrayList<>();
            for (String currency : COMMON_CURRENCIES) {
                fallbackCurrencies.add(currency);
            }
            Collections.sort(fallbackCurrencies);
            updateCurrencyBoxes(fallbackCurrencies);
            lblCurrencyRate.setText("Using fallback currencies (No Internet Connection)");
        });
    }
    
    private void updateCurrencyBoxes(List<String> currencies) {
        comboBoxBaseAddress.getItems().clear();
        comboBoxTargetAddress.getItems().clear();
        comboBoxBaseAddress.getItems().addAll(currencies);
        comboBoxTargetAddress.getItems().addAll(currencies);
        comboBoxBaseAddress.setValue("USD");
        comboBoxTargetAddress.setValue("AFN");
        lblCurrencyRate.setText("Ready");
    }
    
    @FXML
    private void onConvert() {
        String from = comboBoxBaseAddress.getValue();
        String to = comboBoxTargetAddress.getValue();
        String amountStr = txtAmount.getText().trim();
        
        if (from == null || to == null || amountStr.isEmpty()) {
            ControlHelper.showNotification(lblNotification, "Select both currencies and enter an amount.");
            lblNotification.getStyleClass().add("notification-info");
            return;
        }
        
        if (from.equals(to)) {
            ControlHelper.showNotification(lblNotification, "Currencies cannot be the same.");
            lblNotification.getStyleClass().add("notification-info");
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                ControlHelper.showNotification(lblNotification, "Amount must be positive.");
                lblNotification.getStyleClass().add("notification-info");
                return;
            }
        } catch (NumberFormatException ex) {
             ControlHelper.showNotification(lblNotification, "Enter a valid number.");
             lblNotification.getStyleClass().add("notification-info");
            return;
        }
        
        btnGetRate.setDisable(true);
        lblCurrencyRate.setText("Converting...");
        
        Task<Double> task = new Task<Double>() {
            @Override
    protected Double call() throws Exception {
        try {
            String query = CONVERT_URL + from;
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            int code = conn.getResponseCode();
            if (code != 200) throw new IOException("HTTP: " + code);
            
            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                
                if (root.has("result") && root.get("result").getAsString().equals("success")) {
                    JsonObject rates = root.getAsJsonObject("rates");
                    if (rates.has(to)) {
                        double rate = rates.get(to).getAsDouble();
                        return amount * rate;
                    } else {
                        throw new IOException("Target currency not found: " + to);
                    }
                } else {
                    throw new IOException("API returned failure: " + root.toString());
                }
            }
        } catch (Exception e) {
            throw new IOException("Conversion failed: " + e.getMessage(), e);
        }
    }
        };
        
        task.setOnSucceeded(e -> {
            Double result = task.getValue();
            lblCurrencyRate.setText(String.format("%.2f %s = %.2f %s", 
                    amount, from, result, to));
            btnGetRate.setDisable(false);
        });
        
        task.setOnFailed(e -> {
            lblCurrencyRate.setText("Conversion failed: " + task.getException().getMessage());
            btnGetRate.setDisable(false);
        });
        
        new Thread(task).start();
    }
    
    private void loadSaraiShahzadaRate() {
    WebView webView = new WebView();
    WebEngine engine = webView.getEngine();
    engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
        if (newState == Worker.State.SUCCEEDED) {
            anchorPaneLocalExchange.getChildren().clear();
            anchorPaneLocalExchange.getChildren().add(webView);

            JSObject window = (JSObject) engine.executeScript("window");
            window.setMember("appBridge", this);

            String script =
                "(function() {"
                + "function safeCall(val){try{if(window.appBridge && typeof window.appBridge.onConverted==='function'){window.appBridge.onConverted(val);}}catch(e){console.log(e);}}"
                + "function getTargetValue(){var t=document.querySelector('input.to');if(!t)return null;var v=(typeof t.value!=='undefined' && t.value!=='')?t.value:(t.textContent||t.innerText||'');return v?v.toString().trim():null;}"
                + "function updateConversion(){var val=getTargetValue();if(val!==null){console.log('Converted value:'+val);safeCall(val);}}"
                + "var targetInput=document.querySelector('input.to');if(targetInput){targetInput.addEventListener('input',updateConversion);try{var observer=new MutationObserver(function(m){updateConversion();});observer.observe(targetInput,{attributes:true,attributeFilter:['value']});}catch(e){console.log(e);}}"
                + "var sourceSelect=document.querySelector('select.source-currency');if(sourceSelect){sourceSelect.addEventListener('change',function(){setTimeout(updateConversion,500);});}"
                + "var sourceInput=document.querySelector('input.from');if(sourceInput){sourceInput.addEventListener('input',updateConversion);}"
                + "var radioButtons=document.querySelectorAll('input.rateType');if(radioButtons && radioButtons.forEach){radioButtons.forEach(function(r){r.addEventListener('change',function(){setTimeout(updateConversion,150);});});}"
                + "setTimeout(updateConversion,600);"
                + "})();";

            try {
                engine.executeScript(script);
            } catch (JSException ex) {
                ex.printStackTrace();
                lblSSMessage.setVisible(true);
                lblSSMessage.setText("JS injection failed");
            }
        } else if (newState == Worker.State.FAILED) {
            lblSSMessage.setVisible(true);
            lblSSMessage.setText("No internet connection");
        }
    });

    engine.load("https://sarafi.af/fa/exchange-rates/sarai-shahzada");
}

// make sure this is public
public void onConverted(String value) {
    Platform.runLater(() -> {
        latestConvertedValue = value;
        lblSSMessage.setText("Total: " + latestConvertedValue);

        try {
            String numericValue = value.replaceAll("[^\\d.]", "");
            if (!numericValue.isEmpty()) {
                double total = Double.parseDouble(numericValue);
                System.out.println("Numeric Total: " + total);
            }
        } catch (NumberFormatException e) {
            System.out.println("Could not parse value: " + value);
        }
    });
}


    @FXML
    private void manuallConverter(){
    
    double exchangeRate=Double.parseDouble(txtManuallExchangeRate.getText());
    double amount=Double.parseDouble(txtManuallAmount.getText());
    double totalAmount=amount*exchangeRate;
    lblManullCurrencyRate.setText("Total Amount is: "+totalAmount);
}
    private void loadCountries(){
        Set<CountryItemModel>countrySet=new TreeSet<>((c1,c2)->c1.getName().compareTo(c2.getName()));
        for(String iso:Locale.getISOCountries()){
            Locale locale=new Locale("",iso);
            CountryItemModel item=new CountryItemModel(locale);
            if(item.getCurrency()!=null)
                countrySet.add(item);
        }
        comboCountry.setItems(FXCollections.observableArrayList(countrySet));
        comboCountry.setPromptText("Selct Country");
        ControlHelper.makeComboBoxSearchable(comboCountry);
        
        comboCountry.setOnAction(event->{
            CountryItemModel selected=comboCountry.getValue() instanceof CountryItemModel
                    ? (CountryItemModel) comboCountry.getValue() : null;
           
            if(selected!=null && selected.getCurrency() !=null){
                txtSymbol.setText(selected.getCurrency().getSymbol(selected.getLocale()));  
            }   
        });
        
    }
    
    @FXML
    public void saveCurrency(){
        
        String query="insert into Currency(Code,Name,Symbol,Country,DecimalPlaces,Status,Rate,CreatedBy)"
                + "values(?,?,?,?,?,?,?,?)";
        boolean isEmpty=TextFieldValidations.isTextFieldNotEmpty(txtCode,txtName,txtSymbol,comboCountry,txtDP,comboStatus);
        boolean isInserted=operations.insert(query, txtCode.getText(),txtName.getText(),
                txtSymbol.getText(),comboCountry.getValue(),txtDP.getText(),
                comboStatus.getValue(),txtExchangeRate.getText(),currentUser.getUserID());
        if(!isEmpty){
            lblNotification.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblNotification, "Required!");
        }
        if(isInserted){
            lblNotification.getStyleClass().add("notification-success");
            ControlHelper.showNotification(lblNotification, "Record Saved");
            ControlHelper.clearFaileds(txtCode,txtName,txtSymbol,
                    comboCountry,txtDP,comboStatus,txtExchangeRate); 
            loadCurrency();
            
        } else{
            lblNotification.getStyleClass().add("notification-error");
            ControlHelper.showNotification(lblNotification, "Record Failed");
        }
    }
    private void loadCurrencyStatus(){
        ObservableList<String> status=FXCollections.observableArrayList("Active","In-Active");
        comboStatus.setItems(status);
        ControlHelper.makeComboBoxSearchable(comboStatus);
        
    }
    // Loading currency data into table view
    private void loadCurrency(){
        String query="select CurrencyID,code,name,symbol,country,decimalplaces,status,rate from currency";
        List<Map<String,Object>> data=operations.retrieve(query);
        
        ObservableList<CurrencyModel> currencyList=FXCollections.observableArrayList();
        for(Map<String,Object> row:data){
            currencyList.add(new CurrencyModel(Integer.parseInt(row.get("CurrencyID").toString()),
                    row.get("code").toString(),
                    row.get("name").toString(),
                    row.get("symbol").toString(),
                    row.get("country").toString(),
                    Double.parseDouble(row.get("decimalplaces").toString()),
                    row.get("status").toString(),
                    Double.parseDouble(row.get("rate").toString())));
        }
        ControlHelper.setColumnsFactory(new Pair<>(colID,"id"),
                new Pair<>(colCode,"code"),
                new Pair<>(colName,"name"),
                new Pair<>(colSymbol,"symbol"),
                new Pair<>(colCountry,"country"),
                new Pair<>(colDP,"dp"),
                new Pair<>(colStatus,"status"),
                new Pair<>(colERate,"rate")
        );
        tblViewCurrency.setItems(currencyList);
        
    }
    
    private void loadActionButtons(){
        colAction.setCellFactory(col->new TableCell<CurrencyModel,Void>(){
           private final JFXButton updateBtn=new JFXButton("Update");
           private final JFXButton deleteBtn=new JFXButton("Delete");
                {
                    updateBtn.setButtonType(JFXButton.ButtonType.RAISED);
                    updateBtn.getStyleClass().add("btn-update");
                    deleteBtn.setButtonType(JFXButton.ButtonType.RAISED);
                    deleteBtn.getStyleClass().add("btn-delete");
                    
                    
                    updateBtn.setOnAction(event->{
                        CurrencyModel currency=getTableView().getItems().get(getIndex());
                        txtCode.setText(currency.getCode());
                        txtName.setText(currency.getName());
                        txtSymbol.setText(currency.getSymbol());
                        txtSymbol.setDisable(true);
                        comboCountry.getEditor().setText(currency.getCountry());
                        comboStatus.getEditor().setText(currency.getStatus());
                        txtDP.setText(String.valueOf(currency.getDp()));
                        txtExchangeRate.setText(String.valueOf(currency.getRate()));
                        
                    });
                }
           @Override
            protected void updateItem(Void item,boolean empty){
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
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fetchSymbols();
        loadSaraiShahzadaRate();  
        comboManuallBaseAdd.getItems().clear();
        comboManuallTargetAdd.getItems().clear();
        comboManuallBaseAdd.getItems().addAll(COMMON_CURRENCIES);
        comboManuallTargetAdd.getItems().addAll(COMMON_CURRENCIES);
        loadCountries();
        loadCurrencyStatus();
        loadCurrency();
        loadActionButtons();
    }    
}