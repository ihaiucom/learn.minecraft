package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WrittenBookItem extends Item {
   public WrittenBookItem(Item.Properties p_i48454_1_) {
      super(p_i48454_1_);
   }

   public static boolean validBookTagContents(@Nullable CompoundNBT p_77828_0_) {
      if (!WritableBookItem.isNBTValid(p_77828_0_)) {
         return false;
      } else if (!p_77828_0_.contains("title", 8)) {
         return false;
      } else {
         String lvt_1_1_ = p_77828_0_.getString("title");
         return lvt_1_1_.length() > 32 ? false : p_77828_0_.contains("author", 8);
      }
   }

   public static int getGeneration(ItemStack p_179230_0_) {
      return p_179230_0_.getTag().getInt("generation");
   }

   public static int func_220049_j(ItemStack p_220049_0_) {
      CompoundNBT lvt_1_1_ = p_220049_0_.getTag();
      return lvt_1_1_ != null ? lvt_1_1_.getList("pages", 8).size() : 0;
   }

   public ITextComponent getDisplayName(ItemStack p_200295_1_) {
      if (p_200295_1_.hasTag()) {
         CompoundNBT lvt_2_1_ = p_200295_1_.getTag();
         String lvt_3_1_ = lvt_2_1_.getString("title");
         if (!StringUtils.isNullOrEmpty(lvt_3_1_)) {
            return new StringTextComponent(lvt_3_1_);
         }
      }

      return super.getDisplayName(p_200295_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      if (p_77624_1_.hasTag()) {
         CompoundNBT lvt_5_1_ = p_77624_1_.getTag();
         String lvt_6_1_ = lvt_5_1_.getString("author");
         if (!StringUtils.isNullOrEmpty(lvt_6_1_)) {
            p_77624_3_.add((new TranslationTextComponent("book.byAuthor", new Object[]{lvt_6_1_})).applyTextStyle(TextFormatting.GRAY));
         }

         p_77624_3_.add((new TranslationTextComponent("book.generation." + lvt_5_1_.getInt("generation"), new Object[0])).applyTextStyle(TextFormatting.GRAY));
      }

   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      BlockPos lvt_3_1_ = p_195939_1_.getPos();
      BlockState lvt_4_1_ = lvt_2_1_.getBlockState(lvt_3_1_);
      if (lvt_4_1_.getBlock() == Blocks.LECTERN) {
         return LecternBlock.tryPlaceBook(lvt_2_1_, lvt_3_1_, lvt_4_1_, p_195939_1_.getItem()) ? ActionResultType.SUCCESS : ActionResultType.PASS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      p_77659_2_.openBook(lvt_4_1_, p_77659_3_);
      p_77659_2_.addStat(Stats.ITEM_USED.get(this));
      return ActionResult.func_226248_a_(lvt_4_1_);
   }

   public static boolean resolveContents(ItemStack p_220050_0_, @Nullable CommandSource p_220050_1_, @Nullable PlayerEntity p_220050_2_) {
      CompoundNBT lvt_3_1_ = p_220050_0_.getTag();
      if (lvt_3_1_ != null && !lvt_3_1_.getBoolean("resolved")) {
         lvt_3_1_.putBoolean("resolved", true);
         if (!validBookTagContents(lvt_3_1_)) {
            return false;
         } else {
            ListNBT lvt_4_1_ = lvt_3_1_.getList("pages", 8);

            for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.size(); ++lvt_5_1_) {
               String lvt_6_1_ = lvt_4_1_.getString(lvt_5_1_);

               Object lvt_7_2_;
               try {
                  ITextComponent lvt_7_1_ = ITextComponent.Serializer.fromJsonLenient(lvt_6_1_);
                  lvt_7_2_ = TextComponentUtils.updateForEntity(p_220050_1_, lvt_7_1_, p_220050_2_, 0);
               } catch (Exception var9) {
                  lvt_7_2_ = new StringTextComponent(lvt_6_1_);
               }

               lvt_4_1_.set(lvt_5_1_, (INBT)StringNBT.func_229705_a_(ITextComponent.Serializer.toJson((ITextComponent)lvt_7_2_)));
            }

            lvt_3_1_.put("pages", lvt_4_1_);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean hasEffect(ItemStack p_77636_1_) {
      return true;
   }
}
