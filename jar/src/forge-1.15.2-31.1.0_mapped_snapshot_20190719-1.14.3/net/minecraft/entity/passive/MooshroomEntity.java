package net.minecraft.entity.passive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity implements IShearable {
   private static final DataParameter<String> MOOSHROOM_TYPE;
   private Effect hasStewEffect;
   private int effectDuration;
   private UUID lightningUUID;

   public MooshroomEntity(EntityType<? extends MooshroomEntity> p_i50257_1_, World p_i50257_2_) {
      super(p_i50257_1_, p_i50257_2_);
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return p_205022_2_.getBlockState(p_205022_1_.down()).getBlock() == Blocks.MYCELIUM ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
   }

   public static boolean func_223318_c(EntityType<MooshroomEntity> p_223318_0_, IWorld p_223318_1_, SpawnReason p_223318_2_, BlockPos p_223318_3_, Random p_223318_4_) {
      return p_223318_1_.getBlockState(p_223318_3_.down()).getBlock() == Blocks.MYCELIUM && p_223318_1_.func_226659_b_(p_223318_3_, 0) > 8;
   }

   public void onStruckByLightning(LightningBoltEntity p_70077_1_) {
      UUID uuid = p_70077_1_.getUniqueID();
      if (!uuid.equals(this.lightningUUID)) {
         this.setMooshroomType(this.getMooshroomType() == MooshroomEntity.Type.RED ? MooshroomEntity.Type.BROWN : MooshroomEntity.Type.RED);
         this.lightningUUID = uuid;
         this.playSound(SoundEvents.ENTITY_MOOSHROOM_CONVERT, 2.0F, 1.0F);
      }

   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(MOOSHROOM_TYPE, MooshroomEntity.Type.RED.name);
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() == Items.BOWL && !this.isChild() && !p_184645_1_.abilities.isCreativeMode) {
         itemstack.shrink(1);
         boolean flag = false;
         ItemStack itemstack1;
         if (this.hasStewEffect != null) {
            flag = true;
            itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.addEffect(itemstack1, this.hasStewEffect, this.effectDuration);
            this.hasStewEffect = null;
            this.effectDuration = 0;
         } else {
            itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
         }

         if (itemstack.isEmpty()) {
            p_184645_1_.setHeldItem(p_184645_2_, itemstack1);
         } else if (!p_184645_1_.inventory.addItemStackToInventory(itemstack1)) {
            p_184645_1_.dropItem(itemstack1, false);
         }

         SoundEvent soundevent;
         if (flag) {
            soundevent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
         } else {
            soundevent = SoundEvents.ENTITY_MOOSHROOM_MILK;
         }

         this.playSound(soundevent, 1.0F, 1.0F);
         return true;
      } else {
         if (this.getMooshroomType() == MooshroomEntity.Type.BROWN && itemstack.getItem().isIn(ItemTags.SMALL_FLOWERS)) {
            if (this.hasStewEffect != null) {
               for(int i = 0; i < 2; ++i) {
                  this.world.addParticle(ParticleTypes.SMOKE, this.func_226277_ct_() + (double)(this.rand.nextFloat() / 2.0F), this.func_226283_e_(0.5D), this.func_226281_cx_() + (double)(this.rand.nextFloat() / 2.0F), 0.0D, (double)(this.rand.nextFloat() / 5.0F), 0.0D);
               }
            } else {
               Pair<Effect, Integer> pair = this.getStewEffect(itemstack);
               if (!p_184645_1_.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               for(int j = 0; j < 4; ++j) {
                  this.world.addParticle(ParticleTypes.EFFECT, this.func_226277_ct_() + (double)(this.rand.nextFloat() / 2.0F), this.func_226283_e_(0.5D), this.func_226281_cx_() + (double)(this.rand.nextFloat() / 2.0F), 0.0D, (double)(this.rand.nextFloat() / 5.0F), 0.0D);
               }

               this.hasStewEffect = (Effect)pair.getLeft();
               this.effectDuration = (Integer)pair.getRight();
               this.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0F, 1.0F);
            }
         }

         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putString("Type", this.getMooshroomType().name);
      if (this.hasStewEffect != null) {
         p_213281_1_.putByte("EffectId", (byte)Effect.getId(this.hasStewEffect));
         p_213281_1_.putInt("EffectDuration", this.effectDuration);
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setMooshroomType(MooshroomEntity.Type.getTypeByName(p_70037_1_.getString("Type")));
      if (p_70037_1_.contains("EffectId", 1)) {
         this.hasStewEffect = Effect.get(p_70037_1_.getByte("EffectId"));
      }

      if (p_70037_1_.contains("EffectDuration", 3)) {
         this.effectDuration = p_70037_1_.getInt("EffectDuration");
      }

   }

   private Pair<Effect, Integer> getStewEffect(ItemStack p_213443_1_) {
      FlowerBlock flowerblock = (FlowerBlock)((BlockItem)p_213443_1_.getItem()).getBlock();
      return Pair.of(flowerblock.getStewEffect(), flowerblock.getStewEffectDuration());
   }

   private void setMooshroomType(MooshroomEntity.Type p_213446_1_) {
      this.dataManager.set(MOOSHROOM_TYPE, p_213446_1_.name);
   }

   public MooshroomEntity.Type getMooshroomType() {
      return MooshroomEntity.Type.getTypeByName((String)this.dataManager.get(MOOSHROOM_TYPE));
   }

   public MooshroomEntity createChild(AgeableEntity p_90011_1_) {
      MooshroomEntity mooshroomentity = (MooshroomEntity)EntityType.MOOSHROOM.create(this.world);
      mooshroomentity.setMooshroomType(this.func_213445_a((MooshroomEntity)p_90011_1_));
      return mooshroomentity;
   }

   private MooshroomEntity.Type func_213445_a(MooshroomEntity p_213445_1_) {
      MooshroomEntity.Type mooshroomentity$type = this.getMooshroomType();
      MooshroomEntity.Type mooshroomentity$type1 = p_213445_1_.getMooshroomType();
      MooshroomEntity.Type mooshroomentity$type2;
      if (mooshroomentity$type == mooshroomentity$type1 && this.rand.nextInt(1024) == 0) {
         mooshroomentity$type2 = mooshroomentity$type == MooshroomEntity.Type.BROWN ? MooshroomEntity.Type.RED : MooshroomEntity.Type.BROWN;
      } else {
         mooshroomentity$type2 = this.rand.nextBoolean() ? mooshroomentity$type : mooshroomentity$type1;
      }

      return mooshroomentity$type2;
   }

   public boolean isShearable(ItemStack p_isShearable_1_, IWorldReader p_isShearable_2_, BlockPos p_isShearable_3_) {
      return !this.isChild();
   }

   public List<ItemStack> onSheared(ItemStack p_onSheared_1_, IWorld p_onSheared_2_, BlockPos p_onSheared_3_, int p_onSheared_4_) {
      List<ItemStack> ret = new ArrayList();
      this.world.addParticle(ParticleTypes.EXPLOSION, this.func_226277_ct_(), this.func_226283_e_(0.5D), this.func_226281_cx_(), 0.0D, 0.0D, 0.0D);
      if (!this.world.isRemote) {
         this.remove();
         CowEntity cowentity = (CowEntity)EntityType.COW.create(this.world);
         cowentity.setLocationAndAngles(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, this.rotationPitch);
         cowentity.setHealth(this.getHealth());
         cowentity.renderYawOffset = this.renderYawOffset;
         if (this.hasCustomName()) {
            cowentity.setCustomName(this.getCustomName());
            cowentity.setCustomNameVisible(this.isCustomNameVisible());
         }

         this.world.addEntity(cowentity);

         for(int i = 0; i < 5; ++i) {
            ret.add(new ItemStack(this.getMooshroomType().renderState.getBlock()));
         }

         this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
      }

      return ret;
   }

   // $FF: synthetic method
   private static void lambda$processInteract$0(Hand p_lambda$processInteract$0_0_, PlayerEntity p_lambda$processInteract$0_1_) {
      p_lambda$processInteract$0_1_.sendBreakAnimation(p_lambda$processInteract$0_0_);
   }

   static {
      MOOSHROOM_TYPE = EntityDataManager.createKey(MooshroomEntity.class, DataSerializers.STRING);
   }

   public static enum Type {
      RED("red", Blocks.RED_MUSHROOM.getDefaultState()),
      BROWN("brown", Blocks.BROWN_MUSHROOM.getDefaultState());

      private final String name;
      private final BlockState renderState;

      private Type(String p_i50425_3_, BlockState p_i50425_4_) {
         this.name = p_i50425_3_;
         this.renderState = p_i50425_4_;
      }

      @OnlyIn(Dist.CLIENT)
      public BlockState getRenderState() {
         return this.renderState;
      }

      private static MooshroomEntity.Type getTypeByName(String p_221097_0_) {
         MooshroomEntity.Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            MooshroomEntity.Type mooshroomentity$type = var1[var3];
            if (mooshroomentity$type.name.equals(p_221097_0_)) {
               return mooshroomentity$type;
            }
         }

         return RED;
      }
   }
}
