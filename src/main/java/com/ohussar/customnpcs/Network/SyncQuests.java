package com.ohussar.customnpcs.Network;

import java.util.function.Supplier;

import com.ohussar.customnpcs.CustomNpcs;
import com.ohussar.customnpcs.Quests.Quest;
import com.ohussar.customnpcs.Quests.Quests;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class SyncQuests {
    public String json;
    public SyncQuests(String json){
        this.json = json;
    }
    public SyncQuests(FriendlyByteBuf buf){
        this.json = new String((buf.readByteArray()));
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeByteArray(json.getBytes());
    }
    public static void handle(SyncQuests a, Supplier<NetworkEvent.Context> context){
        CustomNpcs.LOGGER.info("null");
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,  () -> () -> SyncQuestsHandler.handlePacket(a, context));
        });
        context.get().setPacketHandled(true);
    }
}
