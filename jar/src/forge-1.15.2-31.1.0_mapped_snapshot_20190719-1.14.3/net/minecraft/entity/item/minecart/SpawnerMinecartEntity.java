package net.minecraft.entity.item.minecart;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpawnerMinecartEntity extends AbstractMinecartEntity {
   private final AbstractSpawner mobSpawnerLogic = new AbstractSpawner() {
      public void broadcastEvent(int p_98267_1_) {
         SpawnerMinecartEntity.this.world.setEntityState(SpawnerMinecartEntity.this, (byte)p_98267_1_);
      }

      public World getWorld() {
         return SpawnerMinecartEntity.this.world;
      }

      public BlockPos getSpawnerPosition() {
         return new BlockPos(SpawnerMinecartEntity.this);
      }

      @Nullable
      public Entity getSpawnerEntity() {
         return SpawnerMinecartEntity.this;
      }
   };

   public SpawnerMinecartEntity(EntityType<? extends SpawnerMinecartEntity> p_i50114_1_, World p_i50114_2_) {
      super(p_i50114_1_, p_i50114_2_);
   }

   public SpawnerMinecartEntity(World p_i46753_1_, double p_i46753_2_, double p_i46753_4_, double p_i46753_6_) {
      super(EntityType.SPAWNER_MINECART, p_i46753_1_, p_i46753_2_, p_i46753_4_, p_i46753_6_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.SPAWNER;
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.SPAWNER.getDefaultState();
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.mobSpawnerLogic.read(p_70037_1_);
   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      this.mobSpawnerLogic.write(p_213281_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      this.mobSpawnerLogic.setDelayToMin(p_70103_1_);
   }

   public void tick() {
      super.tick();
      this.mobSpawnerLogic.tick();
   }

   public boolean ignoreItemEntityData() {
      return true;
   }
}
