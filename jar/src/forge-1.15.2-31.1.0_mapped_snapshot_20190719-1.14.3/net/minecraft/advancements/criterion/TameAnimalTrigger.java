package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class TameAnimalTrigger extends AbstractCriterionTrigger<TameAnimalTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("tame_animal");

   public ResourceLocation getId() {
      return ID;
   }

   public TameAnimalTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate lvt_3_1_ = EntityPredicate.deserialize(p_192166_1_.get("entity"));
      return new TameAnimalTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_193178_1_, AnimalEntity p_193178_2_) {
      this.func_227070_a_(p_193178_1_.getAdvancements(), (p_227251_2_) -> {
         return p_227251_2_.test(p_193178_1_, p_193178_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate entity;

      public Instance(EntityPredicate p_i47513_1_) {
         super(TameAnimalTrigger.ID);
         this.entity = p_i47513_1_;
      }

      public static TameAnimalTrigger.Instance any() {
         return new TameAnimalTrigger.Instance(EntityPredicate.ANY);
      }

      public static TameAnimalTrigger.Instance func_215124_a(EntityPredicate p_215124_0_) {
         return new TameAnimalTrigger.Instance(p_215124_0_);
      }

      public boolean test(ServerPlayerEntity p_193216_1_, AnimalEntity p_193216_2_) {
         return this.entity.test(p_193216_1_, p_193216_2_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("entity", this.entity.serialize());
         return lvt_1_1_;
      }
   }
}
