package net.minecraft.client;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.ChatOptionsScreen;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class KeyboardListener {
   private final Minecraft mc;
   private boolean repeatEventsEnabled;
   private final ClipboardHelper field_216821_c = new ClipboardHelper();
   private long debugCrashKeyPressTime = -1L;
   private long lastDebugCrashWarning = -1L;
   private long debugCrashWarningsSent = -1L;
   private boolean actionKeyF3;

   public KeyboardListener(Minecraft p_i47674_1_) {
      this.mc = p_i47674_1_;
   }

   private void printDebugMessage(String p_197964_1_, Object... p_197964_2_) {
      this.mc.ingameGUI.getChatGUI().printChatMessage((new StringTextComponent("")).appendSibling((new TranslationTextComponent("debug.prefix", new Object[0])).applyTextStyles(new TextFormatting[]{TextFormatting.YELLOW, TextFormatting.BOLD})).appendText(" ").appendSibling(new TranslationTextComponent(p_197964_1_, p_197964_2_)));
   }

   private void printDebugWarning(String p_204869_1_, Object... p_204869_2_) {
      this.mc.ingameGUI.getChatGUI().printChatMessage((new StringTextComponent("")).appendSibling((new TranslationTextComponent("debug.prefix", new Object[0])).applyTextStyles(new TextFormatting[]{TextFormatting.RED, TextFormatting.BOLD})).appendText(" ").appendSibling(new TranslationTextComponent(p_204869_1_, p_204869_2_)));
   }

   private boolean processKeyF3(int p_197962_1_) {
      if (this.debugCrashKeyPressTime > 0L && this.debugCrashKeyPressTime < Util.milliTime() - 100L) {
         return true;
      } else {
         switch(p_197962_1_) {
         case 65:
            this.mc.worldRenderer.loadRenderers();
            this.printDebugMessage("debug.reload_chunks.message");
            return true;
         case 66:
            boolean flag = !this.mc.getRenderManager().isDebugBoundingBox();
            this.mc.getRenderManager().setDebugBoundingBox(flag);
            this.printDebugMessage(flag ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
            return true;
         case 67:
            if (this.mc.player.hasReducedDebug()) {
               return false;
            }

            this.printDebugMessage("debug.copy_location.message");
            this.setClipboardString(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.getKey(this.mc.player.world.dimension.getType()), this.mc.player.func_226277_ct_(), this.mc.player.func_226278_cu_(), this.mc.player.func_226281_cx_(), this.mc.player.rotationYaw, this.mc.player.rotationPitch));
            return true;
         case 68:
            if (this.mc.ingameGUI != null) {
               this.mc.ingameGUI.getChatGUI().clearChatMessages(false);
            }

            return true;
         case 69:
         case 74:
         case 75:
         case 76:
         case 77:
         case 79:
         case 82:
         case 83:
         default:
            return false;
         case 70:
            AbstractOption.RENDER_DISTANCE.set(this.mc.gameSettings, MathHelper.clamp((double)(this.mc.gameSettings.renderDistanceChunks + (Screen.hasShiftDown() ? -1 : 1)), AbstractOption.RENDER_DISTANCE.getMinValue(), AbstractOption.RENDER_DISTANCE.getMaxValue()));
            this.printDebugMessage("debug.cycle_renderdistance.message", this.mc.gameSettings.renderDistanceChunks);
            return true;
         case 71:
            boolean flag1 = this.mc.debugRenderer.toggleChunkBorders();
            this.printDebugMessage(flag1 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            return true;
         case 72:
            this.mc.gameSettings.advancedItemTooltips = !this.mc.gameSettings.advancedItemTooltips;
            this.printDebugMessage(this.mc.gameSettings.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
            this.mc.gameSettings.saveOptions();
            return true;
         case 73:
            if (!this.mc.player.hasReducedDebug()) {
               this.copyHoveredObject(this.mc.player.hasPermissionLevel(2), !Screen.hasShiftDown());
            }

            return true;
         case 78:
            if (!this.mc.player.hasPermissionLevel(2)) {
               this.printDebugMessage("debug.creative_spectator.error");
            } else if (this.mc.player.isCreative()) {
               this.mc.player.sendChatMessage("/gamemode spectator");
            } else {
               this.mc.player.sendChatMessage("/gamemode creative");
            }

            return true;
         case 80:
            this.mc.gameSettings.pauseOnLostFocus = !this.mc.gameSettings.pauseOnLostFocus;
            this.mc.gameSettings.saveOptions();
            this.printDebugMessage(this.mc.gameSettings.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
            return true;
         case 81:
            this.printDebugMessage("debug.help.message");
            NewChatGui newchatgui = this.mc.ingameGUI.getChatGUI();
            newchatgui.printChatMessage(new TranslationTextComponent("debug.reload_chunks.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.show_hitboxes.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.copy_location.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.clear_chat.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.cycle_renderdistance.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.chunk_boundaries.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.advanced_tooltips.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.inspect.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.creative_spectator.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.pause_focus.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.help.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.reload_resourcepacks.help", new Object[0]));
            newchatgui.printChatMessage(new TranslationTextComponent("debug.pause.help", new Object[0]));
            return true;
         case 84:
            this.printDebugMessage("debug.reload_resourcepacks.message");
            this.mc.reloadResources();
            return true;
         }
      }
   }

   private void copyHoveredObject(boolean p_211556_1_, boolean p_211556_2_) {
      RayTraceResult raytraceresult = this.mc.objectMouseOver;
      if (raytraceresult != null) {
         switch(raytraceresult.getType()) {
         case BLOCK:
            BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
            BlockState blockstate = this.mc.player.world.getBlockState(blockpos);
            if (p_211556_1_) {
               if (p_211556_2_) {
                  this.mc.player.connection.getNBTQueryManager().queryTileEntity(blockpos, (p_lambda$copyHoveredObject$0_3_) -> {
                     this.setBlockClipboardString(blockstate, blockpos, p_lambda$copyHoveredObject$0_3_);
                     this.printDebugMessage("debug.inspect.server.block");
                  });
               } else {
                  TileEntity tileentity = this.mc.player.world.getTileEntity(blockpos);
                  CompoundNBT compoundnbt1 = tileentity != null ? tileentity.write(new CompoundNBT()) : null;
                  this.setBlockClipboardString(blockstate, blockpos, compoundnbt1);
                  this.printDebugMessage("debug.inspect.client.block");
               }
            } else {
               this.setBlockClipboardString(blockstate, blockpos, (CompoundNBT)null);
               this.printDebugMessage("debug.inspect.client.block");
            }
            break;
         case ENTITY:
            Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
            ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(entity.getType());
            if (p_211556_1_) {
               if (p_211556_2_) {
                  this.mc.player.connection.getNBTQueryManager().queryEntity(entity.getEntityId(), (p_lambda$copyHoveredObject$1_3_) -> {
                     this.setEntityClipboardString(resourcelocation, entity.getPositionVec(), p_lambda$copyHoveredObject$1_3_);
                     this.printDebugMessage("debug.inspect.server.entity");
                  });
               } else {
                  CompoundNBT compoundnbt = entity.writeWithoutTypeId(new CompoundNBT());
                  this.setEntityClipboardString(resourcelocation, entity.getPositionVec(), compoundnbt);
                  this.printDebugMessage("debug.inspect.client.entity");
               }
            } else {
               this.setEntityClipboardString(resourcelocation, entity.getPositionVec(), (CompoundNBT)null);
               this.printDebugMessage("debug.inspect.client.entity");
            }
         }
      }

   }

   private void setBlockClipboardString(BlockState p_211558_1_, BlockPos p_211558_2_, @Nullable CompoundNBT p_211558_3_) {
      if (p_211558_3_ != null) {
         p_211558_3_.remove("x");
         p_211558_3_.remove("y");
         p_211558_3_.remove("z");
         p_211558_3_.remove("id");
      }

      StringBuilder stringbuilder = new StringBuilder(BlockStateParser.toString(p_211558_1_));
      if (p_211558_3_ != null) {
         stringbuilder.append(p_211558_3_);
      }

      String s = String.format(Locale.ROOT, "/setblock %d %d %d %s", p_211558_2_.getX(), p_211558_2_.getY(), p_211558_2_.getZ(), stringbuilder);
      this.setClipboardString(s);
   }

   private void setEntityClipboardString(ResourceLocation p_211557_1_, Vec3d p_211557_2_, @Nullable CompoundNBT p_211557_3_) {
      String s;
      if (p_211557_3_ != null) {
         p_211557_3_.remove("UUIDMost");
         p_211557_3_.remove("UUIDLeast");
         p_211557_3_.remove("Pos");
         p_211557_3_.remove("Dimension");
         String s1 = p_211557_3_.toFormattedComponent().getString();
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", p_211557_1_.toString(), p_211557_2_.x, p_211557_2_.y, p_211557_2_.z, s1);
      } else {
         s = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", p_211557_1_.toString(), p_211557_2_.x, p_211557_2_.y, p_211557_2_.z);
      }

      this.setClipboardString(s);
   }

   public void onKeyEvent(long p_197961_1_, int p_197961_3_, int p_197961_4_, int p_197961_5_, int p_197961_6_) {
      if (p_197961_1_ == this.mc.func_228018_at_().getHandle()) {
         if (this.debugCrashKeyPressTime > 0L) {
            if (!InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 67) || !InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 292)) {
               this.debugCrashKeyPressTime = -1L;
            }
         } else if (InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 67) && InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 292)) {
            this.actionKeyF3 = true;
            this.debugCrashKeyPressTime = Util.milliTime();
            this.lastDebugCrashWarning = Util.milliTime();
            this.debugCrashWarningsSent = 0L;
         }

         INestedGuiEventHandler inestedguieventhandler = this.mc.currentScreen;
         if (!(this.mc.currentScreen instanceof ControlsScreen) || ((ControlsScreen)inestedguieventhandler).time <= Util.milliTime() - 20L) {
            if (p_197961_5_ == 1) {
               if (this.mc.gameSettings.keyBindFullscreen.matchesKey(p_197961_3_, p_197961_4_)) {
                  this.mc.func_228018_at_().toggleFullscreen();
                  this.mc.gameSettings.fullscreen = this.mc.func_228018_at_().isFullscreen();
                  return;
               }

               if (this.mc.gameSettings.keyBindScreenshot.matchesKey(p_197961_3_, p_197961_4_)) {
                  if (Screen.hasControlDown()) {
                  }

                  ScreenShotHelper.saveScreenshot(this.mc.gameDir, this.mc.func_228018_at_().getFramebufferWidth(), this.mc.func_228018_at_().getFramebufferHeight(), this.mc.getFramebuffer(), (p_lambda$onKeyEvent$3_1_) -> {
                     this.mc.execute(() -> {
                        this.mc.ingameGUI.getChatGUI().printChatMessage(p_lambda$onKeyEvent$3_1_);
                     });
                  });
                  return;
               }
            } else if (p_197961_5_ == 0 && this.mc.currentScreen instanceof ControlsScreen) {
               ((ControlsScreen)this.mc.currentScreen).buttonId = null;
            }
         }

         boolean flag = inestedguieventhandler == null || !(inestedguieventhandler.getFocused() instanceof TextFieldWidget) || !((TextFieldWidget)inestedguieventhandler.getFocused()).func_212955_f();
         if (p_197961_5_ != 0 && p_197961_3_ == 66 && Screen.hasControlDown() && flag) {
            AbstractOption.NARRATOR.func_216722_a(this.mc.gameSettings, 1);
            if (inestedguieventhandler instanceof ChatOptionsScreen) {
               ((ChatOptionsScreen)inestedguieventhandler).updateNarratorButton();
            }

            if (inestedguieventhandler instanceof AccessibilityScreen) {
               ((AccessibilityScreen)inestedguieventhandler).func_212985_a();
            }
         }

         if (inestedguieventhandler != null) {
            boolean[] aboolean = new boolean[]{false};
            Screen.wrapScreenError(() -> {
               if (p_197961_5_ != 1 && (p_197961_5_ != 2 || !this.repeatEventsEnabled)) {
                  if (p_197961_5_ == 0) {
                     aboolean[0] = ForgeHooksClient.onGuiKeyReleasedPre(this.mc.currentScreen, p_197961_3_, p_197961_4_, p_197961_6_);
                     if (!aboolean[0]) {
                        aboolean[0] = inestedguieventhandler.keyReleased(p_197961_3_, p_197961_4_, p_197961_6_);
                     }

                     if (!aboolean[0]) {
                        aboolean[0] = ForgeHooksClient.onGuiKeyReleasedPost(this.mc.currentScreen, p_197961_3_, p_197961_4_, p_197961_6_);
                     }
                  }
               } else {
                  aboolean[0] = ForgeHooksClient.onGuiKeyPressedPre(this.mc.currentScreen, p_197961_3_, p_197961_4_, p_197961_6_);
                  if (!aboolean[0]) {
                     aboolean[0] = inestedguieventhandler.keyPressed(p_197961_3_, p_197961_4_, p_197961_6_);
                  }

                  if (!aboolean[0]) {
                     aboolean[0] = ForgeHooksClient.onGuiKeyPressedPost(this.mc.currentScreen, p_197961_3_, p_197961_4_, p_197961_6_);
                  }
               }

            }, "keyPressed event handler", inestedguieventhandler.getClass().getCanonicalName());
            if (aboolean[0]) {
               return;
            }
         }

         if (this.mc.currentScreen == null || this.mc.currentScreen.passEvents) {
            InputMappings.Input inputmappings$input = InputMappings.getInputByCode(p_197961_3_, p_197961_4_);
            if (p_197961_5_ == 0) {
               KeyBinding.setKeyBindState(inputmappings$input, false);
               if (p_197961_3_ == 292) {
                  if (this.actionKeyF3) {
                     this.actionKeyF3 = false;
                  } else {
                     this.mc.gameSettings.showDebugInfo = !this.mc.gameSettings.showDebugInfo;
                     this.mc.gameSettings.showDebugProfilerChart = this.mc.gameSettings.showDebugInfo && Screen.hasShiftDown();
                     this.mc.gameSettings.showLagometer = this.mc.gameSettings.showDebugInfo && Screen.hasAltDown();
                  }
               }
            } else {
               if (p_197961_3_ == 293 && this.mc.gameRenderer != null) {
                  this.mc.gameRenderer.switchUseShader();
               }

               boolean flag1 = false;
               if (this.mc.currentScreen == null) {
                  if (p_197961_3_ == 256) {
                     boolean flag2 = InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 292);
                     this.mc.displayInGameMenu(flag2);
                  }

                  flag1 = InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), 292) && this.processKeyF3(p_197961_3_);
                  this.actionKeyF3 |= flag1;
                  if (p_197961_3_ == 290) {
                     this.mc.gameSettings.hideGUI = !this.mc.gameSettings.hideGUI;
                  }
               }

               if (flag1) {
                  KeyBinding.setKeyBindState(inputmappings$input, false);
               } else {
                  KeyBinding.setKeyBindState(inputmappings$input, true);
                  KeyBinding.onTick(inputmappings$input);
               }

               if (this.mc.gameSettings.showDebugProfilerChart) {
                  if (p_197961_3_ == 48) {
                     this.mc.updateDebugProfilerName(0);
                  }

                  for(int i = 0; i < 9; ++i) {
                     if (p_197961_3_ == 49 + i) {
                        this.mc.updateDebugProfilerName(i + 1);
                     }
                  }
               }
            }
         }

         ForgeHooksClient.fireKeyInput(p_197961_3_, p_197961_4_, p_197961_5_, p_197961_6_);
      }

   }

   private void onCharEvent(long p_197963_1_, int p_197963_3_, int p_197963_4_) {
      if (p_197963_1_ == this.mc.func_228018_at_().getHandle()) {
         IGuiEventListener iguieventlistener = this.mc.currentScreen;
         if (iguieventlistener != null && this.mc.getLoadingGui() == null) {
            if (Character.charCount(p_197963_3_) == 1) {
               Screen.wrapScreenError(() -> {
                  if (!ForgeHooksClient.onGuiCharTypedPre(this.mc.currentScreen, (char)p_197963_3_, p_197963_4_)) {
                     if (!iguieventlistener.charTyped((char)p_197963_3_, p_197963_4_)) {
                        ForgeHooksClient.onGuiCharTypedPost(this.mc.currentScreen, (char)p_197963_3_, p_197963_4_);
                     }
                  }
               }, "charTyped event handler", iguieventlistener.getClass().getCanonicalName());
            } else {
               char[] var6 = Character.toChars(p_197963_3_);
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  char c0 = var6[var8];
                  Screen.wrapScreenError(() -> {
                     if (!ForgeHooksClient.onGuiCharTypedPre(this.mc.currentScreen, c0, p_197963_4_)) {
                        if (!iguieventlistener.charTyped(c0, p_197963_4_)) {
                           ForgeHooksClient.onGuiCharTypedPost(this.mc.currentScreen, c0, p_197963_4_);
                        }
                     }
                  }, "charTyped event handler", iguieventlistener.getClass().getCanonicalName());
               }
            }
         }
      }

   }

   public void enableRepeatEvents(boolean p_197967_1_) {
      this.repeatEventsEnabled = p_197967_1_;
   }

   public void setupCallbacks(long p_197968_1_) {
      InputMappings.func_216505_a(p_197968_1_, (p_lambda$setupCallbacks$8_1_, p_lambda$setupCallbacks$8_3_, p_lambda$setupCallbacks$8_4_, p_lambda$setupCallbacks$8_5_, p_lambda$setupCallbacks$8_6_) -> {
         this.mc.execute(() -> {
            this.onKeyEvent(p_lambda$setupCallbacks$8_1_, p_lambda$setupCallbacks$8_3_, p_lambda$setupCallbacks$8_4_, p_lambda$setupCallbacks$8_5_, p_lambda$setupCallbacks$8_6_);
         });
      }, (p_lambda$setupCallbacks$10_1_, p_lambda$setupCallbacks$10_3_, p_lambda$setupCallbacks$10_4_) -> {
         this.mc.execute(() -> {
            this.onCharEvent(p_lambda$setupCallbacks$10_1_, p_lambda$setupCallbacks$10_3_, p_lambda$setupCallbacks$10_4_);
         });
      });
   }

   public String getClipboardString() {
      return this.field_216821_c.func_216487_a(this.mc.func_228018_at_().getHandle(), (p_lambda$getClipboardString$11_1_, p_lambda$getClipboardString$11_2_) -> {
         if (p_lambda$getClipboardString$11_1_ != 65545) {
            this.mc.func_228018_at_().logGlError(p_lambda$getClipboardString$11_1_, p_lambda$getClipboardString$11_2_);
         }

      });
   }

   public void setClipboardString(String p_197960_1_) {
      this.field_216821_c.func_216489_a(this.mc.func_228018_at_().getHandle(), p_197960_1_);
   }

   public void tick() {
      if (this.debugCrashKeyPressTime > 0L) {
         long i = Util.milliTime();
         long j = 10000L - (i - this.debugCrashKeyPressTime);
         long k = i - this.lastDebugCrashWarning;
         if (j < 0L) {
            if (Screen.hasControlDown()) {
               NativeUtil.func_216393_a();
            }

            throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
         }

         if (k >= 1000L) {
            if (this.debugCrashWarningsSent == 0L) {
               this.printDebugMessage("debug.crash.message");
            } else {
               this.printDebugWarning("debug.crash.warning", MathHelper.ceil((float)j / 1000.0F));
            }

            this.lastDebugCrashWarning = i;
            ++this.debugCrashWarningsSent;
         }
      }

   }
}
