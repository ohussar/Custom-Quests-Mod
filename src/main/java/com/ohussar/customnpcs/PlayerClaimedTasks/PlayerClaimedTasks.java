package com.ohussar.customnpcs.PlayerClaimedTasks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class PlayerClaimedTasks {
    private List<ClaimedQuest> quest = new ArrayList<ClaimedQuest>();

    public List<ClaimedQuest> getQuests(){
        return quest;
    }

    public void addQuest(int id, UUID uuid){
        quest.add(new ClaimedQuest(uuid, id));
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

    public void copyFrom(List<ClaimedQuest> quest){
        this.quest = quest;
    }

    public void saveNBTData(CompoundTag nbt){
        if(quest != null){
            int size = quest.size();
            nbt.putInt("quest_amount", size);
            for(int i = 0; i < size; i++){
                nbt.putInt("id"+Integer.toString(i), quest.get(i).id);
                nbt.putUUID("uuid"+Integer.toString(i), quest.get(i).npc);
            }
        }
    }

    public void loadNBTData(CompoundTag nbt){
        int size = nbt.getInt("quest_amount");
        for(int i = 0; i < size; i++){
            if(nbt.contains("uuid" + Integer.toString(i))){
                quest.add(
                    new ClaimedQuest(
                        nbt.getUUID("uuid" + Integer.toString(i)), 
                        nbt.getInt("id"+Integer.toString(i))
                        )
                        );
            }
        }
    }
}
