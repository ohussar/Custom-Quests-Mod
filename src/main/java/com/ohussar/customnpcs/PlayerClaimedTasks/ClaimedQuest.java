package com.ohussar.customnpcs.PlayerClaimedTasks;

import java.util.UUID;

public class ClaimedQuest {
    public UUID npc;
    public int id;

    public ClaimedQuest(UUID uuid, int id){
        this.npc = uuid;
        this.id = id;
    }

}
