package com.sc.robot.dealgrabber;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tran Viet Hoang
 */
public class MySQL {
    public Connection conn = null;
    public ResultSet resultSet = null;
    
    public Connection ConnectMySQL() {
            try {
                String userName = "root";
                String password = "";
                String url = "jdbc:mysql://192.168.10.2/collectingdeal)?useEncoding=true&characterEncoding=UTF-8";
                Class.forName ("com.mysql.jdbc.Driver").newInstance ();
                conn = DriverManager.getConnection (url, userName, password);
                //System.out.println ("Database connection established");
                
                return conn;
            } catch (Exception e) {
                System.err.println ("Cannot connect to database server");
            }    
        
        
        return conn;
    }
    
    public void CloseMySQL() {
        if (conn != null) {
            try {
                conn.close();
                //System.out.println ("Database connection terminated");
            } catch (Exception e) {
            }
        }
    }
    
    //Execute an sql query and get a result set.

    public ResultSet ExecuteQuery(String sql) {
        ResultSet rs = null;
        try {
            Connection con = this.ConnectMySQL();
            Statement stm = con.createStatement(); //ctrl + Shift + i
            rs = stm.executeQuery(sql);
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
    //Execute query

    public void ExecuteNonQuery(String sql) {
        try {
            Connection con = this.ConnectMySQL();
            if (con != null) {
                Statement stm = con.createStatement(); //ctrl + Shift + i
                stm.executeUpdate(sql);
                //System.out.println("DEALS SAVED INTO DB");
            } else {
                System.out.println("Cannot connect");
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            //try to close the connection before garbage collection
            conn.close();
        } catch (Exception e) {
        }
        super.finalize();
    }
}
