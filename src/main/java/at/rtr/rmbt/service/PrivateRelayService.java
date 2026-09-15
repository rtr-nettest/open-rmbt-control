package at.rtr.rmbt.service;

import at.rtr.rmbt.model.ProxyInfo;

import java.net.InetAddress;
import java.util.Optional;

/**
 * Looks up client IPs against Apple's iCloud Private Relay egress ranges, which are downloaded
 * periodically and cached in Redis. A present {@link ProxyInfo} means the address is a known
 * Private Relay egress (a "proxy").
 */
public interface PrivateRelayService {

    /**
     * @return the matching Private Relay egress info (longest-prefix match), or empty when the
     * address is not a known egress, no data has been loaded yet, or the store is unavailable.
     * Never throws for a lookup failure — a failed lookup is treated as "not a proxy".
     */
    Optional<ProxyInfo> lookup(InetAddress ip);
}
