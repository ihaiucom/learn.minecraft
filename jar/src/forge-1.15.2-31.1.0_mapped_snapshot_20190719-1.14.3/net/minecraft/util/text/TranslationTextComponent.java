package net.minecraft.util.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.TextComponentMessageFormatHandler;

public class TranslationTextComponent extends TextComponent implements ITargetedTextComponent {
   private static final LanguageMap FALLBACK_LANGUAGE = new LanguageMap();
   private static final LanguageMap LOCAL_LANGUAGE = LanguageMap.getInstance();
   private final String key;
   private final Object[] formatArgs;
   private final Object syncLock = new Object();
   private long lastTranslationUpdateTimeInMilliseconds = -1L;
   protected final List<ITextComponent> children = Lists.newArrayList();
   public static final Pattern STRING_VARIABLE_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TranslationTextComponent(String p_i45160_1_, Object... p_i45160_2_) {
      this.key = p_i45160_1_;
      this.formatArgs = p_i45160_2_;

      for(int i = 0; i < p_i45160_2_.length; ++i) {
         Object object = p_i45160_2_[i];
         if (object instanceof ITextComponent) {
            ITextComponent itextcomponent = ((ITextComponent)object).deepCopy();
            this.formatArgs[i] = itextcomponent;
            itextcomponent.getStyle().setParentStyle(this.getStyle());
         } else if (object == null) {
            this.formatArgs[i] = "null";
         }
      }

   }

   @VisibleForTesting
   synchronized void ensureInitialized() {
      synchronized(this.syncLock) {
         long i = LOCAL_LANGUAGE.getLastUpdateTimeInMilliseconds();
         if (i == this.lastTranslationUpdateTimeInMilliseconds) {
            return;
         }

         this.lastTranslationUpdateTimeInMilliseconds = i;
         this.children.clear();
      }

      String s = LOCAL_LANGUAGE.translateKey(this.key);

      try {
         this.initializeFromFormat(s);
      } catch (TranslationTextComponentFormatException var5) {
         this.children.clear();
         this.children.add(new StringTextComponent(s));
      }

   }

   protected void initializeFromFormat(String p_150269_1_) {
      Matcher matcher = STRING_VARIABLE_PATTERN.matcher(p_150269_1_);

      try {
         int i = 0;

         int j;
         int l;
         for(j = 0; matcher.find(j); j = l) {
            int k = matcher.start();
            l = matcher.end();
            if (k > j) {
               ITextComponent itextcomponent = new StringTextComponent(String.format(p_150269_1_.substring(j, k)));
               itextcomponent.getStyle().setParentStyle(this.getStyle());
               this.children.add(itextcomponent);
            }

            String s2 = matcher.group(2);
            String s = p_150269_1_.substring(k, l);
            if ("%".equals(s2) && "%%".equals(s)) {
               ITextComponent itextcomponent2 = new StringTextComponent("%");
               itextcomponent2.getStyle().setParentStyle(this.getStyle());
               this.children.add(itextcomponent2);
            } else {
               if (!"s".equals(s2)) {
                  throw new TranslationTextComponentFormatException(this, "Unsupported format: '" + s + "'");
               }

               String s1 = matcher.group(1);
               int i1 = s1 != null ? Integer.parseInt(s1) - 1 : i++;
               if (i1 < this.formatArgs.length) {
                  this.children.add(this.getFormatArgumentAsComponent(i1));
               }
            }
         }

         if (j == 0) {
            j = TextComponentMessageFormatHandler.handle(this, this.children, this.formatArgs, p_150269_1_);
         }

         if (j < p_150269_1_.length()) {
            ITextComponent itextcomponent1 = new StringTextComponent(String.format(p_150269_1_.substring(j)));
            itextcomponent1.getStyle().setParentStyle(this.getStyle());
            this.children.add(itextcomponent1);
         }

      } catch (IllegalFormatException var11) {
         throw new TranslationTextComponentFormatException(this, var11);
      }
   }

   private ITextComponent getFormatArgumentAsComponent(int p_150272_1_) {
      if (p_150272_1_ >= this.formatArgs.length) {
         throw new TranslationTextComponentFormatException(this, p_150272_1_);
      } else {
         Object object = this.formatArgs[p_150272_1_];
         Object itextcomponent;
         if (object instanceof ITextComponent) {
            itextcomponent = (ITextComponent)object;
         } else {
            itextcomponent = new StringTextComponent(object == null ? "null" : object.toString());
            ((ITextComponent)itextcomponent).getStyle().setParentStyle(this.getStyle());
         }

         return (ITextComponent)itextcomponent;
      }
   }

   public ITextComponent setStyle(Style p_150255_1_) {
      super.setStyle(p_150255_1_);
      Object[] var2 = this.formatArgs;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object object = var2[var4];
         if (object instanceof ITextComponent) {
            ((ITextComponent)object).getStyle().setParentStyle(this.getStyle());
         }
      }

      if (this.lastTranslationUpdateTimeInMilliseconds > -1L) {
         Iterator var6 = this.children.iterator();

         while(var6.hasNext()) {
            ITextComponent itextcomponent = (ITextComponent)var6.next();
            itextcomponent.getStyle().setParentStyle(p_150255_1_);
         }
      }

      return this;
   }

   public Stream<ITextComponent> stream() {
      this.ensureInitialized();
      return Streams.concat(new Stream[]{this.children.stream(), this.siblings.stream()}).flatMap(ITextComponent::stream);
   }

   public String getUnformattedComponentText() {
      this.ensureInitialized();
      StringBuilder stringbuilder = new StringBuilder();
      Iterator var2 = this.children.iterator();

      while(var2.hasNext()) {
         ITextComponent itextcomponent = (ITextComponent)var2.next();
         stringbuilder.append(itextcomponent.getUnformattedComponentText());
      }

      return stringbuilder.toString();
   }

   public TranslationTextComponent shallowCopy() {
      Object[] aobject = new Object[this.formatArgs.length];

      for(int i = 0; i < this.formatArgs.length; ++i) {
         if (this.formatArgs[i] instanceof ITextComponent) {
            aobject[i] = ((ITextComponent)this.formatArgs[i]).deepCopy();
         } else {
            aobject[i] = this.formatArgs[i];
         }
      }

      return new TranslationTextComponent(this.key, aobject);
   }

   public ITextComponent createNames(@Nullable CommandSource p_197668_1_, @Nullable Entity p_197668_2_, int p_197668_3_) throws CommandSyntaxException {
      Object[] aobject = new Object[this.formatArgs.length];

      for(int i = 0; i < aobject.length; ++i) {
         Object object = this.formatArgs[i];
         if (object instanceof ITextComponent) {
            aobject[i] = TextComponentUtils.updateForEntity(p_197668_1_, (ITextComponent)object, p_197668_2_, p_197668_3_);
         } else {
            aobject[i] = object;
         }
      }

      return new TranslationTextComponent(this.key, aobject);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TranslationTextComponent)) {
         return false;
      } else {
         TranslationTextComponent translationtextcomponent = (TranslationTextComponent)p_equals_1_;
         return Arrays.equals(this.formatArgs, translationtextcomponent.formatArgs) && this.key.equals(translationtextcomponent.key) && super.equals(p_equals_1_);
      }
   }

   public int hashCode() {
      int i = super.hashCode();
      i = 31 * i + this.key.hashCode();
      i = 31 * i + Arrays.hashCode(this.formatArgs);
      return i;
   }

   public String toString() {
      return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.formatArgs) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getFormatArgs() {
      return this.formatArgs;
   }
}
