package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompoundNBT implements INBT {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
   public static final INBTType<CompoundNBT> field_229675_a_ = new INBTType<CompoundNBT>() {
      public CompoundNBT func_225649_b_(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.read(384L);
         if (p_225649_2_ > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
         } else {
            HashMap map = Maps.newHashMap();

            byte b0;
            while((b0 = CompoundNBT.readType(p_225649_1_, p_225649_3_)) != 0) {
               String s = CompoundNBT.readKey(p_225649_1_, p_225649_3_);
               p_225649_3_.read((long)(224 + 16 * s.length()));
               p_225649_3_.read(32L);
               INBT inbt = CompoundNBT.func_229680_b_(NBTTypes.func_229710_a_(b0), s, p_225649_1_, p_225649_2_ + 1, p_225649_3_);
               if (map.put(s, inbt) != null) {
                  p_225649_3_.read(288L);
               }
            }

            return new CompoundNBT(map);
         }
      }

      public String func_225648_a_() {
         return "COMPOUND";
      }

      public String func_225650_b_() {
         return "TAG_Compound";
      }
   };
   private final Map<String, INBT> tagMap;

   private CompoundNBT(Map<String, INBT> p_i226075_1_) {
      this.tagMap = p_i226075_1_;
   }

   public CompoundNBT() {
      this(Maps.newHashMap());
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      Iterator var2 = this.tagMap.keySet().iterator();

      while(var2.hasNext()) {
         String s = (String)var2.next();
         INBT inbt = (INBT)this.tagMap.get(s);
         writeEntry(s, inbt, p_74734_1_);
      }

      p_74734_1_.writeByte(0);
   }

   public Set<String> keySet() {
      return this.tagMap.keySet();
   }

   public byte getId() {
      return 10;
   }

   public INBTType<CompoundNBT> func_225647_b_() {
      return field_229675_a_;
   }

   public int size() {
      return this.tagMap.size();
   }

   @Nullable
   public INBT put(String p_218657_1_, INBT p_218657_2_) {
      if (p_218657_2_ == null) {
         throw new IllegalArgumentException("Invalid null NBT value with key " + p_218657_1_);
      } else {
         return (INBT)this.tagMap.put(p_218657_1_, p_218657_2_);
      }
   }

   public void putByte(String p_74774_1_, byte p_74774_2_) {
      this.tagMap.put(p_74774_1_, ByteNBT.func_229671_a_(p_74774_2_));
   }

   public void putShort(String p_74777_1_, short p_74777_2_) {
      this.tagMap.put(p_74777_1_, ShortNBT.func_229701_a_(p_74777_2_));
   }

   public void putInt(String p_74768_1_, int p_74768_2_) {
      this.tagMap.put(p_74768_1_, IntNBT.func_229692_a_(p_74768_2_));
   }

   public void putLong(String p_74772_1_, long p_74772_2_) {
      this.tagMap.put(p_74772_1_, LongNBT.func_229698_a_(p_74772_2_));
   }

   public void putUniqueId(String p_186854_1_, UUID p_186854_2_) {
      this.putLong(p_186854_1_ + "Most", p_186854_2_.getMostSignificantBits());
      this.putLong(p_186854_1_ + "Least", p_186854_2_.getLeastSignificantBits());
   }

   public UUID getUniqueId(String p_186857_1_) {
      return new UUID(this.getLong(p_186857_1_ + "Most"), this.getLong(p_186857_1_ + "Least"));
   }

   public boolean hasUniqueId(String p_186855_1_) {
      return this.contains(p_186855_1_ + "Most", 99) && this.contains(p_186855_1_ + "Least", 99);
   }

   public void func_229681_c_(String p_229681_1_) {
      this.remove(p_229681_1_ + "Most");
      this.remove(p_229681_1_ + "Least");
   }

   public void putFloat(String p_74776_1_, float p_74776_2_) {
      this.tagMap.put(p_74776_1_, FloatNBT.func_229689_a_(p_74776_2_));
   }

   public void putDouble(String p_74780_1_, double p_74780_2_) {
      this.tagMap.put(p_74780_1_, DoubleNBT.func_229684_a_(p_74780_2_));
   }

   public void putString(String p_74778_1_, String p_74778_2_) {
      this.tagMap.put(p_74778_1_, StringNBT.func_229705_a_(p_74778_2_));
   }

   public void putByteArray(String p_74773_1_, byte[] p_74773_2_) {
      this.tagMap.put(p_74773_1_, new ByteArrayNBT(p_74773_2_));
   }

   public void putIntArray(String p_74783_1_, int[] p_74783_2_) {
      this.tagMap.put(p_74783_1_, new IntArrayNBT(p_74783_2_));
   }

   public void putIntArray(String p_197646_1_, List<Integer> p_197646_2_) {
      this.tagMap.put(p_197646_1_, new IntArrayNBT(p_197646_2_));
   }

   public void putLongArray(String p_197644_1_, long[] p_197644_2_) {
      this.tagMap.put(p_197644_1_, new LongArrayNBT(p_197644_2_));
   }

   public void putLongArray(String p_202168_1_, List<Long> p_202168_2_) {
      this.tagMap.put(p_202168_1_, new LongArrayNBT(p_202168_2_));
   }

   public void putBoolean(String p_74757_1_, boolean p_74757_2_) {
      this.tagMap.put(p_74757_1_, ByteNBT.func_229672_a_(p_74757_2_));
   }

   @Nullable
   public INBT get(String p_74781_1_) {
      return (INBT)this.tagMap.get(p_74781_1_);
   }

   public byte getTagId(String p_150299_1_) {
      INBT inbt = (INBT)this.tagMap.get(p_150299_1_);
      return inbt == null ? 0 : inbt.getId();
   }

   public boolean contains(String p_74764_1_) {
      return this.tagMap.containsKey(p_74764_1_);
   }

   public boolean contains(String p_150297_1_, int p_150297_2_) {
      int i = this.getTagId(p_150297_1_);
      if (i == p_150297_2_) {
         return true;
      } else if (p_150297_2_ != 99) {
         return false;
      } else {
         return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
      }
   }

   public byte getByte(String p_74771_1_) {
      try {
         if (this.contains(p_74771_1_, 99)) {
            return ((NumberNBT)this.tagMap.get(p_74771_1_)).getByte();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public short getShort(String p_74765_1_) {
      try {
         if (this.contains(p_74765_1_, 99)) {
            return ((NumberNBT)this.tagMap.get(p_74765_1_)).getShort();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public int getInt(String p_74762_1_) {
      try {
         if (this.contains(p_74762_1_, 99)) {
            return ((NumberNBT)this.tagMap.get(p_74762_1_)).getInt();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public long getLong(String p_74763_1_) {
      try {
         if (this.contains(p_74763_1_, 99)) {
            return ((NumberNBT)this.tagMap.get(p_74763_1_)).getLong();
         }
      } catch (ClassCastException var3) {
      }

      return 0L;
   }

   public float getFloat(String p_74760_1_) {
      try {
         if (this.contains(p_74760_1_, 99)) {
            return ((NumberNBT)this.tagMap.get(p_74760_1_)).getFloat();
         }
      } catch (ClassCastException var3) {
      }

      return 0.0F;
   }

   public double getDouble(String p_74769_1_) {
      try {
         if (this.contains(p_74769_1_, 99)) {
            return ((NumberNBT)this.tagMap.get(p_74769_1_)).getDouble();
         }
      } catch (ClassCastException var3) {
      }

      return 0.0D;
   }

   public String getString(String p_74779_1_) {
      try {
         if (this.contains(p_74779_1_, 8)) {
            return ((INBT)this.tagMap.get(p_74779_1_)).getString();
         }
      } catch (ClassCastException var3) {
      }

      return "";
   }

   public byte[] getByteArray(String p_74770_1_) {
      try {
         if (this.contains(p_74770_1_, 7)) {
            return ((ByteArrayNBT)this.tagMap.get(p_74770_1_)).getByteArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_229677_a_(p_74770_1_, ByteArrayNBT.field_229667_a_, var3));
      }

      return new byte[0];
   }

   public int[] getIntArray(String p_74759_1_) {
      try {
         if (this.contains(p_74759_1_, 11)) {
            return ((IntArrayNBT)this.tagMap.get(p_74759_1_)).getIntArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_229677_a_(p_74759_1_, IntArrayNBT.field_229690_a_, var3));
      }

      return new int[0];
   }

   public long[] getLongArray(String p_197645_1_) {
      try {
         if (this.contains(p_197645_1_, 12)) {
            return ((LongArrayNBT)this.tagMap.get(p_197645_1_)).getAsLongArray();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_229677_a_(p_197645_1_, LongArrayNBT.field_229696_a_, var3));
      }

      return new long[0];
   }

   public CompoundNBT getCompound(String p_74775_1_) {
      try {
         if (this.contains(p_74775_1_, 10)) {
            return (CompoundNBT)this.tagMap.get(p_74775_1_);
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_229677_a_(p_74775_1_, field_229675_a_, var3));
      }

      return new CompoundNBT();
   }

   public ListNBT getList(String p_150295_1_, int p_150295_2_) {
      try {
         if (this.getTagId(p_150295_1_) == 9) {
            ListNBT listnbt = (ListNBT)this.tagMap.get(p_150295_1_);
            if (!listnbt.isEmpty() && listnbt.getTagType() != p_150295_2_) {
               return new ListNBT();
            }

            return listnbt;
         }
      } catch (ClassCastException var4) {
         throw new ReportedException(this.func_229677_a_(p_150295_1_, ListNBT.field_229694_a_, var4));
      }

      return new ListNBT();
   }

   public boolean getBoolean(String p_74767_1_) {
      return this.getByte(p_74767_1_) != 0;
   }

   public void remove(String p_82580_1_) {
      this.tagMap.remove(p_82580_1_);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("{");
      Collection<String> collection = this.tagMap.keySet();
      if (LOGGER.isDebugEnabled()) {
         List<String> list = Lists.newArrayList(this.tagMap.keySet());
         Collections.sort(list);
         collection = list;
      }

      String s;
      for(Iterator var5 = ((Collection)collection).iterator(); var5.hasNext(); stringbuilder.append(handleEscape(s)).append(':').append(this.tagMap.get(s))) {
         s = (String)var5.next();
         if (stringbuilder.length() != 1) {
            stringbuilder.append(',');
         }
      }

      return stringbuilder.append('}').toString();
   }

   public boolean isEmpty() {
      return this.tagMap.isEmpty();
   }

   private CrashReport func_229677_a_(String p_229677_1_, INBTType<?> p_229677_2_, ClassCastException p_229677_3_) {
      CrashReport crashreport = CrashReport.makeCrashReport(p_229677_3_, "Reading NBT data");
      CrashReportCategory crashreportcategory = crashreport.makeCategoryDepth("Corrupt NBT tag", 1);
      crashreportcategory.addDetail("Tag type found", () -> {
         return ((INBT)this.tagMap.get(p_229677_1_)).func_225647_b_().func_225648_a_();
      });
      crashreportcategory.addDetail("Tag type expected", p_229677_2_::func_225648_a_);
      crashreportcategory.addDetail("Tag name", (Object)p_229677_1_);
      return crashreport;
   }

   public CompoundNBT copy() {
      Map<String, INBT> map = Maps.newHashMap(Maps.transformValues(this.tagMap, INBT::copy));
      return new CompoundNBT(map);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof CompoundNBT && Objects.equals(this.tagMap, ((CompoundNBT)p_equals_1_).tagMap);
      }
   }

   public int hashCode() {
      return this.tagMap.hashCode();
   }

   private static void writeEntry(String p_150298_0_, INBT p_150298_1_, DataOutput p_150298_2_) throws IOException {
      p_150298_2_.writeByte(p_150298_1_.getId());
      if (p_150298_1_.getId() != 0) {
         p_150298_2_.writeUTF(p_150298_0_);
         p_150298_1_.write(p_150298_2_);
      }

   }

   private static byte readType(DataInput p_152447_0_, NBTSizeTracker p_152447_1_) throws IOException {
      p_152447_1_.read(8L);
      return p_152447_0_.readByte();
   }

   private static String readKey(DataInput p_152448_0_, NBTSizeTracker p_152448_1_) throws IOException {
      return p_152448_1_.readUTF(p_152448_0_.readUTF());
   }

   private static INBT func_229680_b_(INBTType<?> p_229680_0_, String p_229680_1_, DataInput p_229680_2_, int p_229680_3_, NBTSizeTracker p_229680_4_) {
      try {
         return p_229680_0_.func_225649_b_(p_229680_2_, p_229680_3_, p_229680_4_);
      } catch (IOException var8) {
         CrashReport crashreport = CrashReport.makeCrashReport(var8, "Loading NBT data");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
         crashreportcategory.addDetail("Tag name", (Object)p_229680_1_);
         crashreportcategory.addDetail("Tag type", (Object)p_229680_0_.func_225648_a_());
         throw new ReportedException(crashreport);
      }
   }

   public CompoundNBT merge(CompoundNBT p_197643_1_) {
      Iterator var2 = p_197643_1_.tagMap.keySet().iterator();

      while(var2.hasNext()) {
         String s = (String)var2.next();
         INBT inbt = (INBT)p_197643_1_.tagMap.get(s);
         if (inbt.getId() == 10) {
            if (this.contains(s, 10)) {
               CompoundNBT compoundnbt = this.getCompound(s);
               compoundnbt.merge((CompoundNBT)inbt);
            } else {
               this.put(s, inbt.copy());
            }
         } else {
            this.put(s, inbt.copy());
         }
      }

      return this;
   }

   protected static String handleEscape(String p_193582_0_) {
      return SIMPLE_VALUE.matcher(p_193582_0_).matches() ? p_193582_0_ : StringNBT.quoteAndEscape(p_193582_0_);
   }

   protected static ITextComponent func_197642_t(String p_197642_0_) {
      if (SIMPLE_VALUE.matcher(p_197642_0_).matches()) {
         return (new StringTextComponent(p_197642_0_)).applyTextStyle(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         String s = StringNBT.quoteAndEscape(p_197642_0_);
         String s1 = s.substring(0, 1);
         ITextComponent itextcomponent = (new StringTextComponent(s.substring(1, s.length() - 1))).applyTextStyle(SYNTAX_HIGHLIGHTING_KEY);
         return (new StringTextComponent(s1)).appendSibling(itextcomponent).appendText(s1);
      }
   }

   public ITextComponent toFormattedComponent(String p_199850_1_, int p_199850_2_) {
      if (this.tagMap.isEmpty()) {
         return new StringTextComponent("{}");
      } else {
         ITextComponent itextcomponent = new StringTextComponent("{");
         Collection<String> collection = this.tagMap.keySet();
         if (LOGGER.isDebugEnabled()) {
            List<String> list = Lists.newArrayList(this.tagMap.keySet());
            Collections.sort(list);
            collection = list;
         }

         if (!p_199850_1_.isEmpty()) {
            itextcomponent.appendText("\n");
         }

         ITextComponent itextcomponent1;
         for(Iterator iterator = ((Collection)collection).iterator(); iterator.hasNext(); itextcomponent.appendSibling(itextcomponent1)) {
            String s = (String)iterator.next();
            itextcomponent1 = (new StringTextComponent(Strings.repeat(p_199850_1_, p_199850_2_ + 1))).appendSibling(func_197642_t(s)).appendText(String.valueOf(':')).appendText(" ").appendSibling(((INBT)this.tagMap.get(s)).toFormattedComponent(p_199850_1_, p_199850_2_ + 1));
            if (iterator.hasNext()) {
               itextcomponent1.appendText(String.valueOf(',')).appendText(p_199850_1_.isEmpty() ? " " : "\n");
            }
         }

         if (!p_199850_1_.isEmpty()) {
            itextcomponent.appendText("\n").appendText(Strings.repeat(p_199850_1_, p_199850_2_));
         }

         itextcomponent.appendText("}");
         return itextcomponent;
      }
   }

   // $FF: synthetic method
   CompoundNBT(Map p_i226076_1_, Object p_i226076_2_) {
      this(p_i226076_1_);
   }
}
