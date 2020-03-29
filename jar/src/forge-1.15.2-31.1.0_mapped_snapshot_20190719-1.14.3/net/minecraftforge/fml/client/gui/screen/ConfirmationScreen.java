package net.minecraftforge.fml.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.StartupQuery;

public class ConfirmationScreen extends NotificationScreen {
   public ConfirmationScreen(StartupQuery query) {
      super(query);
   }

   public void init() {
      this.addButton(new Button(this.width / 2 - 104, this.height - 38, 100, 20, ForgeI18n.parseMessage("gui.yes"), (b) -> {
         this.minecraft.currentScreen = null;
         this.query.setResult(true);
         this.query.finish();
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 38, 100, 20, ForgeI18n.parseMessage("gui.no"), (b) -> {
         this.minecraft.currentScreen = null;
         this.query.setResult(false);
         this.query.finish();
      }));
   }
}
