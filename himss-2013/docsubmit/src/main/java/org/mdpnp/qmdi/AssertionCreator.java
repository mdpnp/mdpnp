package org.mdpnp.qmdi;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.CeType;
import gov.hhs.fha.nhinc.common.nhinccommon.HomeCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.PersonNameType;
import gov.hhs.fha.nhinc.common.nhinccommon.UserType;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Jeff Plourde
 *
 */
public class AssertionCreator {
    private static final String PROPERTY_FILE_NAME = "assertion.properties";
    private static final String PROPERTY_KEY_PURPOSE_CODE = "AssertionPurposeCode";
    private static final String PROPERTY_KEY_PURPOSE_SYSTEM = "AssertionPurposeSystem";
    private static final String PROPERTY_KEY_PURPOSE_SYSTEM_NAME = "AssertionPurposeSystemName";
    private static final String PROPERTY_KEY_PURPOSE_DISPLAY = "AssertionPurposeDisplay";
    private static final String PROPERTY_KEY_USER_FIRST = "AssertionUserFirstName";
    private static final String PROPERTY_KEY_USER_MIDDLE = "AssertionUserMiddleName";
    private static final String PROPERTY_KEY_USER_LAST = "AssertionUserLastName";
    private static final String PROPERTY_KEY_USER_NAME = "AssertionUserName";
    private static final String PROPERTY_KEY_USER_ORG = "AssertionUserOrganization";
    private static final String PROPERTY_KEY_USER_CODE = "AssertionUserCode";
    private static final String PROPERTY_KEY_USER_SYSTEM = "AssertionUserSystem";
    private static final String PROPERTY_KEY_USER_SYSTEM_NAME = "AssertionUserSystemName";
    private static final String PROPERTY_KEY_USER_DISPLAY = "AssertionUserDisplay";

    public final static AssertionType createAssertion() throws IOException {
    	Properties props = new java.util.Properties();
    	java.io.InputStream is = null;
    	try {
    		is = AssertionCreator.class.getResourceAsStream(PROPERTY_FILE_NAME);
    		props.load(is);
    	} finally {
    		if(null != is) {
    			is.close();
    		}
    	}
    	
    	 
    	return createAssertion(props);
    }
    
    public final static AssertionType createAssertion(Properties props)  {
        AssertionType assertOut = new AssertionType();
        CeType purposeCoded = new CeType();
        UserType user = new UserType();
        PersonNameType userPerson = new PersonNameType();
        CeType userRole = new CeType();
        HomeCommunityType userHc = new HomeCommunityType();
        userHc.setHomeCommunityId(props.getProperty(PROPERTY_KEY_USER_SYSTEM));
        user.setPersonName(userPerson);
        user.setOrg(userHc);
        user.setRoleCoded(userRole);
        assertOut.setUserInfo(user);
        assertOut.setPurposeOfDisclosureCoded(purposeCoded);
        assertOut.setHomeCommunity(userHc);

        userPerson.setGivenName(props.getProperty(PROPERTY_KEY_USER_FIRST));
        userPerson.setFamilyName(props.getProperty(PROPERTY_KEY_USER_LAST));
        userPerson.setSecondNameOrInitials(props.getProperty(PROPERTY_KEY_USER_MIDDLE));
        userHc.setName(props.getProperty(PROPERTY_KEY_USER_ORG));
        user.setUserName(props.getProperty(PROPERTY_KEY_USER_NAME));
        userRole.setCode(props.getProperty(PROPERTY_KEY_USER_CODE));
        userRole.setCodeSystem(props.getProperty(PROPERTY_KEY_USER_SYSTEM));
        userRole.setCodeSystemName(props.getProperty(PROPERTY_KEY_USER_SYSTEM_NAME));
        userRole.setDisplayName(props.getProperty(PROPERTY_KEY_USER_DISPLAY));

        purposeCoded.setCode(props.getProperty(PROPERTY_KEY_PURPOSE_CODE));
        purposeCoded.setCodeSystem(props.getProperty(PROPERTY_KEY_PURPOSE_SYSTEM));
        purposeCoded.setCodeSystemName(props.getProperty(PROPERTY_KEY_PURPOSE_SYSTEM_NAME));
        purposeCoded.setDisplayName(props.getProperty(PROPERTY_KEY_PURPOSE_DISPLAY));

        return assertOut;
    }

}
