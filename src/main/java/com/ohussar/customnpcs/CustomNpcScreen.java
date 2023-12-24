package com.ohussar.customnpcs;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CustomNpcScreen extends AbstractContainerScreen<CustomNpcMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CustomNpcs.MODID, "textures/gui/custom_npc.png");
    
    private int buttonX = 0;
    private int buttonY = 165;
    private int buttonW = 75;
    private int buttonH = 15;
    private int buttonYS = 180;

    private int buttonDrawX = 9;
    private int buttonDrawYStart = 18;
    private int questSelected = 0;

    int textX = 86;
    int textY = 18;

    CustomNpcMenu menu;
    private Button[] buttons = new Button[3];
    public CustomNpcScreen(CustomNpcMenu menu, Inventory inv, Component ptitle) {
        super(menu, inv, ptitle);

        this.imageWidth = 176;
        this.imageHeight = 166;
        this.menu = menu;
        for(int i = 0; i < 3; i++){

        }
        
    }

    @Override
    protected void renderBg(GuiGraphics test, float p_97788_, int mouseX, int mouseY) {
        test.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int i = questSelected;
        if(menu.npc.quests[i] != null){
            if(buttons[i] == null){
                buttons[i] = Button.builder(null, (btn) -> {

                }).size(buttonW, buttonH).pos(this.leftPos + buttonDrawX, this.topPos + buttonDrawYStart).build();
                this.addWidget(buttons[i]);
            }   
            int yy = this.buttonY;
            if(inBound(mouseX, mouseY, this.leftPos + buttonDrawX-1, this.topPos + buttonDrawYStart, buttonW+1, buttonH)){
                yy = this.buttonYS;
            }



            test.blit(TEXTURE, this.leftPos + buttonDrawX, this.topPos + buttonDrawYStart, buttonX, yy, buttonW, buttonH);
            test.drawCenteredString(font, Component.literal("Entregar"), this.leftPos + buttonDrawX + buttonW / 2, 
            this.topPos + buttonDrawYStart + 3, 0xffffff);

            int isize = menu.getQuest(i).inputItems.item.length;
            int rsize = menu.getQuest(i).rewardItems.item.length;
            int initialOffy = 13;
            test.drawString(font, Component.literal("Requeridos: "), this.leftPos+textX, this.topPos+textY+3, 0xffffff);
            test.drawString(font, Component.literal("Recompensas: "), this.leftPos + 9, this.topPos+36, 0xffffff);
            for(int k = 0; k < isize; k++){
                String name = new ItemStack(menu.getQuest(i).inputItems.item[k]).getDisplayName().getString();
                String toDrawRequired = Integer.toString(menu.getQuest(i).inputItems.quantity[k]) + "x " + name;
                test.drawString(font, Component.literal(toDrawRequired),this.leftPos+textX+2,this.topPos+textY+initialOffy + k * 10, 0xffffff);
            }
            for(int j = 0; j < rsize; j++){
                String name = new ItemStack(menu.getQuest(i).rewardItems.item[j]).getDisplayName().getString();
                String toDrawRequired = Integer.toString(menu.getQuest(i).rewardItems.quantity[j]) + "x " + name;
                test.drawString(font, Component.literal(toDrawRequired),this.leftPos+9+2,this.topPos+46 + j * 10, 0xffffff);
            }
            
        }
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
