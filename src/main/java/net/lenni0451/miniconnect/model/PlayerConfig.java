package net.lenni0451.miniconnect.model;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.raphimc.viaproxy.saves.impl.accounts.Account;

import javax.annotation.Nullable;
import java.util.function.Function;

public class PlayerConfig {

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

    public boolean allowCloseScreen() {
        return this.chatListener != null;
    }

}
