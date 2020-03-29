package net.minecraft.client.gui.overlay;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

@OnlyIn(Dist.CLIENT)
public class BossOverlayGui extends AbstractGui {
   private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
   private final Minecraft client;
   private final Map<UUID, ClientBossInfo> mapBossInfos = Maps.newLinkedHashMap();

   public BossOverlayGui(Minecraft p_i46606_1_) {
      this.client = p_i46606_1_;
   }

   public void render() {
      if (!this.mapBossInfos.isEmpty()) {
         int i = this.client.func_228018_at_().getScaledWidth();
         int j = 12;
         Iterator var3 = this.mapBossInfos.values().iterator();

         while(var3.hasNext()) {
            ClientBossInfo clientbossinfo = (ClientBossInfo)var3.next();
            int k = i / 2 - 91;
            MainWindow var10000 = this.client.func_228018_at_();
            this.client.fontRenderer.getClass();
            RenderGameOverlayEvent.BossInfo event = ForgeHooksClient.bossBarRenderPre(var10000, clientbossinfo, k, j, 10 + 9);
            if (!event.isCanceled()) {
               RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               this.client.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
               this.render(k, j, clientbossinfo);
               String s = clientbossinfo.getName().getFormattedText();
               int l = this.client.fontRenderer.getStringWidth(s);
               int i1 = i / 2 - l / 2;
               int j1 = j - 9;
               this.client.fontRenderer.drawStringWithShadow(s, (float)i1, (float)j1, 16777215);
            }

            j += event.getIncrement();
            ForgeHooksClient.bossBarRenderPost(this.client.func_228018_at_());
            if (j >= this.client.func_228018_at_().getScaledHeight() / 3) {
               break;
            }
         }
      }

   }

   private void render(int p_184052_1_, int p_184052_2_, BossInfo p_184052_3_) {
      this.blit(p_184052_1_, p_184052_2_, 0, p_184052_3_.getColor().ordinal() * 5 * 2, 182, 5);
      if (p_184052_3_.getOverlay() != BossInfo.Overlay.PROGRESS) {
         this.blit(p_184052_1_, p_184052_2_, 0, 80 + (p_184052_3_.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int i = (int)(p_184052_3_.getPercent() * 183.0F);
      if (i > 0) {
         this.blit(p_184052_1_, p_184052_2_, 0, p_184052_3_.getColor().ordinal() * 5 * 2 + 5, i, 5);
         if (p_184052_3_.getOverlay() != BossInfo.Overlay.PROGRESS) {
            this.blit(p_184052_1_, p_184052_2_, 0, 80 + (p_184052_3_.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
         }
      }

   }

   public void read(SUpdateBossInfoPacket p_184055_1_) {
      if (p_184055_1_.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
         this.mapBossInfos.put(p_184055_1_.getUniqueId(), new ClientBossInfo(p_184055_1_));
      } else if (p_184055_1_.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
         this.mapBossInfos.remove(p_184055_1_.getUniqueId());
      } else {
         ((ClientBossInfo)this.mapBossInfos.get(p_184055_1_.getUniqueId())).updateFromPacket(p_184055_1_);
      }

   }

   public void clearBossInfos() {
      this.mapBossInfos.clear();
   }

   public boolean shouldPlayEndBossMusic() {
      if (!this.mapBossInfos.isEmpty()) {
         Iterator var1 = this.mapBossInfos.values().iterator();

         while(var1.hasNext()) {
            BossInfo bossinfo = (BossInfo)var1.next();
            if (bossinfo.shouldPlayEndBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenSky() {
      if (!this.mapBossInfos.isEmpty()) {
         Iterator var1 = this.mapBossInfos.values().iterator();

         while(var1.hasNext()) {
            BossInfo bossinfo = (BossInfo)var1.next();
            if (bossinfo.shouldDarkenSky()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateFog() {
      if (!this.mapBossInfos.isEmpty()) {
         Iterator var1 = this.mapBossInfos.values().iterator();

         while(var1.hasNext()) {
            BossInfo bossinfo = (BossInfo)var1.next();
            if (bossinfo.shouldCreateFog()) {
               return true;
            }
         }
      }

      return false;
   }
}
