package net.lenni0451.miniconnect.server.states.play;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PlayerConfig {

    @Nullable
    public String serverAddress;
    @Nullable
    public Integer serverPort;
    @Nullable
    public ProtocolVersion targetVersion;

    public transient String handshakeAddress;
    public transient int handshakePort;
    public transient Consumer<String> chatListener;

    public boolean allowCloseScreen() {
        return this.chatListener != null;
    }

}
