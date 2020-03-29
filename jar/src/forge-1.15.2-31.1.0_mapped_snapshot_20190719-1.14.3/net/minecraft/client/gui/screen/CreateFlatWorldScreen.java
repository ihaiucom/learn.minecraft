package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateFlatWorldScreen extends Screen {
   private final CreateWorldScreen createWorldGui;
   private FlatGenerationSettings generatorInfo = FlatGenerationSettings.getDefaultFlatGenerator();
   private String materialText;
   private String heightText;
   private CreateFlatWorldScreen.DetailsList createFlatWorldListSlotGui;
   private Button removeLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen p_i49700_1_, CompoundNBT p_i49700_2_) {
      super(new TranslationTextComponent("createWorld.customize.flat.title", new Object[0]));
      this.createWorldGui = p_i49700_1_;
      this.setGeneratorOptions(p_i49700_2_);
   }

   public String getPreset() {
      return this.generatorInfo.toString();
   }

   public CompoundNBT getGeneratorOptions() {
      return (CompoundNBT)this.generatorInfo.func_210834_a(NBTDynamicOps.INSTANCE).getValue();
   }

   public void setPreset(String p_210502_1_) {
      this.generatorInfo = FlatGenerationSettings.createFlatGeneratorFromString(p_210502_1_);
   }

   public void setGeneratorOptions(CompoundNBT p_210503_1_) {
      this.generatorInfo = FlatGenerationSettings.createFlatGenerator(new Dynamic(NBTDynamicOps.INSTANCE, p_210503_1_));
   }

   protected void init() {
      this.materialText = I18n.format("createWorld.customize.flat.tile");
      this.heightText = I18n.format("createWorld.customize.flat.height");
      this.createFlatWorldListSlotGui = new CreateFlatWorldScreen.DetailsList();
      this.children.add(this.createFlatWorldListSlotGui);
      this.removeLayerButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer"), (p_213007_1_) -> {
         if (this.hasSelectedLayer()) {
            List<FlatLayerInfo> lvt_2_1_ = this.generatorInfo.getFlatLayers();
            int lvt_3_1_ = this.createFlatWorldListSlotGui.children().indexOf(this.createFlatWorldListSlotGui.getSelected());
            int lvt_4_1_ = lvt_2_1_.size() - lvt_3_1_ - 1;
            lvt_2_1_.remove(lvt_4_1_);
            this.createFlatWorldListSlotGui.setSelected(lvt_2_1_.isEmpty() ? null : (CreateFlatWorldScreen.DetailsList.LayerEntry)this.createFlatWorldListSlotGui.children().get(Math.min(lvt_3_1_, lvt_2_1_.size() - 1)));
            this.generatorInfo.updateLayers();
            this.onLayersChanged();
         }
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets"), (p_213011_1_) -> {
         this.minecraft.displayGuiScreen(new FlatPresetsScreen(this));
         this.generatorInfo.updateLayers();
         this.onLayersChanged();
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done"), (p_213010_1_) -> {
         this.createWorldGui.chunkProviderSettingsJson = this.getGeneratorOptions();
         this.minecraft.displayGuiScreen(this.createWorldGui);
         this.generatorInfo.updateLayers();
         this.onLayersChanged();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_213009_1_) -> {
         this.minecraft.displayGuiScreen(this.createWorldGui);
         this.generatorInfo.updateLayers();
         this.onLayersChanged();
      }));
      this.generatorInfo.updateLayers();
      this.onLayersChanged();
   }

   public void onLayersChanged() {
      this.removeLayerButton.active = this.hasSelectedLayer();
      this.createFlatWorldListSlotGui.func_214345_a();
   }

   private boolean hasSelectedLayer() {
      return this.createFlatWorldListSlotGui.getSelected() != null;
   }

   public void onClose() {
      this.minecraft.displayGuiScreen(this.createWorldGui);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.createFlatWorldListSlotGui.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
      int lvt_4_1_ = this.width / 2 - 92 - 16;
      this.drawString(this.font, this.materialText, lvt_4_1_, 32, 16777215);
      this.drawString(this.font, this.heightText, lvt_4_1_ + 2 + 213 - this.font.getStringWidth(this.heightText), 32, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class DetailsList extends ExtendedList<CreateFlatWorldScreen.DetailsList.LayerEntry> {
      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);

         for(int lvt_2_1_ = 0; lvt_2_1_ < CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size(); ++lvt_2_1_) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.LayerEntry());
         }

      }

      public void setSelected(@Nullable CreateFlatWorldScreen.DetailsList.LayerEntry p_setSelected_1_) {
         super.setSelected(p_setSelected_1_);
         if (p_setSelected_1_ != null) {
            FlatLayerInfo lvt_2_1_ = (FlatLayerInfo)CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().get(CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - this.children().indexOf(p_setSelected_1_) - 1);
            Item lvt_3_1_ = lvt_2_1_.getLayerMaterial().getBlock().asItem();
            if (lvt_3_1_ != Items.AIR) {
               NarratorChatListener.INSTANCE.func_216864_a((new TranslationTextComponent("narrator.select", new Object[]{lvt_3_1_.getDisplayName(new ItemStack(lvt_3_1_))})).getString());
            }
         }

      }

      protected void moveSelection(int p_moveSelection_1_) {
         super.moveSelection(p_moveSelection_1_);
         CreateFlatWorldScreen.this.onLayersChanged();
      }

      protected boolean isFocused() {
         return CreateFlatWorldScreen.this.getFocused() == this;
      }

      protected int getScrollbarPosition() {
         return this.width - 70;
      }

      public void func_214345_a() {
         int lvt_1_1_ = this.children().indexOf(this.getSelected());
         this.clearEntries();

         for(int lvt_2_1_ = 0; lvt_2_1_ < CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size(); ++lvt_2_1_) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.LayerEntry());
         }

         List<CreateFlatWorldScreen.DetailsList.LayerEntry> lvt_2_2_ = this.children();
         if (lvt_1_1_ >= 0 && lvt_1_1_ < lvt_2_2_.size()) {
            this.setSelected((CreateFlatWorldScreen.DetailsList.LayerEntry)lvt_2_2_.get(lvt_1_1_));
         }

      }

      // $FF: synthetic method
      public void setSelected(@Nullable AbstractList.AbstractListEntry p_setSelected_1_) {
         this.setSelected((CreateFlatWorldScreen.DetailsList.LayerEntry)p_setSelected_1_);
      }

      @OnlyIn(Dist.CLIENT)
      class LayerEntry extends ExtendedList.AbstractListEntry<CreateFlatWorldScreen.DetailsList.LayerEntry> {
         private LayerEntry() {
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            FlatLayerInfo lvt_10_1_ = (FlatLayerInfo)CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().get(CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - p_render_1_ - 1);
            BlockState lvt_11_1_ = lvt_10_1_.getLayerMaterial();
            Block lvt_12_1_ = lvt_11_1_.getBlock();
            Item lvt_13_1_ = lvt_12_1_.asItem();
            if (lvt_13_1_ == Items.AIR) {
               if (lvt_12_1_ == Blocks.WATER) {
                  lvt_13_1_ = Items.WATER_BUCKET;
               } else if (lvt_12_1_ == Blocks.LAVA) {
                  lvt_13_1_ = Items.LAVA_BUCKET;
               }
            }

            ItemStack lvt_14_1_ = new ItemStack(lvt_13_1_);
            String lvt_15_1_ = lvt_13_1_.getDisplayName(lvt_14_1_).getFormattedText();
            this.func_214389_a(p_render_3_, p_render_2_, lvt_14_1_);
            CreateFlatWorldScreen.this.font.drawString(lvt_15_1_, (float)(p_render_3_ + 18 + 5), (float)(p_render_2_ + 3), 16777215);
            String lvt_16_3_;
            if (p_render_1_ == 0) {
               lvt_16_3_ = I18n.format("createWorld.customize.flat.layer.top", lvt_10_1_.getLayerCount());
            } else if (p_render_1_ == CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - 1) {
               lvt_16_3_ = I18n.format("createWorld.customize.flat.layer.bottom", lvt_10_1_.getLayerCount());
            } else {
               lvt_16_3_ = I18n.format("createWorld.customize.flat.layer", lvt_10_1_.getLayerCount());
            }

            CreateFlatWorldScreen.this.font.drawString(lvt_16_3_, (float)(p_render_3_ + 2 + 213 - CreateFlatWorldScreen.this.font.getStringWidth(lvt_16_3_)), (float)(p_render_2_ + 3), 16777215);
         }

         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
               DetailsList.this.setSelected(this);
               CreateFlatWorldScreen.this.onLayersChanged();
               return true;
            } else {
               return false;
            }
         }

         private void func_214389_a(int p_214389_1_, int p_214389_2_, ItemStack p_214389_3_) {
            this.func_214390_a(p_214389_1_ + 1, p_214389_2_ + 1);
            RenderSystem.enableRescaleNormal();
            if (!p_214389_3_.isEmpty()) {
               CreateFlatWorldScreen.this.itemRenderer.renderItemIntoGUI(p_214389_3_, p_214389_1_ + 2, p_214389_2_ + 2);
            }

            RenderSystem.disableRescaleNormal();
         }

         private void func_214390_a(int p_214390_1_, int p_214390_2_) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            DetailsList.this.minecraft.getTextureManager().bindTexture(AbstractGui.STATS_ICON_LOCATION);
            AbstractGui.blit(p_214390_1_, p_214390_2_, CreateFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }

         // $FF: synthetic method
         LayerEntry(Object p_i50625_2_) {
            this();
         }
      }
   }
}
