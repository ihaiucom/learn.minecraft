package net.minecraft.item;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FishBucketItem extends BucketItem {
   private final EntityType<?> fishType;
   private final Supplier<? extends EntityType<?>> fishTypeSupplier;

   /** @deprecated */
   @Deprecated
   public FishBucketItem(EntityType<?> p_i49022_1_, Fluid p_i49022_2_, Item.Properties p_i49022_3_) {
      super(p_i49022_2_, p_i49022_3_);
      this.fishType = p_i49022_1_;
      this.fishTypeSupplier = () -> {
         return p_i49022_1_;
      };
   }

   public FishBucketItem(Supplier<? extends EntityType<?>> p_i230072_1_, Supplier<? extends Fluid> p_i230072_2_, Item.Properties p_i230072_3_) {
      super(p_i230072_2_, p_i230072_3_);
      this.fishType = null;
      this.fishTypeSupplier = p_i230072_1_;
   }

   public void onLiquidPlaced(World p_203792_1_, ItemStack p_203792_2_, BlockPos p_203792_3_) {
      if (!p_203792_1_.isRemote) {
         this.placeFish(p_203792_1_, p_203792_2_, p_203792_3_);
      }

   }

   protected void playEmptySound(@Nullable PlayerEntity p_203791_1_, IWorld p_203791_2_, BlockPos p_203791_3_) {
      p_203791_2_.playSound(p_203791_1_, p_203791_3_, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }

   private void placeFish(World p_205357_1_, ItemStack p_205357_2_, BlockPos p_205357_3_) {
      Entity entity = this.fishType.spawn(p_205357_1_, p_205357_2_, (PlayerEntity)null, p_205357_3_, SpawnReason.BUCKET, true, false);
      if (entity != null) {
         ((AbstractFishEntity)entity).setFromBucket(true);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      if (this.fishType == EntityType.TROPICAL_FISH) {
         CompoundNBT compoundnbt = p_77624_1_.getTag();
         if (compoundnbt != null && compoundnbt.contains("BucketVariantTag", 3)) {
            int i = compoundnbt.getInt("BucketVariantTag");
            TextFormatting[] atextformatting = new TextFormatting[]{TextFormatting.ITALIC, TextFormatting.GRAY};
            String s = "color.minecraft." + TropicalFishEntity.func_212326_d(i);
            String s1 = "color.minecraft." + TropicalFishEntity.func_212323_p(i);

            for(int j = 0; j < TropicalFishEntity.SPECIAL_VARIANTS.length; ++j) {
               if (i == TropicalFishEntity.SPECIAL_VARIANTS[j]) {
                  p_77624_3_.add((new TranslationTextComponent(TropicalFishEntity.func_212324_b(j), new Object[0])).applyTextStyles(atextformatting));
                  return;
               }
            }

            p_77624_3_.add((new TranslationTextComponent(TropicalFishEntity.func_212327_q(i), new Object[0])).applyTextStyles(atextformatting));
            ITextComponent itextcomponent = new TranslationTextComponent(s, new Object[0]);
            if (!s.equals(s1)) {
               itextcomponent.appendText(", ").appendSibling(new TranslationTextComponent(s1, new Object[0]));
            }

            itextcomponent.applyTextStyles(atextformatting);
            p_77624_3_.add(itextcomponent);
         }
      }

   }

   protected EntityType<?> getFishType() {
      return (EntityType)this.fishTypeSupplier.get();
   }
}
