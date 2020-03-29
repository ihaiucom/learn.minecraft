package net.minecraft.util;

import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentNameParts {
   private static final EnchantmentNameParts INSTANCE = new EnchantmentNameParts();
   private final Random rand = new Random();
   private final String[] namePartsArray = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale phnglui mglwnafh cthulhu rlyeh wgahnagl fhtagnbaguette".split(" ");

   private EnchantmentNameParts() {
   }

   public static EnchantmentNameParts getInstance() {
      return INSTANCE;
   }

   public String generateNewRandomName(FontRenderer p_148334_1_, int p_148334_2_) {
      int lvt_3_1_ = this.rand.nextInt(2) + 3;
      String lvt_4_1_ = "";

      for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_1_; ++lvt_5_1_) {
         if (lvt_5_1_ > 0) {
            lvt_4_1_ = lvt_4_1_ + " ";
         }

         lvt_4_1_ = lvt_4_1_ + this.namePartsArray[this.rand.nextInt(this.namePartsArray.length)];
      }

      List<String> lvt_5_2_ = p_148334_1_.listFormattedStringToWidth(lvt_4_1_, p_148334_2_);
      return org.apache.commons.lang3.StringUtils.join(lvt_5_2_.size() >= 2 ? lvt_5_2_.subList(0, 2) : lvt_5_2_, " ");
   }

   public void reseedRandomGenerator(long p_148335_1_) {
      this.rand.setSeed(p_148335_1_);
   }
}
