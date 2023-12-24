package com.ohussar.customnpcs.Network;

import java.util.function.Supplier;

import com.ohussar.customnpcs.Quests.Quest;
import com.ohussar.customnpcs.Quests.Quests;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class SyncQuestData {
    public Quest[] quests = new Quest[3];
    public int EntityId;
    public SyncQuestData(Quest[] quests, int id){
        this.quests = quests;
        this.EntityId = id;
    }
    public SyncQuestData(FriendlyByteBuf buf){
        this.EntityId = buf.readInt();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            this.quests[i] = Quests.handle_quest_id(buf.readInt());
        }

    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(EntityId);
        buf.writeInt(quests.length);
        for(int i = 0; i < quests.length; i++){
            buf.writeInt(quests[i].id);
        }
    }
    public static void handle(SyncQuestData a, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,  () -> () -> SyncQuestDataHandler.handlePacket(a, context));
        });
        context.get().setPacketHandled(true);
    }
}
