package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SwampHutStructure extends ScatteredStructure<NoFeatureConfig> {
   private static final List<Biome.SpawnListEntry> field_202384_d;
   private static final List<Biome.SpawnListEntry> field_214559_aS;

   public SwampHutStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51424_1_) {
      super(p_i51424_1_);
   }

   public String getStructureName() {
      return "Swamp_Hut";
   }

   public int getSize() {
      return 3;
   }

   public Structure.IStartFactory getStartFactory() {
      return SwampHutStructure.Start::new;
   }

   protected int getSeedModifier() {
      return 14357620;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return field_202384_d;
   }

   public List<Biome.SpawnListEntry> getCreatureSpawnList() {
      return field_214559_aS;
   }

   public boolean func_202383_b(IWorld p_202383_1_, BlockPos p_202383_2_) {
      StructureStart lvt_3_1_ = this.getStart(p_202383_1_, p_202383_2_, true);
      if (lvt_3_1_ != StructureStart.DUMMY && lvt_3_1_ instanceof SwampHutStructure.Start && !lvt_3_1_.getComponents().isEmpty()) {
         StructurePiece lvt_4_1_ = (StructurePiece)lvt_3_1_.getComponents().get(0);
         return lvt_4_1_ instanceof SwampHutPiece;
      } else {
         return false;
      }
   }

   static {
      field_202384_d = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.WITCH, 1, 1, 1)});
      field_214559_aS = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.CAT, 1, 1, 1)});
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225819_1_, int p_i225819_2_, int p_i225819_3_, MutableBoundingBox p_i225819_4_, int p_i225819_5_, long p_i225819_6_) {
         super(p_i225819_1_, p_i225819_2_, p_i225819_3_, p_i225819_4_, p_i225819_5_, p_i225819_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         SwampHutPiece lvt_6_1_ = new SwampHutPiece(this.rand, p_214625_3_ * 16, p_214625_4_ * 16);
         this.components.add(lvt_6_1_);
         this.recalculateStructureSize();
      }
   }
}
