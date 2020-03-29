package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkGenStatus extends DataFix {
   public ChunkGenStatus(Schema p_i49674_1_, boolean p_i49674_2_) {
      super(p_i49674_1_, p_i49674_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> lvt_2_1_ = this.getOutputSchema().getType(TypeReferences.CHUNK);
      Type<?> lvt_3_1_ = lvt_1_1_.findFieldType("Level");
      Type<?> lvt_4_1_ = lvt_2_1_.findFieldType("Level");
      Type<?> lvt_5_1_ = lvt_3_1_.findFieldType("TileTicks");
      OpticFinder<?> lvt_6_1_ = DSL.fieldFinder("Level", lvt_3_1_);
      OpticFinder<?> lvt_7_1_ = DSL.fieldFinder("TileTicks", lvt_5_1_);
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", lvt_1_1_, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_209732_3_) -> {
         return p_209732_3_.updateTyped(lvt_6_1_, lvt_4_1_, (p_207915_2_) -> {
            Optional<? extends Stream<? extends Dynamic<?>>> lvt_3_1_ = p_207915_2_.getOptionalTyped(lvt_7_1_).map(Typed::write).flatMap(Dynamic::asStreamOpt);
            Dynamic<?> lvt_4_1_x = (Dynamic)p_207915_2_.get(DSL.remainderFinder());
            boolean lvt_5_1_ = lvt_4_1_x.get("TerrainPopulated").asBoolean(false) && (!lvt_4_1_x.get("LightPopulated").asNumber().isPresent() || lvt_4_1_x.get("LightPopulated").asBoolean(false));
            lvt_4_1_x = lvt_4_1_x.set("Status", lvt_4_1_x.createString(lvt_5_1_ ? "mobs_spawned" : "empty"));
            lvt_4_1_x = lvt_4_1_x.set("hasLegacyStructureData", lvt_4_1_x.createBoolean(true));
            Dynamic lvt_6_2_;
            if (lvt_5_1_) {
               Optional<ByteBuffer> lvt_7_1_x = lvt_4_1_x.get("Biomes").asByteBufferOpt();
               if (lvt_7_1_x.isPresent()) {
                  ByteBuffer lvt_8_1_ = (ByteBuffer)lvt_7_1_x.get();
                  int[] lvt_9_1_ = new int[256];

                  for(int lvt_10_1_ = 0; lvt_10_1_ < lvt_9_1_.length; ++lvt_10_1_) {
                     if (lvt_10_1_ < lvt_8_1_.capacity()) {
                        lvt_9_1_[lvt_10_1_] = lvt_8_1_.get(lvt_10_1_) & 255;
                     }
                  }

                  lvt_4_1_x = lvt_4_1_x.set("Biomes", lvt_4_1_x.createIntList(Arrays.stream(lvt_9_1_)));
               }

               List<Dynamic<?>> lvt_9_2_ = (List)IntStream.range(0, 16).mapToObj((p_211428_1_) -> {
                  return lvt_4_1_x.createList(Stream.empty());
               }).collect(Collectors.toList());
               if (lvt_3_1_.isPresent()) {
                  ((Stream)lvt_3_1_.get()).forEach((p_211426_2_) -> {
                     int lvt_3_1_ = p_211426_2_.get("x").asInt(0);
                     int lvt_4_1_ = p_211426_2_.get("y").asInt(0);
                     int lvt_5_1_ = p_211426_2_.get("z").asInt(0);
                     short lvt_6_1_ = packOffsetCoordinates(lvt_3_1_, lvt_4_1_, lvt_5_1_);
                     lvt_9_2_.set(lvt_4_1_ >> 4, ((Dynamic)lvt_9_2_.get(lvt_4_1_ >> 4)).merge(lvt_4_1_x.createShort(lvt_6_1_)));
                  });
                  lvt_4_1_x = lvt_4_1_x.set("ToBeTicked", lvt_4_1_x.createList(lvt_9_2_.stream()));
               }

               lvt_6_2_ = p_207915_2_.set(DSL.remainderFinder(), lvt_4_1_x).write();
            } else {
               lvt_6_2_ = lvt_4_1_x;
            }

            return (Typed)((Optional)lvt_4_1_.readTyped(lvt_6_2_).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not read the new chunk");
            });
         });
      }), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE)));
   }

   private static short packOffsetCoordinates(int p_210975_0_, int p_210975_1_, int p_210975_2_) {
      return (short)(p_210975_0_ & 15 | (p_210975_1_ & 15) << 4 | (p_210975_2_ & 15) << 8);
   }
}
