package net.minecraftforge.fluids;

import javax.annotation.Nonnull;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FluidStack {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final FluidStack EMPTY;
   private boolean isEmpty;
   private int amount;
   private CompoundNBT tag;
   private IRegistryDelegate<Fluid> fluidDelegate;

   public FluidStack(Fluid fluid, int amount) {
      if (fluid == null) {
         LOGGER.fatal("Null fluid supplied to fluidstack. Did you try and create a stack for an unregistered fluid?");
         throw new IllegalArgumentException("Cannot create a fluidstack from a null fluid");
      } else if (ForgeRegistries.FLUIDS.getKey(fluid) == null) {
         LOGGER.fatal("Failed attempt to create a FluidStack for an unregistered Fluid {} (type {})", fluid.getRegistryName(), fluid.getClass().getName());
         throw new IllegalArgumentException("Cannot create a fluidstack from an unregistered fluid");
      } else {
         this.fluidDelegate = fluid.delegate;
         this.amount = amount;
         this.updateEmpty();
      }
   }

   public FluidStack(Fluid fluid, int amount, CompoundNBT nbt) {
      this(fluid, amount);
      if (nbt != null) {
         this.tag = nbt.copy();
      }

   }

   public FluidStack(FluidStack stack, int amount) {
      this(stack.getFluid(), amount, stack.tag);
   }

   public static FluidStack loadFluidStackFromNBT(CompoundNBT nbt) {
      if (nbt == null) {
         return EMPTY;
      } else if (!nbt.contains("FluidName", 8)) {
         return EMPTY;
      } else {
         ResourceLocation fluidName = new ResourceLocation(nbt.getString("FluidName"));
         Fluid fluid = (Fluid)ForgeRegistries.FLUIDS.getValue(fluidName);
         if (fluid == null) {
            return EMPTY;
         } else {
            FluidStack stack = new FluidStack(fluid, nbt.getInt("Amount"));
            if (nbt.contains("Tag", 10)) {
               stack.tag = nbt.getCompound("Tag");
            }

            return stack;
         }
      }
   }

   public CompoundNBT writeToNBT(CompoundNBT nbt) {
      nbt.putString("FluidName", this.getFluid().getRegistryName().toString());
      nbt.putInt("Amount", this.amount);
      if (this.tag != null) {
         nbt.put("Tag", this.tag);
      }

      return nbt;
   }

   public void writeToPacket(PacketBuffer buf) {
      buf.writeRegistryId(this.getFluid());
      buf.writeVarInt(this.getAmount());
      buf.writeCompoundTag(this.tag);
   }

   public static FluidStack readFromPacket(PacketBuffer buf) {
      Fluid fluid = (Fluid)buf.readRegistryId();
      int amount = buf.readVarInt();
      CompoundNBT tag = buf.readCompoundTag();
      return fluid == Fluids.EMPTY ? EMPTY : new FluidStack(fluid, amount, tag);
   }

   public final Fluid getFluid() {
      return this.isEmpty ? Fluids.EMPTY : (Fluid)this.fluidDelegate.get();
   }

   public final Fluid getRawFluid() {
      return (Fluid)this.fluidDelegate.get();
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   protected void updateEmpty() {
      this.isEmpty = this.getRawFluid() == Fluids.EMPTY || this.amount <= 0;
   }

   public int getAmount() {
      return this.isEmpty ? 0 : this.amount;
   }

   public void setAmount(int amount) {
      if (this.getRawFluid() == Fluids.EMPTY) {
         throw new IllegalStateException("Can't modify the empty stack.");
      } else {
         this.amount = amount;
         this.updateEmpty();
      }
   }

   public void grow(int amount) {
      this.setAmount(this.amount + amount);
   }

   public void shrink(int amount) {
      this.setAmount(this.amount - amount);
   }

   public boolean hasTag() {
      return this.tag != null;
   }

   public CompoundNBT getTag() {
      return this.tag;
   }

   public void setTag(CompoundNBT tag) {
      if (this.getRawFluid() == Fluids.EMPTY) {
         throw new IllegalStateException("Can't modify the empty stack.");
      } else {
         this.tag = tag;
      }
   }

   public CompoundNBT getOrCreateTag() {
      if (this.tag == null) {
         this.setTag(new CompoundNBT());
      }

      return this.tag;
   }

   public CompoundNBT getChildTag(String childName) {
      return this.tag == null ? null : this.tag.getCompound(childName);
   }

   public CompoundNBT getOrCreateChildTag(String childName) {
      this.getOrCreateTag();
      CompoundNBT child = this.tag.getCompound(childName);
      if (!this.tag.contains(childName, 10)) {
         this.tag.put(childName, child);
      }

      return child;
   }

   public void removeChildTag(String childName) {
      if (this.tag != null) {
         this.tag.remove(childName);
      }

   }

   public ITextComponent getDisplayName() {
      return this.getFluid().getAttributes().getDisplayName(this);
   }

   public String getTranslationKey() {
      return this.getFluid().getAttributes().getTranslationKey(this);
   }

   public FluidStack copy() {
      return new FluidStack(this.getFluid(), this.amount, this.tag);
   }

   public boolean isFluidEqual(@Nonnull FluidStack other) {
      return this.getFluid() == other.getFluid() && this.isFluidStackTagEqual(other);
   }

   private boolean isFluidStackTagEqual(FluidStack other) {
      return this.tag == null ? other.tag == null : other.tag != null && this.tag.equals(other.tag);
   }

   public static boolean areFluidStackTagsEqual(@Nonnull FluidStack stack1, @Nonnull FluidStack stack2) {
      return stack1.isFluidStackTagEqual(stack2);
   }

   public boolean containsFluid(@Nonnull FluidStack other) {
      return this.isFluidEqual(other) && this.amount >= other.amount;
   }

   public boolean isFluidStackIdentical(FluidStack other) {
      return this.isFluidEqual(other) && this.amount == other.amount;
   }

   public boolean isFluidEqual(@Nonnull ItemStack other) {
      return (Boolean)FluidUtil.getFluidContained(other).map(this::isFluidEqual).orElse(false);
   }

   public final int hashCode() {
      int code = 1;
      int code = 31 * code + this.getFluid().hashCode();
      code = 31 * code + this.amount;
      if (this.tag != null) {
         code = 31 * code + this.tag.hashCode();
      }

      return code;
   }

   public final boolean equals(Object o) {
      return !(o instanceof FluidStack) ? false : this.isFluidEqual((FluidStack)o);
   }

   static {
      EMPTY = new FluidStack(Fluids.EMPTY, 0);
   }
}
