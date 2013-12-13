package com.sc.robot.dealgrabber;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.util.Timer;



/**
 *
 * @author Tran Viet Hoang
 */
public class DealGrabber {

    
    /**
     * @param args the command line arguments
     */
    public static Connection conn = null;
    
    public static void main(String[] args) throws MalformedURLException, IOException {
//        Timer nhomMuaTimer = new Timer();
      Timer muaChungTimer = new Timer();
 //         Timer cucReTimer = new Timer();
//         Timer hotDealTimer = new Timer();
//        Timer cungMuaTimer = new Timer();
        
        // EVERY 5 minutes
       muaChungTimer.schedule(new muachungTask(), 5, 300000);
//        nhomMuaTimer.schedule(new nhomMuaTask(), 5, 300000);
//       cucReTimer.schedule(new cucreTask(), 5, 300000);
//        hotDealTimer.schedule(new hotdealTask(), 5, 300000);
//        cungMuaTimer.schedule(new cungmuaTask(), 5, 300000);
        
        //timer.schedule(new Task(), 5, 3000);
        
    }
}
