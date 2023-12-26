package com.ohussar.customnpcs.PlayerClaimedTasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ohussar.customnpcs.CustomNpcs;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class PlayerClaimedTasks {
    private List<ClaimedQuest> quest = new ArrayList<ClaimedQuest>();
    private int questTimer = 0;
    public List<ClaimedQuest> getQuests(){
        return quest;
    }

    public void addQuest(int id, UUID uuid, int[] kills){
        quest.add(new ClaimedQuest(uuid, id, kills));
    }

    public void removeQuest(UUID uuid){
        int size = quest.size();
        for(int i = 0; i < size; i++){
            if(quest.get(i).npc.compareTo(uuid) == 0){
                quest.remove(i);
                break;
            }
        }
    }
    public int getTimer(){
        return questTimer;
    }

    public void setDelay(){
        this.questTimer = CustomNpcs.QUEST_DELAY;
    }

    public void copyFrom(List<ClaimedQuest> quest){
        this.quest = quest;
    }
    public void subTimer(){
        this.questTimer--;
        if(this.questTimer < 0){
            this.questTimer = 0;
        }
    }

    public void saveNBTData(CompoundTag nbt){
        nbt.putInt("questTimer", questTimer);
        if(quest != null){
            int size = quest.size();
            nbt.putInt("quest_amount", size);
            for(int i = 0; i < size; i++){
                nbt.putInt("id"+Integer.toString(i), quest.get(i).id);
                nbt.putUUID("uuid"+Integer.toString(i), quest.get(i).npc);

                CompoundTag kill = new CompoundTag();
                kill.putInt("size", quest.get(i).kills.length);
                for(int k = 0; k < quest.get(i).kills.length; k++){
                    kill.putInt("kills"+Integer.toString(k), quest.get(i).kills[k]);
                }
                nbt.put("kill"+Integer.toString(i), kill);
            }
        }
    }

    public void loadNBTData(CompoundTag nbt, Boolean override){
        if(nbt.contains("questTimer")){
            questTimer = nbt.getInt("questTimer");
        }
        if(override){
            quest.clear();
        }
        int size = nbt.getInt("quest_amount");
        for(int i = 0; i < size; i++){
            if(nbt.contains("uuid" + Integer.toString(i))){

                CompoundTag kill = nbt.getCompound("kill"+Integer.toString(i));
                int ss = kill.getInt("size");
                int[] kills = new int[ss];
                for(int k = 0; k < ss; k++){
                    kills[k] = kill.getInt("kills"+Integer.toString(k));
                }
                quest.add(
                    new ClaimedQuest(
                        nbt.getUUID("uuid" + Integer.toString(i)), 
                        nbt.getInt("id"+Integer.toString(i)),
                        kills
                        )
                        );
            }
        }
    }
}
