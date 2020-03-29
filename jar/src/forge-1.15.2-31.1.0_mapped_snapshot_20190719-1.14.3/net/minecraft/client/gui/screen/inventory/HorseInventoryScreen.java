package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseInventoryScreen extends ContainerScreen<HorseInventoryContainer> {
   private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/horse.png");
   private final AbstractHorseEntity horseEntity;
   private float mousePosx;
   private float mousePosY;

   public HorseInventoryScreen(HorseInventoryContainer p_i51084_1_, PlayerInventory p_i51084_2_, AbstractHorseEntity p_i51084_3_) {
      super(p_i51084_1_, p_i51084_2_, p_i51084_3_.getDisplayName());
      this.horseEntity = p_i51084_3_;
      this.passEvents = false;
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
      this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
      int lvt_4_1_ = (this.width - this.xSize) / 2;
      int lvt_5_1_ = (this.height - this.ySize) / 2;
      this.blit(lvt_4_1_, lvt_5_1_, 0, 0, this.xSize, this.ySize);
      if (this.horseEntity instanceof AbstractChestedHorseEntity) {
         AbstractChestedHorseEntity lvt_6_1_ = (AbstractChestedHorseEntity)this.horseEntity;
         if (lvt_6_1_.hasChest()) {
            this.blit(lvt_4_1_ + 79, lvt_5_1_ + 17, 0, this.ySize, lvt_6_1_.getInventoryColumns() * 18, 54);
         }
      }

      if (this.horseEntity.canBeSaddled()) {
         this.blit(lvt_4_1_ + 7, lvt_5_1_ + 35 - 18, 18, this.ySize + 54, 18, 18);
      }

      if (this.horseEntity.wearsArmor()) {
         if (this.horseEntity instanceof LlamaEntity) {
            this.blit(lvt_4_1_ + 7, lvt_5_1_ + 35, 36, this.ySize + 54, 18, 18);
         } else {
            this.blit(lvt_4_1_ + 7, lvt_5_1_ + 35, 0, this.ySize + 54, 18, 18);
         }
      }

      InventoryScreen.func_228187_a_(lvt_4_1_ + 51, lvt_5_1_ + 60, 17, (float)(lvt_4_1_ + 51) - this.mousePosx, (float)(lvt_5_1_ + 75 - 50) - this.mousePosY, this.horseEntity);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.mousePosx = (float)p_render_1_;
      this.mousePosY = (float)p_render_2_;
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.renderHoveredToolTip(p_render_1_, p_render_2_);
   }
}
