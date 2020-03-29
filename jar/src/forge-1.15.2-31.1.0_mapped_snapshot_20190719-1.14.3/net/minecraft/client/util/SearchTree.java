package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SearchTree<T> extends SearchTreeReloadable<T> {
   protected SuffixArray<T> byName = new SuffixArray();
   private final Function<T, Stream<String>> nameFunc;

   public SearchTree(Function<T, Stream<String>> p_i47612_1_, Function<T, Stream<ResourceLocation>> p_i47612_2_) {
      super(p_i47612_2_);
      this.nameFunc = p_i47612_1_;
   }

   public void recalculate() {
      this.byName = new SuffixArray();
      super.recalculate();
      this.byName.generate();
   }

   protected void index(T p_194042_1_) {
      super.index(p_194042_1_);
      ((Stream)this.nameFunc.apply(p_194042_1_)).forEach((p_217880_2_) -> {
         this.byName.add(p_194042_1_, p_217880_2_.toLowerCase(Locale.ROOT));
      });
   }

   public List<T> search(String p_194038_1_) {
      int lvt_2_1_ = p_194038_1_.indexOf(58);
      if (lvt_2_1_ < 0) {
         return this.byName.search(p_194038_1_);
      } else {
         List<T> lvt_3_1_ = this.field_217875_a.search(p_194038_1_.substring(0, lvt_2_1_).trim());
         String lvt_4_1_ = p_194038_1_.substring(lvt_2_1_ + 1).trim();
         List<T> lvt_5_1_ = this.field_217876_b.search(lvt_4_1_);
         List<T> lvt_6_1_ = this.byName.search(lvt_4_1_);
         return Lists.newArrayList(new SearchTreeReloadable.JoinedIterator(lvt_3_1_.iterator(), new SearchTree.MergingIterator(lvt_5_1_.iterator(), lvt_6_1_.iterator(), this::func_217874_a), this::func_217874_a));
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class MergingIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> leftItr;
      private final PeekingIterator<T> rightItr;
      private final Comparator<T> numbers;

      public MergingIterator(Iterator<T> p_i49977_1_, Iterator<T> p_i49977_2_, Comparator<T> p_i49977_3_) {
         this.leftItr = Iterators.peekingIterator(p_i49977_1_);
         this.rightItr = Iterators.peekingIterator(p_i49977_2_);
         this.numbers = p_i49977_3_;
      }

      protected T computeNext() {
         boolean lvt_1_1_ = !this.leftItr.hasNext();
         boolean lvt_2_1_ = !this.rightItr.hasNext();
         if (lvt_1_1_ && lvt_2_1_) {
            return this.endOfData();
         } else if (lvt_1_1_) {
            return this.rightItr.next();
         } else if (lvt_2_1_) {
            return this.leftItr.next();
         } else {
            int lvt_3_1_ = this.numbers.compare(this.leftItr.peek(), this.rightItr.peek());
            if (lvt_3_1_ == 0) {
               this.rightItr.next();
            }

            return lvt_3_1_ <= 0 ? this.leftItr.next() : this.rightItr.next();
         }
      }
   }
}
