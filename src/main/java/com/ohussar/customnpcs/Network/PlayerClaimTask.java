package com.ohussar.customnpcs.Network;

import java.util.UUID;
import java.util.function.Supplier;

import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class PlayerClaimTask {
    public UUID npc;
    public int id;
    public PlayerClaimTask(UUID uuid, int id){
        this.npc = uuid;
        this.id = id;
    }
    public PlayerClaimTask(FriendlyByteBuf buf){
        this.npc = buf.readUUID();
        this.id = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeUUID(this.npc);
        buf.writeInt(this.id);
    }
    public static void handle(PlayerClaimTask a, Supplier<NetworkEvent.Context> context){

        context.get().enqueueWork(() ->{
                context.get().getSender().getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap -> {
                    cap.addQuest(a.id, a.npc, new int[]{});
                    CompoundTag nbt = new CompoundTag();
                    cap.saveNBTData(nbt);
                    PacketHandler.sendToPlayer(new PlayerClaimTaskClient(nbt), context.get().getSender());
                });
        });
        context.get().setPacketHandled(true);
    }
}
