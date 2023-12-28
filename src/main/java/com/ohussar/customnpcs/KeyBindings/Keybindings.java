package com.ohussar.customnpcs.KeyBindings;

import com.mojang.blaze3d.platform.InputConstants;
import com.ohussar.customnpcs.CustomNpcs;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public class Keybindings {
    public static Keybindings INSTANCE = new Keybindings();

    private Keybindings(){}

    public final KeyMapping exampleKey = new KeyMapping("key."+CustomNpcs.MODID+".open_quests", 
        KeyConflictContext.IN_GAME, 
        InputConstants.getKey(InputConstants.KEY_K, -1), 
        KeyMapping.CATEGORY_INTERFACE
        );

}
