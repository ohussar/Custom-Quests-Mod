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
            });
        });
    }
}
