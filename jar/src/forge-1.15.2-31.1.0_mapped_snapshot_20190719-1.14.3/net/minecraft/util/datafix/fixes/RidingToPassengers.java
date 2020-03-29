package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class RidingToPassengers extends DataFix {
   public RidingToPassengers(Schema p_i49655_1_, boolean p_i49655_2_) {
      super(p_i49655_1_, p_i49655_2_);
   }

   public TypeRewriteRule makeRule() {
      Schema lvt_1_1_ = this.getInputSchema();
      Schema lvt_2_1_ = this.getOutputSchema();
      Type<?> lvt_3_1_ = lvt_1_1_.getTypeRaw(TypeReferences.ENTITY_TYPE);
      Type<?> lvt_4_1_ = lvt_2_1_.getTypeRaw(TypeReferences.ENTITY_TYPE);
      Type<?> lvt_5_1_ = lvt_1_1_.getTypeRaw(TypeReferences.ENTITY);
      return this.cap(lvt_1_1_, lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_);
   }

   private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule cap(Schema p_206340_1_, Schema p_206340_2_, Type<OldEntityTree> p_206340_3_, Type<NewEntityTree> p_206340_4_, Type<Entity> p_206340_5_) {
      Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> lvt_6_1_ = DSL.named(TypeReferences.ENTITY_TYPE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", p_206340_3_)), p_206340_5_));
      Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> lvt_7_1_ = DSL.named(TypeReferences.ENTITY_TYPE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(p_206340_4_))), p_206340_5_));
      Type<?> lvt_8_1_ = p_206340_1_.getType(TypeReferences.ENTITY_TYPE);
      Type<?> lvt_9_1_ = p_206340_2_.getType(TypeReferences.ENTITY_TYPE);
      if (!Objects.equals(lvt_8_1_, lvt_6_1_)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if (!lvt_9_1_.equals(lvt_7_1_, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> lvt_10_1_ = DSL.typeFinder(lvt_6_1_);
         OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> lvt_11_1_ = DSL.typeFinder(lvt_7_1_);
         OpticFinder<NewEntityTree> lvt_12_1_ = DSL.typeFinder(p_206340_4_);
         Type<?> lvt_13_1_ = p_206340_1_.getType(TypeReferences.PLAYER);
         Type<?> lvt_14_1_ = p_206340_2_.getType(TypeReferences.PLAYER);
         return TypeRewriteRule.seq(this.fixTypeEverywhere("EntityRidingToPassengerFix", lvt_6_1_, lvt_7_1_, (p_209760_5_) -> {
            return (p_208042_6_) -> {
               Optional<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> lvt_7_1_ = Optional.empty();
               Pair lvt_8_1_ = p_208042_6_;

               while(true) {
                  Either<List<NewEntityTree>, Unit> lvt_9_1_ = (Either)DataFixUtils.orElse(lvt_7_1_.map((p_208037_4_) -> {
                     Typed<NewEntityTree> lvt_5_1_ = (Typed)p_206340_4_.pointTyped(p_209760_5_).orElseThrow(() -> {
                        return new IllegalStateException("Could not create new entity tree");
                     });
                     NewEntityTree lvt_6_1_ = lvt_5_1_.set(lvt_11_1_, p_208037_4_).getOptional(lvt_12_1_).orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                     return Either.left(ImmutableList.of(lvt_6_1_));
                  }), Either.right(DSL.unit()));
                  lvt_7_1_ = Optional.of(Pair.of(TypeReferences.ENTITY_TYPE.typeName(), Pair.of(lvt_9_1_, ((Pair)lvt_8_1_.getSecond()).getSecond())));
                  Optional<OldEntityTree> lvt_10_1_x = ((Either)((Pair)lvt_8_1_.getSecond()).getFirst()).left();
                  if (!lvt_10_1_x.isPresent()) {
                     return (Pair)lvt_7_1_.orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                  }

                  lvt_8_1_ = (Pair)(new Typed(p_206340_3_, p_209760_5_, lvt_10_1_x.get())).getOptional(lvt_10_1_).orElseThrow(() -> {
                     return new IllegalStateException("Should always have an entity here");
                  });
               }
            };
         }), this.writeAndRead("player RootVehicle injecter", lvt_13_1_, lvt_14_1_));
      }
   }
}
