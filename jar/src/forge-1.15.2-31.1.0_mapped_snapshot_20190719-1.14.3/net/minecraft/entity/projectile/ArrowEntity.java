package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArrowEntity extends AbstractArrowEntity {
   private static final DataParameter<Integer> COLOR;
   private Potion potion;
   private final Set<EffectInstance> customPotionEffects;
   private boolean fixedColor;

   public ArrowEntity(EntityType<? extends ArrowEntity> p_i50172_1_, World p_i50172_2_) {
      super(p_i50172_1_, p_i50172_2_);
      this.potion = Potions.EMPTY;
      this.customPotionEffects = Sets.newHashSet();
   }

   public ArrowEntity(World p_i46757_1_, double p_i46757_2_, double p_i46757_4_, double p_i46757_6_) {
      super(EntityType.ARROW, p_i46757_2_, p_i46757_4_, p_i46757_6_, p_i46757_1_);
      this.potion = Potions.EMPTY;
      this.customPotionEffects = Sets.newHashSet();
   }

   public ArrowEntity(World p_i46758_1_, LivingEntity p_i46758_2_) {
      super(EntityType.ARROW, p_i46758_2_, p_i46758_1_);
      this.potion = Potions.EMPTY;
      this.customPotionEffects = Sets.newHashSet();
   }

   public void setPotionEffect(ItemStack p_184555_1_) {
      if (p_184555_1_.getItem() == Items.TIPPED_ARROW) {
         this.potion = PotionUtils.getPotionFromItem(p_184555_1_);
         Collection<EffectInstance> collection = PotionUtils.getFullEffectsFromItem(p_184555_1_);
         if (!collection.isEmpty()) {
            Iterator var3 = collection.iterator();

            while(var3.hasNext()) {
               EffectInstance effectinstance = (EffectInstance)var3.next();
               this.customPotionEffects.add(new EffectInstance(effectinstance));
            }
         }

         int i = getCustomColor(p_184555_1_);
         if (i == -1) {
            this.refreshColor();
         } else {
            this.setFixedColor(i);
         }
      } else if (p_184555_1_.getItem() == Items.ARROW) {
         this.potion = Potions.EMPTY;
         this.customPotionEffects.clear();
         this.setFixedColor(-1);
      }

   }

   public static int getCustomColor(ItemStack p_191508_0_) {
      CompoundNBT compoundnbt = p_191508_0_.getTag();
      return compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99) ? compoundnbt.getInt("CustomPotionColor") : -1;
   }

   private void refreshColor() {
      this.fixedColor = false;
      if (this.potion == Potions.EMPTY && this.customPotionEffects.isEmpty()) {
         this.dataManager.set(COLOR, -1);
      } else {
         this.dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects)));
      }

   }

   public void addEffect(EffectInstance p_184558_1_) {
      this.customPotionEffects.add(p_184558_1_);
      this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects)));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(COLOR, -1);
   }

   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.inGround) {
            if (this.timeInGround % 5 == 0) {
               this.spawnPotionParticles(1);
            }
         } else {
            this.spawnPotionParticles(2);
         }
      } else if (this.inGround && this.timeInGround != 0 && !this.customPotionEffects.isEmpty() && this.timeInGround >= 600) {
         this.world.setEntityState(this, (byte)0);
         this.potion = Potions.EMPTY;
         this.customPotionEffects.clear();
         this.dataManager.set(COLOR, -1);
      }

   }

   private void spawnPotionParticles(int p_184556_1_) {
      int i = this.getColor();
      if (i != -1 && p_184556_1_ > 0) {
         double d0 = (double)(i >> 16 & 255) / 255.0D;
         double d1 = (double)(i >> 8 & 255) / 255.0D;
         double d2 = (double)(i >> 0 & 255) / 255.0D;

         for(int j = 0; j < p_184556_1_; ++j) {
            this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.func_226282_d_(0.5D), this.func_226279_cv_(), this.func_226287_g_(0.5D), d0, d1, d2);
         }
      }

   }

   public int getColor() {
      return (Integer)this.dataManager.get(COLOR);
   }

   private void setFixedColor(int p_191507_1_) {
      this.fixedColor = true;
      this.dataManager.set(COLOR, p_191507_1_);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if (this.potion != Potions.EMPTY && this.potion != null) {
         p_213281_1_.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (this.fixedColor) {
         p_213281_1_.putInt("Color", this.getColor());
      }

      if (!this.customPotionEffects.isEmpty()) {
         ListNBT listnbt = new ListNBT();
         Iterator var3 = this.customPotionEffects.iterator();

         while(var3.hasNext()) {
            EffectInstance effectinstance = (EffectInstance)var3.next();
            listnbt.add(effectinstance.write(new CompoundNBT()));
         }

         p_213281_1_.put("CustomPotionEffects", listnbt);
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("Potion", 8)) {
         this.potion = PotionUtils.getPotionTypeFromNBT(p_70037_1_);
      }

      Iterator var2 = PotionUtils.getFullEffectsFromTag(p_70037_1_).iterator();

      while(var2.hasNext()) {
         EffectInstance effectinstance = (EffectInstance)var2.next();
         this.addEffect(effectinstance);
      }

      if (p_70037_1_.contains("Color", 99)) {
         this.setFixedColor(p_70037_1_.getInt("Color"));
      } else {
         this.refreshColor();
      }

   }

   protected void arrowHit(LivingEntity p_184548_1_) {
      super.arrowHit(p_184548_1_);
      Iterator var2 = this.potion.getEffects().iterator();

      EffectInstance effectinstance1;
      while(var2.hasNext()) {
         effectinstance1 = (EffectInstance)var2.next();
         p_184548_1_.addPotionEffect(new EffectInstance(effectinstance1.getPotion(), Math.max(effectinstance1.getDuration() / 8, 1), effectinstance1.getAmplifier(), effectinstance1.isAmbient(), effectinstance1.doesShowParticles()));
      }

      if (!this.customPotionEffects.isEmpty()) {
         var2 = this.customPotionEffects.iterator();

         while(var2.hasNext()) {
            effectinstance1 = (EffectInstance)var2.next();
            p_184548_1_.addPotionEffect(effectinstance1);
         }
      }

   }

   protected ItemStack getArrowStack() {
      if (this.customPotionEffects.isEmpty() && this.potion == Potions.EMPTY) {
         return new ItemStack(Items.ARROW);
      } else {
         ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
         PotionUtils.addPotionToItemStack(itemstack, this.potion);
         PotionUtils.appendEffects(itemstack, this.customPotionEffects);
         if (this.fixedColor) {
            itemstack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
         }

         return itemstack;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 0) {
         int i = this.getColor();
         if (i != -1) {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;

            for(int j = 0; j < 20; ++j) {
               this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.func_226282_d_(0.5D), this.func_226279_cv_(), this.func_226287_g_(0.5D), d0, d1, d2);
            }
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   static {
      COLOR = EntityDataManager.createKey(ArrowEntity.class, DataSerializers.VARINT);
   }
}
