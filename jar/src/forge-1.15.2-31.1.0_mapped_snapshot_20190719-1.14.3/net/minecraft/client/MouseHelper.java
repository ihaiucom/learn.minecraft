package net.minecraft.client;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.MouseSmoother;
import net.minecraft.client.util.NativeUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class MouseHelper {
   private final Minecraft minecraft;
   private boolean leftDown;
   private boolean middleDown;
   private boolean rightDown;
   private double mouseX;
   private double mouseY;
   private int simulatedRightClicks;
   private int activeButton = -1;
   private boolean ignoreFirstMove = true;
   private int touchScreenCounter;
   private double eventTime;
   private final MouseSmoother xSmoother = new MouseSmoother();
   private final MouseSmoother ySmoother = new MouseSmoother();
   private double xVelocity;
   private double yVelocity;
   private double accumulatedScrollDelta;
   private double lastLookTime = Double.MIN_VALUE;
   private boolean mouseGrabbed;

   public MouseHelper(Minecraft p_i47672_1_) {
      this.minecraft = p_i47672_1_;
   }

   private void mouseButtonCallback(long p_198023_1_, int p_198023_3_, int p_198023_4_, int p_198023_5_) {
      if (p_198023_1_ == this.minecraft.func_228018_at_().getHandle()) {
         boolean flag = p_198023_4_ == 1;
         if (Minecraft.IS_RUNNING_ON_MAC && p_198023_3_ == 0) {
            if (flag) {
               if ((p_198023_5_ & 2) == 2) {
                  p_198023_3_ = 1;
                  ++this.simulatedRightClicks;
               }
            } else if (this.simulatedRightClicks > 0) {
               p_198023_3_ = 1;
               --this.simulatedRightClicks;
            }
         }

         if (flag) {
            if (this.minecraft.gameSettings.touchscreen && this.touchScreenCounter++ > 0) {
               return;
            }

            this.activeButton = p_198023_3_;
            this.eventTime = NativeUtil.func_216394_b();
         } else if (this.activeButton != -1) {
            if (this.minecraft.gameSettings.touchscreen && --this.touchScreenCounter > 0) {
               return;
            }

            this.activeButton = -1;
         }

         if (ForgeHooksClient.onRawMouseClicked(p_198023_3_, p_198023_4_, p_198023_5_)) {
            return;
         }

         boolean[] aboolean = new boolean[]{false};
         if (this.minecraft.loadingGui == null) {
            if (this.minecraft.currentScreen == null) {
               if (!this.mouseGrabbed && flag) {
                  this.grabMouse();
               }
            } else {
               double d0 = this.mouseX * (double)this.minecraft.func_228018_at_().getScaledWidth() / (double)this.minecraft.func_228018_at_().getWidth();
               double d1 = this.mouseY * (double)this.minecraft.func_228018_at_().getScaledHeight() / (double)this.minecraft.func_228018_at_().getHeight();
               if (flag) {
                  Screen.wrapScreenError(() -> {
                     aboolean[0] = ForgeHooksClient.onGuiMouseClickedPre(this.minecraft.currentScreen, d0, d1, p_198023_3_);
                     if (!aboolean[0]) {
                        aboolean[0] = this.minecraft.currentScreen.mouseClicked(d0, d1, p_198023_3_);
                     }

                     if (!aboolean[0]) {
                        aboolean[0] = ForgeHooksClient.onGuiMouseClickedPost(this.minecraft.currentScreen, d0, d1, p_198023_3_);
                     }

                  }, "mouseClicked event handler", this.minecraft.currentScreen.getClass().getCanonicalName());
               } else {
                  Screen.wrapScreenError(() -> {
                     aboolean[0] = ForgeHooksClient.onGuiMouseReleasedPre(this.minecraft.currentScreen, d0, d1, p_198023_3_);
                     if (!aboolean[0]) {
                        aboolean[0] = this.minecraft.currentScreen.mouseReleased(d0, d1, p_198023_3_);
                     }

                     if (!aboolean[0]) {
                        aboolean[0] = ForgeHooksClient.onGuiMouseReleasedPost(this.minecraft.currentScreen, d0, d1, p_198023_3_);
                     }

                  }, "mouseReleased event handler", this.minecraft.currentScreen.getClass().getCanonicalName());
               }
            }
         }

         if (!aboolean[0] && (this.minecraft.currentScreen == null || this.minecraft.currentScreen.passEvents) && this.minecraft.loadingGui == null) {
            if (p_198023_3_ == 0) {
               this.leftDown = flag;
            } else if (p_198023_3_ == 2) {
               this.middleDown = flag;
            } else if (p_198023_3_ == 1) {
               this.rightDown = flag;
            }

            KeyBinding.setKeyBindState(InputMappings.Type.MOUSE.getOrMakeInput(p_198023_3_), flag);
            if (flag) {
               if (this.minecraft.player.isSpectator() && p_198023_3_ == 2) {
                  this.minecraft.ingameGUI.getSpectatorGui().onMiddleClick();
               } else {
                  KeyBinding.onTick(InputMappings.Type.MOUSE.getOrMakeInput(p_198023_3_));
               }
            }
         }

         ForgeHooksClient.fireMouseInput(p_198023_3_, p_198023_4_, p_198023_5_);
      }

   }

   private void scrollCallback(long p_198020_1_, double p_198020_3_, double p_198020_5_) {
      if (p_198020_1_ == Minecraft.getInstance().func_228018_at_().getHandle()) {
         double d0 = (this.minecraft.gameSettings.discreteMouseScroll ? Math.signum(p_198020_5_) : p_198020_5_) * this.minecraft.gameSettings.mouseWheelSensitivity;
         if (this.minecraft.loadingGui == null) {
            if (this.minecraft.currentScreen != null) {
               double d1 = this.mouseX * (double)this.minecraft.func_228018_at_().getScaledWidth() / (double)this.minecraft.func_228018_at_().getWidth();
               double d2 = this.mouseY * (double)this.minecraft.func_228018_at_().getScaledHeight() / (double)this.minecraft.func_228018_at_().getHeight();
               if (ForgeHooksClient.onGuiMouseScrollPre(this, this.minecraft.currentScreen, d0)) {
                  return;
               }

               if (this.minecraft.currentScreen.mouseScrolled(d1, d2, d0)) {
                  return;
               }

               ForgeHooksClient.onGuiMouseScrollPost(this, this.minecraft.currentScreen, d0);
            } else if (this.minecraft.player != null) {
               if (this.accumulatedScrollDelta != 0.0D && Math.signum(d0) != Math.signum(this.accumulatedScrollDelta)) {
                  this.accumulatedScrollDelta = 0.0D;
               }

               this.accumulatedScrollDelta += d0;
               float f1 = (float)((int)this.accumulatedScrollDelta);
               if (f1 == 0.0F) {
                  return;
               }

               this.accumulatedScrollDelta -= (double)f1;
               if (ForgeHooksClient.onMouseScroll(this, d0)) {
                  return;
               }

               if (this.minecraft.player.isSpectator()) {
                  if (this.minecraft.ingameGUI.getSpectatorGui().isMenuActive()) {
                     this.minecraft.ingameGUI.getSpectatorGui().onMouseScroll((double)(-f1));
                  } else {
                     float f = MathHelper.clamp(this.minecraft.player.abilities.getFlySpeed() + f1 * 0.005F, 0.0F, 0.2F);
                     this.minecraft.player.abilities.setFlySpeed(f);
                  }
               } else {
                  this.minecraft.player.inventory.changeCurrentItem((double)f1);
               }
            }
         }
      }

   }

   public void registerCallbacks(long p_198029_1_) {
      InputMappings.func_216503_a(p_198029_1_, (p_lambda$registerCallbacks$3_1_, p_lambda$registerCallbacks$3_3_, p_lambda$registerCallbacks$3_5_) -> {
         this.minecraft.execute(() -> {
            this.cursorPosCallback(p_lambda$registerCallbacks$3_1_, p_lambda$registerCallbacks$3_3_, p_lambda$registerCallbacks$3_5_);
         });
      }, (p_lambda$registerCallbacks$5_1_, p_lambda$registerCallbacks$5_3_, p_lambda$registerCallbacks$5_4_, p_lambda$registerCallbacks$5_5_) -> {
         this.minecraft.execute(() -> {
            this.mouseButtonCallback(p_lambda$registerCallbacks$5_1_, p_lambda$registerCallbacks$5_3_, p_lambda$registerCallbacks$5_4_, p_lambda$registerCallbacks$5_5_);
         });
      }, (p_lambda$registerCallbacks$7_1_, p_lambda$registerCallbacks$7_3_, p_lambda$registerCallbacks$7_5_) -> {
         this.minecraft.execute(() -> {
            this.scrollCallback(p_lambda$registerCallbacks$7_1_, p_lambda$registerCallbacks$7_3_, p_lambda$registerCallbacks$7_5_);
         });
      });
   }

   private void cursorPosCallback(long p_198022_1_, double p_198022_3_, double p_198022_5_) {
      if (p_198022_1_ == Minecraft.getInstance().func_228018_at_().getHandle()) {
         if (this.ignoreFirstMove) {
            this.mouseX = p_198022_3_;
            this.mouseY = p_198022_5_;
            this.ignoreFirstMove = false;
         }

         IGuiEventListener iguieventlistener = this.minecraft.currentScreen;
         if (iguieventlistener != null && this.minecraft.loadingGui == null) {
            double d0 = p_198022_3_ * (double)this.minecraft.func_228018_at_().getScaledWidth() / (double)this.minecraft.func_228018_at_().getWidth();
            double d1 = p_198022_5_ * (double)this.minecraft.func_228018_at_().getScaledHeight() / (double)this.minecraft.func_228018_at_().getHeight();
            Screen.wrapScreenError(() -> {
               iguieventlistener.mouseMoved(d0, d1);
            }, "mouseMoved event handler", iguieventlistener.getClass().getCanonicalName());
            if (this.activeButton != -1 && this.eventTime > 0.0D) {
               double d2 = (p_198022_3_ - this.mouseX) * (double)this.minecraft.func_228018_at_().getScaledWidth() / (double)this.minecraft.func_228018_at_().getWidth();
               double d3 = (p_198022_5_ - this.mouseY) * (double)this.minecraft.func_228018_at_().getScaledHeight() / (double)this.minecraft.func_228018_at_().getHeight();
               Screen.wrapScreenError(() -> {
                  if (!ForgeHooksClient.onGuiMouseDragPre(this.minecraft.currentScreen, d0, d1, this.activeButton, d2, d3)) {
                     if (!iguieventlistener.mouseDragged(d0, d1, this.activeButton, d2, d3)) {
                        ForgeHooksClient.onGuiMouseDragPost(this.minecraft.currentScreen, d0, d1, this.activeButton, d2, d3);
                     }
                  }
               }, "mouseDragged event handler", iguieventlistener.getClass().getCanonicalName());
            }
         }

         this.minecraft.getProfiler().startSection("mouse");
         if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
            this.xVelocity += p_198022_3_ - this.mouseX;
            this.yVelocity += p_198022_5_ - this.mouseY;
         }

         this.updatePlayerLook();
         this.mouseX = p_198022_3_;
         this.mouseY = p_198022_5_;
         this.minecraft.getProfiler().endSection();
      }

   }

   public void updatePlayerLook() {
      double d0 = NativeUtil.func_216394_b();
      double d1 = d0 - this.lastLookTime;
      this.lastLookTime = d0;
      if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
         double d4 = this.minecraft.gameSettings.mouseSensitivity * 0.6000000238418579D + 0.20000000298023224D;
         double d5 = d4 * d4 * d4 * 8.0D;
         double d2;
         double d3;
         if (this.minecraft.gameSettings.smoothCamera) {
            double d6 = this.xSmoother.smooth(this.xVelocity * d5, d1 * d5);
            double d7 = this.ySmoother.smooth(this.yVelocity * d5, d1 * d5);
            d2 = d6;
            d3 = d7;
         } else {
            this.xSmoother.reset();
            this.ySmoother.reset();
            d2 = this.xVelocity * d5;
            d3 = this.yVelocity * d5;
         }

         this.xVelocity = 0.0D;
         this.yVelocity = 0.0D;
         int i = 1;
         if (this.minecraft.gameSettings.invertMouse) {
            i = -1;
         }

         this.minecraft.getTutorial().onMouseMove(d2, d3);
         if (this.minecraft.player != null) {
            this.minecraft.player.rotateTowards(d2, d3 * (double)i);
         }
      } else {
         this.xVelocity = 0.0D;
         this.yVelocity = 0.0D;
      }

   }

   public boolean isLeftDown() {
      return this.leftDown;
   }

   public boolean isRightDown() {
      return this.rightDown;
   }

   public boolean isMiddleDown() {
      return this.middleDown;
   }

   public double getMouseX() {
      return this.mouseX;
   }

   public double getMouseY() {
      return this.mouseY;
   }

   public double getXVelocity() {
      return this.xVelocity;
   }

   public double getYVelocity() {
      return this.yVelocity;
   }

   public void setIgnoreFirstMove() {
      this.ignoreFirstMove = true;
   }

   public boolean isMouseGrabbed() {
      return this.mouseGrabbed;
   }

   public void grabMouse() {
      if (this.minecraft.isGameFocused() && !this.mouseGrabbed) {
         if (!Minecraft.IS_RUNNING_ON_MAC) {
            KeyBinding.updateKeyBindState();
         }

         this.mouseGrabbed = true;
         this.mouseX = (double)(this.minecraft.func_228018_at_().getWidth() / 2);
         this.mouseY = (double)(this.minecraft.func_228018_at_().getHeight() / 2);
         InputMappings.func_216504_a(this.minecraft.func_228018_at_().getHandle(), 212995, this.mouseX, this.mouseY);
         this.minecraft.displayGuiScreen((Screen)null);
         this.minecraft.leftClickCounter = 10000;
         this.ignoreFirstMove = true;
      }

   }

   public void ungrabMouse() {
      if (this.mouseGrabbed) {
         this.mouseGrabbed = false;
         this.mouseX = (double)(this.minecraft.func_228018_at_().getWidth() / 2);
         this.mouseY = (double)(this.minecraft.func_228018_at_().getHeight() / 2);
         InputMappings.func_216504_a(this.minecraft.func_228018_at_().getHandle(), 212993, this.mouseX, this.mouseY);
      }

   }
}
