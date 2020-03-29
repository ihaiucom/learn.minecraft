package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CooldownTracker {
   private final Map<Item, CooldownTracker.Cooldown> cooldowns = Maps.newHashMap();
   private int ticks;

   public boolean hasCooldown(Item p_185141_1_) {
      return this.getCooldown(p_185141_1_, 0.0F) > 0.0F;
   }

   public float getCooldown(Item p_185143_1_, float p_185143_2_) {
      CooldownTracker.Cooldown lvt_3_1_ = (CooldownTracker.Cooldown)this.cooldowns.get(p_185143_1_);
      if (lvt_3_1_ != null) {
         float lvt_4_1_ = (float)(lvt_3_1_.expireTicks - lvt_3_1_.createTicks);
         float lvt_5_1_ = (float)lvt_3_1_.expireTicks - ((float)this.ticks + p_185143_2_);
         return MathHelper.clamp(lvt_5_1_ / lvt_4_1_, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void tick() {
      ++this.ticks;
      if (!this.cooldowns.isEmpty()) {
         Iterator lvt_1_1_ = this.cooldowns.entrySet().iterator();

         while(lvt_1_1_.hasNext()) {
            Entry<Item, CooldownTracker.Cooldown> lvt_2_1_ = (Entry)lvt_1_1_.next();
            if (((CooldownTracker.Cooldown)lvt_2_1_.getValue()).expireTicks <= this.ticks) {
               lvt_1_1_.remove();
               this.notifyOnRemove((Item)lvt_2_1_.getKey());
            }
         }
      }

   }

   public void setCooldown(Item p_185145_1_, int p_185145_2_) {
      this.cooldowns.put(p_185145_1_, new CooldownTracker.Cooldown(this.ticks, this.ticks + p_185145_2_));
      this.notifyOnSet(p_185145_1_, p_185145_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public void removeCooldown(Item p_185142_1_) {
      this.cooldowns.remove(p_185142_1_);
      this.notifyOnRemove(p_185142_1_);
   }

   protected void notifyOnSet(Item p_185140_1_, int p_185140_2_) {
   }

   protected void notifyOnRemove(Item p_185146_1_) {
   }

   class Cooldown {
      private final int createTicks;
      private final int expireTicks;

      private Cooldown(int p_i47037_2_, int p_i47037_3_) {
         this.createTicks = p_i47037_2_;
         this.expireTicks = p_i47037_3_;
      }

      // $FF: synthetic method
      Cooldown(int p_i47038_2_, int p_i47038_3_, Object p_i47038_4_) {
         this(p_i47038_2_, p_i47038_3_);
      }
   }
}
