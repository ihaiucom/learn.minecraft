package net.minecraft.network.play.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayerListItemPacket implements IPacket<IClientPlayNetHandler> {
   private SPlayerListItemPacket.Action action;
   private final List<SPlayerListItemPacket.AddPlayerData> players = Lists.newArrayList();

   public SPlayerListItemPacket() {
   }

   public SPlayerListItemPacket(SPlayerListItemPacket.Action p_i46929_1_, ServerPlayerEntity... p_i46929_2_) {
      this.action = p_i46929_1_;
      ServerPlayerEntity[] var3 = p_i46929_2_;
      int var4 = p_i46929_2_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ServerPlayerEntity lvt_6_1_ = var3[var5];
         this.players.add(new SPlayerListItemPacket.AddPlayerData(lvt_6_1_.getGameProfile(), lvt_6_1_.ping, lvt_6_1_.interactionManager.getGameType(), lvt_6_1_.getTabListDisplayName()));
      }

   }

   public SPlayerListItemPacket(SPlayerListItemPacket.Action p_i46930_1_, Iterable<ServerPlayerEntity> p_i46930_2_) {
      this.action = p_i46930_1_;
      Iterator var3 = p_i46930_2_.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity lvt_4_1_ = (ServerPlayerEntity)var3.next();
         this.players.add(new SPlayerListItemPacket.AddPlayerData(lvt_4_1_.getGameProfile(), lvt_4_1_.ping, lvt_4_1_.interactionManager.getGameType(), lvt_4_1_.getTabListDisplayName()));
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.action = (SPlayerListItemPacket.Action)p_148837_1_.readEnumValue(SPlayerListItemPacket.Action.class);
      int lvt_2_1_ = p_148837_1_.readVarInt();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         GameProfile lvt_4_1_ = null;
         int lvt_5_1_ = 0;
         GameType lvt_6_1_ = null;
         ITextComponent lvt_7_1_ = null;
         switch(this.action) {
         case ADD_PLAYER:
            lvt_4_1_ = new GameProfile(p_148837_1_.readUniqueId(), p_148837_1_.readString(16));
            int lvt_8_1_ = p_148837_1_.readVarInt();
            int lvt_9_1_ = 0;

            for(; lvt_9_1_ < lvt_8_1_; ++lvt_9_1_) {
               String lvt_10_1_ = p_148837_1_.readString(32767);
               String lvt_11_1_ = p_148837_1_.readString(32767);
               if (p_148837_1_.readBoolean()) {
                  lvt_4_1_.getProperties().put(lvt_10_1_, new Property(lvt_10_1_, lvt_11_1_, p_148837_1_.readString(32767)));
               } else {
                  lvt_4_1_.getProperties().put(lvt_10_1_, new Property(lvt_10_1_, lvt_11_1_));
               }
            }

            lvt_6_1_ = GameType.getByID(p_148837_1_.readVarInt());
            lvt_5_1_ = p_148837_1_.readVarInt();
            if (p_148837_1_.readBoolean()) {
               lvt_7_1_ = p_148837_1_.readTextComponent();
            }
            break;
         case UPDATE_GAME_MODE:
            lvt_4_1_ = new GameProfile(p_148837_1_.readUniqueId(), (String)null);
            lvt_6_1_ = GameType.getByID(p_148837_1_.readVarInt());
            break;
         case UPDATE_LATENCY:
            lvt_4_1_ = new GameProfile(p_148837_1_.readUniqueId(), (String)null);
            lvt_5_1_ = p_148837_1_.readVarInt();
            break;
         case UPDATE_DISPLAY_NAME:
            lvt_4_1_ = new GameProfile(p_148837_1_.readUniqueId(), (String)null);
            if (p_148837_1_.readBoolean()) {
               lvt_7_1_ = p_148837_1_.readTextComponent();
            }
            break;
         case REMOVE_PLAYER:
            lvt_4_1_ = new GameProfile(p_148837_1_.readUniqueId(), (String)null);
         }

         this.players.add(new SPlayerListItemPacket.AddPlayerData(lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.action);
      p_148840_1_.writeVarInt(this.players.size());
      Iterator var2 = this.players.iterator();

      while(true) {
         while(var2.hasNext()) {
            SPlayerListItemPacket.AddPlayerData lvt_3_1_ = (SPlayerListItemPacket.AddPlayerData)var2.next();
            switch(this.action) {
            case ADD_PLAYER:
               p_148840_1_.writeUniqueId(lvt_3_1_.getProfile().getId());
               p_148840_1_.writeString(lvt_3_1_.getProfile().getName());
               p_148840_1_.writeVarInt(lvt_3_1_.getProfile().getProperties().size());
               Iterator var4 = lvt_3_1_.getProfile().getProperties().values().iterator();

               while(var4.hasNext()) {
                  Property lvt_5_1_ = (Property)var4.next();
                  p_148840_1_.writeString(lvt_5_1_.getName());
                  p_148840_1_.writeString(lvt_5_1_.getValue());
                  if (lvt_5_1_.hasSignature()) {
                     p_148840_1_.writeBoolean(true);
                     p_148840_1_.writeString(lvt_5_1_.getSignature());
                  } else {
                     p_148840_1_.writeBoolean(false);
                  }
               }

               p_148840_1_.writeVarInt(lvt_3_1_.getGameMode().getID());
               p_148840_1_.writeVarInt(lvt_3_1_.getPing());
               if (lvt_3_1_.getDisplayName() == null) {
                  p_148840_1_.writeBoolean(false);
               } else {
                  p_148840_1_.writeBoolean(true);
                  p_148840_1_.writeTextComponent(lvt_3_1_.getDisplayName());
               }
               break;
            case UPDATE_GAME_MODE:
               p_148840_1_.writeUniqueId(lvt_3_1_.getProfile().getId());
               p_148840_1_.writeVarInt(lvt_3_1_.getGameMode().getID());
               break;
            case UPDATE_LATENCY:
               p_148840_1_.writeUniqueId(lvt_3_1_.getProfile().getId());
               p_148840_1_.writeVarInt(lvt_3_1_.getPing());
               break;
            case UPDATE_DISPLAY_NAME:
               p_148840_1_.writeUniqueId(lvt_3_1_.getProfile().getId());
               if (lvt_3_1_.getDisplayName() == null) {
                  p_148840_1_.writeBoolean(false);
               } else {
                  p_148840_1_.writeBoolean(true);
                  p_148840_1_.writeTextComponent(lvt_3_1_.getDisplayName());
               }
               break;
            case REMOVE_PLAYER:
               p_148840_1_.writeUniqueId(lvt_3_1_.getProfile().getId());
            }
         }

         return;
      }
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerListItem(this);
   }

   @OnlyIn(Dist.CLIENT)
   public List<SPlayerListItemPacket.AddPlayerData> getEntries() {
      return this.players;
   }

   @OnlyIn(Dist.CLIENT)
   public SPlayerListItemPacket.Action getAction() {
      return this.action;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.players).toString();
   }

   public class AddPlayerData {
      private final int ping;
      private final GameType gamemode;
      private final GameProfile profile;
      private final ITextComponent displayName;

      public AddPlayerData(GameProfile p_i46663_2_, int p_i46663_3_, @Nullable GameType p_i46663_4_, @Nullable ITextComponent p_i46663_5_) {
         this.profile = p_i46663_2_;
         this.ping = p_i46663_3_;
         this.gamemode = p_i46663_4_;
         this.displayName = p_i46663_5_;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getPing() {
         return this.ping;
      }

      public GameType getGameMode() {
         return this.gamemode;
      }

      @Nullable
      public ITextComponent getDisplayName() {
         return this.displayName;
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("latency", this.ping).add("gameMode", this.gamemode).add("profile", this.profile).add("displayName", this.displayName == null ? null : ITextComponent.Serializer.toJson(this.displayName)).toString();
      }
   }

   public static enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;
   }
}
