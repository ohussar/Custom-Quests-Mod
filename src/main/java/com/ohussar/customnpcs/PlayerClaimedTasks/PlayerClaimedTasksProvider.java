package com.ohussar.customnpcs.PlayerClaimedTasks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerClaimedTasksProvider implements ICapabilityProvider, INBTSerializable<CompoundTag>{
    public static Capability<PlayerClaimedTasks> CLAIMED_TASKS = CapabilityManager.get(new CapabilityToken<PlayerClaimedTasks>() {
        
    });

    private PlayerClaimedTasks claimed = null;
    private LazyOptional<PlayerClaimedTasks> optional = LazyOptional.of(this::createPlayerClaimed);
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerClaimed().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerClaimed().loadNBTData(nbt);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CLAIMED_TASKS){
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    private PlayerClaimedTasks createPlayerClaimed() {
        if(this.claimed == null){
            this.claimed = new PlayerClaimedTasks();
        }
        return this.claimed;
    }
    
}
