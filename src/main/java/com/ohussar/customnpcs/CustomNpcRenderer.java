package com.ohussar.customnpcs;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CustomNpcRenderer extends MobRenderer<CustomNpc, CustomNpcModel>{
    private static final ResourceLocation TEXTURE = new ResourceLocation(CustomNpcs.MODID + ":textures/entities/villager.png");
    public CustomNpcRenderer(EntityRendererProvider.Context context) {
      super(context, new CustomNpcModel(context.bakeLayer(CustomNpcModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomNpc entity) {
        return TEXTURE;
    }
}
