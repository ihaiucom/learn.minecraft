package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.SpectatorGui;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> items = Lists.newArrayList();

   public TeleportToTeam() {
      Minecraft lvt_1_1_ = Minecraft.getInstance();
      Iterator var2 = lvt_1_1_.world.getScoreboard().getTeams().iterator();

      while(var2.hasNext()) {
         ScorePlayerTeam lvt_3_1_ = (ScorePlayerTeam)var2.next();
         this.items.add(new TeleportToTeam.TeamSelectionObject(lvt_3_1_));
      }

   }

   public List<ISpectatorMenuObject> getItems() {
      return this.items;
   }

   public ITextComponent getPrompt() {
      return new TranslationTextComponent("spectatorMenu.team_teleport.prompt", new Object[0]);
   }

   public void selectItem(SpectatorMenu p_178661_1_) {
      p_178661_1_.selectCategory(this);
   }

   public ITextComponent getSpectatorName() {
      return new TranslationTextComponent("spectatorMenu.team_teleport", new Object[0]);
   }

   public void renderIcon(float p_178663_1_, int p_178663_2_) {
      Minecraft.getInstance().getTextureManager().bindTexture(SpectatorGui.SPECTATOR_WIDGETS);
      AbstractGui.blit(0, 0, 16.0F, 0.0F, 16, 16, 256, 256);
   }

   public boolean isEnabled() {
      Iterator var1 = this.items.iterator();

      ISpectatorMenuObject lvt_2_1_;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         lvt_2_1_ = (ISpectatorMenuObject)var1.next();
      } while(!lvt_2_1_.isEnabled());

      return true;
   }

   @OnlyIn(Dist.CLIENT)
   class TeamSelectionObject implements ISpectatorMenuObject {
      private final ScorePlayerTeam team;
      private final ResourceLocation location;
      private final List<NetworkPlayerInfo> players;

      public TeamSelectionObject(ScorePlayerTeam p_i45492_2_) {
         this.team = p_i45492_2_;
         this.players = Lists.newArrayList();
         Iterator var3 = p_i45492_2_.getMembershipCollection().iterator();

         while(var3.hasNext()) {
            String lvt_4_1_ = (String)var3.next();
            NetworkPlayerInfo lvt_5_1_ = Minecraft.getInstance().getConnection().getPlayerInfo(lvt_4_1_);
            if (lvt_5_1_ != null) {
               this.players.add(lvt_5_1_);
            }
         }

         if (this.players.isEmpty()) {
            this.location = DefaultPlayerSkin.getDefaultSkinLegacy();
         } else {
            String lvt_3_1_ = ((NetworkPlayerInfo)this.players.get((new Random()).nextInt(this.players.size()))).getGameProfile().getName();
            this.location = AbstractClientPlayerEntity.getLocationSkin(lvt_3_1_);
            AbstractClientPlayerEntity.getDownloadImageSkin(this.location, lvt_3_1_);
         }

      }

      public void selectItem(SpectatorMenu p_178661_1_) {
         p_178661_1_.selectCategory(new TeleportToPlayer(this.players));
      }

      public ITextComponent getSpectatorName() {
         return this.team.getDisplayName();
      }

      public void renderIcon(float p_178663_1_, int p_178663_2_) {
         Integer lvt_3_1_ = this.team.getColor().getColor();
         if (lvt_3_1_ != null) {
            float lvt_4_1_ = (float)(lvt_3_1_ >> 16 & 255) / 255.0F;
            float lvt_5_1_ = (float)(lvt_3_1_ >> 8 & 255) / 255.0F;
            float lvt_6_1_ = (float)(lvt_3_1_ & 255) / 255.0F;
            AbstractGui.fill(1, 1, 15, 15, MathHelper.rgb(lvt_4_1_ * p_178663_1_, lvt_5_1_ * p_178663_1_, lvt_6_1_ * p_178663_1_) | p_178663_2_ << 24);
         }

         Minecraft.getInstance().getTextureManager().bindTexture(this.location);
         RenderSystem.color4f(p_178663_1_, p_178663_1_, p_178663_1_, (float)p_178663_2_ / 255.0F);
         AbstractGui.blit(2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
         AbstractGui.blit(2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
      }

      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}
