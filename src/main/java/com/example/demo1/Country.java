package com.example.demo1;

//This class will be used in the country code for the contact number field in the applicant form
import javafx.scene.image.Image;

public class Country {
    private String name;
    private String dialCode;
    private Image flag;

    public Country(String name, String dialCode, String flagPath) {
        this.name = name;
        this.dialCode = dialCode;
        this.flag = new Image(getClass().getResourceAsStream(flagPath)); // Load flag image
    }

    public String getName() {
        return name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public Image getFlag() {
        return flag;
    }

    @Override
    public String toString() {
        return dialCode + " (" + name + ")";
    }
}
