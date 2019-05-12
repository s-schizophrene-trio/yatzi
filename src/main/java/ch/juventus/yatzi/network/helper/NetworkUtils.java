package ch.juventus.yatzi.network.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * The Network Utils holds all methods which are used for server, client and setup of this game.
 * @author Jan Minder
 */
public class NetworkUtils {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Determine the local ip of the users computer
     * @return A String with the determined IP Address in it
     */
    public String getLocalIP() {
        String localIp = "no ip found";
        try {
            LOGGER.debug("determine local ip address");
            InetAddress inetAddress = InetAddress.getLocalHost();
            localIp = inetAddress.getHostAddress();
        } catch (Exception e) {
            LOGGER.error("Failed to determine local ip address: {}", e.getMessage());
        }
        return localIp;
    }

    /**
     * Gets the local host name
     * @return A String with the full local host name in it
     */
    public String getLocalHostName() {
        String localHostname = "no hostname found";
        try {
            LOGGER.debug("determine local hostname");
            InetAddress inetAddress = InetAddress.getLocalHost();
            localHostname = inetAddress.getHostName();
        } catch (Exception e) {
            LOGGER.error("Failed to determine local hostname: {}", e.getMessage());
        }
        return localHostname;
    }
}
