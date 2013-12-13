package com.sc.robot.dealgrabber;

import java.sql.ResultSet;
import java.util.TimerTask;

/**
 *
 * @author Tran Viet Hoang
 */
public class cucreTask extends TimerTask {
    private int catID;
    
    @Override
    public void run() {
            // GET COUNT OF ACTIVE CATEGORIES 
            String query = "SELECT ID AS ID FROM category WHERE Active = 1";
            
            ResultSet rs = new MySQL().ExecuteQuery(query);
            
            try {
                DealProp cucreSettings = new DealProp();
                cucreSettings.setDeallink("div.wbd_title");
                cucreSettings.setInnerDealLinkDiv("a");
                cucreSettings.setNameDiv("div.wbd_title");
                cucreSettings.setInNameDiv("a");
                cucreSettings.setDescriptionDiv("div.wbd_title");
                cucreSettings.setInDescriptionDiv("p");
                cucreSettings.setOldPriceDiv("div.wrap-bottom-deal-buy > div.wrap-top-deal-percen");
                cucreSettings.setInOldPriceDiv("span");
                cucreSettings.setNewPriceDiv("div.wrap-bottom-deal-buy > div.wrap-top-deal-price");
                cucreSettings.setInNewPriceDiv("span");
                cucreSettings.setDiscountDiv("div.wrap-bottom-deal-percent");
                cucreSettings.setInDiscountDiv("span > span");
                cucreSettings.setEndDateDiv("div.s");
                cucreSettings.setInEndDateDiv("");
                cucreSettings.setImageDiv("div.wrap-bottom-deal-img");
                cucreSettings.setInImageDiv("");
                cucreSettings.setBuyerCountDiv("div.wrap-bottom-deal-info");
                cucreSettings.setInBuyerCountDiv("p.wtd_numberorder > span");
                
                while (rs.next()) {
                    catID = rs.getInt(1);
                    //System.out.println(catID);
                    switch(catID) {
                        // Thoi Trang
                        case 1: 
                            cucreSettings.setUrl("http://cucre.vn/vn/index.php?iCat=686&iCit=5");
                            //Grabber.getCucReDeals(cucreSettings);
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
