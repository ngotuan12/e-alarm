package com.sc.robot.dealgrabber;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Tran Viet Hoang
 */
public class Grabber {

    public static String url;
    private static String html;
    
    private static String deallink;
    private static String innerDealLinkDiv;
    private static String nameDiv;
    private static String inNameDiv;
    private static String descriptionDiv;
    private static String inDescriptionDiv;
    private static String oldPriceDiv;
    private static String inOldPriceDiv;
    private static String newPriceDiv;
    private static String inNewPriceDiv;
    private static String discountDiv;
    private static String inDiscountDiv;
    private static String endDateDiv;
    private static String inEndDateDiv;
    private static String imageLinkDiv;
    private static String inimageLinkDiv;
    private static String buyerCountDiv;
    private static String inBuyerCountDiv;
    
    /**
     * Validation function
     * 
     * Check deals in database if the deal is not allready inserted. 
     * If is, check for the possible changes. If not kill.
     * Check by Name,OldPrice,NewPrice,Discount
     * @param
     * @return true if deal has already existed in database
     * 
     */
    public static boolean validateDatabase(String dealName, String dealDiscount, String dealOldPrice,
            String dealNewPrice) throws SQLException {
        MySQL mySQLAdapter;
        PreparedStatement preparedStatement = null;
        Connection connection;
        ResultSet rs;
        String oldPrice, newPrice, discount;

        mySQLAdapter = new MySQL();
        connection = mySQLAdapter.ConnectMySQL();
        mySQLAdapter.ConnectMySQL();
        String sqlQuery = "SELECT * FROM deal WHERE Name =?";
        preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, dealName);

        rs = preparedStatement.executeQuery();

        if (rs.next()) {
            while (rs.next()) {
//                System.out.println(rs.getString("Name"));
                oldPrice = rs.getString("OldPrice").trim();
                newPrice = rs.getString("NewPrice").trim();
                discount = rs.getString("Discount").trim();
                if (oldPrice.equalsIgnoreCase(dealOldPrice) && newPrice.equalsIgnoreCase(dealNewPrice) && discount.equalsIgnoreCase(dealDiscount)) {

                    return true;
                }
            }
        } else {
            return false;
        }
        preparedStatement.close();
        return false;



    }

    /**
     * Validation function
     * 
     * Check deals in database if the deal is not allready inserted. 
     * If is, check for the possible changes. If not kill.
     * Check by Name,OldPrice,NewPrice,Discount
     * @param
     * @return The ID that deal has already existed in database
     * 
     */
    public static int validateDeal(String dealName, String dealDiscount, String dealOldPrice,
            String dealNewPrice) throws SQLException {
        MySQL mySQLAdapter;
        PreparedStatement preparedStatement = null;
        Connection connection;
        ResultSet rs;
        String oldPrice, newPrice, discount;

        mySQLAdapter = new MySQL();
        connection = mySQLAdapter.ConnectMySQL();
        mySQLAdapter.ConnectMySQL();
        String sqlQuery = "SELECT * FROM deal WHERE Name =?";
        preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setString(1, dealName);
       

        rs = preparedStatement.executeQuery();

        if (rs.next()) {
            while (rs.next()) {
                oldPrice = rs.getString("OldPrice").trim();
                newPrice = rs.getString("NewPrice").trim();
                discount = rs.getString("Discount").trim();
                if (oldPrice.equalsIgnoreCase(dealOldPrice) && newPrice.equalsIgnoreCase(dealNewPrice) && discount.equalsIgnoreCase(dealDiscount)) {
                    return rs.getInt("ID");
                }
            }
        } else {
            return -1;
        }
        preparedStatement.close();
        return -1;


    }
    
    /**
     * Get deals from muachung
     * 
     * This function gets the www, find the description
     * 
     * @param
     * @return 
     * 
     */
    public static long getMuaChungDeals(DealProp settings) throws IOException {
        // START STOPWATCH in nanoTime
        long startTime = System.nanoTime();
        
        Document docs = Jsoup.connect(settings.getUrl()).referrer("http://www.google.com").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
        
        html = docs.html();
        //System.out.println(html);
        deallink = settings.getDeallink();
        innerDealLinkDiv = settings.getInnerDealLinkDiv();
        nameDiv = settings.getNameDiv();
        inNameDiv = settings.getInNameDiv();
        descriptionDiv = settings.getDescriptionDiv();
        inDescriptionDiv = settings.getInDescriptionDiv();
        oldPriceDiv = settings.getOldPriceDiv();
        inOldPriceDiv = settings.getInOldPriceDiv();
        newPriceDiv = settings.getNewPriceDiv();
        inNewPriceDiv = settings.getInNewPriceDiv();
        discountDiv = settings.getDiscountDiv();
        inDiscountDiv = settings.getInDiscountDiv();
        endDateDiv = settings.getEndDateDiv();
        inEndDateDiv = settings.getInEndDateDiv();
        imageLinkDiv = settings.getImageDiv();
        inimageLinkDiv = settings.getInImageDiv();
        buyerCountDiv = settings.getBuyerCountDiv();
        inBuyerCountDiv = settings.getInBuyerCountDiv();
        
        
        Document doc = Jsoup.parse(html);
        Elements dealLinks = doc.select(deallink);
        Elements names = doc.select(nameDiv);
        Elements descriptions = doc.select(descriptionDiv);
        Elements oldprices = doc.select(oldPriceDiv);
        Elements newprices = doc.select(newPriceDiv);
        Elements discounts = doc.select(discountDiv);
        Elements endDates = doc.select(endDateDiv);
        Elements images = doc.select(imageLinkDiv);
        Elements buyers = doc.select(buyerCountDiv);
        
        String dealLinkArr[] = new String[names.size()];
        String nameArr[] = new String[names.size()];
        String descriptionsArr[] = new String[names.size()];
        String oldPriceArr[] = new String[names.size()];
        String newPriceArr[] = new String[names.size()];
        String discountArr[] = new String[names.size()];
        String endDateArr[] = new String[names.size()];
        String imageLinkArr[] = new String[names.size()];
        String buyersArr[] = new String[names.size()];
        
        
        int i = 0;
        
        // SAVE NAMES OF DEAL INTO TEMP ARRAY
        for (Element name : names) {
            if (!"".equals(inNameDiv)) {
                Elements headers = name.select(inNameDiv);
                for (Element header : headers) {
                    nameArr[i] = header.text();
                }
            } else {
                nameArr[i] = name.text();
            }
            i++;
        }
        i = 0;
        
        // SAVE Link OF DEAL INTO TEMP ARRAY
        for (Element dealLink : dealLinks) {
            if (!"".equals(innerDealLinkDiv)) {
                Elements headers = dealLink.select(innerDealLinkDiv);
                for (Element header : headers) {
                    dealLinkArr[i] = header.attr("href");
//                    System.out.println(header.attr("href"));
//                    System.out.println(dealLinkArr[i]);
                    i++;
                }
            } else {
                dealLinkArr[i] = dealLink.text();
                i++;
            }
            
        }
        i = 0;
        // SAVE CONTENT OF DEAL INTO TEMP ARRAY
        for (Element description : descriptions) {
            if (!"".equals(inDescriptionDiv)) {
                Elements headers = description.select(inDescriptionDiv);
                for (Element header : headers) {
                    descriptionsArr[i] = header.text();
                }
            } else {
                descriptionsArr[i] = description.text();
            }
            i++;
        }
        i = 0;
        
        // SAVE OLD PRICE OF DEAL INTO TEMP ARRAY
        for (Element oldPrice : oldprices) {
            if (!"".equals(inOldPriceDiv)) {
                
                Elements headers = oldPrice.select(inOldPriceDiv);
                for (Element header : headers) {
                    String temp = header.text();
                    Pattern p = Pattern.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
                    Matcher m = p.matcher(temp);
                    while (m.find()) {
                        oldPriceArr[i] = m.group();
                    }
                }
            } else {
                
                String temp = oldPrice.text();
                Pattern p = Pattern.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
                Matcher m = p.matcher(temp);
                while (m.find()) {
                    oldPriceArr[i] = m.group();
                }
            }
            i++;
        }
        i = 0;
        
        // SAVE NEW PRICE OF DEAL INTO TEMP ARRAY
        for (Element newprice : newprices) {
            if (!"".equals(newPriceDiv)) {
                Elements headers = newprice.select(newPriceDiv);
                for (Element header : headers) {
                    //oldPriceArr[i] = header.text();
                    String temp = header.text();
                    Pattern p = Pattern.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
                    Matcher m = p.matcher(temp);
                    while (m.find()) {
                        newPriceArr[i] = m.group();
                    }
                }
            } else {
                String temp = newprice.text();
                Pattern p = Pattern.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
                Matcher m = p.matcher(temp);
                while (m.find()) {
                    newPriceArr[i] = m.group();
                }
            }
            i++;
        }
        i = 0;
        
        // SAVE PERCENTS OF DEAL INTO TEMP ARRAY
        for (Element discount : discounts) {
            String temp = discount.text();
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(temp);
            while (m.find()) {
                discountArr[i] = m.group();
            }
            i++;
        }
        i = 0;
        
        // SAVE TIME LEFT OF DEAL INTO TEMP ARRAY
        for (Element endDate : endDates) {
            //System.out.println(endDate);
            if (!"".equals(inEndDateDiv)) {
                Elements headers = endDate.select(inEndDateDiv);
                for (Element header : headers) {
                    endDateArr[i] = header.text();
                    //System.out.println(header.text());
                }
            } else {
                endDateArr[i] = endDate.text();
            }
            
//            String temp = endDate.text();
//            endDateArr[i] = endDate.text();
            i++;
        }
        
        i = 0;
        // SAVE IMAGES OF DEAL INTO TEMP ARRAY
        for (Element image : images) {
            if (!"".equals(inimageLinkDiv)) {
                Elements headers = image.select(inimageLinkDiv);
                for (Element header : headers) {
                    imageLinkArr[i] = header.attr("src");
                }
            } else {
                imageLinkArr[i] = image.attr("src");
            }
            i++;
        }
        
        i = 0;
        
        // SAVE BUYERS OF DEAL INTO TEMP ARRAY
        for (Element buyer : buyers) {
            String temp = buyer.text();
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(temp);
            while (m.find()) {
                buyersArr[i] = m.group();
            }
            i++;
        }
        
        
        
        String oldPriceArray[] = new String[nameArr.length];
        int newDealCreated = 0;
        for (int a = 0; a < nameArr.length; a++) {
//            System.out.println("Link of deal: " + dealLinkArr[a]);
//            System.out.println("Name of deal: " + nameArr[a]);
//            
//            System.out.println("Description of the deal: " + descriptionsArr[a]);
            try {
//                System.out.println("Old price: " + oldPriceArr[a]);
                oldPriceArray[a] = oldPriceArr[a];
            } catch (ArrayIndexOutOfBoundsException e) {
//                System.out.println("Old price: " + "0");
                oldPriceArray[a] = "0";
            }
            
//            System.out.println("New price: " + newPriceArr[a]);
//            System.out.println("Discount: " + discountArr[a]);
//            System.out.println("Timeleft: " + endDateArr[a]);
//            System.out.println("Image link: " + imageLinkArr[a]);
//            System.out.println("Buyers: " + buyersArr[a]);
//            System.out.println("------- END OF DEAL ---------------------");
            java.util.Date date = new java.util.Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //System.out.println(sd.format(date));
//            try {
//                // Validation and save to db
//                if (!validateDatabase(nameArr[a], discountArr[a], oldPriceArr[a], newPriceArr[a])) {
//                    String query = "INSERT INTO deal (NAME, SOURCEID, OLDPRICE, NEWPRICE, DISCOUNT, INSERTEDDATE, PLACE, CATEGORY, URL, VIEWCLICK, BUYERCOUNT, ACTIVE, IMAGE) "
//                             + "VALUES (N'" + nameArr[a] + "', 7, '" + oldPriceArray[a] + "', '" + newPriceArr[a] + "', " + discountArr[a]+", '"+ sd.format(date) + "', " + settings.getPlace() + ", "+ settings.getCategory() +",'" 
//                             + dealLinkArr[a] + "', 0,"+ buyersArr[a] + ", 1,'"+ imageLinkArr[a] +"');";
//                    new MySQL().ExecuteNonQuery(query);
//                    newDealCreated++;
//                    //System.out.println("Deal: "+nameArr[a]+" is not in DB. Saved.");
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(Grabber.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        long estimatedTimeInNanoSeconds = System.nanoTime() - startTime;
        long estimatedTimeInMilliSeconds = estimatedTimeInNanoSeconds / 1000000;
        long estimatedTimeInSeconds = estimatedTimeInNanoSeconds / 1000000000;
        
        
//        System.out.println("Estimated Time = " + estimatedTimeInNanoSeconds + " nano seconds");
//        System.out.println("Estimated Time = " + estimatedTimeInMilliSeconds + " milli seconds");
//        System.out.println("Estimated Time = " + estimatedTimeInSeconds + " seconds");
        System.out.println("New deals created: " + newDealCreated);
        //log.estimatedTimeInNanoSeconds = estimatedTimeInNanoSeconds;
        //log.estimatedTimeInMilliSeconds = estimatedTimeInMilliSeconds;
        //log.estimatedTimeInSeconds = estimatedTimeInSeconds;
        return estimatedTimeInNanoSeconds;
    }
    
    public static long getCucreDeals(DealProp settings) throws IOException {
        // START STOPWATCH in nanoTime
        long startTime = System.nanoTime();
        
        Document docs = Jsoup.connect(settings.getUrl()).referrer("http://www.google.com").userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get();
        
        html = docs.html();
        //System.out.println(html);
        deallink = settings.getDeallink();
        innerDealLinkDiv = settings.getInnerDealLinkDiv();
        nameDiv = settings.getNameDiv();
        inNameDiv = settings.getInNameDiv();
        descriptionDiv = settings.getDescriptionDiv();
        inDescriptionDiv = settings.getInDescriptionDiv();
        oldPriceDiv = settings.getOldPriceDiv();
        inOldPriceDiv = settings.getInOldPriceDiv();
        newPriceDiv = settings.getNewPriceDiv();
        inNewPriceDiv = settings.getInNewPriceDiv();
        discountDiv = settings.getDiscountDiv();
        inDiscountDiv = settings.getInDiscountDiv();
        endDateDiv = settings.getEndDateDiv();
        inEndDateDiv = settings.getInEndDateDiv();
        imageLinkDiv = settings.getImageDiv();
        inimageLinkDiv = settings.getInImageDiv();
        buyerCountDiv = settings.getBuyerCountDiv();
        inBuyerCountDiv = settings.getInBuyerCountDiv();
        
        
        Document doc = Jsoup.parse(html);
        Elements dealLinks = doc.select(deallink);
        Elements names = doc.select(nameDiv);
        Elements descriptions = doc.select(descriptionDiv);
        Elements oldprices = doc.select(oldPriceDiv);
        Elements newprices = doc.select(newPriceDiv);
        Elements discounts = doc.select(discountDiv);
        Elements endDates = doc.select(endDateDiv);
        Elements images = doc.select(imageLinkDiv);
        Elements buyers = doc.select(buyerCountDiv);
        
        String dealLinkArr[] = new String[names.size()];
        String nameArr[] = new String[names.size()];
        String descriptionsArr[] = new String[names.size()];
        String oldPriceArr[] = new String[names.size()];
        String newPriceArr[] = new String[names.size()];
        String discountArr[] = new String[names.size()];
        String endDateArr[] = new String[names.size()];
        String imageLinkArr[] = new String[names.size()];
        String buyersArr[] = new String[names.size()];
        
        
        int i = 0;
        
        // SAVE NAMES OF DEAL INTO TEMP ARRAY
        for (Element name : names) {
            if (!"".equals(inNameDiv)) {
                Elements headers = name.select(inNameDiv);
                for (Element header : headers) {
                    nameArr[i] = header.text();
                }
            } else {
                nameArr[i] = name.text();
            }
            i++;
        }
        i = 0;
        
        // SAVE Link OF DEAL INTO TEMP ARRAY
        for (Element dealLink : dealLinks) {
            if (!"".equals(innerDealLinkDiv)) {
                Elements headers = dealLink.select(innerDealLinkDiv);
                for (Element header : headers) {
                    dealLinkArr[i] = header.attr("href");
//                    System.out.println(header.attr("href"));
//                    System.out.println(dealLinkArr[i]);
                    i++;
                }
            } else {
                dealLinkArr[i] = dealLink.text();
                i++;
            }
            
        }
        i = 0;
        // SAVE CONTENT OF DEAL INTO TEMP ARRAY
        for (Element description : descriptions) {
            if (!"".equals(inDescriptionDiv)) {
                Elements headers = description.select(inDescriptionDiv);
                for (Element header : headers) {
                    descriptionsArr[i] = header.text();
                }
            } else {
                descriptionsArr[i] = description.text();
            }
            i++;
        }
        i = 0;
        
        // SAVE OLD PRICE OF DEAL INTO TEMP ARRAY
        for (Element oldPrice : oldprices) {
            if (!"".equals(inOldPriceDiv)) {
                
                Elements headers = oldPrice.select(inOldPriceDiv);
                for (Element header : headers) {
                    String temp = header.text();
                    Pattern p = Pattern.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
                    Matcher m = p.matcher(temp);
                    while (m.find()) {
                        oldPriceArr[i] = m.group();
                    }
                }
            } else {
                
                String temp = oldPrice.text();
                Pattern p = Pattern.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
                Matcher m = p.matcher(temp);
                while (m.find()) {
                    oldPriceArr[i] = m.group();
                }
            }
            i++;
        }
        i = 0;
        
        // SAVE NEW PRICE OF DEAL INTO TEMP ARRAY
        for (Element newprice : newprices) {
            if (!"".equals(newPriceDiv)) {
                Elements headers = newprice.select(newPriceDiv);
                for (Element header : headers) {
                    //oldPriceArr[i] = header.text();
                    String temp = header.text();
                    Pattern p = Pattern.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
                    Matcher m = p.matcher(temp);
                    while (m.find()) {
                        newPriceArr[i] = m.group();
                    }
                }
            } else {
                String temp = newprice.text();
                Pattern p = Pattern.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
                Matcher m = p.matcher(temp);
                while (m.find()) {
                    newPriceArr[i] = m.group();
                }
            }
            i++;
        }
        i = 0;
        
        // SAVE PERCENTS OF DEAL INTO TEMP ARRAY
        for (Element discount : discounts) {
            String temp = discount.text();
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(temp);
            while (m.find()) {
                discountArr[i] = m.group();
            }
            i++;
        }
        i = 0;
        
        // SAVE TIME LEFT OF DEAL INTO TEMP ARRAY
        for (Element endDate : endDates) {
            //System.out.println(endDate);
            if (!"".equals(inEndDateDiv)) {
                Elements headers = endDate.select(inEndDateDiv);
                for (Element header : headers) {
                    endDateArr[i] = header.text();
                    //System.out.println(header.text());
                }
            } else {
                endDateArr[i] = endDate.text();
            }
            
//            String temp = endDate.text();
//            endDateArr[i] = endDate.text();
            i++;
        }
        
        i = 0;
        // SAVE IMAGES OF DEAL INTO TEMP ARRAY
        for (Element image : images) {
            if (!"".equals(inimageLinkDiv)) {
                Elements headers = image.select(inimageLinkDiv);
                for (Element header : headers) {
                    imageLinkArr[i] = header.attr("src");
                }
            } else {
                imageLinkArr[i] = image.attr("src");
            }
            i++;
        }
        
        i = 0;
        
        // SAVE BUYERS OF DEAL INTO TEMP ARRAY
        for (Element buyer : buyers) {
            String temp = buyer.text();
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(temp);
            while (m.find()) {
                buyersArr[i] = m.group();
            }
            i++;
        }
        
        
        
        String oldPriceArray[] = new String[nameArr.length];
        int newDealCreated = 0;
        for (int a = 0; a < nameArr.length; a++) {
//            System.out.println("Link of deal: " + dealLinkArr[a]);
//            System.out.println("Name of deal: " + nameArr[a]);
//            
//            System.out.println("Description of the deal: " + descriptionsArr[a]);
            try {
//                System.out.println("Old price: " + oldPriceArr[a]);
                oldPriceArray[a] = oldPriceArr[a];
            } catch (ArrayIndexOutOfBoundsException e) {
//                System.out.println("Old price: " + "0");
                oldPriceArray[a] = "0";
            }
            
//            System.out.println("New price: " + newPriceArr[a]);
//            System.out.println("Discount: " + discountArr[a]);
//            System.out.println("Timeleft: " + endDateArr[a]);
//            System.out.println("Image link: " + imageLinkArr[a]);
//            System.out.println("Buyers: " + buyersArr[a]);
//            System.out.println("------- END OF DEAL ---------------------");
            java.util.Date date = new java.util.Date();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //System.out.println(sd.format(date));
            try {
                // Validation and save to db
                if (!validateDatabase(nameArr[a], discountArr[a], oldPriceArr[a], newPriceArr[a])) {
                    String query = "INSERT INTO deal (NAME, SOURCEID, OLDPRICE, NEWPRICE, DISCOUNT, INSERTEDDATE, PLACE, CATEGORY, URL, VIEWCLICK, BUYERCOUNT, ACTIVE, IMAGE) "
                             + "VALUES (N'" + nameArr[a] + "', 7, '" + oldPriceArray[a] + "', '" + newPriceArr[a] + "', " + discountArr[a]+", '"+ sd.format(date) + "', " + settings.getPlace() + ", "+ settings.getCategory() +",'" 
                             + dealLinkArr[a] + "', 0,"+ buyersArr[a] + ", 1,'"+ imageLinkArr[a] +"');";
                    new MySQL().ExecuteNonQuery(query);
                    newDealCreated++;
                    //System.out.println("Deal: "+nameArr[a]+" is not in DB. Saved.");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Grabber.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        long estimatedTimeInNanoSeconds = System.nanoTime() - startTime;
        long estimatedTimeInMilliSeconds = estimatedTimeInNanoSeconds / 1000000;
        long estimatedTimeInSeconds = estimatedTimeInNanoSeconds / 1000000000;
        
        
//        System.out.println("Estimated Time = " + estimatedTimeInNanoSeconds + " nano seconds");
//        System.out.println("Estimated Time = " + estimatedTimeInMilliSeconds + " milli seconds");
//        System.out.println("Estimated Time = " + estimatedTimeInSeconds + " seconds");
        System.out.println("New deals created: " + newDealCreated);
        //log.estimatedTimeInNanoSeconds = estimatedTimeInNanoSeconds;
        //log.estimatedTimeInMilliSeconds = estimatedTimeInMilliSeconds;
        //log.estimatedTimeInSeconds = estimatedTimeInSeconds;
        return estimatedTimeInNanoSeconds;
    }
}
