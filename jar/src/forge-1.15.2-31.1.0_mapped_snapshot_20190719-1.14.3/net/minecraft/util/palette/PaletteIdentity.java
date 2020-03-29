package net.minecraft.util.palette;

import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PaletteIdentity<T> implements IPalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final T defaultState;

   public PaletteIdentity(ObjectIntIdentityMap<T> p_i48965_1_, T p_i48965_2_) {
      this.registry = p_i48965_1_;
      this.defaultState = p_i48965_2_;
   }

   public int idFor(T p_186041_1_) {
      int lvt_2_1_ = this.registry.get(p_186041_1_);
      return lvt_2_1_ == -1 ? 0 : lvt_2_1_;
   }

   public boolean contains(T p_222626_1_) {
      return true;
   }

   public T get(int p_186039_1_) {
      T lvt_2_1_ = this.registry.getByValue(p_186039_1_);
      return lvt_2_1_ == null ? this.defaultState : lvt_2_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
   }

   public void write(PacketBuffer p_186037_1_) {
   }

   public int getSerializedSize() {
      return PacketBuffer.getVarIntSize(0);
   }

   public void read(ListNBT p_196968_1_) {
   }
}
