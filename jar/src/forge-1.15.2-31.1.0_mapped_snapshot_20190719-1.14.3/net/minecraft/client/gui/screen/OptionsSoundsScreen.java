package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.SoundSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsSoundsScreen extends SettingsScreen {
   public OptionsSoundsScreen(Screen p_i45025_1_, GameSettings p_i45025_2_) {
      super(p_i45025_1_, p_i45025_2_, new TranslationTextComponent("options.sounds.title", new Object[0]));
   }

   protected void init() {
      int lvt_1_1_ = 0;
      this.addButton(new SoundSlider(this.minecraft, this.width / 2 - 155 + lvt_1_1_ % 2 * 160, this.height / 6 - 12 + 24 * (lvt_1_1_ >> 1), SoundCategory.MASTER, 310));
      int lvt_1_1_ = lvt_1_1_ + 2;
      SoundCategory[] var2 = SoundCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundCategory lvt_5_1_ = var2[var4];
         if (lvt_5_1_ != SoundCategory.MASTER) {
            this.addButton(new SoundSlider(this.minecraft, this.width / 2 - 155 + lvt_1_1_ % 2 * 160, this.height / 6 - 12 + 24 * (lvt_1_1_ >> 1), lvt_5_1_, 150));
            ++lvt_1_1_;
         }
      }

      int var10003 = this.width / 2 - 75;
      int var10004 = this.height / 6 - 12;
      ++lvt_1_1_;
      this.addButton(new OptionButton(var10003, var10004 + 24 * (lvt_1_1_ >> 1), 150, 20, AbstractOption.SHOW_SUBTITLES, AbstractOption.SHOW_SUBTITLES.func_216743_c(this.field_228183_b_), (p_213105_1_) -> {
         AbstractOption.SHOW_SUBTITLES.func_216740_a(this.minecraft.gameSettings);
         p_213105_1_.setMessage(AbstractOption.SHOW_SUBTITLES.func_216743_c(this.minecraft.gameSettings));
         this.minecraft.gameSettings.saveOptions();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.format("gui.done"), (p_213104_1_) -> {
         this.minecraft.displayGuiScreen(this.field_228182_a_);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
