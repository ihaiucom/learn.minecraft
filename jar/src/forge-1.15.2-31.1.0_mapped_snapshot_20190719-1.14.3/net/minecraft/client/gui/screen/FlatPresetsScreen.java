package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlatPresetsScreen extends Screen {
   private static final List<FlatPresetsScreen.LayerItem> FLAT_WORLD_PRESETS = Lists.newArrayList();
   private final CreateFlatWorldScreen parentScreen;
   private String presetsShare;
   private String listText;
   private FlatPresetsScreen.SlotList list;
   private Button btnSelect;
   private TextFieldWidget export;

   public FlatPresetsScreen(CreateFlatWorldScreen p_i46318_1_) {
      super(new TranslationTextComponent("createWorld.customize.presets.title", new Object[0]));
      this.parentScreen = p_i46318_1_;
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.presetsShare = I18n.format("createWorld.customize.presets.share");
      this.listText = I18n.format("createWorld.customize.presets.list");
      this.export = new TextFieldWidget(this.font, 50, 40, this.width - 100, 20, this.presetsShare);
      this.export.setMaxStringLength(1230);
      this.export.setText(this.parentScreen.getPreset());
      this.children.add(this.export);
      this.list = new FlatPresetsScreen.SlotList();
      this.children.add(this.list);
      this.btnSelect = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("createWorld.customize.presets.select"), (p_213077_1_) -> {
         this.parentScreen.setPreset(this.export.getText());
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_213076_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      this.func_213074_a(this.list.getSelected() != null);
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.list.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String lvt_4_1_ = this.export.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.export.setText(lvt_4_1_);
   }

   public void onClose() {
      this.minecraft.displayGuiScreen(this.parentScreen);
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.list.render(p_render_1_, p_render_2_, p_render_3_);
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, 0.0F, 400.0F);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
      this.drawString(this.font, this.presetsShare, 50, 30, 10526880);
      this.drawString(this.font, this.listText, 50, 70, 10526880);
      RenderSystem.popMatrix();
      this.export.render(p_render_1_, p_render_2_, p_render_3_);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void tick() {
      this.export.tick();
      super.tick();
   }

   public void func_213074_a(boolean p_213074_1_) {
      this.btnSelect.active = p_213074_1_ || this.export.getText().length() > 1;
   }

   private static void addPreset(String p_199709_0_, IItemProvider p_199709_1_, Biome p_199709_2_, List<String> p_199709_3_, FlatLayerInfo... p_199709_4_) {
      FlatGenerationSettings lvt_5_1_ = (FlatGenerationSettings)ChunkGeneratorType.FLAT.createSettings();

      for(int lvt_6_1_ = p_199709_4_.length - 1; lvt_6_1_ >= 0; --lvt_6_1_) {
         lvt_5_1_.getFlatLayers().add(p_199709_4_[lvt_6_1_]);
      }

      lvt_5_1_.setBiome(p_199709_2_);
      lvt_5_1_.updateLayers();
      Iterator var8 = p_199709_3_.iterator();

      while(var8.hasNext()) {
         String lvt_7_1_ = (String)var8.next();
         lvt_5_1_.getWorldFeatures().put(lvt_7_1_, Maps.newHashMap());
      }

      FLAT_WORLD_PRESETS.add(new FlatPresetsScreen.LayerItem(p_199709_1_.asItem(), p_199709_0_, lvt_5_1_.toString()));
   }

   static {
      addPreset(I18n.format("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, Arrays.asList("village"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.MOUNTAINS, Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Arrays.asList("biome_1", "oceanmonument"), new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.SAND), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_TUNDRA, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Arrays.asList("village", "biome_1"), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
      addPreset(I18n.format("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"), new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      addPreset(I18n.format("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, Arrays.asList("decoration"), new FlatLayerInfo(1, Blocks.AIR));
   }

   @OnlyIn(Dist.CLIENT)
   static class LayerItem {
      public final Item icon;
      public final String name;
      public final String generatorInfo;

      public LayerItem(Item p_i48059_1_, String p_i48059_2_, String p_i48059_3_) {
         this.icon = p_i48059_1_;
         this.name = p_i48059_2_;
         this.generatorInfo = p_i48059_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class SlotList extends ExtendedList<FlatPresetsScreen.SlotList.PresetEntry> {
      public SlotList() {
         super(FlatPresetsScreen.this.minecraft, FlatPresetsScreen.this.width, FlatPresetsScreen.this.height, 80, FlatPresetsScreen.this.height - 37, 24);

         for(int lvt_2_1_ = 0; lvt_2_1_ < FlatPresetsScreen.FLAT_WORLD_PRESETS.size(); ++lvt_2_1_) {
            this.addEntry(new FlatPresetsScreen.SlotList.PresetEntry());
         }

      }

      public void setSelected(@Nullable FlatPresetsScreen.SlotList.PresetEntry p_setSelected_1_) {
         super.setSelected(p_setSelected_1_);
         if (p_setSelected_1_ != null) {
            NarratorChatListener.INSTANCE.func_216864_a((new TranslationTextComponent("narrator.select", new Object[]{((FlatPresetsScreen.LayerItem)FlatPresetsScreen.FLAT_WORLD_PRESETS.get(this.children().indexOf(p_setSelected_1_))).name})).getString());
         }

      }

      protected void moveSelection(int p_moveSelection_1_) {
         super.moveSelection(p_moveSelection_1_);
         FlatPresetsScreen.this.func_213074_a(true);
      }

      protected boolean isFocused() {
         return FlatPresetsScreen.this.getFocused() == this;
      }

      public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
         if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
         } else {
            if ((p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335) && this.getSelected() != null) {
               ((FlatPresetsScreen.SlotList.PresetEntry)this.getSelected()).func_214399_a();
            }

            return false;
         }
      }

      // $FF: synthetic method
      public void setSelected(@Nullable AbstractList.AbstractListEntry p_setSelected_1_) {
         this.setSelected((FlatPresetsScreen.SlotList.PresetEntry)p_setSelected_1_);
      }

      @OnlyIn(Dist.CLIENT)
      public class PresetEntry extends ExtendedList.AbstractListEntry<FlatPresetsScreen.SlotList.PresetEntry> {
         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            FlatPresetsScreen.LayerItem lvt_10_1_ = (FlatPresetsScreen.LayerItem)FlatPresetsScreen.FLAT_WORLD_PRESETS.get(p_render_1_);
            this.func_214402_a(p_render_3_, p_render_2_, lvt_10_1_.icon);
            FlatPresetsScreen.this.font.drawString(lvt_10_1_.name, (float)(p_render_3_ + 18 + 5), (float)(p_render_2_ + 6), 16777215);
         }

         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
               this.func_214399_a();
            }

            return false;
         }

         private void func_214399_a() {
            SlotList.this.setSelected(this);
            FlatPresetsScreen.this.func_213074_a(true);
            FlatPresetsScreen.this.export.setText(((FlatPresetsScreen.LayerItem)FlatPresetsScreen.FLAT_WORLD_PRESETS.get(SlotList.this.children().indexOf(this))).generatorInfo);
            FlatPresetsScreen.this.export.setCursorPositionZero();
         }

         private void func_214402_a(int p_214402_1_, int p_214402_2_, Item p_214402_3_) {
            this.func_214400_a(p_214402_1_ + 1, p_214402_2_ + 1);
            RenderSystem.enableRescaleNormal();
            FlatPresetsScreen.this.itemRenderer.renderItemIntoGUI(new ItemStack(p_214402_3_), p_214402_1_ + 2, p_214402_2_ + 2);
            RenderSystem.disableRescaleNormal();
         }

         private void func_214400_a(int p_214400_1_, int p_214400_2_) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            SlotList.this.minecraft.getTextureManager().bindTexture(AbstractGui.STATS_ICON_LOCATION);
            AbstractGui.blit(p_214400_1_, p_214400_2_, FlatPresetsScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}
