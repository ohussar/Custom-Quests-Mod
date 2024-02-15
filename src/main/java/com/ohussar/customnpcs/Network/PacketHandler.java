package com.ohussar.customnpcs.Network;

import com.ohussar.customnpcs.CustomNpcs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static int id = 0;
    private static final String PROTOCOL_VERSION = "1.3";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(CustomNpcs.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );


    public static void register(){
        INSTANCE.messageBuilder(SyncQuests.class, id++)
        .encoder(SyncQuests::encode)
        .decoder(SyncQuests::new)
        .consumerMainThread(SyncQuests::handle)
        .add();
        INSTANCE.messageBuilder(PlayerClaimTask.class, id++)
        .encoder(PlayerClaimTask::encode)
        .decoder(PlayerClaimTask::new)
        .consumerMainThread(PlayerClaimTask::handle)
        .add();
        INSTANCE.messageBuilder(PlayerClaimTaskClient.class, id++)
        .encoder(PlayerClaimTaskClient::encode)
        .decoder(PlayerClaimTaskClient::new)
        .consumerMainThread(PlayerClaimTaskClient::handle)
        .add();
        INSTANCE.messageBuilder(SyncInventory.class, id++)
        .encoder(SyncInventory::encode)
        .decoder(SyncInventory::new)
        .consumerMainThread(SyncInventory::handle)
        .add();
        INSTANCE.messageBuilder(FinishQuest.class, id++)
        .encoder(FinishQuest::encode)
        .decoder(FinishQuest::new)
        .consumerMainThread(FinishQuest::handle)
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
