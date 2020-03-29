package net.minecraftforge.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public class ForgeIngameGui extends IngameGui {
   private static final int WHITE = 16777215;
   public static boolean renderVignette = true;
   public static boolean renderHelmet = true;
   public static boolean renderPortal = true;
   public static boolean renderSpectatorTooltip = true;
   public static boolean renderHotbar = true;
   public static boolean renderCrosshairs = true;
   public static boolean renderBossHealth = true;
   public static boolean renderHealth = true;
   public static boolean renderArmor = true;
   public static boolean renderFood = true;
   public static boolean renderHealthMount = true;
   public static boolean renderAir = true;
   public static boolean renderExperiance = true;
   public static boolean renderJumpBar = true;
   public static boolean renderObjective = true;
   public static int left_height = 39;
   public static int right_height = 39;
   public static double rayTraceDistance = 20.0D;
   private FontRenderer fontrenderer = null;
   private RenderGameOverlayEvent eventParent;
   private ForgeIngameGui.GuiOverlayDebugForge debugOverlay;

   public ForgeIngameGui(Minecraft mc) {
      super(mc);
      this.debugOverlay = new ForgeIngameGui.GuiOverlayDebugForge(mc);
   }

   public void renderGameOverlay(float partialTicks) {
      this.scaledWidth = this.mc.func_228018_at_().getScaledWidth();
      this.scaledHeight = this.mc.func_228018_at_().getScaledHeight();
      this.eventParent = new RenderGameOverlayEvent(partialTicks, this.mc.func_228018_at_());
      renderHealthMount = this.mc.player.getRidingEntity() instanceof LivingEntity;
      renderFood = this.mc.player.getRidingEntity() == null;
      renderJumpBar = this.mc.player.isRidingHorse();
      right_height = 39;
      left_height = 39;
      if (!this.pre(RenderGameOverlayEvent.ElementType.ALL)) {
         this.fontrenderer = this.mc.fontRenderer;
         RenderSystem.enableBlend();
         if (renderVignette && Minecraft.isFancyGraphicsEnabled()) {
            this.renderVignette(this.mc.getRenderViewEntity());
         } else {
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
         }

         if (renderHelmet) {
            this.renderHelmet(partialTicks);
         }

         if (renderPortal && !this.mc.player.isPotionActive(Effects.NAUSEA)) {
            this.renderPortal(partialTicks);
         }

         if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
            if (renderSpectatorTooltip) {
               this.spectatorGui.renderTooltip(partialTicks);
            }
         } else if (!this.mc.gameSettings.hideGUI && renderHotbar) {
            this.renderHotbar(partialTicks);
         }

         if (!this.mc.gameSettings.hideGUI) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.setBlitOffset(-90);
            this.rand.setSeed((long)(this.ticks * 312871));
            if (renderCrosshairs) {
               this.renderAttackIndicator();
            }

            if (renderBossHealth) {
               this.renderBossHealth();
            }

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (this.mc.playerController.shouldDrawHUD() && this.mc.getRenderViewEntity() instanceof PlayerEntity) {
               if (renderHealth) {
                  this.renderHealth(this.scaledWidth, this.scaledHeight);
               }

               if (renderArmor) {
                  this.renderArmor(this.scaledWidth, this.scaledHeight);
               }

               if (renderFood) {
                  this.renderFood(this.scaledWidth, this.scaledHeight);
               }

               if (renderHealthMount) {
                  this.renderHealthMount(this.scaledWidth, this.scaledHeight);
               }

               if (renderAir) {
                  this.renderAir(this.scaledWidth, this.scaledHeight);
               }
            }

            if (renderJumpBar) {
               this.renderHorseJumpBar(this.scaledWidth / 2 - 91);
            } else if (renderExperiance) {
               this.renderExperience(this.scaledWidth / 2 - 91);
            }

            if (this.mc.gameSettings.heldItemTooltips && this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {
               this.renderSelectedItem();
            } else if (this.mc.player.isSpectator()) {
               this.spectatorGui.renderSelectedItem();
            }
         }

         this.renderSleepFade(this.scaledWidth, this.scaledHeight);
         this.renderHUDText(this.scaledWidth, this.scaledHeight);
         this.renderFPSGraph();
         this.renderPotionEffects();
         if (!this.mc.gameSettings.hideGUI) {
            this.renderRecordOverlay(this.scaledWidth, this.scaledHeight, partialTicks);
            this.renderSubtitles();
            this.renderTitle(this.scaledWidth, this.scaledHeight, partialTicks);
         }

         Scoreboard scoreboard = this.mc.world.getScoreboard();
         ScoreObjective objective = null;
         ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.player.getScoreboardName());
         if (scoreplayerteam != null) {
            int slot = scoreplayerteam.getColor().getColorIndex();
            if (slot >= 0) {
               objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
            }
         }

         ScoreObjective scoreobjective1 = objective != null ? objective : scoreboard.getObjectiveInDisplaySlot(1);
         if (renderObjective && scoreobjective1 != null) {
            this.renderScoreboard(scoreobjective1);
         }

         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(770, 771, 1, 0);
         RenderSystem.disableAlphaTest();
         this.renderChat(this.scaledWidth, this.scaledHeight);
         this.renderPlayerList(this.scaledWidth, this.scaledHeight);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableAlphaTest();
         this.post(RenderGameOverlayEvent.ElementType.ALL);
      }
   }

   protected void renderAttackIndicator() {
      if (!this.pre(RenderGameOverlayEvent.ElementType.CROSSHAIRS)) {
         this.bind(AbstractGui.GUI_ICONS_LOCATION);
         RenderSystem.enableBlend();
         RenderSystem.enableAlphaTest();
         super.renderAttackIndicator();
         this.post(RenderGameOverlayEvent.ElementType.CROSSHAIRS);
      }
   }

   protected void renderPotionEffects() {
      if (!this.pre(RenderGameOverlayEvent.ElementType.POTION_ICONS)) {
         super.renderPotionEffects();
         this.post(RenderGameOverlayEvent.ElementType.POTION_ICONS);
      }
   }

   protected void renderSubtitles() {
      if (!this.pre(RenderGameOverlayEvent.ElementType.SUBTITLES)) {
         this.overlaySubtitle.render();
         this.post(RenderGameOverlayEvent.ElementType.SUBTITLES);
      }
   }

   protected void renderBossHealth() {
      if (!this.pre(RenderGameOverlayEvent.ElementType.BOSSHEALTH)) {
         this.bind(AbstractGui.GUI_ICONS_LOCATION);
         RenderSystem.defaultBlendFunc();
         this.mc.getProfiler().startSection("bossHealth");
         RenderSystem.enableBlend();
         this.overlayBoss.render();
         RenderSystem.disableBlend();
         this.mc.getProfiler().endSection();
         this.post(RenderGameOverlayEvent.ElementType.BOSSHEALTH);
      }
   }

   protected void renderVignette(Entity entity) {
      if (this.pre(RenderGameOverlayEvent.ElementType.VIGNETTE)) {
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
      } else {
         super.renderVignette(entity);
         this.post(RenderGameOverlayEvent.ElementType.VIGNETTE);
      }
   }

   private void renderHelmet(float partialTicks) {
      if (!this.pre(RenderGameOverlayEvent.ElementType.HELMET)) {
         ItemStack itemstack = this.mc.player.inventory.armorItemInSlot(3);
         if (this.mc.gameSettings.thirdPersonView == 0 && !itemstack.isEmpty()) {
            Item item = itemstack.getItem();
            if (item == Blocks.CARVED_PUMPKIN.asItem()) {
               this.renderPumpkinOverlay();
            } else {
               item.renderHelmetOverlay(itemstack, this.mc.player, this.scaledWidth, this.scaledHeight, partialTicks);
            }
         }

         this.post(RenderGameOverlayEvent.ElementType.HELMET);
      }
   }

   protected void renderArmor(int width, int height) {
      if (!this.pre(RenderGameOverlayEvent.ElementType.ARMOR)) {
         this.mc.getProfiler().startSection("armor");
         RenderSystem.enableBlend();
         int left = width / 2 - 91;
         int top = height - left_height;
         int level = this.mc.player.getTotalArmorValue();

         for(int i = 1; level > 0 && i < 20; i += 2) {
            if (i < level) {
               this.blit(left, top, 34, 9, 9, 9);
            } else if (i == level) {
               this.blit(left, top, 25, 9, 9, 9);
            } else if (i > level) {
               this.blit(left, top, 16, 9, 9, 9);
            }

            left += 8;
         }

         left_height += 10;
         RenderSystem.disableBlend();
         this.mc.getProfiler().endSection();
         this.post(RenderGameOverlayEvent.ElementType.ARMOR);
      }
   }

   protected void renderPortal(float partialTicks) {
      if (!this.pre(RenderGameOverlayEvent.ElementType.PORTAL)) {
         float f1 = this.mc.player.prevTimeInPortal + (this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal) * partialTicks;
         if (f1 > 0.0F) {
            super.renderPortal(f1);
         }

         this.post(RenderGameOverlayEvent.ElementType.PORTAL);
      }
   }

   protected void renderHotbar(float partialTicks) {
      if (!this.pre(RenderGameOverlayEvent.ElementType.HOTBAR)) {
         if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
            this.spectatorGui.renderTooltip(partialTicks);
         } else {
            super.renderHotbar(partialTicks);
         }

         this.post(RenderGameOverlayEvent.ElementType.HOTBAR);
      }
   }

   public void setOverlayMessage(ITextComponent component, boolean animateColor) {
      this.setOverlayMessage(component.getFormattedText(), animateColor);
   }

   protected void renderAir(int width, int height) {
      if (!this.pre(RenderGameOverlayEvent.ElementType.AIR)) {
         this.mc.getProfiler().startSection("air");
         PlayerEntity player = (PlayerEntity)this.mc.getRenderViewEntity();
         RenderSystem.enableBlend();
         int left = width / 2 + 91;
         int top = height - right_height;
         int air = player.getAir();
         if (player.areEyesInFluid(FluidTags.WATER) || air < 300) {
            int full = MathHelper.ceil((double)(air - 2) * 10.0D / 300.0D);
            int partial = MathHelper.ceil((double)air * 10.0D / 300.0D) - full;

            for(int i = 0; i < full + partial; ++i) {
               this.blit(left - i * 8 - 9, top, i < full ? 16 : 25, 18, 9, 9);
            }

            right_height += 10;
         }

         RenderSystem.disableBlend();
         this.mc.getProfiler().endSection();
         this.post(RenderGameOverlayEvent.ElementType.AIR);
      }
   }

   public void renderHealth(int width, int height) {
      this.bind(GUI_ICONS_LOCATION);
      if (!this.pre(RenderGameOverlayEvent.ElementType.HEALTH)) {
         this.mc.getProfiler().startSection("health");
         RenderSystem.enableBlend();
         PlayerEntity player = (PlayerEntity)this.mc.getRenderViewEntity();
         int health = MathHelper.ceil(player.getHealth());
         boolean highlight = this.healthUpdateCounter > (long)this.ticks && (this.healthUpdateCounter - (long)this.ticks) / 3L % 2L == 1L;
         if (health < this.playerHealth && player.hurtResistantTime > 0) {
            this.lastSystemTime = Util.milliTime();
            this.healthUpdateCounter = (long)(this.ticks + 20);
         } else if (health > this.playerHealth && player.hurtResistantTime > 0) {
            this.lastSystemTime = Util.milliTime();
            this.healthUpdateCounter = (long)(this.ticks + 10);
         }

         if (Util.milliTime() - this.lastSystemTime > 1000L) {
            this.playerHealth = health;
            this.lastPlayerHealth = health;
            this.lastSystemTime = Util.milliTime();
         }

         this.playerHealth = health;
         int healthLast = this.lastPlayerHealth;
         IAttributeInstance attrMaxHealth = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
         float healthMax = (float)attrMaxHealth.getValue();
         float absorb = (float)MathHelper.ceil(player.getAbsorptionAmount());
         int healthRows = MathHelper.ceil((healthMax + absorb) / 2.0F / 10.0F);
         int rowHeight = Math.max(10 - (healthRows - 2), 3);
         this.rand.setSeed((long)(this.ticks * 312871));
         int left = width / 2 - 91;
         int top = height - left_height;
         left_height += healthRows * rowHeight;
         if (rowHeight != 10) {
            left_height += 10 - rowHeight;
         }

         int regen = -1;
         if (player.isPotionActive(Effects.REGENERATION)) {
            regen = this.ticks % 25;
         }

         int TOP = 9 * (this.mc.world.getWorldInfo().isHardcore() ? 5 : 0);
         int BACKGROUND = highlight ? 25 : 16;
         int MARGIN = 16;
         if (player.isPotionActive(Effects.POISON)) {
            MARGIN += 36;
         } else if (player.isPotionActive(Effects.WITHER)) {
            MARGIN += 72;
         }

         float absorbRemaining = absorb;

         for(int i = MathHelper.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            int row = MathHelper.ceil((float)(i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;
            if (health <= 4) {
               y += this.rand.nextInt(2);
            }

            if (i == regen) {
               y -= 2;
            }

            this.blit(x, y, BACKGROUND, TOP, 9, 9);
            if (highlight) {
               if (i * 2 + 1 < healthLast) {
                  this.blit(x, y, MARGIN + 54, TOP, 9, 9);
               } else if (i * 2 + 1 == healthLast) {
                  this.blit(x, y, MARGIN + 63, TOP, 9, 9);
               }
            }

            if (absorbRemaining > 0.0F) {
               if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                  this.blit(x, y, MARGIN + 153, TOP, 9, 9);
                  --absorbRemaining;
               } else {
                  this.blit(x, y, MARGIN + 144, TOP, 9, 9);
                  absorbRemaining -= 2.0F;
               }
            } else if (i * 2 + 1 < health) {
               this.blit(x, y, MARGIN + 36, TOP, 9, 9);
            } else if (i * 2 + 1 == health) {
               this.blit(x, y, MARGIN + 45, TOP, 9, 9);
            }
         }

         RenderSystem.disableBlend();
         this.mc.getProfiler().endSection();
         this.post(RenderGameOverlayEvent.ElementType.HEALTH);
      }
   }

   public void renderFood(int width, int height) {
      if (!this.pre(RenderGameOverlayEvent.ElementType.FOOD)) {
         this.mc.getProfiler().startSection("food");
         PlayerEntity player = (PlayerEntity)this.mc.getRenderViewEntity();
         RenderSystem.enableBlend();
         int left = width / 2 + 91;
         int top = height - right_height;
         right_height += 10;
         boolean unused = false;
         FoodStats stats = this.mc.player.getFoodStats();
         int level = stats.getFoodLevel();

         for(int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            byte background = 0;
            if (this.mc.player.isPotionActive(Effects.HUNGER)) {
               icon += 36;
               background = 13;
            }

            if (unused) {
               background = 1;
            }

            if (player.getFoodStats().getSaturationLevel() <= 0.0F && this.ticks % (level * 3 + 1) == 0) {
               y = top + (this.rand.nextInt(3) - 1);
            }

            this.blit(x, y, 16 + background * 9, 27, 9, 9);
            if (idx < level) {
               this.blit(x, y, icon + 36, 27, 9, 9);
            } else if (idx == level) {
               this.blit(x, y, icon + 45, 27, 9, 9);
            }
         }

         RenderSystem.disableBlend();
         this.mc.getProfiler().endSection();
         this.post(RenderGameOverlayEvent.ElementType.FOOD);
      }
   }

   protected void renderSleepFade(int width, int height) {
      if (this.mc.player.getSleepTimer() > 0) {
         this.mc.getProfiler().startSection("sleep");
         RenderSystem.disableDepthTest();
         RenderSystem.disableAlphaTest();
         int sleepTime = this.mc.player.getSleepTimer();
         float opacity = (float)sleepTime / 100.0F;
         if (opacity > 1.0F) {
            opacity = 1.0F - (float)(sleepTime - 100) / 10.0F;
         }

         int color = (int)(220.0F * opacity) << 24 | 1052704;
         fill(0, 0, width, height, color);
         RenderSystem.enableAlphaTest();
         RenderSystem.enableDepthTest();
         this.mc.getProfiler().endSection();
      }

   }

   protected void renderExperience(int x) {
      this.bind(GUI_ICONS_LOCATION);
      if (!this.pre(RenderGameOverlayEvent.ElementType.EXPERIENCE)) {
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.disableBlend();
         if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            super.renderExpBar(x);
         }

         RenderSystem.enableBlend();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.post(RenderGameOverlayEvent.ElementType.EXPERIENCE);
      }
   }

   public void renderHorseJumpBar(int x) {
      this.bind(GUI_ICONS_LOCATION);
      if (!this.pre(RenderGameOverlayEvent.ElementType.JUMPBAR)) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.disableBlend();
         super.renderHorseJumpBar(x);
         RenderSystem.enableBlend();
         this.mc.getProfiler().endSection();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.post(RenderGameOverlayEvent.ElementType.JUMPBAR);
      }
   }

   protected void renderHUDText(int width, int height) {
      this.mc.getProfiler().startSection("forgeHudText");
      RenderSystem.defaultBlendFunc();
      ArrayList<String> listL = new ArrayList();
      ArrayList<String> listR = new ArrayList();
      if (this.mc.isDemo()) {
         long time = this.mc.world.getGameTime();
         if (time >= 120500L) {
            listR.add(I18n.format("demo.demoExpired"));
         } else {
            listR.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - time))));
         }
      }

      if (this.mc.gameSettings.showDebugInfo && !this.pre(RenderGameOverlayEvent.ElementType.DEBUG)) {
         this.debugOverlay.update();
         listL.addAll(this.debugOverlay.getLeft());
         listR.addAll(this.debugOverlay.getRight());
         this.post(RenderGameOverlayEvent.ElementType.DEBUG);
      }

      RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(this.eventParent, listL, listR);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         int top = 2;
         Iterator var7 = listL.iterator();

         String msg;
         int var10001;
         int var10002;
         while(var7.hasNext()) {
            msg = (String)var7.next();
            if (msg != null) {
               var10001 = top - 1;
               var10002 = 2 + this.fontrenderer.getStringWidth(msg) + 1;
               this.fontrenderer.getClass();
               fill(1, var10001, var10002, top + 9 - 1, -1873784752);
               this.fontrenderer.drawStringWithShadow(msg, 2.0F, (float)top, 14737632);
               this.fontrenderer.getClass();
               top += 9;
            }
         }

         top = 2;
         var7 = listR.iterator();

         while(var7.hasNext()) {
            msg = (String)var7.next();
            if (msg != null) {
               int w = this.fontrenderer.getStringWidth(msg);
               int left = width - 2 - w;
               int var10000 = left - 1;
               var10001 = top - 1;
               var10002 = left + w + 1;
               this.fontrenderer.getClass();
               fill(var10000, var10001, var10002, top + 9 - 1, -1873784752);
               this.fontrenderer.drawStringWithShadow(msg, (float)left, (float)top, 14737632);
               this.fontrenderer.getClass();
               top += 9;
            }
         }
      }

      this.mc.getProfiler().endSection();
      this.post(RenderGameOverlayEvent.ElementType.TEXT);
   }

   protected void renderFPSGraph() {
      if (this.mc.gameSettings.showDebugInfo && this.mc.gameSettings.showLagometer && !this.pre(RenderGameOverlayEvent.ElementType.FPS_GRAPH)) {
         this.debugOverlay.render();
         this.post(RenderGameOverlayEvent.ElementType.FPS_GRAPH);
      }

   }

   protected void renderRecordOverlay(int width, int height, float partialTicks) {
      if (this.overlayMessageTime > 0) {
         this.mc.getProfiler().startSection("overlayMessage");
         float hue = (float)this.overlayMessageTime - partialTicks;
         int opacity = (int)(hue * 256.0F / 20.0F);
         if (opacity > 255) {
            opacity = 255;
         }

         if (opacity > 0) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(width / 2), (float)(height - 68), 0.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            int color = this.animateOverlayMessageColor ? MathHelper.hsvToRGB(hue / 50.0F, 0.7F, 0.6F) & 16777215 : 16777215;
            this.fontrenderer.drawStringWithShadow(this.overlayMessage, (float)(-this.fontrenderer.getStringWidth(this.overlayMessage) / 2), -4.0F, color | opacity << 24);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
         }

         this.mc.getProfiler().endSection();
      }

   }

   protected void renderTitle(int width, int height, float partialTicks) {
      if (this.titlesTimer > 0) {
         this.mc.getProfiler().startSection("titleAndSubtitle");
         float age = (float)this.titlesTimer - partialTicks;
         int opacity = 255;
         if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
            float f3 = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - age;
            opacity = (int)(f3 * 255.0F / (float)this.titleFadeIn);
         }

         if (this.titlesTimer <= this.titleFadeOut) {
            opacity = (int)(age * 255.0F / (float)this.titleFadeOut);
         }

         opacity = MathHelper.clamp(opacity, 0, 255);
         if (opacity > 8) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(width / 2), (float)(height / 2), 0.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.pushMatrix();
            RenderSystem.scalef(4.0F, 4.0F, 4.0F);
            int l = opacity << 24 & -16777216;
            this.getFontRenderer().drawStringWithShadow(this.displayedTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedTitle) / 2), -10.0F, 16777215 | l);
            RenderSystem.popMatrix();
            RenderSystem.pushMatrix();
            RenderSystem.scalef(2.0F, 2.0F, 2.0F);
            this.getFontRenderer().drawStringWithShadow(this.displayedSubTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2), 5.0F, 16777215 | l);
            RenderSystem.popMatrix();
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
         }

         this.mc.getProfiler().endSection();
      }

   }

   protected void renderChat(int width, int height) {
      this.mc.getProfiler().startSection("chat");
      RenderGameOverlayEvent.Chat event = new RenderGameOverlayEvent.Chat(this.eventParent, 0, height - 48);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)event.getPosX(), (float)event.getPosY(), 0.0F);
         this.persistantChatGUI.render(this.ticks);
         RenderSystem.popMatrix();
         this.post(RenderGameOverlayEvent.ElementType.CHAT);
         this.mc.getProfiler().endSection();
      }
   }

   protected void renderPlayerList(int width, int height) {
      ScoreObjective scoreobjective = this.mc.world.getScoreboard().getObjectiveInDisplaySlot(0);
      ClientPlayNetHandler handler = this.mc.player.connection;
      if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null)) {
         this.overlayPlayerList.setVisible(true);
         if (this.pre(RenderGameOverlayEvent.ElementType.PLAYER_LIST)) {
            return;
         }

         this.overlayPlayerList.render(width, this.mc.world.getScoreboard(), scoreobjective);
         this.post(RenderGameOverlayEvent.ElementType.PLAYER_LIST);
      } else {
         this.overlayPlayerList.setVisible(false);
      }

   }

   protected void renderHealthMount(int width, int height) {
      PlayerEntity player = (PlayerEntity)this.mc.getRenderViewEntity();
      Entity tmp = player.getRidingEntity();
      if (tmp instanceof LivingEntity) {
         this.bind(GUI_ICONS_LOCATION);
         if (!this.pre(RenderGameOverlayEvent.ElementType.HEALTHMOUNT)) {
            boolean unused = false;
            int left_align = width / 2 + 91;
            this.mc.getProfiler().endStartSection("mountHealth");
            RenderSystem.enableBlend();
            LivingEntity mount = (LivingEntity)tmp;
            int health = (int)Math.ceil((double)mount.getHealth());
            float healthMax = mount.getMaxHealth();
            int hearts = (int)(healthMax + 0.5F) / 2;
            if (hearts > 30) {
               hearts = 30;
            }

            int MARGIN = true;
            int BACKGROUND = 52 + (unused ? 1 : 0);
            int HALF = true;
            int FULL = true;

            for(int heart = 0; hearts > 0; heart += 20) {
               int top = height - right_height;
               int rowCount = Math.min(hearts, 10);
               hearts -= rowCount;

               for(int i = 0; i < rowCount; ++i) {
                  int x = left_align - i * 8 - 9;
                  this.blit(x, top, BACKGROUND, 9, 9, 9);
                  if (i * 2 + 1 + heart < health) {
                     this.blit(x, top, 88, 9, 9, 9);
                  } else if (i * 2 + 1 + heart == health) {
                     this.blit(x, top, 97, 9, 9, 9);
                  }
               }

               right_height += 10;
            }

            RenderSystem.disableBlend();
            this.post(RenderGameOverlayEvent.ElementType.HEALTHMOUNT);
         }
      }
   }

   private boolean pre(RenderGameOverlayEvent.ElementType type) {
      return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(this.eventParent, type));
   }

   private void post(RenderGameOverlayEvent.ElementType type) {
      MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(this.eventParent, type));
   }

   private void bind(ResourceLocation res) {
      this.mc.getTextureManager().bindTexture(res);
   }

   private class GuiOverlayDebugForge extends DebugOverlayGui {
      private Minecraft mc;

      private GuiOverlayDebugForge(Minecraft mc) {
         super(mc);
         this.mc = mc;
      }

      public void update() {
         Entity entity = this.mc.getRenderViewEntity();
         this.rayTraceBlock = entity.func_213324_a(ForgeIngameGui.rayTraceDistance, 0.0F, false);
         this.rayTraceFluid = entity.func_213324_a(ForgeIngameGui.rayTraceDistance, 0.0F, true);
      }

      protected void func_230024_c_() {
      }

      protected void func_230025_d_() {
      }

      private List<String> getLeft() {
         List<String> ret = this.call();
         ret.add("");
         ret.add("Debug: Pie [shift]: " + (this.mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden") + " FPS [alt]: " + (this.mc.gameSettings.showLagometer ? "visible" : "hidden"));
         ret.add("For help: press F3 + Q");
         return ret;
      }

      private List<String> getRight() {
         return this.getDebugInfoRight();
      }

      // $FF: synthetic method
      GuiOverlayDebugForge(Minecraft x1, Object x2) {
         this(x1);
      }
   }
}
