package net.minecraft.client.gui.screen;

import com.google.gson.JsonElement;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.FileUtil;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class CreateWorldScreen extends Screen {
   private final Screen parentScreen;
   private TextFieldWidget worldNameField;
   private TextFieldWidget worldSeedField;
   private String saveDirName;
   private CreateWorldScreen.GameMode field_228197_f_;
   @Nullable
   private CreateWorldScreen.GameMode field_228198_g_;
   private boolean generateStructuresEnabled;
   private boolean allowCheats;
   private boolean allowCheatsWasSetByUser;
   private boolean bonusChestEnabled;
   private boolean hardCoreMode;
   private boolean alreadyGenerated;
   private boolean inMoreWorldOptionsDisplay;
   private Button btnCreateWorld;
   private Button btnGameMode;
   private Button btnMoreOptions;
   private Button btnMapFeatures;
   private Button btnBonusItems;
   private Button btnMapType;
   private Button btnAllowCommands;
   private Button btnCustomizeType;
   private String gameModeDesc1;
   private String gameModeDesc2;
   private String worldSeed;
   private String worldName;
   private int selectedIndex;
   public CompoundNBT chunkProviderSettingsJson;

   public CreateWorldScreen(Screen p_i46320_1_) {
      super(new TranslationTextComponent("selectWorld.create", new Object[0]));
      this.field_228197_f_ = CreateWorldScreen.GameMode.SURVIVAL;
      this.generateStructuresEnabled = true;
      this.chunkProviderSettingsJson = new CompoundNBT();
      this.parentScreen = p_i46320_1_;
      this.worldSeed = "";
      this.worldName = I18n.format("selectWorld.newWorld");
   }

   public void tick() {
      this.worldNameField.tick();
      this.worldSeedField.tick();
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.worldNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.format("selectWorld.enterName")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.format("selectWorld.resultFolder") + " " + CreateWorldScreen.this.saveDirName;
         }
      };
      this.worldNameField.setText(this.worldName);
      this.worldNameField.func_212954_a((p_lambda$init$0_1_) -> {
         this.worldName = p_lambda$init$0_1_;
         this.btnCreateWorld.active = !this.worldNameField.getText().isEmpty();
         this.calcSaveDirName();
      });
      this.children.add(this.worldNameField);
      this.btnGameMode = (Button)this.addButton(new Button(this.width / 2 - 75, 115, 150, 20, I18n.format("selectWorld.gameMode"), (p_lambda$init$1_1_) -> {
         switch(this.field_228197_f_) {
         case SURVIVAL:
            this.func_228200_a_(CreateWorldScreen.GameMode.HARDCORE);
            break;
         case HARDCORE:
            this.func_228200_a_(CreateWorldScreen.GameMode.CREATIVE);
            break;
         case CREATIVE:
            this.func_228200_a_(CreateWorldScreen.GameMode.SURVIVAL);
         }

         p_lambda$init$1_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + CreateWorldScreen.this.field_228197_f_.field_228217_e_);
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + CreateWorldScreen.this.gameModeDesc1 + " " + CreateWorldScreen.this.gameModeDesc2;
         }
      });
      this.worldSeedField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.format("selectWorld.enterSeed"));
      this.worldSeedField.setText(this.worldSeed);
      this.worldSeedField.func_212954_a((p_lambda$init$2_1_) -> {
         this.worldSeed = this.worldSeedField.getText();
      });
      this.children.add(this.worldSeedField);
      this.btnMapFeatures = (Button)this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.mapFeatures"), (p_lambda$init$3_1_) -> {
         this.generateStructuresEnabled = !this.generateStructuresEnabled;
         p_lambda$init$3_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.format("selectWorld.mapFeatures") + ' ' + I18n.format(CreateWorldScreen.this.generateStructuresEnabled ? "options.on" : "options.off");
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.format("selectWorld.mapFeatures.info");
         }
      });
      this.btnMapFeatures.visible = false;
      this.btnMapType = (Button)this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.mapType"), (p_lambda$init$4_1_) -> {
         ++this.selectedIndex;
         if (this.selectedIndex >= WorldType.WORLD_TYPES.length) {
            this.selectedIndex = 0;
         }

         while(!this.canSelectCurWorldType()) {
            ++this.selectedIndex;
            if (this.selectedIndex >= WorldType.WORLD_TYPES.length) {
               this.selectedIndex = 0;
            }
         }

         this.chunkProviderSettingsJson = new CompoundNBT();
         this.showMoreWorldOptions(this.inMoreWorldOptionsDisplay);
         p_lambda$init$4_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.format("selectWorld.mapType") + ' ' + I18n.format(WorldType.WORLD_TYPES[CreateWorldScreen.this.selectedIndex].getTranslationKey());
         }

         protected String getNarrationMessage() {
            WorldType worldtype = WorldType.WORLD_TYPES[CreateWorldScreen.this.selectedIndex];
            return worldtype.hasInfoNotice() ? super.getNarrationMessage() + ". " + I18n.format(worldtype.getInfoTranslationKey()) : super.getNarrationMessage();
         }
      });
      this.btnMapType.visible = false;
      this.btnCustomizeType = (Button)this.addButton(new Button(this.width / 2 + 5, 120, 150, 20, I18n.format("selectWorld.customizeType"), (p_lambda$init$5_1_) -> {
         WorldType.WORLD_TYPES[this.selectedIndex].onCustomizeButton(this.minecraft, this);
      }));
      this.btnCustomizeType.visible = false;
      this.btnAllowCommands = (Button)this.addButton(new Button(this.width / 2 - 155, 151, 150, 20, I18n.format("selectWorld.allowCommands"), (p_lambda$init$6_1_) -> {
         this.allowCheatsWasSetByUser = true;
         this.allowCheats = !this.allowCheats;
         p_lambda$init$6_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.format("selectWorld.allowCommands") + ' ' + I18n.format(CreateWorldScreen.this.allowCheats && !CreateWorldScreen.this.hardCoreMode ? "options.on" : "options.off");
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.format("selectWorld.allowCommands.info");
         }
      });
      this.btnAllowCommands.visible = false;
      this.btnBonusItems = (Button)this.addButton(new Button(this.width / 2 + 5, 151, 150, 20, I18n.format("selectWorld.bonusItems"), (p_lambda$init$7_1_) -> {
         this.bonusChestEnabled = !this.bonusChestEnabled;
         p_lambda$init$7_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.format("selectWorld.bonusItems") + ' ' + I18n.format(CreateWorldScreen.this.bonusChestEnabled && !CreateWorldScreen.this.hardCoreMode ? "options.on" : "options.off");
         }
      });
      this.btnBonusItems.visible = false;
      this.btnMoreOptions = (Button)this.addButton(new Button(this.width / 2 - 75, 187, 150, 20, I18n.format("selectWorld.moreWorldOptions"), (p_lambda$init$8_1_) -> {
         this.toggleMoreWorldOptions();
      }));
      this.btnCreateWorld = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("selectWorld.create"), (p_lambda$init$9_1_) -> {
         this.createWorld();
      }));
      this.btnCreateWorld.active = !this.worldName.isEmpty();
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_lambda$init$10_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      this.showMoreWorldOptions(this.inMoreWorldOptionsDisplay);
      this.func_212928_a(this.worldNameField);
      this.func_228200_a_(this.field_228197_f_);
      this.calcSaveDirName();
   }

   private void func_228199_a_() {
      this.gameModeDesc1 = I18n.format("selectWorld.gameMode." + this.field_228197_f_.field_228217_e_ + ".line1");
      this.gameModeDesc2 = I18n.format("selectWorld.gameMode." + this.field_228197_f_.field_228217_e_ + ".line2");
   }

   private void calcSaveDirName() {
      this.saveDirName = this.worldNameField.getText().trim();
      if (this.saveDirName.isEmpty()) {
         this.saveDirName = "World";
      }

      try {
         this.saveDirName = FileUtil.func_214992_a(this.minecraft.getSaveLoader().func_215781_c(), this.saveDirName, "");
      } catch (Exception var4) {
         this.saveDirName = "World";

         try {
            this.saveDirName = FileUtil.func_214992_a(this.minecraft.getSaveLoader().func_215781_c(), this.saveDirName, "");
         } catch (Exception var3) {
            throw new RuntimeException("Could not create save folder", var3);
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   private void createWorld() {
      this.minecraft.displayGuiScreen((Screen)null);
      if (!this.alreadyGenerated) {
         this.alreadyGenerated = true;
         long i = (new Random()).nextLong();
         String s = this.worldSeedField.getText();
         if (!StringUtils.isEmpty(s)) {
            try {
               long j = Long.parseLong(s);
               if (j != 0L) {
                  i = j;
               }
            } catch (NumberFormatException var6) {
               i = (long)s.hashCode();
            }
         }

         WorldType.WORLD_TYPES[this.selectedIndex].onGUICreateWorldPress();
         WorldSettings worldsettings = new WorldSettings(i, this.field_228197_f_.field_228218_f_, this.generateStructuresEnabled, this.hardCoreMode, WorldType.WORLD_TYPES[this.selectedIndex]);
         worldsettings.setGeneratorOptions((JsonElement)Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.chunkProviderSettingsJson));
         if (this.bonusChestEnabled && !this.hardCoreMode) {
            worldsettings.enableBonusChest();
         }

         if (this.allowCheats && !this.hardCoreMode) {
            worldsettings.enableCommands();
         }

         this.minecraft.launchIntegratedServer(this.saveDirName, this.worldNameField.getText().trim(), worldsettings);
      }

   }

   private boolean canSelectCurWorldType() {
      WorldType worldtype = WorldType.WORLD_TYPES[this.selectedIndex];
      if (worldtype != null && worldtype.canBeCreated()) {
         return worldtype == WorldType.DEBUG_ALL_BLOCK_STATES ? hasShiftDown() : true;
      } else {
         return false;
      }
   }

   private void toggleMoreWorldOptions() {
      this.showMoreWorldOptions(!this.inMoreWorldOptionsDisplay);
   }

   private void func_228200_a_(CreateWorldScreen.GameMode p_228200_1_) {
      if (!this.allowCheatsWasSetByUser) {
         this.allowCheats = p_228200_1_ == CreateWorldScreen.GameMode.CREATIVE;
      }

      if (p_228200_1_ == CreateWorldScreen.GameMode.HARDCORE) {
         this.hardCoreMode = true;
         this.btnAllowCommands.active = false;
         this.btnBonusItems.active = false;
      } else {
         this.hardCoreMode = false;
         this.btnAllowCommands.active = true;
         this.btnBonusItems.active = true;
      }

      this.field_228197_f_ = p_228200_1_;
      this.func_228199_a_();
   }

   private void showMoreWorldOptions(boolean p_146316_1_) {
      this.inMoreWorldOptionsDisplay = p_146316_1_;
      this.btnGameMode.visible = !this.inMoreWorldOptionsDisplay;
      this.btnMapType.visible = this.inMoreWorldOptionsDisplay;
      if (WorldType.WORLD_TYPES[this.selectedIndex] == WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.btnGameMode.active = false;
         if (this.field_228198_g_ == null) {
            this.field_228198_g_ = this.field_228197_f_;
         }

         this.func_228200_a_(CreateWorldScreen.GameMode.DEBUG);
         this.btnMapFeatures.visible = false;
         this.btnBonusItems.visible = false;
         this.btnAllowCommands.visible = false;
         this.btnCustomizeType.visible = false;
      } else {
         this.btnGameMode.active = true;
         if (this.field_228198_g_ != null) {
            this.func_228200_a_(this.field_228198_g_);
         }

         this.btnMapFeatures.visible = this.inMoreWorldOptionsDisplay && WorldType.WORLD_TYPES[this.selectedIndex] != WorldType.CUSTOMIZED;
         this.btnBonusItems.visible = this.inMoreWorldOptionsDisplay;
         this.btnAllowCommands.visible = this.inMoreWorldOptionsDisplay;
         this.btnCustomizeType.visible = this.inMoreWorldOptionsDisplay && WorldType.WORLD_TYPES[this.selectedIndex].hasCustomOptions();
      }

      this.worldSeedField.setVisible(this.inMoreWorldOptionsDisplay);
      this.worldNameField.setVisible(!this.inMoreWorldOptionsDisplay);
      if (this.inMoreWorldOptionsDisplay) {
         this.btnMoreOptions.setMessage(I18n.format("gui.done"));
      } else {
         this.btnMoreOptions.setMessage(I18n.format("selectWorld.moreWorldOptions"));
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.createWorld();
         return true;
      }
   }

   public void onClose() {
      if (this.inMoreWorldOptionsDisplay) {
         this.showMoreWorldOptions(false);
      } else {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, -1);
      if (this.inMoreWorldOptionsDisplay) {
         this.drawString(this.font, I18n.format("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.format("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
         if (this.btnMapFeatures.visible) {
            this.drawString(this.font, I18n.format("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
         }

         if (this.btnAllowCommands.visible) {
            this.drawString(this.font, I18n.format("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
         }

         this.worldSeedField.render(p_render_1_, p_render_2_, p_render_3_);
         if (WorldType.WORLD_TYPES[this.selectedIndex].hasInfoNotice()) {
            this.font.drawSplitString(I18n.format(WorldType.WORLD_TYPES[this.selectedIndex].getInfoTranslationKey()), this.btnMapType.x + 2, this.btnMapType.y + 22, this.btnMapType.getWidth(), 10526880);
         }
      } else {
         this.drawString(this.font, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.format("selectWorld.resultFolder") + " " + this.saveDirName, this.width / 2 - 100, 85, -6250336);
         this.worldNameField.render(p_render_1_, p_render_2_, p_render_3_);
         this.drawCenteredString(this.font, this.gameModeDesc1, this.width / 2, 137, -6250336);
         this.drawCenteredString(this.font, this.gameModeDesc2, this.width / 2, 149, -6250336);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void recreateFromExistingWorld(WorldInfo p_146318_1_) {
      this.worldName = p_146318_1_.getWorldName();
      this.worldSeed = Long.toString(p_146318_1_.getSeed());
      WorldType worldtype = p_146318_1_.getGenerator() == WorldType.CUSTOMIZED ? WorldType.DEFAULT : p_146318_1_.getGenerator();
      this.selectedIndex = worldtype.getId();
      this.chunkProviderSettingsJson = p_146318_1_.getGeneratorOptions();
      this.generateStructuresEnabled = p_146318_1_.isMapFeaturesEnabled();
      this.allowCheats = p_146318_1_.areCommandsAllowed();
      if (p_146318_1_.isHardcore()) {
         this.field_228197_f_ = CreateWorldScreen.GameMode.HARDCORE;
      } else if (p_146318_1_.getGameType().isSurvivalOrAdventure()) {
         this.field_228197_f_ = CreateWorldScreen.GameMode.SURVIVAL;
      } else if (p_146318_1_.getGameType().isCreative()) {
         this.field_228197_f_ = CreateWorldScreen.GameMode.CREATIVE;
      }

   }

   @OnlyIn(Dist.CLIENT)
   static enum GameMode {
      SURVIVAL("survival", GameType.SURVIVAL),
      HARDCORE("hardcore", GameType.SURVIVAL),
      CREATIVE("creative", GameType.CREATIVE),
      DEBUG("spectator", GameType.SPECTATOR);

      private final String field_228217_e_;
      private final GameType field_228218_f_;

      private GameMode(String p_i225940_3_, GameType p_i225940_4_) {
         this.field_228217_e_ = p_i225940_3_;
         this.field_228218_f_ = p_i225940_4_;
      }
   }
}
