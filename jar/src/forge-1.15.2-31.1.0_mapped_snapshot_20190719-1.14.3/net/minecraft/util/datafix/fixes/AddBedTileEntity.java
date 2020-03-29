package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class AddBedTileEntity extends DataFix {
   public AddBedTileEntity(Schema p_i49690_1_, boolean p_i49690_2_) {
      super(p_i49690_1_, p_i49690_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getOutputSchema().getType(TypeReferences.CHUNK);
      Type<?> lvt_2_1_ = lvt_1_1_.findFieldType("Level");
      Type<?> lvt_3_1_ = lvt_2_1_.findFieldType("TileEntities");
      if (!(lvt_3_1_ instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType<?> lvt_4_1_ = (ListType)lvt_3_1_;
         return this.cap(lvt_2_1_, lvt_4_1_);
      }
   }

   private <TE> TypeRewriteRule cap(Type<?> p_206296_1_, ListType<TE> p_206296_2_) {
      Type<TE> lvt_3_1_ = p_206296_2_.getElement();
      OpticFinder<?> lvt_4_1_ = DSL.fieldFinder("Level", p_206296_1_);
      OpticFinder<List<TE>> lvt_5_1_ = DSL.fieldFinder("TileEntities", p_206296_2_);
      int lvt_6_1_ = true;
      return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), (p_212021_0_) -> {
         return (p_209696_0_) -> {
            return p_209696_0_;
         };
      }), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(TypeReferences.CHUNK), (p_207434_3_) -> {
         Typed<?> lvt_4_1_x = p_207434_3_.getTyped(lvt_4_1_);
         Dynamic<?> lvt_5_1_x = (Dynamic)lvt_4_1_x.get(DSL.remainderFinder());
         int lvt_6_1_ = lvt_5_1_x.get("xPos").asInt(0);
         int lvt_7_1_ = lvt_5_1_x.get("zPos").asInt(0);
         List<TE> lvt_8_1_ = Lists.newArrayList((Iterable)lvt_4_1_x.getOrCreate(lvt_5_1_));
         List<? extends Dynamic<?>> lvt_9_1_ = lvt_5_1_x.get("Sections").asList(Function.identity());

         for(int lvt_10_1_ = 0; lvt_10_1_ < lvt_9_1_.size(); ++lvt_10_1_) {
            Dynamic<?> lvt_11_1_ = (Dynamic)lvt_9_1_.get(lvt_10_1_);
            int lvt_12_1_ = lvt_11_1_.get("Y").asInt(0);
            Stream<Integer> lvt_13_1_ = lvt_11_1_.get("Blocks").asStream().map((p_209698_0_) -> {
               return p_209698_0_.asInt(0);
            });
            int lvt_14_1_ = 0;
            lvt_13_1_.getClass();

            for(Iterator var15 = (lvt_13_1_::iterator).iterator(); var15.hasNext(); ++lvt_14_1_) {
               int lvt_16_1_ = (Integer)var15.next();
               if (416 == (lvt_16_1_ & 255) << 4) {
                  int lvt_17_1_ = lvt_14_1_ & 15;
                  int lvt_18_1_ = lvt_14_1_ >> 8 & 15;
                  int lvt_19_1_ = lvt_14_1_ >> 4 & 15;
                  Map<Dynamic<?>, Dynamic<?>> lvt_20_1_ = Maps.newHashMap();
                  lvt_20_1_.put(lvt_11_1_.createString("id"), lvt_11_1_.createString("minecraft:bed"));
                  lvt_20_1_.put(lvt_11_1_.createString("x"), lvt_11_1_.createInt(lvt_17_1_ + (lvt_6_1_ << 4)));
                  lvt_20_1_.put(lvt_11_1_.createString("y"), lvt_11_1_.createInt(lvt_18_1_ + (lvt_12_1_ << 4)));
                  lvt_20_1_.put(lvt_11_1_.createString("z"), lvt_11_1_.createInt(lvt_19_1_ + (lvt_7_1_ << 4)));
                  lvt_20_1_.put(lvt_11_1_.createString("color"), lvt_11_1_.createShort((short)14));
                  lvt_8_1_.add(((Optional)lvt_3_1_.read(lvt_11_1_.createMap(lvt_20_1_)).getSecond()).orElseThrow(() -> {
                     return new IllegalStateException("Could not parse newly created bed block entity.");
                  }));
               }
            }
         }

         if (!lvt_8_1_.isEmpty()) {
            return p_207434_3_.set(lvt_4_1_, lvt_4_1_x.set(lvt_5_1_, lvt_8_1_));
         } else {
            return p_207434_3_;
         }
      }));
   }
}
