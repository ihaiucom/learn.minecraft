package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class SummonedEntityTrigger extends AbstractCriterionTrigger<SummonedEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("summoned_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public SummonedEntityTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate lvt_3_1_ = EntityPredicate.deserialize(p_192166_1_.get("entity"));
      return new SummonedEntityTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_192229_1_, Entity p_192229_2_) {
      this.func_227070_a_(p_192229_1_.getAdvancements(), (p_227229_2_) -> {
         return p_227229_2_.test(p_192229_1_, p_192229_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate entity;

      public Instance(EntityPredicate p_i47371_1_) {
         super(SummonedEntityTrigger.ID);
         this.entity = p_i47371_1_;
      }

      public static SummonedEntityTrigger.Instance summonedEntity(EntityPredicate.Builder p_203937_0_) {
         return new SummonedEntityTrigger.Instance(p_203937_0_.build());
      }

      public boolean test(ServerPlayerEntity p_192283_1_, Entity p_192283_2_) {
         return this.entity.test(p_192283_1_, p_192283_2_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("entity", this.entity.serialize());
         return lvt_1_1_;
      }
   }
}
