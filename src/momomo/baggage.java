package momomo;

/**
 * This baggage file is required to avoid having to generate any java-doc, something sonatype insist on is required which is quite frankly an absurd requirement in order to share code.
 *
 * Our code is very well commented, however, we prefer to format code for those visiting our source code from within their editor.
 *
 * While javadoc has its use, for some of our classes we do not need it.
 *
 * While we are here, we might also take the oppurtonity to discuss why we choose to use momomo.com rather than com.momomo as we are sure some are asking themselves.
 *
 * Frankly we see no reason as to why packages are named in reverse. It makes no sense. If your domain is momomo.com why would you type it as com.momomo?
 * Well, you argue, and those before you have argued that due to domains being able to declare a subdomain, i.e io.momomo.com, then you would put code related to io in
 * com.momomo.io which makes kind of sense, although you are not likely going to visit momomo.com.io to get the javadoc for that package, so the connection to a domain is wierd to begin with.
 *
 * It is not an address.
 *
 * So, since connecting subdomains is kind of pointless, we'd just prefer to slash and keep the domain intact. So if you want to visit the documentation for the io package, do not visit momomo.com/io!
 *
 * @author Joseph S.
 **/
public final class baggage { private baggage(){} }