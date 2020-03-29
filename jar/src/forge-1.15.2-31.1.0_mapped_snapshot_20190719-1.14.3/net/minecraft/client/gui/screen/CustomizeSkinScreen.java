package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomizeSkinScreen extends SettingsScreen {
   public CustomizeSkinScreen(Screen p_i225931_1_, GameSettings p_i225931_2_) {
      super(p_i225931_1_, p_i225931_2_, new TranslationTextComponent("options.skinCustomisation.title", new Object[0]));
   }

   protected void init() {
      int lvt_1_1_ = 0;
      PlayerModelPart[] var2 = PlayerModelPart.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PlayerModelPart lvt_5_1_ = var2[var4];
         this.addButton(new Button(this.width / 2 - 155 + lvt_1_1_ % 2 * 160, this.height / 6 + 24 * (lvt_1_1_ >> 1), 150, 20, this.getMessage(lvt_5_1_), (p_213080_2_) -> {
            this.field_228183_b_.switchModelPartEnabled(lvt_5_1_);
            p_213080_2_.setMessage(this.getMessage(lvt_5_1_));
         }));
         ++lvt_1_1_;
      }

      this.addButton(new OptionButton(this.width / 2 - 155 + lvt_1_1_ % 2 * 160, this.height / 6 + 24 * (lvt_1_1_ >> 1), 150, 20, AbstractOption.MAIN_HAND, AbstractOption.MAIN_HAND.func_216720_c(this.field_228183_b_), (p_213081_1_) -> {
         AbstractOption.MAIN_HAND.func_216722_a(this.field_228183_b_, 1);
         this.field_228183_b_.saveOptions();
         p_213081_1_.setMessage(AbstractOption.MAIN_HAND.func_216720_c(this.field_228183_b_));
         this.field_228183_b_.sendSettingsToServer();
      }));
      ++lvt_1_1_;
      if (lvt_1_1_ % 2 == 1) {
         ++lvt_1_1_;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (lvt_1_1_ >> 1), 200, 20, I18n.format("gui.done"), (p_213079_1_) -> {
         this.minecraft.displayGuiScreen(this.field_228182_a_);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private String getMessage(PlayerModelPart p_175358_1_) {
      String lvt_2_2_;
      if (this.field_228183_b_.getModelParts().contains(p_175358_1_)) {
         lvt_2_2_ = I18n.format("options.on");
      } else {
         lvt_2_2_ = I18n.format("options.off");
      }

      return p_175358_1_.getName().getFormattedText() + ": " + lvt_2_2_;
   }
}
