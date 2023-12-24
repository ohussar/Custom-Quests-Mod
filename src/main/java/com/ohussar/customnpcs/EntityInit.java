package com.ohussar.customnpcs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CustomNpcs.MODID);
    
    public static final RegistryObject<EntityType<CustomNpc>> CustomNpc = ENTITIES.register("custom_npc", 
    () -> EntityType.Builder.of(CustomNpc::new, MobCategory.MISC).build(new ResourceLocation(CustomNpcs.MODID, "custom_npc").toString()));
}
