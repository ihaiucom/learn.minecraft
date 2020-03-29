package net.minecraft.data;

import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemTagsProvider extends TagsProvider<Item> {
   private static final Logger LOGGER = LogManager.getLogger();

   public ItemTagsProvider(DataGenerator p_i48255_1_) {
      super(p_i48255_1_, Registry.ITEM);
   }

   protected void registerTags() {
      this.copy(BlockTags.WOOL, ItemTags.WOOL);
      this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
      this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
      this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
      this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
      this.copy(BlockTags.CARPETS, ItemTags.CARPETS);
      this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
      this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
      this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
      this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
      this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
      this.copy(BlockTags.DOORS, ItemTags.DOORS);
      this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
      this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
      this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
      this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
      this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
      this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
      this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
      this.copy(BlockTags.LOGS, ItemTags.LOGS);
      this.copy(BlockTags.SAND, ItemTags.SAND);
      this.copy(BlockTags.SLABS, ItemTags.SLABS);
      this.copy(BlockTags.WALLS, ItemTags.WALLS);
      this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
      this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
      this.copy(BlockTags.RAILS, ItemTags.RAILS);
      this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
      this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
      this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
      this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
      this.copy(BlockTags.BEDS, ItemTags.BEDS);
      this.copy(BlockTags.FENCES, ItemTags.FENCES);
      this.copy(BlockTags.field_226148_H_, ItemTags.field_226158_H_);
      this.copy(BlockTags.field_226149_I_, ItemTags.field_226159_I_);
      this.getBuilder(ItemTags.BANNERS).add((Object[])(Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER));
      this.getBuilder(ItemTags.BOATS).add((Object[])(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT));
      this.getBuilder(ItemTags.FISHES).add((Object[])(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH));
      this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
      this.getBuilder(ItemTags.MUSIC_DISCS).add((Object[])(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT));
      this.getBuilder(ItemTags.COALS).add((Object[])(Items.COAL, Items.CHARCOAL));
      this.getBuilder(ItemTags.ARROWS).add((Object[])(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW));
      this.getBuilder(ItemTags.field_226160_P_).add((Object[])(Items.WRITTEN_BOOK, Items.WRITABLE_BOOK));
   }

   protected void copy(Tag<Block> p_200438_1_, Tag<Item> p_200438_2_) {
      Tag.Builder<Item> lvt_3_1_ = this.getBuilder(p_200438_2_);
      Iterator var4 = p_200438_1_.getEntries().iterator();

      while(var4.hasNext()) {
         Tag.ITagEntry<Block> lvt_5_1_ = (Tag.ITagEntry)var4.next();
         Tag.ITagEntry<Item> lvt_6_1_ = this.copyEntry(lvt_5_1_);
         lvt_3_1_.add(lvt_6_1_);
      }

   }

   private Tag.ITagEntry<Item> copyEntry(Tag.ITagEntry<Block> p_200439_1_) {
      if (p_200439_1_ instanceof Tag.TagEntry) {
         return new Tag.TagEntry(((Tag.TagEntry)p_200439_1_).getSerializedId());
      } else if (p_200439_1_ instanceof Tag.ListEntry) {
         List<Item> lvt_2_1_ = Lists.newArrayList();
         Iterator var3 = ((Tag.ListEntry)p_200439_1_).getTaggedItems().iterator();

         while(var3.hasNext()) {
            Block lvt_4_1_ = (Block)var3.next();
            Item lvt_5_1_ = lvt_4_1_.asItem();
            if (lvt_5_1_ == Items.AIR) {
               LOGGER.warn("Itemless block copied to item tag: {}", Registry.BLOCK.getKey(lvt_4_1_));
            } else {
               lvt_2_1_.add(lvt_5_1_);
            }
         }

         return new Tag.ListEntry(lvt_2_1_);
      } else {
         throw new UnsupportedOperationException("Unknown tag entry " + p_200439_1_);
      }
   }

   protected Path makePath(ResourceLocation p_200431_1_) {
      return this.generator.getOutputFolder().resolve("data/" + p_200431_1_.getNamespace() + "/tags/items/" + p_200431_1_.getPath() + ".json");
   }

   public String getName() {
      return "Item Tags";
   }

   protected void setCollection(TagCollection<Item> p_200429_1_) {
      ItemTags.setCollection(p_200429_1_);
   }
}
