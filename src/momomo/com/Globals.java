package momomo.com;

import momomo.com.sources.Sysprop;

/**
 * @author Joseph S.
 */
public class Globals {
    
    /**
     * These can't really be configured as of now for us, since we are still using Spring annotations to map our controller / actions and they have to be static and final!
     * 
     * Unfortunately! We will get rid of that soon as well. Time to implement a pure HttpServlet.  
     */
    public static final String SERVER_URL = "/server/";
    public static final String ASSETS     = "assets/";
    public static final String ASSETS_URL = SERVER_URL + ASSETS;
    
    public static class Configurable {
        public static final Sysprop ENVIRONMENT_ALLOW_DEVELOPMENT_OVER_PRODUCTION = new Sysprop("momomo.com.environment.allow.development.over.production", false);
    
        /**
         * Has to be on top. 
         * 
         * Since we have some modules dependending on Spring this might also needs to be set for production. 
         */
        public static final Sysprop ENVIRONMENT_SPRING_PROFILE                    = new Sysprop("spring.profiles.active");
    
        /**
         * We start explaining here. SysProp = System.getProperty is a utility class that allows you to get a default value, yet allows it to be supplied though the command line or set programmatically later.
         */
        public static final Sysprop DATABASE_ENVIRONMENT                          = new Sysprop("momomo.com.database.environment"     , $Environment.active() );
        public static final Sysprop DATABASE_DROP_ALL                             = new Sysprop("momomo.com.database.drop.all"        , !Is.Production() );
        public static final Sysprop DATABASE_SERVER_PROTOCOL                      = new Sysprop("momomo.com.database.server.protocol" , "jdbc:postgresql://");
                                                                                  
        public static final Sysprop DATABASE_SERVER_HOST                          = new Sysprop("momomo.com.database.server.host"     , "localhost");
        public static final Sysprop DATABASE_SERVER_PORT                          = new Sysprop("momomo.com.database.server.port"     , "5432");
        public static final Sysprop DATABASE_SERVER_USERNAME                      = new Sysprop("momomo.com.database.server.username" , "postgres");
        public static final Sysprop DATABASE_SERVER_PASSWORD                      = new Sysprop("momomo.com.database.server.password" , "postgres");
        public static final Sysprop DATABASE_SQL_LOGGING                          = new Sysprop("momomo.com.database.sql.logging"     , !Is.Production());
    
        
    }
}
