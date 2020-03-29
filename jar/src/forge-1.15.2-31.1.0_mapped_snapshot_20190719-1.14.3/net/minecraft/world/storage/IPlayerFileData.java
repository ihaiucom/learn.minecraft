package net.minecraft.world.storage;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IPlayerFileData {
   void writePlayerData(PlayerEntity var1);

   @Nullable
   CompoundNBT readPlayerData(PlayerEntity var1);
}
