package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class PotionEntity extends ThrowableEntity implements IRendersAsItem {
   private static final DataParameter<ItemStack> ITEM;
   private static final Logger LOGGER;
   public static final Predicate<LivingEntity> WATER_SENSITIVE;

   public PotionEntity(EntityType<? extends PotionEntity> p_i50149_1_, World p_i50149_2_) {
      super(p_i50149_1_, p_i50149_2_);
   }

   public PotionEntity(World p_i50150_1_, LivingEntity p_i50150_2_) {
      super(EntityType.POTION, p_i50150_2_, p_i50150_1_);
   }

   public PotionEntity(World p_i50151_1_, double p_i50151_2_, double p_i50151_4_, double p_i50151_6_) {
      super(EntityType.POTION, p_i50151_2_, p_i50151_4_, p_i50151_6_, p_i50151_1_);
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   public ItemStack getItem() {
      ItemStack lvt_1_1_ = (ItemStack)this.getDataManager().get(ITEM);
      if (lvt_1_1_.getItem() != Items.SPLASH_POTION && lvt_1_1_.getItem() != Items.LINGERING_POTION) {
         if (this.world != null) {
            LOGGER.error("ThrownPotion entity {} has no item?!", this.getEntityId());
         }

         return new ItemStack(Items.SPLASH_POTION);
      } else {
         return lvt_1_1_;
      }
   }

   public void setItem(ItemStack p_184541_1_) {
      this.getDataManager().set(ITEM, p_184541_1_.copy());
   }

   protected float getGravityVelocity() {
      return 0.05F;
   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      if (!this.world.isRemote) {
         ItemStack lvt_2_1_ = this.getItem();
         Potion lvt_3_1_ = PotionUtils.getPotionFromItem(lvt_2_1_);
         List<EffectInstance> lvt_4_1_ = PotionUtils.getEffectsFromStack(lvt_2_1_);
         boolean lvt_5_1_ = lvt_3_1_ == Potions.WATER && lvt_4_1_.isEmpty();
         if (p_70184_1_.getType() == RayTraceResult.Type.BLOCK && lvt_5_1_) {
            BlockRayTraceResult lvt_6_1_ = (BlockRayTraceResult)p_70184_1_;
            Direction lvt_7_1_ = lvt_6_1_.getFace();
            BlockPos lvt_8_1_ = lvt_6_1_.getPos().offset(lvt_7_1_);
            this.extinguishFires(lvt_8_1_, lvt_7_1_);
            this.extinguishFires(lvt_8_1_.offset(lvt_7_1_.getOpposite()), lvt_7_1_);
            Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

            while(var9.hasNext()) {
               Direction lvt_10_1_ = (Direction)var9.next();
               this.extinguishFires(lvt_8_1_.offset(lvt_10_1_), lvt_10_1_);
            }
         }

         if (lvt_5_1_) {
            this.applyWater();
         } else if (!lvt_4_1_.isEmpty()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(lvt_2_1_, lvt_3_1_);
            } else {
               this.func_213888_a(lvt_4_1_, p_70184_1_.getType() == RayTraceResult.Type.ENTITY ? ((EntityRayTraceResult)p_70184_1_).getEntity() : null);
            }
         }

         int lvt_6_2_ = lvt_3_1_.hasInstantEffect() ? 2007 : 2002;
         this.world.playEvent(lvt_6_2_, new BlockPos(this), PotionUtils.getColor(lvt_2_1_));
         this.remove();
      }
   }

   private void applyWater() {
      AxisAlignedBB lvt_1_1_ = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
      List<LivingEntity> lvt_2_1_ = this.world.getEntitiesWithinAABB(LivingEntity.class, lvt_1_1_, WATER_SENSITIVE);
      if (!lvt_2_1_.isEmpty()) {
         Iterator var3 = lvt_2_1_.iterator();

         while(var3.hasNext()) {
            LivingEntity lvt_4_1_ = (LivingEntity)var3.next();
            double lvt_5_1_ = this.getDistanceSq(lvt_4_1_);
            if (lvt_5_1_ < 16.0D && isWaterSensitiveEntity(lvt_4_1_)) {
               lvt_4_1_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(lvt_4_1_, this.getThrower()), 1.0F);
            }
         }
      }

   }

   private void func_213888_a(List<EffectInstance> p_213888_1_, @Nullable Entity p_213888_2_) {
      AxisAlignedBB lvt_3_1_ = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
      List<LivingEntity> lvt_4_1_ = this.world.getEntitiesWithinAABB(LivingEntity.class, lvt_3_1_);
      if (!lvt_4_1_.isEmpty()) {
         Iterator var5 = lvt_4_1_.iterator();

         while(true) {
            LivingEntity lvt_6_1_;
            double lvt_7_1_;
            do {
               do {
                  if (!var5.hasNext()) {
                     return;
                  }

                  lvt_6_1_ = (LivingEntity)var5.next();
               } while(!lvt_6_1_.canBeHitWithPotion());

               lvt_7_1_ = this.getDistanceSq(lvt_6_1_);
            } while(lvt_7_1_ >= 16.0D);

            double lvt_9_1_ = 1.0D - Math.sqrt(lvt_7_1_) / 4.0D;
            if (lvt_6_1_ == p_213888_2_) {
               lvt_9_1_ = 1.0D;
            }

            Iterator var11 = p_213888_1_.iterator();

            while(var11.hasNext()) {
               EffectInstance lvt_12_1_ = (EffectInstance)var11.next();
               Effect lvt_13_1_ = lvt_12_1_.getPotion();
               if (lvt_13_1_.isInstant()) {
                  lvt_13_1_.affectEntity(this, this.getThrower(), lvt_6_1_, lvt_12_1_.getAmplifier(), lvt_9_1_);
               } else {
                  int lvt_14_1_ = (int)(lvt_9_1_ * (double)lvt_12_1_.getDuration() + 0.5D);
                  if (lvt_14_1_ > 20) {
                     lvt_6_1_.addPotionEffect(new EffectInstance(lvt_13_1_, lvt_14_1_, lvt_12_1_.getAmplifier(), lvt_12_1_.isAmbient(), lvt_12_1_.doesShowParticles()));
                  }
               }
            }
         }
      }
   }

   private void makeAreaOfEffectCloud(ItemStack p_190542_1_, Potion p_190542_2_) {
      AreaEffectCloudEntity lvt_3_1_ = new AreaEffectCloudEntity(this.world, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
      lvt_3_1_.setOwner(this.getThrower());
      lvt_3_1_.setRadius(3.0F);
      lvt_3_1_.setRadiusOnUse(-0.5F);
      lvt_3_1_.setWaitTime(10);
      lvt_3_1_.setRadiusPerTick(-lvt_3_1_.getRadius() / (float)lvt_3_1_.getDuration());
      lvt_3_1_.setPotion(p_190542_2_);
      Iterator var4 = PotionUtils.getFullEffectsFromItem(p_190542_1_).iterator();

      while(var4.hasNext()) {
         EffectInstance lvt_5_1_ = (EffectInstance)var4.next();
         lvt_3_1_.addEffect(new EffectInstance(lvt_5_1_));
      }

      CompoundNBT lvt_4_1_ = p_190542_1_.getTag();
      if (lvt_4_1_ != null && lvt_4_1_.contains("CustomPotionColor", 99)) {
         lvt_3_1_.setColor(lvt_4_1_.getInt("CustomPotionColor"));
      }

      this.world.addEntity(lvt_3_1_);
   }

   private boolean isLingering() {
      return this.getItem().getItem() == Items.LINGERING_POTION;
   }

   private void extinguishFires(BlockPos p_184542_1_, Direction p_184542_2_) {
      BlockState lvt_3_1_ = this.world.getBlockState(p_184542_1_);
      Block lvt_4_1_ = lvt_3_1_.getBlock();
      if (lvt_4_1_ == Blocks.FIRE) {
         this.world.extinguishFire((PlayerEntity)null, p_184542_1_.offset(p_184542_2_), p_184542_2_.getOpposite());
      } else if (lvt_4_1_ == Blocks.CAMPFIRE && (Boolean)lvt_3_1_.get(CampfireBlock.LIT)) {
         this.world.playEvent((PlayerEntity)null, 1009, p_184542_1_, 0);
         this.world.setBlockState(p_184542_1_, (BlockState)lvt_3_1_.with(CampfireBlock.LIT, false));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      ItemStack lvt_2_1_ = ItemStack.read(p_70037_1_.getCompound("Potion"));
      if (lvt_2_1_.isEmpty()) {
         this.remove();
      } else {
         this.setItem(lvt_2_1_);
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      ItemStack lvt_2_1_ = this.getItem();
      if (!lvt_2_1_.isEmpty()) {
         p_213281_1_.put("Potion", lvt_2_1_.write(new CompoundNBT()));
      }

   }

   private static boolean isWaterSensitiveEntity(LivingEntity p_190544_0_) {
      return p_190544_0_ instanceof EndermanEntity || p_190544_0_ instanceof BlazeEntity;
   }

   static {
      ITEM = EntityDataManager.createKey(PotionEntity.class, DataSerializers.ITEMSTACK);
      LOGGER = LogManager.getLogger();
      WATER_SENSITIVE = PotionEntity::isWaterSensitiveEntity;
   }
}
