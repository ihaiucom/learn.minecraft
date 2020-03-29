package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EntityHurtPlayerTrigger extends AbstractCriterionTrigger<EntityHurtPlayerTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("entity_hurt_player");

   public ResourceLocation getId() {
      return ID;
   }

   public EntityHurtPlayerTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DamagePredicate lvt_3_1_ = DamagePredicate.deserialize(p_192166_1_.get("damage"));
      return new EntityHurtPlayerTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_192200_1_, DamageSource p_192200_2_, float p_192200_3_, float p_192200_4_, boolean p_192200_5_) {
      this.func_227070_a_(p_192200_1_.getAdvancements(), (p_226603_5_) -> {
         return p_226603_5_.test(p_192200_1_, p_192200_2_, p_192200_3_, p_192200_4_, p_192200_5_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final DamagePredicate damage;

      public Instance(DamagePredicate p_i47438_1_) {
         super(EntityHurtPlayerTrigger.ID);
         this.damage = p_i47438_1_;
      }

      public static EntityHurtPlayerTrigger.Instance forDamage(DamagePredicate.Builder p_203921_0_) {
         return new EntityHurtPlayerTrigger.Instance(p_203921_0_.build());
      }

      public boolean test(ServerPlayerEntity p_192263_1_, DamageSource p_192263_2_, float p_192263_3_, float p_192263_4_, boolean p_192263_5_) {
         return this.damage.test(p_192263_1_, p_192263_2_, p_192263_3_, p_192263_4_, p_192263_5_);
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("damage", this.damage.serialize());
         return lvt_1_1_;
      }
   }
}
