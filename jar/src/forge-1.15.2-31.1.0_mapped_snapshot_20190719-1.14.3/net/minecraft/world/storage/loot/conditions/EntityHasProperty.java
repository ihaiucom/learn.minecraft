package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class EntityHasProperty implements ILootCondition {
   private final EntityPredicate field_216001_a;
   private final LootContext.EntityTarget target;

   private EntityHasProperty(EntityPredicate p_i51196_1_, LootContext.EntityTarget p_i51196_2_) {
      this.field_216001_a = p_i51196_1_;
      this.target = p_i51196_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.POSITION, this.target.getParameter());
   }

   public boolean test(LootContext p_test_1_) {
      Entity lvt_2_1_ = (Entity)p_test_1_.get(this.target.getParameter());
      BlockPos lvt_3_1_ = (BlockPos)p_test_1_.get(LootParameters.POSITION);
      return this.field_216001_a.func_217993_a(p_test_1_.getWorld(), lvt_3_1_ != null ? new Vec3d(lvt_3_1_) : null, lvt_2_1_);
   }

   public static ILootCondition.IBuilder func_215998_a(LootContext.EntityTarget p_215998_0_) {
      return func_215999_a(p_215998_0_, EntityPredicate.Builder.create());
   }

   public static ILootCondition.IBuilder func_215999_a(LootContext.EntityTarget p_215999_0_, EntityPredicate.Builder p_215999_1_) {
      return () -> {
         return new EntityHasProperty(p_215999_1_.build(), p_215999_0_);
      };
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((LootContext)p_test_1_);
   }

   // $FF: synthetic method
   EntityHasProperty(EntityPredicate p_i51197_1_, LootContext.EntityTarget p_i51197_2_, Object p_i51197_3_) {
      this(p_i51197_1_, p_i51197_2_);
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<EntityHasProperty> {
      protected Serializer() {
         super(new ResourceLocation("entity_properties"), EntityHasProperty.class);
      }

      public void serialize(JsonObject p_186605_1_, EntityHasProperty p_186605_2_, JsonSerializationContext p_186605_3_) {
         p_186605_1_.add("predicate", p_186605_2_.field_216001_a.serialize());
         p_186605_1_.add("entity", p_186605_3_.serialize(p_186605_2_.target));
      }

      public EntityHasProperty deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         EntityPredicate lvt_3_1_ = EntityPredicate.deserialize(p_186603_1_.get("predicate"));
         return new EntityHasProperty(lvt_3_1_, (LootContext.EntityTarget)JSONUtils.deserializeClass(p_186603_1_, "entity", p_186603_2_, LootContext.EntityTarget.class));
      }

      // $FF: synthetic method
      public ILootCondition deserialize(JsonObject p_186603_1_, JsonDeserializationContext p_186603_2_) {
         return this.deserialize(p_186603_1_, p_186603_2_);
      }
   }
}
