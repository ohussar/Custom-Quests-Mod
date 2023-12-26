package com.ohussar.customnpcs;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.ohussar.customnpcs.Quests.QuestRawJson;
import com.ohussar.customnpcs.Quests.Quests;
// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = CustomNpcs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{

    public static final String FILENAME = CustomNpcs.MODID + "-quests.json";
    public static String Json = "";
    public static Path getConfigPath(String name){
        return FMLPaths.CONFIGDIR.get().resolve(name);
    }

    public static void loadFromJson(String json){
        Quests.quests.clear();
        Gson gson = new Gson();
        QuestRawJson.Data raw;
        JsonElement element = JsonParser.parseString(json);
        raw = gson.fromJson(element, QuestRawJson.Data.class);
        CustomNpcs.LOGGER.info(json);
        int size = raw.quests.size();
        for(int i = 0; i < size; i++){
            Quests.loadQuest(raw.quests.get(i));
            CustomNpcs.LOGGER.info("Received task id " + Integer.toString(raw.quests.get(i).id));
        }

    }
    static void loadConfig() 
    {
        Path path = getConfigPath(FILENAME);
        if(Files.exists(path)){
                Gson gson = new Gson();
                QuestRawJson.Data raw;
                JsonReader reader;
                try {
                    Json = FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
                    CustomNpcs.LOGGER.info(Json);
                    reader = new JsonReader(new FileReader(path.toFile()));
                    raw = gson.fromJson(reader, QuestRawJson.Data.class);
                    
                    int size = raw.quests.size();
                    for(int i = 0; i < size; i++){
                        Quests.loadQuest(raw.quests.get(i));
                    }
                   
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


        }
    }
}
