package net.lenni0451.miniconnect.proxy.via;

import com.viaversion.viaversion.api.connection.UserConnection;
import net.lenni0451.miniconnect.proxy.TransferHandler;
import net.raphimc.viaproxy.protocoltranslator.providers.ViaProxyTransferProvider;

public class HookedTransferProvider extends ViaProxyTransferProvider {

    @Override
    public void connectToServer(UserConnection user, String host, int port) {
        TransferHandler.handle(user.getChannel());
        super.connectToServer(user, host, port);
    }

}
