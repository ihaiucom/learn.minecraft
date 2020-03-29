package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

public abstract class EntityViewRenderEvent extends Event {
   private final GameRenderer renderer;
   private final ActiveRenderInfo info;
   private final double renderPartialTicks;

   public EntityViewRenderEvent(GameRenderer renderer, ActiveRenderInfo info, double renderPartialTicks) {
      this.renderer = renderer;
      this.info = info;
      this.renderPartialTicks = renderPartialTicks;
   }

   public GameRenderer getRenderer() {
      return this.renderer;
   }

   public ActiveRenderInfo getInfo() {
      return this.info;
   }

   public double getRenderPartialTicks() {
      return this.renderPartialTicks;
   }

   public static class FOVModifier extends EntityViewRenderEvent {
      private double fov;

      public FOVModifier(GameRenderer renderer, ActiveRenderInfo info, double renderPartialTicks, double fov) {
         super(renderer, info, renderPartialTicks);
         this.setFOV(fov);
      }

      public double getFOV() {
         return this.fov;
      }

      public void setFOV(double fov) {
         this.fov = fov;
      }
   }

   public static class CameraSetup extends EntityViewRenderEvent {
      private float yaw;
      private float pitch;
      private float roll;

      public CameraSetup(GameRenderer renderer, ActiveRenderInfo info, double renderPartialTicks, float yaw, float pitch, float roll) {
         super(renderer, info, renderPartialTicks);
         this.setYaw(yaw);
         this.setPitch(pitch);
         this.setRoll(roll);
      }

      public float getYaw() {
         return this.yaw;
      }

      public void setYaw(float yaw) {
         this.yaw = yaw;
      }

      public float getPitch() {
         return this.pitch;
      }

      public void setPitch(float pitch) {
         this.pitch = pitch;
      }

      public float getRoll() {
         return this.roll;
      }

      public void setRoll(float roll) {
         this.roll = roll;
      }
   }

   public static class FogColors extends EntityViewRenderEvent {
      private float red;
      private float green;
      private float blue;

      public FogColors(ActiveRenderInfo info, float partialTicks, float red, float green, float blue) {
         super(Minecraft.getInstance().gameRenderer, info, (double)partialTicks);
         this.setRed(red);
         this.setGreen(green);
         this.setBlue(blue);
      }

      public float getRed() {
         return this.red;
      }

      public void setRed(float red) {
         this.red = red;
      }

      public float getGreen() {
         return this.green;
      }

      public void setGreen(float green) {
         this.green = green;
      }

      public float getBlue() {
         return this.blue;
      }

      public void setBlue(float blue) {
         this.blue = blue;
      }
   }

   @HasResult
   public static class RenderFogEvent extends EntityViewRenderEvent.FogEvent {
      private final float farPlaneDistance;

      public RenderFogEvent(FogRenderer.FogType type, ActiveRenderInfo info, float partialTicks, float distance) {
         super(type, info, (double)partialTicks);
         this.farPlaneDistance = distance;
      }

      public float getFarPlaneDistance() {
         return this.farPlaneDistance;
      }
   }

   @Cancelable
   public static class FogDensity extends EntityViewRenderEvent.FogEvent {
      private float density;

      public FogDensity(FogRenderer.FogType type, ActiveRenderInfo info, float partialTicks, float density) {
         super(type, info, (double)partialTicks);
         this.setDensity(density);
      }

      public float getDensity() {
         return this.density;
      }

      public void setDensity(float density) {
         this.density = density;
      }
   }

   private static class FogEvent extends EntityViewRenderEvent {
      private final FogRenderer.FogType type;

      protected FogEvent(FogRenderer.FogType type, ActiveRenderInfo info, double renderPartialTicks) {
         super(Minecraft.getInstance().gameRenderer, info, renderPartialTicks);
         this.type = type;
      }

      public FogRenderer.FogType getType() {
         return this.type;
      }
   }
}
