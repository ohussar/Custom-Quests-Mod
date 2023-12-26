package com.ohussar.customnpcs.Quests;


public class Quest {
    
    public QuestItem inputItems;
    public QuestItem rewardItems;
    public int id;
    public int difficulty;
    public QuestKills kills;
    public int[] killed;


    public int type;

    public Quest(int type, QuestItem reward, QuestItem inpuItems, QuestKills kills, int difficulty, int id){
        this.type = type;
        this.rewardItems = reward;
        this.inputItems = inpuItems;
        this.kills = kills;
        this.id = id;
        this.difficulty = difficulty;
        killed = new int[]{};
    }
}
