package com.ohussar.customnpcs.Quests;


public class Quest {
    
    public QuestItem inputItems;
    public QuestItem rewardItems;
    public int id;

    public int type;

    public Quest(int type, QuestItem reward, QuestItem inpuItems, int id){
        this.type = type;
        this.rewardItems = reward;
        this.inputItems = inpuItems;
        this.id = id;
    }
}
