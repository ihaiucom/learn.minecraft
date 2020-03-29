package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerMenuObject implements ISpectatorMenuObject {
   private final GameProfile profile;
   private final ResourceLocation resourceLocation;

   public PlayerMenuObject(GameProfile p_i45498_1_) {
      this.profile = p_i45498_1_;
      Minecraft lvt_2_1_ = Minecraft.getInstance();
      Map<Type, MinecraftProfileTexture> lvt_3_1_ = lvt_2_1_.getSkinManager().loadSkinFromCache(p_i45498_1_);
      if (lvt_3_1_.containsKey(Type.SKIN)) {
         this.resourceLocation = lvt_2_1_.getSkinManager().loadSkin((MinecraftProfileTexture)lvt_3_1_.get(Type.SKIN), Type.SKIN);
      } else {
         this.resourceLocation = DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(p_i45498_1_));
      }

   }

   public void selectItem(SpectatorMenu p_178661_1_) {
      Minecraft.getInstance().getConnection().sendPacket(new CSpectatePacket(this.profile.getId()));
   }

   public ITextComponent getSpectatorName() {
      return new StringTextComponent(this.profile.getName());
   }

   public void renderIcon(float p_178663_1_, int p_178663_2_) {
      Minecraft.getInstance().getTextureManager().bindTexture(this.resourceLocation);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, (float)p_178663_2_ / 255.0F);
      AbstractGui.blit(2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
      AbstractGui.blit(2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
   }

   public boolean isEnabled() {
      return true;
   }
}
