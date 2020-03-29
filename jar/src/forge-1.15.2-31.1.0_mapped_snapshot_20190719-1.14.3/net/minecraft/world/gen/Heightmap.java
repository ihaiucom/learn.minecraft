package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.BitArray;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Heightmap {
   private static final Predicate<BlockState> field_222691_a = (p_222688_0_) -> {
      return !p_222688_0_.isAir();
   };
   private static final Predicate<BlockState> field_222692_b = (p_222689_0_) -> {
      return p_222689_0_.getMaterial().blocksMovement();
   };
   private final BitArray data = new BitArray(9, 256);
   private final Predicate<BlockState> field_222693_d;
   private final IChunk chunk;

   public Heightmap(IChunk p_i48695_1_, Heightmap.Type p_i48695_2_) {
      this.field_222693_d = p_i48695_2_.func_222684_d();
      this.chunk = p_i48695_1_;
   }

   public static void func_222690_a(IChunk p_222690_0_, Set<Heightmap.Type> p_222690_1_) {
      int lvt_2_1_ = p_222690_1_.size();
      ObjectList<Heightmap> lvt_3_1_ = new ObjectArrayList(lvt_2_1_);
      ObjectListIterator<Heightmap> lvt_4_1_ = lvt_3_1_.iterator();
      int lvt_5_1_ = p_222690_0_.getTopFilledSegment() + 16;
      BlockPos.PooledMutable lvt_6_1_ = BlockPos.PooledMutable.retain();
      Throwable var7 = null;

      try {
         for(int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
            for(int lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_) {
               Iterator var10 = p_222690_1_.iterator();

               while(var10.hasNext()) {
                  Heightmap.Type lvt_11_1_ = (Heightmap.Type)var10.next();
                  lvt_3_1_.add(p_222690_0_.func_217303_b(lvt_11_1_));
               }

               for(int lvt_10_1_ = lvt_5_1_ - 1; lvt_10_1_ >= 0; --lvt_10_1_) {
                  lvt_6_1_.setPos(lvt_8_1_, lvt_10_1_, lvt_9_1_);
                  BlockState lvt_11_2_ = p_222690_0_.getBlockState(lvt_6_1_);
                  if (lvt_11_2_.getBlock() != Blocks.AIR) {
                     while(lvt_4_1_.hasNext()) {
                        Heightmap lvt_12_1_ = (Heightmap)lvt_4_1_.next();
                        if (lvt_12_1_.field_222693_d.test(lvt_11_2_)) {
                           lvt_12_1_.set(lvt_8_1_, lvt_9_1_, lvt_10_1_ + 1);
                           lvt_4_1_.remove();
                        }
                     }

                     if (lvt_3_1_.isEmpty()) {
                        break;
                     }

                     lvt_4_1_.back(lvt_2_1_);
                  }
               }
            }
         }
      } catch (Throwable var20) {
         var7 = var20;
         throw var20;
      } finally {
         if (lvt_6_1_ != null) {
            if (var7 != null) {
               try {
                  lvt_6_1_.close();
               } catch (Throwable var19) {
                  var7.addSuppressed(var19);
               }
            } else {
               lvt_6_1_.close();
            }
         }

      }

   }

   public boolean update(int p_202270_1_, int p_202270_2_, int p_202270_3_, BlockState p_202270_4_) {
      int lvt_5_1_ = this.getHeight(p_202270_1_, p_202270_3_);
      if (p_202270_2_ <= lvt_5_1_ - 2) {
         return false;
      } else {
         if (this.field_222693_d.test(p_202270_4_)) {
            if (p_202270_2_ >= lvt_5_1_) {
               this.set(p_202270_1_, p_202270_3_, p_202270_2_ + 1);
               return true;
            }
         } else if (lvt_5_1_ - 1 == p_202270_2_) {
            BlockPos.Mutable lvt_6_1_ = new BlockPos.Mutable();

            for(int lvt_7_1_ = p_202270_2_ - 1; lvt_7_1_ >= 0; --lvt_7_1_) {
               lvt_6_1_.setPos(p_202270_1_, lvt_7_1_, p_202270_3_);
               if (this.field_222693_d.test(this.chunk.getBlockState(lvt_6_1_))) {
                  this.set(p_202270_1_, p_202270_3_, lvt_7_1_ + 1);
                  return true;
               }
            }

            this.set(p_202270_1_, p_202270_3_, 0);
            return true;
         }

         return false;
      }
   }

   public int getHeight(int p_202273_1_, int p_202273_2_) {
      return this.getHeight(getDataArrayIndex(p_202273_1_, p_202273_2_));
   }

   private int getHeight(int p_202274_1_) {
      return this.data.getAt(p_202274_1_);
   }

   private void set(int p_202272_1_, int p_202272_2_, int p_202272_3_) {
      this.data.setAt(getDataArrayIndex(p_202272_1_, p_202272_2_), p_202272_3_);
   }

   public void setDataArray(long[] p_202268_1_) {
      System.arraycopy(p_202268_1_, 0, this.data.getBackingLongArray(), 0, p_202268_1_.length);
   }

   public long[] getDataArray() {
      return this.data.getBackingLongArray();
   }

   private static int getDataArrayIndex(int p_202267_0_, int p_202267_1_) {
      return p_202267_0_ + p_202267_1_ * 16;
   }

   public static enum Type {
      WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Usage.WORLDGEN, Heightmap.field_222691_a),
      WORLD_SURFACE("WORLD_SURFACE", Heightmap.Usage.CLIENT, Heightmap.field_222691_a),
      OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Usage.WORLDGEN, Heightmap.field_222692_b),
      OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Usage.LIVE_WORLD, Heightmap.field_222692_b),
      MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Usage.CLIENT, (p_222680_0_) -> {
         return p_222680_0_.getMaterial().blocksMovement() || !p_222680_0_.getFluidState().isEmpty();
      }),
      MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Heightmap.Usage.LIVE_WORLD, (p_222682_0_) -> {
         return (p_222682_0_.getMaterial().blocksMovement() || !p_222682_0_.getFluidState().isEmpty()) && !(p_222682_0_.getBlock() instanceof LeavesBlock);
      });

      private final String id;
      private final Heightmap.Usage usage;
      private final Predicate<BlockState> field_222685_i;
      private static final Map<String, Heightmap.Type> field_203503_g = (Map)Util.make(Maps.newHashMap(), (p_222679_0_) -> {
         Heightmap.Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Heightmap.Type lvt_4_1_ = var1[var3];
            p_222679_0_.put(lvt_4_1_.id, lvt_4_1_);
         }

      });

      private Type(String p_i50821_3_, Heightmap.Usage p_i50821_4_, Predicate<BlockState> p_i50821_5_) {
         this.id = p_i50821_3_;
         this.usage = p_i50821_4_;
         this.field_222685_i = p_i50821_5_;
      }

      public String getId() {
         return this.id;
      }

      public boolean func_222681_b() {
         return this.usage == Heightmap.Usage.CLIENT;
      }

      @OnlyIn(Dist.CLIENT)
      public boolean func_222683_c() {
         return this.usage != Heightmap.Usage.WORLDGEN;
      }

      public static Heightmap.Type func_203501_a(String p_203501_0_) {
         return (Heightmap.Type)field_203503_g.get(p_203501_0_);
      }

      public Predicate<BlockState> func_222684_d() {
         return this.field_222685_i;
      }
   }

   public static enum Usage {
      WORLDGEN,
      LIVE_WORLD,
      CLIENT;
   }
}
