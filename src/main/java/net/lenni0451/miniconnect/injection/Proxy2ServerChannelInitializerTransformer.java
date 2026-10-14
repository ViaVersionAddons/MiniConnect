package net.lenni0451.miniconnect.injection;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.AttributeKey;
import net.lenni0451.classtransform.annotations.CInline;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CRedirect;
import net.lenni0451.miniconnect.model.AttributeKeys;
import net.raphimc.viaproxy.proxy.proxy2server.Proxy2ServerChannelInitializer;

@CTransformer(Proxy2ServerChannelInitializer.class)
public class Proxy2ServerChannelInitializerTransformer {

    @CInline
    @CRedirect(method = "initChannel", target = @CTarget(value = "INVOKE", target = "Lio/netty/channel/ChannelPipeline;addLast(Ljava/lang/String;Lio/netty/channel/ChannelHandler;)Lio/netty/channel/ChannelPipeline;"))
    private ChannelPipeline addLast(final ChannelPipeline pipeline, final String name, final ChannelHandler handler) {
        // Don't directly access the AttributeKeys class because of ClassLoader things
        // Using the name works fine because the compiler inlines the static final field
        AttributeKey<Boolean> key = AttributeKey.valueOf(AttributeKeys.SKIP_BACKEND_PROXY_NAME);
        if (name.equals(Proxy2ServerChannelInitializer.VIAPROXY_PROXY_HANDLER_NAME) && pipeline.channel().attr(key).get() != null) {
            return pipeline;
        } else {
            return pipeline.addLast(name, handler);
        }
    }

}
