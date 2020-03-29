package net.minecraft.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemInput implements Predicate<ItemStack> {
   private static final Dynamic2CommandExceptionType STACK_TOO_LARGE = new Dynamic2CommandExceptionType((p_208695_0_, p_208695_1_) -> {
      return new TranslationTextComponent("arguments.item.overstacked", new Object[]{p_208695_0_, p_208695_1_});
   });
   private final Item item;
   @Nullable
   private final CompoundNBT tag;

   public ItemInput(Item p_i47961_1_, @Nullable CompoundNBT p_i47961_2_) {
      this.item = p_i47961_1_;
      this.tag = p_i47961_2_;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean test(ItemStack p_test_1_) {
      return p_test_1_.getItem() == this.item && NBTUtil.areNBTEquals(this.tag, p_test_1_.getTag(), true);
   }

   public ItemStack createStack(int p_197320_1_, boolean p_197320_2_) throws CommandSyntaxException {
      ItemStack lvt_3_1_ = new ItemStack(this.item, p_197320_1_);
      if (this.tag != null) {
         lvt_3_1_.setTag(this.tag);
      }

      if (p_197320_2_ && p_197320_1_ > lvt_3_1_.getMaxStackSize()) {
         throw STACK_TOO_LARGE.create(Registry.ITEM.getKey(this.item), lvt_3_1_.getMaxStackSize());
      } else {
         return lvt_3_1_;
      }
   }

   public String serialize() {
      StringBuilder lvt_1_1_ = new StringBuilder(Registry.ITEM.getId(this.item));
      if (this.tag != null) {
         lvt_1_1_.append(this.tag);
      }

      return lvt_1_1_.toString();
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((ItemStack)p_test_1_);
   }
}
