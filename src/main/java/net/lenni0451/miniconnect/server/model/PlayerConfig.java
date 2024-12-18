package net.lenni0451.miniconnect.server.model;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.lenni0451.miniconnect.model.ConnectionInfo;
import net.raphimc.viaproxy.saves.impl.accounts.Account;
import net.raphimc.viaproxy.util.AddressUtil;

import javax.annotation.Nullable;
import java.util.function.Function;

public class PlayerConfig {

    public static PlayerConfig fromConnectionInfo(final ConnectionInfo connectionInfo) {
        PlayerConfig playerConfig = new PlayerConfig();
        playerConfig.serverAddress = connectionInfo.host();
        playerConfig.serverPort = connectionInfo.port();
        playerConfig.targetVersion = connectionInfo.protocolVersion();
        playerConfig.account = connectionInfo.account();
        return playerConfig;
    }


    @Nullable
    public String serverAddress;
    @Nullable
    public Integer serverPort;
    @Nullable
    public ProtocolVersion targetVersion;
    @Nullable
    public Account account;

    public transient String handshakeAddress;
    public transient int handshakePort;
    public transient Function<String, Boolean> chatListener;

    public ConnectionInfo toConnectionInfo() {
        int serverPort = this.serverPort == null || this.serverPort == -1 ? AddressUtil.getDefaultPort(this.targetVersion) : this.serverPort;
        return new ConnectionInfo(this.handshakeAddress, this.handshakePort, this.serverAddress, serverPort, this.targetVersion, this.account);
    }

}
