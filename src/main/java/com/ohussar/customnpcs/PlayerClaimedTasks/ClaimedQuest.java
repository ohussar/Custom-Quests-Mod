package com.ohussar.customnpcs.PlayerClaimedTasks;

import java.util.UUID;

public class ClaimedQuest {
    public UUID npc;
    public int id;
    public int[] kills;

    public ClaimedQuest(UUID uuid, int id, int[] kills){
        this.npc = uuid;
        this.id = id;
        this.kills = kills;
    }

}
