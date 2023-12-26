package com.ohussar.customnpcs;
import java.lang.reflect.InvocationTargetException;

import com.ohussar.customnpcs.Network.PacketHandler;
import com.ohussar.customnpcs.Network.PlayerClaimTask;
import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;
import com.ohussar.customnpcs.Quests.Quest;
import com.ohussar.customnpcs.Quests.QuestComplete;
import com.ohussar.customnpcs.Quests.QuestItem;
import com.ohussar.customnpcs.Quests.QuestType;
import com.ohussar.customnpcs.Quests.Quests;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.registries.ForgeRegistries;


public class CustomNpcScreen extends AbstractContainerScreen<CustomNpcMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/gui/custom_npc.png");
    
    private int buttonX = 0;
    private int buttonY = 170+16;
    private int buttonW = 84;
    private int buttonH = 15;
    private int buttonYS = 185+16;


    private int nextButtonX = 84;
    private int nextButtonY = 170+16;
    private int nextButtonYS = 185+16;
    private int nextButtonYBlocked = 200+16;
    private int nextButtonArrowX = 99;
    private int nextButtonArrowY = 170+16;
    private int nextButtonW = 15;
    private int nextButtonH = 15;

    private int questSelected = 0;

    private int buttonPosX = 43;
    private int buttonPosY = 17;

    private Vec2[] inputItemsPos = 
    new Vec2[]{
        new Vec2(16, 35+16), 
               new Vec2(33, 35+16),
               new Vec2(16, 52+16), 
               new Vec2(33, 52+16)
              };
    private Vec2[] rewardItemsPos = 
    new Vec2[]{
        new Vec2(127, 35+16), 
               new Vec2(144, 35+16),
               new Vec2(127, 47+16), 
               new Vec2(144, 47+16)
              };          


    int textX = 86;
    int textY = 18;

    CustomNpcMenu menu;
    private Button questButton;
    private Button left;
    private Button right;
    private Boolean canDraw;
    private Quest quest;

    private String mobDisplayName = "";
    private int iteration;

    public CustomNpcScreen(CustomNpcMenu menu, Inventory inv, Component ptitle) {
        super(menu, inv, ptitle);
        this.inventoryLabelY += 21;
        this.imageWidth = 176;
        this.imageHeight = 187;
        this.menu = menu;
        for(int i = 0; i < 3; i++){

        }
        
    }

    @Override
    protected void renderBg(GuiGraphics test, float p_97788_, int mouseX, int mouseY) {
        test.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if(menu.getQuest(questSelected) != null){
            canDraw = true;
            quest = menu.getQuest(questSelected);
            Minecraft.getInstance().player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap -> {
                int size = cap.getQuests().size();
                for(int k = 0; k < size; k++){
                    if(cap.getQuests().get(k).npc.equals(menu.npc.getUUID())){
                        canDraw = false;
                        quest = Quests.handle_quest_id(cap.getQuests().get(k).id);
                    }
                }
            });
            drawQuestItems(test, quest, mouseX, mouseY,!canDraw);
            drawGetQuestButton(test, mouseX, mouseY, canDraw);
            drawChangeMissionButtons(test, quest, mouseX, mouseY, canDraw);
        }
    }

    public void drawChangeMissionButtons(GuiGraphics render, Quest quest, int mouseX, int mouseY, Boolean can){
        int xx = buttonW + buttonPosX + leftPos + 1;
        int yy = buttonPosY + topPos + 3;

        int xx1 = buttonPosX + leftPos - 1 - nextButtonW;

        int buttonYY1 = nextButtonY;
        int buttonYY2 = nextButtonY;
        if(inBound(mouseX, mouseY, xx-1, yy, nextButtonW-1, nextButtonH)){
            buttonYY1 = nextButtonYS;
        }
        if(inBound(mouseX, mouseY, xx1-1, yy, nextButtonW-1, nextButtonH)){
            buttonYY2 = nextButtonYS;
        }
        if(!can){
            buttonYY1 = nextButtonYBlocked;
            buttonYY2 = nextButtonYBlocked;
            if(right != null){
                right.active = false;
            }
            if(left != null){
                left.active = false;
            }
        }else{
            if(right != null){
                right.active = true;
            }
            if(left != null){
                left.active = true;
            }
        }
        if(right == null){
            right = Button.builder(null, (btn) -> { 
                this.questSelected++;
                if(this.questSelected > this.menu.npc.questAmount-1){
                    this.questSelected = this.menu.npc.questAmount-1;
                }
            }).size(nextButtonW, nextButtonH).pos(xx, yy).build();
            this.addWidget(right);
        }
        if(left == null){
            left = Button.builder(null, (btn) -> {
                this.questSelected--;
                if(this.questSelected < 0){
                    this.questSelected = 0;
                }
            }).size(nextButtonW, nextButtonH).pos(xx1, yy).build();
            this.addWidget(left);
        }
        render.blit(TEXTURE, xx, yy, nextButtonX, buttonYY1, nextButtonW, nextButtonH);
        render.blit(TEXTURE, xx1, yy, nextButtonX, buttonYY2, nextButtonW, nextButtonH);

        render.blit(TEXTURE, xx, yy, nextButtonArrowX, nextButtonArrowY, nextButtonW, nextButtonH);
        render.blit(TEXTURE, xx1, yy, nextButtonArrowX+15, nextButtonArrowY, nextButtonW, nextButtonH);
    }


    public void drawQuestItems(GuiGraphics render, Quest quest, int mouseX, int mouseY, Boolean claimed){

        QuestItem rewards = quest.rewardItems;
        QuestItem inputs = quest.inputItems;

        int textxx = 32 + leftPos + 4;
        int textyy = 51 - 14 + topPos;
        int textxx1 = 143 + leftPos - 11;

        render.drawCenteredString(font, Component.literal("Requisitos:"), textxx, textyy, 0xffffff);
        render.drawCenteredString(font, Component.literal("Recompensas:"), textxx1, textyy, 0xffffff);
        // requisitos
        // recompensas
        for(int i = 0; i < rewards.item.length; i++){
            ItemStack item = new ItemStack(rewards.item[i]);
            render.renderItem(item, this.leftPos + (int)rewardItemsPos[i].x, this.topPos + (int)rewardItemsPos[i].y);

            int yyNumb = this.topPos + (int) rewardItemsPos[i].y + 9;
            int xxNumb = this.leftPos + (int) rewardItemsPos[i].x + 11;
            if(rewards.quantity[i] < 10){
                xxNumb += 3;
            }
            if(inBound(mouseX, mouseY, this.leftPos + (int)rewardItemsPos[i].x, this.topPos + (int)rewardItemsPos[i].y, 16, 16)){
                render.renderTooltip(font, item.getDisplayName(), mouseX, mouseY);
            }
            render.pose().translate(0.0f, 0.0f, 200f);
            render.drawCenteredString(font, Component.literal(Integer.toString(rewards.quantity[i])), xxNumb, yyNumb, 0xffffff);
            render.pose().translate(0.0f, 0.0f, -200f);

        }
        for(int i = 0; i < inputs.item.length; i++){
                        
            ItemStack item = new ItemStack(inputs.item[i]);
            render.renderItem(item, this.leftPos + (int)inputItemsPos[i].x, this.topPos + (int)inputItemsPos[i].y);

            int yyNumb = this.topPos + (int) inputItemsPos[i].y + 9;
            int xxNumb = this.leftPos + (int) inputItemsPos[i].x + 11;
            if(inputs.quantity[i] < 10){
                xxNumb += 3;
            }
            if(inBound(mouseX, mouseY, this.leftPos + (int)inputItemsPos[i].x, this.topPos + (int)inputItemsPos[i].y, 16, 16)){
                render.renderTooltip(font, item.getDisplayName(), mouseX, mouseY);
            }
            render.pose().translate(0.0f, 0.0f, 200f);
            render.drawCenteredString(font, Component.literal(Integer.toString(inputs.quantity[i])), xxNumb, yyNumb, 0xffffff);
            render.pose().translate(0.0f, 0.0f, -200f);
        }


        if(quest.type == QuestType.KILL){

            int size = quest.kills.mobs.length;
            for(int i = 0; i < size; i++){

                EntityType<?> mob = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation( quest.kills.mobs[i] ) );
                Entity en = mob.create(this.menu.player.level());
                String displayName = en.getDisplayName().getString();
                en.kill();
                String needed = Integer.toString(quest.kills.kills[i]);
                mobDisplayName = "Mate "+displayName;
                if(claimed){
                    mobDisplayName += " ";
                    iteration = i;
                    Minecraft.getInstance().player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap -> {
                        int s = cap.getQuests().size();
                        for(int k = 0; k < s; k++){
                            if(cap.getQuests().get(k).npc.equals(this.menu.npc.getUUID())){
                                if(iteration < cap.getQuests().get(k).kills.length){
                                    mobDisplayName += Integer.toString(cap.getQuests().get(k).kills[iteration]);
                                }else{
                                    mobDisplayName += "0";
                                }
  
                            }
                        }
                    }); 
                    mobDisplayName += "/"+needed;
                }else{
                    mobDisplayName += " x"+needed;
                }   


                render.drawString(font, Component.literal(mobDisplayName), leftPos + 10, textyy+10+10*i, 0xffffff);

            }

        }


    }

    public void drawGetQuestButton(GuiGraphics render, int mouseX, int mouseY, Boolean can){
        int xx = this.leftPos + buttonPosX;
        int yy = this.topPos + buttonPosY;

        int buttonYY = buttonY;

        if(inBound(mouseX, mouseY, xx-1, yy, buttonW+1, buttonH)){
            buttonYY = buttonYS; // if mouse is in bounds, then draw the selected button;
        }
        String buttonText = "Começar Missão";
        if(!can){
            buttonText = "Entregar";
        }
        if(questButton == null){
            questButton = Button.builder(null, (btn) -> {
                if(this.canDraw){
                    PacketHandler.sendToServer(new PlayerClaimTask(menu.npc.getUUID(), this.menu.getQuest(this.questSelected).id));
                }else{
                    QuestComplete.handleTaskComplete(quest, this.menu.npc.getUUID());
                }
            }).size(buttonW, buttonH).pos(xx, yy).build();
            this.addWidget(questButton);
        }


        render.blit(TEXTURE, xx, yy, buttonX, buttonYY, buttonW, buttonH);
        render.drawCenteredString(font, Component.literal(buttonText), xx + buttonW / 2, 
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
