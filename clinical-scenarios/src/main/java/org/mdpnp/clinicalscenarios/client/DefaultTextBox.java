package org.mdpnp.clinicalscenarios.client;

import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DefaultTextBox extends TextBox implements FocusListener { 
    String defaultText; 
    boolean defaultTextMode = false; 

    public DefaultTextBox(String defaultText) { 
            setDefaultText(defaultText); 
            setDefaultTextMode(); 
            addFocusListener(this); 
    } 

    public String getDefaultText() { 
            return defaultText; 
    } 

    public String getText() { 
            if (!defaultTextMode) { 
                    return super.getText(); 
            } else { 
                    return ""; 
            } 
    } 

    public void onFocus(Widget sender) { 
            if (defaultTextMode) { 
                    setNormalTextMode(); 
            } 
    } 

    public void onLostFocus(Widget sender) { 
            if (getText().length() == 0) { 
                    setDefaultTextMode(); 
            } 
    } 

    public void setDefaultText(String defaultText) { 
            this.defaultText = defaultText; 
            if (defaultTextMode) { 
                    setDefaultTextMode();        // Refresh 
            } 
    } 

    void setDefaultTextMode() { 
            assert super.getText().length() == 0; 
            super.setText(defaultText); 
            addStyleDependentName("default"); 
            defaultTextMode = true; 
    } 

    void setNormalTextMode() { 
            assert super.getText().length() != 0; 
            super.setText(""); 
            removeStyleDependentName("default"); 
            defaultTextMode = false; 
    } 

    public void setText(String text) { 
            super.setText(text); 
            if (text.length() == 0) { 
                    setDefaultTextMode(); 
            } else { 
                    setNormalTextMode(); 
            } 
    } 
} 
