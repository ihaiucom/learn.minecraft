package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class TextComponent implements ITextComponent {
   protected final List<ITextComponent> siblings = Lists.newArrayList();
   private Style style;

   public ITextComponent appendSibling(ITextComponent p_150257_1_) {
      p_150257_1_.getStyle().setParentStyle(this.getStyle());
      this.siblings.add(p_150257_1_);
      return this;
   }

   public List<ITextComponent> getSiblings() {
      return this.siblings;
   }

   public ITextComponent setStyle(Style p_150255_1_) {
      this.style = p_150255_1_;
      Iterator var2 = this.siblings.iterator();

      while(var2.hasNext()) {
         ITextComponent lvt_3_1_ = (ITextComponent)var2.next();
         lvt_3_1_.getStyle().setParentStyle(this.getStyle());
      }

      return this;
   }

   public Style getStyle() {
      if (this.style == null) {
         this.style = new Style();
         Iterator var1 = this.siblings.iterator();

         while(var1.hasNext()) {
            ITextComponent lvt_2_1_ = (ITextComponent)var1.next();
            lvt_2_1_.getStyle().setParentStyle(this.style);
         }
      }

      return this.style;
   }

   public Stream<ITextComponent> stream() {
      return Streams.concat(new Stream[]{Stream.of(this), this.siblings.stream().flatMap(ITextComponent::stream)});
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponent)) {
         return false;
      } else {
         TextComponent lvt_2_1_ = (TextComponent)p_equals_1_;
         return this.siblings.equals(lvt_2_1_.siblings) && this.getStyle().equals(lvt_2_1_.getStyle());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getStyle(), this.siblings});
   }

   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}
