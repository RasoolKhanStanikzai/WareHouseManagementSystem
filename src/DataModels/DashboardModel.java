/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Rasookhan
 */
public class DashboardModel {
    private static final DashboardModel instance=new DashboardModel();
    
    public static DashboardModel getInstance(){
        return instance;
    }
    private final IntegerProperty customerCount=new SimpleIntegerProperty(0);
    private final IntegerProperty productCount=new SimpleIntegerProperty(0);
    private final IntegerProperty supplierCount=new SimpleIntegerProperty(0);
    private final IntegerProperty stockCount=new SimpleIntegerProperty(0);
    private final IntegerProperty purchaseCount=new SimpleIntegerProperty(0);
    public IntegerProperty customerCountProperty(){
        return customerCount;
    }
    
    public void setCustomerCount(int count){
        customerCount.set(count);
    }
    
    public IntegerProperty productCountProperty(){
        return productCount;
    }
    
    public void setProductCount(int count){
        productCount.set(count);
    }
    
    public IntegerProperty supplierCountProperty(){
        return supplierCount;
    }
    public void setSupplierCount(int count){
        supplierCount.set(count);
    }
    
    public void setStockCount(int count){
        stockCount.set(count);
    }
    public IntegerProperty stockCountProperty(){
        return stockCount;
    }
    
    public void setPurchaseCount(int count){
        purchaseCount.set(count);
    }
    public IntegerProperty purchaseCountProperty(){
        return purchaseCount;
    }
}
