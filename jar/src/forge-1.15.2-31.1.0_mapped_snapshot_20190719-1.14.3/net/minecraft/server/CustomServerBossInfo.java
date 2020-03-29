package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.server.ServerBossInfo;

public class CustomServerBossInfo extends ServerBossInfo {
   private final ResourceLocation id;
   private final Set<UUID> players = Sets.newHashSet();
   private int value;
   private int max = 100;

   public CustomServerBossInfo(ResourceLocation p_i48620_1_, ITextComponent p_i48620_2_) {
      super(p_i48620_2_, BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);
      this.id = p_i48620_1_;
      this.setPercent(0.0F);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public void addPlayer(ServerPlayerEntity p_186760_1_) {
      super.addPlayer(p_186760_1_);
      this.players.add(p_186760_1_.getUniqueID());
   }

   public void addPlayer(UUID p_201372_1_) {
      this.players.add(p_201372_1_);
   }

   public void removePlayer(ServerPlayerEntity p_186761_1_) {
      super.removePlayer(p_186761_1_);
      this.players.remove(p_186761_1_.getUniqueID());
   }

   public void removeAllPlayers() {
      super.removeAllPlayers();
      this.players.clear();
   }

   public int getValue() {
      return this.value;
   }

   public int getMax() {
      return this.max;
   }

   public void setValue(int p_201362_1_) {
      this.value = p_201362_1_;
      this.setPercent(MathHelper.clamp((float)p_201362_1_ / (float)this.max, 0.0F, 1.0F));
   }

   public void setMax(int p_201366_1_) {
      this.max = p_201366_1_;
      this.setPercent(MathHelper.clamp((float)this.value / (float)p_201366_1_, 0.0F, 1.0F));
   }

   public final ITextComponent getFormattedName() {
      return TextComponentUtils.wrapInSquareBrackets(this.getName()).applyTextStyle((p_211569_1_) -> {
         p_211569_1_.setColor(this.getColor().getFormatting()).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.getId().toString()))).setInsertion(this.getId().toString());
      });
   }

   public boolean setPlayers(Collection<ServerPlayerEntity> p_201368_1_) {
      Set<UUID> lvt_2_1_ = Sets.newHashSet();
      Set<ServerPlayerEntity> lvt_3_1_ = Sets.newHashSet();
      Iterator var4 = this.players.iterator();

      UUID lvt_5_3_;
      boolean lvt_6_2_;
      Iterator var7;
      while(var4.hasNext()) {
         lvt_5_3_ = (UUID)var4.next();
         lvt_6_2_ = false;
         var7 = p_201368_1_.iterator();

         while(var7.hasNext()) {
            ServerPlayerEntity lvt_8_1_ = (ServerPlayerEntity)var7.next();
            if (lvt_8_1_.getUniqueID().equals(lvt_5_3_)) {
               lvt_6_2_ = true;
               break;
            }
         }

         if (!lvt_6_2_) {
            lvt_2_1_.add(lvt_5_3_);
         }
      }

      var4 = p_201368_1_.iterator();

      ServerPlayerEntity lvt_5_4_;
      while(var4.hasNext()) {
         lvt_5_4_ = (ServerPlayerEntity)var4.next();
         lvt_6_2_ = false;
         var7 = this.players.iterator();

         while(var7.hasNext()) {
            UUID lvt_8_2_ = (UUID)var7.next();
            if (lvt_5_4_.getUniqueID().equals(lvt_8_2_)) {
               lvt_6_2_ = true;
               break;
            }
         }

         if (!lvt_6_2_) {
            lvt_3_1_.add(lvt_5_4_);
         }
      }

      for(var4 = lvt_2_1_.iterator(); var4.hasNext(); this.players.remove(lvt_5_3_)) {
         lvt_5_3_ = (UUID)var4.next();
         Iterator var11 = this.getPlayers().iterator();

         while(var11.hasNext()) {
            ServerPlayerEntity lvt_7_1_ = (ServerPlayerEntity)var11.next();
            if (lvt_7_1_.getUniqueID().equals(lvt_5_3_)) {
               this.removePlayer(lvt_7_1_);
               break;
            }
         }
      }

      var4 = lvt_3_1_.iterator();

      while(var4.hasNext()) {
         lvt_5_4_ = (ServerPlayerEntity)var4.next();
         this.addPlayer(lvt_5_4_);
      }

      return !lvt_2_1_.isEmpty() || !lvt_3_1_.isEmpty();
   }

   public CompoundNBT write() {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      lvt_1_1_.putString("Name", ITextComponent.Serializer.toJson(this.name));
      lvt_1_1_.putBoolean("Visible", this.isVisible());
      lvt_1_1_.putInt("Value", this.value);
      lvt_1_1_.putInt("Max", this.max);
      lvt_1_1_.putString("Color", this.getColor().getName());
      lvt_1_1_.putString("Overlay", this.getOverlay().getName());
      lvt_1_1_.putBoolean("DarkenScreen", this.shouldDarkenSky());
      lvt_1_1_.putBoolean("PlayBossMusic", this.shouldPlayEndBossMusic());
      lvt_1_1_.putBoolean("CreateWorldFog", this.shouldCreateFog());
      ListNBT lvt_2_1_ = new ListNBT();
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         UUID lvt_4_1_ = (UUID)var3.next();
         lvt_2_1_.add(NBTUtil.writeUniqueId(lvt_4_1_));
      }

      lvt_1_1_.put("Players", lvt_2_1_);
      return lvt_1_1_;
   }

   public static CustomServerBossInfo read(CompoundNBT p_201371_0_, ResourceLocation p_201371_1_) {
      CustomServerBossInfo lvt_2_1_ = new CustomServerBossInfo(p_201371_1_, ITextComponent.Serializer.fromJson(p_201371_0_.getString("Name")));
      lvt_2_1_.setVisible(p_201371_0_.getBoolean("Visible"));
      lvt_2_1_.setValue(p_201371_0_.getInt("Value"));
      lvt_2_1_.setMax(p_201371_0_.getInt("Max"));
      lvt_2_1_.setColor(BossInfo.Color.byName(p_201371_0_.getString("Color")));
      lvt_2_1_.setOverlay(BossInfo.Overlay.byName(p_201371_0_.getString("Overlay")));
      lvt_2_1_.setDarkenSky(p_201371_0_.getBoolean("DarkenScreen"));
      lvt_2_1_.setPlayEndBossMusic(p_201371_0_.getBoolean("PlayBossMusic"));
      lvt_2_1_.setCreateFog(p_201371_0_.getBoolean("CreateWorldFog"));
      ListNBT lvt_3_1_ = p_201371_0_.getList("Players", 10);

      for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.size(); ++lvt_4_1_) {
         lvt_2_1_.addPlayer(NBTUtil.readUniqueId(lvt_3_1_.getCompound(lvt_4_1_)));
      }

      return lvt_2_1_;
   }

   public void onPlayerLogin(ServerPlayerEntity p_201361_1_) {
      if (this.players.contains(p_201361_1_.getUniqueID())) {
         this.addPlayer(p_201361_1_);
      }

   }

   public void onPlayerLogout(ServerPlayerEntity p_201363_1_) {
      super.removePlayer(p_201363_1_);
   }
}
