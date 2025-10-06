/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package warehousemanagementsystem;

import DataModels.DashboardModel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import DataModels.UserModel;
import DataModels.Session;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author Rasookhan
 */
public class FXMLDocumentController implements Initializable {
    UserModel currentUser=Session.getCurrentUser();
    

    @FXML
    private JFXHamburger hamburger;
    @FXML
    private VBox sidebar;
    private boolean sidebarVisible=true;
    private double sidebarWidth;
    private Node dashboardCenterCentents;
    private Map<String,Parent> pages=new HashMap<>();
    private Map<String,Object> controllers=new HashMap<>();
    
    private DashboardModel dashboardModel;
    @FXML
    private JFXButton dashboardBtnCustomer;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private VBox centerContentVBOX;
    @FXML
    private Label lblCustomerCount;
    @FXML
    private JFXButton dashboardBtnCConverter;
    @FXML
    private Label lblLoggedUser;
    @FXML
    private ImageView userImageView;
    public void hamburgerAnitmation(){
        sidebarWidth=sidebar.getPrefWidth();
        HamburgerBackArrowBasicTransition burgerTask=new HamburgerBackArrowBasicTransition(hamburger);
        burgerTask.setRate(-1);
        hamburger.setOnMouseClicked(event->{
            burgerTask.setRate(burgerTask.getRate()*-1);
            burgerTask.play();
            
            toggleSidebar();
        });
    }
    
    private void toggleSidebar(){
       
        Timeline timeline=new Timeline();
        if(sidebarVisible){
            KeyValue kv=new KeyValue(sidebar.prefWidthProperty(),0);
            KeyFrame kf=new KeyFrame(Duration.millis(300),kv);
            timeline.getKeyFrames().add(kf);
            
            for(Node child:sidebar.getChildren()){
                FadeTransition fade=new FadeTransition(Duration.millis(300),child);
                fade.setFromValue(1.0);
                fade.setToValue(0.0);
                fade.play();
                fade.setOnFinished(event->child.setManaged(false));
                
                
            }   
            timeline.play();
            sidebarVisible=false; 
        }
        else{
            for(Node child:sidebar.getChildren()){
                child.setManaged(true);
                FadeTransition fade=new FadeTransition(Duration.millis(300),child);
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                fade.play();
            }
            KeyValue kv=new KeyValue(sidebar.prefWidthProperty(),sidebarWidth);
            KeyFrame kf=new KeyFrame(Duration.millis(300),kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
            sidebarVisible=true;
            
        }
    }
   
  
    @FXML
    private void loadCustomerFXML(){
        loadPage("Customer");
    }
    @FXML
    private void loadCurrencyConverter(){
        loadPage("CurrencyConverter");
    }
    @FXML
    private void loadCategory(){
        loadPage("CategoryFXML");
    }
    @FXML
    private void loadDashbaordFXML(){
        centerContentVBOX.getChildren().clear();
        centerContentVBOX.getChildren().add(dashboardCenterCentents);
    }
    @FXML
    private void loadUserFXML(){
        loadPage("UserFXML");
    }
    private void loadProductFXML(){
        loadPage("ProductFXML");
    }
   private void preloadPage(String name,String fxmlPath){
        try{
        FXMLLoader loader=new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root=loader.load();
        pages.put(name, root);
        controllers.put(name, loader.getController());
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }
   private void loadPage(String name){
       centerContentVBOX.getChildren().clear();
       centerContentVBOX.getChildren().add(pages.get(name));
   }
   
    @FXML
   private void handleLogOut()throws IOException{
       Session.setCurrentUser(null);
       Stage currentStage=(Stage)lblLoggedUser.getScene().getWindow();
       currentStage.close();
       FXMLLoader loader=new FXMLLoader(getClass().getResource("/UserLogin/UserLoginFXML.fxml"));
       Parent root=loader.load();
       Stage stage=new Stage();
       Scene scene=new Scene(root);
       stage.setScene(scene);
       stage.show();
       
       
       
   }
  private void loadUserLoginFXML(){
      loadPage("UserLoginFXML"); 
  }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preloadPage("Customer","/Customer/CustomerFXML.fxml");
        preloadPage("CurrencyConverter","/CurrencyConverter/CurrencyConverter.fxml");
        preloadPage("CategoryFXML","/Category/CategoryFXML.fxml");
        preloadPage("UserFXML","/User/UserFXML.fxml");
        preloadPage("UserLoginFXML","/UserLogin/UserLoginFXML.fxml");
        preloadPage("ProductFXML","/Product/ProductFXML.fxml");
        hamburgerAnitmation();
        dashboardCenterCentents=centerContentVBOX.getChildren().get(0);
        lblCustomerCount.textProperty().bind(DashboardModel.getInstance().customerCountProperty().asString());
        
       
       if(currentUser!=null){
           lblLoggedUser.setText("Welcome, "+currentUser.getUserFullName());
       } 
    }    
    
}
