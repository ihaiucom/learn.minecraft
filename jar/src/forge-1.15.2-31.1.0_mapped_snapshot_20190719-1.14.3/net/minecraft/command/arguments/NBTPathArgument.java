package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NBTPathArgument implements ArgumentType<NBTPathArgument.NBTPath> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}");
   public static final SimpleCommandExceptionType PATH_MALFORMED = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.nbtpath.node.invalid", new Object[0]));
   public static final DynamicCommandExceptionType field_218084_b = new DynamicCommandExceptionType((p_208665_0_) -> {
      return new TranslationTextComponent("arguments.nbtpath.nothing_found", new Object[]{p_208665_0_});
   });

   public static NBTPathArgument nbtPath() {
      return new NBTPathArgument();
   }

   public static NBTPathArgument.NBTPath getNBTPath(CommandContext<CommandSource> p_197148_0_, String p_197148_1_) {
      return (NBTPathArgument.NBTPath)p_197148_0_.getArgument(p_197148_1_, NBTPathArgument.NBTPath.class);
   }

   public NBTPathArgument.NBTPath parse(StringReader p_parse_1_) throws CommandSyntaxException {
      List<NBTPathArgument.INode> lvt_2_1_ = Lists.newArrayList();
      int lvt_3_1_ = p_parse_1_.getCursor();
      Object2IntMap<NBTPathArgument.INode> lvt_4_1_ = new Object2IntOpenHashMap();
      boolean lvt_5_1_ = true;

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         NBTPathArgument.INode lvt_6_1_ = func_218079_a(p_parse_1_, lvt_5_1_);
         lvt_2_1_.add(lvt_6_1_);
         lvt_4_1_.put(lvt_6_1_, p_parse_1_.getCursor() - lvt_3_1_);
         lvt_5_1_ = false;
         if (p_parse_1_.canRead()) {
            char lvt_7_1_ = p_parse_1_.peek();
            if (lvt_7_1_ != ' ' && lvt_7_1_ != '[' && lvt_7_1_ != '{') {
               p_parse_1_.expect('.');
            }
         }
      }

      return new NBTPathArgument.NBTPath(p_parse_1_.getString().substring(lvt_3_1_, p_parse_1_.getCursor()), (NBTPathArgument.INode[])lvt_2_1_.toArray(new NBTPathArgument.INode[0]), lvt_4_1_);
   }

   private static NBTPathArgument.INode func_218079_a(StringReader p_218079_0_, boolean p_218079_1_) throws CommandSyntaxException {
      String lvt_2_3_;
      switch(p_218079_0_.peek()) {
      case '"':
         lvt_2_3_ = p_218079_0_.readString();
         return func_218083_a(p_218079_0_, lvt_2_3_);
      case '[':
         p_218079_0_.skip();
         int lvt_2_2_ = p_218079_0_.peek();
         if (lvt_2_2_ == '{') {
            CompoundNBT lvt_3_1_ = (new JsonToNBT(p_218079_0_)).readStruct();
            p_218079_0_.expect(']');
            return new NBTPathArgument.ListNode(lvt_3_1_);
         } else {
            if (lvt_2_2_ == ']') {
               p_218079_0_.skip();
               return NBTPathArgument.EmptyListNode.field_218067_a;
            }

            int lvt_3_2_ = p_218079_0_.readInt();
            p_218079_0_.expect(']');
            return new NBTPathArgument.CollectionNode(lvt_3_2_);
         }
      case '{':
         if (!p_218079_1_) {
            throw PATH_MALFORMED.createWithContext(p_218079_0_);
         }

         CompoundNBT lvt_2_1_ = (new JsonToNBT(p_218079_0_)).readStruct();
         return new NBTPathArgument.CompoundNode(lvt_2_1_);
      default:
         lvt_2_3_ = readTagName(p_218079_0_);
         return func_218083_a(p_218079_0_, lvt_2_3_);
      }
   }

   private static NBTPathArgument.INode func_218083_a(StringReader p_218083_0_, String p_218083_1_) throws CommandSyntaxException {
      if (p_218083_0_.canRead() && p_218083_0_.peek() == '{') {
         CompoundNBT lvt_2_1_ = (new JsonToNBT(p_218083_0_)).readStruct();
         return new NBTPathArgument.JsonNode(p_218083_1_, lvt_2_1_);
      } else {
         return new NBTPathArgument.StringNode(p_218083_1_);
      }
   }

   private static String readTagName(StringReader p_197151_0_) throws CommandSyntaxException {
      int lvt_1_1_ = p_197151_0_.getCursor();

      while(p_197151_0_.canRead() && isSimpleNameChar(p_197151_0_.peek())) {
         p_197151_0_.skip();
      }

      if (p_197151_0_.getCursor() == lvt_1_1_) {
         throw PATH_MALFORMED.createWithContext(p_197151_0_);
      } else {
         return p_197151_0_.getString().substring(lvt_1_1_, p_197151_0_.getCursor());
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static boolean isSimpleNameChar(char p_197146_0_) {
      return p_197146_0_ != ' ' && p_197146_0_ != '"' && p_197146_0_ != '[' && p_197146_0_ != ']' && p_197146_0_ != '.' && p_197146_0_ != '{' && p_197146_0_ != '}';
   }

   private static Predicate<INBT> func_218080_b(CompoundNBT p_218080_0_) {
      return (p_218081_1_) -> {
         return NBTUtil.areNBTEquals(p_218080_0_, p_218081_1_, true);
      };
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }

   static class CompoundNode implements NBTPathArgument.INode {
      private final Predicate<INBT> field_218066_a;

      public CompoundNode(CompoundNBT p_i51149_1_) {
         this.field_218066_a = NBTPathArgument.func_218080_b(p_i51149_1_);
      }

      public void func_218050_a(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CompoundNBT && this.field_218066_a.test(p_218050_1_)) {
            p_218050_2_.add(p_218050_1_);
         }

      }

      public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         this.func_218050_a(p_218054_1_, p_218054_3_);
      }

      public INBT createEmptyElement() {
         return new CompoundNBT();
      }

      public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         return 0;
      }

      public int func_218053_a(INBT p_218053_1_) {
         return 0;
      }
   }

   static class JsonNode implements NBTPathArgument.INode {
      private final String field_218063_a;
      private final CompoundNBT field_218064_b;
      private final Predicate<INBT> field_218065_c;

      public JsonNode(String p_i51150_1_, CompoundNBT p_i51150_2_) {
         this.field_218063_a = p_i51150_1_;
         this.field_218064_b = p_i51150_2_;
         this.field_218065_c = NBTPathArgument.func_218080_b(p_i51150_2_);
      }

      public void func_218050_a(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CompoundNBT) {
            INBT lvt_3_1_ = ((CompoundNBT)p_218050_1_).get(this.field_218063_a);
            if (this.field_218065_c.test(lvt_3_1_)) {
               p_218050_2_.add(lvt_3_1_);
            }
         }

      }

      public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         if (p_218054_1_ instanceof CompoundNBT) {
            CompoundNBT lvt_4_1_ = (CompoundNBT)p_218054_1_;
            INBT lvt_5_1_ = lvt_4_1_.get(this.field_218063_a);
            if (lvt_5_1_ == null) {
               INBT lvt_5_1_ = this.field_218064_b.copy();
               lvt_4_1_.put(this.field_218063_a, lvt_5_1_);
               p_218054_3_.add(lvt_5_1_);
            } else if (this.field_218065_c.test(lvt_5_1_)) {
               p_218054_3_.add(lvt_5_1_);
            }
         }

      }

      public INBT createEmptyElement() {
         return new CompoundNBT();
      }

      public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         if (p_218051_1_ instanceof CompoundNBT) {
            CompoundNBT lvt_3_1_ = (CompoundNBT)p_218051_1_;
            INBT lvt_4_1_ = lvt_3_1_.get(this.field_218063_a);
            if (this.field_218065_c.test(lvt_4_1_)) {
               INBT lvt_5_1_ = (INBT)p_218051_2_.get();
               if (!lvt_5_1_.equals(lvt_4_1_)) {
                  lvt_3_1_.put(this.field_218063_a, lvt_5_1_);
                  return 1;
               }
            }
         }

         return 0;
      }

      public int func_218053_a(INBT p_218053_1_) {
         if (p_218053_1_ instanceof CompoundNBT) {
            CompoundNBT lvt_2_1_ = (CompoundNBT)p_218053_1_;
            INBT lvt_3_1_ = lvt_2_1_.get(this.field_218063_a);
            if (this.field_218065_c.test(lvt_3_1_)) {
               lvt_2_1_.remove(this.field_218063_a);
               return 1;
            }
         }

         return 0;
      }
   }

   static class EmptyListNode implements NBTPathArgument.INode {
      public static final NBTPathArgument.EmptyListNode field_218067_a = new NBTPathArgument.EmptyListNode();

      private EmptyListNode() {
      }

      public void func_218050_a(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CollectionNBT) {
            p_218050_2_.addAll((CollectionNBT)p_218050_1_);
         }

      }

      public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         if (p_218054_1_ instanceof CollectionNBT) {
            CollectionNBT<?> lvt_4_1_ = (CollectionNBT)p_218054_1_;
            if (lvt_4_1_.isEmpty()) {
               INBT lvt_5_1_ = (INBT)p_218054_2_.get();
               if (lvt_4_1_.func_218660_b(0, lvt_5_1_)) {
                  p_218054_3_.add(lvt_5_1_);
               }
            } else {
               p_218054_3_.addAll(lvt_4_1_);
            }
         }

      }

      public INBT createEmptyElement() {
         return new ListNBT();
      }

      public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         if (!(p_218051_1_ instanceof CollectionNBT)) {
            return 0;
         } else {
            CollectionNBT<?> lvt_3_1_ = (CollectionNBT)p_218051_1_;
            int lvt_4_1_ = lvt_3_1_.size();
            if (lvt_4_1_ == 0) {
               lvt_3_1_.func_218660_b(0, (INBT)p_218051_2_.get());
               return 1;
            } else {
               INBT lvt_5_1_ = (INBT)p_218051_2_.get();
               Stream var10001 = lvt_3_1_.stream();
               lvt_5_1_.getClass();
               int lvt_6_1_ = lvt_4_1_ - (int)var10001.filter(lvt_5_1_::equals).count();
               if (lvt_6_1_ == 0) {
                  return 0;
               } else {
                  lvt_3_1_.clear();
                  if (!lvt_3_1_.func_218660_b(0, lvt_5_1_)) {
                     return 0;
                  } else {
                     for(int lvt_7_1_ = 1; lvt_7_1_ < lvt_4_1_; ++lvt_7_1_) {
                        lvt_3_1_.func_218660_b(lvt_7_1_, (INBT)p_218051_2_.get());
                     }

                     return lvt_6_1_;
                  }
               }
            }
         }
      }

      public int func_218053_a(INBT p_218053_1_) {
         if (p_218053_1_ instanceof CollectionNBT) {
            CollectionNBT<?> lvt_2_1_ = (CollectionNBT)p_218053_1_;
            int lvt_3_1_ = lvt_2_1_.size();
            if (lvt_3_1_ > 0) {
               lvt_2_1_.clear();
               return lvt_3_1_;
            }
         }

         return 0;
      }
   }

   static class ListNode implements NBTPathArgument.INode {
      private final CompoundNBT field_218061_a;
      private final Predicate<INBT> field_218062_b;

      public ListNode(CompoundNBT p_i51151_1_) {
         this.field_218061_a = p_i51151_1_;
         this.field_218062_b = NBTPathArgument.func_218080_b(p_i51151_1_);
      }

      public void func_218050_a(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof ListNBT) {
            ListNBT lvt_3_1_ = (ListNBT)p_218050_1_;
            lvt_3_1_.stream().filter(this.field_218062_b).forEach(p_218050_2_::add);
         }

      }

      public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         MutableBoolean lvt_4_1_ = new MutableBoolean();
         if (p_218054_1_ instanceof ListNBT) {
            ListNBT lvt_5_1_ = (ListNBT)p_218054_1_;
            lvt_5_1_.stream().filter(this.field_218062_b).forEach((p_218060_2_) -> {
               p_218054_3_.add(p_218060_2_);
               lvt_4_1_.setTrue();
            });
            if (lvt_4_1_.isFalse()) {
               CompoundNBT lvt_6_1_ = this.field_218061_a.copy();
               lvt_5_1_.add(lvt_6_1_);
               p_218054_3_.add(lvt_6_1_);
            }
         }

      }

      public INBT createEmptyElement() {
         return new ListNBT();
      }

      public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         int lvt_3_1_ = 0;
         if (p_218051_1_ instanceof ListNBT) {
            ListNBT lvt_4_1_ = (ListNBT)p_218051_1_;
            int lvt_5_1_ = lvt_4_1_.size();
            if (lvt_5_1_ == 0) {
               lvt_4_1_.add(p_218051_2_.get());
               ++lvt_3_1_;
            } else {
               for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_; ++lvt_6_1_) {
                  INBT lvt_7_1_ = lvt_4_1_.get(lvt_6_1_);
                  if (this.field_218062_b.test(lvt_7_1_)) {
                     INBT lvt_8_1_ = (INBT)p_218051_2_.get();
                     if (!lvt_8_1_.equals(lvt_7_1_) && lvt_4_1_.func_218659_a(lvt_6_1_, lvt_8_1_)) {
                        ++lvt_3_1_;
                     }
                  }
               }
            }
         }

         return lvt_3_1_;
      }

      public int func_218053_a(INBT p_218053_1_) {
         int lvt_2_1_ = 0;
         if (p_218053_1_ instanceof ListNBT) {
            ListNBT lvt_3_1_ = (ListNBT)p_218053_1_;

            for(int lvt_4_1_ = lvt_3_1_.size() - 1; lvt_4_1_ >= 0; --lvt_4_1_) {
               if (this.field_218062_b.test(lvt_3_1_.get(lvt_4_1_))) {
                  lvt_3_1_.remove(lvt_4_1_);
                  ++lvt_2_1_;
               }
            }
         }

         return lvt_2_1_;
      }
   }

   static class CollectionNode implements NBTPathArgument.INode {
      private final int field_218059_a;

      public CollectionNode(int p_i51153_1_) {
         this.field_218059_a = p_i51153_1_;
      }

      public void func_218050_a(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CollectionNBT) {
            CollectionNBT<?> lvt_3_1_ = (CollectionNBT)p_218050_1_;
            int lvt_4_1_ = lvt_3_1_.size();
            int lvt_5_1_ = this.field_218059_a < 0 ? lvt_4_1_ + this.field_218059_a : this.field_218059_a;
            if (0 <= lvt_5_1_ && lvt_5_1_ < lvt_4_1_) {
               p_218050_2_.add(lvt_3_1_.get(lvt_5_1_));
            }
         }

      }

      public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         this.func_218050_a(p_218054_1_, p_218054_3_);
      }

      public INBT createEmptyElement() {
         return new ListNBT();
      }

      public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         if (p_218051_1_ instanceof CollectionNBT) {
            CollectionNBT<?> lvt_3_1_ = (CollectionNBT)p_218051_1_;
            int lvt_4_1_ = lvt_3_1_.size();
            int lvt_5_1_ = this.field_218059_a < 0 ? lvt_4_1_ + this.field_218059_a : this.field_218059_a;
            if (0 <= lvt_5_1_ && lvt_5_1_ < lvt_4_1_) {
               INBT lvt_6_1_ = (INBT)lvt_3_1_.get(lvt_5_1_);
               INBT lvt_7_1_ = (INBT)p_218051_2_.get();
               if (!lvt_7_1_.equals(lvt_6_1_) && lvt_3_1_.func_218659_a(lvt_5_1_, lvt_7_1_)) {
                  return 1;
               }
            }
         }

         return 0;
      }

      public int func_218053_a(INBT p_218053_1_) {
         if (p_218053_1_ instanceof CollectionNBT) {
            CollectionNBT<?> lvt_2_1_ = (CollectionNBT)p_218053_1_;
            int lvt_3_1_ = lvt_2_1_.size();
            int lvt_4_1_ = this.field_218059_a < 0 ? lvt_3_1_ + this.field_218059_a : this.field_218059_a;
            if (0 <= lvt_4_1_ && lvt_4_1_ < lvt_3_1_) {
               lvt_2_1_.remove(lvt_4_1_);
               return 1;
            }
         }

         return 0;
      }
   }

   static class StringNode implements NBTPathArgument.INode {
      private final String field_218058_a;

      public StringNode(String p_i51154_1_) {
         this.field_218058_a = p_i51154_1_;
      }

      public void func_218050_a(INBT p_218050_1_, List<INBT> p_218050_2_) {
         if (p_218050_1_ instanceof CompoundNBT) {
            INBT lvt_3_1_ = ((CompoundNBT)p_218050_1_).get(this.field_218058_a);
            if (lvt_3_1_ != null) {
               p_218050_2_.add(lvt_3_1_);
            }
         }

      }

      public void func_218054_a(INBT p_218054_1_, Supplier<INBT> p_218054_2_, List<INBT> p_218054_3_) {
         if (p_218054_1_ instanceof CompoundNBT) {
            CompoundNBT lvt_4_1_ = (CompoundNBT)p_218054_1_;
            INBT lvt_5_2_;
            if (lvt_4_1_.contains(this.field_218058_a)) {
               lvt_5_2_ = lvt_4_1_.get(this.field_218058_a);
            } else {
               lvt_5_2_ = (INBT)p_218054_2_.get();
               lvt_4_1_.put(this.field_218058_a, lvt_5_2_);
            }

            p_218054_3_.add(lvt_5_2_);
         }

      }

      public INBT createEmptyElement() {
         return new CompoundNBT();
      }

      public int func_218051_a(INBT p_218051_1_, Supplier<INBT> p_218051_2_) {
         if (p_218051_1_ instanceof CompoundNBT) {
            CompoundNBT lvt_3_1_ = (CompoundNBT)p_218051_1_;
            INBT lvt_4_1_ = (INBT)p_218051_2_.get();
            INBT lvt_5_1_ = lvt_3_1_.put(this.field_218058_a, lvt_4_1_);
            if (!lvt_4_1_.equals(lvt_5_1_)) {
               return 1;
            }
         }

         return 0;
      }

      public int func_218053_a(INBT p_218053_1_) {
         if (p_218053_1_ instanceof CompoundNBT) {
            CompoundNBT lvt_2_1_ = (CompoundNBT)p_218053_1_;
            if (lvt_2_1_.contains(this.field_218058_a)) {
               lvt_2_1_.remove(this.field_218058_a);
               return 1;
            }
         }

         return 0;
      }
   }

   interface INode {
      void func_218050_a(INBT var1, List<INBT> var2);

      void func_218054_a(INBT var1, Supplier<INBT> var2, List<INBT> var3);

      INBT createEmptyElement();

      int func_218051_a(INBT var1, Supplier<INBT> var2);

      int func_218053_a(INBT var1);

      default List<INBT> func_218056_a(List<INBT> p_218056_1_) {
         return this.func_218057_a(p_218056_1_, this::func_218050_a);
      }

      default List<INBT> func_218052_a(List<INBT> p_218052_1_, Supplier<INBT> p_218052_2_) {
         return this.func_218057_a(p_218052_1_, (p_218055_2_, p_218055_3_) -> {
            this.func_218054_a(p_218055_2_, p_218052_2_, p_218055_3_);
         });
      }

      default List<INBT> func_218057_a(List<INBT> p_218057_1_, BiConsumer<INBT, List<INBT>> p_218057_2_) {
         List<INBT> lvt_3_1_ = Lists.newArrayList();
         Iterator var4 = p_218057_1_.iterator();

         while(var4.hasNext()) {
            INBT lvt_5_1_ = (INBT)var4.next();
            p_218057_2_.accept(lvt_5_1_, lvt_3_1_);
         }

         return lvt_3_1_;
      }
   }

   public static class NBTPath {
      private final String rawText;
      private final Object2IntMap<NBTPathArgument.INode> field_218078_b;
      private final NBTPathArgument.INode[] nodes;

      public NBTPath(String p_i51148_1_, NBTPathArgument.INode[] p_i51148_2_, Object2IntMap<NBTPathArgument.INode> p_i51148_3_) {
         this.rawText = p_i51148_1_;
         this.nodes = p_i51148_2_;
         this.field_218078_b = p_i51148_3_;
      }

      public List<INBT> func_218071_a(INBT p_218071_1_) throws CommandSyntaxException {
         List<INBT> lvt_2_1_ = Collections.singletonList(p_218071_1_);
         NBTPathArgument.INode[] var3 = this.nodes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            NBTPathArgument.INode lvt_6_1_ = var3[var5];
            lvt_2_1_ = lvt_6_1_.func_218056_a(lvt_2_1_);
            if (lvt_2_1_.isEmpty()) {
               throw this.func_218070_a(lvt_6_1_);
            }
         }

         return lvt_2_1_;
      }

      public int func_218069_b(INBT p_218069_1_) {
         List<INBT> lvt_2_1_ = Collections.singletonList(p_218069_1_);
         NBTPathArgument.INode[] var3 = this.nodes;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            NBTPathArgument.INode lvt_6_1_ = var3[var5];
            lvt_2_1_ = lvt_6_1_.func_218056_a(lvt_2_1_);
            if (lvt_2_1_.isEmpty()) {
               return 0;
            }
         }

         return lvt_2_1_.size();
      }

      private List<INBT> func_218072_d(INBT p_218072_1_) throws CommandSyntaxException {
         List<INBT> lvt_2_1_ = Collections.singletonList(p_218072_1_);

         for(int lvt_3_1_ = 0; lvt_3_1_ < this.nodes.length - 1; ++lvt_3_1_) {
            NBTPathArgument.INode lvt_4_1_ = this.nodes[lvt_3_1_];
            int lvt_5_1_ = lvt_3_1_ + 1;
            NBTPathArgument.INode var10002 = this.nodes[lvt_5_1_];
            var10002.getClass();
            lvt_2_1_ = lvt_4_1_.func_218052_a(lvt_2_1_, var10002::createEmptyElement);
            if (lvt_2_1_.isEmpty()) {
               throw this.func_218070_a(lvt_4_1_);
            }
         }

         return lvt_2_1_;
      }

      public List<INBT> func_218073_a(INBT p_218073_1_, Supplier<INBT> p_218073_2_) throws CommandSyntaxException {
         List<INBT> lvt_3_1_ = this.func_218072_d(p_218073_1_);
         NBTPathArgument.INode lvt_4_1_ = this.nodes[this.nodes.length - 1];
         return lvt_4_1_.func_218052_a(lvt_3_1_, p_218073_2_);
      }

      private static int func_218075_a(List<INBT> p_218075_0_, Function<INBT, Integer> p_218075_1_) {
         return (Integer)p_218075_0_.stream().map(p_218075_1_).reduce(0, (p_218074_0_, p_218074_1_) -> {
            return p_218074_0_ + p_218074_1_;
         });
      }

      public int func_218076_b(INBT p_218076_1_, Supplier<INBT> p_218076_2_) throws CommandSyntaxException {
         List<INBT> lvt_3_1_ = this.func_218072_d(p_218076_1_);
         NBTPathArgument.INode lvt_4_1_ = this.nodes[this.nodes.length - 1];
         return func_218075_a(lvt_3_1_, (p_218077_2_) -> {
            return lvt_4_1_.func_218051_a(p_218077_2_, p_218076_2_);
         });
      }

      public int func_218068_c(INBT p_218068_1_) {
         List<INBT> lvt_2_1_ = Collections.singletonList(p_218068_1_);

         for(int lvt_3_1_ = 0; lvt_3_1_ < this.nodes.length - 1; ++lvt_3_1_) {
            lvt_2_1_ = this.nodes[lvt_3_1_].func_218056_a(lvt_2_1_);
         }

         NBTPathArgument.INode lvt_3_2_ = this.nodes[this.nodes.length - 1];
         lvt_3_2_.getClass();
         return func_218075_a(lvt_2_1_, lvt_3_2_::func_218053_a);
      }

      private CommandSyntaxException func_218070_a(NBTPathArgument.INode p_218070_1_) {
         int lvt_2_1_ = this.field_218078_b.getInt(p_218070_1_);
         return NBTPathArgument.field_218084_b.create(this.rawText.substring(0, lvt_2_1_));
      }

      public String toString() {
         return this.rawText;
      }
   }
}
