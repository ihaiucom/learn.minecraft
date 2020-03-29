package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CustomServerBossInfoManager {
   private final MinecraftServer server;
   private final Map<ResourceLocation, CustomServerBossInfo> bars = Maps.newHashMap();

   public CustomServerBossInfoManager(MinecraftServer p_i48619_1_) {
      this.server = p_i48619_1_;
   }

   @Nullable
   public CustomServerBossInfo get(ResourceLocation p_201384_1_) {
      return (CustomServerBossInfo)this.bars.get(p_201384_1_);
   }

   public CustomServerBossInfo add(ResourceLocation p_201379_1_, ITextComponent p_201379_2_) {
      CustomServerBossInfo lvt_3_1_ = new CustomServerBossInfo(p_201379_1_, p_201379_2_);
      this.bars.put(p_201379_1_, lvt_3_1_);
      return lvt_3_1_;
   }

   public void remove(CustomServerBossInfo p_201385_1_) {
      this.bars.remove(p_201385_1_.getId());
   }

   public Collection<ResourceLocation> getIDs() {
      return this.bars.keySet();
   }

   public Collection<CustomServerBossInfo> getBossbars() {
      return this.bars.values();
   }

   public CompoundNBT write() {
      CompoundNBT lvt_1_1_ = new CompoundNBT();
      Iterator var2 = this.bars.values().iterator();

      while(var2.hasNext()) {
         CustomServerBossInfo lvt_3_1_ = (CustomServerBossInfo)var2.next();
         lvt_1_1_.put(lvt_3_1_.getId().toString(), lvt_3_1_.write());
      }

      return lvt_1_1_;
   }

   public void read(CompoundNBT p_201381_1_) {
      Iterator var2 = p_201381_1_.keySet().iterator();

      while(var2.hasNext()) {
         String lvt_3_1_ = (String)var2.next();
         ResourceLocation lvt_4_1_ = new ResourceLocation(lvt_3_1_);
         this.bars.put(lvt_4_1_, CustomServerBossInfo.read(p_201381_1_.getCompound(lvt_3_1_), lvt_4_1_));
      }

   }

   public void onPlayerLogin(ServerPlayerEntity p_201383_1_) {
      Iterator var2 = this.bars.values().iterator();

      while(var2.hasNext()) {
         CustomServerBossInfo lvt_3_1_ = (CustomServerBossInfo)var2.next();
         lvt_3_1_.onPlayerLogin(p_201383_1_);
      }

   }

   public void onPlayerLogout(ServerPlayerEntity p_201382_1_) {
      Iterator var2 = this.bars.values().iterator();

      while(var2.hasNext()) {
         CustomServerBossInfo lvt_3_1_ = (CustomServerBossInfo)var2.next();
         lvt_3_1_.onPlayerLogout(p_201382_1_);
      }

   }
}
