package com.ohussar.customnpcs.QuestScreen;

import com.ohussar.customnpcs.CachedEntityNames;
import com.ohussar.customnpcs.CustomNpcs;
import com.ohussar.customnpcs.PlayerClaimedTasks.ClaimedQuest;
import com.ohussar.customnpcs.PlayerClaimedTasks.PlayerClaimedTasksProvider;
import com.ohussar.customnpcs.Quests.Quest;
import com.ohussar.customnpcs.Quests.QuestType;
import com.ohussar.customnpcs.Quests.Quests;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;

import java.util.List;
public class QuestScreen extends Screen {

    private int imageWidth = 248;
    private int imageHeight = 166;

    private int buttonX = 0;
    private int buttonY = 166;

    private int buttonW = 84;
    private int buttonH = 15;

    private int buttonYS = buttonY + 15;
    private int buttonYB = buttonYS + 15;

    private QuestScreenButton[] buttons = new QuestScreenButton[9];
    private List<ClaimedQuest> quests;
    private int buttonSelected = -1;

    private int leftPos = 0;
    private int topPos = 0;

    private static final ResourceLocation TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/gui/quest_screen.png");


    public QuestScreen(Component comp) {
        super(Component.literal("ABC"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        leftPos = this.width / 2 - imageWidth / 2;
        topPos = this.height / 2 - imageHeight / 2;
        
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderButtons(graphics, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }


    public void renderButtons(GuiGraphics render, int mouseX, int mouseY){
        int baseY = 9;
        int xx = 8;
        for(int i = 0; i < 9; i++){
            int yy = baseY + i * 16;
            if(buttons[i] == null){
                buttons[i] = QuestScreenButton.builder(null, (btn) -> {
                        this.buttonSelected = btn.id;
                }).size(buttonW,buttonH).pos(leftPos + xx, topPos + yy).build();
                buttons[i].id = i;
                this.addWidget(buttons[i]);
            }
            int by = buttonY;
            if(this.buttonSelected == i || inBound(mouseX, mouseY, leftPos + xx-1, topPos + yy, buttonW + 1, buttonH )){
                by = buttonYS;
            }
            quests = null;
            Minecraft mine = Minecraft.getInstance();
            mine.player.getCapability(PlayerClaimedTasksProvider.CLAIMED_TASKS).ifPresent(cap ->{
                quests = cap.getQuests();
            });
            int size = quests.size();
            if(size > 0){
                
                Quest[] q = new Quest[size];
                
                for(int k = 0; k < size; k++){
                    q[k] = Quests.handle_quest_id(quests.get(k).id);
                    q[k].killed = quests.get(k).kills;
                }
                if(q.length > 9){

                }else{
                    if(i > q.length - 1){
                        by = buttonYB;
                        buttons[i].active = false;
                    }else{
                        buttons[i].active = true;

                        int x = leftPos + xx + buttonW / 2;
                        int y = topPos  + yy + 3;

                        render.drawCenteredString(font, Component.literal("Quest #" + Integer.toString(i+1)), x, y, 0xffffff);

                        if(i == buttonSelected){
                            renderQuest(render, q[i], mouseX, mouseY);
                        }
                    }
                }

            }else{
                by = buttonYB;
                buttons[i].active = false;
            }
            render.blit(TEXTURE, leftPos + xx, topPos + yy, buttonX, by, buttonW, buttonH);
        }
    }

    public void drawItemBackground(GuiGraphics graphics, int x, int y){
        graphics.blit(TEXTURE, x, y, 84, 166, 16, 16);
    }

    public void renderQuest(GuiGraphics graphics, Quest quest, int mouseX, int mouseY){
        int textX = leftPos + 97;
        int textY = topPos  + 10;
        graphics.drawString(font, Component.literal("Quest id: " + Integer.toString(quest.id)), textX, textY, 0xffffff);
        int rewardsBaseY = 78;

        graphics.drawCenteredString(font, Component.literal("Requisitos:"), leftPos + 166,topPos + 26, 0xffffff);

        Vec2[] inp = new Vec2[]{new Vec2(150, 38), new Vec2(168, 38), new Vec2(150, 56), new Vec2(168, 56) };
        Vec2[] rew = new Vec2[]{new Vec2(150, 88), new Vec2(168, 88), new Vec2(150, 106), new Vec2(168, 106) };
        switch(quest.type){
            case QuestType.DELIVER_ITEM:
                int sizei = quest.inputItems.item.length;
                int maxyy = 0;
                for(int i = 0; i < sizei; i++){
                    ItemStack item = new ItemStack(quest.inputItems.item[i]);
                    drawItemBackground(graphics,(int)inp[i].x + leftPos, (int)inp[i].y + topPos);
                    graphics.renderItem(item, (int)inp[i].x + leftPos, (int)inp[i].y + topPos);
                    int yyNumb = this.topPos + (int) inp[i].y + 9;
                    int xxNumb = this.leftPos + (int) inp[i].x + 11;
                    if(quest.inputItems.quantity[i] < 10){
                        xxNumb += 3;
                    }
                    if(inBound(mouseX, mouseY, this.leftPos + (int)inp[i].x, this.topPos + (int)inp[i].y, 16, 16)){
                        graphics.renderTooltip(font, item.getDisplayName(), mouseX, mouseY);
                    }
                    graphics.pose().translate(0.0f, 0.0f, 200f);
                    graphics.drawCenteredString(font, Component.literal(Integer.toString(quest.inputItems.quantity[i])), xxNumb, yyNumb, 0xffffff);
                    graphics.pose().translate(0.0f, 0.0f, -200f);
                    item = null;
                    maxyy = (int)inp[i].y + 16;
                }
                int miny = maxyy + 16;
                rewardsBaseY = maxyy + 3;
                int sizer = quest.rewardItems.item.length;
                for(int i = 0; i < sizer; i++){
                    int yy = miny;
                    if(i > 1){
                        yy+=18;
                    }


                    ItemStack item = new ItemStack(quest.rewardItems.item[i]);
                    drawItemBackground(graphics,(int)rew[i].x + leftPos, yy + topPos);
                    graphics.renderItem(item, (int)rew[i].x + leftPos, yy + topPos);
                    int yyNumb = this.topPos + yy + 9;
                    int xxNumb = this.leftPos + (int) rew[i].x + 11;
                    if(quest.rewardItems.quantity[i] < 10){
                        xxNumb += 3;
                    }
                    if(inBound(mouseX, mouseY, this.leftPos + (int)rew[i].x, this.topPos + yy, 16, 16)){
                        graphics.renderTooltip(font, item.getDisplayName(), mouseX, mouseY);
                    }
                    graphics.pose().translate(0.0f, 0.0f, 200f);
                    graphics.drawCenteredString(font, Component.literal(Integer.toString(quest.rewardItems.quantity[i])), xxNumb, yyNumb, 0xffffff);
                    graphics.pose().translate(0.0f, 0.0f, -200f);
                    item = null;
                }
            break;
            case QuestType.KILL:

                int sizemob = quest.kills.mobs.length;
                int maxy = 0;
                for(int i = 0; i < sizemob; i++){
                    String name = CachedEntityNames.getKey(quest.kills.mobs[i]);
                    int count = quest.killed.length > 0 ? quest.killed[i] : 0;
                    int n =  quest.kills.kills[i];
                    int yy = 42 + topPos;

                    String toDraw = "Mate " + name + " " + Integer.toString(count) + "/" + Integer.toString(n);
 
                    graphics.drawCenteredString(font, Component.literal(toDraw), leftPos + 166,yy + 10 * i, 0xffffff);
                    
                    maxy = 42 + 10 * i + 16;
                }

                int minyy = maxy + 16;
                rewardsBaseY = maxy + 3;
                int sizerr = quest.rewardItems.item.length;
                for(int i = 0; i < sizerr; i++){
                    int yy = minyy;
                    if(i > 1){
                        yy+=18;
                    }
                    ItemStack item = new ItemStack(quest.rewardItems.item[i]);
                    drawItemBackground(graphics,(int)rew[i].x + leftPos, yy+topPos);
                    graphics.renderItem(item, (int)rew[i].x + leftPos, yy+topPos);
                    int yyNumb = this.topPos + yy + 9;
                    int xxNumb = this.leftPos + (int) rew[i].x + 11;
                    if(quest.rewardItems.quantity[i] < 10){
                        xxNumb += 3;
                    }
                    if(inBound(mouseX, mouseY, this.leftPos + (int)rew[i].x, this.topPos + yy, 16, 16)){
                        graphics.renderTooltip(font, item.getDisplayName(), mouseX, mouseY);
                    }
                    graphics.pose().translate(0.0f, 0.0f, 200f);
                    graphics.drawCenteredString(font, Component.literal(Integer.toString(quest.rewardItems.quantity[i])), xxNumb, yyNumb, 0xffffff);
                    graphics.pose().translate(0.0f, 0.0f, -200f);
                    item = null;
                }                
            break;
        }

        graphics.drawCenteredString(font, Component.literal("Recompensas:"), leftPos + 166,topPos + rewardsBaseY, 0xffffff);

    }

    public Boolean inBound(int mx, int my, int x, int y,int w,int h){
        return mx > x && mx < x + w && my > y && my < y + h;
    }
       
}
