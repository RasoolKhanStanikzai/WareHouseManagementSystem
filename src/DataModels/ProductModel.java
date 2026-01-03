/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

import java.time.LocalDateTime;

/**
 *
 * @author Rasookhan
 */
public class ProductModel {
    private int id;
    private String name;
    private String category;
    private String unit;
    private int costPrice,salePrice;
    private String currency;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updateBy;
    private LocalDateTime updatedAt;
    private String deletedBy;
    private LocalDateTime deletedAt;

    public ProductModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    
    public ProductModel(int id, String name, String category, String unit, int costPrice, int salePrice, String currency) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.currency = currency;
    }
     public ProductModel(int id, String name, String category, String unit, int costPrice,  String currency,LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.costPrice = costPrice;
        this.currency = currency;
        this.createdAt=createdAt;
    }

    public ProductModel(int id, String name, String category, String unit, int costPrice, int salePrice, String currency, String createdBy, LocalDateTime createdAt, String updateBy, LocalDateTime updatedAt, String deletedBy, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.currency = currency;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updateBy = updateBy;
        this.updatedAt = updatedAt;
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(int costPrice) {
        this.costPrice = costPrice;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
