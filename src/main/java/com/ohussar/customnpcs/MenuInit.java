package com.ohussar.customnpcs;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CustomNpcs.MODID);
    
    public static final RegistryObject<MenuType<CustomNpcMenu>> NPC_MENU = MENU_TYPES.register("npc_menu", 
    ()-> IForgeMenuType.create(CustomNpcMenu::new));

}
