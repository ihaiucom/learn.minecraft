package net.minecraftforge.common.extensions;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IForgeEffect {
   default Effect getEffect() {
      return (Effect)this;
   }

   default boolean shouldRender(EffectInstance effect) {
      return true;
   }

   default boolean shouldRenderInvText(EffectInstance effect) {
      return true;
   }

   default boolean shouldRenderHUD(EffectInstance effect) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   default void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, int x, int y, float z) {
   }

   @OnlyIn(Dist.CLIENT)
   default void renderHUDEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z, float alpha) {
   }

   default List<ItemStack> getCurativeItems() {
      ArrayList<ItemStack> ret = new ArrayList();
      ret.add(new ItemStack(Items.MILK_BUCKET));
      return ret;
   }

   default int getGuiSortColor(EffectInstance potionEffect) {
      return this.getEffect().getLiquidColor();
   }
}
