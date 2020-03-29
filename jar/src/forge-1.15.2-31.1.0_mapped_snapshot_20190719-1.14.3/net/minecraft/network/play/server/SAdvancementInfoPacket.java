package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SAdvancementInfoPacket implements IPacket<IClientPlayNetHandler> {
   private boolean firstSync;
   private Map<ResourceLocation, Advancement.Builder> advancementsToAdd;
   private Set<ResourceLocation> advancementsToRemove;
   private Map<ResourceLocation, AdvancementProgress> progressUpdates;

   public SAdvancementInfoPacket() {
   }

   public SAdvancementInfoPacket(boolean p_i47519_1_, Collection<Advancement> p_i47519_2_, Set<ResourceLocation> p_i47519_3_, Map<ResourceLocation, AdvancementProgress> p_i47519_4_) {
      this.firstSync = p_i47519_1_;
      this.advancementsToAdd = Maps.newHashMap();
      Iterator var5 = p_i47519_2_.iterator();

      while(var5.hasNext()) {
         Advancement lvt_6_1_ = (Advancement)var5.next();
         this.advancementsToAdd.put(lvt_6_1_.getId(), lvt_6_1_.copy());
      }

      this.advancementsToRemove = p_i47519_3_;
      this.progressUpdates = Maps.newHashMap(p_i47519_4_);
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAdvancementInfo(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.firstSync = p_148837_1_.readBoolean();
      this.advancementsToAdd = Maps.newHashMap();
      this.advancementsToRemove = Sets.newLinkedHashSet();
      this.progressUpdates = Maps.newHashMap();
      int lvt_2_1_ = p_148837_1_.readVarInt();

      int lvt_3_3_;
      ResourceLocation lvt_4_3_;
      for(lvt_3_3_ = 0; lvt_3_3_ < lvt_2_1_; ++lvt_3_3_) {
         lvt_4_3_ = p_148837_1_.readResourceLocation();
         Advancement.Builder lvt_5_1_ = Advancement.Builder.readFrom(p_148837_1_);
         this.advancementsToAdd.put(lvt_4_3_, lvt_5_1_);
      }

      lvt_2_1_ = p_148837_1_.readVarInt();

      for(lvt_3_3_ = 0; lvt_3_3_ < lvt_2_1_; ++lvt_3_3_) {
         lvt_4_3_ = p_148837_1_.readResourceLocation();
         this.advancementsToRemove.add(lvt_4_3_);
      }

      lvt_2_1_ = p_148837_1_.readVarInt();

      for(lvt_3_3_ = 0; lvt_3_3_ < lvt_2_1_; ++lvt_3_3_) {
         lvt_4_3_ = p_148837_1_.readResourceLocation();
         this.progressUpdates.put(lvt_4_3_, AdvancementProgress.fromNetwork(p_148837_1_));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBoolean(this.firstSync);
      p_148840_1_.writeVarInt(this.advancementsToAdd.size());
      Iterator var2 = this.advancementsToAdd.entrySet().iterator();

      Entry lvt_3_3_;
      while(var2.hasNext()) {
         lvt_3_3_ = (Entry)var2.next();
         ResourceLocation lvt_4_1_ = (ResourceLocation)lvt_3_3_.getKey();
         Advancement.Builder lvt_5_1_ = (Advancement.Builder)lvt_3_3_.getValue();
         p_148840_1_.writeResourceLocation(lvt_4_1_);
         lvt_5_1_.writeTo(p_148840_1_);
      }

      p_148840_1_.writeVarInt(this.advancementsToRemove.size());
      var2 = this.advancementsToRemove.iterator();

      while(var2.hasNext()) {
         ResourceLocation lvt_3_2_ = (ResourceLocation)var2.next();
         p_148840_1_.writeResourceLocation(lvt_3_2_);
      }

      p_148840_1_.writeVarInt(this.progressUpdates.size());
      var2 = this.progressUpdates.entrySet().iterator();

      while(var2.hasNext()) {
         lvt_3_3_ = (Entry)var2.next();
         p_148840_1_.writeResourceLocation((ResourceLocation)lvt_3_3_.getKey());
         ((AdvancementProgress)lvt_3_3_.getValue()).serializeToNetwork(p_148840_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Map<ResourceLocation, Advancement.Builder> getAdvancementsToAdd() {
      return this.advancementsToAdd;
   }

   @OnlyIn(Dist.CLIENT)
   public Set<ResourceLocation> getAdvancementsToRemove() {
      return this.advancementsToRemove;
   }

   @OnlyIn(Dist.CLIENT)
   public Map<ResourceLocation, AdvancementProgress> getProgressUpdates() {
      return this.progressUpdates;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isFirstSync() {
      return this.firstSync;
   }
}
