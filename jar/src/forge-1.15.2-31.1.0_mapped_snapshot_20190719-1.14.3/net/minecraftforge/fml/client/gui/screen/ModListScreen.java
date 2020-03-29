package net.minecraftforge.fml.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ModListWidget;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ComparableVersion;

public class ModListScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PADDING = 6;
   private Screen mainMenu;
   private ModListWidget modList;
   private ModListScreen.InfoPanel modInfo;
   private ModListWidget.ModEntry selected = null;
   private int listWidth;
   private List<ModInfo> mods;
   private final List<ModInfo> unsortedMods;
   private Button configButton;
   private Button openModsFolderButton;
   private int buttonMargin = 1;
   private int numButtons = ModListScreen.SortType.values().length;
   private String lastFilterText = "";
   private TextFieldWidget search;
   private boolean sorted = false;
   private ModListScreen.SortType sortType;

   private static String stripControlCodes(String value) {
      return StringUtils.stripControlCodes(value);
   }

   public ModListScreen(Screen mainMenu) {
      super(new TranslationTextComponent("fml.menu.mods.title", new Object[0]));
      this.sortType = ModListScreen.SortType.NORMAL;
      this.mainMenu = mainMenu;
      this.mods = Collections.unmodifiableList(ModList.get().getMods());
      this.unsortedMods = Collections.unmodifiableList(this.mods);
   }

   public void init() {
      ModInfo mod;
      for(Iterator var1 = this.mods.iterator(); var1.hasNext(); this.listWidth = Math.max(this.listWidth, this.getFontRenderer().getStringWidth(MavenVersionStringHelper.artifactVersionToString(mod.getVersion())) + 5)) {
         mod = (ModInfo)var1.next();
         this.listWidth = Math.max(this.listWidth, this.getFontRenderer().getStringWidth(mod.getDisplayName()) + 10);
      }

      this.listWidth = Math.max(Math.min(this.listWidth, this.width / 3), 100);
      this.listWidth += this.listWidth % this.numButtons != 0 ? this.numButtons - this.listWidth % this.numButtons : 0;
      int modInfoWidth = this.width - this.listWidth - 18;
      int doneButtonWidth = Math.min(modInfoWidth, 200);
      int y = this.height - 20 - 6;
      this.addButton(new Button((this.listWidth + 6 + this.width - doneButtonWidth) / 2, y, doneButtonWidth, 20, I18n.format("gui.done"), (b) -> {
         this.minecraft.displayGuiScreen(this.mainMenu);
      }));
      this.addButton(this.openModsFolderButton = new Button(6, y, this.listWidth, 20, I18n.format("fml.menu.mods.openmodsfolder"), (b) -> {
         Util.getOSType().openFile(FMLPaths.MODSDIR.get().toFile());
      }));
      y -= 26;
      this.addButton(this.configButton = new Button(6, y, this.listWidth, 20, I18n.format("fml.menu.mods.config"), (b) -> {
         this.displayModConfig();
      }));
      this.configButton.active = false;
      y -= 21;
      this.search = new TextFieldWidget(this.getFontRenderer(), 7, y, this.listWidth - 2, 14, I18n.format("fml.menu.mods.search"));
      int fullButtonHeight = 32;
      int var10004 = this.listWidth;
      int var10006 = this.search.y;
      this.getFontRenderer().getClass();
      this.modList = new ModListWidget(this, var10004, fullButtonHeight, var10006 - 9 - 6);
      this.modList.setLeftPos(6);
      this.modInfo = new ModListScreen.InfoPanel(this.minecraft, modInfoWidth, this.height - 6 - fullButtonHeight, 6);
      this.children.add(this.search);
      this.children.add(this.modList);
      this.children.add(this.modInfo);
      this.search.setFocused2(false);
      this.search.setCanLoseFocus(true);
      int width = this.listWidth / this.numButtons;
      int x = 6;
      this.addButton(ModListScreen.SortType.NORMAL.button = new Button(x, 6, width - this.buttonMargin, 20, ModListScreen.SortType.NORMAL.getButtonText(), (b) -> {
         this.resortMods(ModListScreen.SortType.NORMAL);
      }));
      int x = x + width + this.buttonMargin;
      this.addButton(ModListScreen.SortType.A_TO_Z.button = new Button(x, 6, width - this.buttonMargin, 20, ModListScreen.SortType.A_TO_Z.getButtonText(), (b) -> {
         this.resortMods(ModListScreen.SortType.A_TO_Z);
      }));
      x += width + this.buttonMargin;
      this.addButton(ModListScreen.SortType.Z_TO_A.button = new Button(x, 6, width - this.buttonMargin, 20, ModListScreen.SortType.Z_TO_A.getButtonText(), (b) -> {
         this.resortMods(ModListScreen.SortType.Z_TO_A);
      }));
      this.resortMods(ModListScreen.SortType.NORMAL);
      this.updateCache();
   }

   private void displayModConfig() {
      if (this.selected != null) {
         try {
            ConfigGuiHandler.getGuiFactoryFor(this.selected.getInfo()).map((f) -> {
               return (Screen)f.apply(this.minecraft, this);
            }).ifPresent((newScreen) -> {
               this.minecraft.displayGuiScreen(newScreen);
            });
         } catch (Exception var2) {
            LOGGER.error("There was a critical issue trying to build the config GUI for {}", this.selected.getInfo().getModId(), var2);
         }

      }
   }

   public void tick() {
      this.search.tick();
      this.modList.setSelected(this.selected);
      if (!this.search.getText().equals(this.lastFilterText)) {
         this.reloadMods();
         this.sorted = false;
      }

      if (!this.sorted) {
         this.reloadMods();
         this.mods.sort(this.sortType);
         this.modList.refreshList();
         if (this.selected != null) {
            this.selected = (ModListWidget.ModEntry)this.modList.children().stream().filter((e) -> {
               return e.getInfo() == this.selected.getInfo();
            }).findFirst().orElse((Object)null);
            this.updateCache();
         }

         this.sorted = true;
      }

   }

   public <T extends ExtendedList.AbstractListEntry<T>> void buildModList(Consumer<T> modListViewConsumer, Function<ModInfo, T> newEntry) {
      this.mods.forEach((mod) -> {
         modListViewConsumer.accept(newEntry.apply(mod));
      });
   }

   private void reloadMods() {
      this.mods = (List)this.unsortedMods.stream().filter((mi) -> {
         return net.minecraftforge.fml.loading.StringUtils.toLowerCase(stripControlCodes(mi.getDisplayName())).contains(net.minecraftforge.fml.loading.StringUtils.toLowerCase(this.search.getText()));
      }).collect(Collectors.toList());
      this.lastFilterText = this.search.getText();
   }

   private void resortMods(ModListScreen.SortType newSort) {
      this.sortType = newSort;
      ModListScreen.SortType[] var2 = ModListScreen.SortType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ModListScreen.SortType sort = var2[var4];
         if (sort.button != null) {
            sort.button.active = this.sortType != sort;
         }
      }

      this.sorted = false;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.modList.render(mouseX, mouseY, partialTicks);
      if (this.modInfo != null) {
         this.modInfo.render(mouseX, mouseY, partialTicks);
      }

      String text = I18n.format("fml.menu.mods.search");
      int x = this.modList.getLeft() + (this.modList.getRight() - this.modList.getLeft()) / 2 - this.getFontRenderer().getStringWidth(text) / 2;
      FontRenderer var10000 = this.getFontRenderer();
      float var10002 = (float)x;
      int var10003 = this.search.y;
      this.getFontRenderer().getClass();
      var10000.drawString(text, var10002, (float)(var10003 - 9), 16777215);
      this.search.render(mouseX, mouseY, partialTicks);
      super.render(mouseX, mouseY, partialTicks);
   }

   public Minecraft getMinecraftInstance() {
      return this.minecraft;
   }

   public FontRenderer getFontRenderer() {
      return this.font;
   }

   public void setSelected(ModListWidget.ModEntry entry) {
      this.selected = entry == this.selected ? null : entry;
      this.updateCache();
   }

   private void updateCache() {
      if (this.selected == null) {
         this.configButton.active = false;
         this.modInfo.clearInfo();
      } else {
         ModInfo selectedMod = this.selected.getInfo();
         this.configButton.active = ConfigGuiHandler.getGuiFactoryFor(selectedMod).isPresent();
         List<String> lines = new ArrayList();
         VersionChecker.CheckResult vercheck = VersionChecker.getResult(selectedMod);
         Pair<ResourceLocation, Size2i> logoData = (Pair)selectedMod.getLogoFile().map((logoFile) -> {
            TextureManager tm = this.minecraft.getTextureManager();
            ModFileResourcePack resourcePack = (ModFileResourcePack)ResourcePackLoader.getResourcePackFor(selectedMod.getModId()).orElse(ResourcePackLoader.getResourcePackFor("forge").orElseThrow(() -> {
               return new RuntimeException("Can't find forge, WHAT!");
            }));

            try {
               NativeImage logo = null;
               InputStream logoResource = resourcePack.getRootResourceStream(logoFile);
               if (logoResource != null) {
                  logo = NativeImage.read(logoResource);
               }

               if (logo != null) {
                  return Pair.of(tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo) {
                     public void updateDynamicTexture() {
                        this.func_229148_d_();
                        NativeImage td = this.getTextureData();
                        this.getTextureData().func_227789_a_(0, 0, 0, 0, 0, td.getWidth(), td.getHeight(), selectedMod.getLogoBlur(), false, false, false);
                     }
                  }), new Size2i(logo.getWidth(), logo.getHeight()));
               }
            } catch (IOException var7) {
            }

            return Pair.of((Object)null, new Size2i(0, 0));
         }).orElse(Pair.of((Object)null, new Size2i(0, 0)));
         lines.add(selectedMod.getDisplayName());
         lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.version", MavenVersionStringHelper.artifactVersionToString(selectedMod.getVersion())));
         lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.idstate", selectedMod.getModId(), ModList.get().getModContainerById(selectedMod.getModId()).map(ModContainer::getCurrentState).map(Object::toString).orElse("NONE")));
         selectedMod.getModConfig().getOptional("credits").ifPresent((credits) -> {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.credits", credits));
         });
         selectedMod.getModConfig().getOptional("authors").ifPresent((authors) -> {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.authors", authors));
         });
         selectedMod.getModConfig().getOptional("displayURL").ifPresent((displayURL) -> {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.displayurl", displayURL));
         });
         if (selectedMod.getOwningFile() != null && selectedMod.getOwningFile().getMods().size() != 1) {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.childmods", selectedMod.getOwningFile().getMods().stream().map(IModInfo::getDisplayName).collect(Collectors.joining(","))));
         } else {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.nochildmods"));
         }

         if (vercheck.status == VersionChecker.Status.OUTDATED || vercheck.status == VersionChecker.Status.BETA_OUTDATED) {
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.updateavailable", vercheck.url == null ? "" : vercheck.url));
         }

         lines.add((Object)null);
         lines.add(selectedMod.getDescription());
         if ((vercheck.status == VersionChecker.Status.OUTDATED || vercheck.status == VersionChecker.Status.BETA_OUTDATED) && vercheck.changes.size() > 0) {
            lines.add((Object)null);
            lines.add(ForgeI18n.parseMessage("fml.menu.mods.info.changelogheader"));
            Iterator var5 = vercheck.changes.entrySet().iterator();

            while(var5.hasNext()) {
               Entry<ComparableVersion, String> entry = (Entry)var5.next();
               lines.add("  " + entry.getKey() + ":");
               lines.add(entry.getValue());
               lines.add((Object)null);
            }
         }

         this.modInfo.setInfo(lines, (ResourceLocation)logoData.getLeft(), (Size2i)logoData.getRight());
      }
   }

   public void resize(Minecraft mc, int width, int height) {
      String s = this.search.getText();
      ModListScreen.SortType sort = this.sortType;
      ModListWidget.ModEntry selected = this.selected;
      this.init(mc, width, height);
      this.search.setText(s);
      this.selected = selected;
      if (!this.search.getText().isEmpty()) {
         this.reloadMods();
      }

      if (sort != ModListScreen.SortType.NORMAL) {
         this.resortMods(sort);
      }

      this.updateCache();
   }

   class InfoPanel extends ScrollPanel {
      private ResourceLocation logoPath;
      private Size2i logoDims = new Size2i(0, 0);
      private List<ITextComponent> lines = Collections.emptyList();

      InfoPanel(Minecraft mcIn, int widthIn, int heightIn, int topIn) {
         super(mcIn, widthIn, heightIn, topIn, ModListScreen.this.modList.getRight() + 6);
      }

      void setInfo(List<String> lines, ResourceLocation logoPath, Size2i logoDims) {
         this.logoPath = logoPath;
         this.logoDims = logoDims;
         this.lines = this.resizeContent(lines);
      }

      void clearInfo() {
         this.logoPath = null;
         this.logoDims = new Size2i(0, 0);
         this.lines = Collections.emptyList();
      }

      private List<ITextComponent> resizeContent(List<String> lines) {
         List<ITextComponent> ret = new ArrayList();
         Iterator var3 = lines.iterator();

         while(var3.hasNext()) {
            String line = (String)var3.next();
            if (line == null) {
               ret.add((Object)null);
            } else {
               ITextComponent chat = ForgeHooks.newChatWithLinks(line, false);
               int maxTextLength = this.width - 12;
               if (maxTextLength >= 0) {
                  ret.addAll(RenderComponentsUtil.splitText(chat, maxTextLength, ModListScreen.this.font, false, true));
               }
            }
         }

         return ret;
      }

      public int getContentHeight() {
         int height = 50;
         int var10001 = this.lines.size();
         ModListScreen.this.font.getClass();
         int heightx = height + var10001 * 9;
         if (heightx < this.bottom - this.top - 8) {
            heightx = this.bottom - this.top - 8;
         }

         return heightx;
      }

      protected int getScrollAmount() {
         ModListScreen.this.font.getClass();
         return 9 * 3;
      }

      protected void drawPanel(int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
         if (this.logoPath != null) {
            Minecraft.getInstance().getTextureManager().bindTexture(this.logoPath);
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int headerHeight = 50;
            GuiUtils.drawInscribedRect(this.left + 6, relativeY, this.width - 12, headerHeight, this.logoDims.width, this.logoDims.height, false, true);
            relativeY += headerHeight + 6;
         }

         for(Iterator var8 = this.lines.iterator(); var8.hasNext(); relativeY += 9) {
            ITextComponent line = (ITextComponent)var8.next();
            if (line != null) {
               RenderSystem.enableBlend();
               ModListScreen.this.font.drawStringWithShadow(line.getFormattedText(), (float)(this.left + 6), (float)relativeY, 16777215);
               RenderSystem.disableAlphaTest();
               RenderSystem.disableBlend();
            }

            ModListScreen.this.font.getClass();
         }

         ITextComponent component = this.findTextLine(mouseX, mouseY);
         if (component != null) {
            ModListScreen.this.renderComponentHoverEffect(component, mouseX, mouseY);
         }

      }

      private ITextComponent findTextLine(int mouseX, int mouseY) {
         double offset = (double)((float)(mouseY - this.top + 4) + this.scrollDistance + 1.0F);
         if (this.logoPath != null) {
            offset -= 50.0D;
         }

         if (offset <= 0.0D) {
            return null;
         } else {
            ModListScreen.this.font.getClass();
            int lineIdx = (int)(offset / 9.0D);
            if (lineIdx < this.lines.size() && lineIdx >= 1) {
               ITextComponent line = (ITextComponent)this.lines.get(lineIdx - 1);
               if (line != null) {
                  int k = this.left + 4;
                  Iterator var8 = line.iterator();

                  while(var8.hasNext()) {
                     ITextComponent part = (ITextComponent)var8.next();
                     if (part instanceof StringTextComponent) {
                        k += ModListScreen.this.font.getStringWidth(((StringTextComponent)part).getText());
                        if (k >= mouseX) {
                           return part;
                        }
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         }
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         ITextComponent component = this.findTextLine((int)mouseX, (int)mouseY);
         if (component != null) {
            ModListScreen.this.handleComponentClicked(component);
            return true;
         } else {
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }

      protected void drawBackground() {
      }
   }

   private static enum SortType implements Comparator<ModInfo> {
      NORMAL,
      A_TO_Z {
         protected int compare(String name1, String name2) {
            return name1.compareTo(name2);
         }
      },
      Z_TO_A {
         protected int compare(String name1, String name2) {
            return name2.compareTo(name1);
         }
      };

      Button button;

      private SortType() {
      }

      protected int compare(String name1, String name2) {
         return 0;
      }

      public int compare(ModInfo o1, ModInfo o2) {
         String name1 = net.minecraftforge.fml.loading.StringUtils.toLowerCase(ModListScreen.stripControlCodes(o1.getDisplayName()));
         String name2 = net.minecraftforge.fml.loading.StringUtils.toLowerCase(ModListScreen.stripControlCodes(o2.getDisplayName()));
         return this.compare(name1, name2);
      }

      String getButtonText() {
         return I18n.format("fml.menu.mods." + net.minecraftforge.fml.loading.StringUtils.toLowerCase(this.name()));
      }

      // $FF: synthetic method
      SortType(Object x2) {
         this();
      }
   }
}
