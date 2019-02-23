package com.app.cardholder;

/**
 * Class for storing data about user's barcode card.
 * Contain information about card name, store place, card color and barcode value.
 */
public class Card {

    private String name;
    private String place;
    private String color;
    private String code;

    /**
     * Main initialize constructor
     *
     * @param name  - card name
     * @param place - store place
     * @param color - card color
     * @param code  - barcode
     */
    public Card(String name, String place, String color, String code) {
        this.name = name;
        this.place = place;
        this.color = color;
        this.code = code;
    }

    //getter & setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
