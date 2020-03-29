package net.minecraft.client.settings;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BooleanOption extends AbstractOption {
   private final Predicate<GameSettings> getter;
   private final BiConsumer<GameSettings, Boolean> setter;

   public BooleanOption(String p_i51167_1_, Predicate<GameSettings> p_i51167_2_, BiConsumer<GameSettings, Boolean> p_i51167_3_) {
      super(p_i51167_1_);
      this.getter = p_i51167_2_;
      this.setter = p_i51167_3_;
   }

   public void set(GameSettings p_216742_1_, String p_216742_2_) {
      this.set(p_216742_1_, "true".equals(p_216742_2_));
   }

   public void func_216740_a(GameSettings p_216740_1_) {
      this.set(p_216740_1_, !this.get(p_216740_1_));
      p_216740_1_.saveOptions();
   }

   private void set(GameSettings p_216744_1_, boolean p_216744_2_) {
      this.setter.accept(p_216744_1_, p_216744_2_);
   }

   public boolean get(GameSettings p_216741_1_) {
      return this.getter.test(p_216741_1_);
   }

   public Widget createWidget(GameSettings p_216586_1_, int p_216586_2_, int p_216586_3_, int p_216586_4_) {
      return new OptionButton(p_216586_2_, p_216586_3_, p_216586_4_, 20, this, this.func_216743_c(p_216586_1_), (p_216745_2_) -> {
         this.func_216740_a(p_216586_1_);
         p_216745_2_.setMessage(this.func_216743_c(p_216586_1_));
      });
   }

   public String func_216743_c(GameSettings p_216743_1_) {
      return this.getDisplayString() + I18n.format(this.get(p_216743_1_) ? "options.on" : "options.off");
   }
}
