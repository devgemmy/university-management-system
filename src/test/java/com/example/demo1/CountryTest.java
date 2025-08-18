package com.example.demo1;
import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CountryTest {

    @Test
    public void testCountryCreation() {
        Country country = new Country("United States", "+1", "/com/example/demo1/flags/united-states.png");

        assertEquals("United States", country.getName());
        assertEquals("+1", country.getDialCode());
        assertNotNull(country.getFlag());
    }

    @Test
    public void testCountryToString() {
        Country country = new Country("United States", "+1", "/com/example/demo1/flags/united-states.png");

        assertEquals("+1 (United States)", country.toString());
    }

    @Test
    public void testFlagLoading() {
        Country country = new Country("United States", "+1", "/com/example/demo1/flags/united-states.png");

        Image flag = country.getFlag();
        assertNotNull(flag);

        // Check the actual dimensions of the image used in your project
        System.out.println("Flag Width: " + flag.getWidth());
        System.out.println("Flag Height: " + flag.getHeight());

        // Adjust these values based on the actual dimensions of your image
        assertEquals(512.0, flag.getWidth());
        assertEquals(512.0, flag.getHeight());
    }
}