package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CombatTracker {
   private final List<CombatEntry> combatEntries = Lists.newArrayList();
   private final LivingEntity fighter;
   private int lastDamageTime;
   private int combatStartTime;
   private int combatEndTime;
   private boolean inCombat;
   private boolean takingDamage;
   private String fallSuffix;

   public CombatTracker(LivingEntity p_i1565_1_) {
      this.fighter = p_i1565_1_;
   }

   public void calculateFallSuffix() {
      this.resetFallSuffix();
      if (this.fighter.isOnLadder()) {
         Block lvt_1_1_ = this.fighter.world.getBlockState(new BlockPos(this.fighter)).getBlock();
         if (lvt_1_1_ == Blocks.LADDER) {
            this.fallSuffix = "ladder";
         } else if (lvt_1_1_ == Blocks.VINE) {
            this.fallSuffix = "vines";
         }
      } else if (this.fighter.isInWater()) {
         this.fallSuffix = "water";
      }

   }

   public void trackDamage(DamageSource p_94547_1_, float p_94547_2_, float p_94547_3_) {
      this.reset();
      this.calculateFallSuffix();
      CombatEntry lvt_4_1_ = new CombatEntry(p_94547_1_, this.fighter.ticksExisted, p_94547_2_, p_94547_3_, this.fallSuffix, this.fighter.fallDistance);
      this.combatEntries.add(lvt_4_1_);
      this.lastDamageTime = this.fighter.ticksExisted;
      this.takingDamage = true;
      if (lvt_4_1_.isLivingDamageSrc() && !this.inCombat && this.fighter.isAlive()) {
         this.inCombat = true;
         this.combatStartTime = this.fighter.ticksExisted;
         this.combatEndTime = this.combatStartTime;
         this.fighter.sendEnterCombat();
      }

   }

   public ITextComponent getDeathMessage() {
      if (this.combatEntries.isEmpty()) {
         return new TranslationTextComponent("death.attack.generic", new Object[]{this.fighter.getDisplayName()});
      } else {
         CombatEntry lvt_1_1_ = this.getBestCombatEntry();
         CombatEntry lvt_2_1_ = (CombatEntry)this.combatEntries.get(this.combatEntries.size() - 1);
         ITextComponent lvt_4_1_ = lvt_2_1_.getDamageSrcDisplayName();
         Entity lvt_5_1_ = lvt_2_1_.getDamageSrc().getTrueSource();
         Object lvt_3_7_;
         if (lvt_1_1_ != null && lvt_2_1_.getDamageSrc() == DamageSource.FALL) {
            ITextComponent lvt_6_1_ = lvt_1_1_.getDamageSrcDisplayName();
            if (lvt_1_1_.getDamageSrc() != DamageSource.FALL && lvt_1_1_.getDamageSrc() != DamageSource.OUT_OF_WORLD) {
               if (lvt_6_1_ == null || lvt_4_1_ != null && lvt_6_1_.equals(lvt_4_1_)) {
                  if (lvt_4_1_ != null) {
                     ItemStack lvt_7_2_ = lvt_5_1_ instanceof LivingEntity ? ((LivingEntity)lvt_5_1_).getHeldItemMainhand() : ItemStack.EMPTY;
                     if (!lvt_7_2_.isEmpty() && lvt_7_2_.hasDisplayName()) {
                        lvt_3_7_ = new TranslationTextComponent("death.fell.finish.item", new Object[]{this.fighter.getDisplayName(), lvt_4_1_, lvt_7_2_.getTextComponent()});
                     } else {
                        lvt_3_7_ = new TranslationTextComponent("death.fell.finish", new Object[]{this.fighter.getDisplayName(), lvt_4_1_});
                     }
                  } else {
                     lvt_3_7_ = new TranslationTextComponent("death.fell.killer", new Object[]{this.fighter.getDisplayName()});
                  }
               } else {
                  Entity lvt_7_1_ = lvt_1_1_.getDamageSrc().getTrueSource();
                  ItemStack lvt_8_1_ = lvt_7_1_ instanceof LivingEntity ? ((LivingEntity)lvt_7_1_).getHeldItemMainhand() : ItemStack.EMPTY;
                  if (!lvt_8_1_.isEmpty() && lvt_8_1_.hasDisplayName()) {
                     lvt_3_7_ = new TranslationTextComponent("death.fell.assist.item", new Object[]{this.fighter.getDisplayName(), lvt_6_1_, lvt_8_1_.getTextComponent()});
                  } else {
                     lvt_3_7_ = new TranslationTextComponent("death.fell.assist", new Object[]{this.fighter.getDisplayName(), lvt_6_1_});
                  }
               }
            } else {
               lvt_3_7_ = new TranslationTextComponent("death.fell.accident." + this.getFallSuffix(lvt_1_1_), new Object[]{this.fighter.getDisplayName()});
            }
         } else {
            lvt_3_7_ = lvt_2_1_.getDamageSrc().getDeathMessage(this.fighter);
         }

         return (ITextComponent)lvt_3_7_;
      }
   }

   @Nullable
   public LivingEntity getBestAttacker() {
      LivingEntity lvt_1_1_ = null;
      PlayerEntity lvt_2_1_ = null;
      float lvt_3_1_ = 0.0F;
      float lvt_4_1_ = 0.0F;
      Iterator var5 = this.combatEntries.iterator();

      while(true) {
         CombatEntry lvt_6_1_;
         do {
            do {
               if (!var5.hasNext()) {
                  if (lvt_2_1_ != null && lvt_4_1_ >= lvt_3_1_ / 3.0F) {
                     return lvt_2_1_;
                  }

                  return lvt_1_1_;
               }

               lvt_6_1_ = (CombatEntry)var5.next();
               if (lvt_6_1_.getDamageSrc().getTrueSource() instanceof PlayerEntity && (lvt_2_1_ == null || lvt_6_1_.getDamage() > lvt_4_1_)) {
                  lvt_4_1_ = lvt_6_1_.getDamage();
                  lvt_2_1_ = (PlayerEntity)lvt_6_1_.getDamageSrc().getTrueSource();
               }
            } while(!(lvt_6_1_.getDamageSrc().getTrueSource() instanceof LivingEntity));
         } while(lvt_1_1_ != null && lvt_6_1_.getDamage() <= lvt_3_1_);

         lvt_3_1_ = lvt_6_1_.getDamage();
         lvt_1_1_ = (LivingEntity)lvt_6_1_.getDamageSrc().getTrueSource();
      }
   }

   @Nullable
   private CombatEntry getBestCombatEntry() {
      CombatEntry lvt_1_1_ = null;
      CombatEntry lvt_2_1_ = null;
      float lvt_3_1_ = 0.0F;
      float lvt_4_1_ = 0.0F;

      for(int lvt_5_1_ = 0; lvt_5_1_ < this.combatEntries.size(); ++lvt_5_1_) {
         CombatEntry lvt_6_1_ = (CombatEntry)this.combatEntries.get(lvt_5_1_);
         CombatEntry lvt_7_1_ = lvt_5_1_ > 0 ? (CombatEntry)this.combatEntries.get(lvt_5_1_ - 1) : null;
         if ((lvt_6_1_.getDamageSrc() == DamageSource.FALL || lvt_6_1_.getDamageSrc() == DamageSource.OUT_OF_WORLD) && lvt_6_1_.getDamageAmount() > 0.0F && (lvt_1_1_ == null || lvt_6_1_.getDamageAmount() > lvt_4_1_)) {
            if (lvt_5_1_ > 0) {
               lvt_1_1_ = lvt_7_1_;
            } else {
               lvt_1_1_ = lvt_6_1_;
            }

            lvt_4_1_ = lvt_6_1_.getDamageAmount();
         }

         if (lvt_6_1_.getFallSuffix() != null && (lvt_2_1_ == null || lvt_6_1_.getDamage() > lvt_3_1_)) {
            lvt_2_1_ = lvt_6_1_;
            lvt_3_1_ = lvt_6_1_.getDamage();
         }
      }

      if (lvt_4_1_ > 5.0F && lvt_1_1_ != null) {
         return lvt_1_1_;
      } else if (lvt_3_1_ > 5.0F && lvt_2_1_ != null) {
         return lvt_2_1_;
      } else {
         return null;
      }
   }

   private String getFallSuffix(CombatEntry p_94548_1_) {
      return p_94548_1_.getFallSuffix() == null ? "generic" : p_94548_1_.getFallSuffix();
   }

   public int getCombatDuration() {
      return this.inCombat ? this.fighter.ticksExisted - this.combatStartTime : this.combatEndTime - this.combatStartTime;
   }

   private void resetFallSuffix() {
      this.fallSuffix = null;
   }

   public void reset() {
      int lvt_1_1_ = this.inCombat ? 300 : 100;
      if (this.takingDamage && (!this.fighter.isAlive() || this.fighter.ticksExisted - this.lastDamageTime > lvt_1_1_)) {
         boolean lvt_2_1_ = this.inCombat;
         this.takingDamage = false;
         this.inCombat = false;
         this.combatEndTime = this.fighter.ticksExisted;
         if (lvt_2_1_) {
            this.fighter.sendEndCombat();
         }

         this.combatEntries.clear();
      }

   }

   public LivingEntity getFighter() {
      return this.fighter;
   }
}
