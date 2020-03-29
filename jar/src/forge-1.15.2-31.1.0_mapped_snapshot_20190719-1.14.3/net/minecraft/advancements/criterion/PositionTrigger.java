package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

public class PositionTrigger extends AbstractCriterionTrigger<PositionTrigger.Instance> {
   private final ResourceLocation id;

   public PositionTrigger(ResourceLocation p_i47432_1_) {
      this.id = p_i47432_1_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public PositionTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      LocationPredicate lvt_3_1_ = LocationPredicate.deserialize(p_192166_1_);
      return new PositionTrigger.Instance(this.id, lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_192215_1_) {
      this.func_227070_a_(p_192215_1_.getAdvancements(), (p_226923_1_) -> {
         return p_226923_1_.test(p_192215_1_.getServerWorld(), p_192215_1_.func_226277_ct_(), p_192215_1_.func_226278_cu_(), p_192215_1_.func_226281_cx_());
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final LocationPredicate location;

      public Instance(ResourceLocation p_i47544_1_, LocationPredicate p_i47544_2_) {
         super(p_i47544_1_);
         this.location = p_i47544_2_;
      }

      public static PositionTrigger.Instance forLocation(LocationPredicate p_203932_0_) {
         return new PositionTrigger.Instance(CriteriaTriggers.LOCATION.id, p_203932_0_);
      }

      public static PositionTrigger.Instance sleptInBed() {
         return new PositionTrigger.Instance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
      }

      public static PositionTrigger.Instance func_215120_d() {
         return new PositionTrigger.Instance(CriteriaTriggers.HERO_OF_THE_VILLAGE.id, LocationPredicate.ANY);
      }

      public boolean test(ServerWorld p_193204_1_, double p_193204_2_, double p_193204_4_, double p_193204_6_) {
         return this.location.test(p_193204_1_, p_193204_2_, p_193204_4_, p_193204_6_);
      }

      public JsonElement serialize() {
         return this.location.serialize();
      }
   }
}
