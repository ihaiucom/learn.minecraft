package net.minecraft.client.gui.screen;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditStructureScreen extends Screen {
   private final StructureBlockTileEntity tileStructure;
   private Mirror mirror;
   private Rotation rotation;
   private StructureMode mode;
   private boolean ignoreEntities;
   private boolean showAir;
   private boolean showBoundingBox;
   private TextFieldWidget nameEdit;
   private TextFieldWidget posXEdit;
   private TextFieldWidget posYEdit;
   private TextFieldWidget posZEdit;
   private TextFieldWidget sizeXEdit;
   private TextFieldWidget sizeYEdit;
   private TextFieldWidget sizeZEdit;
   private TextFieldWidget integrityEdit;
   private TextFieldWidget seedEdit;
   private TextFieldWidget dataEdit;
   private Button doneButton;
   private Button cancelButton;
   private Button saveButton;
   private Button loadButton;
   private Button rotateZeroDegreesButton;
   private Button rotateNinetyDegreesButton;
   private Button rotate180DegreesButton;
   private Button rotate270DegressButton;
   private Button modeButton;
   private Button detectSizeButton;
   private Button showEntitiesButton;
   private Button mirrorButton;
   private Button showAirButton;
   private Button showBoundingBoxButton;
   private final DecimalFormat decimalFormat;

   public EditStructureScreen(StructureBlockTileEntity p_i47142_1_) {
      super(new TranslationTextComponent(Blocks.STRUCTURE_BLOCK.getTranslationKey(), new Object[0]));
      this.mirror = Mirror.NONE;
      this.rotation = Rotation.NONE;
      this.mode = StructureMode.DATA;
      this.decimalFormat = new DecimalFormat("0.0###");
      this.tileStructure = p_i47142_1_;
      this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   }

   public void tick() {
      this.nameEdit.tick();
      this.posXEdit.tick();
      this.posYEdit.tick();
      this.posZEdit.tick();
      this.sizeXEdit.tick();
      this.sizeYEdit.tick();
      this.sizeZEdit.tick();
      this.integrityEdit.tick();
      this.seedEdit.tick();
      this.dataEdit.tick();
   }

   private void func_195275_h() {
      if (this.func_210143_a(StructureBlockTileEntity.UpdateCommand.UPDATE_DATA)) {
         this.minecraft.displayGuiScreen((Screen)null);
      }

   }

   private void func_195272_i() {
      this.tileStructure.setMirror(this.mirror);
      this.tileStructure.setRotation(this.rotation);
      this.tileStructure.setMode(this.mode);
      this.tileStructure.setIgnoresEntities(this.ignoreEntities);
      this.tileStructure.setShowAir(this.showAir);
      this.tileStructure.setShowBoundingBox(this.showBoundingBox);
      this.minecraft.displayGuiScreen((Screen)null);
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, I18n.format("gui.done"), (p_214274_1_) -> {
         this.func_195275_h();
      }));
      this.cancelButton = (Button)this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, I18n.format("gui.cancel"), (p_214275_1_) -> {
         this.func_195272_i();
      }));
      this.saveButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, I18n.format("structure_block.button.save"), (p_214276_1_) -> {
         if (this.tileStructure.getMode() == StructureMode.SAVE) {
            this.func_210143_a(StructureBlockTileEntity.UpdateCommand.SAVE_AREA);
            this.minecraft.displayGuiScreen((Screen)null);
         }

      }));
      this.loadButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 185, 50, 20, I18n.format("structure_block.button.load"), (p_214277_1_) -> {
         if (this.tileStructure.getMode() == StructureMode.LOAD) {
            this.func_210143_a(StructureBlockTileEntity.UpdateCommand.LOAD_AREA);
            this.minecraft.displayGuiScreen((Screen)null);
         }

      }));
      this.modeButton = (Button)this.addButton(new Button(this.width / 2 - 4 - 150, 185, 50, 20, "MODE", (p_214280_1_) -> {
         this.tileStructure.nextMode();
         this.updateMode();
      }));
      this.detectSizeButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 120, 50, 20, I18n.format("structure_block.button.detect_size"), (p_214278_1_) -> {
         if (this.tileStructure.getMode() == StructureMode.SAVE) {
            this.func_210143_a(StructureBlockTileEntity.UpdateCommand.SCAN_AREA);
            this.minecraft.displayGuiScreen((Screen)null);
         }

      }));
      this.showEntitiesButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES", (p_214282_1_) -> {
         this.tileStructure.setIgnoresEntities(!this.tileStructure.ignoresEntities());
         this.updateEntitiesButton();
      }));
      this.mirrorButton = (Button)this.addButton(new Button(this.width / 2 - 20, 185, 40, 20, "MIRROR", (p_214281_1_) -> {
         switch(this.tileStructure.getMirror()) {
         case NONE:
            this.tileStructure.setMirror(Mirror.LEFT_RIGHT);
            break;
         case LEFT_RIGHT:
            this.tileStructure.setMirror(Mirror.FRONT_BACK);
            break;
         case FRONT_BACK:
            this.tileStructure.setMirror(Mirror.NONE);
         }

         this.updateMirrorButton();
      }));
      this.showAirButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR", (p_214269_1_) -> {
         this.tileStructure.setShowAir(!this.tileStructure.showsAir());
         this.updateToggleAirButton();
      }));
      this.showBoundingBoxButton = (Button)this.addButton(new Button(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB", (p_214270_1_) -> {
         this.tileStructure.setShowBoundingBox(!this.tileStructure.showsBoundingBox());
         this.updateToggleBoundingBox();
      }));
      this.rotateZeroDegreesButton = (Button)this.addButton(new Button(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0", (p_214268_1_) -> {
         this.tileStructure.setRotation(Rotation.NONE);
         this.updateDirectionButtons();
      }));
      this.rotateNinetyDegreesButton = (Button)this.addButton(new Button(this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90", (p_214273_1_) -> {
         this.tileStructure.setRotation(Rotation.CLOCKWISE_90);
         this.updateDirectionButtons();
      }));
      this.rotate180DegreesButton = (Button)this.addButton(new Button(this.width / 2 + 1 + 20, 185, 40, 20, "180", (p_214272_1_) -> {
         this.tileStructure.setRotation(Rotation.CLOCKWISE_180);
         this.updateDirectionButtons();
      }));
      this.rotate270DegressButton = (Button)this.addButton(new Button(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270", (p_214271_1_) -> {
         this.tileStructure.setRotation(Rotation.COUNTERCLOCKWISE_90);
         this.updateDirectionButtons();
      }));
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, I18n.format("structure_block.structure_name")) {
         public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
            return !EditStructureScreen.this.isValidCharacterForName(this.getText(), p_charTyped_1_, this.getCursorPosition()) ? false : super.charTyped(p_charTyped_1_, p_charTyped_2_);
         }
      };
      this.nameEdit.setMaxStringLength(64);
      this.nameEdit.setText(this.tileStructure.getName());
      this.children.add(this.nameEdit);
      BlockPos lvt_1_1_ = this.tileStructure.getPosition();
      this.posXEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 80, 20, I18n.format("structure_block.position.x"));
      this.posXEdit.setMaxStringLength(15);
      this.posXEdit.setText(Integer.toString(lvt_1_1_.getX()));
      this.children.add(this.posXEdit);
      this.posYEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 80, 80, 20, I18n.format("structure_block.position.y"));
      this.posYEdit.setMaxStringLength(15);
      this.posYEdit.setText(Integer.toString(lvt_1_1_.getY()));
      this.children.add(this.posYEdit);
      this.posZEdit = new TextFieldWidget(this.font, this.width / 2 + 8, 80, 80, 20, I18n.format("structure_block.position.z"));
      this.posZEdit.setMaxStringLength(15);
      this.posZEdit.setText(Integer.toString(lvt_1_1_.getZ()));
      this.children.add(this.posZEdit);
      BlockPos lvt_2_1_ = this.tileStructure.getStructureSize();
      this.sizeXEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, I18n.format("structure_block.size.x"));
      this.sizeXEdit.setMaxStringLength(15);
      this.sizeXEdit.setText(Integer.toString(lvt_2_1_.getX()));
      this.children.add(this.sizeXEdit);
      this.sizeYEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, I18n.format("structure_block.size.y"));
      this.sizeYEdit.setMaxStringLength(15);
      this.sizeYEdit.setText(Integer.toString(lvt_2_1_.getY()));
      this.children.add(this.sizeYEdit);
      this.sizeZEdit = new TextFieldWidget(this.font, this.width / 2 + 8, 120, 80, 20, I18n.format("structure_block.size.z"));
      this.sizeZEdit.setMaxStringLength(15);
      this.sizeZEdit.setText(Integer.toString(lvt_2_1_.getZ()));
      this.children.add(this.sizeZEdit);
      this.integrityEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, I18n.format("structure_block.integrity.integrity"));
      this.integrityEdit.setMaxStringLength(15);
      this.integrityEdit.setText(this.decimalFormat.format((double)this.tileStructure.getIntegrity()));
      this.children.add(this.integrityEdit);
      this.seedEdit = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, I18n.format("structure_block.integrity.seed"));
      this.seedEdit.setMaxStringLength(31);
      this.seedEdit.setText(Long.toString(this.tileStructure.getSeed()));
      this.children.add(this.seedEdit);
      this.dataEdit = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 240, 20, I18n.format("structure_block.custom_data"));
      this.dataEdit.setMaxStringLength(128);
      this.dataEdit.setText(this.tileStructure.getMetadata());
      this.children.add(this.dataEdit);
      this.mirror = this.tileStructure.getMirror();
      this.updateMirrorButton();
      this.rotation = this.tileStructure.getRotation();
      this.updateDirectionButtons();
      this.mode = this.tileStructure.getMode();
      this.updateMode();
      this.ignoreEntities = this.tileStructure.ignoresEntities();
      this.updateEntitiesButton();
      this.showAir = this.tileStructure.showsAir();
      this.updateToggleAirButton();
      this.showBoundingBox = this.tileStructure.showsBoundingBox();
      this.updateToggleBoundingBox();
      this.func_212928_a(this.nameEdit);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String lvt_4_1_ = this.nameEdit.getText();
      String lvt_5_1_ = this.posXEdit.getText();
      String lvt_6_1_ = this.posYEdit.getText();
      String lvt_7_1_ = this.posZEdit.getText();
      String lvt_8_1_ = this.sizeXEdit.getText();
      String lvt_9_1_ = this.sizeYEdit.getText();
      String lvt_10_1_ = this.sizeZEdit.getText();
      String lvt_11_1_ = this.integrityEdit.getText();
      String lvt_12_1_ = this.seedEdit.getText();
      String lvt_13_1_ = this.dataEdit.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.nameEdit.setText(lvt_4_1_);
      this.posXEdit.setText(lvt_5_1_);
      this.posYEdit.setText(lvt_6_1_);
      this.posZEdit.setText(lvt_7_1_);
      this.sizeXEdit.setText(lvt_8_1_);
      this.sizeYEdit.setText(lvt_9_1_);
      this.sizeZEdit.setText(lvt_10_1_);
      this.integrityEdit.setText(lvt_11_1_);
      this.seedEdit.setText(lvt_12_1_);
      this.dataEdit.setText(lvt_13_1_);
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   private void updateEntitiesButton() {
      boolean lvt_1_1_ = !this.tileStructure.ignoresEntities();
      if (lvt_1_1_) {
         this.showEntitiesButton.setMessage(I18n.format("options.on"));
      } else {
         this.showEntitiesButton.setMessage(I18n.format("options.off"));
      }

   }

   private void updateToggleAirButton() {
      boolean lvt_1_1_ = this.tileStructure.showsAir();
      if (lvt_1_1_) {
         this.showAirButton.setMessage(I18n.format("options.on"));
      } else {
         this.showAirButton.setMessage(I18n.format("options.off"));
      }

   }

   private void updateToggleBoundingBox() {
      boolean lvt_1_1_ = this.tileStructure.showsBoundingBox();
      if (lvt_1_1_) {
         this.showBoundingBoxButton.setMessage(I18n.format("options.on"));
      } else {
         this.showBoundingBoxButton.setMessage(I18n.format("options.off"));
      }

   }

   private void updateMirrorButton() {
      Mirror lvt_1_1_ = this.tileStructure.getMirror();
      switch(lvt_1_1_) {
      case NONE:
         this.mirrorButton.setMessage("|");
         break;
      case LEFT_RIGHT:
         this.mirrorButton.setMessage("< >");
         break;
      case FRONT_BACK:
         this.mirrorButton.setMessage("^ v");
      }

   }

   private void updateDirectionButtons() {
      this.rotateZeroDegreesButton.active = true;
      this.rotateNinetyDegreesButton.active = true;
      this.rotate180DegreesButton.active = true;
      this.rotate270DegressButton.active = true;
      switch(this.tileStructure.getRotation()) {
      case NONE:
         this.rotateZeroDegreesButton.active = false;
         break;
      case CLOCKWISE_180:
         this.rotate180DegreesButton.active = false;
         break;
      case COUNTERCLOCKWISE_90:
         this.rotate270DegressButton.active = false;
         break;
      case CLOCKWISE_90:
         this.rotateNinetyDegreesButton.active = false;
      }

   }

   private void updateMode() {
      this.nameEdit.setVisible(false);
      this.posXEdit.setVisible(false);
      this.posYEdit.setVisible(false);
      this.posZEdit.setVisible(false);
      this.sizeXEdit.setVisible(false);
      this.sizeYEdit.setVisible(false);
      this.sizeZEdit.setVisible(false);
      this.integrityEdit.setVisible(false);
      this.seedEdit.setVisible(false);
      this.dataEdit.setVisible(false);
      this.saveButton.visible = false;
      this.loadButton.visible = false;
      this.detectSizeButton.visible = false;
      this.showEntitiesButton.visible = false;
      this.mirrorButton.visible = false;
      this.rotateZeroDegreesButton.visible = false;
      this.rotateNinetyDegreesButton.visible = false;
      this.rotate180DegreesButton.visible = false;
      this.rotate270DegressButton.visible = false;
      this.showAirButton.visible = false;
      this.showBoundingBoxButton.visible = false;
      switch(this.tileStructure.getMode()) {
      case SAVE:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.sizeXEdit.setVisible(true);
         this.sizeYEdit.setVisible(true);
         this.sizeZEdit.setVisible(true);
         this.saveButton.visible = true;
         this.detectSizeButton.visible = true;
         this.showEntitiesButton.visible = true;
         this.showAirButton.visible = true;
         break;
      case LOAD:
         this.nameEdit.setVisible(true);
         this.posXEdit.setVisible(true);
         this.posYEdit.setVisible(true);
         this.posZEdit.setVisible(true);
         this.integrityEdit.setVisible(true);
         this.seedEdit.setVisible(true);
         this.loadButton.visible = true;
         this.showEntitiesButton.visible = true;
         this.mirrorButton.visible = true;
         this.rotateZeroDegreesButton.visible = true;
         this.rotateNinetyDegreesButton.visible = true;
         this.rotate180DegreesButton.visible = true;
         this.rotate270DegressButton.visible = true;
         this.showBoundingBoxButton.visible = true;
         this.updateDirectionButtons();
         break;
      case CORNER:
         this.nameEdit.setVisible(true);
         break;
      case DATA:
         this.dataEdit.setVisible(true);
      }

      this.modeButton.setMessage(I18n.format("structure_block.mode." + this.tileStructure.getMode().getName()));
   }

   private boolean func_210143_a(StructureBlockTileEntity.UpdateCommand p_210143_1_) {
      BlockPos lvt_2_1_ = new BlockPos(this.parseCoordinate(this.posXEdit.getText()), this.parseCoordinate(this.posYEdit.getText()), this.parseCoordinate(this.posZEdit.getText()));
      BlockPos lvt_3_1_ = new BlockPos(this.parseCoordinate(this.sizeXEdit.getText()), this.parseCoordinate(this.sizeYEdit.getText()), this.parseCoordinate(this.sizeZEdit.getText()));
      float lvt_4_1_ = this.parseIntegrity(this.integrityEdit.getText());
      long lvt_5_1_ = this.parseSeed(this.seedEdit.getText());
      this.minecraft.getConnection().sendPacket(new CUpdateStructureBlockPacket(this.tileStructure.getPos(), p_210143_1_, this.tileStructure.getMode(), this.nameEdit.getText(), lvt_2_1_, lvt_3_1_, this.tileStructure.getMirror(), this.tileStructure.getRotation(), this.dataEdit.getText(), this.tileStructure.ignoresEntities(), this.tileStructure.showsAir(), this.tileStructure.showsBoundingBox(), lvt_4_1_, lvt_5_1_));
      return true;
   }

   private long parseSeed(String p_189821_1_) {
      try {
         return Long.valueOf(p_189821_1_);
      } catch (NumberFormatException var3) {
         return 0L;
      }
   }

   private float parseIntegrity(String p_189819_1_) {
      try {
         return Float.valueOf(p_189819_1_);
      } catch (NumberFormatException var3) {
         return 1.0F;
      }
   }

   private int parseCoordinate(String p_189817_1_) {
      try {
         return Integer.parseInt(p_189817_1_);
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   public void onClose() {
      this.func_195272_i();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.func_195275_h();
         return true;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      StructureMode lvt_4_1_ = this.tileStructure.getMode();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 10, 16777215);
      if (lvt_4_1_ != StructureMode.DATA) {
         this.drawString(this.font, I18n.format("structure_block.structure_name"), this.width / 2 - 153, 30, 10526880);
         this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
      }

      String lvt_5_4_;
      int lvt_6_3_;
      if (lvt_4_1_ == StructureMode.LOAD || lvt_4_1_ == StructureMode.SAVE) {
         this.drawString(this.font, I18n.format("structure_block.position"), this.width / 2 - 153, 70, 10526880);
         this.posXEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.posYEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.posZEdit.render(p_render_1_, p_render_2_, p_render_3_);
         lvt_5_4_ = I18n.format("structure_block.include_entities");
         lvt_6_3_ = this.font.getStringWidth(lvt_5_4_);
         this.drawString(this.font, lvt_5_4_, this.width / 2 + 154 - lvt_6_3_, 150, 10526880);
      }

      if (lvt_4_1_ == StructureMode.SAVE) {
         this.drawString(this.font, I18n.format("structure_block.size"), this.width / 2 - 153, 110, 10526880);
         this.sizeXEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.sizeYEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.sizeZEdit.render(p_render_1_, p_render_2_, p_render_3_);
         lvt_5_4_ = I18n.format("structure_block.detect_size");
         lvt_6_3_ = this.font.getStringWidth(lvt_5_4_);
         this.drawString(this.font, lvt_5_4_, this.width / 2 + 154 - lvt_6_3_, 110, 10526880);
         String lvt_7_1_ = I18n.format("structure_block.show_air");
         int lvt_8_1_ = this.font.getStringWidth(lvt_7_1_);
         this.drawString(this.font, lvt_7_1_, this.width / 2 + 154 - lvt_8_1_, 70, 10526880);
      }

      if (lvt_4_1_ == StructureMode.LOAD) {
         this.drawString(this.font, I18n.format("structure_block.integrity"), this.width / 2 - 153, 110, 10526880);
         this.integrityEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.seedEdit.render(p_render_1_, p_render_2_, p_render_3_);
         lvt_5_4_ = I18n.format("structure_block.show_boundingbox");
         lvt_6_3_ = this.font.getStringWidth(lvt_5_4_);
         this.drawString(this.font, lvt_5_4_, this.width / 2 + 154 - lvt_6_3_, 70, 10526880);
      }

      if (lvt_4_1_ == StructureMode.DATA) {
         this.drawString(this.font, I18n.format("structure_block.custom_data"), this.width / 2 - 153, 110, 10526880);
         this.dataEdit.render(p_render_1_, p_render_2_, p_render_3_);
      }

      lvt_5_4_ = "structure_block.mode_info." + lvt_4_1_.getName();
      this.drawString(this.font, I18n.format(lvt_5_4_), this.width / 2 - 153, 174, 10526880);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
