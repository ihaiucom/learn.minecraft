package net.minecraft.client.entity.player;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity {
   private NetworkPlayerInfo playerInfo;
   public float rotateElytraX;
   public float rotateElytraY;
   public float rotateElytraZ;
   public final ClientWorld field_213837_d;

   public AbstractClientPlayerEntity(ClientWorld p_i50991_1_, GameProfile p_i50991_2_) {
      super(p_i50991_1_, p_i50991_2_);
      this.field_213837_d = p_i50991_1_;
   }

   public boolean isSpectator() {
      NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
      return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.CREATIVE;
   }

   public boolean hasPlayerInfo() {
      return this.getPlayerInfo() != null;
   }

   @Nullable
   protected NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUniqueID());
      }

      return this.playerInfo;
   }

   public boolean hasSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
   }

   public ResourceLocation getLocationSkin() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
   }

   @Nullable
   public ResourceLocation getLocationCape() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
   }

   public boolean isPlayerInfoSet() {
      return this.getPlayerInfo() != null;
   }

   @Nullable
   public ResourceLocation getLocationElytra() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationElytra();
   }

   public static DownloadingTexture getDownloadImageSkin(ResourceLocation p_110304_0_, String p_110304_1_) {
      TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
      Texture texture = texturemanager.func_229267_b_(p_110304_0_);
      if (texture == null) {
         texture = new DownloadingTexture((File)null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(p_110304_1_)), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(p_110304_1_)), true, (Runnable)null);
         texturemanager.func_229263_a_(p_110304_0_, (Texture)texture);
      }

      return (DownloadingTexture)texture;
   }

   public static ResourceLocation getLocationSkin(String p_110311_0_) {
      return new ResourceLocation("skins/" + Hashing.sha1().hashUnencodedChars(StringUtils.stripControlCodes(p_110311_0_)));
   }

   public String getSkinType() {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
   }

   public float getFovModifier() {
      float f = 1.0F;
      if (this.abilities.isFlying) {
         f *= 1.1F;
      }

      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      f = (float)((double)f * ((iattributeinstance.getValue() / (double)this.abilities.getWalkSpeed() + 1.0D) / 2.0D));
      if (this.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
         f = 1.0F;
      }

      if (this.isHandActive() && this.getActiveItemStack().getItem() instanceof BowItem) {
         int i = this.getItemInUseMaxCount();
         float f1 = (float)i / 20.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F;
         } else {
            f1 *= f1;
         }

         f *= 1.0F - f1 * 0.15F;
      }

      return ForgeHooksClient.getOffsetFOV(this, f);
   }
}
