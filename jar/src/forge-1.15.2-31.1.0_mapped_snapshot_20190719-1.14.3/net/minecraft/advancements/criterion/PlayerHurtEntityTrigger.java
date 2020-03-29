package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class PlayerHurtEntityTrigger extends AbstractCriterionTrigger<PlayerHurtEntityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public PlayerHurtEntityTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      DamagePredicate lvt_3_1_ = DamagePredicate.deserialize(p_192166_1_.get("damage"));
      EntityPredicate lvt_4_1_ = EntityPredicate.deserialize(p_192166_1_.get("entity"));
      return new PlayerHurtEntityTrigger.Instance(lvt_3_1_, lvt_4_1_);
   }

   public void trigger(ServerPlayerEntity p_192220_1_, Entity p_192220_2_, DamageSource p_192220_3_, float p_192220_4_, float p_192220_5_, boolean p_192220_6_) {
      this.func_227070_a_(p_192220_1_.getAdvancements(), (p_226956_6_) -> {
         return p_226956_6_.test(p_192220_1_, p_192220_2_, p_192220_3_, p_192220_4_, p_192220_5_, p_192220_6_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final DamagePredicate damage;
      private final EntityPredicate entity;

      public Instance(DamagePredicate p_i47406_1_, EntityPredicate p_i47406_2_) {
         super(PlayerHurtEntityTrigger.ID);
         this.damage = p_i47406_1_;
         this.entity = p_i47406_2_;
      }

      public static PlayerHurtEntityTrigger.Instance forDamage(DamagePredicate.Builder p_203936_0_) {
         return new PlayerHurtEntityTrigger.Instance(p_203936_0_.build(), EntityPredicate.ANY);
      }

      public boolean test(ServerPlayerEntity p_192278_1_, Entity p_192278_2_, DamageSource p_192278_3_, float p_192278_4_, float p_192278_5_, boolean p_192278_6_) {
         if (!this.damage.test(p_192278_1_, p_192278_3_, p_192278_4_, p_192278_5_, p_192278_6_)) {
            return false;
         } else {
            return this.entity.test(p_192278_1_, p_192278_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("damage", this.damage.serialize());
         lvt_1_1_.add("entity", this.entity.serialize());
         return lvt_1_1_;
      }
   }
}
