package at.rtr.rmbt.model;

/**
 * Result of a client-IP lookup against Apple's iCloud Private Relay egress ranges. A non-null
 * instance means the IP belongs to a known Private Relay egress ("is a proxy"); the geographic
 * fields describe where that egress is located (they may be null/blank when the source omits them).
 */
public record ProxyInfo(String proxyCountry, String proxyRegion, String proxyCity, boolean ipv6) {
}
