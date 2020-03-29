package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class KilledByCrossbowTrigger extends AbstractCriterionTrigger<KilledByCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public KilledByCrossbowTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate[] lvt_3_1_ = EntityPredicate.deserializeArray(p_192166_1_.get("victims"));
      MinMaxBounds.IntBound lvt_4_1_ = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.Instance(lvt_3_1_, lvt_4_1_);
   }

   public void func_215105_a(ServerPlayerEntity p_215105_1_, Collection<Entity> p_215105_2_, int p_215105_3_) {
      this.func_227070_a_(p_215105_1_.getAdvancements(), (p_226842_3_) -> {
         return p_226842_3_.func_215115_a(p_215105_1_, p_215105_2_, p_215105_3_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate[] field_215118_a;
      private final MinMaxBounds.IntBound field_215119_b;

      public Instance(EntityPredicate[] p_i50580_1_, MinMaxBounds.IntBound p_i50580_2_) {
         super(KilledByCrossbowTrigger.ID);
         this.field_215118_a = p_i50580_1_;
         this.field_215119_b = p_i50580_2_;
      }

      public static KilledByCrossbowTrigger.Instance func_215116_a(EntityPredicate.Builder... p_215116_0_) {
         EntityPredicate[] lvt_1_1_ = new EntityPredicate[p_215116_0_.length];

         for(int lvt_2_1_ = 0; lvt_2_1_ < p_215116_0_.length; ++lvt_2_1_) {
            EntityPredicate.Builder lvt_3_1_ = p_215116_0_[lvt_2_1_];
            lvt_1_1_[lvt_2_1_] = lvt_3_1_.build();
         }

         return new KilledByCrossbowTrigger.Instance(lvt_1_1_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public static KilledByCrossbowTrigger.Instance func_215117_a(MinMaxBounds.IntBound p_215117_0_) {
         EntityPredicate[] lvt_1_1_ = new EntityPredicate[0];
         return new KilledByCrossbowTrigger.Instance(lvt_1_1_, p_215117_0_);
      }

      public boolean func_215115_a(ServerPlayerEntity p_215115_1_, Collection<Entity> p_215115_2_, int p_215115_3_) {
         if (this.field_215118_a.length > 0) {
            List<Entity> lvt_4_1_ = Lists.newArrayList(p_215115_2_);
            EntityPredicate[] var5 = this.field_215118_a;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               EntityPredicate lvt_8_1_ = var5[var7];
               boolean lvt_9_1_ = false;
               Iterator lvt_10_1_ = lvt_4_1_.iterator();

               while(lvt_10_1_.hasNext()) {
                  Entity lvt_11_1_ = (Entity)lvt_10_1_.next();
                  if (lvt_8_1_.test(p_215115_1_, lvt_11_1_)) {
                     lvt_10_1_.remove();
                     lvt_9_1_ = true;
                     break;
                  }
               }

               if (!lvt_9_1_) {
                  return false;
               }
            }
         }

         if (this.field_215119_b == MinMaxBounds.IntBound.UNBOUNDED) {
            return true;
         } else {
            Set<EntityType<?>> lvt_4_2_ = Sets.newHashSet();
            Iterator var13 = p_215115_2_.iterator();

            while(var13.hasNext()) {
               Entity lvt_6_1_ = (Entity)var13.next();
               lvt_4_2_.add(lvt_6_1_.getType());
            }

            return this.field_215119_b.test(lvt_4_2_.size()) && this.field_215119_b.test(p_215115_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("victims", EntityPredicate.serializeArray(this.field_215118_a));
         lvt_1_1_.add("unique_entity_types", this.field_215119_b.serialize());
         return lvt_1_1_;
      }
   }
}
