/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataValidations;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javax.mail.internet.InternetAddress;
/**
 *
 * @author Rasookhan
 */
public class TextFieldValidations {
   
    public static boolean isTextFieldNotEmpty(Object... nodes){
        boolean allValid=true;
        
        for(Object node:nodes){
          if(node instanceof TextInputControl) {
              TextInputControl field=(TextInputControl) node;
              if(field.getText()==null || field.getText().trim().isEmpty()){
                  field.setStyle("-fx-border-color:red;");
                  allValid=false;
              }else{
                  field.setStyle(null);
              }
          }
          else if(node instanceof ComboBox){
              ComboBox<?> combo=(ComboBox<?>) node;
              if(combo.getValue()==null || combo.getValue().toString().trim().isEmpty()){
                  combo.setStyle("-fx-border-color:red;");
                  allValid=false;
              } else{
                  combo.setStyle(null);
              }
          }
        }
        return allValid;
    }  
    
    public static boolean validateNoNumber(TextField...txt){
        for(TextField fields:txt){
            String check=fields.getText();
            if(check.matches(".*\\d.*")){
                fields.setStyle("-fx-border-color:red;");
                return false;
            }
            
        }
        return true;
        
    }
    
    // Validating Email Fields
    public static boolean validateEmail(TextField...txt){
        boolean isValid=true;
        for(TextField field:txt){
            String email=field.getText().trim();
            if(!isValidEmail(email)){
                field.setStyle("-fx-border-color:red;");
                isValid=false;
            }else{
                field.setStyle(null);
            }
        }
        return isValid;
    }
    private static boolean isValidEmail(String email){
        try{
            if(email.isEmpty())
                return false;
            InternetAddress emailAddr=new InternetAddress(email);
            emailAddr.validate();
            return true;
        }
        catch(Exception ex){
            return false;
        }
    }
    
    // Validating Phone Number
    public static boolean isPhoneNumber(TextField txt){
        String phone=txt.getText().trim();
        
        String regex="^[0-9]{10}$";
        if(phone.matches(regex)){
            txt.setStyle(null);
            return true;
        }
        else{
            txt.setStyle("-fx-border-color:red;");
            return false;
        }
    }
}
