package com.sc.robot.dealgrabber;

import java.sql.ResultSet;
import java.util.TimerTask;

/**
 *
 * @author Tran Viet Hoang
 */
public class cungmuaTask extends TimerTask {
    private int catID;
    
    @Override
    public void run() {
            // GET COUNT OF ACTIVE CATEGORIES 
            String query = "SELECT ID AS ID FROM category WHERE Active = 1";
            
            ResultSet rs = new MySQL().ExecuteQuery(query);
            
            try {
                DealProp cungmuaSettings = new DealProp();
                cungmuaSettings.setDeallink("ul.product > a");
                cungmuaSettings.setInnerDealLinkDiv("");
                cungmuaSettings.setNameDiv("ul.product");
                cungmuaSettings.setInNameDiv("h3");
                cungmuaSettings.setDescriptionDiv("s");
                cungmuaSettings.setInDescriptionDiv("");
                cungmuaSettings.setOldPriceDiv("p.product_price_true");
                cungmuaSettings.setInOldPriceDiv("span");
                cungmuaSettings.setNewPriceDiv("p.product_price");
                cungmuaSettings.setInNewPriceDiv("");
                cungmuaSettings.setDiscountDiv("div.percent1");
                cungmuaSettings.setInDiscountDiv("");
                cungmuaSettings.setEndDateDiv("span.DateItemNumber");
                cungmuaSettings.setInEndDateDiv("");
                cungmuaSettings.setImageDiv("div.product");
                cungmuaSettings.setInImageDiv("img.product_img.loading1");
                cungmuaSettings.setBuyerCountDiv("div.product > p.product_infobuy");
                cungmuaSettings.setInBuyerCountDiv("span");
                
                
                while (rs.next()) {
                    catID = rs.getInt(1);
                    System.out.println(catID);
                    switch(catID) {
                        // Thoi Trang
                        case 1: 
                            cungmuaSettings.setUrl("http://www.cungmua.com/thoi-trang");
                            //Grabber.getCucReDeals(cungmuaSettings);
                            break;
                        case 2:
                            break;
                        default: 
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }
    
}
