package com.ohussar.customnpcs.Network;

import java.util.function.Supplier;

import com.ohussar.customnpcs.Config;
import com.ohussar.customnpcs.CustomNpcs;

import net.minecraftforge.network.NetworkEvent;

public class SyncQuestsHandler { 
    public static void handlePacket(SyncQuests a, Supplier<NetworkEvent.Context> context){
        CustomNpcs.LOGGER.info(a.json);
        Config.loadFromJson(a.json);
        context.get().enqueueWork(() -> {
           
        });
    }
}
