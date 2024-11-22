package momomo.com.sources;

import momomo.com.Strings;
import momomo.com.annotations.informative.Beta;
import momomo.com.Ex;
import momomo.com.IO;
import momomo.com.Is;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import static momomo.com.IO.toURL;

/**
 * SSL related.
 * 
 * @see momomo.com.IO#SSL for direct usage
 * 
 * Example usage 
 *    IO.SSL.toConnection(...) 
 * 
 * @author Joseph S.
 */
@Beta public final class SSL {
    
    /**
     * More for a template on how to setup a basic ssl connection 
     */
    public HttpsURLConnection toConnection(String url) {
        return toConnection(url, "SSL", TrustAll.Hostnames.SINGLETON, new SecureRandom());
    }
    
    /**
     * More for a template on how to setup a basic ssl connection 
     */
    public HttpsURLConnection toConnection(String url, String type, HostnameVerifier verifier, SecureRandom randomizer) {
        return toConnection(toURL(url), type, verifier, randomizer);
    }
    
    /**
     * More for a template on how to setup a basic ssl connection 
     */
    public HttpsURLConnection toConnection(URL url, String type, HostnameVerifier verifier, SecureRandom randomizer) {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trust = new TrustManager[]{TrustAll.Certificates.SINGLETON};
            
            // Install the all-trusting trust manager
            SSLContext context = SSLContext.getInstance(type);     // TLSv1.2
            context.init(null, trust, randomizer);
            
            HttpsURLConnection connection = (HttpsURLConnection) IO.toConnection(url);
            
            // Guard against "bad hostname" errors during handshake.
            connection.setHostnameVerifier(verifier);
            
            connection.setSSLSocketFactory(context.getSocketFactory());
            
            return connection;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw Ex.runtime(e);
        }
    }
    
    /**
     * Wrapper class only. 
     */
    public static final class TrustAll { private TrustAll(){}
    
        /**
         * 
         * Example
         *  
         *  Ssl.TrustAll.Certificates.SINGLETON
         * 
         * @singleton
         * 
         *
         * @author Joseph S.
         */
        public static class Certificates implements X509TrustManager { private Certificates() {}
            public static final Certificates SINGLETON = new Certificates();
            
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            
            public void checkClientTrusted( X509Certificate[] certs, String authType ) {}
            public void checkServerTrusted( X509Certificate[] certs, String authType ) {}
        }
    
        /**
         * Example
         * 
         *  Ssl.TrustAll.Hostnames.SINGLETON
         * 
         * @singleton
         *
         * @author Joseph S.
         */
        public static class Hostnames implements HostnameVerifier {
            public static final Hostnames SINGLETON = new Hostnames();
        
            private Hostnames() {}
            public boolean verify( String host, SSLSession sess ) {
                return true;
            }
        }
        
    }
    
    /**
     * @author Joseph S.
     */
    @momomo.com.annotations.informative.Development
    public static final class Development {
        
        public static String getText(CharSequence url) {
            return getText(url, Strings.CHARSET);
        }
        
        public static String getText(CharSequence url, Charset charset) {
            return getText( toURL(url), charset);
        }
        
        public static String getText(URL url) {
            return getText(url, Strings.CHARSET);
        }
        
        public static String getText(URL url, Charset charset) {
            HttpsURLConnection connection = (HttpsURLConnection) IO.toConnection(url);
            
            if ( !Is.Production() ) {
                printCertificateInformation(connection);
            }
            
            return IO.text(connection, charset);
        }
        
        /**
         * Used in development
         */
        public static void printCertificateInformation(HttpsURLConnection connection) {
            try {
                if (connection != null) {
                    System.out.println("Connection Response Code : " + connection.getResponseCode() );
                    System.out.println("Connection Cipher Suite  : "  + connection.getCipherSuite() );
                    
                    System.out.println(Strings.NEWLINE);
                    
                    Certificate[] certificates = connection.getServerCertificates();
                    for (Certificate certificate : certificates) {
                        System.out.println("Certification Type : " + certificate.getType());
                        System.out.println("Certification Hash Code : " + certificate.hashCode());
                        System.out.println("Certification Public Key Algorithm : " + certificate.getPublicKey().getAlgorithm());
                        System.out.println("Certification Public Key Format : " + certificate.getPublicKey().getFormat());
                        
                        System.out.println(Strings.NEWLINE);
                    }
                    
                }
            }
            catch(IOException e) {
                throw Ex.runtime(e);
            }
        }
    }
    
}
