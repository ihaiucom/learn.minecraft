package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class Screen extends FocusableGui implements IRenderable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
   protected final ITextComponent title;
   protected final List<IGuiEventListener> children = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   protected ItemRenderer itemRenderer;
   public int width;
   public int height;
   protected final List<Widget> buttons = Lists.newArrayList();
   public boolean passEvents;
   protected FontRenderer font;
   private URI clickedLink;

   protected Screen(ITextComponent p_i51108_1_) {
      this.title = p_i51108_1_;
   }

   public ITextComponent getTitle() {
      return this.title;
   }

   public String getNarrationMessage() {
      return this.getTitle().getString();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      for(int i = 0; i < this.buttons.size(); ++i) {
         ((Widget)this.buttons.get(i)).render(p_render_1_, p_render_2_, p_render_3_);
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (p_keyPressed_1_ == 258) {
         boolean flag = !hasShiftDown();
         if (!this.changeFocus(flag)) {
            this.changeFocus(flag);
         }

         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.displayGuiScreen((Screen)null);
   }

   protected <T extends Widget> T addButton(T p_addButton_1_) {
      this.buttons.add(p_addButton_1_);
      this.children.add(p_addButton_1_);
      return p_addButton_1_;
   }

   protected void renderTooltip(ItemStack p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      FontRenderer font = p_renderTooltip_1_.getItem().getFontRenderer(p_renderTooltip_1_);
      GuiUtils.preItemToolTip(p_renderTooltip_1_);
      this.renderTooltip(this.getTooltipFromItem(p_renderTooltip_1_), p_renderTooltip_2_, p_renderTooltip_3_, font == null ? this.font : font);
      GuiUtils.postItemToolTip();
   }

   public List<String> getTooltipFromItem(ItemStack p_getTooltipFromItem_1_) {
      List<ITextComponent> list = p_getTooltipFromItem_1_.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
      List<String> list1 = Lists.newArrayList();
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         ITextComponent itextcomponent = (ITextComponent)var4.next();
         list1.add(itextcomponent.getFormattedText());
      }

      return list1;
   }

   public void renderTooltip(String p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      this.renderTooltip(Arrays.asList(p_renderTooltip_1_), p_renderTooltip_2_, p_renderTooltip_3_);
   }

   public void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
      this.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_, this.font);
   }

   public void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_, FontRenderer p_renderTooltip_4_) {
      GuiUtils.drawHoveringText(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_, this.width, this.height, -1, p_renderTooltip_4_);
   }

   protected void renderComponentHoverEffect(ITextComponent p_renderComponentHoverEffect_1_, int p_renderComponentHoverEffect_2_, int p_renderComponentHoverEffect_3_) {
      if (p_renderComponentHoverEffect_1_ != null && p_renderComponentHoverEffect_1_.getStyle().getHoverEvent() != null) {
         HoverEvent hoverevent = p_renderComponentHoverEffect_1_.getStyle().getHoverEvent();
         if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemstack = ItemStack.EMPTY;

            try {
               INBT inbt = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
               if (inbt instanceof CompoundNBT) {
                  itemstack = ItemStack.read((CompoundNBT)inbt);
               }
            } catch (CommandSyntaxException var10) {
            }

            if (itemstack.isEmpty()) {
               this.renderTooltip(TextFormatting.RED + "Invalid Item!", p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
            } else {
               this.renderTooltip(itemstack, p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
            }
         } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.minecraft.gameSettings.advancedItemTooltips) {
               try {
                  CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(hoverevent.getValue().getString());
                  List<String> list = Lists.newArrayList();
                  ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("name"));
                  if (itextcomponent != null) {
                     list.add(itextcomponent.getFormattedText());
                  }

                  if (compoundnbt.contains("type", 8)) {
                     String s = compoundnbt.getString("type");
                     list.add("Type: " + s);
                  }

                  list.add(compoundnbt.getString("id"));
                  this.renderTooltip((List)list, p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
               } catch (JsonSyntaxException | CommandSyntaxException var9) {
                  this.renderTooltip(TextFormatting.RED + "Invalid Entity!", p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
               }
            }
         } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(this.minecraft.fontRenderer.listFormattedStringToWidth(hoverevent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), p_renderComponentHoverEffect_2_, p_renderComponentHoverEffect_3_);
         }
      }

   }

   protected void insertText(String p_insertText_1_, boolean p_insertText_2_) {
   }

   public boolean handleComponentClicked(ITextComponent p_handleComponentClicked_1_) {
      if (p_handleComponentClicked_1_ == null) {
         return false;
      } else {
         ClickEvent clickevent = p_handleComponentClicked_1_.getStyle().getClickEvent();
         if (hasShiftDown()) {
            if (p_handleComponentClicked_1_.getStyle().getInsertion() != null) {
               this.insertText(p_handleComponentClicked_1_.getStyle().getInsertion(), false);
            }
         } else if (clickevent != null) {
            URI uri;
            if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.minecraft.gameSettings.chatLinks) {
                  return false;
               }

               try {
                  uri = new URI(clickevent.getValue());
                  String s = uri.getScheme();
                  if (s == null) {
                     throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                  }

                  if (!ALLOWED_PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                  }

                  if (this.minecraft.gameSettings.chatLinksPrompt) {
                     this.clickedLink = uri;
                     this.minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::confirmLink, clickevent.getValue(), false));
                  } else {
                     this.openLink(uri);
                  }
               } catch (URISyntaxException var5) {
                  LOGGER.error("Can't open url for {}", clickevent, var5);
               }
            } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
               uri = (new File(clickevent.getValue())).toURI();
               this.openLink(uri);
            } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(clickevent.getValue(), true);
            } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.sendMessage(clickevent.getValue(), false);
            } else if (clickevent.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
               this.minecraft.keyboardListener.setClipboardString(clickevent.getValue());
            } else {
               LOGGER.error("Don't know how to handle {}", clickevent);
            }

            return true;
         }

         return false;
      }
   }

   public void sendMessage(String p_sendMessage_1_) {
      this.sendMessage(p_sendMessage_1_, true);
   }

   public void sendMessage(String p_sendMessage_1_, boolean p_sendMessage_2_) {
      p_sendMessage_1_ = ForgeEventFactory.onClientSendMessage(p_sendMessage_1_);
      if (!p_sendMessage_1_.isEmpty()) {
         if (p_sendMessage_2_) {
            this.minecraft.ingameGUI.getChatGUI().addToSentMessages(p_sendMessage_1_);
         }

         this.minecraft.player.sendChatMessage(p_sendMessage_1_);
      }
   }

   public void init(Minecraft p_init_1_, int p_init_2_, int p_init_3_) {
      this.minecraft = p_init_1_;
      this.itemRenderer = p_init_1_.getItemRenderer();
      this.font = p_init_1_.fontRenderer;
      this.width = p_init_2_;
      this.height = p_init_3_;
      Consumer<Widget> remove = (p_lambda$init$0_1_) -> {
         this.buttons.remove(p_lambda$init$0_1_);
         this.children.remove(p_lambda$init$0_1_);
      };
      if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Pre(this, this.buttons, this::addButton, remove))) {
         this.buttons.clear();
         this.children.clear();
         this.setFocused((IGuiEventListener)null);
         this.init();
      }

      MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Post(this, this.buttons, this::addButton, remove));
   }

   public void setSize(int p_setSize_1_, int p_setSize_2_) {
      this.width = p_setSize_1_;
      this.height = p_setSize_2_;
   }

   public List<? extends IGuiEventListener> children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void renderBackground() {
      this.renderBackground(0);
   }

   public void renderBackground(int p_renderBackground_1_) {
      if (this.minecraft.world != null) {
         this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
         MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this));
      } else {
         this.renderDirtBackground(p_renderBackground_1_);
      }

   }

   public void renderDirtBackground(int p_renderDirtBackground_1_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      bufferbuilder.func_225582_a_(0.0D, (double)this.height, 0.0D).func_225583_a_(0.0F, (float)this.height / 32.0F + (float)p_renderDirtBackground_1_).func_225586_a_(64, 64, 64, 255).endVertex();
      bufferbuilder.func_225582_a_((double)this.width, (double)this.height, 0.0D).func_225583_a_((float)this.width / 32.0F, (float)this.height / 32.0F + (float)p_renderDirtBackground_1_).func_225586_a_(64, 64, 64, 255).endVertex();
      bufferbuilder.func_225582_a_((double)this.width, 0.0D, 0.0D).func_225583_a_((float)this.width / 32.0F, (float)p_renderDirtBackground_1_).func_225586_a_(64, 64, 64, 255).endVertex();
      bufferbuilder.func_225582_a_(0.0D, 0.0D, 0.0D).func_225583_a_(0.0F, (float)p_renderDirtBackground_1_).func_225586_a_(64, 64, 64, 255).endVertex();
      tessellator.draw();
      MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this));
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void confirmLink(boolean p_confirmLink_1_) {
      if (p_confirmLink_1_) {
         this.openLink(this.clickedLink);
      }

      this.clickedLink = null;
      this.minecraft.displayGuiScreen(this);
   }

   private void openLink(URI p_openLink_1_) {
      Util.getOSType().openURI(p_openLink_1_);
   }

   public static boolean hasControlDown() {
      if (Minecraft.IS_RUNNING_ON_MAC) {
         return InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 343) || InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 347);
      } else {
         return InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 341) || InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 344);
   }

   public static boolean hasAltDown() {
      return InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 342) || InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 346);
   }

   public static boolean isCut(int p_isCut_0_) {
      return p_isCut_0_ == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int p_isPaste_0_) {
      return p_isPaste_0_ == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int p_isCopy_0_) {
      return p_isCopy_0_ == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int p_isSelectAll_0_) {
      return p_isSelectAll_0_ == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
   }

   public static void wrapScreenError(Runnable p_wrapScreenError_0_, String p_wrapScreenError_1_, String p_wrapScreenError_2_) {
      try {
         p_wrapScreenError_0_.run();
      } catch (Throwable var6) {
         CrashReport crashreport = CrashReport.makeCrashReport(var6, p_wrapScreenError_1_);
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
         crashreportcategory.addDetail("Screen name", () -> {
            return p_wrapScreenError_2_;
         });
         throw new ReportedException(crashreport);
      }
   }

   protected boolean isValidCharacterForName(String p_isValidCharacterForName_1_, char p_isValidCharacterForName_2_, int p_isValidCharacterForName_3_) {
      int i = p_isValidCharacterForName_1_.indexOf(58);
      int j = p_isValidCharacterForName_1_.indexOf(47);
      if (p_isValidCharacterForName_2_ == ':') {
         return (j == -1 || p_isValidCharacterForName_3_ <= j) && i == -1;
      } else if (p_isValidCharacterForName_2_ == '/') {
         return p_isValidCharacterForName_3_ > i;
      } else {
         return p_isValidCharacterForName_2_ == '_' || p_isValidCharacterForName_2_ == '-' || p_isValidCharacterForName_2_ >= 'a' && p_isValidCharacterForName_2_ <= 'z' || p_isValidCharacterForName_2_ >= '0' && p_isValidCharacterForName_2_ <= '9' || p_isValidCharacterForName_2_ == '.';
      }
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return true;
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }
}
