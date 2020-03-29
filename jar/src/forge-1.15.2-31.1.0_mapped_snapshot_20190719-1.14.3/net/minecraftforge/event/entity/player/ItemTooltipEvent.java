package net.minecraftforge.event.entity.player;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class ItemTooltipEvent extends PlayerEvent {
   private final ITooltipFlag flags;
   @Nonnull
   private final ItemStack itemStack;
   private final List<ITextComponent> toolTip;

   public ItemTooltipEvent(@Nonnull ItemStack itemStack, @Nullable PlayerEntity entityPlayer, List<ITextComponent> list, ITooltipFlag flags) {
      super(entityPlayer);
      this.itemStack = itemStack;
      this.toolTip = list;
      this.flags = flags;
   }

   public ITooltipFlag getFlags() {
      return this.flags;
   }

   @Nonnull
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public List<ITextComponent> getToolTip() {
      return this.toolTip;
   }

   @Nullable
   public PlayerEntity getPlayer() {
      return super.getPlayer();
   }
}
