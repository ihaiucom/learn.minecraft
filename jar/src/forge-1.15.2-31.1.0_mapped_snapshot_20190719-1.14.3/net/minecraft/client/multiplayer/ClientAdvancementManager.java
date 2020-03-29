package net.minecraft.client.multiplayer;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientAdvancementManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final AdvancementList advancementList = new AdvancementList();
   private final Map<Advancement, AdvancementProgress> advancementToProgress = Maps.newHashMap();
   @Nullable
   private ClientAdvancementManager.IListener listener;
   @Nullable
   private Advancement selectedTab;

   public ClientAdvancementManager(Minecraft p_i47380_1_) {
      this.mc = p_i47380_1_;
   }

   public void read(SAdvancementInfoPacket p_192799_1_) {
      if (p_192799_1_.isFirstSync()) {
         this.advancementList.clear();
         this.advancementToProgress.clear();
      }

      this.advancementList.removeAll(p_192799_1_.getAdvancementsToRemove());
      this.advancementList.loadAdvancements(p_192799_1_.getAdvancementsToAdd());
      Iterator var2 = p_192799_1_.getProgressUpdates().entrySet().iterator();

      while(var2.hasNext()) {
         Entry<ResourceLocation, AdvancementProgress> lvt_3_1_ = (Entry)var2.next();
         Advancement lvt_4_1_ = this.advancementList.getAdvancement((ResourceLocation)lvt_3_1_.getKey());
         if (lvt_4_1_ != null) {
            AdvancementProgress lvt_5_1_ = (AdvancementProgress)lvt_3_1_.getValue();
            lvt_5_1_.update(lvt_4_1_.getCriteria(), lvt_4_1_.getRequirements());
            this.advancementToProgress.put(lvt_4_1_, lvt_5_1_);
            if (this.listener != null) {
               this.listener.onUpdateAdvancementProgress(lvt_4_1_, lvt_5_1_);
            }

            if (!p_192799_1_.isFirstSync() && lvt_5_1_.isDone() && lvt_4_1_.getDisplay() != null && lvt_4_1_.getDisplay().shouldShowToast()) {
               this.mc.getToastGui().add(new AdvancementToast(lvt_4_1_));
            }
         } else {
            LOGGER.warn("Server informed client about progress for unknown advancement {}", lvt_3_1_.getKey());
         }
      }

   }

   public AdvancementList getAdvancementList() {
      return this.advancementList;
   }

   public void setSelectedTab(@Nullable Advancement p_194230_1_, boolean p_194230_2_) {
      ClientPlayNetHandler lvt_3_1_ = this.mc.getConnection();
      if (lvt_3_1_ != null && p_194230_1_ != null && p_194230_2_) {
         lvt_3_1_.sendPacket(CSeenAdvancementsPacket.openedTab(p_194230_1_));
      }

      if (this.selectedTab != p_194230_1_) {
         this.selectedTab = p_194230_1_;
         if (this.listener != null) {
            this.listener.setSelectedTab(p_194230_1_);
         }
      }

   }

   public void setListener(@Nullable ClientAdvancementManager.IListener p_192798_1_) {
      this.listener = p_192798_1_;
      this.advancementList.setListener(p_192798_1_);
      if (p_192798_1_ != null) {
         Iterator var2 = this.advancementToProgress.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<Advancement, AdvancementProgress> lvt_3_1_ = (Entry)var2.next();
            p_192798_1_.onUpdateAdvancementProgress((Advancement)lvt_3_1_.getKey(), (AdvancementProgress)lvt_3_1_.getValue());
         }

         p_192798_1_.setSelectedTab(this.selectedTab);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public interface IListener extends AdvancementList.IListener {
      void onUpdateAdvancementProgress(Advancement var1, AdvancementProgress var2);

      void setSelectedTab(@Nullable Advancement var1);
   }
}
