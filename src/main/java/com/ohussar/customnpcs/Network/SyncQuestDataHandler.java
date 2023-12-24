package com.ohussar.customnpcs.Network;

import java.util.function.Supplier;

import com.ohussar.customnpcs.CustomNpc;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class SyncQuestDataHandler { 
    public static void handlePacket(SyncQuestData a, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            Entity npc = context.get().getSender().level().getEntity(a.EntityId);
            
            if(npc instanceof CustomNpc n){
                n.quests = a.quests;
            }
        });
    }
}
