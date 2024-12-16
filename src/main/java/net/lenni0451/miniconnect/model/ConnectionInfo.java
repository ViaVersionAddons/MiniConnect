package net.lenni0451.miniconnect.model;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public record ConnectionInfo(String host, int port, ProtocolVersion protocolVersion) {
}
