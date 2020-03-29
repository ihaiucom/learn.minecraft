package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class LevitationTrigger extends AbstractCriterionTrigger<LevitationTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("levitation");

   public ResourceLocation getId() {
      return ID;
   }

   public LevitationTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DistancePredicate lvt_3_1_ = DistancePredicate.deserialize(p_192166_1_.get("distance"));
      MinMaxBounds.IntBound lvt_4_1_ = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("duration"));
      return new LevitationTrigger.Instance(lvt_3_1_, lvt_4_1_);
   }

   public void trigger(ServerPlayerEntity p_193162_1_, Vec3d p_193162_2_, int p_193162_3_) {
      this.func_227070_a_(p_193162_1_.getAdvancements(), (p_226852_3_) -> {
         return p_226852_3_.test(p_193162_1_, p_193162_2_, p_193162_3_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final DistancePredicate distance;
      private final MinMaxBounds.IntBound duration;

      public Instance(DistancePredicate p_i49729_1_, MinMaxBounds.IntBound p_i49729_2_) {
         super(LevitationTrigger.ID);
         this.distance = p_i49729_1_;
         this.duration = p_i49729_2_;
      }

      public static LevitationTrigger.Instance forDistance(DistancePredicate p_203930_0_) {
         return new LevitationTrigger.Instance(p_203930_0_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ServerPlayerEntity p_193201_1_, Vec3d p_193201_2_, int p_193201_3_) {
         if (!this.distance.test(p_193201_2_.x, p_193201_2_.y, p_193201_2_.z, p_193201_1_.func_226277_ct_(), p_193201_1_.func_226278_cu_(), p_193201_1_.func_226281_cx_())) {
            return false;
         } else {
            return this.duration.test(p_193201_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("distance", this.distance.serialize());
         lvt_1_1_.add("duration", this.duration.serialize());
         return lvt_1_1_;
      }
   }
}
