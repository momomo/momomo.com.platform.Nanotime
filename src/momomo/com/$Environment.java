/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com;

import momomo.com.exceptions.$RuntimeException;

import static momomo.com.Globals.Configurable.ENVIRONMENT_ALLOW_DEVELOPMENT_OVER_PRODUCTION;

/**
 * Example: 
 *  Is.Production()
 *  Is.Test()
 *  Is.Development()
 *
 *  
 *  !Is.Production()    => true if Is.Test()       || Is.Development()
 *  !Is.Test()          => true if Is.Production() || Is.Development()
 *  !Is.Development()   => true if Is.Production() || Is.Test()
 *  
 *  Is.Production(true) => is false, if Is.Production() and the special system property 'momomo.com.environment.allow.development.over.production' flag has been set to true.   
 * 
 * @author Joseph S.
 */
public abstract class $Environment {
    public static final String PRODUCTION = "production", DEVELOPMENT = "development", TEST = "test";
    
    // Determined once only. Once set, it can not be modified.
    private static String PROFILE;
    
    // This are not final, because we allow calls to setTest, setProduction to be made, and so on the
    private static Boolean isProduction, isDevelopment, isTest;
    
    public static String active() {
        return Is.Or(getProfile(), DEVELOPMENT);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Should be called before any call is made to isProduction, isDevelopment or isTest
     */
    public static void setProduction() {
        setEnvironment(PRODUCTION);
    }
    /**
     * Should be called before any call is made to isProduction, isDevelopment or isTest
     */
    public static void setDevelopment() {
        setEnvironment(DEVELOPMENT);
    }
    /**
     * Should be called before any call is made to isProduction, isDevelopment or isTest
     */
    public static void setTest() {
        setEnvironment(TEST);
    }
    
    /////////////////////////////////////////////////////////////////////
    
    /**
     * Package private. Use Is.Development() instead. 
     */
    static boolean isDevelopment() {
        if (isDevelopment == null) {
            return (isDevelopment = DEVELOPMENT.equals(getProfile()) || (!isProduction() && !isTest()));
        }
        return isDevelopment;
    }
    
    /**
     * Package private. Use Is.Test() instead. 
     */
    static boolean isTest() {
        if (isTest == null) {
            return (isTest = TEST.equals(getProfile()));
        }
        return isTest;
    }
    
    /**
     * Package private. Use Is.Production() instead. 
     */
    static boolean isProduction() {
        if (isProduction == null) {
            return (isProduction = PRODUCTION.equals(getProfile()));
        }
        return isProduction;
    }
    
    /**
     * Package private. Use Is.Production(loose) instead.
     */
    static boolean isProduction(boolean loose) {
        return isProduction() && (loose == false || !ENVIRONMENT_ALLOW_DEVELOPMENT_OVER_PRODUCTION.isTrue());
    }
    
    /////////////////////////////////////////////////////////////////////
    
    private static String getProfile() {
        if (PROFILE == null) {
            PROFILE = Globals.Configurable.ENVIRONMENT_SPRING_PROFILE.get();
        }
        
        return PROFILE;
    }
    /**
     * Should be called before any call is made to isProduction, isDevelopment or isTest
     */
    private static void setEnvironment(String profile) {
        if (PROFILE == null) {
            if (PRODUCTION.equals(profile) || DEVELOPMENT.equals(profile) || TEST.equals(profile)) {
                PROFILE = Globals.Configurable.ENVIRONMENT_SPRING_PROFILE.set(profile).get();
            }
            else {
                throw new $RuntimeException("Invalid profile value: " + profile);
            }
        }
        else {
            throw new $RuntimeException("The active profile has already been set! Too late to change it now! Current value: " + PROFILE);
        }
    }
    
}
