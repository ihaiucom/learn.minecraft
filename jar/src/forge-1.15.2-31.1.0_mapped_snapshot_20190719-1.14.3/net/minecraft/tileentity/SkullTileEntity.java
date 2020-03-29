package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.StringUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkullTileEntity extends TileEntity implements ITickableTileEntity {
   private GameProfile playerProfile;
   private int dragonAnimatedTicks;
   private boolean dragonAnimated;
   private static PlayerProfileCache profileCache;
   private static MinecraftSessionService sessionService;

   public SkullTileEntity() {
      super(TileEntityType.SKULL);
   }

   public static void setProfileCache(PlayerProfileCache p_184293_0_) {
      profileCache = p_184293_0_;
   }

   public static void setSessionService(MinecraftSessionService p_184294_0_) {
      sessionService = p_184294_0_;
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (this.playerProfile != null) {
         CompoundNBT lvt_2_1_ = new CompoundNBT();
         NBTUtil.writeGameProfile(lvt_2_1_, this.playerProfile);
         p_189515_1_.put("Owner", lvt_2_1_);
      }

      return p_189515_1_;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      if (p_145839_1_.contains("Owner", 10)) {
         this.setPlayerProfile(NBTUtil.readGameProfile(p_145839_1_.getCompound("Owner")));
      } else if (p_145839_1_.contains("ExtraType", 8)) {
         String lvt_2_1_ = p_145839_1_.getString("ExtraType");
         if (!StringUtils.isNullOrEmpty(lvt_2_1_)) {
            this.setPlayerProfile(new GameProfile((UUID)null, lvt_2_1_));
         }
      }

   }

   public void tick() {
      Block lvt_1_1_ = this.getBlockState().getBlock();
      if (lvt_1_1_ == Blocks.DRAGON_HEAD || lvt_1_1_ == Blocks.DRAGON_WALL_HEAD) {
         if (this.world.isBlockPowered(this.pos)) {
            this.dragonAnimated = true;
            ++this.dragonAnimatedTicks;
         } else {
            this.dragonAnimated = false;
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getAnimationProgress(float p_184295_1_) {
      return this.dragonAnimated ? (float)this.dragonAnimatedTicks + p_184295_1_ : (float)this.dragonAnimatedTicks;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public GameProfile getPlayerProfile() {
      return this.playerProfile;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 4, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   public void setPlayerProfile(@Nullable GameProfile p_195485_1_) {
      this.playerProfile = p_195485_1_;
      this.updatePlayerProfile();
   }

   private void updatePlayerProfile() {
      this.playerProfile = updateGameProfile(this.playerProfile);
      this.markDirty();
   }

   public static GameProfile updateGameProfile(GameProfile p_174884_0_) {
      if (p_174884_0_ != null && !StringUtils.isNullOrEmpty(p_174884_0_.getName())) {
         if (p_174884_0_.isComplete() && p_174884_0_.getProperties().containsKey("textures")) {
            return p_174884_0_;
         } else if (profileCache != null && sessionService != null) {
            GameProfile lvt_1_1_ = profileCache.getGameProfileForUsername(p_174884_0_.getName());
            if (lvt_1_1_ == null) {
               return p_174884_0_;
            } else {
               Property lvt_2_1_ = (Property)Iterables.getFirst(lvt_1_1_.getProperties().get("textures"), (Object)null);
               if (lvt_2_1_ == null) {
                  lvt_1_1_ = sessionService.fillProfileProperties(lvt_1_1_, true);
               }

               return lvt_1_1_;
            }
         } else {
            return p_174884_0_;
         }
      } else {
         return p_174884_0_;
      }
   }
}
