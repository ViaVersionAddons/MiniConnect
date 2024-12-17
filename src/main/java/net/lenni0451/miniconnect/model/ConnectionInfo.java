package net.lenni0451.miniconnect.model;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.raphimc.viaproxy.saves.impl.accounts.Account;

import javax.annotation.Nullable;

public record ConnectionInfo(String handshakeAddress, int handshakePort, String host, int port, ProtocolVersion protocolVersion, @Nullable Account account) {
}
