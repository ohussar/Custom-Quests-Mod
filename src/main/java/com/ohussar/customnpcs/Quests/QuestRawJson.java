package com.ohussar.customnpcs.Quests;

import java.util.List;

public class QuestRawJson {
    public int id;
    public int type;
    public int difficulty;
    public List<QuestInput> input;
    public List<QuestInput> rewards;

    public List<QuestKillsRaw> kills;

    public class Data{
        public List<QuestRawJson> quests;
    }

}
