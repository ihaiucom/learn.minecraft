package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PaintingEntity extends HangingEntity {
   public PaintingType art;

   public PaintingEntity(EntityType<? extends PaintingEntity> p_i50221_1_, World p_i50221_2_) {
      super(p_i50221_1_, p_i50221_2_);
   }

   public PaintingEntity(World p_i45849_1_, BlockPos p_i45849_2_, Direction p_i45849_3_) {
      super(EntityType.PAINTING, p_i45849_1_, p_i45849_2_);
      List<PaintingType> lvt_4_1_ = Lists.newArrayList();
      int lvt_5_1_ = 0;
      Iterator lvt_6_1_ = Registry.MOTIVE.iterator();

      PaintingType lvt_7_2_;
      while(lvt_6_1_.hasNext()) {
         lvt_7_2_ = (PaintingType)lvt_6_1_.next();
         this.art = lvt_7_2_;
         this.updateFacingWithBoundingBox(p_i45849_3_);
         if (this.onValidSurface()) {
            lvt_4_1_.add(lvt_7_2_);
            int lvt_8_1_ = lvt_7_2_.getWidth() * lvt_7_2_.getHeight();
            if (lvt_8_1_ > lvt_5_1_) {
               lvt_5_1_ = lvt_8_1_;
            }
         }
      }

      if (!lvt_4_1_.isEmpty()) {
         lvt_6_1_ = lvt_4_1_.iterator();

         while(lvt_6_1_.hasNext()) {
            lvt_7_2_ = (PaintingType)lvt_6_1_.next();
            if (lvt_7_2_.getWidth() * lvt_7_2_.getHeight() < lvt_5_1_) {
               lvt_6_1_.remove();
            }
         }

         this.art = (PaintingType)lvt_4_1_.get(this.rand.nextInt(lvt_4_1_.size()));
      }

      this.updateFacingWithBoundingBox(p_i45849_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public PaintingEntity(World p_i48559_1_, BlockPos p_i48559_2_, Direction p_i48559_3_, PaintingType p_i48559_4_) {
      this(p_i48559_1_, p_i48559_2_, p_i48559_3_);
      this.art = p_i48559_4_;
      this.updateFacingWithBoundingBox(p_i48559_3_);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putString("Motive", Registry.MOTIVE.getKey(this.art).toString());
      super.writeAdditional(p_213281_1_);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      this.art = (PaintingType)Registry.MOTIVE.getOrDefault(ResourceLocation.tryCreate(p_70037_1_.getString("Motive")));
      super.readAdditional(p_70037_1_);
   }

   public int getWidthPixels() {
      return this.art == null ? 1 : this.art.getWidth();
   }

   public int getHeightPixels() {
      return this.art == null ? 1 : this.art.getHeight();
   }

   public void onBroken(@Nullable Entity p_110128_1_) {
      if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
         if (p_110128_1_ instanceof PlayerEntity) {
            PlayerEntity lvt_2_1_ = (PlayerEntity)p_110128_1_;
            if (lvt_2_1_.abilities.isCreativeMode) {
               return;
            }
         }

         this.entityDropItem(Items.PAINTING);
      }
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
   }

   public void setLocationAndAngles(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_) {
      this.setPosition(p_70012_1_, p_70012_3_, p_70012_5_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      BlockPos lvt_11_1_ = this.hangingPosition.add(p_180426_1_ - this.func_226277_ct_(), p_180426_3_ - this.func_226278_cu_(), p_180426_5_ - this.func_226281_cx_());
      this.setPosition((double)lvt_11_1_.getX(), (double)lvt_11_1_.getY(), (double)lvt_11_1_.getZ());
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnPaintingPacket(this);
   }
}
