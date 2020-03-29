package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class TickTrigger extends AbstractCriterionTrigger<TickTrigger.Instance> {
   public static final ResourceLocation ID = new ResourceLocation("tick");

   public ResourceLocation getId() {
      return ID;
   }

   public TickTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return new TickTrigger.Instance();
   }

   public void trigger(ServerPlayerEntity p_193182_1_) {
      this.func_227071_b_(p_193182_1_.getAdvancements());
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      public Instance() {
         super(TickTrigger.ID);
      }
   }
}
