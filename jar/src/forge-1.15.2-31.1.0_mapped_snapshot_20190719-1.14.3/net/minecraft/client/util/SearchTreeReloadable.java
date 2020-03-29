package net.minecraft.client.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
public class SearchTreeReloadable<T> implements IMutableSearchTree<T> {
   protected SuffixArray<T> field_217875_a = new SuffixArray();
   protected SuffixArray<T> field_217876_b = new SuffixArray();
   private final Function<T, Stream<ResourceLocation>> field_217877_c;
   private final List<T> field_217878_d = Lists.newArrayList();
   private final Object2IntMap<T> field_217879_e = new Object2IntOpenHashMap();

   public SearchTreeReloadable(Function<T, Stream<ResourceLocation>> p_i50896_1_) {
      this.field_217877_c = p_i50896_1_;
   }

   public void recalculate() {
      this.field_217875_a = new SuffixArray();
      this.field_217876_b = new SuffixArray();
      Iterator var1 = this.field_217878_d.iterator();

      while(var1.hasNext()) {
         T lvt_2_1_ = var1.next();
         this.index(lvt_2_1_);
      }

      this.field_217875_a.generate();
      this.field_217876_b.generate();
   }

   public void func_217872_a(T p_217872_1_) {
      this.field_217879_e.put(p_217872_1_, this.field_217878_d.size());
      this.field_217878_d.add(p_217872_1_);
      this.index(p_217872_1_);
   }

   public void func_217871_a() {
      this.field_217878_d.clear();
      this.field_217879_e.clear();
   }

   protected void index(T p_194042_1_) {
      ((Stream)this.field_217877_c.apply(p_194042_1_)).forEach((p_217873_2_) -> {
         this.field_217875_a.add(p_194042_1_, p_217873_2_.getNamespace().toLowerCase(Locale.ROOT));
         this.field_217876_b.add(p_194042_1_, p_217873_2_.getPath().toLowerCase(Locale.ROOT));
      });
   }

   protected int func_217874_a(T p_217874_1_, T p_217874_2_) {
      return Integer.compare(this.field_217879_e.getInt(p_217874_1_), this.field_217879_e.getInt(p_217874_2_));
   }

   public List<T> search(String p_194038_1_) {
      int lvt_2_1_ = p_194038_1_.indexOf(58);
      if (lvt_2_1_ == -1) {
         return this.field_217876_b.search(p_194038_1_);
      } else {
         List<T> lvt_3_1_ = this.field_217875_a.search(p_194038_1_.substring(0, lvt_2_1_).trim());
         String lvt_4_1_ = p_194038_1_.substring(lvt_2_1_ + 1).trim();
         List<T> lvt_5_1_ = this.field_217876_b.search(lvt_4_1_);
         return Lists.newArrayList(new SearchTreeReloadable.JoinedIterator(lvt_3_1_.iterator(), lvt_5_1_.iterator(), this::func_217874_a));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class JoinedIterator<T> extends AbstractIterator<T> {
      private final PeekingIterator<T> field_217881_a;
      private final PeekingIterator<T> field_217882_b;
      private final Comparator<T> field_217883_c;

      public JoinedIterator(Iterator<T> p_i50270_1_, Iterator<T> p_i50270_2_, Comparator<T> p_i50270_3_) {
         this.field_217881_a = Iterators.peekingIterator(p_i50270_1_);
         this.field_217882_b = Iterators.peekingIterator(p_i50270_2_);
         this.field_217883_c = p_i50270_3_;
      }

      protected T computeNext() {
         while(this.field_217881_a.hasNext() && this.field_217882_b.hasNext()) {
            int lvt_1_1_ = this.field_217883_c.compare(this.field_217881_a.peek(), this.field_217882_b.peek());
            if (lvt_1_1_ == 0) {
               this.field_217882_b.next();
               return this.field_217881_a.next();
            }

            if (lvt_1_1_ < 0) {
               this.field_217881_a.next();
            } else {
               this.field_217882_b.next();
            }
         }

         return this.endOfData();
      }
   }
}
