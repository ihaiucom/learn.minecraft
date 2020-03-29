package net.minecraftforge.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.ReloadRequirements;
import net.minecraftforge.resource.SelectiveReloadStateHandler;
import net.minecraftforge.resource.VanillaResourceType;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.async.ThreadNameCachingStrategy;
import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class ForgeHooksClient {
   private static final Logger LOGGER = LogManager.getLogger();
   static final ThreadLocal<RenderType> renderLayer = new ThreadLocal();
   private static int skyX;
   private static int skyZ;
   private static boolean skyInit;
   private static int skyRGBMultiplier;
   public static String forgeStatusLine;
   static int worldRenderPass;
   private static final Matrix4f flipX = Matrix4f.func_226593_a_(-1.0F, 1.0F, 1.0F);
   private static final Matrix3f flipXNormal;
   private static int slotMainHand;

   public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlotType slot, String type) {
      String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
      return result != null ? result : _default;
   }

   public static boolean onDrawBlockHighlight(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffers) {
      switch(target.getType()) {
      case BLOCK:
         if (!(target instanceof BlockRayTraceResult)) {
            return false;
         }

         return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent.HighlightBlock(context, info, target, partialTicks, matrix, buffers));
      case ENTITY:
         if (!(target instanceof EntityRayTraceResult)) {
            return false;
         }

         return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent.HighlightEntity(context, info, target, partialTicks, matrix, buffers));
      default:
         return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent(context, info, target, partialTicks, matrix, buffers));
      }
   }

   public static void dispatchRenderLast(WorldRenderer context, MatrixStack mat, float partialTicks) {
      MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, mat, partialTicks));
   }

   public static boolean renderSpecificFirstPersonHand(Hand hand, MatrixStack mat, IRenderTypeBuffer buffers, int light, float partialTicks, float interpPitch, float swingProgress, float equipProgress, ItemStack stack) {
      return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(hand, mat, buffers, light, partialTicks, interpPitch, swingProgress, equipProgress, stack));
   }

   public static void onTextureStitchedPre(AtlasTexture map, Set<ResourceLocation> resourceLocations) {
      ModLoader.get().postEvent(new TextureStitchEvent.Pre(map, resourceLocations));
   }

   public static void onTextureStitchedPost(AtlasTexture map) {
      ModLoader.get().postEvent(new TextureStitchEvent.Post(map));
   }

   public static void onBlockColorsInit(BlockColors blockColors) {
      ModLoader.get().postEvent(new ColorHandlerEvent.Block(blockColors));
   }

   public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors) {
      ModLoader.get().postEvent(new ColorHandlerEvent.Item(itemColors, blockColors));
   }

   public static void setRenderLayer(RenderType layer) {
      renderLayer.set(layer);
   }

   public static <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType slot, A _default) {
      A model = itemStack.getItem().getArmorModel(entityLiving, itemStack, slot, _default);
      return model == null ? _default : model;
   }

   public static String fixDomain(String base, String complex) {
      int idx = complex.indexOf(58);
      if (idx == -1) {
         return base + complex;
      } else {
         String name = complex.substring(idx + 1, complex.length());
         if (idx > 1) {
            String domain = complex.substring(0, idx);
            return domain + ':' + base + name;
         } else {
            return base + name;
         }
      }
   }

   public static float getOffsetFOV(PlayerEntity entity, float fov) {
      FOVUpdateEvent fovUpdateEvent = new FOVUpdateEvent(entity, fov);
      MinecraftForge.EVENT_BUS.post(fovUpdateEvent);
      return fovUpdateEvent.getNewfov();
   }

   public static double getFOVModifier(GameRenderer renderer, ActiveRenderInfo info, double renderPartialTicks, double fov) {
      EntityViewRenderEvent.FOVModifier event = new EntityViewRenderEvent.FOVModifier(renderer, info, renderPartialTicks, fov);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getFOV();
   }

   public static int getSkyBlendColour(World world, BlockPos center) {
      if (center.getX() == skyX && center.getZ() == skyZ && skyInit) {
         return skyRGBMultiplier;
      } else {
         skyInit = true;
         GameSettings settings = Minecraft.getInstance().gameSettings;
         int[] ranges = new int[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34};
         int distance = 0;
         if (settings.fancyGraphics && ranges.length > 0) {
            distance = ranges[MathHelper.clamp(settings.renderDistanceChunks, 0, ranges.length - 1)];
         }

         int r = 0;
         int g = 0;
         int b = 0;
         int divider = 0;

         int x;
         for(x = -distance; x <= distance; ++x) {
            for(int z = -distance; z <= distance; ++z) {
               BlockPos pos = center.add(x, 0, z);
               world.func_225526_b_(pos.getX(), pos.getY(), pos.getZ());
               int colour = 16777215;
               r += (colour & 16711680) >> 16;
               g += (colour & '\uff00') >> 8;
               b += colour & 255;
               ++divider;
            }
         }

         x = (r / divider & 255) << 16 | (g / divider & 255) << 8 | b / divider & 255;
         skyX = center.getX();
         skyZ = center.getZ();
         skyRGBMultiplier = x;
         return skyRGBMultiplier;
      }
   }

   public static void renderMainMenu(MainMenuScreen gui, FontRenderer font, int width, int height) {
      VersionChecker.Status status = ForgeVersion.getStatus();
      String line;
      if (status == VersionChecker.Status.BETA || status == VersionChecker.Status.BETA_OUTDATED) {
         line = I18n.format("forge.update.beta.1", TextFormatting.RED, TextFormatting.RESET);
         int var10003 = (width - font.getStringWidth(line)) / 2;
         font.getClass();
         gui.drawString(font, line, var10003, 4 + 0 * (9 + 1), -1);
         line = I18n.format("forge.update.beta.2");
         var10003 = (width - font.getStringWidth(line)) / 2;
         font.getClass();
         gui.drawString(font, line, var10003, 4 + 1 * (9 + 1), -1);
      }

      line = null;
      switch(status) {
      case OUTDATED:
      case BETA_OUTDATED:
         line = I18n.format("forge.update.newversion", ForgeVersion.getTarget());
      default:
         forgeStatusLine = line;
      }
   }

   public static ISound playSound(SoundEngine manager, ISound sound) {
      PlaySoundEvent e = new PlaySoundEvent(manager, sound);
      MinecraftForge.EVENT_BUS.post(e);
      return e.getResultSound();
   }

   public static int getWorldRenderPass() {
      return worldRenderPass;
   }

   public static void drawScreen(Screen screen, int mouseX, int mouseY, float partialTicks) {
      if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Pre(screen, mouseX, mouseY, partialTicks))) {
         screen.render(mouseX, mouseY, partialTicks);
      }

      MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Post(screen, mouseX, mouseY, partialTicks));
   }

   public static float getFogDensity(FogRenderer.FogType type, ActiveRenderInfo info, float partial, float density) {
      EntityViewRenderEvent.FogDensity event = new EntityViewRenderEvent.FogDensity(type, info, partial, density);
      return MinecraftForge.EVENT_BUS.post(event) ? event.getDensity() : -1.0F;
   }

   public static void onFogRender(FogRenderer.FogType type, ActiveRenderInfo info, float partial, float distance) {
      MinecraftForge.EVENT_BUS.post(new EntityViewRenderEvent.RenderFogEvent(type, info, partial, distance));
   }

   public static EntityViewRenderEvent.CameraSetup onCameraSetup(GameRenderer renderer, ActiveRenderInfo info, float partial) {
      EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup(renderer, info, (double)partial, info.getYaw(), info.getPitch(), 0.0F);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static void onModelBake(ModelManager modelManager, Map<ResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader) {
      ModLoader.get().postEvent(new ModelBakeEvent(modelManager, modelRegistry, modelLoader));
      modelLoader.onPostBakeEvent(modelRegistry);
   }

   public static IBakedModel handleCameraTransforms(MatrixStack matrixStack, IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType, boolean leftHandHackery) {
      MatrixStack stack = new MatrixStack();
      model = model.handlePerspective(cameraTransformType, stack);
      if (!stack.func_227867_d_()) {
         Matrix4f tMat = stack.func_227866_c_().func_227870_a_();
         Matrix3f nMat = stack.func_227866_c_().func_227872_b_();
         if (leftHandHackery) {
            tMat.multiplyBackward(flipX);
            tMat.func_226595_a_(flipX);
            nMat.multiplyBackward(flipXNormal);
            nMat.func_226118_b_(flipXNormal);
         }

         matrixStack.func_227866_c_().func_227870_a_().func_226595_a_(tMat);
         matrixStack.func_227866_c_().func_227872_b_().func_226118_b_(nMat);
      }

      return model;
   }

   public static void preDraw(VertexFormatElement.Usage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer) {
      VertexFormatElement attr = (VertexFormatElement)format.func_227894_c_().get(element);
      int count = attr.getElementCount();
      int constant = attr.getType().getGlConstant();
      buffer.position(format.getOffset(element));
      switch(attrType) {
      case POSITION:
         GL11.glVertexPointer(count, constant, stride, buffer);
         GL11.glEnableClientState(32884);
         break;
      case NORMAL:
         if (count != 3) {
            throw new IllegalArgumentException("Normal attribute should have the size 3: " + attr);
         }

         GL11.glNormalPointer(constant, stride, buffer);
         GL11.glEnableClientState(32885);
         break;
      case COLOR:
         GL11.glColorPointer(count, constant, stride, buffer);
         GL11.glEnableClientState(32886);
         break;
      case UV:
         GL13.glClientActiveTexture('蓀' + attr.getIndex());
         GL11.glTexCoordPointer(count, constant, stride, buffer);
         GL11.glEnableClientState(32888);
         GL13.glClientActiveTexture(33984);
      case PADDING:
         break;
      case GENERIC:
         GL20.glEnableVertexAttribArray(attr.getIndex());
         GL20.glVertexAttribPointer(attr.getIndex(), count, constant, false, stride, buffer);
         break;
      default:
         LOGGER.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
      }

   }

   public static void postDraw(VertexFormatElement.Usage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer) {
      VertexFormatElement attr = (VertexFormatElement)format.func_227894_c_().get(element);
      switch(attrType) {
      case POSITION:
         GL11.glDisableClientState(32884);
         break;
      case NORMAL:
         GL11.glDisableClientState(32885);
         break;
      case COLOR:
         GL11.glDisableClientState(32886);
         break;
      case UV:
         GL13.glClientActiveTexture('蓀' + attr.getIndex());
         GL11.glDisableClientState(32888);
         GL13.glClientActiveTexture(33984);
      case PADDING:
         break;
      case GENERIC:
         GL20.glDisableVertexAttribArray(attr.getIndex());
         break;
      default:
         LOGGER.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
      }

   }

   public static int getColorIndex(VertexFormat fmt) {
      ImmutableList<VertexFormatElement> elements = fmt.func_227894_c_();

      for(int i = 0; i < elements.size(); ++i) {
         if (((VertexFormatElement)elements.get(i)).getUsage() == VertexFormatElement.Usage.COLOR) {
            return i;
         }
      }

      throw new IndexOutOfBoundsException("There is no COLOR element in the provided VertexFormat.");
   }

   public static TextureAtlasSprite[] getFluidSprites(ILightReader world, BlockPos pos, IFluidState fluidStateIn) {
      ResourceLocation overlayTexture = fluidStateIn.getFluid().getAttributes().getOverlayTexture();
      return new TextureAtlasSprite[]{(TextureAtlasSprite)Minecraft.getInstance().func_228015_a_(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluidStateIn.getFluid().getAttributes().getStillTexture(world, pos)), (TextureAtlasSprite)Minecraft.getInstance().func_228015_a_(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluidStateIn.getFluid().getAttributes().getFlowingTexture(world, pos)), overlayTexture == null ? null : (TextureAtlasSprite)Minecraft.getInstance().func_228015_a_(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(overlayTexture)};
   }

   public static void gatherFluidTextures(Set<Material> textures) {
      ForgeRegistries.FLUIDS.getValues().stream().flatMap(ForgeHooksClient::getFluidMaterials).forEach(textures::add);
   }

   public static Stream<Material> getFluidMaterials(Fluid fluid) {
      return fluid.getAttributes().getTextures().filter(Objects::nonNull).map(ForgeHooksClient::getBlockMaterial);
   }

   public static Material getBlockMaterial(ResourceLocation loc) {
      return new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, loc);
   }

   public static void fillNormal(int[] faceData, Direction facing) {
      Vector3f v1 = getVertexPos(faceData, 3);
      Vector3f t1 = getVertexPos(faceData, 1);
      Vector3f v2 = getVertexPos(faceData, 2);
      Vector3f t2 = getVertexPos(faceData, 0);
      v1.sub(t1);
      v2.sub(t2);
      v2.cross(v1);
      v2.func_229194_d_();
      int x = (byte)Math.round(v2.getX() * 127.0F) & 255;
      int y = (byte)Math.round(v2.getY() * 127.0F) & 255;
      int z = (byte)Math.round(v2.getZ() * 127.0F) & 255;
      int normal = x | y << 8 | z << 16;

      for(int i = 0; i < 4; ++i) {
         faceData[i * 8 + 7] = normal;
      }

   }

   private static Vector3f getVertexPos(int[] data, int vertex) {
      int idx = vertex * 8;
      float x = Float.intBitsToFloat(data[idx]);
      float y = Float.intBitsToFloat(data[idx + 1]);
      float z = Float.intBitsToFloat(data[idx + 2]);
      return new Vector3f(x, y, z);
   }

   public static void loadEntityShader(Entity entity, GameRenderer entityRenderer) {
      if (entity != null) {
         ResourceLocation shader = ClientRegistry.getEntityShader(entity.getClass());
         if (shader != null) {
            entityRenderer.loadShader(shader);
         }
      }

   }

   public static boolean shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot) {
      boolean fromInvalid = from.isEmpty();
      boolean toInvalid = to.isEmpty();
      if (fromInvalid && toInvalid) {
         return false;
      } else if (!fromInvalid && !toInvalid) {
         boolean changed = false;
         if (slot != -1) {
            changed = slot != slotMainHand;
            slotMainHand = slot;
         }

         return from.getItem().shouldCauseReequipAnimation(from, to, changed);
      } else {
         return true;
      }
   }

   public static RenderGameOverlayEvent.BossInfo bossBarRenderPre(MainWindow res, ClientBossInfo bossInfo, int x, int y, int increment) {
      RenderGameOverlayEvent.BossInfo evt = new RenderGameOverlayEvent.BossInfo(new RenderGameOverlayEvent(Animation.getPartialTickTime(), res), RenderGameOverlayEvent.ElementType.BOSSINFO, bossInfo, x, y, increment);
      MinecraftForge.EVENT_BUS.post(evt);
      return evt;
   }

   public static void bossBarRenderPost(MainWindow res) {
      MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(new RenderGameOverlayEvent(Animation.getPartialTickTime(), res), RenderGameOverlayEvent.ElementType.BOSSINFO));
   }

   public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile) {
      ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static IBakedModel handlePerspective(IBakedModel model, ItemCameraTransforms.TransformType type, MatrixStack stack) {
      TransformationMatrix tr = TransformationHelper.toTransformation(model.getItemCameraTransforms().getTransform(type));
      if (!tr.isIdentity()) {
         tr.push(stack);
      }

      return model;
   }

   public static void onInputUpdate(PlayerEntity player, MovementInput movementInput) {
      MinecraftForge.EVENT_BUS.post(new InputUpdateEvent(player, movementInput));
   }

   public static void refreshResources(Minecraft mc, VanillaResourceType... types) {
      SelectiveReloadStateHandler.INSTANCE.beginReload(ReloadRequirements.include(types));
      mc.reloadResources();
      SelectiveReloadStateHandler.INSTANCE.endReload();
   }

   public static boolean onGuiMouseClickedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
      Event event = new GuiScreenEvent.MouseClickedEvent.Pre(guiScreen, mouseX, mouseY, button);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiMouseClickedPost(Screen guiScreen, double mouseX, double mouseY, int button) {
      Event event = new GuiScreenEvent.MouseClickedEvent.Post(guiScreen, mouseX, mouseY, button);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiMouseReleasedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
      Event event = new GuiScreenEvent.MouseReleasedEvent.Pre(guiScreen, mouseX, mouseY, button);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiMouseReleasedPost(Screen guiScreen, double mouseX, double mouseY, int button) {
      Event event = new GuiScreenEvent.MouseReleasedEvent.Post(guiScreen, mouseX, mouseY, button);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
      Event event = new GuiScreenEvent.MouseDragEvent.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
      Event event = new GuiScreenEvent.MouseDragEvent.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiMouseScrollPre(MouseHelper mouseHelper, Screen guiScreen, double scrollDelta) {
      MainWindow mainWindow = guiScreen.getMinecraft().func_228018_at_();
      double mouseX = mouseHelper.getMouseX() * (double)mainWindow.getScaledWidth() / (double)mainWindow.getWidth();
      double mouseY = mouseHelper.getMouseY() * (double)mainWindow.getScaledHeight() / (double)mainWindow.getHeight();
      Event event = new GuiScreenEvent.MouseScrollEvent.Pre(guiScreen, mouseX, mouseY, scrollDelta);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiMouseScrollPost(MouseHelper mouseHelper, Screen guiScreen, double scrollDelta) {
      MainWindow mainWindow = guiScreen.getMinecraft().func_228018_at_();
      double mouseX = mouseHelper.getMouseX() * (double)mainWindow.getScaledWidth() / (double)mainWindow.getWidth();
      double mouseY = mouseHelper.getMouseY() * (double)mainWindow.getScaledHeight() / (double)mainWindow.getHeight();
      Event event = new GuiScreenEvent.MouseScrollEvent.Post(guiScreen, mouseX, mouseY, scrollDelta);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiKeyPressedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
      Event event = new GuiScreenEvent.KeyboardKeyPressedEvent.Pre(guiScreen, keyCode, scanCode, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiKeyPressedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
      Event event = new GuiScreenEvent.KeyboardKeyPressedEvent.Post(guiScreen, keyCode, scanCode, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiKeyReleasedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
      Event event = new GuiScreenEvent.KeyboardKeyReleasedEvent.Pre(guiScreen, keyCode, scanCode, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiKeyReleasedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
      Event event = new GuiScreenEvent.KeyboardKeyReleasedEvent.Post(guiScreen, keyCode, scanCode, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiCharTypedPre(Screen guiScreen, char codePoint, int modifiers) {
      Event event = new GuiScreenEvent.KeyboardCharTypedEvent.Pre(guiScreen, codePoint, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onGuiCharTypedPost(Screen guiScreen, char codePoint, int modifiers) {
      Event event = new GuiScreenEvent.KeyboardCharTypedEvent.Post(guiScreen, codePoint, modifiers);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static void onRecipesUpdated(RecipeManager mgr) {
      Event event = new RecipesUpdatedEvent(mgr);
      MinecraftForge.EVENT_BUS.post(event);
   }

   public static void invalidateLog4jThreadCache() {
      if (System.getProperty("java.version").compareTo("1.8.0_102") < 0) {
         try {
            Field nameField = ThreadNameCachingStrategy.class.getDeclaredField("THREADLOCAL_NAME");
            Field logEventField = ReusableLogEventFactory.class.getDeclaredField("mutableLogEventThreadLocal");
            nameField.setAccessible(true);
            logEventField.setAccessible(true);
            ((ThreadLocal)nameField.get((Object)null)).set((Object)null);
            ((ThreadLocal)logEventField.get((Object)null)).set((Object)null);
         } catch (NoClassDefFoundError | ReflectiveOperationException var2) {
            LOGGER.error("Unable to invalidate log4j thread cache, thread fields in logs may be inaccurate", var2);
         }

      }
   }

   public static void fireMouseInput(int button, int action, int mods) {
      MinecraftForge.EVENT_BUS.post(new InputEvent.MouseInputEvent(button, action, mods));
   }

   public static void fireKeyInput(int key, int scanCode, int action, int modifiers) {
      MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(key, scanCode, action, modifiers));
   }

   public static boolean onMouseScroll(MouseHelper mouseHelper, double scrollDelta) {
      Event event = new InputEvent.MouseScrollEvent(scrollDelta, mouseHelper.isLeftDown(), mouseHelper.isMiddleDown(), mouseHelper.isRightDown(), mouseHelper.getMouseX(), mouseHelper.getMouseY());
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onRawMouseClicked(int button, int action, int mods) {
      return MinecraftForge.EVENT_BUS.post(new InputEvent.RawMouseEvent(button, action, mods));
   }

   public static InputEvent.ClickInputEvent onClickInput(int button, KeyBinding keyBinding, Hand hand) {
      InputEvent.ClickInputEvent event = new InputEvent.ClickInputEvent(button, keyBinding, hand);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   static {
      flipXNormal = new Matrix3f(flipX);
      slotMainHand = 0;
   }
}
