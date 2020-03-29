package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyModifier;

@OnlyIn(Dist.CLIENT)
public class ControlsScreen extends SettingsScreen {
   public KeyBinding buttonId;
   public long time;
   private KeyBindingList keyBindingList;
   private Button field_146493_s;

   public ControlsScreen(Screen p_i1027_1_, GameSettings p_i1027_2_) {
      super(p_i1027_1_, p_i1027_2_, new TranslationTextComponent("controls.title", new Object[0]));
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, I18n.format("options.mouse_settings"), (p_lambda$init$0_1_) -> {
         this.minecraft.displayGuiScreen(new MouseSettingsScreen(this, this.field_228183_b_));
      }));
      this.addButton(AbstractOption.AUTO_JUMP.createWidget(this.field_228183_b_, this.width / 2 - 155 + 160, 18, 150));
      this.keyBindingList = new KeyBindingList(this, this.minecraft);
      this.children.add(this.keyBindingList);
      this.field_146493_s = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll"), (p_lambda$init$1_1_) -> {
         KeyBinding[] var2 = this.field_228183_b_.keyBindings;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            KeyBinding keybinding = var2[var4];
            keybinding.setToDefault();
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done"), (p_lambda$init$2_1_) -> {
         this.minecraft.displayGuiScreen(this.field_228182_a_);
      }));
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.buttonId != null) {
         this.field_228183_b_.setKeyBindingCode(this.buttonId, InputMappings.Type.MOUSE.getOrMakeInput(p_mouseClicked_5_));
         this.buttonId = null;
         KeyBinding.resetKeyBindingArrayAndHash();
         return true;
      } else {
         return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.buttonId != null) {
         if (p_keyPressed_1_ == 256) {
            this.buttonId.setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputMappings.INPUT_INVALID);
            this.field_228183_b_.setKeyBindingCode(this.buttonId, InputMappings.INPUT_INVALID);
         } else {
            this.buttonId.setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
            this.field_228183_b_.setKeyBindingCode(this.buttonId, InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
         }

         if (!KeyModifier.isKeyCodeModifier(this.buttonId.getKey())) {
            this.buttonId = null;
         }

         this.time = Util.milliTime();
         KeyBinding.resetKeyBindingArrayAndHash();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.keyBindingList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
      boolean flag = false;
      KeyBinding[] var5 = this.field_228183_b_.keyBindings;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         KeyBinding keybinding = var5[var7];
         if (!keybinding.isDefault()) {
            flag = true;
            break;
         }
      }

      this.field_146493_s.active = flag;
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
