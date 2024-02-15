package com.ohussar.customnpcs.Network;

import java.util.function.Supplier;

import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class PlayerClaimTaskHandler {
    public static void handlePacket(PlayerClaimTaskClient a, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() -> {
            Minecraft mine = Minecraft.getInstance();
            mine.player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                cap.loadNBTData(a.nbt, true);
            });
        });
    }
}
