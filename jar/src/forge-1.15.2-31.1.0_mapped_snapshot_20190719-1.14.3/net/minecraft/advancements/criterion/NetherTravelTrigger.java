package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class NetherTravelTrigger extends AbstractCriterionTrigger<NetherTravelTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("nether_travel");

   public ResourceLocation getId() {
      return ID;
   }

   public NetherTravelTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      LocationPredicate lvt_3_1_ = LocationPredicate.deserialize(p_192166_1_.get("entered"));
      LocationPredicate lvt_4_1_ = LocationPredicate.deserialize(p_192166_1_.get("exited"));
      DistancePredicate lvt_5_1_ = DistancePredicate.deserialize(p_192166_1_.get("distance"));
      return new NetherTravelTrigger.Instance(lvt_3_1_, lvt_4_1_, lvt_5_1_);
   }

   public void trigger(ServerPlayerEntity p_193168_1_, Vec3d p_193168_2_) {
      this.func_227070_a_(p_193168_1_.getAdvancements(), (p_226945_2_) -> {
         return p_226945_2_.test(p_193168_1_.getServerWorld(), p_193168_2_, p_193168_1_.func_226277_ct_(), p_193168_1_.func_226278_cu_(), p_193168_1_.func_226281_cx_());
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final LocationPredicate entered;
      private final LocationPredicate exited;
      private final DistancePredicate distance;

      public Instance(LocationPredicate p_i47574_1_, LocationPredicate p_i47574_2_, DistancePredicate p_i47574_3_) {
         super(NetherTravelTrigger.ID);
         this.entered = p_i47574_1_;
         this.exited = p_i47574_2_;
         this.distance = p_i47574_3_;
      }

      public static NetherTravelTrigger.Instance forDistance(DistancePredicate p_203933_0_) {
         return new NetherTravelTrigger.Instance(LocationPredicate.ANY, LocationPredicate.ANY, p_203933_0_);
      }

      public boolean test(ServerWorld p_193206_1_, Vec3d p_193206_2_, double p_193206_3_, double p_193206_5_, double p_193206_7_) {
         if (!this.entered.test(p_193206_1_, p_193206_2_.x, p_193206_2_.y, p_193206_2_.z)) {
            return false;
         } else if (!this.exited.test(p_193206_1_, p_193206_3_, p_193206_5_, p_193206_7_)) {
            return false;
         } else {
            return this.distance.test(p_193206_2_.x, p_193206_2_.y, p_193206_2_.z, p_193206_3_, p_193206_5_, p_193206_7_);
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("entered", this.entered.serialize());
         lvt_1_1_.add("exited", this.exited.serialize());
         lvt_1_1_.add("distance", this.distance.serialize());
         return lvt_1_1_;
      }
   }
}
