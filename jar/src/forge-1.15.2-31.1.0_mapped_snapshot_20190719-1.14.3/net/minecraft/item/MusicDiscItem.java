package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MusicDiscItem extends Item {
   private static final Map<SoundEvent, MusicDiscItem> RECORDS = Maps.newHashMap();
   private final int comparatorValue;
   private final SoundEvent sound;

   protected MusicDiscItem(int p_i48475_1_, SoundEvent p_i48475_2_, Item.Properties p_i48475_3_) {
      super(p_i48475_3_);
      this.comparatorValue = p_i48475_1_;
      this.sound = p_i48475_2_;
      RECORDS.put(this.sound, this);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      BlockPos lvt_3_1_ = p_195939_1_.getPos();
      BlockState lvt_4_1_ = lvt_2_1_.getBlockState(lvt_3_1_);
      if (lvt_4_1_.getBlock() == Blocks.JUKEBOX && !(Boolean)lvt_4_1_.get(JukeboxBlock.HAS_RECORD)) {
         ItemStack lvt_5_1_ = p_195939_1_.getItem();
         if (!lvt_2_1_.isRemote) {
            ((JukeboxBlock)Blocks.JUKEBOX).insertRecord(lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_);
            lvt_2_1_.playEvent((PlayerEntity)null, 1010, lvt_3_1_, Item.getIdFromItem(this));
            lvt_5_1_.shrink(1);
            PlayerEntity lvt_6_1_ = p_195939_1_.getPlayer();
            if (lvt_6_1_ != null) {
               lvt_6_1_.addStat(Stats.PLAY_RECORD);
            }
         }

         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public int getComparatorValue() {
      return this.comparatorValue;
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      p_77624_3_.add(this.getRecordDescription().applyTextStyle(TextFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getRecordDescription() {
      return new TranslationTextComponent(this.getTranslationKey() + ".desc", new Object[0]);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static MusicDiscItem getBySound(SoundEvent p_185074_0_) {
      return (MusicDiscItem)RECORDS.get(p_185074_0_);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSound() {
      return this.sound;
   }
}
