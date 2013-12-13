/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.robot.dealgrabber;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Tran Viet Hoang
 */
public class Log {
    public String source;
    public long estimatedTimeInNanoSeconds;
    public long estimatedTimeInMilliSeconds;
    public long estimatedTimeInSeconds;
    
    public Log() {
        source = "";
        estimatedTimeInMilliSeconds = 0;
        estimatedTimeInNanoSeconds = 0;
        estimatedTimeInSeconds = 0;
    }
    
    public long calcMilliSeconds(long nanoSeconds) {
        return (nanoSeconds / 1000000);
    }
    
    public long calcSeconds(long milliSeconds) {
        return (milliSeconds / 1000);
    }
    
    public void reportLog() throws IOException, ParseException {
        Date date = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        
        
        BufferedWriter bw = new BufferedWriter(new FileWriter("report_"+this.getSource()+"_"+sd.format(date)+".html"));
        bw.write("<html><head><title>New Page</title></head><body>");
        bw.write("<h1>"+ this.getSource() +"</h1>");
        bw.write("<table>");
            bw.write("<th>");
            bw.write("<p>Time format</p>");
            bw.write("</th>");
            bw.write("<th>");
            bw.write("<p>Estimated time</p>");
            bw.write("</th>");
            bw.write("<tr>");
                bw.write("<td>");
                bw.write("<p>Nano seconds</p>");
                bw.write("</td>");
                bw.write("<td>");
                bw.write("<p>"+ this.getEstimatedTimeInNanoSeconds() +" ns</p>");
                bw.write("</td>");
            bw.write("</tr>");
            bw.write("<tr>");
                bw.write("<td>");
                bw.write("<p>Milli seconds</p>");
                bw.write("</td>");
                bw.write("<td>");
                bw.write("<p>"+ this.getEstimatedTimeInMilliSeconds() +" ms</p>");
                bw.write("</td>");
            bw.write("</tr>");
            
            bw.write("<tr>");
                bw.write("<td>");
                bw.write("<p>Seconds</p>");
                bw.write("</td>");
                bw.write("<td>");
                bw.write("<p>"+ this.getEstimatedTimeInSeconds() +" s</p>");
                bw.write("</td>");
            bw.write("</tr>");
        bw.write("</table>");
        bw.write("</body></html>");
        System.out.println("Report from "+ this.getSource()+" has been saved.");
        bw.close();
    }

    public long getEstimatedTimeInMilliSeconds() {
        return estimatedTimeInMilliSeconds;
    }

    public void setEstimatedTimeInMilliSeconds(long estimatedTimeInMilliSeconds) {
        this.estimatedTimeInMilliSeconds = estimatedTimeInMilliSeconds;
    }

    public long getEstimatedTimeInNanoSeconds() {
        return estimatedTimeInNanoSeconds;
    }

    public void setEstimatedTimeInNanoSeconds(long estimatedTimeInNanoSeconds) {
        this.estimatedTimeInNanoSeconds = estimatedTimeInNanoSeconds;
    }

    public long getEstimatedTimeInSeconds() {
        return estimatedTimeInSeconds;
    }

    public void setEstimatedTimeInSeconds(long estimatedTimeInSeconds) {
        this.estimatedTimeInSeconds = estimatedTimeInSeconds;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
