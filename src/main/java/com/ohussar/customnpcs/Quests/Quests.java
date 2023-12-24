package com.ohussar.customnpcs.Quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ohussar.customnpcs.CustomNpcs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class Quests {
    public static final int DIAMOND_1 = 1;

    public static List<Quest> quests = new ArrayList<Quest>();

    public static Quest handle_quest_id(int id){
        int size = quests.size();
        for(int i = 0; i < size; i++){
            if(quests.get(i).id == id){
                return quests.get(i);
            }
        }
        return null;
    }

    public static Quest getRandomQuest(){
        Random random = new Random();
        return quests.get(random.nextInt(quests.size()));
    }

    public static void loadQuest(QuestRawJson q){
        int size1 = q.input.size();
        Item[] inputItem = new Item[size1];
        int[] inputCount = new int[size1];

        for(int i = 0; i < size1; i++){
            inputItem[i] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(q.input.get(i).item));
            inputCount[i] = q.input.get(i).count;
        }
        int size2 = q.rewards.size();
        Item[] rewardItem = new Item[size2];
        int[] rewardCount = new int[size2];
        for(int i = 0; i < size2; i++){
            rewardItem[i] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(q.rewards.get(i).item));
            rewardCount[i] = q.rewards.get(i).count;
        }

        quests.add(new Quest(q.type, 
            new QuestItem(rewardItem, rewardCount),
            new QuestItem(inputItem, inputCount), 
            q.id
            ));
        CustomNpcs.LOGGER.info("Registered Quest ID: " + Integer.toString(q.id));    
    }

}
