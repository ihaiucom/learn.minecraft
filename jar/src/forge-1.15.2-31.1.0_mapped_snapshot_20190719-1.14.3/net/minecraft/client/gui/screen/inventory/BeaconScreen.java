package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeaconScreen extends ContainerScreen<BeaconContainer> {
   private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("textures/gui/container/beacon.png");
   private BeaconScreen.ConfirmButton beaconConfirmButton;
   private boolean buttonsNotDrawn;
   private Effect field_214105_n;
   private Effect field_214106_o;

   public BeaconScreen(final BeaconContainer p_i51102_1_, PlayerInventory p_i51102_2_, ITextComponent p_i51102_3_) {
      super(p_i51102_1_, p_i51102_2_, p_i51102_3_);
      this.xSize = 230;
      this.ySize = 219;
      p_i51102_1_.addListener(new IContainerListener() {
         public void sendAllContents(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
         }

         public void sendSlotContents(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
         }

         public void sendWindowProperty(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
            BeaconScreen.this.field_214105_n = p_i51102_1_.func_216967_f();
            BeaconScreen.this.field_214106_o = p_i51102_1_.func_216968_g();
            BeaconScreen.this.buttonsNotDrawn = true;
         }
      });
   }

   protected void init() {
      super.init();
      this.beaconConfirmButton = (BeaconScreen.ConfirmButton)this.addButton(new BeaconScreen.ConfirmButton(this.guiLeft + 164, this.guiTop + 107));
      this.addButton(new BeaconScreen.CancelButton(this.guiLeft + 190, this.guiTop + 107));
      this.buttonsNotDrawn = true;
      this.beaconConfirmButton.active = false;
   }

   public void tick() {
      super.tick();
      int lvt_1_1_ = ((BeaconContainer)this.container).func_216969_e();
      if (this.buttonsNotDrawn && lvt_1_1_ >= 0) {
         this.buttonsNotDrawn = false;

         int lvt_3_2_;
         int lvt_4_2_;
         int lvt_5_2_;
         Effect lvt_6_2_;
         BeaconScreen.PowerButton lvt_7_2_;
         for(int lvt_2_1_ = 0; lvt_2_1_ <= 2; ++lvt_2_1_) {
            lvt_3_2_ = BeaconTileEntity.EFFECTS_LIST[lvt_2_1_].length;
            lvt_4_2_ = lvt_3_2_ * 22 + (lvt_3_2_ - 1) * 2;

            for(lvt_5_2_ = 0; lvt_5_2_ < lvt_3_2_; ++lvt_5_2_) {
               lvt_6_2_ = BeaconTileEntity.EFFECTS_LIST[lvt_2_1_][lvt_5_2_];
               lvt_7_2_ = new BeaconScreen.PowerButton(this.guiLeft + 76 + lvt_5_2_ * 24 - lvt_4_2_ / 2, this.guiTop + 22 + lvt_2_1_ * 25, lvt_6_2_, true);
               this.addButton(lvt_7_2_);
               if (lvt_2_1_ >= lvt_1_1_) {
                  lvt_7_2_.active = false;
               } else if (lvt_6_2_ == this.field_214105_n) {
                  lvt_7_2_.setSelected(true);
               }
            }
         }

         int lvt_2_2_ = true;
         lvt_3_2_ = BeaconTileEntity.EFFECTS_LIST[3].length + 1;
         lvt_4_2_ = lvt_3_2_ * 22 + (lvt_3_2_ - 1) * 2;

         for(lvt_5_2_ = 0; lvt_5_2_ < lvt_3_2_ - 1; ++lvt_5_2_) {
            lvt_6_2_ = BeaconTileEntity.EFFECTS_LIST[3][lvt_5_2_];
            lvt_7_2_ = new BeaconScreen.PowerButton(this.guiLeft + 167 + lvt_5_2_ * 24 - lvt_4_2_ / 2, this.guiTop + 47, lvt_6_2_, false);
            this.addButton(lvt_7_2_);
            if (3 >= lvt_1_1_) {
               lvt_7_2_.active = false;
            } else if (lvt_6_2_ == this.field_214106_o) {
               lvt_7_2_.setSelected(true);
            }
         }

         if (this.field_214105_n != null) {
            BeaconScreen.PowerButton lvt_5_3_ = new BeaconScreen.PowerButton(this.guiLeft + 167 + (lvt_3_2_ - 1) * 24 - lvt_4_2_ / 2, this.guiTop + 47, this.field_214105_n, false);
            this.addButton(lvt_5_3_);
            if (3 >= lvt_1_1_) {
               lvt_5_3_.active = false;
            } else if (this.field_214105_n == this.field_214106_o) {
               lvt_5_3_.setSelected(true);
            }
         }
      }

      this.beaconConfirmButton.active = ((BeaconContainer)this.container).func_216970_h() && this.field_214105_n != null;
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.drawCenteredString(this.font, I18n.format("block.minecraft.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.font, I18n.format("block.minecraft.beacon.secondary"), 169, 10, 14737632);
      Iterator var3 = this.buttons.iterator();

      while(var3.hasNext()) {
         Widget lvt_4_1_ = (Widget)var3.next();
         if (lvt_4_1_.isHovered()) {
            lvt_4_1_.renderToolTip(p_146979_1_ - this.guiLeft, p_146979_2_ - this.guiTop);
            break;
         }
      }

   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
      int lvt_4_1_ = (this.width - this.xSize) / 2;
      int lvt_5_1_ = (this.height - this.ySize) / 2;
      this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
      this.itemRenderer.zLevel = 100.0F;
      this.itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(Items.EMERALD), lvt_4_1_ + 42, lvt_5_1_ + 109);
      this.itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(Items.DIAMOND), lvt_4_1_ + 42 + 22, lvt_5_1_ + 109);
      this.itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(Items.GOLD_INGOT), lvt_4_1_ + 42 + 44, lvt_5_1_ + 109);
      this.itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(Items.IRON_INGOT), lvt_4_1_ + 42 + 66, lvt_5_1_ + 109);
      this.itemRenderer.zLevel = 0.0F;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }

   @OnlyIn(Dist.CLIENT)
   class CancelButton extends BeaconScreen.SpriteButton {
      public CancelButton(int p_i50829_2_, int p_i50829_3_) {
         super(p_i50829_2_, p_i50829_3_, 112, 220);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.player.connection.sendPacket(new CCloseWindowPacket(BeaconScreen.this.minecraft.player.openContainer.windowId));
         BeaconScreen.this.minecraft.displayGuiScreen((Screen)null);
      }

      public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
         BeaconScreen.this.renderTooltip(I18n.format("gui.cancel"), p_renderToolTip_1_, p_renderToolTip_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ConfirmButton extends BeaconScreen.SpriteButton {
      public ConfirmButton(int p_i50828_2_, int p_i50828_3_) {
         super(p_i50828_2_, p_i50828_3_, 90, 220);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.getConnection().sendPacket(new CUpdateBeaconPacket(Effect.getId(BeaconScreen.this.field_214105_n), Effect.getId(BeaconScreen.this.field_214106_o)));
         BeaconScreen.this.minecraft.player.connection.sendPacket(new CCloseWindowPacket(BeaconScreen.this.minecraft.player.openContainer.windowId));
         BeaconScreen.this.minecraft.displayGuiScreen((Screen)null);
      }

      public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
         BeaconScreen.this.renderTooltip(I18n.format("gui.done"), p_renderToolTip_1_, p_renderToolTip_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class SpriteButton extends BeaconScreen.Button {
      private final int field_212948_a;
      private final int field_212949_b;

      protected SpriteButton(int p_i50825_1_, int p_i50825_2_, int p_i50825_3_, int p_i50825_4_) {
         super(p_i50825_1_, p_i50825_2_);
         this.field_212948_a = p_i50825_3_;
         this.field_212949_b = p_i50825_4_;
      }

      protected void func_212945_a() {
         this.blit(this.x + 2, this.y + 2, this.field_212948_a, this.field_212949_b, 18, 18);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PowerButton extends BeaconScreen.Button {
      private final Effect effect;
      private final TextureAtlasSprite field_212946_c;
      private final boolean field_212947_d;

      public PowerButton(int p_i50827_2_, int p_i50827_3_, Effect p_i50827_4_, boolean p_i50827_5_) {
         super(p_i50827_2_, p_i50827_3_);
         this.effect = p_i50827_4_;
         this.field_212946_c = Minecraft.getInstance().getPotionSpriteUploader().getSprite(p_i50827_4_);
         this.field_212947_d = p_i50827_5_;
      }

      public void onPress() {
         if (!this.isSelected()) {
            if (this.field_212947_d) {
               BeaconScreen.this.field_214105_n = this.effect;
            } else {
               BeaconScreen.this.field_214106_o = this.effect;
            }

            BeaconScreen.this.buttons.clear();
            BeaconScreen.this.children.clear();
            BeaconScreen.this.init();
            BeaconScreen.this.tick();
         }
      }

      public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
         String lvt_3_1_ = I18n.format(this.effect.getName());
         if (!this.field_212947_d && this.effect != Effects.REGENERATION) {
            lvt_3_1_ = lvt_3_1_ + " II";
         }

         BeaconScreen.this.renderTooltip(lvt_3_1_, p_renderToolTip_1_, p_renderToolTip_2_);
      }

      protected void func_212945_a() {
         Minecraft.getInstance().getTextureManager().bindTexture(this.field_212946_c.func_229241_m_().func_229223_g_());
         blit(this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.field_212946_c);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class Button extends AbstractButton {
      private boolean selected;

      protected Button(int p_i50826_1_, int p_i50826_2_) {
         super(p_i50826_1_, p_i50826_2_, 22, 22, "");
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         Minecraft.getInstance().getTextureManager().bindTexture(BeaconScreen.BEACON_GUI_TEXTURES);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int lvt_4_1_ = true;
         int lvt_5_1_ = 0;
         if (!this.active) {
            lvt_5_1_ += this.width * 2;
         } else if (this.selected) {
            lvt_5_1_ += this.width * 1;
         } else if (this.isHovered()) {
            lvt_5_1_ += this.width * 3;
         }

         this.blit(this.x, this.y, lvt_5_1_, 219, this.width, this.height);
         this.func_212945_a();
      }

      protected abstract void func_212945_a();

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean p_146140_1_) {
         this.selected = p_146140_1_;
      }
   }
}
