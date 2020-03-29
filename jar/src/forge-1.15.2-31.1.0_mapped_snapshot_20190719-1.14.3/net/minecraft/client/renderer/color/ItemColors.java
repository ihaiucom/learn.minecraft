package net.minecraft.client.renderer.color;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.IRegistryDelegate;

@OnlyIn(Dist.CLIENT)
public class ItemColors {
   private final Map<IRegistryDelegate<Item>, IItemColor> colors = new HashMap();

   public static ItemColors init(BlockColors p_186729_0_) {
      ItemColors itemcolors = new ItemColors();
      itemcolors.register((p_lambda$init$0_0_, p_lambda$init$0_1_) -> {
         return p_lambda$init$0_1_ > 0 ? -1 : ((IDyeableArmorItem)p_lambda$init$0_0_.getItem()).getColor(p_lambda$init$0_0_);
      }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
      itemcolors.register((p_lambda$init$1_0_, p_lambda$init$1_1_) -> {
         return GrassColors.get(0.5D, 1.0D);
      }, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
      itemcolors.register((p_lambda$init$2_0_, p_lambda$init$2_1_) -> {
         if (p_lambda$init$2_1_ != 1) {
            return -1;
         } else {
            CompoundNBT compoundnbt = p_lambda$init$2_0_.getChildTag("Explosion");
            int[] aint = compoundnbt != null && compoundnbt.contains("Colors", 11) ? compoundnbt.getIntArray("Colors") : null;
            if (aint == null) {
               return 9079434;
            } else if (aint.length == 1) {
               return aint[0];
            } else {
               int i = 0;
               int j = 0;
               int k = 0;
               int[] var7 = aint;
               int var8 = aint.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  int l = var7[var9];
                  i += (l & 16711680) >> 16;
                  j += (l & '\uff00') >> 8;
                  k += (l & 255) >> 0;
               }

               i /= aint.length;
               j /= aint.length;
               k /= aint.length;
               return i << 16 | j << 8 | k;
            }
         }
      }, Items.FIREWORK_STAR);
      itemcolors.register((p_lambda$init$3_0_, p_lambda$init$3_1_) -> {
         return p_lambda$init$3_1_ > 0 ? -1 : PotionUtils.getColor(p_lambda$init$3_0_);
      }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
      Iterator var2 = SpawnEggItem.getEggs().iterator();

      while(var2.hasNext()) {
         SpawnEggItem spawneggitem = (SpawnEggItem)var2.next();
         itemcolors.register((p_lambda$init$4_1_, p_lambda$init$4_2_) -> {
            return spawneggitem.getColor(p_lambda$init$4_2_);
         }, spawneggitem);
      }

      itemcolors.register((p_lambda$init$5_1_, p_lambda$init$5_2_) -> {
         BlockState blockstate = ((BlockItem)p_lambda$init$5_1_.getItem()).getBlock().getDefaultState();
         return p_186729_0_.func_228054_a_(blockstate, (ILightReader)null, (BlockPos)null, p_lambda$init$5_2_);
      }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
      itemcolors.register((p_lambda$init$6_0_, p_lambda$init$6_1_) -> {
         return p_lambda$init$6_1_ == 0 ? PotionUtils.getColor(p_lambda$init$6_0_) : -1;
      }, Items.TIPPED_ARROW);
      itemcolors.register((p_lambda$init$7_0_, p_lambda$init$7_1_) -> {
         return p_lambda$init$7_1_ == 0 ? -1 : FilledMapItem.getColor(p_lambda$init$7_0_);
      }, Items.FILLED_MAP);
      ForgeHooksClient.onItemColorsInit(itemcolors, p_186729_0_);
      return itemcolors;
   }

   public int getColor(ItemStack p_186728_1_, int p_186728_2_) {
      IItemColor iitemcolor = (IItemColor)this.colors.get(p_186728_1_.getItem().delegate);
      return iitemcolor == null ? -1 : iitemcolor.getColor(p_186728_1_, p_186728_2_);
   }

   public void register(IItemColor p_199877_1_, IItemProvider... p_199877_2_) {
      IItemProvider[] var3 = p_199877_2_;
      int var4 = p_199877_2_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         IItemProvider iitemprovider = var3[var5];
         this.colors.put(iitemprovider.asItem().delegate, p_199877_1_);
      }

   }
}
