package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FortressStructure extends Structure<NoFeatureConfig> {
   private static final List<Biome.SpawnListEntry> field_202381_d;

   public FortressStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51476_1_) {
      super(p_i51476_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      int lvt_7_1_ = p_225558_4_ >> 4;
      int lvt_8_1_ = p_225558_5_ >> 4;
      p_225558_3_.setSeed((long)(lvt_7_1_ ^ lvt_8_1_ << 4) ^ p_225558_2_.getSeed());
      p_225558_3_.nextInt();
      if (p_225558_3_.nextInt(3) != 0) {
         return false;
      } else if (p_225558_4_ != (lvt_7_1_ << 4) + 4 + p_225558_3_.nextInt(8)) {
         return false;
      } else {
         return p_225558_5_ != (lvt_8_1_ << 4) + 4 + p_225558_3_.nextInt(8) ? false : p_225558_2_.hasStructure(p_225558_6_, this);
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return FortressStructure.Start::new;
   }

   public String getStructureName() {
      return "Fortress";
   }

   public int getSize() {
      return 8;
   }

   public List<Biome.SpawnListEntry> getSpawnList() {
      return field_202381_d;
   }

   static {
      field_202381_d = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnListEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnListEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnListEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 3, 4, 4)});
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225812_1_, int p_i225812_2_, int p_i225812_3_, MutableBoundingBox p_i225812_4_, int p_i225812_5_, long p_i225812_6_) {
         super(p_i225812_1_, p_i225812_2_, p_i225812_3_, p_i225812_4_, p_i225812_5_, p_i225812_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         FortressPieces.Start lvt_6_1_ = new FortressPieces.Start(this.rand, (p_214625_3_ << 4) + 2, (p_214625_4_ << 4) + 2);
         this.components.add(lvt_6_1_);
         lvt_6_1_.buildComponent(lvt_6_1_, this.components, this.rand);
         List lvt_7_1_ = lvt_6_1_.pendingChildren;

         while(!lvt_7_1_.isEmpty()) {
            int lvt_8_1_ = this.rand.nextInt(lvt_7_1_.size());
            StructurePiece lvt_9_1_ = (StructurePiece)lvt_7_1_.remove(lvt_8_1_);
            lvt_9_1_.buildComponent(lvt_6_1_, this.components, this.rand);
         }

         this.recalculateStructureSize();
         this.func_214626_a(this.rand, 48, 70);
      }
   }
}
