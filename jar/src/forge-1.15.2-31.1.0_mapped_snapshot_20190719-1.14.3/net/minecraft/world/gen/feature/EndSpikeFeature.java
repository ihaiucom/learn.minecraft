package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndSpikeFeature extends Feature<EndSpikeFeatureConfig> {
   private static final LoadingCache<Long, List<EndSpikeFeature.EndSpike>> field_214555_a;

   public EndSpikeFeature(Function<Dynamic<?>, ? extends EndSpikeFeatureConfig> p_i51432_1_) {
      super(p_i51432_1_);
   }

   public static List<EndSpikeFeature.EndSpike> func_214554_a(IWorld p_214554_0_) {
      Random lvt_1_1_ = new Random(p_214554_0_.getSeed());
      long lvt_2_1_ = lvt_1_1_.nextLong() & 65535L;
      return (List)field_214555_a.getUnchecked(lvt_2_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, EndSpikeFeatureConfig p_212245_5_) {
      List<EndSpikeFeature.EndSpike> lvt_6_1_ = p_212245_5_.func_214671_b();
      if (lvt_6_1_.isEmpty()) {
         lvt_6_1_ = func_214554_a(p_212245_1_);
      }

      Iterator var7 = lvt_6_1_.iterator();

      while(var7.hasNext()) {
         EndSpikeFeature.EndSpike lvt_8_1_ = (EndSpikeFeature.EndSpike)var7.next();
         if (lvt_8_1_.doesStartInChunk(p_212245_4_)) {
            this.func_214553_a(p_212245_1_, p_212245_3_, p_212245_5_, lvt_8_1_);
         }
      }

      return true;
   }

   private void func_214553_a(IWorld p_214553_1_, Random p_214553_2_, EndSpikeFeatureConfig p_214553_3_, EndSpikeFeature.EndSpike p_214553_4_) {
      int lvt_5_1_ = p_214553_4_.getRadius();
      Iterator var6 = BlockPos.getAllInBoxMutable(new BlockPos(p_214553_4_.getCenterX() - lvt_5_1_, 0, p_214553_4_.getCenterZ() - lvt_5_1_), new BlockPos(p_214553_4_.getCenterX() + lvt_5_1_, p_214553_4_.getHeight() + 10, p_214553_4_.getCenterZ() + lvt_5_1_)).iterator();

      while(true) {
         while(var6.hasNext()) {
            BlockPos lvt_7_1_ = (BlockPos)var6.next();
            if (lvt_7_1_.distanceSq((double)p_214553_4_.getCenterX(), (double)lvt_7_1_.getY(), (double)p_214553_4_.getCenterZ(), false) <= (double)(lvt_5_1_ * lvt_5_1_ + 1) && lvt_7_1_.getY() < p_214553_4_.getHeight()) {
               this.setBlockState(p_214553_1_, lvt_7_1_, Blocks.OBSIDIAN.getDefaultState());
            } else if (lvt_7_1_.getY() > 65) {
               this.setBlockState(p_214553_1_, lvt_7_1_, Blocks.AIR.getDefaultState());
            }
         }

         if (p_214553_4_.isGuarded()) {
            int lvt_6_1_ = true;
            int lvt_7_2_ = true;
            int lvt_8_1_ = true;
            BlockPos.Mutable lvt_9_1_ = new BlockPos.Mutable();

            for(int lvt_10_1_ = -2; lvt_10_1_ <= 2; ++lvt_10_1_) {
               for(int lvt_11_1_ = -2; lvt_11_1_ <= 2; ++lvt_11_1_) {
                  for(int lvt_12_1_ = 0; lvt_12_1_ <= 3; ++lvt_12_1_) {
                     boolean lvt_13_1_ = MathHelper.abs(lvt_10_1_) == 2;
                     boolean lvt_14_1_ = MathHelper.abs(lvt_11_1_) == 2;
                     boolean lvt_15_1_ = lvt_12_1_ == 3;
                     if (lvt_13_1_ || lvt_14_1_ || lvt_15_1_) {
                        boolean lvt_16_1_ = lvt_10_1_ == -2 || lvt_10_1_ == 2 || lvt_15_1_;
                        boolean lvt_17_1_ = lvt_11_1_ == -2 || lvt_11_1_ == 2 || lvt_15_1_;
                        BlockState lvt_18_1_ = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, lvt_16_1_ && lvt_11_1_ != -2)).with(PaneBlock.SOUTH, lvt_16_1_ && lvt_11_1_ != 2)).with(PaneBlock.WEST, lvt_17_1_ && lvt_10_1_ != -2)).with(PaneBlock.EAST, lvt_17_1_ && lvt_10_1_ != 2);
                        this.setBlockState(p_214553_1_, lvt_9_1_.setPos(p_214553_4_.getCenterX() + lvt_10_1_, p_214553_4_.getHeight() + lvt_12_1_, p_214553_4_.getCenterZ() + lvt_11_1_), lvt_18_1_);
                     }
                  }
               }
            }
         }

         EnderCrystalEntity lvt_6_2_ = (EnderCrystalEntity)EntityType.END_CRYSTAL.create(p_214553_1_.getWorld());
         lvt_6_2_.setBeamTarget(p_214553_3_.func_214668_c());
         lvt_6_2_.setInvulnerable(p_214553_3_.func_214669_a());
         lvt_6_2_.setLocationAndAngles((double)((float)p_214553_4_.getCenterX() + 0.5F), (double)(p_214553_4_.getHeight() + 1), (double)((float)p_214553_4_.getCenterZ() + 0.5F), p_214553_2_.nextFloat() * 360.0F, 0.0F);
         p_214553_1_.addEntity(lvt_6_2_);
         this.setBlockState(p_214553_1_, new BlockPos(p_214553_4_.getCenterX(), p_214553_4_.getHeight(), p_214553_4_.getCenterZ()), Blocks.BEDROCK.getDefaultState());
         return;
      }
   }

   static {
      field_214555_a = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new EndSpikeFeature.EndSpikeCacheLoader());
   }

   static class EndSpikeCacheLoader extends CacheLoader<Long, List<EndSpikeFeature.EndSpike>> {
      private EndSpikeCacheLoader() {
      }

      public List<EndSpikeFeature.EndSpike> load(Long p_load_1_) {
         List<Integer> lvt_2_1_ = (List)IntStream.range(0, 10).boxed().collect(Collectors.toList());
         Collections.shuffle(lvt_2_1_, new Random(p_load_1_));
         List<EndSpikeFeature.EndSpike> lvt_3_1_ = Lists.newArrayList();

         for(int lvt_4_1_ = 0; lvt_4_1_ < 10; ++lvt_4_1_) {
            int lvt_5_1_ = MathHelper.floor(42.0D * Math.cos(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double)lvt_4_1_)));
            int lvt_6_1_ = MathHelper.floor(42.0D * Math.sin(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double)lvt_4_1_)));
            int lvt_7_1_ = (Integer)lvt_2_1_.get(lvt_4_1_);
            int lvt_8_1_ = 2 + lvt_7_1_ / 3;
            int lvt_9_1_ = 76 + lvt_7_1_ * 3;
            boolean lvt_10_1_ = lvt_7_1_ == 1 || lvt_7_1_ == 2;
            lvt_3_1_.add(new EndSpikeFeature.EndSpike(lvt_5_1_, lvt_6_1_, lvt_8_1_, lvt_9_1_, lvt_10_1_));
         }

         return lvt_3_1_;
      }

      // $FF: synthetic method
      public Object load(Object p_load_1_) throws Exception {
         return this.load((Long)p_load_1_);
      }

      // $FF: synthetic method
      EndSpikeCacheLoader(Object p_i49969_1_) {
         this();
      }
   }

   public static class EndSpike {
      private final int centerX;
      private final int centerZ;
      private final int radius;
      private final int height;
      private final boolean guarded;
      private final AxisAlignedBB topBoundingBox;

      public EndSpike(int p_i47020_1_, int p_i47020_2_, int p_i47020_3_, int p_i47020_4_, boolean p_i47020_5_) {
         this.centerX = p_i47020_1_;
         this.centerZ = p_i47020_2_;
         this.radius = p_i47020_3_;
         this.height = p_i47020_4_;
         this.guarded = p_i47020_5_;
         this.topBoundingBox = new AxisAlignedBB((double)(p_i47020_1_ - p_i47020_3_), 0.0D, (double)(p_i47020_2_ - p_i47020_3_), (double)(p_i47020_1_ + p_i47020_3_), 256.0D, (double)(p_i47020_2_ + p_i47020_3_));
      }

      public boolean doesStartInChunk(BlockPos p_186154_1_) {
         return p_186154_1_.getX() >> 4 == this.centerX >> 4 && p_186154_1_.getZ() >> 4 == this.centerZ >> 4;
      }

      public int getCenterX() {
         return this.centerX;
      }

      public int getCenterZ() {
         return this.centerZ;
      }

      public int getRadius() {
         return this.radius;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean isGuarded() {
         return this.guarded;
      }

      public AxisAlignedBB getTopBoundingBox() {
         return this.topBoundingBox;
      }

      public <T> Dynamic<T> func_214749_a(DynamicOps<T> p_214749_1_) {
         Builder<T, T> lvt_2_1_ = ImmutableMap.builder();
         lvt_2_1_.put(p_214749_1_.createString("centerX"), p_214749_1_.createInt(this.centerX));
         lvt_2_1_.put(p_214749_1_.createString("centerZ"), p_214749_1_.createInt(this.centerZ));
         lvt_2_1_.put(p_214749_1_.createString("radius"), p_214749_1_.createInt(this.radius));
         lvt_2_1_.put(p_214749_1_.createString("height"), p_214749_1_.createInt(this.height));
         lvt_2_1_.put(p_214749_1_.createString("guarded"), p_214749_1_.createBoolean(this.guarded));
         return new Dynamic(p_214749_1_, p_214749_1_.createMap(lvt_2_1_.build()));
      }

      public static <T> EndSpikeFeature.EndSpike func_214747_a(Dynamic<T> p_214747_0_) {
         return new EndSpikeFeature.EndSpike(p_214747_0_.get("centerX").asInt(0), p_214747_0_.get("centerZ").asInt(0), p_214747_0_.get("radius").asInt(0), p_214747_0_.get("height").asInt(0), p_214747_0_.get("guarded").asBoolean(false));
      }
   }
}
