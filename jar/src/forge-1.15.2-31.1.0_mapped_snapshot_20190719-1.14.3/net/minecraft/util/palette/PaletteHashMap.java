package net.minecraft.util.palette;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PaletteHashMap<T> implements IPalette<T> {
   private final ObjectIntIdentityMap<T> registry;
   private final IntIdentityHashBiMap<T> statePaletteMap;
   private final IResizeCallback<T> paletteResizer;
   private final Function<CompoundNBT, T> deserializer;
   private final Function<T, CompoundNBT> serializer;
   private final int bits;

   public PaletteHashMap(ObjectIntIdentityMap<T> p_i48964_1_, int p_i48964_2_, IResizeCallback<T> p_i48964_3_, Function<CompoundNBT, T> p_i48964_4_, Function<T, CompoundNBT> p_i48964_5_) {
      this.registry = p_i48964_1_;
      this.bits = p_i48964_2_;
      this.paletteResizer = p_i48964_3_;
      this.deserializer = p_i48964_4_;
      this.serializer = p_i48964_5_;
      this.statePaletteMap = new IntIdentityHashBiMap(1 << p_i48964_2_);
   }

   public int idFor(T p_186041_1_) {
      int lvt_2_1_ = this.statePaletteMap.getId(p_186041_1_);
      if (lvt_2_1_ == -1) {
         lvt_2_1_ = this.statePaletteMap.add(p_186041_1_);
         if (lvt_2_1_ >= 1 << this.bits) {
            lvt_2_1_ = this.paletteResizer.onResize(this.bits + 1, p_186041_1_);
         }
      }

      return lvt_2_1_;
   }

   public boolean contains(T p_222626_1_) {
      return this.statePaletteMap.getId(p_222626_1_) != -1;
   }

   @Nullable
   public T get(int p_186039_1_) {
      return this.statePaletteMap.getByValue(p_186039_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186038_1_) {
      this.statePaletteMap.clear();
      int lvt_2_1_ = p_186038_1_.readVarInt();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         this.statePaletteMap.add(this.registry.getByValue(p_186038_1_.readVarInt()));
      }

   }

   public void write(PacketBuffer p_186037_1_) {
      int lvt_2_1_ = this.getPaletteSize();
      p_186037_1_.writeVarInt(lvt_2_1_);

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         p_186037_1_.writeVarInt(this.registry.get(this.statePaletteMap.getByValue(lvt_3_1_)));
      }

   }

   public int getSerializedSize() {
      int lvt_1_1_ = PacketBuffer.getVarIntSize(this.getPaletteSize());

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.getPaletteSize(); ++lvt_2_1_) {
         lvt_1_1_ += PacketBuffer.getVarIntSize(this.registry.get(this.statePaletteMap.getByValue(lvt_2_1_)));
      }

      return lvt_1_1_;
   }

   public int getPaletteSize() {
      return this.statePaletteMap.size();
   }

   public void read(ListNBT p_196968_1_) {
      this.statePaletteMap.clear();

      for(int lvt_2_1_ = 0; lvt_2_1_ < p_196968_1_.size(); ++lvt_2_1_) {
         this.statePaletteMap.add(this.deserializer.apply(p_196968_1_.getCompound(lvt_2_1_)));
      }

   }

   public void writePaletteToList(ListNBT p_196969_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < this.getPaletteSize(); ++lvt_2_1_) {
         p_196969_1_.add(this.serializer.apply(this.statePaletteMap.getByValue(lvt_2_1_)));
      }

   }
}
