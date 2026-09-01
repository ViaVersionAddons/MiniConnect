package net.lenni0451.miniconnect.haproxy;

import com.google.common.net.HostAndPort;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.haproxy.*;
import net.lenni0451.miniconnect.model.HandshakeData;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class HAProxyUtil {

    public static HAProxyMessage createMessage(final Channel sourceChannel, final Channel targetChannel, final HostAndPort handshakeAddress, final ProtocolVersion clientVersion) {
        List<HAProxyTLV> tlvs = new ArrayList<>();
        ByteBuf handshakeBuf = Unpooled.buffer();
        new HandshakeData(handshakeAddress.getHost(), handshakeAddress.getPort(), clientVersion).write(handshakeBuf);
        tlvs.add(new HAProxyTLV((byte) 0xE0, handshakeBuf));

        if (sourceChannel.remoteAddress() instanceof InetSocketAddress sourceAddress && targetChannel.remoteAddress() instanceof InetSocketAddress targetAddress) {
            HAProxyProxiedProtocol protocol = sourceAddress.getAddress() instanceof Inet4Address ? HAProxyProxiedProtocol.TCP4 : HAProxyProxiedProtocol.TCP6;
            String sourceAddressString = sourceAddress.getAddress().getHostAddress();

            // The target address is set to the source address to prevent issues with IPv4 and IPv6 mismatches
            // Haproxy requires both addresses to be of the same type, which we can't guarantee here
            // The target address is never used, so it's safe to set it to the source address and prevent issues
            return new HAProxyMessage(HAProxyProtocolVersion.V2, HAProxyCommand.PROXY, protocol, sourceAddressString, sourceAddressString, sourceAddress.getPort(), targetAddress.getPort(), tlvs);
        } else if (targetChannel.remoteAddress() instanceof DomainSocketAddress targetAddress) {
            return new HAProxyMessage(HAProxyProtocolVersion.V2, HAProxyCommand.PROXY, HAProxyProxiedProtocol.UNIX_STREAM, "", targetAddress.path(), 0, 0, tlvs);
        } else {
            throw new IllegalArgumentException("Unsupported address type: " + targetChannel.remoteAddress().getClass().getName());
        }
    }

}
