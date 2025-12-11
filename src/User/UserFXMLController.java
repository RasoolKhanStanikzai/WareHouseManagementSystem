/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package User;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import DatabaseOperations.CRUDOperations;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import DataValidations.TextFieldValidations;
import javafx.scene.control.Label;
import ControlHelper.ControlHelper;
import DataModels.Session;
import DataModels.UserModel;
import java.nio.file.Files;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author Rasookhan
 */
public class UserFXMLController implements Initializable {
    CRUDOperations operation=new CRUDOperations();
    UserModel currentUser=Session.getCurrentUser();
    File file=null;
    private static final int ITERATIONS=65536;
    private static final int KEY_LENGTH=256;
    ObservableList<String> role=FXCollections.observableArrayList("Admin");
    @FXML
    private TextField txtUserName;
    @FXML
    private TextField txtUserFullName;
    @FXML
    private PasswordField txtUserPassword;
    @FXML
    private ComboBox<String> txtUserRole;
    @FXML
    private TextField txtUserEmail;
    @FXML
    private ImageView imageViewUser;
    @FXML
    private JFXButton btnSaveUser;

    /**
     * Initializes the controller class.
     */
    // Generate a random salt
    private static String getSalt(){
        SecureRandom sr=new SecureRandom();
        byte[] salt=new byte[16];
        sr.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    // Hash password with salt
    private static String hashPassword(String password,String salt)throws NoSuchAlgorithmException,InvalidKeySpecException{
        PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),Base64.getDecoder().decode(salt),ITERATIONS,KEY_LENGTH);
        SecretKeyFactory skf=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash=skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }
    //Verify password
    private static boolean verifyPassword(String enteredPassword,String storedHash,String storedSalt)throws NoSuchAlgorithmException, InvalidKeySpecException{
        String hashedEntered=hashPassword(enteredPassword,storedSalt);
        return hashedEntered.equals(storedHash);
    }
    @FXML
    private JFXButton btnBrowse;
    @FXML
    private Label lblNotification;
    @FXML
    private void saveUser() throws Exception{
        
       boolean isValid=TextFieldValidations.isTextFieldNotEmpty(txtUserName,txtUserFullName,txtUserPassword,txtUserRole,txtUserEmail);
       if(!isValid){
           lblNotification.getStyleClass().add("notification-warnning");
           ControlHelper.showNotification(lblNotification, "Required");
           return ;
       }
       byte[] imageBytes=Files.readAllBytes(file.toPath());
       String salt=getSalt();
       String passwordHash=hashPassword(txtUserPassword.getText(),salt);
       String query="insert into User(UserName,PasswordHash,Salt,Role,FullName,Email,Photo,CreatedBy)values(?,?,?,?,?,?,?,?)";
       boolean isInserted= operation.insert(query,txtUserName.getText(),passwordHash,salt,txtUserRole.getValue()
               ,txtUserFullName.getText(),txtUserEmail.getText(),imageBytes,currentUser.getUserFullName());
       if(isInserted){
           lblNotification.getStyleClass().add("notification-success");
           ControlHelper.showNotification(lblNotification, "Record Inserted");
       }else{
           lblNotification.getStyleClass().add("notification-error");
           ControlHelper.showNotification(lblNotification, "Record Failed");;
       }
    }
    
    @FXML
    private void browseImage(){
        
        FileChooser chooser=new FileChooser();
        chooser.setTitle("Selct image");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image files", "*.png","*.jpg","*.jpeg","*.gif"));
        
        Stage stage=(Stage) btnBrowse.getScene().getWindow();
        file=chooser.showOpenDialog(stage);
        if(file!=null){
            Image image=new Image(file.toURI().toString());
            imageViewUser.setImage(image);
        }
    }
    private void openFullImage(){
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        ImageView fullImage=new ImageView(imageViewUser.getImage());
        fullImage.setPreserveRatio(true);
        fullImage.setFitWidth(600);
        fullImage.setFitHeight(600);
        
        StackPane pane=new StackPane(fullImage);
        pane.setStyle("-fx-background-color:rgba(0,0,0,0.8)");
        Scene scene=new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Profile Image");
        stage.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        txtUserRole.setItems(role);
        double radius=75;
        Circle circle=new Circle(radius,radius,radius);
        imageViewUser.setClip(circle);
        imageViewUser.setOnMouseClicked(event->openFullImage());
    }    
    
}
