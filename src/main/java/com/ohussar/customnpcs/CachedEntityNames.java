package com.ohussar.customnpcs;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
public class CachedEntityNames {
    private static Map<String, String> names = new HashMap<String, String>();

    public static String getKey(String name){
        if(!names.containsKey(name)){
            hashName(name);
        }
        return names.get(name);
    }

    private static void hashName(String name){
        EntityType<?> mob = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(name) );
        Entity en = mob.create(Minecraft.getInstance().player.level());
        names.put(name, en.getDisplayName().getString());
        en.kill();
    }


}
