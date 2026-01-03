/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleModel {
    private int saleId;
    private int invoiceId;
    private String customerName;
    private String productName;
    private BigDecimal quantity;
    private int purchasePrice;
    private BigDecimal salePrice;
    private String currencyName;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updateAt;
    private String updateBy;
    private LocalDateTime deletedAt;
    private String deletedBy;

    public SaleModel(int saleId,int invoiceId, String customerName, String productName, BigDecimal quantity, int purchasePrice, BigDecimal salePrice, String currencyName, LocalDateTime createdAt) {
        this.saleId = saleId;
        this.invoiceId=invoiceId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.currencyName = currencyName;
        this.createdAt = createdAt;
    }

    public SaleModel(int saleId,int invoiceId, String customerName, String productName, BigDecimal quantity, int purchasePrice, BigDecimal salePrice, String currencyName, LocalDateTime createdAt, String createdBy, LocalDateTime updateAt, String updateBy, LocalDateTime deletedAt, String deletedBy) {
        this.saleId = saleId;
        this.invoiceId=invoiceId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.currencyName = currencyName;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updateAt = updateAt;
        this.updateBy = updateBy;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(int purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
    // getter method for calcuating the totalPrice
    public BigDecimal getTotalPrice(){
        if(quantity==null || salePrice==null){
            return BigDecimal.ZERO;
        }
        return quantity.multiply(salePrice);
    }
}