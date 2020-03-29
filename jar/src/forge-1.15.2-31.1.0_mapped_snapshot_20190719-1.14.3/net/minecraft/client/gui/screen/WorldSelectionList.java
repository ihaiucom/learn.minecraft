package net.minecraft.client.gui.screen;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldSelectionList extends ExtendedList<WorldSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat field_214377_b = new SimpleDateFormat();
   private static final ResourceLocation field_214378_c = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation field_214379_d = new ResourceLocation("textures/gui/world_selection.png");
   private final WorldSelectionScreen worldSelection;
   @Nullable
   private List<WorldSummary> field_212331_y;

   public WorldSelectionList(WorldSelectionScreen p_i49846_1_, Minecraft p_i49846_2_, int p_i49846_3_, int p_i49846_4_, int p_i49846_5_, int p_i49846_6_, int p_i49846_7_, Supplier<String> p_i49846_8_, @Nullable WorldSelectionList p_i49846_9_) {
      super(p_i49846_2_, p_i49846_3_, p_i49846_4_, p_i49846_5_, p_i49846_6_, p_i49846_7_);
      this.worldSelection = p_i49846_1_;
      if (p_i49846_9_ != null) {
         this.field_212331_y = p_i49846_9_.field_212331_y;
      }

      this.func_212330_a(p_i49846_8_, false);
   }

   public void func_212330_a(Supplier<String> p_212330_1_, boolean p_212330_2_) {
      this.clearEntries();
      SaveFormat lvt_3_1_ = this.minecraft.getSaveLoader();
      if (this.field_212331_y == null || p_212330_2_) {
         try {
            this.field_212331_y = lvt_3_1_.getSaveList();
         } catch (AnvilConverterException var7) {
            LOGGER.error("Couldn't load level list", var7);
            this.minecraft.displayGuiScreen(new ErrorScreen(new TranslationTextComponent("selectWorld.unable_to_load", new Object[0]), var7.getMessage()));
            return;
         }

         Collections.sort(this.field_212331_y);
      }

      String lvt_4_2_ = ((String)p_212330_1_.get()).toLowerCase(Locale.ROOT);
      Iterator var5 = this.field_212331_y.iterator();

      while(true) {
         WorldSummary lvt_6_1_;
         do {
            if (!var5.hasNext()) {
               return;
            }

            lvt_6_1_ = (WorldSummary)var5.next();
         } while(!lvt_6_1_.getDisplayName().toLowerCase(Locale.ROOT).contains(lvt_4_2_) && !lvt_6_1_.getFileName().toLowerCase(Locale.ROOT).contains(lvt_4_2_));

         this.addEntry(new WorldSelectionList.Entry(this, lvt_6_1_, this.minecraft.getSaveLoader()));
      }
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 50;
   }

   protected boolean isFocused() {
      return this.worldSelection.getFocused() == this;
   }

   public void setSelected(@Nullable WorldSelectionList.Entry p_setSelected_1_) {
      super.setSelected(p_setSelected_1_);
      if (p_setSelected_1_ != null) {
         WorldSummary lvt_2_1_ = p_setSelected_1_.field_214451_d;
         NarratorChatListener.INSTANCE.func_216864_a((new TranslationTextComponent("narrator.select", new Object[]{new TranslationTextComponent("narrator.select.world", new Object[]{lvt_2_1_.getDisplayName(), new Date(lvt_2_1_.getLastTimePlayed()), lvt_2_1_.isHardcoreModeEnabled() ? I18n.format("gameMode.hardcore") : I18n.format("gameMode." + lvt_2_1_.getEnumGameType().getName()), lvt_2_1_.getCheatsEnabled() ? I18n.format("selectWorld.cheats") : "", lvt_2_1_.func_200538_i()})})).getString());
      }

   }

   protected void moveSelection(int p_moveSelection_1_) {
      super.moveSelection(p_moveSelection_1_);
      this.worldSelection.func_214324_a(true);
   }

   public Optional<WorldSelectionList.Entry> func_214376_a() {
      return Optional.ofNullable(this.getSelected());
   }

   public WorldSelectionScreen getGuiWorldSelection() {
      return this.worldSelection;
   }

   // $FF: synthetic method
   public void setSelected(@Nullable AbstractList.AbstractListEntry p_setSelected_1_) {
      this.setSelected((WorldSelectionList.Entry)p_setSelected_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public final class Entry extends ExtendedList.AbstractListEntry<WorldSelectionList.Entry> implements AutoCloseable {
      private final Minecraft field_214449_b;
      private final WorldSelectionScreen field_214450_c;
      private final WorldSummary field_214451_d;
      private final ResourceLocation field_214452_e;
      private File field_214453_f;
      @Nullable
      private final DynamicTexture field_214454_g;
      private long field_214455_h;

      public Entry(WorldSelectionList p_i50631_2_, WorldSummary p_i50631_3_, SaveFormat p_i50631_4_) {
         this.field_214450_c = p_i50631_2_.getGuiWorldSelection();
         this.field_214451_d = p_i50631_3_;
         this.field_214449_b = Minecraft.getInstance();
         this.field_214452_e = new ResourceLocation("worlds/" + Hashing.sha1().hashUnencodedChars(p_i50631_3_.getFileName()) + "/icon");
         this.field_214453_f = p_i50631_4_.getFile(p_i50631_3_.getFileName(), "icon.png");
         if (!this.field_214453_f.isFile()) {
            this.field_214453_f = null;
         }

         this.field_214454_g = this.func_214446_f();
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         String lvt_10_1_ = this.field_214451_d.getDisplayName();
         String lvt_11_1_ = this.field_214451_d.getFileName() + " (" + WorldSelectionList.field_214377_b.format(new Date(this.field_214451_d.getLastTimePlayed())) + ")";
         if (StringUtils.isEmpty(lvt_10_1_)) {
            lvt_10_1_ = I18n.format("selectWorld.world") + " " + (p_render_1_ + 1);
         }

         String lvt_12_1_ = "";
         if (this.field_214451_d.requiresConversion()) {
            lvt_12_1_ = I18n.format("selectWorld.conversion") + " " + lvt_12_1_;
         } else {
            lvt_12_1_ = I18n.format("gameMode." + this.field_214451_d.getEnumGameType().getName());
            if (this.field_214451_d.isHardcoreModeEnabled()) {
               lvt_12_1_ = TextFormatting.DARK_RED + I18n.format("gameMode.hardcore") + TextFormatting.RESET;
            }

            if (this.field_214451_d.getCheatsEnabled()) {
               lvt_12_1_ = lvt_12_1_ + ", " + I18n.format("selectWorld.cheats");
            }

            String lvt_13_1_ = this.field_214451_d.func_200538_i().getFormattedText();
            if (this.field_214451_d.markVersionInList()) {
               if (this.field_214451_d.askToOpenWorld()) {
                  lvt_12_1_ = lvt_12_1_ + ", " + I18n.format("selectWorld.version") + " " + TextFormatting.RED + lvt_13_1_ + TextFormatting.RESET;
               } else {
                  lvt_12_1_ = lvt_12_1_ + ", " + I18n.format("selectWorld.version") + " " + TextFormatting.ITALIC + lvt_13_1_ + TextFormatting.RESET;
               }
            } else {
               lvt_12_1_ = lvt_12_1_ + ", " + I18n.format("selectWorld.version") + " " + lvt_13_1_;
            }
         }

         this.field_214449_b.fontRenderer.drawString(lvt_10_1_, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
         FontRenderer var10000 = this.field_214449_b.fontRenderer;
         float var10002 = (float)(p_render_3_ + 32 + 3);
         this.field_214449_b.fontRenderer.getClass();
         var10000.drawString(lvt_11_1_, var10002, (float)(p_render_2_ + 9 + 3), 8421504);
         var10000 = this.field_214449_b.fontRenderer;
         var10002 = (float)(p_render_3_ + 32 + 3);
         this.field_214449_b.fontRenderer.getClass();
         int var10003 = p_render_2_ + 9;
         this.field_214449_b.fontRenderer.getClass();
         var10000.drawString(lvt_12_1_, var10002, (float)(var10003 + 9 + 3), 8421504);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_214449_b.getTextureManager().bindTexture(this.field_214454_g != null ? this.field_214452_e : WorldSelectionList.field_214378_c);
         RenderSystem.enableBlend();
         AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
         if (this.field_214449_b.gameSettings.touchscreen || p_render_8_) {
            this.field_214449_b.getTextureManager().bindTexture(WorldSelectionList.field_214379_d);
            AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int lvt_13_2_ = p_render_6_ - p_render_3_;
            int lvt_14_1_ = lvt_13_2_ < 32 ? 32 : 0;
            if (this.field_214451_d.markVersionInList()) {
               AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, (float)lvt_14_1_, 32, 32, 256, 256);
               if (this.field_214451_d.func_202842_n()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, (float)lvt_14_1_, 32, 32, 256, 256);
                  if (lvt_13_2_ < 32) {
                     ITextComponent lvt_15_1_ = (new TranslationTextComponent("selectWorld.tooltip.unsupported", new Object[]{this.field_214451_d.func_200538_i()})).applyTextStyle(TextFormatting.RED);
                     this.field_214450_c.setVersionTooltip(this.field_214449_b.fontRenderer.wrapFormattedStringToWidth(lvt_15_1_.getFormattedText(), 175));
                  }
               } else if (this.field_214451_d.askToOpenWorld()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, (float)lvt_14_1_, 32, 32, 256, 256);
                  if (lvt_13_2_ < 32) {
                     this.field_214450_c.setVersionTooltip(TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion1") + "\n" + TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion2"));
                  }
               } else if (!SharedConstants.getVersion().isStable()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, (float)lvt_14_1_, 32, 32, 256, 256);
                  if (lvt_13_2_ < 32) {
                     this.field_214450_c.setVersionTooltip(TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot1") + "\n" + TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot2"));
                  }
               }
            } else {
               AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, (float)lvt_14_1_, 32, 32, 256, 256);
            }
         }

      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         WorldSelectionList.this.setSelected(this);
         this.field_214450_c.func_214324_a(WorldSelectionList.this.func_214376_a().isPresent());
         if (p_mouseClicked_1_ - (double)WorldSelectionList.this.getRowLeft() <= 32.0D) {
            this.func_214438_a();
            return true;
         } else if (Util.milliTime() - this.field_214455_h < 250L) {
            this.func_214438_a();
            return true;
         } else {
            this.field_214455_h = Util.milliTime();
            return false;
         }
      }

      public void func_214438_a() {
         if (!this.field_214451_d.func_197731_n() && !this.field_214451_d.func_202842_n()) {
            if (this.field_214451_d.askToOpenWorld()) {
               this.field_214449_b.displayGuiScreen(new ConfirmScreen((p_214434_1_) -> {
                  if (p_214434_1_) {
                     try {
                        this.func_214443_e();
                     } catch (Exception var3) {
                        WorldSelectionList.LOGGER.error("Failure to open 'future world'", var3);
                        this.field_214449_b.displayGuiScreen(new AlertScreen(() -> {
                           this.field_214449_b.displayGuiScreen(this.field_214450_c);
                        }, new TranslationTextComponent("selectWorld.futureworld.error.title", new Object[0]), new TranslationTextComponent("selectWorld.futureworld.error.text", new Object[0])));
                     }
                  } else {
                     this.field_214449_b.displayGuiScreen(this.field_214450_c);
                  }

               }, new TranslationTextComponent("selectWorld.versionQuestion", new Object[0]), new TranslationTextComponent("selectWorld.versionWarning", new Object[]{this.field_214451_d.func_200538_i().getFormattedText()}), I18n.format("selectWorld.versionJoinButton"), I18n.format("gui.cancel")));
            } else {
               this.func_214443_e();
            }
         } else {
            ITextComponent lvt_1_1_ = new TranslationTextComponent("selectWorld.backupQuestion", new Object[0]);
            ITextComponent lvt_2_1_ = new TranslationTextComponent("selectWorld.backupWarning", new Object[]{this.field_214451_d.func_200538_i().getFormattedText(), SharedConstants.getVersion().getName()});
            if (this.field_214451_d.func_202842_n()) {
               lvt_1_1_ = new TranslationTextComponent("selectWorld.backupQuestion.customized", new Object[0]);
               lvt_2_1_ = new TranslationTextComponent("selectWorld.backupWarning.customized", new Object[0]);
            }

            this.field_214449_b.displayGuiScreen(new ConfirmBackupScreen(this.field_214450_c, (p_214436_1_, p_214436_2_) -> {
               if (p_214436_1_) {
                  String lvt_3_1_ = this.field_214451_d.getFileName();
                  EditWorldScreen.createBackup(this.field_214449_b.getSaveLoader(), lvt_3_1_);
               }

               this.func_214443_e();
            }, lvt_1_1_, lvt_2_1_, false));
         }

      }

      public void func_214442_b() {
         this.field_214449_b.displayGuiScreen(new ConfirmScreen((p_214440_1_) -> {
            if (p_214440_1_) {
               this.field_214449_b.displayGuiScreen(new WorkingScreen());
               SaveFormat lvt_2_1_ = this.field_214449_b.getSaveLoader();
               lvt_2_1_.deleteWorldDirectory(this.field_214451_d.getFileName());
               WorldSelectionList.this.func_212330_a(() -> {
                  return this.field_214450_c.field_212352_g.getText();
               }, true);
            }

            this.field_214449_b.displayGuiScreen(this.field_214450_c);
         }, new TranslationTextComponent("selectWorld.deleteQuestion", new Object[0]), new TranslationTextComponent("selectWorld.deleteWarning", new Object[]{this.field_214451_d.getDisplayName()}), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel")));
      }

      public void func_214444_c() {
         this.field_214449_b.displayGuiScreen(new EditWorldScreen((p_214435_1_) -> {
            if (p_214435_1_) {
               WorldSelectionList.this.func_212330_a(() -> {
                  return this.field_214450_c.field_212352_g.getText();
               }, true);
            }

            this.field_214449_b.displayGuiScreen(this.field_214450_c);
         }, this.field_214451_d.getFileName()));
      }

      public void func_214445_d() {
         try {
            this.field_214449_b.displayGuiScreen(new WorkingScreen());
            CreateWorldScreen lvt_1_1_ = new CreateWorldScreen(this.field_214450_c);
            SaveHandler lvt_2_1_ = this.field_214449_b.getSaveLoader().getSaveLoader(this.field_214451_d.getFileName(), (MinecraftServer)null);
            WorldInfo lvt_3_1_ = lvt_2_1_.loadWorldInfo();
            if (lvt_3_1_ != null) {
               lvt_1_1_.recreateFromExistingWorld(lvt_3_1_);
               if (this.field_214451_d.func_202842_n()) {
                  this.field_214449_b.displayGuiScreen(new ConfirmScreen((p_214439_2_) -> {
                     this.field_214449_b.displayGuiScreen((Screen)(p_214439_2_ ? lvt_1_1_ : this.field_214450_c));
                  }, new TranslationTextComponent("selectWorld.recreate.customized.title", new Object[0]), new TranslationTextComponent("selectWorld.recreate.customized.text", new Object[0]), I18n.format("gui.proceed"), I18n.format("gui.cancel")));
               } else {
                  this.field_214449_b.displayGuiScreen(lvt_1_1_);
               }
            }
         } catch (Exception var4) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", var4);
            this.field_214449_b.displayGuiScreen(new AlertScreen(() -> {
               this.field_214449_b.displayGuiScreen(this.field_214450_c);
            }, new TranslationTextComponent("selectWorld.recreate.error.title", new Object[0]), new TranslationTextComponent("selectWorld.recreate.error.text", new Object[0])));
         }

      }

      private void func_214443_e() {
         this.field_214449_b.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.field_214449_b.getSaveLoader().canLoadWorld(this.field_214451_d.getFileName())) {
            this.field_214449_b.launchIntegratedServer(this.field_214451_d.getFileName(), this.field_214451_d.getDisplayName(), (WorldSettings)null);
         }

      }

      @Nullable
      private DynamicTexture func_214446_f() {
         boolean lvt_1_1_ = this.field_214453_f != null && this.field_214453_f.isFile();
         if (lvt_1_1_) {
            try {
               InputStream lvt_2_1_ = new FileInputStream(this.field_214453_f);
               Throwable var3 = null;

               DynamicTexture var6;
               try {
                  NativeImage lvt_4_1_ = NativeImage.read((InputStream)lvt_2_1_);
                  Validate.validState(lvt_4_1_.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                  Validate.validState(lvt_4_1_.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                  DynamicTexture lvt_5_1_ = new DynamicTexture(lvt_4_1_);
                  this.field_214449_b.getTextureManager().func_229263_a_(this.field_214452_e, lvt_5_1_);
                  var6 = lvt_5_1_;
               } catch (Throwable var16) {
                  var3 = var16;
                  throw var16;
               } finally {
                  if (lvt_2_1_ != null) {
                     if (var3 != null) {
                        try {
                           lvt_2_1_.close();
                        } catch (Throwable var15) {
                           var3.addSuppressed(var15);
                        }
                     } else {
                        lvt_2_1_.close();
                     }
                  }

               }

               return var6;
            } catch (Throwable var18) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.field_214451_d.getFileName(), var18);
               this.field_214453_f = null;
               return null;
            }
         } else {
            this.field_214449_b.getTextureManager().deleteTexture(this.field_214452_e);
            return null;
         }
      }

      public void close() {
         if (this.field_214454_g != null) {
            this.field_214454_g.close();
         }

      }
   }
}
