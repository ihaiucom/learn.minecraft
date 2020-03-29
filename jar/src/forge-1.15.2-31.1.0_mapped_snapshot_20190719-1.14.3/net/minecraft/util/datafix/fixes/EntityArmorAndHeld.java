package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class EntityArmorAndHeld extends DataFix {
   public EntityArmorAndHeld(Schema p_i49667_1_, boolean p_i49667_2_) {
      super(p_i49667_1_, p_i49667_2_);
   }

   public TypeRewriteRule makeRule() {
      return this.cap(this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK));
   }

   private <IS> TypeRewriteRule cap(Type<IS> p_206323_1_) {
      Type<Pair<Either<List<IS>, Unit>, Dynamic<?>>> lvt_2_1_ = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(p_206323_1_))), DSL.remainderType());
      Type<Pair<Either<List<IS>, Unit>, Pair<Either<List<IS>, Unit>, Dynamic<?>>>> lvt_3_1_ = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(p_206323_1_))), DSL.optional(DSL.field("HandItems", DSL.list(p_206323_1_))), DSL.remainderType());
      OpticFinder<Pair<Either<List<IS>, Unit>, Dynamic<?>>> lvt_4_1_ = DSL.typeFinder(lvt_2_1_);
      OpticFinder<List<IS>> lvt_5_1_ = DSL.fieldFinder("Equipment", DSL.list(p_206323_1_));
      return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(TypeReferences.ENTITY), this.getOutputSchema().getType(TypeReferences.ENTITY), (p_207448_4_) -> {
         Either<List<IS>, Unit> lvt_5_1_x = Either.right(DSL.unit());
         Either<List<IS>, Unit> lvt_6_1_ = Either.right(DSL.unit());
         Dynamic<?> lvt_7_1_ = (Dynamic)p_207448_4_.getOrCreate(DSL.remainderFinder());
         Optional<List<IS>> lvt_8_1_ = p_207448_4_.getOptional(lvt_5_1_);
         if (lvt_8_1_.isPresent()) {
            List<IS> lvt_9_1_ = (List)lvt_8_1_.get();
            IS lvt_10_1_ = ((Optional)p_206323_1_.read(lvt_7_1_.emptyMap()).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not parse newly created empty itemstack.");
            });
            if (!lvt_9_1_.isEmpty()) {
               lvt_5_1_x = Either.left(Lists.newArrayList(new Object[]{lvt_9_1_.get(0), lvt_10_1_}));
            }

            if (lvt_9_1_.size() > 1) {
               List<IS> lvt_11_1_ = Lists.newArrayList(new Object[]{lvt_10_1_, lvt_10_1_, lvt_10_1_, lvt_10_1_});

               for(int lvt_12_1_ = 1; lvt_12_1_ < Math.min(lvt_9_1_.size(), 5); ++lvt_12_1_) {
                  lvt_11_1_.set(lvt_12_1_ - 1, lvt_9_1_.get(lvt_12_1_));
               }

               lvt_6_1_ = Either.left(lvt_11_1_);
            }
         }

         Optional<? extends Stream<? extends Dynamic<?>>> lvt_10_2_ = lvt_7_1_.get("DropChances").asStreamOpt();
         if (lvt_10_2_.isPresent()) {
            Iterator<? extends Dynamic<?>> lvt_11_2_ = Stream.concat((Stream)lvt_10_2_.get(), Stream.generate(() -> {
               return lvt_7_1_.createInt(0);
            })).iterator();
            float lvt_12_2_ = ((Dynamic)lvt_11_2_.next()).asFloat(0.0F);
            Dynamic lvt_13_2_;
            if (!lvt_7_1_.get("HandDropChances").get().isPresent()) {
               lvt_13_2_ = lvt_7_1_.emptyMap().merge(lvt_7_1_.createFloat(lvt_12_2_)).merge(lvt_7_1_.createFloat(0.0F));
               lvt_7_1_ = lvt_7_1_.set("HandDropChances", lvt_13_2_);
            }

            if (!lvt_7_1_.get("ArmorDropChances").get().isPresent()) {
               lvt_13_2_ = lvt_7_1_.emptyMap().merge(lvt_7_1_.createFloat(((Dynamic)lvt_11_2_.next()).asFloat(0.0F))).merge(lvt_7_1_.createFloat(((Dynamic)lvt_11_2_.next()).asFloat(0.0F))).merge(lvt_7_1_.createFloat(((Dynamic)lvt_11_2_.next()).asFloat(0.0F))).merge(lvt_7_1_.createFloat(((Dynamic)lvt_11_2_.next()).asFloat(0.0F)));
               lvt_7_1_ = lvt_7_1_.set("ArmorDropChances", lvt_13_2_);
            }

            lvt_7_1_ = lvt_7_1_.remove("DropChances");
         }

         return p_207448_4_.set(lvt_4_1_, lvt_3_1_, Pair.of(lvt_5_1_x, Pair.of(lvt_6_1_, lvt_7_1_)));
      });
   }
}
