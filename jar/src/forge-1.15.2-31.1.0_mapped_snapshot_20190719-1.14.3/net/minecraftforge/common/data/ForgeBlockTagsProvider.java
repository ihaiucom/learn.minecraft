package net.minecraftforge.common.data;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeBlockTagsProvider extends BlockTagsProvider {
   private Set<ResourceLocation> filter = null;

   public ForgeBlockTagsProvider(DataGenerator gen) {
      super(gen);
   }

   public void registerTags() {
      super.registerTags();
      this.filter = (Set)this.tagToBuilder.entrySet().stream().map((e) -> {
         return ((Tag)e.getKey()).getId();
      }).collect(Collectors.toSet());
      this.getBuilder(Tags.Blocks.CHESTS).add(Tags.Blocks.CHESTS_ENDER, Tags.Blocks.CHESTS_TRAPPED, Tags.Blocks.CHESTS_WOODEN);
      this.getBuilder(Tags.Blocks.CHESTS_ENDER).add((Object)Blocks.ENDER_CHEST);
      this.getBuilder(Tags.Blocks.CHESTS_TRAPPED).add((Object)Blocks.TRAPPED_CHEST);
      this.getBuilder(Tags.Blocks.CHESTS_WOODEN).add((Object[])(Blocks.CHEST, Blocks.TRAPPED_CHEST));
      this.getBuilder(Tags.Blocks.COBBLESTONE).add((Object[])(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE, Blocks.MOSSY_COBBLESTONE));
      this.getBuilder(Tags.Blocks.DIRT).add((Object[])(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.MYCELIUM));
      this.getBuilder(Tags.Blocks.END_STONES).add((Object)Blocks.END_STONE);
      this.getBuilder(Tags.Blocks.FENCE_GATES).add(Tags.Blocks.FENCE_GATES_WOODEN);
      this.getBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add((Object[])(Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE));
      this.getBuilder(Tags.Blocks.FENCES).add(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Blocks.FENCES_WOODEN);
      this.getBuilder(Tags.Blocks.FENCES_NETHER_BRICK).add((Object)Blocks.NETHER_BRICK_FENCE);
      this.getBuilder(Tags.Blocks.FENCES_WOODEN).add((Object[])(Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE));
      this.getBuilder(Tags.Blocks.GLASS).add(Tags.Blocks.GLASS_COLORLESS, Tags.Blocks.STAINED_GLASS);
      this.getBuilder(Tags.Blocks.GLASS_COLORLESS).add((Object)Blocks.GLASS);
      this.addColored(this.getBuilder(Tags.Blocks.STAINED_GLASS)::add, Tags.Blocks.GLASS, "{color}_stained_glass");
      this.getBuilder(Tags.Blocks.GLASS_PANES).add(Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Blocks.STAINED_GLASS_PANES);
      this.getBuilder(Tags.Blocks.GLASS_PANES_COLORLESS).add((Object)Blocks.GLASS_PANE);
      this.addColored(this.getBuilder(Tags.Blocks.STAINED_GLASS_PANES)::add, Tags.Blocks.GLASS_PANES, "{color}_stained_glass_pane");
      this.getBuilder(Tags.Blocks.GRAVEL).add((Object)Blocks.GRAVEL);
      this.getBuilder(Tags.Blocks.NETHERRACK).add((Object)Blocks.NETHERRACK);
      this.getBuilder(Tags.Blocks.OBSIDIAN).add((Object)Blocks.OBSIDIAN);
      this.getBuilder(Tags.Blocks.ORES).add(Tags.Blocks.ORES_COAL, Tags.Blocks.ORES_DIAMOND, Tags.Blocks.ORES_EMERALD, Tags.Blocks.ORES_GOLD, Tags.Blocks.ORES_IRON, Tags.Blocks.ORES_LAPIS, Tags.Blocks.ORES_REDSTONE, Tags.Blocks.ORES_QUARTZ);
      this.getBuilder(Tags.Blocks.ORES_COAL).add((Object)Blocks.COAL_ORE);
      this.getBuilder(Tags.Blocks.ORES_DIAMOND).add((Object)Blocks.DIAMOND_ORE);
      this.getBuilder(Tags.Blocks.ORES_EMERALD).add((Object)Blocks.EMERALD_ORE);
      this.getBuilder(Tags.Blocks.ORES_GOLD).add((Object)Blocks.GOLD_ORE);
      this.getBuilder(Tags.Blocks.ORES_IRON).add((Object)Blocks.IRON_ORE);
      this.getBuilder(Tags.Blocks.ORES_LAPIS).add((Object)Blocks.LAPIS_ORE);
      this.getBuilder(Tags.Blocks.ORES_QUARTZ).add((Object)Blocks.NETHER_QUARTZ_ORE);
      this.getBuilder(Tags.Blocks.ORES_REDSTONE).add((Object)Blocks.REDSTONE_ORE);
      this.getBuilder(Tags.Blocks.SAND).add(Tags.Blocks.SAND_COLORLESS, Tags.Blocks.SAND_RED);
      this.getBuilder(Tags.Blocks.SAND_COLORLESS).add((Object)Blocks.SAND);
      this.getBuilder(Tags.Blocks.SAND_RED).add((Object)Blocks.RED_SAND);
      this.getBuilder(Tags.Blocks.SANDSTONE).add((Object[])(Blocks.SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE));
      this.getBuilder(Tags.Blocks.STONE).add((Object[])(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE));
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS).add(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Blocks.STORAGE_BLOCKS_REDSTONE);
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_COAL).add((Object)Blocks.COAL_BLOCK);
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_DIAMOND).add((Object)Blocks.DIAMOND_BLOCK);
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_EMERALD).add((Object)Blocks.EMERALD_BLOCK);
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_GOLD).add((Object)Blocks.GOLD_BLOCK);
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_IRON).add((Object)Blocks.IRON_BLOCK);
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_LAPIS).add((Object)Blocks.LAPIS_BLOCK);
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_QUARTZ).add((Object)Blocks.QUARTZ_BLOCK);
      this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_REDSTONE).add((Object)Blocks.REDSTONE_BLOCK);
      this.getBuilder(Tags.Blocks.SUPPORTS_BEACON).add((Object[])(Blocks.EMERALD_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.IRON_BLOCK));
      this.getBuilder(Tags.Blocks.SUPPORTS_CONDUIT).add((Object[])(Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE));
   }

   private void addColored(Consumer<Block> consumer, Tag<Block> group, String pattern) {
      String prefix = group.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
      DyeColor[] var5 = DyeColor.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         DyeColor color = var5[var7];
         ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.getTranslationKey()));
         Tag<Block> tag = this.getForgeTag(prefix + color.getTranslationKey());
         Block block = (Block)ForgeRegistries.BLOCKS.getValue(key);
         if (block == null || block == Blocks.AIR) {
            throw new IllegalStateException("Unknown vanilla block: " + key.toString());
         }

         this.getBuilder(tag).add((Object)block);
         consumer.accept(block);
      }

   }

   private Tag<Block> getForgeTag(String name) {
      try {
         name = name.toUpperCase(Locale.ENGLISH);
         return (Tag)Tags.Blocks.class.getDeclaredField(name).get((Object)null);
      } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException var3) {
         throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
      }
   }

   protected Path makePath(ResourceLocation id) {
      return this.filter != null && this.filter.contains(id) ? null : super.makePath(id);
   }

   public String getName() {
      return "Forge Block Tags";
   }
}
