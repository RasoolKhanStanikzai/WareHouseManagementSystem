/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

import java.security.Timestamp;

/**
 *
 * @author Rasookhan
 */
public class CurrencyModel {
    
    private int id;
    private String code,name,symbol,country;
    private int dp;
    private String status;
    private double rate;
    private Timestamp createAt;
    private String createdBy;
    private Timestamp updateAt;
    private String updateBy;
    private Timestamp deletedAt;
    private String deletedBy;

    public CurrencyModel(int id, String code, String name, String symbol, String country, int dp, String status, double rate) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.country = country;
        this.dp = dp;
        this.status = status;
        this.rate = rate;
    }

    public CurrencyModel(int id, String code, String name, String symbol, String country, int dp, String status, double rate, Timestamp createAt, String createdBy, Timestamp updateAt, String updateBy,
            Timestamp deletedAt,String deletedBy) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.country = country;
        this.dp = dp;
        this.status = status;
        this.rate = rate;
        this.createAt = createAt;
        this.createdBy = createdBy;
        this.updateAt = updateAt;
        this.updateBy = updateBy;
        this.deletedAt=deletedAt;
        this.deletedBy=deletedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getDp() {
        return dp;
    }

    public void setDp(int dp) {
        this.dp = dp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Timestamp updateAt) {
        this.updateAt = updateAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
     
    @Override
    public String toString(){
        return name;
    }
    
}
