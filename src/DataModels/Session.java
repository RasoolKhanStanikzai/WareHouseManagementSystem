/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataModels;

/**
 *
 * @author Rasookhan
 */
public class Session {
    private static UserModel currentUser;
    
    public static void setCurrentUser(UserModel user){
        currentUser=user;
    }
    public static UserModel getCurrentUser(){
        return currentUser;
    }
            
}
