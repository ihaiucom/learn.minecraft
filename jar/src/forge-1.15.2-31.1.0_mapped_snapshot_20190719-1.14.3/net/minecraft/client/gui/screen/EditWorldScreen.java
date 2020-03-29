package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;

@OnlyIn(Dist.CLIENT)
public class EditWorldScreen extends Screen {
   private Button saveButton;
   private final BooleanConsumer field_214311_b;
   private TextFieldWidget nameEdit;
   private final String worldId;

   public EditWorldScreen(BooleanConsumer p_i51073_1_, String p_i51073_2_) {
      super(new TranslationTextComponent("selectWorld.edit.title", new Object[0]));
      this.field_214311_b = p_i51073_1_;
      this.worldId = p_i51073_2_;
   }

   public void tick() {
      this.nameEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      Button lvt_1_1_ = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, I18n.format("selectWorld.edit.resetIcon"), (p_214309_1_) -> {
         SaveFormat lvt_2_1_ = this.minecraft.getSaveLoader();
         FileUtils.deleteQuietly(lvt_2_1_.getFile(this.worldId, "icon.png"));
         p_214309_1_.active = false;
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, I18n.format("selectWorld.edit.openFolder"), (p_214303_1_) -> {
         SaveFormat lvt_2_1_ = this.minecraft.getSaveLoader();
         Util.getOSType().openFile(lvt_2_1_.getFile(this.worldId, "icon.png").getParentFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, I18n.format("selectWorld.edit.backup"), (p_214304_1_) -> {
         SaveFormat lvt_2_1_ = this.minecraft.getSaveLoader();
         createBackup(lvt_2_1_, this.worldId);
         this.field_214311_b.accept(false);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, I18n.format("selectWorld.edit.backupFolder"), (p_214302_1_) -> {
         SaveFormat lvt_2_1_ = this.minecraft.getSaveLoader();
         Path lvt_3_1_ = lvt_2_1_.getBackupsFolder();

         try {
            Files.createDirectories(Files.exists(lvt_3_1_, new LinkOption[0]) ? lvt_3_1_.toRealPath() : lvt_3_1_);
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }

         Util.getOSType().openFile(lvt_3_1_.toFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, I18n.format("selectWorld.edit.optimize"), (p_214310_1_) -> {
         this.minecraft.displayGuiScreen(new ConfirmBackupScreen(this, (p_214305_1_, p_214305_2_) -> {
            if (p_214305_1_) {
               createBackup(this.minecraft.getSaveLoader(), this.worldId);
            }

            this.minecraft.displayGuiScreen(new OptimizeWorldScreen(this.field_214311_b, this.worldId, this.minecraft.getSaveLoader(), p_214305_2_));
         }, new TranslationTextComponent("optimizeWorld.confirm.title", new Object[0]), new TranslationTextComponent("optimizeWorld.confirm.description", new Object[0]), true));
      }));
      this.saveButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.format("selectWorld.edit.save"), (p_214308_1_) -> {
         this.saveChanges();
      }));
      this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.format("gui.cancel"), (p_214306_1_) -> {
         this.field_214311_b.accept(false);
      }));
      lvt_1_1_.active = this.minecraft.getSaveLoader().getFile(this.worldId, "icon.png").isFile();
      SaveFormat lvt_2_1_ = this.minecraft.getSaveLoader();
      WorldInfo lvt_3_1_ = lvt_2_1_.getWorldInfo(this.worldId);
      String lvt_4_1_ = lvt_3_1_ == null ? "" : lvt_3_1_.getWorldName();
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 53, 200, 20, I18n.format("selectWorld.enterName"));
      this.nameEdit.setText(lvt_4_1_);
      this.nameEdit.func_212954_a((p_214301_1_) -> {
         this.saveButton.active = !p_214301_1_.trim().isEmpty();
      });
      this.children.add(this.nameEdit);
      this.func_212928_a(this.nameEdit);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String lvt_4_1_ = this.nameEdit.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.nameEdit.setText(lvt_4_1_);
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   private void saveChanges() {
      SaveFormat lvt_1_1_ = this.minecraft.getSaveLoader();
      lvt_1_1_.renameWorld(this.worldId, this.nameEdit.getText().trim());
      this.field_214311_b.accept(true);
   }

   public static void createBackup(SaveFormat p_200212_0_, String p_200212_1_) {
      ToastGui lvt_2_1_ = Minecraft.getInstance().getToastGui();
      long lvt_3_1_ = 0L;
      IOException lvt_5_1_ = null;

      try {
         lvt_3_1_ = p_200212_0_.createBackup(p_200212_1_);
      } catch (IOException var8) {
         lvt_5_1_ = var8;
      }

      TranslationTextComponent lvt_6_3_;
      Object lvt_7_2_;
      if (lvt_5_1_ != null) {
         lvt_6_3_ = new TranslationTextComponent("selectWorld.edit.backupFailed", new Object[0]);
         lvt_7_2_ = new StringTextComponent(lvt_5_1_.getMessage());
      } else {
         lvt_6_3_ = new TranslationTextComponent("selectWorld.edit.backupCreated", new Object[]{p_200212_1_});
         lvt_7_2_ = new TranslationTextComponent("selectWorld.edit.backupSize", new Object[]{MathHelper.ceil((double)lvt_3_1_ / 1048576.0D)});
      }

      lvt_2_1_.add(new SystemToast(SystemToast.Type.WORLD_BACKUP, lvt_6_3_, (ITextComponent)lvt_7_2_));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 40, 10526880);
      this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}
