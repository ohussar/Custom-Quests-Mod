package com.ohussar.customnpcs.Network;
import java.util.ArrayList;
import java.util.List;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
public class SyncInventory {
    List<ItemStack> stacksChanged = new ArrayList<ItemStack>();
    List<Integer> slotsChanged = new ArrayList<Integer>();

    public SyncInventory(List<ItemStack> stacks, List<Integer> slots){
        this.stacksChanged = stacks;
        this.slotsChanged = slots;
    }

    public SyncInventory(FriendlyByteBuf buf){
        int s = buf.readInt();
        for(int i = 0; i < s; i++){
            stacksChanged.add(buf.readItem());
        }
        int s1 = buf.readInt();
        for(int i = 0; i < s1; i++){
            slotsChanged.add(buf.readInt());
        }
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(stacksChanged.size());
        for(int i = 0; i < stacksChanged.size(); i++){
            buf.writeItemStack(stacksChanged.get(i), false);
        }
        buf.writeInt(slotsChanged.size());
        for(int i = 0; i < slotsChanged.size(); i++){
            buf.writeInt(slotsChanged.get(i));
        }
    }

    public static void handle(SyncInventory sync, Supplier<NetworkEvent.Context> context){
        context.get().enqueueWork(() ->{
            ServerPlayer player = context.get().getSender();
            for(int k = 0; k < sync.stacksChanged.size(); k++){
                player.getInventory().setItem(sync.slotsChanged.get(k), sync.stacksChanged.get(k));
            }
        });

        context.get().setPacketHandled(true);
    }

}
