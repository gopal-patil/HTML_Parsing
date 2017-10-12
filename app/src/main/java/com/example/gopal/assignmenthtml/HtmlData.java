package com.example.gopal.assignmenthtml;

import java.io.Serializable;

import static com.example.gopal.assignmenthtml.MainActivity.BASE_URL;

/**
 * Created by Gopal on 11-10-2017.
 */

public class HtmlData implements Serializable {

    private String productUrl;
    private String productName;
    private String priceWithDiscount;
    private String priceWithoutDiscount;
    private String discount;
    private String imageUrl;
    private boolean discountVisibility;

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = BASE_URL + productUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPriceWithDiscount() {
        return priceWithDiscount;
    }

    public void setPriceWithDiscount(String priceWithDiscount) {
        this.priceWithDiscount = priceWithDiscount;
    }

    public String getPriceWithoutDiscount() {
        return priceWithoutDiscount;
    }

    public void setPriceWithoutDiscount(String priceWithoutDiscount) {
        this.priceWithoutDiscount = priceWithoutDiscount;
    }

    public String getDiscount() {
        if (discount != null && !discount.isEmpty()) {
            return discount = discount.substring(0, discount.indexOf("%") + 1);
        }
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
        setDiscountVisibility();
    }

    public String getImageUrl() {
        if (imageUrl != null && imageUrl.length() > 0) {
            return "https:" + imageUrl.substring(imageUrl.indexOf("('") + 2, imageUrl.indexOf("')"));
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {

        this.imageUrl = imageUrl;
    }

    public boolean isDiscountVisibility() {
        return discountVisibility;
    }

    public void setDiscountVisibility() {
        String discount = getDiscount();
        this.discountVisibility = (discount != null && discount.length() > 0 && discount.length() <= 3);
    }
}
