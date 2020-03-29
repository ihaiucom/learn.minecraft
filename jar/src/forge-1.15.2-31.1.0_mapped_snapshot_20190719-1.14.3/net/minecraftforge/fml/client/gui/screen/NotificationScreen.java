package net.minecraftforge.fml.client.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.StartupQuery;

public class NotificationScreen extends Screen {
   protected final StartupQuery query;

   public NotificationScreen(StartupQuery query) {
      super(new TranslationTextComponent("fml.menu.notification.title", new Object[0]));
      this.query = query;
   }

   public void init() {
      this.buttons.add(new Button(this.width / 2 - 100, this.height - 38, 200, 20, I18n.format("gui.done"), (b) -> {
         this.minecraft.displayGuiScreen((Screen)null);
         this.query.finish();
      }));
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.renderBackground();
      String[] lines = this.query.getText().split("\n");
      int spaceAvailable = this.height - 38 - 20;
      int spaceRequired = Math.min(spaceAvailable, 10 + 10 * lines.length);
      int offset = 10 + (spaceAvailable - spaceRequired) / 2;
      String[] var8 = lines;
      int var9 = lines.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String line = var8[var10];
         if (offset >= spaceAvailable) {
            this.drawCenteredString(this.font, "...", this.width / 2, offset, 16777215);
            break;
         }

         if (!line.isEmpty()) {
            this.drawCenteredString(this.font, line, this.width / 2, offset, 16777215);
         }

         offset += 10;
      }

      super.render(mouseX, mouseY, partialTicks);
   }
}
