package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrappedChestTileEntitySplit extends DataFix {
   private static final Logger LOGGER = LogManager.getLogger();

   public TrappedChestTileEntitySplit(Schema p_i49815_1_, boolean p_i49815_2_) {
      super(p_i49815_1_, p_i49815_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getOutputSchema().getType(TypeReferences.CHUNK);
      Type<?> lvt_2_1_ = lvt_1_1_.findFieldType("Level");
      Type<?> lvt_3_1_ = lvt_2_1_.findFieldType("TileEntities");
      if (!(lvt_3_1_ instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType<?> lvt_4_1_ = (ListType)lvt_3_1_;
         OpticFinder<? extends List<?>> lvt_5_1_ = DSL.fieldFinder("TileEntities", lvt_4_1_);
         Type<?> lvt_6_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
         OpticFinder<?> lvt_7_1_ = lvt_6_1_.findField("Level");
         OpticFinder<?> lvt_8_1_ = lvt_7_1_.type().findField("Sections");
         Type<?> lvt_9_1_ = lvt_8_1_.type();
         if (!(lvt_9_1_ instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
         } else {
            Type<?> lvt_10_1_ = ((ListType)lvt_9_1_).getElement();
            OpticFinder<?> lvt_11_1_ = DSL.typeFinder(lvt_10_1_);
            return TypeRewriteRule.seq((new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", lvt_6_1_, (p_212533_5_) -> {
               return p_212533_5_.updateTyped(lvt_7_1_, (p_212531_4_) -> {
                  Optional<? extends Typed<?>> lvt_5_1_x = p_212531_4_.getOptionalTyped(lvt_8_1_);
                  if (!lvt_5_1_x.isPresent()) {
                     return p_212531_4_;
                  } else {
                     List<? extends Typed<?>> lvt_6_1_ = ((Typed)lvt_5_1_x.get()).getAllTyped(lvt_11_1_);
                     IntSet lvt_7_1_ = new IntOpenHashSet();
                     Iterator var8 = lvt_6_1_.iterator();

                     while(true) {
                        TrappedChestTileEntitySplit.Section lvt_10_1_;
                        do {
                           if (!var8.hasNext()) {
                              Dynamic<?> lvt_8_1_x = (Dynamic)p_212531_4_.get(DSL.remainderFinder());
                              int lvt_9_2_ = lvt_8_1_x.get("xPos").asInt(0);
                              int lvt_10_2_ = lvt_8_1_x.get("zPos").asInt(0);
                              TaggedChoiceType<String> lvt_11_2_ = this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
                              return p_212531_4_.updateTyped(lvt_5_1_, (p_212532_4_) -> {
                                 return p_212532_4_.updateTyped(lvt_11_2_.finder(), (p_212530_4_) -> {
                                    Dynamic<?> lvt_5_1_ = (Dynamic)p_212530_4_.getOrCreate(DSL.remainderFinder());
                                    int lvt_6_1_ = lvt_5_1_.get("x").asInt(0) - (lvt_9_2_ << 4);
                                    int lvt_7_1_x = lvt_5_1_.get("y").asInt(0);
                                    int lvt_8_1_ = lvt_5_1_.get("z").asInt(0) - (lvt_10_2_ << 4);
                                    return lvt_7_1_.contains(LeavesFix.getIndex(lvt_6_1_, lvt_7_1_x, lvt_8_1_)) ? p_212530_4_.update(lvt_11_2_.finder(), (p_212534_0_) -> {
                                       return p_212534_0_.mapFirst((p_212535_0_) -> {
                                          if (!Objects.equals(p_212535_0_, "minecraft:chest")) {
                                             LOGGER.warn("Block Entity was expected to be a chest");
                                          }

                                          return "minecraft:trapped_chest";
                                       });
                                    }) : p_212530_4_;
                                 });
                              });
                           }

                           Typed<?> lvt_9_1_ = (Typed)var8.next();
                           lvt_10_1_ = new TrappedChestTileEntitySplit.Section(lvt_9_1_, this.getInputSchema());
                        } while(lvt_10_1_.isSkippable());

                        for(int lvt_11_1_x = 0; lvt_11_1_x < 4096; ++lvt_11_1_x) {
                           int lvt_12_1_ = lvt_10_1_.getBlock(lvt_11_1_x);
                           if (lvt_10_1_.func_212511_a(lvt_12_1_)) {
                              lvt_7_1_.add(lvt_10_1_.getIndex() << 12 | lvt_11_1_x);
                           }
                        }
                     }
                  }
               });
            }));
         }
      }
   }

   public static final class Section extends LeavesFix.Section {
      @Nullable
      private IntSet field_212512_f;

      public Section(Typed<?> p_i49831_1_, Schema p_i49831_2_) {
         super(p_i49831_1_, p_i49831_2_);
      }

      protected boolean func_212508_a() {
         this.field_212512_f = new IntOpenHashSet();

         for(int lvt_1_1_ = 0; lvt_1_1_ < this.palette.size(); ++lvt_1_1_) {
            Dynamic<?> lvt_2_1_ = (Dynamic)this.palette.get(lvt_1_1_);
            String lvt_3_1_ = lvt_2_1_.get("Name").asString("");
            if (Objects.equals(lvt_3_1_, "minecraft:trapped_chest")) {
               this.field_212512_f.add(lvt_1_1_);
            }
         }

         return this.field_212512_f.isEmpty();
      }

      public boolean func_212511_a(int p_212511_1_) {
         return this.field_212512_f.contains(p_212511_1_);
      }
   }
}
