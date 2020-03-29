package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class LegacyStructureDataUtil {
   private static final Map<String, String> field_208220_b = (Map)Util.make(Maps.newHashMap(), (p_208213_0_) -> {
      p_208213_0_.put("Village", "Village");
      p_208213_0_.put("Mineshaft", "Mineshaft");
      p_208213_0_.put("Mansion", "Mansion");
      p_208213_0_.put("Igloo", "Temple");
      p_208213_0_.put("Desert_Pyramid", "Temple");
      p_208213_0_.put("Jungle_Pyramid", "Temple");
      p_208213_0_.put("Swamp_Hut", "Temple");
      p_208213_0_.put("Stronghold", "Stronghold");
      p_208213_0_.put("Monument", "Monument");
      p_208213_0_.put("Fortress", "Fortress");
      p_208213_0_.put("EndCity", "EndCity");
   });
   private static final Map<String, String> field_208221_c = (Map)Util.make(Maps.newHashMap(), (p_208215_0_) -> {
      p_208215_0_.put("Iglu", "Igloo");
      p_208215_0_.put("TeDP", "Desert_Pyramid");
      p_208215_0_.put("TeJP", "Jungle_Pyramid");
      p_208215_0_.put("TeSH", "Swamp_Hut");
   });
   private final boolean field_208222_d;
   private final Map<String, Long2ObjectMap<CompoundNBT>> field_208223_e = Maps.newHashMap();
   private final Map<String, StructureIndexesSavedData> field_208224_f = Maps.newHashMap();
   private final List<String> field_215132_f;
   private final List<String> field_215133_g;

   public LegacyStructureDataUtil(@Nullable DimensionSavedDataManager p_i51349_1_, List<String> p_i51349_2_, List<String> p_i51349_3_) {
      this.field_215132_f = p_i51349_2_;
      this.field_215133_g = p_i51349_3_;
      this.func_212184_a(p_i51349_1_);
      boolean lvt_4_1_ = false;

      String lvt_6_1_;
      for(Iterator var5 = this.field_215133_g.iterator(); var5.hasNext(); lvt_4_1_ |= this.field_208223_e.get(lvt_6_1_) != null) {
         lvt_6_1_ = (String)var5.next();
      }

      this.field_208222_d = lvt_4_1_;
   }

   public void func_208216_a(long p_208216_1_) {
      Iterator var3 = this.field_215132_f.iterator();

      while(var3.hasNext()) {
         String lvt_4_1_ = (String)var3.next();
         StructureIndexesSavedData lvt_5_1_ = (StructureIndexesSavedData)this.field_208224_f.get(lvt_4_1_);
         if (lvt_5_1_ != null && lvt_5_1_.func_208023_c(p_208216_1_)) {
            lvt_5_1_.func_201762_c(p_208216_1_);
            lvt_5_1_.markDirty();
         }
      }

   }

   public CompoundNBT func_212181_a(CompoundNBT p_212181_1_) {
      CompoundNBT lvt_2_1_ = p_212181_1_.getCompound("Level");
      ChunkPos lvt_3_1_ = new ChunkPos(lvt_2_1_.getInt("xPos"), lvt_2_1_.getInt("zPos"));
      if (this.func_208209_a(lvt_3_1_.x, lvt_3_1_.z)) {
         p_212181_1_ = this.func_212182_a(p_212181_1_, lvt_3_1_);
      }

      CompoundNBT lvt_4_1_ = lvt_2_1_.getCompound("Structures");
      CompoundNBT lvt_5_1_ = lvt_4_1_.getCompound("References");
      Iterator var6 = this.field_215133_g.iterator();

      while(true) {
         String lvt_7_1_;
         Structure lvt_8_1_;
         do {
            do {
               if (!var6.hasNext()) {
                  lvt_4_1_.put("References", lvt_5_1_);
                  lvt_2_1_.put("Structures", lvt_4_1_);
                  p_212181_1_.put("Level", lvt_2_1_);
                  return p_212181_1_;
               }

               lvt_7_1_ = (String)var6.next();
               lvt_8_1_ = (Structure)Feature.STRUCTURES.get(lvt_7_1_.toLowerCase(Locale.ROOT));
            } while(lvt_5_1_.contains(lvt_7_1_, 12));
         } while(lvt_8_1_ == null);

         int lvt_9_1_ = lvt_8_1_.getSize();
         LongList lvt_10_1_ = new LongArrayList();

         for(int lvt_11_1_ = lvt_3_1_.x - lvt_9_1_; lvt_11_1_ <= lvt_3_1_.x + lvt_9_1_; ++lvt_11_1_) {
            for(int lvt_12_1_ = lvt_3_1_.z - lvt_9_1_; lvt_12_1_ <= lvt_3_1_.z + lvt_9_1_; ++lvt_12_1_) {
               if (this.func_208211_a(lvt_11_1_, lvt_12_1_, lvt_7_1_)) {
                  lvt_10_1_.add(ChunkPos.asLong(lvt_11_1_, lvt_12_1_));
               }
            }
         }

         lvt_5_1_.putLongArray(lvt_7_1_, (List)lvt_10_1_);
      }
   }

   private boolean func_208211_a(int p_208211_1_, int p_208211_2_, String p_208211_3_) {
      if (!this.field_208222_d) {
         return false;
      } else {
         return this.field_208223_e.get(p_208211_3_) != null && ((StructureIndexesSavedData)this.field_208224_f.get(field_208220_b.get(p_208211_3_))).func_208024_b(ChunkPos.asLong(p_208211_1_, p_208211_2_));
      }
   }

   private boolean func_208209_a(int p_208209_1_, int p_208209_2_) {
      if (!this.field_208222_d) {
         return false;
      } else {
         Iterator var3 = this.field_215133_g.iterator();

         String lvt_4_1_;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            lvt_4_1_ = (String)var3.next();
         } while(this.field_208223_e.get(lvt_4_1_) == null || !((StructureIndexesSavedData)this.field_208224_f.get(field_208220_b.get(lvt_4_1_))).func_208023_c(ChunkPos.asLong(p_208209_1_, p_208209_2_)));

         return true;
      }
   }

   private CompoundNBT func_212182_a(CompoundNBT p_212182_1_, ChunkPos p_212182_2_) {
      CompoundNBT lvt_3_1_ = p_212182_1_.getCompound("Level");
      CompoundNBT lvt_4_1_ = lvt_3_1_.getCompound("Structures");
      CompoundNBT lvt_5_1_ = lvt_4_1_.getCompound("Starts");
      Iterator var6 = this.field_215133_g.iterator();

      while(var6.hasNext()) {
         String lvt_7_1_ = (String)var6.next();
         Long2ObjectMap<CompoundNBT> lvt_8_1_ = (Long2ObjectMap)this.field_208223_e.get(lvt_7_1_);
         if (lvt_8_1_ != null) {
            long lvt_9_1_ = p_212182_2_.asLong();
            if (((StructureIndexesSavedData)this.field_208224_f.get(field_208220_b.get(lvt_7_1_))).func_208023_c(lvt_9_1_)) {
               CompoundNBT lvt_11_1_ = (CompoundNBT)lvt_8_1_.get(lvt_9_1_);
               if (lvt_11_1_ != null) {
                  lvt_5_1_.put(lvt_7_1_, lvt_11_1_);
               }
            }
         }
      }

      lvt_4_1_.put("Starts", lvt_5_1_);
      lvt_3_1_.put("Structures", lvt_4_1_);
      p_212182_1_.put("Level", lvt_3_1_);
      return p_212182_1_;
   }

   private void func_212184_a(@Nullable DimensionSavedDataManager p_212184_1_) {
      if (p_212184_1_ != null) {
         Iterator var2 = this.field_215132_f.iterator();

         while(var2.hasNext()) {
            String lvt_3_1_ = (String)var2.next();
            CompoundNBT lvt_4_1_ = new CompoundNBT();

            try {
               lvt_4_1_ = p_212184_1_.load(lvt_3_1_, 1493).getCompound("data").getCompound("Features");
               if (lvt_4_1_.isEmpty()) {
                  continue;
               }
            } catch (IOException var13) {
            }

            Iterator var5 = lvt_4_1_.keySet().iterator();

            while(var5.hasNext()) {
               String lvt_6_1_ = (String)var5.next();
               CompoundNBT lvt_7_1_ = lvt_4_1_.getCompound(lvt_6_1_);
               long lvt_8_1_ = ChunkPos.asLong(lvt_7_1_.getInt("ChunkX"), lvt_7_1_.getInt("ChunkZ"));
               ListNBT lvt_10_1_ = lvt_7_1_.getList("Children", 10);
               String lvt_11_2_;
               if (!lvt_10_1_.isEmpty()) {
                  lvt_11_2_ = lvt_10_1_.getCompound(0).getString("id");
                  String lvt_12_1_ = (String)field_208221_c.get(lvt_11_2_);
                  if (lvt_12_1_ != null) {
                     lvt_7_1_.putString("id", lvt_12_1_);
                  }
               }

               lvt_11_2_ = lvt_7_1_.getString("id");
               ((Long2ObjectMap)this.field_208223_e.computeIfAbsent(lvt_11_2_, (p_208208_0_) -> {
                  return new Long2ObjectOpenHashMap();
               })).put(lvt_8_1_, lvt_7_1_);
            }

            String lvt_5_1_ = lvt_3_1_ + "_index";
            StructureIndexesSavedData lvt_6_2_ = (StructureIndexesSavedData)p_212184_1_.getOrCreate(() -> {
               return new StructureIndexesSavedData(lvt_5_1_);
            }, lvt_5_1_);
            if (!lvt_6_2_.getAll().isEmpty()) {
               this.field_208224_f.put(lvt_3_1_, lvt_6_2_);
            } else {
               StructureIndexesSavedData lvt_7_2_ = new StructureIndexesSavedData(lvt_5_1_);
               this.field_208224_f.put(lvt_3_1_, lvt_7_2_);
               Iterator var17 = lvt_4_1_.keySet().iterator();

               while(var17.hasNext()) {
                  String lvt_9_1_ = (String)var17.next();
                  CompoundNBT lvt_10_2_ = lvt_4_1_.getCompound(lvt_9_1_);
                  lvt_7_2_.func_201763_a(ChunkPos.asLong(lvt_10_2_.getInt("ChunkX"), lvt_10_2_.getInt("ChunkZ")));
               }

               lvt_7_2_.markDirty();
            }
         }

      }
   }

   public static LegacyStructureDataUtil func_215130_a(DimensionType p_215130_0_, @Nullable DimensionSavedDataManager p_215130_1_) {
      if (p_215130_0_ == DimensionType.OVERWORLD) {
         return new LegacyStructureDataUtil(p_215130_1_, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
      } else {
         ImmutableList lvt_2_2_;
         if (p_215130_0_ == DimensionType.THE_NETHER) {
            lvt_2_2_ = ImmutableList.of("Fortress");
            return new LegacyStructureDataUtil(p_215130_1_, lvt_2_2_, lvt_2_2_);
         } else if (p_215130_0_ == DimensionType.THE_END) {
            lvt_2_2_ = ImmutableList.of("EndCity");
            return new LegacyStructureDataUtil(p_215130_1_, lvt_2_2_, lvt_2_2_);
         } else {
            throw new RuntimeException(String.format("Unknown dimension type : %s", p_215130_0_));
         }
      }
   }
}
