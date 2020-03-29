package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.BlockStateProviderType;

public class BlockClusterFeatureConfig implements IFeatureConfig {
   public final BlockStateProvider field_227289_a_;
   public final BlockPlacer field_227290_b_;
   public final Set<Block> field_227291_c_;
   public final Set<BlockState> field_227292_d_;
   public final int field_227293_f_;
   public final int field_227294_g_;
   public final int field_227295_h_;
   public final int field_227296_i_;
   public final boolean field_227297_j_;
   public final boolean field_227298_k_;
   public final boolean field_227299_l_;

   private BlockClusterFeatureConfig(BlockStateProvider p_i225836_1_, BlockPlacer p_i225836_2_, Set<Block> p_i225836_3_, Set<BlockState> p_i225836_4_, int p_i225836_5_, int p_i225836_6_, int p_i225836_7_, int p_i225836_8_, boolean p_i225836_9_, boolean p_i225836_10_, boolean p_i225836_11_) {
      this.field_227289_a_ = p_i225836_1_;
      this.field_227290_b_ = p_i225836_2_;
      this.field_227291_c_ = p_i225836_3_;
      this.field_227292_d_ = p_i225836_4_;
      this.field_227293_f_ = p_i225836_5_;
      this.field_227294_g_ = p_i225836_6_;
      this.field_227295_h_ = p_i225836_7_;
      this.field_227296_i_ = p_i225836_8_;
      this.field_227297_j_ = p_i225836_9_;
      this.field_227298_k_ = p_i225836_10_;
      this.field_227299_l_ = p_i225836_11_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      com.google.common.collect.ImmutableMap.Builder<T, T> lvt_2_1_ = ImmutableMap.builder();
      lvt_2_1_.put(p_214634_1_.createString("state_provider"), this.field_227289_a_.serialize(p_214634_1_)).put(p_214634_1_.createString("block_placer"), this.field_227290_b_.serialize(p_214634_1_)).put(p_214634_1_.createString("whitelist"), p_214634_1_.createList(this.field_227291_c_.stream().map((p_227301_1_) -> {
         return BlockState.serialize(p_214634_1_, p_227301_1_.getDefaultState()).getValue();
      }))).put(p_214634_1_.createString("blacklist"), p_214634_1_.createList(this.field_227292_d_.stream().map((p_227302_1_) -> {
         return BlockState.serialize(p_214634_1_, p_227302_1_).getValue();
      }))).put(p_214634_1_.createString("tries"), p_214634_1_.createInt(this.field_227293_f_)).put(p_214634_1_.createString("xspread"), p_214634_1_.createInt(this.field_227294_g_)).put(p_214634_1_.createString("yspread"), p_214634_1_.createInt(this.field_227295_h_)).put(p_214634_1_.createString("zspread"), p_214634_1_.createInt(this.field_227296_i_)).put(p_214634_1_.createString("can_replace"), p_214634_1_.createBoolean(this.field_227297_j_)).put(p_214634_1_.createString("project"), p_214634_1_.createBoolean(this.field_227298_k_)).put(p_214634_1_.createString("need_water"), p_214634_1_.createBoolean(this.field_227299_l_));
      return new Dynamic(p_214634_1_, p_214634_1_.createMap(lvt_2_1_.build()));
   }

   public static <T> BlockClusterFeatureConfig func_227300_a_(Dynamic<T> p_227300_0_) {
      BlockStateProviderType<?> lvt_1_1_ = (BlockStateProviderType)Registry.field_229387_t_.getOrDefault(new ResourceLocation((String)p_227300_0_.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockPlacerType<?> lvt_2_1_ = (BlockPlacerType)Registry.field_229388_u_.getOrDefault(new ResourceLocation((String)p_227300_0_.get("block_placer").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BlockClusterFeatureConfig(lvt_1_1_.func_227399_a_(p_227300_0_.get("state_provider").orElseEmptyMap()), lvt_2_1_.func_227263_a_(p_227300_0_.get("block_placer").orElseEmptyMap()), (Set)p_227300_0_.get("whitelist").asList(BlockState::deserialize).stream().map(BlockState::getBlock).collect(Collectors.toSet()), Sets.newHashSet(p_227300_0_.get("blacklist").asList(BlockState::deserialize)), p_227300_0_.get("tries").asInt(128), p_227300_0_.get("xspread").asInt(7), p_227300_0_.get("yspread").asInt(3), p_227300_0_.get("zspread").asInt(7), p_227300_0_.get("can_replace").asBoolean(false), p_227300_0_.get("project").asBoolean(true), p_227300_0_.get("need_water").asBoolean(false));
   }

   // $FF: synthetic method
   BlockClusterFeatureConfig(BlockStateProvider p_i225837_1_, BlockPlacer p_i225837_2_, Set p_i225837_3_, Set p_i225837_4_, int p_i225837_5_, int p_i225837_6_, int p_i225837_7_, int p_i225837_8_, boolean p_i225837_9_, boolean p_i225837_10_, boolean p_i225837_11_, Object p_i225837_12_) {
      this(p_i225837_1_, p_i225837_2_, p_i225837_3_, p_i225837_4_, p_i225837_5_, p_i225837_6_, p_i225837_7_, p_i225837_8_, p_i225837_9_, p_i225837_10_, p_i225837_11_);
   }

   public static class Builder {
      private final BlockStateProvider field_227303_a_;
      private final BlockPlacer field_227304_b_;
      private Set<Block> field_227305_c_ = ImmutableSet.of();
      private Set<BlockState> field_227306_d_ = ImmutableSet.of();
      private int field_227307_e_ = 64;
      private int field_227308_f_ = 7;
      private int field_227309_g_ = 3;
      private int field_227310_h_ = 7;
      private boolean field_227311_i_;
      private boolean field_227312_j_ = true;
      private boolean field_227313_k_ = false;

      public Builder(BlockStateProvider p_i225838_1_, BlockPlacer p_i225838_2_) {
         this.field_227303_a_ = p_i225838_1_;
         this.field_227304_b_ = p_i225838_2_;
      }

      public BlockClusterFeatureConfig.Builder func_227316_a_(Set<Block> p_227316_1_) {
         this.field_227305_c_ = p_227316_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227319_b_(Set<BlockState> p_227319_1_) {
         this.field_227306_d_ = p_227319_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227315_a_(int p_227315_1_) {
         this.field_227307_e_ = p_227315_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227318_b_(int p_227318_1_) {
         this.field_227308_f_ = p_227318_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227321_c_(int p_227321_1_) {
         this.field_227309_g_ = p_227321_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227323_d_(int p_227323_1_) {
         this.field_227310_h_ = p_227323_1_;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227314_a_() {
         this.field_227311_i_ = true;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227317_b_() {
         this.field_227312_j_ = false;
         return this;
      }

      public BlockClusterFeatureConfig.Builder func_227320_c_() {
         this.field_227313_k_ = true;
         return this;
      }

      public BlockClusterFeatureConfig func_227322_d_() {
         return new BlockClusterFeatureConfig(this.field_227303_a_, this.field_227304_b_, this.field_227305_c_, this.field_227306_d_, this.field_227307_e_, this.field_227308_f_, this.field_227309_g_, this.field_227310_h_, this.field_227311_i_, this.field_227312_j_, this.field_227313_k_);
      }
   }
}
