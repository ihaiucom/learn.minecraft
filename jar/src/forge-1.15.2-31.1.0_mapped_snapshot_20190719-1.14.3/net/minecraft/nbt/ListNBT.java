package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ListNBT extends CollectionNBT<INBT> {
   public static final INBTType<ListNBT> field_229694_a_ = new INBTType<ListNBT>() {
      public ListNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(296L);
         if (p_225649_2_ > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
         } else {
            byte lvt_4_1_ = p_225649_1_.readByte();
            int lvt_5_1_ = p_225649_1_.readInt();
            if (lvt_4_1_ == 0 && lvt_5_1_ > 0) {
               throw new RuntimeException("Missing type on ListTag");
            } else {
               p_225649_3_.read(32L * (long)lvt_5_1_);
               INBTType<?> lvt_6_1_ = NBTTypes.func_229710_a_(lvt_4_1_);
               List<INBT> lvt_7_1_ = Lists.newArrayListWithCapacity(lvt_5_1_);

               for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_5_1_; ++lvt_8_1_) {
                  lvt_7_1_.add(lvt_6_1_.func_225649_b_(p_225649_1_, p_225649_2_ + 1, p_225649_3_));
               }

               return new ListNBT(lvt_7_1_, lvt_4_1_);
            }
         }
      }

      public String func_225648_a_() {
         return "LIST";
      }

      public String func_225650_b_() {
         return "TAG_List";
      }

      // $FF: synthetic method
      public INBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         return this.func_225649_b_(p_225649_1_, p_225649_2_, p_225649_3_);
      }
   };
   private static final ByteSet field_229695_b_ = new ByteOpenHashSet(Arrays.asList(1, 2, 3, 4, 5, 6));
   private final List<INBT> tagList;
   private byte tagType;

   private ListNBT(List<INBT> p_i226078_1_, byte p_i226078_2_) {
      this.tagList = p_i226078_1_;
      this.tagType = p_i226078_2_;
   }

   public ListNBT() {
      this(Lists.newArrayList(), (byte)0);
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      if (this.tagList.isEmpty()) {
         this.tagType = 0;
      } else {
         this.tagType = ((INBT)this.tagList.get(0)).getId();
      }

      p_74734_1_.writeByte(this.tagType);
      p_74734_1_.writeInt(this.tagList.size());
      Iterator var2 = this.tagList.iterator();

      while(var2.hasNext()) {
         INBT lvt_3_1_ = (INBT)var2.next();
         lvt_3_1_.write(p_74734_1_);
      }

   }

   public byte getId() {
      return 9;
   }

   public INBTType<ListNBT> func_225647_b_() {
      return field_229694_a_;
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder("[");

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.tagList.size(); ++lvt_2_1_) {
         if (lvt_2_1_ != 0) {
            lvt_1_1_.append(',');
         }

         lvt_1_1_.append(this.tagList.get(lvt_2_1_));
      }

      return lvt_1_1_.append(']').toString();
   }

   private void func_218663_f() {
      if (this.tagList.isEmpty()) {
         this.tagType = 0;
      }

   }

   public INBT remove(int p_remove_1_) {
      INBT lvt_2_1_ = (INBT)this.tagList.remove(p_remove_1_);
      this.func_218663_f();
      return lvt_2_1_;
   }

   public boolean isEmpty() {
      return this.tagList.isEmpty();
   }

   public CompoundNBT getCompound(int p_150305_1_) {
      if (p_150305_1_ >= 0 && p_150305_1_ < this.tagList.size()) {
         INBT lvt_2_1_ = (INBT)this.tagList.get(p_150305_1_);
         if (lvt_2_1_.getId() == 10) {
            return (CompoundNBT)lvt_2_1_;
         }
      }

      return new CompoundNBT();
   }

   public ListNBT getList(int p_202169_1_) {
      if (p_202169_1_ >= 0 && p_202169_1_ < this.tagList.size()) {
         INBT lvt_2_1_ = (INBT)this.tagList.get(p_202169_1_);
         if (lvt_2_1_.getId() == 9) {
            return (ListNBT)lvt_2_1_;
         }
      }

      return new ListNBT();
   }

   public short getShort(int p_202170_1_) {
      if (p_202170_1_ >= 0 && p_202170_1_ < this.tagList.size()) {
         INBT lvt_2_1_ = (INBT)this.tagList.get(p_202170_1_);
         if (lvt_2_1_.getId() == 2) {
            return ((ShortNBT)lvt_2_1_).getShort();
         }
      }

      return 0;
   }

   public int getInt(int p_186858_1_) {
      if (p_186858_1_ >= 0 && p_186858_1_ < this.tagList.size()) {
         INBT lvt_2_1_ = (INBT)this.tagList.get(p_186858_1_);
         if (lvt_2_1_.getId() == 3) {
            return ((IntNBT)lvt_2_1_).getInt();
         }
      }

      return 0;
   }

   public int[] getIntArray(int p_150306_1_) {
      if (p_150306_1_ >= 0 && p_150306_1_ < this.tagList.size()) {
         INBT lvt_2_1_ = (INBT)this.tagList.get(p_150306_1_);
         if (lvt_2_1_.getId() == 11) {
            return ((IntArrayNBT)lvt_2_1_).getIntArray();
         }
      }

      return new int[0];
   }

   public double getDouble(int p_150309_1_) {
      if (p_150309_1_ >= 0 && p_150309_1_ < this.tagList.size()) {
         INBT lvt_2_1_ = (INBT)this.tagList.get(p_150309_1_);
         if (lvt_2_1_.getId() == 6) {
            return ((DoubleNBT)lvt_2_1_).getDouble();
         }
      }

      return 0.0D;
   }

   public float getFloat(int p_150308_1_) {
      if (p_150308_1_ >= 0 && p_150308_1_ < this.tagList.size()) {
         INBT lvt_2_1_ = (INBT)this.tagList.get(p_150308_1_);
         if (lvt_2_1_.getId() == 5) {
            return ((FloatNBT)lvt_2_1_).getFloat();
         }
      }

      return 0.0F;
   }

   public String getString(int p_150307_1_) {
      if (p_150307_1_ >= 0 && p_150307_1_ < this.tagList.size()) {
         INBT lvt_2_1_ = (INBT)this.tagList.get(p_150307_1_);
         return lvt_2_1_.getId() == 8 ? lvt_2_1_.getString() : lvt_2_1_.toString();
      } else {
         return "";
      }
   }

   public int size() {
      return this.tagList.size();
   }

   public INBT get(int p_get_1_) {
      return (INBT)this.tagList.get(p_get_1_);
   }

   public INBT set(int p_set_1_, INBT p_set_2_) {
      INBT lvt_3_1_ = this.get(p_set_1_);
      if (!this.func_218659_a(p_set_1_, p_set_2_)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_set_2_.getId(), this.tagType));
      } else {
         return lvt_3_1_;
      }
   }

   public void add(int p_add_1_, INBT p_add_2_) {
      if (!this.func_218660_b(p_add_1_, p_add_2_)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_add_2_.getId(), this.tagType));
      }
   }

   public boolean func_218659_a(int p_218659_1_, INBT p_218659_2_) {
      if (this.func_218661_a(p_218659_2_)) {
         this.tagList.set(p_218659_1_, p_218659_2_);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_218660_b(int p_218660_1_, INBT p_218660_2_) {
      if (this.func_218661_a(p_218660_2_)) {
         this.tagList.add(p_218660_1_, p_218660_2_);
         return true;
      } else {
         return false;
      }
   }

   private boolean func_218661_a(INBT p_218661_1_) {
      if (p_218661_1_.getId() == 0) {
         return false;
      } else if (this.tagType == 0) {
         this.tagType = p_218661_1_.getId();
         return true;
      } else {
         return this.tagType == p_218661_1_.getId();
      }
   }

   public ListNBT copy() {
      Iterable<INBT> lvt_1_1_ = NBTTypes.func_229710_a_(this.tagType).func_225651_c_() ? this.tagList : Iterables.transform(this.tagList, INBT::copy);
      List<INBT> lvt_2_1_ = Lists.newArrayList((Iterable)lvt_1_1_);
      return new ListNBT(lvt_2_1_, this.tagType);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof ListNBT && Objects.equals(this.tagList, ((ListNBT)p_equals_1_).tagList);
      }
   }

   public int hashCode() {
      return this.tagList.hashCode();
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      if (this.isEmpty()) {
         return new StringTextComponent("[]");
      } else {
         int lvt_5_2_;
         if (field_229695_b_.contains(this.tagType) && this.size() <= 8) {
            String lvt_3_1_ = ", ";
            ITextComponent lvt_4_1_ = new StringTextComponent("[");

            for(lvt_5_2_ = 0; lvt_5_2_ < this.tagList.size(); ++lvt_5_2_) {
               if (lvt_5_2_ != 0) {
                  lvt_4_1_.appendText(", ");
               }

               lvt_4_1_.appendSibling(((INBT)this.tagList.get(lvt_5_2_)).toFormattedComponent());
            }

            lvt_4_1_.appendText("]");
            return lvt_4_1_;
         } else {
            ITextComponent lvt_3_2_ = new StringTextComponent("[");
            if (!p_199850_1_.isEmpty()) {
               lvt_3_2_.appendText("\n");
            }

            String lvt_4_2_ = String.valueOf(',');

            for(lvt_5_2_ = 0; lvt_5_2_ < this.tagList.size(); ++lvt_5_2_) {
               ITextComponent lvt_6_1_ = new StringTextComponent(Strings.repeat(p_199850_1_, p_199850_2_ + 1));
               lvt_6_1_.appendSibling(((INBT)this.tagList.get(lvt_5_2_)).toFormattedComponent(p_199850_1_, p_199850_2_ + 1));
               if (lvt_5_2_ != this.tagList.size() - 1) {
                  lvt_6_1_.appendText(lvt_4_2_).appendText(p_199850_1_.isEmpty() ? " " : "\n");
               }

               lvt_3_2_.appendSibling(lvt_6_1_);
            }

            if (!p_199850_1_.isEmpty()) {
               lvt_3_2_.appendText("\n").appendText(Strings.repeat(p_199850_1_, p_199850_2_));
            }

            lvt_3_2_.appendText("]");
            return lvt_3_2_;
         }
      }
   }

   public int getTagType() {
      return this.tagType;
   }

   public void clear() {
      this.tagList.clear();
      this.tagType = 0;
   }

   // $FF: synthetic method
   public INBT copy() {
      return this.copy();
   }

   // $FF: synthetic method
   public Object remove(int p_remove_1_) {
      return this.remove(p_remove_1_);
   }

   // $FF: synthetic method
   public void add(int p_add_1_, Object p_add_2_) {
      this.add(p_add_1_, (INBT)p_add_2_);
   }

   // $FF: synthetic method
   public Object set(int p_set_1_, Object p_set_2_) {
      return this.set(p_set_1_, (INBT)p_set_2_);
   }

   // $FF: synthetic method
   public Object get(int p_get_1_) {
      return this.get(p_get_1_);
   }

   // $FF: synthetic method
   ListNBT(List p_i226079_1_, byte p_i226079_2_, Object p_i226079_3_) {
      this(p_i226079_1_, p_i226079_2_);
   }
}
