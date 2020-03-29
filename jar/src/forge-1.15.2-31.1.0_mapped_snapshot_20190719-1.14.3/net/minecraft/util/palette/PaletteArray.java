package net.minecraft.util.palette;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

public class PaletteArray<T> implements IPalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final T[] states;
   private final IResizeCallback<T> resizeHandler;
   private final Function<CompoundNBT, T> deserializer;
   private final int bits;
   private int arraySize;

   public PaletteArray(ObjectIntIdentityMap<T> p_i48962_1_, int p_i48962_2_, IResizeCallback<T> p_i48962_3_, Function<CompoundNBT, T> p_i48962_4_) {
      this.registry = p_i48962_1_;
      this.states = (Object[])(new Object[1 << p_i48962_2_]);
      this.bits = p_i48962_2_;
      this.resizeHandler = p_i48962_3_;
      this.deserializer = p_i48962_4_;
   }

   public int idFor(T p_186041_1_) {
      int lvt_2_2_;
      for(lvt_2_2_ = 0; lvt_2_2_ < this.arraySize; ++lvt_2_2_) {
         if (this.states[lvt_2_2_] == p_186041_1_) {
            return lvt_2_2_;
         }
      }

      lvt_2_2_ = this.arraySize;
      if (lvt_2_2_ < this.states.length) {
         this.states[lvt_2_2_] = p_186041_1_;
         ++this.arraySize;
         return lvt_2_2_;
      } else {
         return this.resizeHandler.onResize(this.bits + 1, p_186041_1_);
      }
   }

   public boolean contains(T p_222626_1_) {
      return ArrayUtils.contains(this.states, p_222626_1_);
   }

   @Nullable
   public T get(int p_186039_1_) {
      return p_186039_1_ >= 0 && p_186039_1_ < this.arraySize ? this.states[p_186039_1_] : null;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
      this.arraySize = p_186038_1_.readVarInt();

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.arraySize; ++lvt_2_1_) {
         this.states[lvt_2_1_] = this.registry.getByValue(p_186038_1_.readVarInt());
      }

   }

   public void write(PacketBuffer p_186037_1_) {
      p_186037_1_.writeVarInt(this.arraySize);

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.arraySize; ++lvt_2_1_) {
         p_186037_1_.writeVarInt(this.registry.get(this.states[lvt_2_1_]));
      }

   }

   public int getSerializedSize() {
      int lvt_1_1_ = PacketBuffer.getVarIntSize(this.func_202137_b());

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.func_202137_b(); ++lvt_2_1_) {
         lvt_1_1_ += PacketBuffer.getVarIntSize(this.registry.get(this.states[lvt_2_1_]));
      }

      return lvt_1_1_;
   }

   public int func_202137_b() {
      return this.arraySize;
   }

   public void read(ListNBT p_196968_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < p_196968_1_.size(); ++lvt_2_1_) {
         this.states[lvt_2_1_] = this.deserializer.apply(p_196968_1_.getCompound(lvt_2_1_));
      }

      this.arraySize = p_196968_1_.size();
   }
}
