package com.ohussar.customnpcs;

import com.mojang.logging.LogUtils;
import com.ohussar.customnpcs.CustomNpcs.ServerModEvents;
import com.ohussar.customnpcs.KeyBindings.Keybindings;
import com.ohussar.customnpcs.Network.PacketHandler;
import com.ohussar.customnpcs.Network.PlayerClaimTaskClient;
import com.ohussar.customnpcs.Network.SyncQuests;
import com.ohussar.customnpcs.PlayerClaimedTasks.ClaimedQuest;
import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasks;
import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;
import com.ohussar.customnpcs.QuestScreen.QuestScreen;
import com.ohussar.customnpcs.Quests.Quest;
import com.ohussar.customnpcs.Quests.QuestType;
import com.ohussar.customnpcs.Quests.Quests;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.ticks.TickPriority;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import oshi.hardware.CentralProcessor.TickType;

import java.util.List;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file

@Mod(CustomNpcs.MODID)
public class CustomNpcs
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "customnpcs";
    public static final int QUEST_DELAY = 6000;
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public CustomNpcs()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        EntityInit.ENTITIES.register(modEventBus);
        MenuInit.MENU_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ServerModEvents());
        Config.loadConfig();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            PacketHandler.register();
        });
    }


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
            event.getEntity().getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                syncCapability(cap, (ServerPlayer) event.getEntity());
            });
        }
    }
    @SubscribeEvent
    public void kill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                List<ClaimedQuest> quest = cap.getQuests();
                int size = quest.size();
                for(int i = 0; i < size; i++){
                    Quest q = Quests.handle_quest_id(quest.get(i).id);
                    if(q.type == QuestType.KILL){

                        int ss = q.kills.mobs.length;
                        for(int k = 0; k < ss; k++){
                            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(q.kills.mobs[k]));
                            if(event.getEntity().getType().equals(type)){
                                if(quest.get(i).kills.length == 0){
                                    quest.get(i).kills = new int[ss];
                                    quest.get(i).kills[k] = 0;

                                }
                                quest.get(i).kills[k]++;
                                syncCapability(cap, (ServerPlayer) player);
                            }
                        }
                    }
                }
            });
        }
    }

    public void syncCapability(PlayerClaimedTasks cap, ServerPlayer player){
        CompoundTag c = new CompoundTag(); //// Capability syncing
        cap.saveNBTData(c);
        PacketHandler.sendToPlayer(new PlayerClaimTaskClient(c), player);
    }
    public static void sync(PlayerClaimedTasks cap, ServerPlayer player){
        CompoundTag c = new CompoundTag(); //// Capability syncing
        cap.saveNBTData(c);
        PacketHandler.sendToPlayer(new PlayerClaimTaskClient(c), player);
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
        @SubscribeEvent
        public static void playerTick(PlayerTickEvent event)
        {
            //CustomNpcs.LOGGER.info("aaa");
            if(event.phase == TickEvent.Phase.START){
                event.player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                    //CustomNpcs.LOGGER.info("aaa");
                    if(event.side == LogicalSide.SERVER){
                        if(cap.getTimer() > 0){
                            cap.subTimer();
                            if(cap.getTimer()%5 == 0){
                                sync(cap, (ServerPlayer) event.player);
                            }
                        }
                    }else{
                        if(cap.getTimer() > 0){
                            cap.subTimer();
                        }
                    }
                });
            }
        }
    }

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
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event){
            event.register(Keybindings.INSTANCE.exampleKey);
        }
    }
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public class ClientForgeHandler{
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event){
            if(Keybindings.INSTANCE.exampleKey.isDown()){
                Keybindings.INSTANCE.exampleKey.consumeClick();
                Minecraft.getInstance().setScreen(new QuestScreen(null));
            }
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
