/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

import java.util.Currency;
import java.util.Locale;

/**
 *
 * @author Rasookhan
 */
public class CountryItemModel {
    private final String name;
    private final Locale locale;
    
    public CountryItemModel(Locale locale){
        this.locale=locale;
        this.name=locale.getDisplayCountry();
    }
    public String getName(){
        return name;
    }
    public Locale getLocale(){
        return locale;
    }
    // For currency
    public Currency getCurrency(){
        try{
            return Currency.getInstance(locale);
        }
        catch(IllegalArgumentException ex){
            return null;
        }
    }
    @Override
    public String toString(){
        return name;
    }
    
}
