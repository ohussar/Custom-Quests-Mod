package com.ohussar.customnpcs;

import com.mojang.logging.LogUtils;
import com.ohussar.customnpcs.Network.PacketHandler;
import com.ohussar.customnpcs.Network.PlayerClaimTaskClient;
import com.ohussar.customnpcs.Network.SyncQuests;
import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file

@Mod(CustomNpcs.MODID)
public class CustomNpcs
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "customnpcs";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public CustomNpcs()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        EntityInit.ENTITIES.register(modEventBus);
        MenuInit.MENU_TYPES.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ServerModEvents());
        // Register the item to a creative tab
        Config.loadConfig();
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            PacketHandler.register();
        });
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }
    @SubscribeEvent
    public void onEntityJoinWorld(PlayerLoggedInEvent event){
        LOGGER.info("Player logged in");
        if(Config.Json != ""){
            LOGGER.info("Sent info to client");
            PacketHandler.sendToPlayer(new SyncQuests(Config.Json), (ServerPlayer) event.getEntity());
            CompoundTag c = new CompoundTag();
            event.getEntity().getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                cap.saveNBTData(c);
            });
            PacketHandler.sendToPlayer(new PlayerClaimTaskClient(c), (ServerPlayer) event.getEntity());
        }
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public class ModEvents{
        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
                if(event.getObject() instanceof Player){
                    if(!event.getObject().getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).isPresent()){
                        event.addCapability(new ResourceLocation(MODID, "properties"), new PlayerClaimedTasksProvider());
                    }
                }
        }
        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
            event.getEntity().getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                CompoundTag c = new CompoundTag(); //// Capability syncing
                cap.saveNBTData(c);
                PacketHandler.sendToPlayer(new PlayerClaimTaskClient(c), (ServerPlayer) event.getEntity());
            });
        }
        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event){
            if(event.isWasDeath()){
                event.getOriginal().reviveCaps();
                event.getOriginal().getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(oldStore -> {
                    event.getEntity().getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(newStore ->{
                        newStore.copyFrom(oldStore.getQuests());
                    });
                });
                event.getOriginal().invalidateCaps();
            }
        }
        @SubscribeEvent
        public void onDimensionChange (PlayerEvent.PlayerChangedDimensionEvent event){
            event.getEntity().reviveCaps();
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            event.enqueueWork(() -> {
                MenuScreens.register(MenuInit.NPC_MENU.get(), CustomNpcScreen::new);
            });
        }   
        @SubscribeEvent
        public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event){
            event.registerEntityRenderer(EntityInit.CustomNpc.get(), CustomNpcRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event){
            event.registerLayerDefinition(CustomNpcModel.LAYER_LOCATION, CustomNpcModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void entityAttributes(EntityAttributeCreationEvent event){
            event.put(EntityInit.CustomNpc.get(), CustomNpc.getCustomNpcAttributes().build());
        }

    }
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public class ServerModEvents{
        @SubscribeEvent
        public static void entityAttributes(EntityAttributeCreationEvent event){
            event.put(EntityInit.CustomNpc.get(), CustomNpc.getCustomNpcAttributes().build());
        }
    }
}
