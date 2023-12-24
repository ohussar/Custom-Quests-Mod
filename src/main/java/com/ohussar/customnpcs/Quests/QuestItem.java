package com.ohussar.customnpcs.Quests;

import net.minecraft.world.item.Item;

public class QuestItem {
    public Item[] item;
    public int[] quantity;

    public QuestItem(Item[] item, int[] quantity){
        this.item = item;
        this.quantity = quantity;
    }

}
