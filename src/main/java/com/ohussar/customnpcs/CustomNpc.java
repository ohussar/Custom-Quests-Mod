package com.ohussar.customnpcs;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;

public class CustomNpc extends AgeableMob implements MenuProvider {

    public int questAmount = 3;
    public int frame = 0;


    public UUID myUUID;

    protected CustomNpc(EntityType<? extends AgeableMob> type_, Level level) {
        super(type_, level);
    }


    public static AttributeSupplier.Builder getCustomNpcAttributes(){
        return Mob.createMobAttributes().add(ForgeMod.ENTITY_GRAVITY.get(), 0.5f).add(Attributes.MAX_HEALTH, 1000)
        .add(Attributes.MOVEMENT_SPEED, 0.0f);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }
    @Override
    public final InteractionResult mobInteract(net.minecraft.world.entity.player.Player player, InteractionHand p_21421_) {
        if(!player.level().isClientSide()){
            if(player instanceof ServerPlayer s){
                if(!s.level().isClientSide){
                    NetworkHooks.openScreen(s, this, buf -> buf.writeInt(this.getId()));
                }
            }
        }
        return super.mobInteract(player, p_21421_);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
    }


    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }
    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv,
            net.minecraft.world.entity.player.Player player) {
        return new CustomNpcMenu(id, inv, this);
    }

    @Override
    public Component getDisplayName() {
       return Component.literal("Quests");
    }
}
