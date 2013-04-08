package org.mdpnp.apps.testapp;

import java.awt.Color;

public enum ComponentStatus {
    UNKOWN(Color.GRAY, Color.BLACK, "Unknown"),
    CONNECTED(Color.BLACK, Color.WHITE, "Connected"),
    ASSOCIATED(Color.BLUE, Color.WHITE, "Associated"),
    CERTIFIED(Color.GREEN, Color.BLACK, "Certified"),
    SENDING_DATA(Color.GREEN, Color.BLACK, "Sending Data"),
    DISCONNECTED(Color.RED, Color.BLACK, "Disconnected"),
    ON(Color.GREEN, Color.BLACK, "On"),
    OFF(Color.RED, Color.BLACK, "Off");
    
    private Color panelColor;
    private Color textColor;
    private String text;
    
    ComponentStatus(Color panelColor, Color textColor, String text) {
        this.panelColor = panelColor;
        this.textColor = textColor;
        this.text = text;
    }
    
    public Color getPanelColor() {
        return panelColor;
    }
    
    public Color getTextColor() {
        return textColor;
    }
    
    public String getText() {
        return text;
    }
}
