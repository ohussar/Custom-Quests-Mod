package com.ohussar.customnpcs.Network;
import java.util.UUID;
import java.util.function.Supplier;

import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
public class FinishQuest {
    public UUID uuid;

    public FinishQuest(UUID uuid){
        this.uuid = uuid;
    }

    public FinishQuest(FriendlyByteBuf buf){
        this.uuid = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeUUID(uuid);
    }

    public static void handle(FinishQuest q, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() ->{
            ServerPlayer player = context.get().getSender();
            player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap -> {
                cap.removeQuest(q.uuid);
                if(cap.getTimer() <= 0){
                    cap.setDelay();
                }
            });
        });
    }

}
