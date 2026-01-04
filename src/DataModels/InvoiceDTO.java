/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

/**
 *
 * @author Rasookhan
 */
public class InvoiceDTO {
    private final int invoiceNumber;
    private final String customerName;
    private final String productName;
    private final int quantity;
    private final int salePrice;
    private final String currencyName;
    private final int totalPrice;

    public InvoiceDTO(int invoiceNumber,String customerName,String productName, int quantity,int salePrice, String currencyName) {
        this.invoiceNumber=invoiceNumber;
        this.customerName=customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.salePrice = salePrice;
        this.currencyName = currencyName;
        this.totalPrice=quantity*salePrice;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
    
}
