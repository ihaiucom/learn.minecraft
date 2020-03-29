package net.minecraftforge.fml.client.gui.screen;

import com.google.common.base.Strings;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingErrorScreen extends ErrorScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path modsDir;
   private final Path logFile;
   private final List<ModLoadingException> modLoadErrors;
   private final List<ModLoadingWarning> modLoadWarnings;
   private LoadingErrorScreen.LoadingEntryList entryList;
   private String errorHeader;
   private String warningHeader;

   public LoadingErrorScreen(LoadingFailedException loadingException, List<ModLoadingWarning> warnings) {
      super(new StringTextComponent("Loading Error"), (String)null);
      this.modLoadWarnings = warnings;
      this.modLoadErrors = loadingException == null ? Collections.emptyList() : loadingException.getErrors();
      this.modsDir = FMLPaths.MODSDIR.get();
      this.logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs", "latest.log"));
   }

   public void init() {
      super.init();
      this.buttons.clear();
      this.children.clear();
      this.errorHeader = TextFormatting.RED + ForgeI18n.parseMessage("fml.loadingerrorscreen.errorheader", this.modLoadErrors.size()) + TextFormatting.RESET;
      this.warningHeader = TextFormatting.YELLOW + ForgeI18n.parseMessage("fml.loadingerrorscreen.warningheader", this.modLoadErrors.size()) + TextFormatting.RESET;
      int yOffset = this.modLoadErrors.isEmpty() ? 46 : 38;
      this.addButton(new ExtendedButton(50, this.height - yOffset, this.width / 2 - 55, 20, ForgeI18n.parseMessage("fml.button.open.mods.folder"), (b) -> {
         Util.getOSType().openFile(this.modsDir.toFile());
      }));
      this.addButton(new ExtendedButton(this.width / 2 + 5, this.height - yOffset, this.width / 2 - 55, 20, ForgeI18n.parseMessage("fml.button.open.file", this.logFile.getFileName()), (b) -> {
         Util.getOSType().openFile(this.logFile.toFile());
      }));
      if (this.modLoadErrors.isEmpty()) {
         this.addButton(new ExtendedButton(this.width / 4, this.height - 24, this.width / 2, 20, ForgeI18n.parseMessage("fml.button.continue.launch"), (b) -> {
            ClientHooks.logMissingTextureErrors();
            this.minecraft.displayGuiScreen((Screen)null);
         }));
      }

      this.entryList = new LoadingErrorScreen.LoadingEntryList(this, this.modLoadErrors, this.modLoadWarnings);
      this.children.add(this.entryList);
      this.setFocused(this.entryList);
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.renderBackground();
      this.entryList.render(mouseX, mouseY, partialTicks);
      this.drawMultiLineCenteredString(this.font, this.modLoadErrors.isEmpty() ? this.warningHeader : this.errorHeader, this.width / 2, 10);
      this.buttons.forEach((button) -> {
         button.render(mouseX, mouseY, partialTicks);
      });
   }

   private void drawMultiLineCenteredString(FontRenderer fr, String str, int x, int y) {
      for(Iterator var5 = fr.listFormattedStringToWidth(str, this.width).iterator(); var5.hasNext(); y += 9) {
         String s = (String)var5.next();
         fr.drawStringWithShadow(s, (float)((double)x - (double)fr.getStringWidth(s) / 2.0D), (float)y, 16777215);
         fr.getClass();
      }

   }

   public static class LoadingEntryList extends ExtendedList<LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry> {
      LoadingEntryList(LoadingErrorScreen parent, List<ModLoadingException> errors, List<ModLoadingWarning> warnings) {
         Minecraft var10001 = parent.minecraft;
         int var10002 = parent.width;
         int var10003 = parent.height;
         int var10005 = parent.height - 50;
         parent.minecraft.fontRenderer.getClass();
         super(var10001, var10002, var10003, 35, var10005, 2 * 9 + 8);
         boolean both = !errors.isEmpty() && !warnings.isEmpty();
         if (both) {
            this.addEntry(new LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry(parent.errorHeader, true));
         }

         errors.forEach((e) -> {
            this.addEntry(new LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry(e.formatToString()));
         });
         if (both) {
            int maxChars = (this.width - 10) / parent.minecraft.fontRenderer.getStringWidth("-");
            this.addEntry(new LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry("\n" + Strings.repeat("-", maxChars) + "\n"));
            this.addEntry(new LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry(parent.warningHeader, true));
         }

         warnings.forEach((w) -> {
            this.addEntry(new LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry(w.formatToString()));
         });
      }

      protected int getScrollbarPosition() {
         return this.getRight() - 6;
      }

      public int getRowWidth() {
         return this.width;
      }

      public class LoadingMessageEntry extends ExtendedList.AbstractListEntry<LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry> {
         private final String message;
         private final boolean center;

         LoadingMessageEntry(String message) {
            this(message, false);
         }

         LoadingMessageEntry(String message, boolean center) {
            this.message = (String)Objects.requireNonNull(message);
            this.center = center;
         }

         public void render(int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
            FontRenderer font = Minecraft.getInstance().fontRenderer;
            List<String> strings = font.listFormattedStringToWidth(this.message, LoadingEntryList.this.width);
            int y = top + 2;

            for(int i = 0; i < Math.min(strings.size(), 2); ++i) {
               if (this.center) {
                  font.drawString((String)strings.get(i), (float)left + (float)LoadingEntryList.this.width / 2.0F - (float)font.getStringWidth((String)strings.get(i)) / 2.0F, (float)y, 16777215);
               } else {
                  font.drawString((String)strings.get(i), (float)(left + 5), (float)y, 16777215);
               }

               font.getClass();
               y += 9;
            }

         }
      }
   }
}
