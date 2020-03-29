package net.minecraftforge.common.data;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeItemTagsProvider extends ItemTagsProvider {
   private Set<ResourceLocation> filter = null;

   public ForgeItemTagsProvider(DataGenerator gen) {
      super(gen);
   }

   public void registerTags() {
      super.registerTags();
      this.filter = (Set)this.tagToBuilder.entrySet().stream().map((e) -> {
         return ((Tag)e.getKey()).getId();
      }).collect(Collectors.toSet());
      this.getBuilder(Tags.Items.ARROWS).add((Object[])(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW));
      this.getBuilder(Tags.Items.BEACON_PAYMENT).add((Object[])(Items.EMERALD, Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT));
      this.getBuilder(Tags.Items.BONES).add((Object)Items.BONE);
      this.getBuilder(Tags.Items.BOOKSHELVES).add((Object)Items.BOOKSHELF);
      this.copy(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
      this.copy(Tags.Blocks.CHESTS_ENDER, Tags.Items.CHESTS_ENDER);
      this.copy(Tags.Blocks.CHESTS_TRAPPED, Tags.Items.CHESTS_TRAPPED);
      this.copy(Tags.Blocks.CHESTS_WOODEN, Tags.Items.CHESTS_WOODEN);
      this.copy(Tags.Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
      this.getBuilder(Tags.Items.CROPS).add(Tags.Items.CROPS_BEETROOT, Tags.Items.CROPS_CARROT, Tags.Items.CROPS_NETHER_WART, Tags.Items.CROPS_POTATO, Tags.Items.CROPS_WHEAT);
      this.getBuilder(Tags.Items.CROPS_BEETROOT).add((Object)Items.BEETROOT);
      this.getBuilder(Tags.Items.CROPS_CARROT).add((Object)Items.CARROT);
      this.getBuilder(Tags.Items.CROPS_NETHER_WART).add((Object)Items.NETHER_WART);
      this.getBuilder(Tags.Items.CROPS_POTATO).add((Object)Items.POTATO);
      this.getBuilder(Tags.Items.CROPS_WHEAT).add((Object)Items.WHEAT);
      this.getBuilder(Tags.Items.DUSTS).add(Tags.Items.DUSTS_GLOWSTONE, Tags.Items.DUSTS_PRISMARINE, Tags.Items.DUSTS_REDSTONE);
      this.getBuilder(Tags.Items.DUSTS_GLOWSTONE).add((Object)Items.GLOWSTONE_DUST);
      this.getBuilder(Tags.Items.DUSTS_PRISMARINE).add((Object)Items.PRISMARINE_SHARD);
      this.getBuilder(Tags.Items.DUSTS_REDSTONE).add((Object)Items.REDSTONE);
      this.addColored(this.getBuilder(Tags.Items.DYES)::add, Tags.Items.DYES, "{color}_dye");
      this.getBuilder(Tags.Items.EGGS).add((Object)Items.EGG);
      this.copy(Tags.Blocks.END_STONES, Tags.Items.END_STONES);
      this.getBuilder(Tags.Items.ENDER_PEARLS).add((Object)Items.ENDER_PEARL);
      this.getBuilder(Tags.Items.FEATHERS).add((Object)Items.FEATHER);
      this.copy(Tags.Blocks.FENCE_GATES, Tags.Items.FENCE_GATES);
      this.copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
      this.copy(Tags.Blocks.FENCES, Tags.Items.FENCES);
      this.copy(Tags.Blocks.FENCES_NETHER_BRICK, Tags.Items.FENCES_NETHER_BRICK);
      this.copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
      this.getBuilder(Tags.Items.GEMS).add(Tags.Items.GEMS_DIAMOND, Tags.Items.GEMS_EMERALD, Tags.Items.GEMS_LAPIS, Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_QUARTZ);
      this.getBuilder(Tags.Items.GEMS_DIAMOND).add((Object)Items.DIAMOND);
      this.getBuilder(Tags.Items.GEMS_EMERALD).add((Object)Items.EMERALD);
      this.getBuilder(Tags.Items.GEMS_LAPIS).add((Object)Items.LAPIS_LAZULI);
      this.getBuilder(Tags.Items.GEMS_PRISMARINE).add((Object)Items.PRISMARINE_CRYSTALS);
      this.getBuilder(Tags.Items.GEMS_QUARTZ).add((Object)Items.QUARTZ);
      this.copy(Tags.Blocks.GLASS, Tags.Items.GLASS);
      this.copyColored(Tags.Blocks.GLASS, Tags.Items.GLASS);
      this.copy(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
      this.copyColored(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);
      this.copy(Tags.Blocks.GRAVEL, Tags.Items.GRAVEL);
      this.getBuilder(Tags.Items.GUNPOWDER).add((Object)Items.GUNPOWDER);
      this.getBuilder(Tags.Items.HEADS).add((Object[])(Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.PLAYER_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.ZOMBIE_HEAD));
      this.getBuilder(Tags.Items.INGOTS).add(Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_BRICK, Tags.Items.INGOTS_NETHER_BRICK);
      this.getBuilder(Tags.Items.INGOTS_BRICK).add((Object)Items.BRICK);
      this.getBuilder(Tags.Items.INGOTS_GOLD).add((Object)Items.GOLD_INGOT);
      this.getBuilder(Tags.Items.INGOTS_IRON).add((Object)Items.IRON_INGOT);
      this.getBuilder(Tags.Items.INGOTS_NETHER_BRICK).add((Object)Items.NETHER_BRICK);
      this.getBuilder(Tags.Items.LEATHER).add((Object)Items.LEATHER);
      this.getBuilder(Tags.Items.MUSHROOMS).add((Object[])(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM));
      this.getBuilder(Tags.Items.MUSIC_DISCS).add((Object[])(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT));
      this.getBuilder(Tags.Items.NETHER_STARS).add((Object)Items.NETHER_STAR);
      this.copy(Tags.Blocks.NETHERRACK, Tags.Items.NETHERRACK);
      this.getBuilder(Tags.Items.NUGGETS).add(Tags.Items.NUGGETS_IRON, Tags.Items.NUGGETS_GOLD);
      this.getBuilder(Tags.Items.NUGGETS_IRON).add((Object)Items.IRON_NUGGET);
      this.getBuilder(Tags.Items.NUGGETS_GOLD).add((Object)Items.GOLD_NUGGET);
      this.copy(Tags.Blocks.OBSIDIAN, Tags.Items.OBSIDIAN);
      this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
      this.copy(Tags.Blocks.ORES_COAL, Tags.Items.ORES_COAL);
      this.copy(Tags.Blocks.ORES_DIAMOND, Tags.Items.ORES_DIAMOND);
      this.copy(Tags.Blocks.ORES_EMERALD, Tags.Items.ORES_EMERALD);
      this.copy(Tags.Blocks.ORES_GOLD, Tags.Items.ORES_GOLD);
      this.copy(Tags.Blocks.ORES_IRON, Tags.Items.ORES_IRON);
      this.copy(Tags.Blocks.ORES_LAPIS, Tags.Items.ORES_LAPIS);
      this.copy(Tags.Blocks.ORES_QUARTZ, Tags.Items.ORES_QUARTZ);
      this.copy(Tags.Blocks.ORES_REDSTONE, Tags.Items.ORES_REDSTONE);
      this.getBuilder(Tags.Items.RODS).add(Tags.Items.RODS_BLAZE, Tags.Items.RODS_WOODEN);
      this.getBuilder(Tags.Items.RODS_BLAZE).add((Object)Items.BLAZE_ROD);
      this.getBuilder(Tags.Items.RODS_WOODEN).add((Object)Items.STICK);
      this.copy(Tags.Blocks.SAND, Tags.Items.SAND);
      this.copy(Tags.Blocks.SAND_COLORLESS, Tags.Items.SAND_COLORLESS);
      this.copy(Tags.Blocks.SAND_RED, Tags.Items.SAND_RED);
      this.copy(Tags.Blocks.SANDSTONE, Tags.Items.SANDSTONE);
      this.getBuilder(Tags.Items.SEEDS).add(Tags.Items.SEEDS_BEETROOT, Tags.Items.SEEDS_MELON, Tags.Items.SEEDS_PUMPKIN, Tags.Items.SEEDS_WHEAT);
      this.getBuilder(Tags.Items.SEEDS_BEETROOT).add((Object)Items.BEETROOT_SEEDS);
      this.getBuilder(Tags.Items.SEEDS_MELON).add((Object)Items.MELON_SEEDS);
      this.getBuilder(Tags.Items.SEEDS_PUMPKIN).add((Object)Items.PUMPKIN_SEEDS);
      this.getBuilder(Tags.Items.SEEDS_WHEAT).add((Object)Items.WHEAT_SEEDS);
      this.getBuilder(Tags.Items.SLIMEBALLS).add((Object)Items.SLIME_BALL);
      this.copy(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
      this.copy(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
      this.copy(Tags.Blocks.STONE, Tags.Items.STONE);
      this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
      this.copy(Tags.Blocks.STORAGE_BLOCKS_COAL, Tags.Items.STORAGE_BLOCKS_COAL);
      this.copy(Tags.Blocks.STORAGE_BLOCKS_DIAMOND, Tags.Items.STORAGE_BLOCKS_DIAMOND);
      this.copy(Tags.Blocks.STORAGE_BLOCKS_EMERALD, Tags.Items.STORAGE_BLOCKS_EMERALD);
      this.copy(Tags.Blocks.STORAGE_BLOCKS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
      this.copy(Tags.Blocks.STORAGE_BLOCKS_IRON, Tags.Items.STORAGE_BLOCKS_IRON);
      this.copy(Tags.Blocks.STORAGE_BLOCKS_LAPIS, Tags.Items.STORAGE_BLOCKS_LAPIS);
      this.copy(Tags.Blocks.STORAGE_BLOCKS_QUARTZ, Tags.Items.STORAGE_BLOCKS_QUARTZ);
      this.copy(Tags.Blocks.STORAGE_BLOCKS_REDSTONE, Tags.Items.STORAGE_BLOCKS_REDSTONE);
      this.getBuilder(Tags.Items.STRING).add((Object)Items.STRING);
      this.copy(Tags.Blocks.SUPPORTS_BEACON, Tags.Items.SUPPORTS_BEACON);
      this.copy(Tags.Blocks.SUPPORTS_CONDUIT, Tags.Items.SUPPORTS_CONDUIT);
   }

   private void addColored(Consumer<Tag<Item>> consumer, Tag<Item> group, String pattern) {
      String prefix = group.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
      DyeColor[] var5 = DyeColor.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         DyeColor color = var5[var7];
         ResourceLocation key = new ResourceLocation("minecraft", pattern.replace("{color}", color.getTranslationKey()));
         Tag<Item> tag = this.getForgeItemTag(prefix + color.getTranslationKey());
         Item item = (Item)ForgeRegistries.ITEMS.getValue(key);
         if (item == null || item == Items.AIR) {
            throw new IllegalStateException("Unknown vanilla item: " + key.toString());
         }

         this.getBuilder(tag).add((Object)item);
         consumer.accept(tag);
      }

   }

   private void copyColored(Tag<Block> blockGroup, Tag<Item> itemGroup) {
      String blockPre = blockGroup.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
      String itemPre = itemGroup.getId().getPath().toUpperCase(Locale.ENGLISH) + '_';
      DyeColor[] var5 = DyeColor.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         DyeColor color = var5[var7];
         Tag<Block> from = this.getForgeBlockTag(blockPre + color.getTranslationKey());
         Tag<Item> to = this.getForgeItemTag(itemPre + color.getTranslationKey());
         this.copy(from, to);
      }

      this.copy(this.getForgeBlockTag(blockPre + "colorless"), this.getForgeItemTag(itemPre + "colorless"));
   }

   private Tag<Block> getForgeBlockTag(String name) {
      try {
         name = name.toUpperCase(Locale.ENGLISH);
         return (Tag)Tags.Blocks.class.getDeclaredField(name).get((Object)null);
      } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException var3) {
         throw new IllegalStateException(Tags.Blocks.class.getName() + " is missing tag name: " + name);
      }
   }

   private Tag<Item> getForgeItemTag(String name) {
      try {
         name = name.toUpperCase(Locale.ENGLISH);
         return (Tag)Tags.Items.class.getDeclaredField(name).get((Object)null);
      } catch (IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException var3) {
         throw new IllegalStateException(Tags.Items.class.getName() + " is missing tag name: " + name);
      }
   }

   protected Path makePath(ResourceLocation id) {
      return this.filter != null && this.filter.contains(id) ? null : super.makePath(id);
   }

   public String getName() {
      return "Forge Item Tags";
   }
}
