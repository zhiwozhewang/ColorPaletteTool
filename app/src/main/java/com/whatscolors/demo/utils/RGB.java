package com.whatscolors.demo.utils;

public class RGB {
    public int red;
    public int green;
    public int blue;

    public RGB() {
    }

    public RGB(int red, int green, int blue) {
        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    public String toString() {
        return "RGB {" + red + ", " + green + ", " + blue + "}";
    }
}