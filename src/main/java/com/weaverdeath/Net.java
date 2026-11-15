package com.weaverdeath;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class Net {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WeaverDeathMod.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void init() {
        CHANNEL.registerMessage(0, DeathPacket.class, DeathPacket::encode, DeathPacket::decode, DeathPacket::handle);
    }

    /** Manda la animacion a TODOS los jugadores conectados. */
    public static void sendToAll(String playerName) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), new DeathPacket(playerName));
    }

    public static final class DeathPacket {
        private final String name;

        public DeathPacket(String name) {
            this.name = name;
        }

        public static void encode(DeathPacket p, FriendlyByteBuf buf) {
            buf.writeUtf(p.name, 64);
        }

        public static DeathPacket decode(FriendlyByteBuf buf) {
            return new DeathPacket(buf.readUtf(64));
        }

        public static void handle(DeathPacket p, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context c = ctx.get();
            c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> com.weaverdeath.client.ClientAnim.start(p.name)));
            c.setPacketHandled(true);
        }
    }

    private Net() {
    }
}
