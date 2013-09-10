package org.mdpnp.clinicalscenarios.server.user;

@SuppressWarnings("serial")
public class LoginProvider implements java.io.Serializable {
    private String name, loginURL;
    
    public LoginProvider() {
    }
    public LoginProvider(String name, String loginURL) {
        this.name = name;
        this.loginURL = loginURL;
    }
    public String getLoginURL() {
        return loginURL;
    }
    public String getName() {
        return name;
    }
    public void setLoginURL(String loginURL) {
        this.loginURL = loginURL;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "["+name+","+loginURL+"]";
    }
}
