package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

@Immutable
public class LockCode {
   public static final LockCode EMPTY_CODE = new LockCode("");
   private final String lock;

   public LockCode(String p_i45903_1_) {
      this.lock = p_i45903_1_;
   }

   public boolean func_219964_a(ItemStack p_219964_1_) {
      return this.lock.isEmpty() || !p_219964_1_.isEmpty() && p_219964_1_.hasDisplayName() && this.lock.equals(p_219964_1_.getDisplayName().getString());
   }

   public void write(CompoundNBT p_180157_1_) {
      if (!this.lock.isEmpty()) {
         p_180157_1_.putString("Lock", this.lock);
      }

   }

   public static LockCode read(CompoundNBT p_180158_0_) {
      return p_180158_0_.contains("Lock", 8) ? new LockCode(p_180158_0_.getString("Lock")) : EMPTY_CODE;
   }
}
