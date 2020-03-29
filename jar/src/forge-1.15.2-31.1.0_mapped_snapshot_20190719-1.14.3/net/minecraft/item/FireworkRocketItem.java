package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireworkRocketItem extends Item {
   public FireworkRocketItem(Item.Properties p_i48498_1_) {
      super(p_i48498_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      if (!lvt_2_1_.isRemote) {
         ItemStack lvt_3_1_ = p_195939_1_.getItem();
         Vec3d lvt_4_1_ = p_195939_1_.getHitVec();
         Direction lvt_5_1_ = p_195939_1_.getFace();
         FireworkRocketEntity lvt_6_1_ = new FireworkRocketEntity(lvt_2_1_, lvt_4_1_.x + (double)lvt_5_1_.getXOffset() * 0.15D, lvt_4_1_.y + (double)lvt_5_1_.getYOffset() * 0.15D, lvt_4_1_.z + (double)lvt_5_1_.getZOffset() * 0.15D, lvt_3_1_);
         lvt_2_1_.addEntity(lvt_6_1_);
         lvt_3_1_.shrink(1);
      }

      return ActionResultType.SUCCESS;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      if (p_77659_2_.isElytraFlying()) {
         ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
         if (!p_77659_1_.isRemote) {
            p_77659_1_.addEntity(new FireworkRocketEntity(p_77659_1_, lvt_4_1_, p_77659_2_));
            if (!p_77659_2_.abilities.isCreativeMode) {
               lvt_4_1_.shrink(1);
            }
         }

         return ActionResult.func_226248_a_(p_77659_2_.getHeldItem(p_77659_3_));
      } else {
         return ActionResult.func_226250_c_(p_77659_2_.getHeldItem(p_77659_3_));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      CompoundNBT lvt_5_1_ = p_77624_1_.getChildTag("Fireworks");
      if (lvt_5_1_ != null) {
         if (lvt_5_1_.contains("Flight", 99)) {
            p_77624_3_.add((new TranslationTextComponent("item.minecraft.firework_rocket.flight", new Object[0])).appendText(" ").appendText(String.valueOf(lvt_5_1_.getByte("Flight"))).applyTextStyle(TextFormatting.GRAY));
         }

         ListNBT lvt_6_1_ = lvt_5_1_.getList("Explosions", 10);
         if (!lvt_6_1_.isEmpty()) {
            for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_.size(); ++lvt_7_1_) {
               CompoundNBT lvt_8_1_ = lvt_6_1_.getCompound(lvt_7_1_);
               List<ITextComponent> lvt_9_1_ = Lists.newArrayList();
               FireworkStarItem.func_195967_a(lvt_8_1_, lvt_9_1_);
               if (!lvt_9_1_.isEmpty()) {
                  for(int lvt_10_1_ = 1; lvt_10_1_ < lvt_9_1_.size(); ++lvt_10_1_) {
                     lvt_9_1_.set(lvt_10_1_, (new StringTextComponent("  ")).appendSibling((ITextComponent)lvt_9_1_.get(lvt_10_1_)).applyTextStyle(TextFormatting.GRAY));
                  }

                  p_77624_3_.addAll(lvt_9_1_);
               }
            }
         }

      }
   }

   public static enum Shape {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final FireworkRocketItem.Shape[] field_196077_f = (FireworkRocketItem.Shape[])Arrays.stream(values()).sorted(Comparator.comparingInt((p_199796_0_) -> {
         return p_199796_0_.field_196078_g;
      })).toArray((p_199797_0_) -> {
         return new FireworkRocketItem.Shape[p_199797_0_];
      });
      private final int field_196078_g;
      private final String field_196079_h;

      private Shape(int p_i47931_3_, String p_i47931_4_) {
         this.field_196078_g = p_i47931_3_;
         this.field_196079_h = p_i47931_4_;
      }

      public int func_196071_a() {
         return this.field_196078_g;
      }

      @OnlyIn(Dist.CLIENT)
      public String func_196068_b() {
         return this.field_196079_h;
      }

      @OnlyIn(Dist.CLIENT)
      public static FireworkRocketItem.Shape func_196070_a(int p_196070_0_) {
         return p_196070_0_ >= 0 && p_196070_0_ < field_196077_f.length ? field_196077_f[p_196070_0_] : SMALL_BALL;
      }
   }
}
