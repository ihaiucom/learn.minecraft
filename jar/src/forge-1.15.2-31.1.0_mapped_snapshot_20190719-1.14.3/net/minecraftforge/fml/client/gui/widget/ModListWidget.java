package net.minecraftforge.fml.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class ModListWidget extends ExtendedList<ModListWidget.ModEntry> {
   private static final ResourceLocation VERSION_CHECK_ICONS = new ResourceLocation("forge", "textures/gui/version_check_icons.png");
   private final int listWidth;
   private ModListScreen parent;

   private static String stripControlCodes(String value) {
      return StringUtils.stripControlCodes(value);
   }

   public ModListWidget(ModListScreen parent, int listWidth, int top, int bottom) {
      Minecraft var10001 = parent.getMinecraftInstance();
      int var10003 = parent.height;
      parent.getFontRenderer().getClass();
      super(var10001, listWidth, var10003, top, bottom, 9 * 2 + 8);
      this.parent = parent;
      this.listWidth = listWidth;
      this.refreshList();
   }

   protected int getScrollbarPosition() {
      return this.listWidth;
   }

   public int getRowWidth() {
      return this.listWidth;
   }

   public void refreshList() {
      this.clearEntries();
      this.parent.buildModList(this::addEntry, (mod) -> {
         return new ModListWidget.ModEntry(mod, this.parent);
      });
   }

   protected void renderBackground() {
      this.parent.renderBackground();
   }

   public class ModEntry extends ExtendedList.AbstractListEntry<ModListWidget.ModEntry> {
      private final ModInfo modInfo;
      private final ModListScreen parent;

      ModEntry(ModInfo info, ModListScreen parent) {
         this.modInfo = info;
         this.parent = parent;
      }

      public void render(int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks) {
         String name = ModListWidget.stripControlCodes(this.modInfo.getDisplayName());
         String version = ModListWidget.stripControlCodes(MavenVersionStringHelper.artifactVersionToString(this.modInfo.getVersion()));
         VersionChecker.CheckResult vercheck = VersionChecker.getResult(this.modInfo);
         FontRenderer font = this.parent.getFontRenderer();
         font.drawString(font.trimStringToWidth(name, ModListWidget.this.listWidth), (float)(left + 3), (float)(top + 2), 16777215);
         String var10001 = font.trimStringToWidth(version, ModListWidget.this.listWidth);
         float var10002 = (float)(left + 3);
         int var10003 = top + 2;
         font.getClass();
         font.drawString(var10001, var10002, (float)(var10003 + 9), 13421772);
         if (vercheck.status.shouldDraw()) {
            Minecraft.getInstance().getTextureManager().bindTexture(ModListWidget.VERSION_CHECK_ICONS);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.pushMatrix();
            AbstractGui.blit(ModListWidget.this.getLeft() + ModListWidget.this.width - 12, top + entryHeight / 4, (float)(vercheck.status.getSheetOffset() * 8), vercheck.status.isAnimated() && (System.currentTimeMillis() / 800L & 1L) == 1L ? 8.0F : 0.0F, 8, 8, 64, 16);
            RenderSystem.popMatrix();
         }

      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         this.parent.setSelected(this);
         ModListWidget.this.setSelected(this);
         return false;
      }

      public ModInfo getInfo() {
         return this.modInfo;
      }
   }
}
