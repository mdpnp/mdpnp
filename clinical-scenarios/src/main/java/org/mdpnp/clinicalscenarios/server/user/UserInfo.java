package org.mdpnp.clinicalscenarios.server.user;

import static org.mdpnp.clinicalscenarios.server.OfyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.OnSave;

@SuppressWarnings("serial")
@Entity
public class UserInfo implements java.io.Serializable {
    @Id
    private String userId;
    private Integer version = 1;

    @OnSave
    void onPersist() {
        version++;
    }

    public String getUserId() {
        return userId;
    }

    protected void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getHighestLevelOfEducation() {
        return highestLevelOfEducation;
    }

    public void setHighestLevelOfEducation(String highestLevelOfEducation) {
        this.highestLevelOfEducation = highestLevelOfEducation;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getYearsInField() {
        return yearsInField;
    }

    public void setYearsInField(String yearsInField) {
        this.yearsInField = yearsInField;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAgreeToBeContacted() {
        return agreeToBeContacted;
    }

    public void setAgreeToBeContacted(boolean agreeToBeContacted) {
        this.agreeToBeContacted = agreeToBeContacted;
    }

    private String email;
    private String title;
    private String givenName;
    private String familyName;
    private String highestLevelOfEducation;
    private String company;
    private String jobTitle;
    private String yearsInField;
    private String phoneNumber;
    private boolean agreeToBeContacted;

    @Ignore
    private String logoutURL;
    @Ignore
    private List<LoginProvider> loginURL;
    @Ignore
    private boolean admin;

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public String getLogoutURL() {
        return logoutURL;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    protected void setLogoutURL(String logoutURL) {
        this.logoutURL = logoutURL;
    }

    public List<LoginProvider> getLoginURL() {
        return loginURL;
    }

    protected void setLoginURL(List<LoginProvider> loginURL) {
        this.loginURL = loginURL;
    }

    public UserInfo() {

    }

    private static final Map<String, String> openIdProviders;
    static {
        openIdProviders = new HashMap<String, String>();
        openIdProviders.put("Google", "https://www.google.com/accounts/o8/id");
        openIdProviders.put("Yahoo", "yahoo.com");
        openIdProviders.put("MySpace", "myspace.com");
        openIdProviders.put("AOL", "aol.com");
        openIdProviders.put("MyOpenId.com", "myopenid.com");
    }

    public static List<UserInfo> findAllUserInfo() {
        return ofy().load().type(UserInfo.class).list();
    }

    public static UserInfo findCurrentUserInfo(String url) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        UserInfo ui = null;

        if (null == user) {
            // Create a non-persisted temporary object
            // client will find a null userid and null email and
            // know to only examine login and logout urls
            ui = new UserInfo();
            Set<String> attributes = new HashSet<String>();
            List<LoginProvider> loginURL = new ArrayList<LoginProvider>(openIdProviders.size());
            for (String k : openIdProviders.keySet()) {
                loginURL.add(new LoginProvider(k, userService.createLoginURL(url, null, openIdProviders.get(k),
                        attributes)));
            }
            ui.setLoginURL(loginURL);
        } else {
            ui = ofy().load().type(UserInfo.class).id(user.getUserId()).now();

            if (null == ui) {
                // This user is authenticated but we do not have any information
                // about them
                ui = new UserInfo();
                ui.setUserId(user.getUserId());
                ui.setEmail(user.getEmail());
                ofy().save().entity(ui).now();
            } else {
                // This is an authenticated user that we know
            }

        }
        ui.setAdmin(userService.isUserLoggedIn() && userService.isUserAdmin());
        ui.setLogoutURL(userService.createLogoutURL(url));

        return ui;

    }

    public UserInfo persist() {
        ofy().save().entity(this).now();
        return this;
    }
}
