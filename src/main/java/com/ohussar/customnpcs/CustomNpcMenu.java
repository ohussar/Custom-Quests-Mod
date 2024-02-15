package com.ohussar.customnpcs;

import com.ohussar.customnpcs.Quests.Quest;
import com.ohussar.customnpcs.Quests.Quests;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CustomNpcMenu extends AbstractContainerMenu{
    public CustomNpc npc;
    public Player player;
    public Inventory playerInv;

    private Quest[] quests = new Quest[3];

    public CustomNpcMenu(int id, Inventory inv, FriendlyByteBuf buf){
        this(id, inv, inv.player.level().getEntity(buf.readInt()));
    }

    protected CustomNpcMenu(int id, Inventory playerInv, Entity npc) {
        super(MenuInit.NPC_MENU.get(), id);
        createPlayerHotbar(playerInv);  
        createPlayerInventory(playerInv);
        if(npc instanceof CustomNpc n){
            this.npc = n;
        }
        this.player = playerInv.player;
        this.playerInv = playerInv;
        for(int i = 0; i < 3; i++){
            quests[i] = Quests.getRandomQuest();
        }
    }

    public Quest getQuest(int i){
        return quests[i];
    }


    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        return ItemStack.EMPTY;
    }

    public void createPlayerInventory(Inventory inv){
        for(int ii = 0; ii < 3; ii ++ ){
            for(int jj = 0; jj < 9; jj++){
                addSlot(new Slot(inv, 9 + jj + ii * 9, 8 + jj * 18, 89 + ii * 18+16));
            }
        }
    }
    
    public void createPlayerHotbar(Inventory inv){
        for(int ii = 0; ii < 9; ii ++ ){
            addSlot(new Slot(inv, ii, 8 + ii * 18, 147+16));
        }
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
    
}
