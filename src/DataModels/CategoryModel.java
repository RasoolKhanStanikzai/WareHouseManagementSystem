/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

import java.security.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author Rasookhan
 */
public class CategoryModel {
    private int id;
    private String name,descriptions;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updateAt;
    private String updateBy;
    private LocalDateTime deletedAt;
    private String deleteBy;

    public CategoryModel(int id, String name, String descriptions, LocalDateTime createdAt, String createdBy, LocalDateTime updateAt, String updateBy, LocalDateTime deletedAt, String deleteBy) {
        this.id = id;
        this.name = name;
        this.descriptions = descriptions;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updateAt = updateAt;
        this.updateBy = updateBy;
        this.deletedAt = deletedAt;
        this.deleteBy = deleteBy;
    }

    public CategoryModel(int id, String name, String descriptions) {
        this.id = id;
        this.name = name;
        this.descriptions = descriptions;
    }

    public CategoryModel(int id, String name, String descriptions, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.descriptions = descriptions;
        this.createdAt = createdAt;
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

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
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

    public String getDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(String deleteBy) {
        this.deleteBy = deleteBy;
    }
    
    // override method for to load data into combo box 
    @Override
    public String toString(){
        return name;
    }
}
