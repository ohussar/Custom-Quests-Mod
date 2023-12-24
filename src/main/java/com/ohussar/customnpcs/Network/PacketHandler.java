package com.ohussar.customnpcs.Network;

import com.ohussar.customnpcs.CustomNpcs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static int id = 0;
    private static final SimpleChannel INSTANCE = ChannelBuilder.named(
        new ResourceLocation(CustomNpcs.MODID, "main"))
        .serverAcceptedVersions(version -> true)
        .clientAcceptedVersions(version -> true)
        .networkProtocolVersion(() -> "1")
        .simpleChannel();

    public static void register(){
        INSTANCE.messageBuilder(SyncQuestData.class, id++)
        .encoder(SyncQuestData::encode)
        .decoder(SyncQuestData::new)
        .consumerMainThread(SyncQuestData::handle)
        .add();
         INSTANCE.messageBuilder(SyncQuests.class, id++)
        .encoder(SyncQuests::encode)
        .decoder(SyncQuests::new)
        .consumerMainThread(SyncQuests::handle)
        .add();
    }

    public static void sendToServer(Object msg){
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }
    public static void sendToPlayer(Object msg, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
    public static void sendToAllClients(Object msg){
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}
