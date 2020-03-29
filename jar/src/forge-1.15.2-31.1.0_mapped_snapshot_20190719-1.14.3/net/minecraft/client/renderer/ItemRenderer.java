package net.minecraft.client.renderer;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements IResourceManagerReloadListener {
   public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> ITEM_MODEL_BLACKLIST;
   public float zLevel;
   private final ItemModelMesher itemModelMesher;
   private final TextureManager textureManager;
   private final ItemColors itemColors;

   public ItemRenderer(TextureManager p_i46552_1_, ModelManager p_i46552_2_, ItemColors p_i46552_3_) {
      this.textureManager = p_i46552_1_;
      this.itemModelMesher = new ItemModelMesherForge(p_i46552_2_);
      Iterator var4 = Registry.ITEM.iterator();

      while(var4.hasNext()) {
         Item item = (Item)var4.next();
         if (!ITEM_MODEL_BLACKLIST.contains(item)) {
            this.itemModelMesher.register(item, new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory"));
         }
      }

      this.itemColors = p_i46552_3_;
   }

   public ItemModelMesher getItemModelMesher() {
      return this.itemModelMesher;
   }

   private void func_229114_a_(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_) {
      Random random = new Random();
      long i = 42L;
      Direction[] var10 = Direction.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Direction direction = var10[var12];
         random.setSeed(42L);
         this.func_229112_a_(p_229114_5_, p_229114_6_, p_229114_1_.getQuads((BlockState)null, direction, random), p_229114_2_, p_229114_3_, p_229114_4_);
      }

      random.setSeed(42L);
      this.func_229112_a_(p_229114_5_, p_229114_6_, p_229114_1_.getQuads((BlockState)null, (Direction)null, random), p_229114_2_, p_229114_3_, p_229114_4_);
   }

   public void func_229111_a_(ItemStack p_229111_1_, ItemCameraTransforms.TransformType p_229111_2_, boolean p_229111_3_, MatrixStack p_229111_4_, IRenderTypeBuffer p_229111_5_, int p_229111_6_, int p_229111_7_, IBakedModel p_229111_8_) {
      if (!p_229111_1_.isEmpty()) {
         p_229111_4_.func_227860_a_();
         boolean flag = p_229111_2_ == ItemCameraTransforms.TransformType.GUI;
         boolean flag1 = flag || p_229111_2_ == ItemCameraTransforms.TransformType.GROUND || p_229111_2_ == ItemCameraTransforms.TransformType.FIXED;
         if (p_229111_1_.getItem() == Items.TRIDENT && flag1) {
            p_229111_8_ = this.itemModelMesher.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
         }

         p_229111_8_ = ForgeHooksClient.handleCameraTransforms(p_229111_4_, p_229111_8_, p_229111_2_, p_229111_3_);
         p_229111_4_.func_227861_a_(-0.5D, -0.5D, -0.5D);
         if (p_229111_8_.isBuiltInRenderer() || p_229111_1_.getItem() == Items.TRIDENT && !flag1) {
            p_229111_1_.getItem().getItemStackTileEntityRenderer().func_228364_a_(p_229111_1_, p_229111_4_, p_229111_5_, p_229111_6_, p_229111_7_);
         } else {
            RenderType rendertype = RenderTypeLookup.func_228389_a_(p_229111_1_);
            RenderType rendertype1;
            if (flag && Objects.equals(rendertype, Atlases.func_228784_i_())) {
               rendertype1 = Atlases.func_228785_j_();
            } else {
               rendertype1 = rendertype;
            }

            IVertexBuilder ivertexbuilder = func_229113_a_(p_229111_5_, rendertype1, true, p_229111_1_.hasEffect());
            this.func_229114_a_(p_229111_8_, p_229111_1_, p_229111_6_, p_229111_7_, p_229111_4_, ivertexbuilder);
         }

         p_229111_4_.func_227865_b_();
      }

   }

   public static IVertexBuilder func_229113_a_(IRenderTypeBuffer p_229113_0_, RenderType p_229113_1_, boolean p_229113_2_, boolean p_229113_3_) {
      return p_229113_3_ ? VertexBuilderUtils.func_227915_a_(p_229113_0_.getBuffer(p_229113_2_ ? RenderType.func_228653_j_() : RenderType.func_228655_k_()), p_229113_0_.getBuffer(p_229113_1_)) : p_229113_0_.getBuffer(p_229113_1_);
   }

   public void func_229112_a_(MatrixStack p_229112_1_, IVertexBuilder p_229112_2_, List<BakedQuad> p_229112_3_, ItemStack p_229112_4_, int p_229112_5_, int p_229112_6_) {
      boolean flag = !p_229112_4_.isEmpty();
      MatrixStack.Entry matrixstack$entry = p_229112_1_.func_227866_c_();
      Iterator var9 = p_229112_3_.iterator();

      while(var9.hasNext()) {
         BakedQuad bakedquad = (BakedQuad)var9.next();
         int i = -1;
         if (flag && bakedquad.hasTintIndex()) {
            i = this.itemColors.getColor(p_229112_4_, bakedquad.getTintIndex());
         }

         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         p_229112_2_.addVertexData(matrixstack$entry, bakedquad, f, f1, f2, p_229112_5_, p_229112_6_, true);
      }

   }

   public IBakedModel getItemModelWithOverrides(ItemStack p_184393_1_, @Nullable World p_184393_2_, @Nullable LivingEntity p_184393_3_) {
      Item item = p_184393_1_.getItem();
      IBakedModel ibakedmodel;
      if (item == Items.TRIDENT) {
         ibakedmodel = this.itemModelMesher.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         ibakedmodel = this.itemModelMesher.getItemModel(p_184393_1_);
      }

      return !item.hasCustomProperties() ? ibakedmodel : this.getModelWithOverrides(ibakedmodel, p_184393_1_, p_184393_2_, p_184393_3_);
   }

   private IBakedModel getModelWithOverrides(IBakedModel p_204207_1_, ItemStack p_204207_2_, @Nullable World p_204207_3_, @Nullable LivingEntity p_204207_4_) {
      IBakedModel ibakedmodel = p_204207_1_.getOverrides().getModelWithOverrides(p_204207_1_, p_204207_2_, p_204207_3_, p_204207_4_);
      return ibakedmodel == null ? this.itemModelMesher.getModelManager().getMissingModel() : ibakedmodel;
   }

   public void func_229110_a_(ItemStack p_229110_1_, ItemCameraTransforms.TransformType p_229110_2_, int p_229110_3_, int p_229110_4_, MatrixStack p_229110_5_, IRenderTypeBuffer p_229110_6_) {
      this.func_229109_a_((LivingEntity)null, p_229110_1_, p_229110_2_, false, p_229110_5_, p_229110_6_, (World)null, p_229110_3_, p_229110_4_);
   }

   public void func_229109_a_(@Nullable LivingEntity p_229109_1_, ItemStack p_229109_2_, ItemCameraTransforms.TransformType p_229109_3_, boolean p_229109_4_, MatrixStack p_229109_5_, IRenderTypeBuffer p_229109_6_, @Nullable World p_229109_7_, int p_229109_8_, int p_229109_9_) {
      if (!p_229109_2_.isEmpty()) {
         IBakedModel ibakedmodel = this.getItemModelWithOverrides(p_229109_2_, p_229109_7_, p_229109_1_);
         this.func_229111_a_(p_229109_2_, p_229109_3_, p_229109_4_, p_229109_5_, p_229109_6_, p_229109_8_, p_229109_9_, ibakedmodel);
      }

   }

   public void renderItemIntoGUI(ItemStack p_175042_1_, int p_175042_2_, int p_175042_3_) {
      this.renderItemModelIntoGUI(p_175042_1_, p_175042_2_, p_175042_3_, this.getItemModelWithOverrides(p_175042_1_, (World)null, (LivingEntity)null));
   }

   protected void renderItemModelIntoGUI(ItemStack p_191962_1_, int p_191962_2_, int p_191962_3_, IBakedModel p_191962_4_) {
      RenderSystem.pushMatrix();
      this.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
      this.textureManager.func_229267_b_(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.translatef((float)p_191962_2_, (float)p_191962_3_, 100.0F + this.zLevel);
      RenderSystem.translatef(8.0F, 8.0F, 0.0F);
      RenderSystem.scalef(1.0F, -1.0F, 1.0F);
      RenderSystem.scalef(16.0F, 16.0F, 16.0F);
      MatrixStack matrixstack = new MatrixStack();
      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().func_228019_au_().func_228487_b_();
      boolean flag = !p_191962_4_.func_230044_c_();
      if (flag) {
         RenderHelper.func_227783_c_();
      }

      this.func_229111_a_(p_191962_1_, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.field_229196_a_, p_191962_4_);
      irendertypebuffer$impl.func_228461_a_();
      RenderSystem.enableDepthTest();
      if (flag) {
         RenderHelper.func_227784_d_();
      }

      RenderSystem.disableAlphaTest();
      RenderSystem.disableRescaleNormal();
      RenderSystem.popMatrix();
   }

   public void renderItemAndEffectIntoGUI(ItemStack p_180450_1_, int p_180450_2_, int p_180450_3_) {
      this.renderItemAndEffectIntoGUI(Minecraft.getInstance().player, p_180450_1_, p_180450_2_, p_180450_3_);
   }

   public void renderItemAndEffectIntoGUI(@Nullable LivingEntity p_184391_1_, ItemStack p_184391_2_, int p_184391_3_, int p_184391_4_) {
      if (!p_184391_2_.isEmpty()) {
         this.zLevel += 50.0F;

         try {
            this.renderItemModelIntoGUI(p_184391_2_, p_184391_3_, p_184391_4_, this.getItemModelWithOverrides(p_184391_2_, (World)null, p_184391_1_));
         } catch (Throwable var8) {
            CrashReport crashreport = CrashReport.makeCrashReport(var8, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
            crashreportcategory.addDetail("Item Type", () -> {
               return String.valueOf(p_184391_2_.getItem());
            });
            crashreportcategory.addDetail("Registry Name", () -> {
               return String.valueOf(p_184391_2_.getItem().getRegistryName());
            });
            crashreportcategory.addDetail("Item Damage", () -> {
               return String.valueOf(p_184391_2_.getDamage());
            });
            crashreportcategory.addDetail("Item NBT", () -> {
               return String.valueOf(p_184391_2_.getTag());
            });
            crashreportcategory.addDetail("Item Foil", () -> {
               return String.valueOf(p_184391_2_.hasEffect());
            });
            throw new ReportedException(crashreport);
         }

         this.zLevel -= 50.0F;
      }

   }

   public void renderItemOverlays(FontRenderer p_175030_1_, ItemStack p_175030_2_, int p_175030_3_, int p_175030_4_) {
      this.renderItemOverlayIntoGUI(p_175030_1_, p_175030_2_, p_175030_3_, p_175030_4_, (String)null);
   }

   public void renderItemOverlayIntoGUI(FontRenderer p_180453_1_, ItemStack p_180453_2_, int p_180453_3_, int p_180453_4_, @Nullable String p_180453_5_) {
      if (!p_180453_2_.isEmpty()) {
         MatrixStack matrixstack = new MatrixStack();
         if (p_180453_2_.getCount() != 1 || p_180453_5_ != null) {
            String s = p_180453_5_ == null ? String.valueOf(p_180453_2_.getCount()) : p_180453_5_;
            matrixstack.func_227861_a_(0.0D, 0.0D, (double)(this.zLevel + 200.0F));
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.func_228455_a_(Tessellator.getInstance().getBuffer());
            p_180453_1_.func_228079_a_(s, (float)(p_180453_3_ + 19 - 2 - p_180453_1_.getStringWidth(s)), (float)(p_180453_4_ + 6 + 3), 16777215, true, matrixstack.func_227866_c_().func_227870_a_(), irendertypebuffer$impl, false, 0, 15728880);
            irendertypebuffer$impl.func_228461_a_();
         }

         if (p_180453_2_.getItem().showDurabilityBar(p_180453_2_)) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            double health = p_180453_2_.getItem().getDurabilityForDisplay(p_180453_2_);
            int i = Math.round(13.0F - (float)health * 13.0F);
            int j = p_180453_2_.getItem().getRGBDurabilityForDisplay(p_180453_2_);
            this.draw(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, 13, 2, 0, 0, 0, 255);
            this.draw(bufferbuilder, p_180453_3_ + 2, p_180453_4_ + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

         ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
         float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldownTracker().getCooldown(p_180453_2_.getItem(), Minecraft.getInstance().getRenderPartialTicks());
         if (f3 > 0.0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
            this.draw(bufferbuilder1, p_180453_3_, p_180453_4_ + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }
      }

   }

   private void draw(BufferBuilder p_181565_1_, int p_181565_2_, int p_181565_3_, int p_181565_4_, int p_181565_5_, int p_181565_6_, int p_181565_7_, int p_181565_8_, int p_181565_9_) {
      p_181565_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
      p_181565_1_.func_225582_a_((double)(p_181565_2_ + 0), (double)(p_181565_3_ + 0), 0.0D).func_225586_a_(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.func_225582_a_((double)(p_181565_2_ + 0), (double)(p_181565_3_ + p_181565_5_), 0.0D).func_225586_a_(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.func_225582_a_((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + p_181565_5_), 0.0D).func_225586_a_(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      p_181565_1_.func_225582_a_((double)(p_181565_2_ + p_181565_4_), (double)(p_181565_3_ + 0), 0.0D).func_225586_a_(p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_).endVertex();
      Tessellator.getInstance().draw();
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.itemModelMesher.rebuildCache();
   }

   public IResourceType getResourceType() {
      return VanillaResourceType.MODELS;
   }

   static {
      ITEM_MODEL_BLACKLIST = Sets.newHashSet(new Item[]{Items.AIR});
   }
}
