package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeTrigger extends AbstractCriterionTrigger<UsedEnderEyeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedEnderEyeTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      MinMaxBounds.FloatBound lvt_3_1_ = MinMaxBounds.FloatBound.fromJson(p_192166_1_.get("distance"));
      return new UsedEnderEyeTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_192239_1_, BlockPos p_192239_2_) {
      double lvt_3_1_ = p_192239_1_.func_226277_ct_() - (double)p_192239_2_.getX();
      double lvt_5_1_ = p_192239_1_.func_226281_cx_() - (double)p_192239_2_.getZ();
      double lvt_7_1_ = lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;
      this.func_227070_a_(p_192239_1_.getAdvancements(), (p_227325_2_) -> {
         return p_227325_2_.test(lvt_7_1_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.FloatBound distance;

      public Instance(MinMaxBounds.FloatBound p_i49730_1_) {
         super(UsedEnderEyeTrigger.ID);
         this.distance = p_i49730_1_;
      }

      public boolean test(double p_192288_1_) {
         return this.distance.testSquared(p_192288_1_);
      }
   }
}
