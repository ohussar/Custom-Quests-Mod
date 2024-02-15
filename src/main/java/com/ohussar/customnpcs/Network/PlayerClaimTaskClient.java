package com.ohussar.customnpcs.Network;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class PlayerClaimTaskClient {
    public CompoundTag nbt;
    public PlayerClaimTaskClient(CompoundTag nbt){
        this.nbt = nbt;
    }
    public PlayerClaimTaskClient(FriendlyByteBuf buf){
        this.nbt = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(nbt);
    }

    public static void handle(PlayerClaimTaskClient a, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() ->{
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,  () -> () -> PlayerClaimTaskHandler.handlePacket(a, context));
        });
        context.get().setPacketHandled(true);
    }
}
