package com.sc.robot.dealgrabber;

import java.util.TimerTask;

/**
 *
 * @author Tran Viet Hoang
 */
public class muachungTask extends TimerTask {
    private int catID;
    
    @Override
    public void run() {
            catID = (int) 9;
            // GET COUNT OF ACTIVE CATEGORIES 
//            String query = "SELECT ID AS ID FROM category WHERE Active = 1";
//            
//            ResultSet rs = new MySQL().ExecuteQuery(query);
            
//            try {
//                for (int i = 0; rs.next(); i++) {
//                   catID[i] = rs.getInt(1);     
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(muachungTask.class.getName()).log(Level.SEVERE, null, ex);
//            }
            
            try {
                DealProp muachungSettings = new DealProp();
                muachungSettings.setPlace(1);
                
                muachungSettings.setDeallink("div.SellingTitle > a");
                muachungSettings.setInnerDealLinkDiv("a");
                muachungSettings.setNameDiv("div.SellingTitle");
                muachungSettings.setInNameDiv("a");
                muachungSettings.setDescriptionDiv("div.realTitle.mTop5");
                muachungSettings.setInDescriptionDiv("a");
                muachungSettings.setOldPriceDiv("div.rootPrice");
                muachungSettings.setInOldPriceDiv("");
                muachungSettings.setNewPriceDiv("div.SellingPrice");
                muachungSettings.setInNewPriceDiv("");
                muachungSettings.setDiscountDiv("div.fixPNG.discount_new");
                muachungSettings.setInDiscountDiv("div");
                muachungSettings.setEndDateDiv("span.DateItemNumber");
                muachungSettings.setInEndDateDiv("");
                muachungSettings.setImageDiv("div.SellingLeft img");
                muachungSettings.setInImageDiv("");
                muachungSettings.setBuyerCountDiv("div.SellingInfoValue");
                muachungSettings.setInBuyerCountDiv("span");
                Log log = new Log();
                log.setSource("Muachung.vn");
                long totalEstimatedTimeInNanoSeconds = 0;
                for (int i = 1; i <= catID; i++) {
                    //System.out.println(catID);
                    long tempTimeInNano;
                    switch(i) {
                        // 
                        case 1: 
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Am Thuc -----------------");
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-9/quan-an.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-15/thuc-pham.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            break;
                        case 2:
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Cong Nghe -----------------");
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-11/dien-may.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            
                            break;
                        case 3:
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Hoat dong (Activity) -----------------");
                            muachungSettings.setUrl("");
//                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
//                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            break;
                        case 4:
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Du lich -----------------");
                            muachungSettings.setUrl("");
//                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
//                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            break;
                        case 5:
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Thoi trang -----------------");
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-6/spa-lam-dep.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-13/thoi-trang-my-pham.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            break;
                        case 6:
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Hang tieu dung -----------------");
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-8/tieu-dung.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            break;
                        case 7:
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Giao duc -----------------");
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-7/khoa-hoc-di-choi.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            break;
                        case 8:
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Tre em -----------------");
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-12/tre-em.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            break;
                        case 9:
                            muachungSettings.setCategory(i);
                            System.out.println("-------------- Loai Khac -----------------");
                            muachungSettings.setUrl("http://muachung.vn/danh-muc/c-5/sach-truyen.html");
                            tempTimeInNano = Grabber.getMuaChungDeals(muachungSettings);
                            totalEstimatedTimeInNanoSeconds += tempTimeInNano;
                            break;
                        default: 
                            break;
                    }
                }
                
                // Count time and save it to log
                long finalEstimatedTimeInNano = totalEstimatedTimeInNanoSeconds;
                long finalEstimatedTimeInMilli = log.calcMilliSeconds(totalEstimatedTimeInNanoSeconds);
                long finalEstimatedTimeInSeconds = log.calcSeconds(finalEstimatedTimeInMilli);
                        
                log.setEstimatedTimeInNanoSeconds(finalEstimatedTimeInNano);
                log.setEstimatedTimeInMilliSeconds(finalEstimatedTimeInMilli);
                log.setEstimatedTimeInSeconds(finalEstimatedTimeInSeconds);
                
                log.reportLog();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }
    
}
