package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;

public class MobSpawnerTileEntity extends TileEntity implements ITickableTileEntity {
   private final AbstractSpawner spawnerLogic = new AbstractSpawner() {
      public void broadcastEvent(int p_98267_1_) {
         MobSpawnerTileEntity.this.world.addBlockEvent(MobSpawnerTileEntity.this.pos, Blocks.SPAWNER, p_98267_1_, 0);
      }

      public World getWorld() {
         return MobSpawnerTileEntity.this.world;
      }

      public BlockPos getSpawnerPosition() {
         return MobSpawnerTileEntity.this.pos;
      }

      public void setNextSpawnData(WeightedSpawnerEntity p_184993_1_) {
         super.setNextSpawnData(p_184993_1_);
         if (this.getWorld() != null) {
            BlockState lvt_2_1_ = this.getWorld().getBlockState(this.getSpawnerPosition());
            this.getWorld().notifyBlockUpdate(MobSpawnerTileEntity.this.pos, lvt_2_1_, lvt_2_1_, 4);
         }

      }
   };

   public MobSpawnerTileEntity() {
      super(TileEntityType.MOB_SPAWNER);
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.spawnerLogic.read(p_145839_1_);
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      this.spawnerLogic.write(p_189515_1_);
      return p_189515_1_;
   }

   public void tick() {
      this.spawnerLogic.tick();
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      CompoundNBT lvt_1_1_ = this.write(new CompoundNBT());
      lvt_1_1_.remove("SpawnPotentials");
      return lvt_1_1_;
   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      return this.spawnerLogic.setDelayToMin(p_145842_1_) ? true : super.receiveClientEvent(p_145842_1_, p_145842_2_);
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public AbstractSpawner getSpawnerBaseLogic() {
      return this.spawnerLogic;
   }
}
