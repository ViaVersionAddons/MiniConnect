package net.lenni0451.miniconnect.proxy.event;

import com.viaversion.viabackwards.protocol.v1_20_5to1_20_3.provider.TransferProvider;
import com.viaversion.viaversion.api.Via;
import net.lenni0451.lambdaevents.EventHandler;
import net.lenni0451.miniconnect.proxy.via.HookedTransferProvider;
import net.raphimc.viaproxy.plugins.events.ViaLoadingEvent;

public class ViaLoadHandler {

    @EventHandler
    public void onViaLoading(final ViaLoadingEvent event) {
        Via.getManager().getProviders().use(TransferProvider.class, new HookedTransferProvider());
    }

}
