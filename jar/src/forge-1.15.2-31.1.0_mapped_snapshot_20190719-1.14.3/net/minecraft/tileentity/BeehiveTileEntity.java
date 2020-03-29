package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class BeehiveTileEntity extends TileEntity implements ITickableTileEntity {
   private final List<BeehiveTileEntity.Bee> field_226958_a_ = Lists.newArrayList();
   @Nullable
   private BlockPos field_226959_b_ = null;

   public BeehiveTileEntity() {
      super(TileEntityType.field_226985_G_);
   }

   public void markDirty() {
      if (this.func_226968_d_()) {
         this.func_226963_a_((PlayerEntity)null, this.world.getBlockState(this.getPos()), BeehiveTileEntity.State.EMERGENCY);
      }

      super.markDirty();
   }

   public boolean func_226968_d_() {
      if (this.world == null) {
         return false;
      } else {
         Iterator var1 = BlockPos.getAllInBoxMutable(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1)).iterator();

         BlockPos lvt_2_1_;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            lvt_2_1_ = (BlockPos)var1.next();
         } while(!(this.world.getBlockState(lvt_2_1_).getBlock() instanceof FireBlock));

         return true;
      }
   }

   public boolean func_226969_f_() {
      return this.field_226958_a_.isEmpty();
   }

   public boolean func_226970_h_() {
      return this.field_226958_a_.size() == 3;
   }

   public void func_226963_a_(@Nullable PlayerEntity p_226963_1_, BlockState p_226963_2_, BeehiveTileEntity.State p_226963_3_) {
      List<Entity> lvt_4_1_ = this.func_226965_a_(p_226963_2_, p_226963_3_);
      if (p_226963_1_ != null) {
         Iterator var5 = lvt_4_1_.iterator();

         while(var5.hasNext()) {
            Entity lvt_6_1_ = (Entity)var5.next();
            if (lvt_6_1_ instanceof BeeEntity) {
               BeeEntity lvt_7_1_ = (BeeEntity)lvt_6_1_;
               if (p_226963_1_.getPositionVec().squareDistanceTo(lvt_6_1_.getPositionVec()) <= 16.0D) {
                  if (!this.func_226972_k_()) {
                     lvt_7_1_.func_226391_a_(p_226963_1_);
                  } else {
                     lvt_7_1_.func_226450_t_(400);
                  }
               }
            }
         }
      }

   }

   private List<Entity> func_226965_a_(BlockState p_226965_1_, BeehiveTileEntity.State p_226965_2_) {
      List<Entity> lvt_3_1_ = Lists.newArrayList();
      this.field_226958_a_.removeIf((p_226966_4_) -> {
         return this.func_226967_a_(p_226965_1_, p_226966_4_.field_226977_a_, lvt_3_1_, p_226965_2_);
      });
      return lvt_3_1_;
   }

   public void func_226961_a_(Entity p_226961_1_, boolean p_226961_2_) {
      this.func_226962_a_(p_226961_1_, p_226961_2_, 0);
   }

   public int func_226971_j_() {
      return this.field_226958_a_.size();
   }

   public static int func_226964_a_(BlockState p_226964_0_) {
      return (Integer)p_226964_0_.get(BeehiveBlock.field_226873_c_);
   }

   public boolean func_226972_k_() {
      return CampfireBlock.func_226914_b_(this.world, this.getPos(), 5);
   }

   protected void func_226973_l_() {
      DebugPacketSender.func_229750_a_(this);
   }

   public void func_226962_a_(Entity p_226962_1_, boolean p_226962_2_, int p_226962_3_) {
      if (this.field_226958_a_.size() < 3) {
         p_226962_1_.stopRiding();
         p_226962_1_.removePassengers();
         CompoundNBT lvt_4_1_ = new CompoundNBT();
         p_226962_1_.writeUnlessPassenger(lvt_4_1_);
         this.field_226958_a_.add(new BeehiveTileEntity.Bee(lvt_4_1_, p_226962_3_, p_226962_2_ ? 2400 : 600));
         if (this.world != null) {
            if (p_226962_1_ instanceof BeeEntity) {
               BeeEntity lvt_5_1_ = (BeeEntity)p_226962_1_;
               if (lvt_5_1_.func_226425_er_() && (!this.func_226975_x_() || this.world.rand.nextBoolean())) {
                  this.field_226959_b_ = lvt_5_1_.func_226424_eq_();
               }
            }

            BlockPos lvt_5_2_ = this.getPos();
            this.world.playSound((PlayerEntity)null, (double)lvt_5_2_.getX(), (double)lvt_5_2_.getY(), (double)lvt_5_2_.getZ(), SoundEvents.field_226131_af_, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         p_226962_1_.remove();
      }
   }

   private boolean func_226967_a_(BlockState p_226967_1_, CompoundNBT p_226967_2_, @Nullable List<Entity> p_226967_3_, BeehiveTileEntity.State p_226967_4_) {
      BlockPos lvt_5_1_ = this.getPos();
      if ((this.world.func_226690_K_() || this.world.isRaining()) && p_226967_4_ != BeehiveTileEntity.State.EMERGENCY) {
         return false;
      } else {
         p_226967_2_.remove("Passengers");
         p_226967_2_.remove("Leash");
         p_226967_2_.func_229681_c_("UUID");
         Direction lvt_6_1_ = (Direction)p_226967_1_.get(BeehiveBlock.field_226872_b_);
         BlockPos lvt_7_1_ = lvt_5_1_.offset(lvt_6_1_);
         boolean lvt_8_1_ = !this.world.getBlockState(lvt_7_1_).getCollisionShape(this.world, lvt_7_1_).isEmpty();
         if (lvt_8_1_ && p_226967_4_ != BeehiveTileEntity.State.EMERGENCY) {
            return false;
         } else {
            Entity lvt_9_1_ = EntityType.func_220335_a(p_226967_2_, this.world, (p_226960_0_) -> {
               return p_226960_0_;
            });
            if (lvt_9_1_ != null) {
               float lvt_10_1_ = lvt_9_1_.getWidth();
               double lvt_11_1_ = lvt_8_1_ ? 0.0D : 0.55D + (double)(lvt_10_1_ / 2.0F);
               double lvt_13_1_ = (double)lvt_5_1_.getX() + 0.5D + lvt_11_1_ * (double)lvt_6_1_.getXOffset();
               double lvt_15_1_ = (double)lvt_5_1_.getY() + 0.5D - (double)(lvt_9_1_.getHeight() / 2.0F);
               double lvt_17_1_ = (double)lvt_5_1_.getZ() + 0.5D + lvt_11_1_ * (double)lvt_6_1_.getZOffset();
               lvt_9_1_.setLocationAndAngles(lvt_13_1_, lvt_15_1_, lvt_17_1_, lvt_9_1_.rotationYaw, lvt_9_1_.rotationPitch);
               if (!lvt_9_1_.getType().isContained(EntityTypeTags.field_226155_c_)) {
                  return false;
               } else {
                  if (lvt_9_1_ instanceof BeeEntity) {
                     BeeEntity lvt_19_1_ = (BeeEntity)lvt_9_1_;
                     if (this.func_226975_x_() && !lvt_19_1_.func_226425_er_() && this.world.rand.nextFloat() < 0.9F) {
                        lvt_19_1_.func_226431_g_(this.field_226959_b_);
                     }

                     if (p_226967_4_ == BeehiveTileEntity.State.HONEY_DELIVERED) {
                        lvt_19_1_.func_226413_eG_();
                        if (p_226967_1_.getBlock().isIn(BlockTags.field_226151_aa_)) {
                           int lvt_20_1_ = func_226964_a_(p_226967_1_);
                           if (lvt_20_1_ < 5) {
                              int lvt_21_1_ = this.world.rand.nextInt(100) == 0 ? 2 : 1;
                              if (lvt_20_1_ + lvt_21_1_ > 5) {
                                 --lvt_21_1_;
                              }

                              this.world.setBlockState(this.getPos(), (BlockState)p_226967_1_.with(BeehiveBlock.field_226873_c_, lvt_20_1_ + lvt_21_1_));
                           }
                        }
                     }

                     lvt_19_1_.func_226426_eu_();
                     if (p_226967_3_ != null) {
                        p_226967_3_.add(lvt_19_1_);
                     }
                  }

                  BlockPos lvt_19_2_ = this.getPos();
                  this.world.playSound((PlayerEntity)null, (double)lvt_19_2_.getX(), (double)lvt_19_2_.getY(), (double)lvt_19_2_.getZ(), SoundEvents.field_226132_ag_, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  return this.world.addEntity(lvt_9_1_);
               }
            } else {
               return false;
            }
         }
      }
   }

   private boolean func_226975_x_() {
      return this.field_226959_b_ != null;
   }

   private void func_226976_y_() {
      Iterator<BeehiveTileEntity.Bee> lvt_1_1_ = this.field_226958_a_.iterator();
      BlockState lvt_2_1_ = this.getBlockState();

      while(lvt_1_1_.hasNext()) {
         BeehiveTileEntity.Bee lvt_3_1_ = (BeehiveTileEntity.Bee)lvt_1_1_.next();
         if (lvt_3_1_.field_226978_b_ > lvt_3_1_.field_226979_c_) {
            CompoundNBT lvt_4_1_ = lvt_3_1_.field_226977_a_;
            BeehiveTileEntity.State lvt_5_1_ = lvt_4_1_.getBoolean("HasNectar") ? BeehiveTileEntity.State.HONEY_DELIVERED : BeehiveTileEntity.State.BEE_RELEASED;
            if (this.func_226967_a_(lvt_2_1_, lvt_4_1_, (List)null, lvt_5_1_)) {
               lvt_1_1_.remove();
            }
         } else {
            lvt_3_1_.field_226978_b_++;
         }
      }

   }

   public void tick() {
      if (!this.world.isRemote) {
         this.func_226976_y_();
         BlockPos lvt_1_1_ = this.getPos();
         if (this.field_226958_a_.size() > 0 && this.world.getRandom().nextDouble() < 0.005D) {
            double lvt_2_1_ = (double)lvt_1_1_.getX() + 0.5D;
            double lvt_4_1_ = (double)lvt_1_1_.getY();
            double lvt_6_1_ = (double)lvt_1_1_.getZ() + 0.5D;
            this.world.playSound((PlayerEntity)null, lvt_2_1_, lvt_4_1_, lvt_6_1_, SoundEvents.field_226134_ai_, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }

         this.func_226973_l_();
      }
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.field_226958_a_.clear();
      ListNBT lvt_2_1_ = p_145839_1_.getList("Bees", 10);

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
         CompoundNBT lvt_4_1_ = lvt_2_1_.getCompound(lvt_3_1_);
         BeehiveTileEntity.Bee lvt_5_1_ = new BeehiveTileEntity.Bee(lvt_4_1_.getCompound("EntityData"), lvt_4_1_.getInt("TicksInHive"), lvt_4_1_.getInt("MinOccupationTicks"));
         this.field_226958_a_.add(lvt_5_1_);
      }

      this.field_226959_b_ = null;
      if (p_145839_1_.contains("FlowerPos")) {
         this.field_226959_b_ = NBTUtil.readBlockPos(p_145839_1_.getCompound("FlowerPos"));
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      p_189515_1_.put("Bees", this.func_226974_m_());
      if (this.func_226975_x_()) {
         p_189515_1_.put("FlowerPos", NBTUtil.writeBlockPos(this.field_226959_b_));
      }

      return p_189515_1_;
   }

   public ListNBT func_226974_m_() {
      ListNBT lvt_1_1_ = new ListNBT();
      Iterator var2 = this.field_226958_a_.iterator();

      while(var2.hasNext()) {
         BeehiveTileEntity.Bee lvt_3_1_ = (BeehiveTileEntity.Bee)var2.next();
         lvt_3_1_.field_226977_a_.func_229681_c_("UUID");
         CompoundNBT lvt_4_1_ = new CompoundNBT();
         lvt_4_1_.put("EntityData", lvt_3_1_.field_226977_a_);
         lvt_4_1_.putInt("TicksInHive", lvt_3_1_.field_226978_b_);
         lvt_4_1_.putInt("MinOccupationTicks", lvt_3_1_.field_226979_c_);
         lvt_1_1_.add(lvt_4_1_);
      }

      return lvt_1_1_;
   }

   static class Bee {
      private final CompoundNBT field_226977_a_;
      private int field_226978_b_;
      private final int field_226979_c_;

      private Bee(CompoundNBT p_i225767_1_, int p_i225767_2_, int p_i225767_3_) {
         p_i225767_1_.func_229681_c_("UUID");
         this.field_226977_a_ = p_i225767_1_;
         this.field_226978_b_ = p_i225767_2_;
         this.field_226979_c_ = p_i225767_3_;
      }

      // $FF: synthetic method
      Bee(CompoundNBT p_i225768_1_, int p_i225768_2_, int p_i225768_3_, Object p_i225768_4_) {
         this(p_i225768_1_, p_i225768_2_, p_i225768_3_);
      }
   }

   public static enum State {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY;
   }
}
