package net.minecraftforge.common.extensions;

import java.util.List;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IForgeEffectInstance {
   default EffectInstance getEffectInstance() {
      return (EffectInstance)this;
   }

   default boolean shouldRender() {
      return this.getEffectInstance().getPotion().shouldRender(this.getEffectInstance());
   }

   default boolean shouldRenderInvText() {
      return this.getEffectInstance().getPotion().shouldRenderInvText(this.getEffectInstance());
   }

   default boolean shouldRenderHUD() {
      return this.getEffectInstance().getPotion().shouldRenderHUD(this.getEffectInstance());
   }

   @OnlyIn(Dist.CLIENT)
   default void renderInventoryEffect(DisplayEffectsScreen<?> gui, int x, int y, float z) {
      this.getEffectInstance().getPotion().renderInventoryEffect(this.getEffectInstance(), gui, x, y, z);
   }

   @OnlyIn(Dist.CLIENT)
   default void renderHUDEffect(AbstractGui gui, int x, int y, float z, float alpha) {
      this.getEffectInstance().getPotion().renderHUDEffect(this.getEffectInstance(), gui, x, y, z, alpha);
   }

   List<ItemStack> getCurativeItems();

   default boolean isCurativeItem(ItemStack stack) {
      return this.getCurativeItems().stream().anyMatch((e) -> {
         return e.isItemEqual(stack);
      });
   }

   void setCurativeItems(List<ItemStack> var1);

   default void addCurativeItem(ItemStack stack) {
      if (!this.isCurativeItem(stack)) {
         this.getCurativeItems().add(stack);
      }

   }

   default void writeCurativeItems(CompoundNBT nbt) {
      ListNBT list = new ListNBT();
      this.getCurativeItems().forEach((s) -> {
         list.add(s.write(new CompoundNBT()));
      });
      nbt.put("CurativeItems", list);
   }
}
