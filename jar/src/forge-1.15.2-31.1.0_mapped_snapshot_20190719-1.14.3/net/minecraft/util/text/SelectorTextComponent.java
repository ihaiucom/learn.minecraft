package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectorTextComponent extends TextComponent implements ITargetedTextComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String selector;
   @Nullable
   private final EntitySelector field_197670_d;

   public SelectorTextComponent(String p_i45996_1_) {
      this.selector = p_i45996_1_;
      EntitySelector lvt_2_1_ = null;

      try {
         EntitySelectorParser lvt_3_1_ = new EntitySelectorParser(new StringReader(p_i45996_1_));
         lvt_2_1_ = lvt_3_1_.parse();
      } catch (CommandSyntaxException var4) {
         LOGGER.warn("Invalid selector component: {}", p_i45996_1_, var4.getMessage());
      }

      this.field_197670_d = lvt_2_1_;
   }

   public String getSelector() {
      return this.selector;
   }

   public ITextComponent createNames(@Nullable CommandSource p_197668_1_, @Nullable Entity p_197668_2_, int p_197668_3_) throws CommandSyntaxException {
      return (ITextComponent)(p_197668_1_ != null && this.field_197670_d != null ? EntitySelector.joinNames(this.field_197670_d.select(p_197668_1_)) : new StringTextComponent(""));
   }

   public String getUnformattedComponentText() {
      return this.selector;
   }

   public SelectorTextComponent shallowCopy() {
      return new SelectorTextComponent(this.selector);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof SelectorTextComponent)) {
         return false;
      } else {
         SelectorTextComponent lvt_2_1_ = (SelectorTextComponent)p_equals_1_;
         return this.selector.equals(lvt_2_1_.selector) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   // $FF: synthetic method
   public ITextComponent shallowCopy() {
      return this.shallowCopy();
   }
}
