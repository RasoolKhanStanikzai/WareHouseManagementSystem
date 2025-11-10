/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DatabaseOperations;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Rasookhan
 */
public class CRUDOperations {
    Connection connect=DBConnection.getConnection();
    
    public  boolean insert(String query,Object...params){
        try(PreparedStatement stmt=connect.prepareStatement(query)){
            setParameters(stmt,params);
            return stmt.executeUpdate()>0;
        }
        catch(SQLException ex){
            System.err.println("No Insertion: "+ex.getMessage());
            return false;
        }
        
    }
    // Generic Data Retrieve method
    public List<Map<String,Object>> retrieve(String query,Object...params){
        List<Map<String,Object>> data= new ArrayList<>();
        try(PreparedStatement stmt=connect.prepareStatement(query)){
            setParameters(stmt,params);
            ResultSet rSet=stmt.executeQuery();
            ResultSetMetaData meta=rSet.getMetaData();
            int columnCount=meta.getColumnCount();
            
            // Converting each row into a map
            while(rSet.next()){
                Map<String,Object> row=new HashMap<>();
                for(int i=1;i<=columnCount;i++){
                    String colName=meta.getColumnLabel(i);
                    Object value;
                    if(meta.getColumnType(i)==Types.BLOB)
                        value=rSet.getBytes(i);
                    else
                        value=rSet.getObject(i);
                    
                    row.put(colName, value);
                    //row.put(meta.getColumnLabel(i), rSet.getObject(i));
                }
                data.add(row);
                
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return data;
    }
    private void setParameters(PreparedStatement stmt,Object...params)throws SQLException {
        int index=1;
        for(Object param:params){
            if(param instanceof String)
                stmt.setString(index++, (String)param);
            else if(param instanceof Integer)
                stmt.setInt(index++, (Integer)param);
            else if(param instanceof Double)
                stmt.setDouble(index++, (Double) param);
            else if(param instanceof byte[])
                stmt.setBytes(index++, (byte[]) param);
            else if(param instanceof File ){
                try{
                    stmt.setBinaryStream(index++,new FileInputStream((File)param));
                }
                catch(FileNotFoundException ex){
                    throw new SQLException("File not found"+param,ex);
                }
            }
            else if(param instanceof InputStream)
                stmt.setBinaryStream(index++, (InputStream)param);
            else
                stmt.setObject(index++, param);
                
        }
    }
    
    // Delete Record  Method
    public boolean delete(String query,Object...params){
        try(PreparedStatement stmt=connect.prepareStatement(query)){
            setParameters(stmt,params);
            return stmt.executeUpdate() > 0;
        }
        catch(SQLException ex){
            System.err.println("Delete Failed: "+ex.getMessage());
            return false;
        }
    }
    // Update Record Method
    public boolean update(String query,Object...params){
        try(PreparedStatement stmt=connect.prepareStatement(query)){
            setParameters(stmt,params);
            return stmt.executeUpdate() > 0;
            
        }
        catch(SQLException ex){
            System.err.print("Update Failed: "+ex.getMessage());
            return false;
        }
    }
    
    // Getting the specific id of a row in a table
    public int getInsertAndUpdateID(String query,Object...params){
        int generatedId=-1;
        try(PreparedStatement stmt=connect.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)){
            setParameters(stmt,params);
            int affectedRows=stmt.executeUpdate();
            
            if(affectedRows>0){
                try(ResultSet rs=stmt.getGeneratedKeys()){
                    if(rs.next()){
                        generatedId=rs.getInt(1);
                    }
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return generatedId;
    }
    
    public int getCount(String query,Object...params){
        int count=0;
        try(PreparedStatement stmt=connect.prepareStatement(query)){
            setParameters(stmt,params);
            
            try(ResultSet rSet=stmt.executeQuery()){
                if(rSet.next()){
                    count=rSet.getInt(1);
                }
            } 
        } catch(SQLException ex){
            System.err.println("GetCount failed: "+ex.getMessage());
            ex.printStackTrace();
        }
        return count;
    }
}
