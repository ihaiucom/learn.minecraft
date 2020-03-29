package net.minecraft.client.settings;

import com.google.common.collect.ForwardingList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HotbarSnapshot extends ForwardingList<ItemStack> {
   private final NonNullList<ItemStack> hotbarItems;

   public HotbarSnapshot() {
      this.hotbarItems = NonNullList.withSize(PlayerInventory.getHotbarSize(), ItemStack.EMPTY);
   }

   protected List<ItemStack> delegate() {
      return this.hotbarItems;
   }

   public ListNBT createTag() {
      ListNBT lvt_1_1_ = new ListNBT();
      Iterator var2 = this.delegate().iterator();

      while(var2.hasNext()) {
         ItemStack lvt_3_1_ = (ItemStack)var2.next();
         lvt_1_1_.add(lvt_3_1_.write(new CompoundNBT()));
      }

      return lvt_1_1_;
   }

   public void fromTag(ListNBT p_192833_1_) {
      List<ItemStack> lvt_2_1_ = this.delegate();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
         lvt_2_1_.set(lvt_3_1_, ItemStack.read(p_192833_1_.getCompound(lvt_3_1_)));
      }

   }

   public boolean isEmpty() {
      Iterator var1 = this.delegate().iterator();

      ItemStack lvt_2_1_;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         lvt_2_1_ = (ItemStack)var1.next();
      } while(lvt_2_1_.isEmpty());

      return false;
   }
}
