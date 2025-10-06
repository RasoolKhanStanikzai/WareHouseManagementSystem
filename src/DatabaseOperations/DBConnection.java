/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DatabaseOperations;
import java.sql.*;
/**
 *
 * @author Rasookhan
 */
public class DBConnection {
    private static final String URL="jdbc:mysql://localhost:3306/warehousemanagementsystem";
    private static final String USER="root";
    private static final String PASSWORD="";
    
    // Loading mysql Driver
    static{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException ex){
            System.err.print("Driver Not Found: "+ex.getMessage());
        }
    }
    
    // Get Database Connection
    public static Connection getConnection(){
        try{
            return DriverManager.getConnection(URL,USER,PASSWORD);
        }
        catch(SQLException ex){
            System.err.println("Database Connection failed: "+ex.getMessage());
            return null;
        }
        
    }
    // Close Connection safely
        
        public static void closeConnection(Connection conn){
            if(conn !=null){
                try{
                    conn.close();
                }
                catch(SQLException ex){
                    System.err.println("Failed to close conenction: "+ ex.getMessage());
                }
            }
        }
}
