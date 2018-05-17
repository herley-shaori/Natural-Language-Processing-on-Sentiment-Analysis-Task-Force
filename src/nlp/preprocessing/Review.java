/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.preprocessing;

/**
 *
 * @author herley
 */
public class Review {
    
    public static final int POSITIVE=1,NEGATIVE=-1,UNKNOWN=0;
    public static final String positive="POSITIVE",negative="NEGATIVE",unknown="UNKNOWN";
    private String rawText,translatedText,foodPolarity,servicePolarity,pricePolarity,ambiencePolarity;
    private Integer foodPolarityInteger=null, servicePolarityInteger=null,pricePolarityInteger=null,ambiencePolarityInteger=null;

    /**
     * @return the rawText
     */
    public String getRawText() {
        return rawText;
    }

    /**
     * @param rawText the rawText to set
     */
    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    /**
     * @return the translatedText
     */
    public String getTranslatedText() {
        return translatedText;
    }

    /**
     * @param translatedText the translatedText to set
     */
    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    /**
     * @return the foodPolarity
     */
    public String getFoodPolarity() {
        return foodPolarity;
    }

    /**
     * @param foodPolarity the foodPolarity to set
     */
    public void setFoodPolarity(String foodPolarity) {
        this.foodPolarity = foodPolarity;
    }

    /**
     * @return the servicePolarity
     */
    public String getServicePolarity() {
        return servicePolarity;
    }

    /**
     * @param servicePolarity the servicePolarity to set
     */
    public void setServicePolarity(String servicePolarity) {
        this.servicePolarity = servicePolarity;
    }

    /**
     * @return the pricePolarity
     */
    public String getPricePolarity() {
        return pricePolarity;
    }

    /**
     * @param pricePolarity the pricePolarity to set
     */
    public void setPricePolarity(String pricePolarity) {
        this.pricePolarity = pricePolarity;
    }

    /**
     * @return the ambiencePolarity
     */
    public String getAmbiencePolarity() {
        return ambiencePolarity;
    }

    /**
     * @param ambiencePolarity the ambiencePolarity to set
     */
    public void setAmbiencePolarity(String ambiencePolarity) {
        this.ambiencePolarity = ambiencePolarity;
    }

    /**
     * @return the foodPolarityInteger
     */
    public Integer getFoodPolarityInteger() {
        return foodPolarityInteger;
    }

    /**
     * @param foodPolarityInteger the foodPolarityInteger to set
     */
    public void setFoodPolarityInteger(Integer foodPolarityInteger) {
        this.foodPolarityInteger = foodPolarityInteger;
    }

    /**
     * @return the servicePolarityInteger
     */
    public Integer getServicePolarityInteger() {
        return servicePolarityInteger;
    }

    /**
     * @param servicePolarityInteger the servicePolarityInteger to set
     */
    public void setServicePolarityInteger(Integer servicePolarityInteger) {
        this.servicePolarityInteger = servicePolarityInteger;
    }

    /**
     * @return the pricePolarityInteger
     */
    public Integer getPricePolarityInteger() {
        return pricePolarityInteger;
    }

    /**
     * @param pricePolarityInteger the pricePolarityInteger to set
     */
    public void setPricePolarityInteger(Integer pricePolarityInteger) {
        this.pricePolarityInteger = pricePolarityInteger;
    }

    /**
     * @return the ambiencePolarityInteger
     */
    public Integer getAmbiencePolarityInteger() {
        return ambiencePolarityInteger;
    }

    /**
     * @param ambiencePolarityInteger the ambiencePolarityInteger to set
     */
    public void setAmbiencePolarityInteger(Integer ambiencePolarityInteger) {
        this.ambiencePolarityInteger = ambiencePolarityInteger;
    }
    
}
