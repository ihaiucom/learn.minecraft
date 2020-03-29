package net.minecraft.resources;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ResourcePackList<T extends ResourcePackInfo> implements AutoCloseable {
   private final Set<IPackFinder> packFinders = Sets.newHashSet();
   private final Map<String, T> packNameToInfo = Maps.newLinkedHashMap();
   private final List<T> enabled = Lists.newLinkedList();
   private final ResourcePackInfo.IFactory<T> packInfoFactory;

   public ResourcePackList(ResourcePackInfo.IFactory<T> p_i47909_1_) {
      this.packInfoFactory = p_i47909_1_;
   }

   public void reloadPacksFromFinders() {
      this.close();
      Set<String> lvt_1_1_ = (Set)this.enabled.stream().map(ResourcePackInfo::getName).collect(Collectors.toCollection(LinkedHashSet::new));
      this.packNameToInfo.clear();
      this.enabled.clear();
      Iterator var2 = this.packFinders.iterator();

      while(var2.hasNext()) {
         IPackFinder lvt_3_1_ = (IPackFinder)var2.next();
         lvt_3_1_.addPackInfosToMap(this.packNameToInfo, this.packInfoFactory);
      }

      this.sortPackNameToInfo();
      List var10000 = this.enabled;
      Stream var10001 = lvt_1_1_.stream();
      Map var10002 = this.packNameToInfo;
      var10002.getClass();
      var10000.addAll((Collection)var10001.map(var10002::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));
      var2 = this.packNameToInfo.values().iterator();

      while(var2.hasNext()) {
         T lvt_3_2_ = (ResourcePackInfo)var2.next();
         if (lvt_3_2_.isAlwaysEnabled() && !this.enabled.contains(lvt_3_2_)) {
            lvt_3_2_.getPriority().func_198993_a(this.enabled, lvt_3_2_, Functions.identity(), false);
         }
      }

   }

   private void sortPackNameToInfo() {
      List<Entry<String, T>> lvt_1_1_ = Lists.newArrayList(this.packNameToInfo.entrySet());
      this.packNameToInfo.clear();
      lvt_1_1_.stream().sorted(Entry.comparingByKey()).forEachOrdered((p_198984_1_) -> {
         ResourcePackInfo var10000 = (ResourcePackInfo)this.packNameToInfo.put(p_198984_1_.getKey(), p_198984_1_.getValue());
      });
   }

   public void setEnabledPacks(Collection<T> p_198985_1_) {
      this.enabled.clear();
      this.enabled.addAll(p_198985_1_);
      Iterator var2 = this.packNameToInfo.values().iterator();

      while(var2.hasNext()) {
         T lvt_3_1_ = (ResourcePackInfo)var2.next();
         if (lvt_3_1_.isAlwaysEnabled() && !this.enabled.contains(lvt_3_1_)) {
            lvt_3_1_.getPriority().func_198993_a(this.enabled, lvt_3_1_, Functions.identity(), false);
         }
      }

   }

   public Collection<T> getAllPacks() {
      return this.packNameToInfo.values();
   }

   public Collection<T> getAvailablePacks() {
      Collection<T> lvt_1_1_ = Lists.newArrayList(this.packNameToInfo.values());
      lvt_1_1_.removeAll(this.enabled);
      return lvt_1_1_;
   }

   public Collection<T> getEnabledPacks() {
      return this.enabled;
   }

   @Nullable
   public T getPackInfo(String p_198981_1_) {
      return (ResourcePackInfo)this.packNameToInfo.get(p_198981_1_);
   }

   public void addPackFinder(IPackFinder p_198982_1_) {
      this.packFinders.add(p_198982_1_);
   }

   public void close() {
      this.packNameToInfo.values().forEach(ResourcePackInfo::close);
   }
}
