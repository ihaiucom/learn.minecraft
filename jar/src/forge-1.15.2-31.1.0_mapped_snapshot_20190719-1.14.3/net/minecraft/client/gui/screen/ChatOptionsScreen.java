package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatOptionsScreen extends SettingsScreen {
   private static final AbstractOption[] CHAT_OPTIONS;
   private Widget narratorButton;

   public ChatOptionsScreen(Screen p_i1023_1_, GameSettings p_i1023_2_) {
      super(p_i1023_1_, p_i1023_2_, new TranslationTextComponent("options.chat.title", new Object[0]));
   }

   protected void init() {
      int lvt_1_1_ = 0;
      AbstractOption[] var2 = CHAT_OPTIONS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AbstractOption lvt_5_1_ = var2[var4];
         int lvt_6_1_ = this.width / 2 - 155 + lvt_1_1_ % 2 * 160;
         int lvt_7_1_ = this.height / 6 + 24 * (lvt_1_1_ >> 1);
         Widget lvt_8_1_ = this.addButton(lvt_5_1_.createWidget(this.minecraft.gameSettings, lvt_6_1_, lvt_7_1_, 150));
         if (lvt_5_1_ == AbstractOption.NARRATOR) {
            this.narratorButton = lvt_8_1_;
            lvt_8_1_.active = NarratorChatListener.INSTANCE.isActive();
         }

         ++lvt_1_1_;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (lvt_1_1_ + 1) / 2, 200, 20, I18n.format("gui.done"), (p_212990_1_) -> {
         this.minecraft.displayGuiScreen(this.field_228182_a_);
      }));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void updateNarratorButton() {
      this.narratorButton.setMessage(AbstractOption.NARRATOR.func_216720_c(this.field_228183_b_));
   }

   static {
      CHAT_OPTIONS = new AbstractOption[]{AbstractOption.CHAT_VISIBILITY, AbstractOption.CHAT_COLOR, AbstractOption.CHAT_LINKS, AbstractOption.CHAT_LINKS_PROMPT, AbstractOption.CHAT_OPACITY, AbstractOption.ACCESSIBILITY_TEXT_BACKGROUND_OPACITY, AbstractOption.CHAT_SCALE, AbstractOption.CHAT_WIDTH, AbstractOption.CHAT_HEIGHT_FOCUSED, AbstractOption.CHAT_HEIGHT_UNFOCUSED, AbstractOption.REDUCED_DEBUG_INFO, AbstractOption.AUTO_SUGGEST_COMMANDS, AbstractOption.NARRATOR};
   }
}
