package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class ChanneledLightningTrigger extends AbstractCriterionTrigger<ChanneledLightningTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

   public ResourceLocation getId() {
      return ID;
   }

   public ChanneledLightningTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate[] lvt_3_1_ = EntityPredicate.deserializeArray(p_192166_1_.get("victims"));
      return new ChanneledLightningTrigger.Instance(lvt_3_1_);
   }

   public void trigger(ServerPlayerEntity p_204814_1_, Collection<? extends Entity> p_204814_2_) {
      this.func_227070_a_(p_204814_1_.getAdvancements(), (p_226307_2_) -> {
         return p_226307_2_.test(p_204814_1_, p_204814_2_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate[] victims;

      public Instance(EntityPredicate[] p_i48921_1_) {
         super(ChanneledLightningTrigger.ID);
         this.victims = p_i48921_1_;
      }

      public static ChanneledLightningTrigger.Instance channeledLightning(EntityPredicate... p_204824_0_) {
         return new ChanneledLightningTrigger.Instance(p_204824_0_);
      }

      public boolean test(ServerPlayerEntity p_204823_1_, Collection<? extends Entity> p_204823_2_) {
         EntityPredicate[] var3 = this.victims;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EntityPredicate lvt_6_1_ = var3[var5];
            boolean lvt_7_1_ = false;
            Iterator var8 = p_204823_2_.iterator();

            while(var8.hasNext()) {
               Entity lvt_9_1_ = (Entity)var8.next();
               if (lvt_6_1_.test(p_204823_1_, lvt_9_1_)) {
                  lvt_7_1_ = true;
                  break;
               }
            }

            if (!lvt_7_1_) {
               return false;
            }
         }

         return true;
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("victims", EntityPredicate.serializeArray(this.victims));
         return lvt_1_1_;
      }
   }
}
