package com.ohussar.customnpcs.Network;

import java.util.function.Supplier;

import com.ohussar.customnpcs.CustomNpcs;
import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;

import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;

public class PlayerClaimTaskHandler {
    public static void handlePacket(PlayerClaimTaskClient a, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            Minecraft.getInstance().player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                cap.loadNBTData(a.nbt, true);
                CustomNpcs.LOGGER.info("recebido");
                if(a.nbt.contains("kill0")){
                    if(a.nbt.getCompound("kill0").contains("kills0")){
                        CustomNpcs.LOGGER.info(Integer.toString(a.nbt.getCompound("kill0").getInt("kills0")));
                    }
                }
            });
        });
    }
}
