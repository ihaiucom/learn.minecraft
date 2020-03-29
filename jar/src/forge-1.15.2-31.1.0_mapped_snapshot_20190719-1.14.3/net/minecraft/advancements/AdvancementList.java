package net.minecraft.advancements;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Advancement> advancements = Maps.newHashMap();
   private final Set<Advancement> roots = Sets.newLinkedHashSet();
   private final Set<Advancement> nonRoots = Sets.newLinkedHashSet();
   private AdvancementList.IListener listener;

   @OnlyIn(Dist.CLIENT)
   private void remove(Advancement p_192090_1_) {
      Iterator var2 = p_192090_1_.getChildren().iterator();

      while(var2.hasNext()) {
         Advancement lvt_3_1_ = (Advancement)var2.next();
         this.remove(lvt_3_1_);
      }

      LOGGER.info("Forgot about advancement {}", p_192090_1_.getId());
      this.advancements.remove(p_192090_1_.getId());
      if (p_192090_1_.getParent() == null) {
         this.roots.remove(p_192090_1_);
         if (this.listener != null) {
            this.listener.rootAdvancementRemoved(p_192090_1_);
         }
      } else {
         this.nonRoots.remove(p_192090_1_);
         if (this.listener != null) {
            this.listener.nonRootAdvancementRemoved(p_192090_1_);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void removeAll(Set<ResourceLocation> p_192085_1_) {
      Iterator var2 = p_192085_1_.iterator();

      while(var2.hasNext()) {
         ResourceLocation lvt_3_1_ = (ResourceLocation)var2.next();
         Advancement lvt_4_1_ = (Advancement)this.advancements.get(lvt_3_1_);
         if (lvt_4_1_ == null) {
            LOGGER.warn("Told to remove advancement {} but I don't know what that is", lvt_3_1_);
         } else {
            this.remove(lvt_4_1_);
         }
      }

   }

   public void loadAdvancements(Map<ResourceLocation, Advancement.Builder> p_192083_1_) {
      Function lvt_2_1_ = Functions.forMap(this.advancements, (Object)null);

      label42:
      while(!p_192083_1_.isEmpty()) {
         boolean lvt_3_1_ = false;
         Iterator lvt_4_1_ = p_192083_1_.entrySet().iterator();

         Entry lvt_5_2_;
         while(lvt_4_1_.hasNext()) {
            lvt_5_2_ = (Entry)lvt_4_1_.next();
            ResourceLocation lvt_6_1_ = (ResourceLocation)lvt_5_2_.getKey();
            Advancement.Builder lvt_7_1_ = (Advancement.Builder)lvt_5_2_.getValue();
            if (lvt_7_1_.resolveParent(lvt_2_1_)) {
               Advancement lvt_8_1_ = lvt_7_1_.build(lvt_6_1_);
               this.advancements.put(lvt_6_1_, lvt_8_1_);
               lvt_3_1_ = true;
               lvt_4_1_.remove();
               if (lvt_8_1_.getParent() == null) {
                  this.roots.add(lvt_8_1_);
                  if (this.listener != null) {
                     this.listener.rootAdvancementAdded(lvt_8_1_);
                  }
               } else {
                  this.nonRoots.add(lvt_8_1_);
                  if (this.listener != null) {
                     this.listener.nonRootAdvancementAdded(lvt_8_1_);
                  }
               }
            }
         }

         if (!lvt_3_1_) {
            lvt_4_1_ = p_192083_1_.entrySet().iterator();

            while(true) {
               if (!lvt_4_1_.hasNext()) {
                  break label42;
               }

               lvt_5_2_ = (Entry)lvt_4_1_.next();
               LOGGER.error("Couldn't load advancement {}: {}", lvt_5_2_.getKey(), lvt_5_2_.getValue());
            }
         }
      }

      LOGGER.info("Loaded {} advancements", this.advancements.size());
   }

   @OnlyIn(Dist.CLIENT)
   public void clear() {
      this.advancements.clear();
      this.roots.clear();
      this.nonRoots.clear();
      if (this.listener != null) {
         this.listener.advancementsCleared();
      }

   }

   public Iterable<Advancement> getRoots() {
      return this.roots;
   }

   public Collection<Advancement> getAll() {
      return this.advancements.values();
   }

   @Nullable
   public Advancement getAdvancement(ResourceLocation p_192084_1_) {
      return (Advancement)this.advancements.get(p_192084_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setListener(@Nullable AdvancementList.IListener p_192086_1_) {
      this.listener = p_192086_1_;
      if (p_192086_1_ != null) {
         Iterator var2 = this.roots.iterator();

         Advancement lvt_3_2_;
         while(var2.hasNext()) {
            lvt_3_2_ = (Advancement)var2.next();
            p_192086_1_.rootAdvancementAdded(lvt_3_2_);
         }

         var2 = this.nonRoots.iterator();

         while(var2.hasNext()) {
            lvt_3_2_ = (Advancement)var2.next();
            p_192086_1_.nonRootAdvancementAdded(lvt_3_2_);
         }
      }

   }

   public interface IListener {
      void rootAdvancementAdded(Advancement var1);

      @OnlyIn(Dist.CLIENT)
      void rootAdvancementRemoved(Advancement var1);

      void nonRootAdvancementAdded(Advancement var1);

      @OnlyIn(Dist.CLIENT)
      void nonRootAdvancementRemoved(Advancement var1);

      @OnlyIn(Dist.CLIENT)
      void advancementsCleared();
   }
}
