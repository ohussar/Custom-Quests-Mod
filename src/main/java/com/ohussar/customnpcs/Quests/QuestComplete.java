package com.ohussar.customnpcs.Quests;
import java.util.UUID;
import java.util.List;
import com.ohussar.customnpcs.CustomNpcs;
import com.ohussar.customnpcs.Network.FinishQuest;
import com.ohussar.customnpcs.Network.PacketHandler;
import com.ohussar.customnpcs.Network.SyncInventory;
import com.ohussar.customnpcs.PlayerClaimedTasks.ClaimedQuest;
import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class QuestComplete {

    private static Boolean thisCompleted;

    public static void handleTaskComplete(Quest quest, UUID uuid){
        switch(quest.type){
            case QuestType.DELIVER_ITEM:
                normalTaskComplete(quest, uuid);
            break;
            case QuestType.KILL:
                killQuestComplete(quest, uuid);
            break;
        }
    }
    public static void killQuestComplete(Quest quest, UUID uuid){
        Minecraft mine = Minecraft.getInstance();
        Player player = mine.player;

        thisCompleted = true;

        player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap -> {
            List<ClaimedQuest> q = cap.getQuests();
            for(int k = 0; k < q.size(); k++){
                if(q.get(k).npc.equals(uuid)){
                    int l = quest.kills.mobs.length;
                    for(int j = 0; j < l; j++){
                        if(q.get(k).kills.length > 0){
                            if(q.get(k).kills[j] >= quest.kills.kills[j]){

                            }else{
                                CustomNpcs.LOGGER.info("nossa");
                                thisCompleted = false;
                            }
                        }else{
                            thisCompleted = false;
                        }
                    }
                }
            }
        });
        if(thisCompleted){
            List<Integer> slotsChanged = new ArrayList<Integer>();
            List<ItemStack> newStacks = new ArrayList<ItemStack>();

            QuestItem reward = quest.rewardItems;

            for(int i = 0; i < reward.item.length; i++){
                Inventory inv = Minecraft.getInstance().player.getInventory();
                int free = inv.getFreeSlot();  
                ItemStack item = new ItemStack(reward.item[i]);
                item.setCount(reward.quantity[i]); 
                inv.setItem(free, item);
                slotsChanged.add(free);
                newStacks.add(item);                         
            }

            PacketHandler.sendToServer(new SyncInventory(newStacks, slotsChanged));
            PacketHandler.sendToServer(new FinishQuest(uuid));
            Minecraft.getInstance().player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                cap.removeQuest(uuid);
            });
            Minecraft.getInstance().player.closeContainer();

        }
    }
    public static void normalTaskComplete(Quest quest, UUID uuid){
        QuestItem input = quest.inputItems;
        QuestItem reward = quest.rewardItems;
        int size = input.item.length;
        Boolean hasAllItemsNeeded = true;
        for(int i = 0; i < size; i++){
            Inventory inv = Minecraft.getInstance().player.getInventory();
            int count = 0;
            Item itemToCheck = input.item[i];
            for(int j = 0;  j < inv.getContainerSize(); j++){
                if(inv.getItem(j).getItem().equals(itemToCheck)){
                    if(inv.getItem(j).getCount() >= input.quantity[i]){
                        count += inv.getItem(j).getCount();
                    }
                }
            }
            if(count >= input.quantity[i]){

            }else{
                hasAllItemsNeeded = false;
            }
        }
        if(hasAllItemsNeeded){
            List<Integer> slotsChanged = new ArrayList<Integer>();
            List<ItemStack> newStacks = new ArrayList<ItemStack>();
            for(int i = 0; i < size; i++){
                Inventory inv = Minecraft.getInstance().player.getInventory();
                Item itemToCheck = input.item[i];
                for(int j = 0;  j < inv.getContainerSize(); j++){
                    if(inv.getItem(j).getItem().equals(itemToCheck)){
                        if(inv.getItem(j).getCount() >= input.quantity[i]){
                            inv.getItem(j).setCount(inv.getItem(j).getCount() - input.quantity[i]);
                            ItemStack newStack = inv.getItem(j);
                            if(inv.getItem(j).getCount() <= 0){
                                newStack = ItemStack.EMPTY;
                            }

                            slotsChanged.add(j);
                            newStacks.add(newStack);
                            inv.setItem(j, newStack);
                            break;
                        }
                    }
                }
            }
            for(int i = 0; i < reward.item.length; i++){
                Inventory inv = Minecraft.getInstance().player.getInventory();
                int free = inv.getFreeSlot();  
                ItemStack item = new ItemStack(reward.item[i]);
                item.setCount(reward.quantity[i]); 
                inv.setItem(free, item);
                slotsChanged.add(free);
                newStacks.add(item);                         
            }

            PacketHandler.sendToServer(new SyncInventory(newStacks, slotsChanged));
            PacketHandler.sendToServer(new FinishQuest(uuid));
            Minecraft.getInstance().player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                cap.removeQuest(uuid);
            });
            Minecraft.getInstance().player.closeContainer();
        }
    }
}
