package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IExtensibleEnum;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern implements IExtensibleEnum {
   BASE("base", "b"),
   SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
   SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
   SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
   SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
   STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
   STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
   STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
   STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
   STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
   STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
   STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
   STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
   STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
   CROSS("cross", "cr", "# #", " # ", "# #"),
   STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
   TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
   TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
   TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
   TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
   DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
   DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
   DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
   DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
   CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
   RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
   HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
   HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
   HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
   HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
   BORDER("border", "bo", "###", "# #", "###"),
   CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)),
   GRADIENT("gradient", "gra", "# #", " # ", " # "),
   GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
   BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)),
   GLOBE("globe", "glb"),
   CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)),
   SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
   FLOWER("flower", "flo", new ItemStack(Blocks.OXEYE_DAISY)),
   MOJANG("mojang", "moj", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

   public static final int field_222480_O = values().length;
   public static final int field_222481_P = field_222480_O - 5 - 1;
   private final String fileName;
   private final String hashname;
   private final String[] patterns;
   private ItemStack patternItem;

   private BannerPattern(String p_i47245_3_, String p_i47245_4_) {
      this.patterns = new String[3];
      this.patternItem = ItemStack.EMPTY;
      this.fileName = p_i47245_3_;
      this.hashname = p_i47245_4_;
   }

   private BannerPattern(String p_i47246_3_, String p_i47246_4_, ItemStack p_i47246_5_) {
      this(p_i47246_3_, p_i47246_4_);
      this.patternItem = p_i47246_5_;
   }

   private BannerPattern(String p_i47247_3_, String p_i47247_4_, String p_i47247_5_, String p_i47247_6_, String p_i47247_7_) {
      this(p_i47247_3_, p_i47247_4_);
      this.patterns[0] = p_i47247_5_;
      this.patterns[1] = p_i47247_6_;
      this.patterns[2] = p_i47247_7_;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation func_226957_a_(boolean p_226957_1_) {
      String s = p_226957_1_ ? "banner" : "shield";
      return new ResourceLocation("entity/" + s + "/" + this.getFileName());
   }

   @OnlyIn(Dist.CLIENT)
   public String getFileName() {
      return this.fileName;
   }

   public String getHashname() {
      return this.hashname;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static BannerPattern byHash(String p_190994_0_) {
      BannerPattern[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         BannerPattern bannerpattern = var1[var3];
         if (bannerpattern.hashname.equals(p_190994_0_)) {
            return bannerpattern;
         }
      }

      return null;
   }

   public static BannerPattern create(String p_create_0_, String p_create_1_, String p_create_2_, ItemStack p_create_3_) {
      throw new IllegalStateException("Enum not extended");
   }

   public static BannerPattern create(String p_create_0_, String p_create_1_, String p_create_2_, String p_create_3_, String p_create_4_, String p_create_5_) {
      throw new IllegalStateException("Enum not extended");
   }

   public static class Builder {
      private final List<Pair<BannerPattern, DyeColor>> field_222478_a = Lists.newArrayList();

      public BannerPattern.Builder func_222477_a(BannerPattern p_222477_1_, DyeColor p_222477_2_) {
         this.field_222478_a.add(Pair.of(p_222477_1_, p_222477_2_));
         return this;
      }

      public ListNBT func_222476_a() {
         ListNBT listnbt = new ListNBT();
         Iterator var2 = this.field_222478_a.iterator();

         while(var2.hasNext()) {
            Pair<BannerPattern, DyeColor> pair = (Pair)var2.next();
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("Pattern", ((BannerPattern)pair.getLeft()).hashname);
            compoundnbt.putInt("Color", ((DyeColor)pair.getRight()).getId());
            listnbt.add(compoundnbt);
         }

         return listnbt;
      }
   }
}
