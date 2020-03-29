package net.minecraft.entity.merchant;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IMerchant {
   void setCustomer(@Nullable PlayerEntity var1);

   @Nullable
   PlayerEntity getCustomer();

   MerchantOffers getOffers();

   @OnlyIn(Dist.CLIENT)
   void func_213703_a(@Nullable MerchantOffers var1);

   void onTrade(MerchantOffer var1);

   void verifySellingItem(ItemStack var1);

   World getWorld();

   int getXp();

   void func_213702_q(int var1);

   boolean func_213705_dZ();

   SoundEvent func_213714_ea();

   default boolean func_223340_ej() {
      return false;
   }

   default void func_213707_a(PlayerEntity p_213707_1_, ITextComponent p_213707_2_, int p_213707_3_) {
      OptionalInt lvt_4_1_ = p_213707_1_.openContainer(new SimpleNamedContainerProvider((p_213701_1_, p_213701_2_, p_213701_3_) -> {
         return new MerchantContainer(p_213701_1_, p_213701_2_, this);
      }, p_213707_2_));
      if (lvt_4_1_.isPresent()) {
         MerchantOffers lvt_5_1_ = this.getOffers();
         if (!lvt_5_1_.isEmpty()) {
            p_213707_1_.func_213818_a(lvt_4_1_.getAsInt(), lvt_5_1_, p_213707_3_, this.getXp(), this.func_213705_dZ(), this.func_223340_ej());
         }
      }

   }
}
