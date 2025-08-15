package net.lenni0451.miniconnect.server.protocol.packets.play.s2c;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.raphimc.netminecraft.packet.Packet;

@NoArgsConstructor
@AllArgsConstructor
public class S2CPlayerAbilitiesPacket implements Packet {

    public boolean invulnerable;
    public boolean isFlying;
    public boolean canFly;
    public boolean instabuild;
    public float flyingSpeed;
    public float walkingSpeed;


    @Override
    public void read(ByteBuf byteBuf, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf byteBuf, int i) {
        int flags = 0;
        if (this.invulnerable) flags |= 0b1;
        if (this.isFlying) flags |= 0b10;
        if (this.canFly) flags |= 0b100;
        if (this.instabuild) flags |= 0b1000;
        byteBuf.writeByte(flags);
        byteBuf.writeFloat(this.flyingSpeed);
        byteBuf.writeFloat(this.walkingSpeed);
    }

}
