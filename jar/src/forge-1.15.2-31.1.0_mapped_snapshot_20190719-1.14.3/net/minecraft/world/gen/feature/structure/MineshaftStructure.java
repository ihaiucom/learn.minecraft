package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class MineshaftStructure extends Structure<MineshaftConfig> {
   public MineshaftStructure(Function<Dynamic<?>, ? extends MineshaftConfig> p_i51478_1_) {
      super(p_i51478_1_);
   }

   public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> p_225558_2_, Random p_225558_3_, int p_225558_4_, int p_225558_5_, Biome p_225558_6_) {
      ((SharedSeedRandom)p_225558_3_).setLargeFeatureSeed(p_225558_2_.getSeed(), p_225558_4_, p_225558_5_);
      if (p_225558_2_.hasStructure(p_225558_6_, this)) {
         MineshaftConfig lvt_7_1_ = (MineshaftConfig)p_225558_2_.getStructureConfig(p_225558_6_, this);
         double lvt_8_1_ = lvt_7_1_.probability;
         return p_225558_3_.nextDouble() < lvt_8_1_;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return MineshaftStructure.Start::new;
   }

   public String getStructureName() {
      return "Mineshaft";
   }

   public int getSize() {
      return 8;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225811_1_, int p_i225811_2_, int p_i225811_3_, MutableBoundingBox p_i225811_4_, int p_i225811_5_, long p_i225811_6_) {
         super(p_i225811_1_, p_i225811_2_, p_i225811_3_, p_i225811_4_, p_i225811_5_, p_i225811_6_);
      }

      public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         MineshaftConfig lvt_6_1_ = (MineshaftConfig)p_214625_1_.getStructureConfig(p_214625_5_, Feature.MINESHAFT);
         MineshaftPieces.Room lvt_7_1_ = new MineshaftPieces.Room(0, this.rand, (p_214625_3_ << 4) + 2, (p_214625_4_ << 4) + 2, lvt_6_1_.type);
         this.components.add(lvt_7_1_);
         lvt_7_1_.buildComponent(lvt_7_1_, this.components, this.rand);
         this.recalculateStructureSize();
         if (lvt_6_1_.type == MineshaftStructure.Type.MESA) {
            int lvt_8_1_ = true;
            int lvt_9_1_ = p_214625_1_.getSeaLevel() - this.bounds.maxY + this.bounds.getYSize() / 2 - -5;
            this.bounds.offset(0, lvt_9_1_, 0);
            Iterator var10 = this.components.iterator();

            while(var10.hasNext()) {
               StructurePiece lvt_11_1_ = (StructurePiece)var10.next();
               lvt_11_1_.offset(0, lvt_9_1_, 0);
            }
         } else {
            this.func_214628_a(p_214625_1_.getSeaLevel(), this.rand, 10);
         }

      }
   }

   public static enum Type {
      NORMAL("normal"),
      MESA("mesa");

      private static final Map<String, MineshaftStructure.Type> field_214717_c = (Map)Arrays.stream(values()).collect(Collectors.toMap(MineshaftStructure.Type::func_214714_a, (p_214716_0_) -> {
         return p_214716_0_;
      }));
      private final String field_214718_d;

      private Type(String p_i50444_3_) {
         this.field_214718_d = p_i50444_3_;
      }

      public String func_214714_a() {
         return this.field_214718_d;
      }

      public static MineshaftStructure.Type func_214715_a(String p_214715_0_) {
         return (MineshaftStructure.Type)field_214717_c.get(p_214715_0_);
      }

      public static MineshaftStructure.Type byId(int p_189910_0_) {
         return p_189910_0_ >= 0 && p_189910_0_ < values().length ? values()[p_189910_0_] : NORMAL;
      }
   }
}
