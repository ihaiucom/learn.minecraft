package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class NBTDynamicOps implements DynamicOps<INBT> {
   public static final NBTDynamicOps INSTANCE = new NBTDynamicOps();

   protected NBTDynamicOps() {
   }

   public INBT empty() {
      return EndNBT.field_229686_b_;
   }

   public Type<?> getType(INBT p_getType_1_) {
      switch(p_getType_1_.getId()) {
      case 0:
         return DSL.nilType();
      case 1:
         return DSL.byteType();
      case 2:
         return DSL.shortType();
      case 3:
         return DSL.intType();
      case 4:
         return DSL.longType();
      case 5:
         return DSL.floatType();
      case 6:
         return DSL.doubleType();
      case 7:
         return DSL.list(DSL.byteType());
      case 8:
         return DSL.string();
      case 9:
         return DSL.list(DSL.remainderType());
      case 10:
         return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
      case 11:
         return DSL.list(DSL.intType());
      case 12:
         return DSL.list(DSL.longType());
      default:
         return DSL.remainderType();
      }
   }

   public Optional<Number> getNumberValue(INBT p_getNumberValue_1_) {
      return p_getNumberValue_1_ instanceof NumberNBT ? Optional.of(((NumberNBT)p_getNumberValue_1_).getAsNumber()) : Optional.empty();
   }

   public INBT createNumeric(Number p_createNumeric_1_) {
      return DoubleNBT.func_229684_a_(p_createNumeric_1_.doubleValue());
   }

   public INBT createByte(byte p_createByte_1_) {
      return ByteNBT.func_229671_a_(p_createByte_1_);
   }

   public INBT createShort(short p_createShort_1_) {
      return ShortNBT.func_229701_a_(p_createShort_1_);
   }

   public INBT createInt(int p_createInt_1_) {
      return IntNBT.func_229692_a_(p_createInt_1_);
   }

   public INBT createLong(long p_createLong_1_) {
      return LongNBT.func_229698_a_(p_createLong_1_);
   }

   public INBT createFloat(float p_createFloat_1_) {
      return FloatNBT.func_229689_a_(p_createFloat_1_);
   }

   public INBT createDouble(double p_createDouble_1_) {
      return DoubleNBT.func_229684_a_(p_createDouble_1_);
   }

   public INBT createBoolean(boolean p_createBoolean_1_) {
      return ByteNBT.func_229672_a_(p_createBoolean_1_);
   }

   public Optional<String> getStringValue(INBT p_getStringValue_1_) {
      return p_getStringValue_1_ instanceof StringNBT ? Optional.of(p_getStringValue_1_.getString()) : Optional.empty();
   }

   public INBT createString(String p_createString_1_) {
      return StringNBT.func_229705_a_(p_createString_1_);
   }

   public INBT mergeInto(INBT p_mergeInto_1_, INBT p_mergeInto_2_) {
      if (p_mergeInto_2_ instanceof EndNBT) {
         return p_mergeInto_1_;
      } else if (!(p_mergeInto_1_ instanceof CompoundNBT)) {
         if (p_mergeInto_1_ instanceof EndNBT) {
            throw new IllegalArgumentException("mergeInto called with a null input.");
         } else if (p_mergeInto_1_ instanceof CollectionNBT) {
            CollectionNBT<INBT> lvt_3_2_ = new ListNBT();
            CollectionNBT<?> lvt_4_2_ = (CollectionNBT)p_mergeInto_1_;
            lvt_3_2_.addAll(lvt_4_2_);
            lvt_3_2_.add(p_mergeInto_2_);
            return lvt_3_2_;
         } else {
            return p_mergeInto_1_;
         }
      } else if (!(p_mergeInto_2_ instanceof CompoundNBT)) {
         return p_mergeInto_1_;
      } else {
         CompoundNBT lvt_4_1_ = new CompoundNBT();
         CompoundNBT lvt_5_1_ = (CompoundNBT)p_mergeInto_1_;
         Iterator var6 = lvt_5_1_.keySet().iterator();

         while(var6.hasNext()) {
            String lvt_7_1_ = (String)var6.next();
            lvt_4_1_.put(lvt_7_1_, lvt_5_1_.get(lvt_7_1_));
         }

         CompoundNBT lvt_6_1_ = (CompoundNBT)p_mergeInto_2_;
         Iterator var11 = lvt_6_1_.keySet().iterator();

         while(var11.hasNext()) {
            String lvt_8_1_ = (String)var11.next();
            lvt_4_1_.put(lvt_8_1_, lvt_6_1_.get(lvt_8_1_));
         }

         return lvt_4_1_;
      }
   }

   public INBT mergeInto(INBT p_mergeInto_1_, INBT p_mergeInto_2_, INBT p_mergeInto_3_) {
      CompoundNBT lvt_4_2_;
      if (p_mergeInto_1_ instanceof EndNBT) {
         lvt_4_2_ = new CompoundNBT();
      } else {
         if (!(p_mergeInto_1_ instanceof CompoundNBT)) {
            return p_mergeInto_1_;
         }

         CompoundNBT lvt_5_1_ = (CompoundNBT)p_mergeInto_1_;
         lvt_4_2_ = new CompoundNBT();
         lvt_5_1_.keySet().forEach((p_212014_2_) -> {
            lvt_4_2_.put(p_212014_2_, lvt_5_1_.get(p_212014_2_));
         });
      }

      lvt_4_2_.put(p_mergeInto_2_.getString(), p_mergeInto_3_);
      return lvt_4_2_;
   }

   public INBT merge(INBT p_merge_1_, INBT p_merge_2_) {
      if (p_merge_1_ instanceof EndNBT) {
         return p_merge_2_;
      } else if (p_merge_2_ instanceof EndNBT) {
         return p_merge_1_;
      } else {
         if (p_merge_1_ instanceof CompoundNBT && p_merge_2_ instanceof CompoundNBT) {
            CompoundNBT lvt_3_1_ = (CompoundNBT)p_merge_1_;
            CompoundNBT lvt_4_1_ = (CompoundNBT)p_merge_2_;
            CompoundNBT lvt_5_1_ = new CompoundNBT();
            lvt_3_1_.keySet().forEach((p_211384_2_) -> {
               lvt_5_1_.put(p_211384_2_, lvt_3_1_.get(p_211384_2_));
            });
            lvt_4_1_.keySet().forEach((p_212012_2_) -> {
               lvt_5_1_.put(p_212012_2_, lvt_4_1_.get(p_212012_2_));
            });
         }

         if (p_merge_1_ instanceof CollectionNBT && p_merge_2_ instanceof CollectionNBT) {
            ListNBT lvt_3_2_ = new ListNBT();
            lvt_3_2_.addAll((CollectionNBT)p_merge_1_);
            lvt_3_2_.addAll((CollectionNBT)p_merge_2_);
            return lvt_3_2_;
         } else {
            throw new IllegalArgumentException("Could not merge " + p_merge_1_ + " and " + p_merge_2_);
         }
      }
   }

   public Optional<Map<INBT, INBT>> getMapValues(INBT p_getMapValues_1_) {
      if (p_getMapValues_1_ instanceof CompoundNBT) {
         CompoundNBT lvt_2_1_ = (CompoundNBT)p_getMapValues_1_;
         return Optional.of(lvt_2_1_.keySet().stream().map((p_210819_2_) -> {
            return Pair.of(this.createString(p_210819_2_), lvt_2_1_.get(p_210819_2_));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      } else {
         return Optional.empty();
      }
   }

   public INBT createMap(Map<INBT, INBT> p_createMap_1_) {
      CompoundNBT lvt_2_1_ = new CompoundNBT();
      Iterator var3 = p_createMap_1_.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<INBT, INBT> lvt_4_1_ = (Entry)var3.next();
         lvt_2_1_.put(((INBT)lvt_4_1_.getKey()).getString(), (INBT)lvt_4_1_.getValue());
      }

      return lvt_2_1_;
   }

   public Optional<Stream<INBT>> getStream(INBT p_getStream_1_) {
      return p_getStream_1_ instanceof CollectionNBT ? Optional.of(((CollectionNBT)p_getStream_1_).stream().map((p_210817_0_) -> {
         return p_210817_0_;
      })) : Optional.empty();
   }

   public Optional<ByteBuffer> getByteBuffer(INBT p_getByteBuffer_1_) {
      return p_getByteBuffer_1_ instanceof ByteArrayNBT ? Optional.of(ByteBuffer.wrap(((ByteArrayNBT)p_getByteBuffer_1_).getByteArray())) : super.getByteBuffer(p_getByteBuffer_1_);
   }

   public INBT createByteList(ByteBuffer p_createByteList_1_) {
      return new ByteArrayNBT(DataFixUtils.toArray(p_createByteList_1_));
   }

   public Optional<IntStream> getIntStream(INBT p_getIntStream_1_) {
      return p_getIntStream_1_ instanceof IntArrayNBT ? Optional.of(Arrays.stream(((IntArrayNBT)p_getIntStream_1_).getIntArray())) : super.getIntStream(p_getIntStream_1_);
   }

   public INBT createIntList(IntStream p_createIntList_1_) {
      return new IntArrayNBT(p_createIntList_1_.toArray());
   }

   public Optional<LongStream> getLongStream(INBT p_getLongStream_1_) {
      return p_getLongStream_1_ instanceof LongArrayNBT ? Optional.of(Arrays.stream(((LongArrayNBT)p_getLongStream_1_).getAsLongArray())) : super.getLongStream(p_getLongStream_1_);
   }

   public INBT createLongList(LongStream p_createLongList_1_) {
      return new LongArrayNBT(p_createLongList_1_.toArray());
   }

   public INBT createList(Stream<INBT> p_createList_1_) {
      PeekingIterator<INBT> lvt_2_1_ = Iterators.peekingIterator(p_createList_1_.iterator());
      if (!lvt_2_1_.hasNext()) {
         return new ListNBT();
      } else {
         INBT lvt_3_1_ = (INBT)lvt_2_1_.peek();
         ArrayList lvt_4_3_;
         if (lvt_3_1_ instanceof ByteNBT) {
            lvt_4_3_ = Lists.newArrayList(Iterators.transform(lvt_2_1_, (p_210815_0_) -> {
               return ((ByteNBT)p_210815_0_).getByte();
            }));
            return new ByteArrayNBT(lvt_4_3_);
         } else if (lvt_3_1_ instanceof IntNBT) {
            lvt_4_3_ = Lists.newArrayList(Iterators.transform(lvt_2_1_, (p_210818_0_) -> {
               return ((IntNBT)p_210818_0_).getInt();
            }));
            return new IntArrayNBT(lvt_4_3_);
         } else if (lvt_3_1_ instanceof LongNBT) {
            lvt_4_3_ = Lists.newArrayList(Iterators.transform(lvt_2_1_, (p_210816_0_) -> {
               return ((LongNBT)p_210816_0_).getLong();
            }));
            return new LongArrayNBT(lvt_4_3_);
         } else {
            ListNBT lvt_4_4_ = new ListNBT();

            while(lvt_2_1_.hasNext()) {
               INBT lvt_5_1_ = (INBT)lvt_2_1_.next();
               if (!(lvt_5_1_ instanceof EndNBT)) {
                  lvt_4_4_.add(lvt_5_1_);
               }
            }

            return lvt_4_4_;
         }
      }
   }

   public INBT remove(INBT p_remove_1_, String p_remove_2_) {
      if (p_remove_1_ instanceof CompoundNBT) {
         CompoundNBT lvt_3_1_ = (CompoundNBT)p_remove_1_;
         CompoundNBT lvt_4_1_ = new CompoundNBT();
         lvt_3_1_.keySet().stream().filter((p_212019_1_) -> {
            return !Objects.equals(p_212019_1_, p_remove_2_);
         }).forEach((p_212010_2_) -> {
            lvt_4_1_.put(p_212010_2_, lvt_3_1_.get(p_212010_2_));
         });
         return lvt_4_1_;
      } else {
         return p_remove_1_;
      }
   }

   public String toString() {
      return "NBT";
   }

   // $FF: synthetic method
   public Object remove(Object p_remove_1_, String p_remove_2_) {
      return this.remove((INBT)p_remove_1_, p_remove_2_);
   }

   // $FF: synthetic method
   public Object createLongList(LongStream p_createLongList_1_) {
      return this.createLongList(p_createLongList_1_);
   }

   // $FF: synthetic method
   public Optional getLongStream(Object p_getLongStream_1_) {
      return this.getLongStream((INBT)p_getLongStream_1_);
   }

   // $FF: synthetic method
   public Object createIntList(IntStream p_createIntList_1_) {
      return this.createIntList(p_createIntList_1_);
   }

   // $FF: synthetic method
   public Optional getIntStream(Object p_getIntStream_1_) {
      return this.getIntStream((INBT)p_getIntStream_1_);
   }

   // $FF: synthetic method
   public Object createByteList(ByteBuffer p_createByteList_1_) {
      return this.createByteList(p_createByteList_1_);
   }

   // $FF: synthetic method
   public Optional getByteBuffer(Object p_getByteBuffer_1_) {
      return this.getByteBuffer((INBT)p_getByteBuffer_1_);
   }

   // $FF: synthetic method
   public Object createList(Stream p_createList_1_) {
      return this.createList(p_createList_1_);
   }

   // $FF: synthetic method
   public Optional getStream(Object p_getStream_1_) {
      return this.getStream((INBT)p_getStream_1_);
   }

   // $FF: synthetic method
   public Object createMap(Map p_createMap_1_) {
      return this.createMap(p_createMap_1_);
   }

   // $FF: synthetic method
   public Optional getMapValues(Object p_getMapValues_1_) {
      return this.getMapValues((INBT)p_getMapValues_1_);
   }

   // $FF: synthetic method
   public Object merge(Object p_merge_1_, Object p_merge_2_) {
      return this.merge((INBT)p_merge_1_, (INBT)p_merge_2_);
   }

   // $FF: synthetic method
   public Object mergeInto(Object p_mergeInto_1_, Object p_mergeInto_2_, Object p_mergeInto_3_) {
      return this.mergeInto((INBT)p_mergeInto_1_, (INBT)p_mergeInto_2_, (INBT)p_mergeInto_3_);
   }

   // $FF: synthetic method
   public Object mergeInto(Object p_mergeInto_1_, Object p_mergeInto_2_) {
      return this.mergeInto((INBT)p_mergeInto_1_, (INBT)p_mergeInto_2_);
   }

   // $FF: synthetic method
   public Object createString(String p_createString_1_) {
      return this.createString(p_createString_1_);
   }

   // $FF: synthetic method
   public Optional getStringValue(Object p_getStringValue_1_) {
      return this.getStringValue((INBT)p_getStringValue_1_);
   }

   // $FF: synthetic method
   public Object createBoolean(boolean p_createBoolean_1_) {
      return this.createBoolean(p_createBoolean_1_);
   }

   // $FF: synthetic method
   public Object createDouble(double p_createDouble_1_) {
      return this.createDouble(p_createDouble_1_);
   }

   // $FF: synthetic method
   public Object createFloat(float p_createFloat_1_) {
      return this.createFloat(p_createFloat_1_);
   }

   // $FF: synthetic method
   public Object createLong(long p_createLong_1_) {
      return this.createLong(p_createLong_1_);
   }

   // $FF: synthetic method
   public Object createInt(int p_createInt_1_) {
      return this.createInt(p_createInt_1_);
   }

   // $FF: synthetic method
   public Object createShort(short p_createShort_1_) {
      return this.createShort(p_createShort_1_);
   }

   // $FF: synthetic method
   public Object createByte(byte p_createByte_1_) {
      return this.createByte(p_createByte_1_);
   }

   // $FF: synthetic method
   public Object createNumeric(Number p_createNumeric_1_) {
      return this.createNumeric(p_createNumeric_1_);
   }

   // $FF: synthetic method
   public Optional getNumberValue(Object p_getNumberValue_1_) {
      return this.getNumberValue((INBT)p_getNumberValue_1_);
   }

   // $FF: synthetic method
   public Type getType(Object p_getType_1_) {
      return this.getType((INBT)p_getType_1_);
   }

   // $FF: synthetic method
   public Object empty() {
      return this.empty();
   }
}
