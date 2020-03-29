package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class BannerItem extends WallOrFloorItem {
   public BannerItem(Block p_i48529_1_, Block p_i48529_2_, Item.Properties p_i48529_3_) {
      super(p_i48529_1_, p_i48529_2_, p_i48529_3_);
      Validate.isInstanceOf(AbstractBannerBlock.class, p_i48529_1_);
      Validate.isInstanceOf(AbstractBannerBlock.class, p_i48529_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public static void appendHoverTextFromTileEntityTag(ItemStack p_185054_0_, List<ITextComponent> p_185054_1_) {
      CompoundNBT lvt_2_1_ = p_185054_0_.getChildTag("BlockEntityTag");
      if (lvt_2_1_ != null && lvt_2_1_.contains("Patterns")) {
         ListNBT lvt_3_1_ = lvt_2_1_.getList("Patterns", 10);

         for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.size() && lvt_4_1_ < 6; ++lvt_4_1_) {
            CompoundNBT lvt_5_1_ = lvt_3_1_.getCompound(lvt_4_1_);
            DyeColor lvt_6_1_ = DyeColor.byId(lvt_5_1_.getInt("Color"));
            BannerPattern lvt_7_1_ = BannerPattern.byHash(lvt_5_1_.getString("Pattern"));
            if (lvt_7_1_ != null) {
               p_185054_1_.add((new TranslationTextComponent("block.minecraft.banner." + lvt_7_1_.getFileName() + '.' + lvt_6_1_.getTranslationKey(), new Object[0])).applyTextStyle(TextFormatting.GRAY));
            }
         }

      }
   }

   public DyeColor getColor() {
      return ((AbstractBannerBlock)this.getBlock()).getColor();
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      appendHoverTextFromTileEntityTag(p_77624_1_, p_77624_3_);
   }
}
