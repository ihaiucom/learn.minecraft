package net.minecraft.client.renderer.color;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ShearableDoublePlantBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.IRegistryDelegate;

@OnlyIn(Dist.CLIENT)
public class BlockColors {
   private final Map<IRegistryDelegate<Block>, IBlockColor> colors = new HashMap();
   private final Map<Block, Set<IProperty<?>>> field_225311_b = Maps.newHashMap();

   public static BlockColors init() {
      BlockColors blockcolors = new BlockColors();
      blockcolors.register((p_lambda$init$0_0_, p_lambda$init$0_1_, p_lambda$init$0_2_, p_lambda$init$0_3_) -> {
         return p_lambda$init$0_1_ != null && p_lambda$init$0_2_ != null ? BiomeColors.func_228358_a_(p_lambda$init$0_1_, p_lambda$init$0_0_.get(ShearableDoublePlantBlock.field_208063_b) == DoubleBlockHalf.UPPER ? p_lambda$init$0_2_.down() : p_lambda$init$0_2_) : -1;
      }, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.func_225308_a(ShearableDoublePlantBlock.field_208063_b, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
      blockcolors.register((p_lambda$init$1_0_, p_lambda$init$1_1_, p_lambda$init$1_2_, p_lambda$init$1_3_) -> {
         return p_lambda$init$1_1_ != null && p_lambda$init$1_2_ != null ? BiomeColors.func_228358_a_(p_lambda$init$1_1_, p_lambda$init$1_2_) : GrassColors.get(0.5D, 1.0D);
      }, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN);
      blockcolors.register((p_lambda$init$2_0_, p_lambda$init$2_1_, p_lambda$init$2_2_, p_lambda$init$2_3_) -> {
         return FoliageColors.getSpruce();
      }, Blocks.SPRUCE_LEAVES);
      blockcolors.register((p_lambda$init$3_0_, p_lambda$init$3_1_, p_lambda$init$3_2_, p_lambda$init$3_3_) -> {
         return FoliageColors.getBirch();
      }, Blocks.BIRCH_LEAVES);
      blockcolors.register((p_lambda$init$4_0_, p_lambda$init$4_1_, p_lambda$init$4_2_, p_lambda$init$4_3_) -> {
         return p_lambda$init$4_1_ != null && p_lambda$init$4_2_ != null ? BiomeColors.func_228361_b_(p_lambda$init$4_1_, p_lambda$init$4_2_) : FoliageColors.getDefault();
      }, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE);
      blockcolors.register((p_lambda$init$5_0_, p_lambda$init$5_1_, p_lambda$init$5_2_, p_lambda$init$5_3_) -> {
         return p_lambda$init$5_1_ != null && p_lambda$init$5_2_ != null ? BiomeColors.func_228363_c_(p_lambda$init$5_1_, p_lambda$init$5_2_) : -1;
      }, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON);
      blockcolors.register((p_lambda$init$6_0_, p_lambda$init$6_1_, p_lambda$init$6_2_, p_lambda$init$6_3_) -> {
         return RedstoneWireBlock.colorMultiplier((Integer)p_lambda$init$6_0_.get(RedstoneWireBlock.POWER));
      }, Blocks.REDSTONE_WIRE);
      blockcolors.func_225308_a(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
      blockcolors.register((p_lambda$init$7_0_, p_lambda$init$7_1_, p_lambda$init$7_2_, p_lambda$init$7_3_) -> {
         return p_lambda$init$7_1_ != null && p_lambda$init$7_2_ != null ? BiomeColors.func_228358_a_(p_lambda$init$7_1_, p_lambda$init$7_2_) : -1;
      }, Blocks.SUGAR_CANE);
      blockcolors.register((p_lambda$init$8_0_, p_lambda$init$8_1_, p_lambda$init$8_2_, p_lambda$init$8_3_) -> {
         return 14731036;
      }, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      blockcolors.register((p_lambda$init$9_0_, p_lambda$init$9_1_, p_lambda$init$9_2_, p_lambda$init$9_3_) -> {
         int i = (Integer)p_lambda$init$9_0_.get(StemBlock.AGE);
         int j = i * 32;
         int k = 255 - i * 8;
         int l = i * 4;
         return j << 16 | k << 8 | l;
      }, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.func_225308_a(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
      blockcolors.register((p_lambda$init$10_0_, p_lambda$init$10_1_, p_lambda$init$10_2_, p_lambda$init$10_3_) -> {
         return p_lambda$init$10_1_ != null && p_lambda$init$10_2_ != null ? 2129968 : 7455580;
      }, Blocks.LILY_PAD);
      ForgeHooksClient.onBlockColorsInit(blockcolors);
      return blockcolors;
   }

   public int getColorOrMaterialColor(BlockState p_189991_1_, World p_189991_2_, BlockPos p_189991_3_) {
      IBlockColor iblockcolor = (IBlockColor)this.colors.get(p_189991_1_.getBlock().delegate);
      if (iblockcolor != null) {
         return iblockcolor.getColor(p_189991_1_, (ILightReader)null, (BlockPos)null, 0);
      } else {
         MaterialColor materialcolor = p_189991_1_.getMaterialColor(p_189991_2_, p_189991_3_);
         return materialcolor != null ? materialcolor.colorValue : -1;
      }
   }

   public int func_228054_a_(BlockState p_228054_1_, @Nullable ILightReader p_228054_2_, @Nullable BlockPos p_228054_3_, int p_228054_4_) {
      IBlockColor iblockcolor = (IBlockColor)this.colors.get(p_228054_1_.getBlock().delegate);
      return iblockcolor == null ? -1 : iblockcolor.getColor(p_228054_1_, p_228054_2_, p_228054_3_, p_228054_4_);
   }

   public void register(IBlockColor p_186722_1_, Block... p_186722_2_) {
      Block[] var3 = p_186722_2_;
      int var4 = p_186722_2_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Block block = var3[var5];
         this.colors.put(block.delegate, p_186722_1_);
      }

   }

   private void func_225309_a(Set<IProperty<?>> p_225309_1_, Block... p_225309_2_) {
      Block[] var3 = p_225309_2_;
      int var4 = p_225309_2_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Block block = var3[var5];
         this.field_225311_b.put(block, p_225309_1_);
      }

   }

   private void func_225308_a(IProperty<?> p_225308_1_, Block... p_225308_2_) {
      this.func_225309_a(ImmutableSet.of(p_225308_1_), p_225308_2_);
   }

   public Set<IProperty<?>> func_225310_a(Block p_225310_1_) {
      return (Set)this.field_225311_b.getOrDefault(p_225310_1_, ImmutableSet.of());
   }
}
