package net.minecraftforge.event.entity.player;

import com.google.common.base.Preconditions;
import java.util.List;
import javax.annotation.Nonnegative;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ItemFishedEvent extends PlayerEvent {
   private final NonNullList<ItemStack> stacks = NonNullList.create();
   private final FishingBobberEntity hook;
   private int rodDamage;

   public ItemFishedEvent(List<ItemStack> stacks, int rodDamage, FishingBobberEntity hook) {
      super(hook.getAngler());
      this.stacks.addAll(stacks);
      this.rodDamage = rodDamage;
      this.hook = hook;
   }

   public int getRodDamage() {
      return this.rodDamage;
   }

   public void damageRodBy(@Nonnegative int rodDamage) {
      Preconditions.checkArgument(rodDamage >= 0);
      this.rodDamage = rodDamage;
   }

   public NonNullList<ItemStack> getDrops() {
      return this.stacks;
   }

   public FishingBobberEntity getHookEntity() {
      return this.hook;
   }
}
