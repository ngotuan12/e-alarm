package com.sc.robot.dealgrabber;

/**
 *
 * @author Tran Viet Hoang
 */

/**
 * DEAL
 * 
 * SourceID
 * Name
 * OldPrice
 * NewPrice
 * Discount
 * StartDate
 * EndDate
 * InsertedDate
 * Place
 * Category
 * Url 
 * ViewClick
 * Description
 * BuyerCount
 * Active
 * Image
 * 
 */

public class DealProp {
    public String url, deallink, innerDealLinkDiv, nameDiv, inNameDiv, descriptionDiv, inDescriptionDiv,
                  oldPriceDiv, inOldPriceDiv, newPriceDiv, inNewPriceDiv, discountDiv, inDiscountDiv,
                  endDateDiv, inEndDateDiv, imageDiv, inImageDiv, buyerCountDiv, inBuyerCountDiv;
    public int category, place, viewclick, active;
    
    public DealProp() {
        url = "";
        deallink = "";
        innerDealLinkDiv = ""; 
        nameDiv = "";
        inNameDiv = ""; 
        descriptionDiv = ""; 
        inDescriptionDiv = "";
        oldPriceDiv = ""; 
        inOldPriceDiv = ""; 
        newPriceDiv = ""; 
        inNewPriceDiv = ""; 
        discountDiv = ""; 
        inDiscountDiv = "";
        endDateDiv = ""; 
        inEndDateDiv = ""; 
        buyerCountDiv = ""; 
        inBuyerCountDiv = "";
        imageDiv = "";
        inImageDiv = "";
    }

    public String getImageDiv() {
        return imageDiv;
    }

    public void setImageDiv(String imageDiv) {
        this.imageDiv = imageDiv;
    }

    public String getInImageDiv() {
        return inImageDiv;
    }

    public void setInImageDiv(String inImageDiv) {
        this.inImageDiv = inImageDiv;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getBuyerCountDiv() {
        return buyerCountDiv;
    }

    public void setBuyerCountDiv(String buyerCountDiv) {
        this.buyerCountDiv = buyerCountDiv;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescriptionDiv() {
        return descriptionDiv;
    }

    public void setDescriptionDiv(String descriptionDiv) {
        this.descriptionDiv = descriptionDiv;
    }

    public String getDeallink() {
        return deallink;
    }

    public void setDeallink(String deallink) {
        this.deallink = deallink;
    }

    public String getInBuyerCountDiv() {
        return inBuyerCountDiv;
    }

    public void setInBuyerCountDiv(String inBuyerCountDiv) {
        this.inBuyerCountDiv = inBuyerCountDiv;
    }

    public String getInDescriptionDiv() {
        return inDescriptionDiv;
    }

    public void setInDescriptionDiv(String inDescriptionDiv) {
        this.inDescriptionDiv = inDescriptionDiv;
    }

    public String getInNameDiv() {
        return inNameDiv;
    }

    public void setInNameDiv(String inNameDiv) {
        this.inNameDiv = inNameDiv;
    }

    public String getInNewPriceDiv() {
        return inNewPriceDiv;
    }

    public void setInNewPriceDiv(String inNewPriceDiv) {
        this.inNewPriceDiv = inNewPriceDiv;
    }

    public String getInOldPriceDiv() {
        return inOldPriceDiv;
    }

    public void setInOldPriceDiv(String inOldPriceDiv) {
        this.inOldPriceDiv = inOldPriceDiv;
    }

    public String getInDiscountDiv() {
        return inDiscountDiv;
    }

    public void setInDiscountDiv(String inDiscountDiv) {
        this.inDiscountDiv = inDiscountDiv;
    }

    public String getInEndDateDiv() {
        return inEndDateDiv;
    }

    public void setInEndDateDiv(String inEndDateDiv) {
        this.inEndDateDiv = inEndDateDiv;
    }

    public String getInnerDealLinkDiv() {
        return innerDealLinkDiv;
    }

    public void setInnerDealLinkDiv(String innerDealLinkDiv) {
        this.innerDealLinkDiv = innerDealLinkDiv;
    }

    public String getNameDiv() {
        return nameDiv;
    }

    public void setNameDiv(String nameDiv) {
        this.nameDiv = nameDiv;
    }

    public String getNewPriceDiv() {
        return newPriceDiv;
    }

    public void setNewPriceDiv(String newPriceDiv) {
        this.newPriceDiv = newPriceDiv;
    }

    public String getOldPriceDiv() {
        return oldPriceDiv;
    }

    public void setOldPriceDiv(String oldPriceDiv) {
        this.oldPriceDiv = oldPriceDiv;
    }

    public String getDiscountDiv() {
        return discountDiv;
    }

    public void setDiscountDiv(String discountDiv) {
        this.discountDiv = discountDiv;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public String getEndDateDiv() {
        return endDateDiv;
    }

    public void setEndDateDiv(String endDateDiv) {
        this.endDateDiv = endDateDiv;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getViewclick() {
        return viewclick;
    }

    public void setViewclick(int viewclick) {
        this.viewclick = viewclick;
    }
    
    
    
}
