package com.ohussar.customnpcs;

import com.ohussar.customnpcs.Network.PacketHandler;
import com.ohussar.customnpcs.Network.PlayerClaimTask;
import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;
import com.ohussar.customnpcs.Quests.Quest;
import com.ohussar.customnpcs.Quests.QuestItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

public class CustomNpcScreen extends AbstractContainerScreen<CustomNpcMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/gui/custom_npc.png");
    
    private int buttonX = 0;
    private int buttonY = 170;
    private int buttonW = 84;
    private int buttonH = 15;
    private int buttonYS = 185;

    private int questSelected = 0;


    private Vec2[] rewardItemsPos = 
    new Vec2[]{
        new Vec2(16, 35), 
               new Vec2(33, 35),
               new Vec2(16, 52), 
               new Vec2(33, 52)
              };
    private Vec2[] inputItemsPos = 
    new Vec2[]{
        new Vec2(127, 35), 
               new Vec2(144, 35),
               new Vec2(127, 47), 
               new Vec2(144, 47)
              };          


    int textX = 86;
    int textY = 18;

    CustomNpcMenu menu;
    private Button questButton;
    private Boolean canDraw;

    public CustomNpcScreen(CustomNpcMenu menu, Inventory inv, Component ptitle) {
        super(menu, inv, ptitle);
        this.inventoryLabelY += 5;
        this.imageWidth = 176;
        this.imageHeight = 171;
        this.menu = menu;
        for(int i = 0; i < 3; i++){

        }
        
    }

    @Override
    protected void renderBg(GuiGraphics test, float p_97788_, int mouseX, int mouseY) {
        test.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int i = questSelected;
        if(menu.getQuest(i) != null){
            canDraw = true;
            Minecraft.getInstance().player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap -> {
                int size = cap.getQuests().size();
                for(int k = 0; k < size; k++){
                    if(cap.getQuests().get(k).npc.equals(menu.npc.getUUID())){
                        canDraw = false;
                    }
                }
            });
            if(canDraw){
                drawGetQuestButton(test, mouseX, mouseY);
                drawQuestItems(test, menu.getQuest(i));
            }
        }
    }
    public void drawQuestItems(GuiGraphics render, Quest quest){

        QuestItem rewards = quest.rewardItems;
        QuestItem inputs = quest.inputItems;

        for(int i = 0; i < rewards.item.length; i++){
            ItemStack item = new ItemStack(rewards.item[i]);
            //render.blit(TEXTURE, this.leftPos + (int)rewardItemsPos[i].x, this.topPos + (int)rewardItemsPos[i].y, 176, 0, 16, 16);
            render.renderItem(item, this.leftPos + (int)rewardItemsPos[i].x, this.topPos + (int)rewardItemsPos[i].y);
        }
        for(int i = 0; i < inputs.item.length; i++){
            ItemStack item = new ItemStack(inputs.item[i]);
            // item background
            //render.blit(TEXTURE, this.leftPos + (int)inputItemsPos[i].x, this.topPos + (int)inputItemsPos[i].y, 176, 0, 16, 16);
            render.renderItem(item, this.leftPos + (int)inputItemsPos[i].x, this.topPos + (int)inputItemsPos[i].y);
        }

    }

    public void drawGetQuestButton(GuiGraphics render, int mouseX, int mouseY){
        int xx = this.leftPos + 43;
        int yy = this.topPos + 17;

        int buttonYY = buttonY;
        
        if(questButton == null){
            questButton = Button.builder(null, (btn) -> {
                PacketHandler.sendToServer(new PlayerClaimTask(menu.npc.getUUID(), menu.getQuest(questSelected).id));
            }).size(buttonW, buttonH).pos(xx, yy).build();
            this.addWidget(questButton);
        }

        if(inBound(mouseX, mouseY, xx-1, yy, buttonW+1, buttonH)){
            buttonYY = buttonYS; // if mouse is in bounds, then draw the selected button;
        }
        // requisitos
        // recompensas
        render.blit(TEXTURE, xx, yy, buttonX, buttonYY, buttonW, buttonH);
        render.drawCenteredString(font, Component.literal("Começar Missão"), xx + buttonW / 2, 
            yy + 3, 0xffffff);
    }

    @Override
    public void render(GuiGraphics render, int p_283661_, int p_281248_, float p_281886_) {
        renderBackground(render);
        super.render(render, p_283661_, p_281248_, p_281886_);
        renderTooltip(render, p_283661_, p_281248_);

    }

    public Boolean inBound(int mx, int my, int x, int y,int w,int h){
        return mx > x && mx < x + w && my > y && my < y + h;
    }
       
}
