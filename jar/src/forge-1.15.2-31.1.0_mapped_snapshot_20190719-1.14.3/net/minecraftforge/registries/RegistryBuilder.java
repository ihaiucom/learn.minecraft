package net.minecraftforge.registries;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class RegistryBuilder<T extends IForgeRegistryEntry<T>> {
   private static final int MAX_ID = 2147483646;
   private ResourceLocation registryName;
   private Class<T> registryType;
   private ResourceLocation optionalDefaultKey;
   private int minId = 0;
   private int maxId = 2147483646;
   private List<IForgeRegistry.AddCallback<T>> addCallback = Lists.newArrayList();
   private List<IForgeRegistry.ClearCallback<T>> clearCallback = Lists.newArrayList();
   private List<IForgeRegistry.CreateCallback<T>> createCallback = Lists.newArrayList();
   private List<IForgeRegistry.ValidateCallback<T>> validateCallback = Lists.newArrayList();
   private List<IForgeRegistry.BakeCallback<T>> bakeCallback = Lists.newArrayList();
   private boolean saveToDisc = true;
   private boolean sync = true;
   private boolean allowOverrides = true;
   private boolean allowModifications = false;
   private IForgeRegistry.DummyFactory<T> dummyFactory;
   private IForgeRegistry.MissingFactory<T> missingFactory;
   private Set<ResourceLocation> legacyNames = new HashSet();

   public RegistryBuilder<T> setName(ResourceLocation name) {
      this.registryName = name;
      return this;
   }

   public RegistryBuilder<T> setType(Class<T> type) {
      this.registryType = type;
      return this;
   }

   public RegistryBuilder<T> setIDRange(int min, int max) {
      this.minId = Math.max(min, 0);
      this.maxId = Math.min(max, 2147483646);
      return this;
   }

   public RegistryBuilder<T> setMaxID(int max) {
      return this.setIDRange(0, max);
   }

   public RegistryBuilder<T> setDefaultKey(ResourceLocation key) {
      this.optionalDefaultKey = key;
      return this;
   }

   public RegistryBuilder<T> addCallback(Object inst) {
      if (inst instanceof IForgeRegistry.AddCallback) {
         this.add((IForgeRegistry.AddCallback)inst);
      }

      if (inst instanceof IForgeRegistry.ClearCallback) {
         this.add((IForgeRegistry.ClearCallback)inst);
      }

      if (inst instanceof IForgeRegistry.CreateCallback) {
         this.add((IForgeRegistry.CreateCallback)inst);
      }

      if (inst instanceof IForgeRegistry.ValidateCallback) {
         this.add((IForgeRegistry.ValidateCallback)inst);
      }

      if (inst instanceof IForgeRegistry.BakeCallback) {
         this.add((IForgeRegistry.BakeCallback)inst);
      }

      if (inst instanceof IForgeRegistry.DummyFactory) {
         this.set((IForgeRegistry.DummyFactory)inst);
      }

      if (inst instanceof IForgeRegistry.MissingFactory) {
         this.set((IForgeRegistry.MissingFactory)inst);
      }

      return this;
   }

   public RegistryBuilder<T> add(IForgeRegistry.AddCallback<T> add) {
      this.addCallback.add(add);
      return this;
   }

   public RegistryBuilder<T> add(IForgeRegistry.ClearCallback<T> clear) {
      this.clearCallback.add(clear);
      return this;
   }

   public RegistryBuilder<T> add(IForgeRegistry.CreateCallback<T> create) {
      this.createCallback.add(create);
      return this;
   }

   public RegistryBuilder<T> add(IForgeRegistry.ValidateCallback<T> validate) {
      this.validateCallback.add(validate);
      return this;
   }

   public RegistryBuilder<T> add(IForgeRegistry.BakeCallback<T> bake) {
      this.bakeCallback.add(bake);
      return this;
   }

   public RegistryBuilder<T> set(IForgeRegistry.DummyFactory<T> factory) {
      this.dummyFactory = factory;
      return this;
   }

   public RegistryBuilder<T> set(IForgeRegistry.MissingFactory<T> missing) {
      this.missingFactory = missing;
      return this;
   }

   public RegistryBuilder<T> disableSaving() {
      this.saveToDisc = false;
      return this;
   }

   public RegistryBuilder<T> disableSync() {
      this.sync = false;
      return this;
   }

   public RegistryBuilder<T> disableOverrides() {
      this.allowOverrides = false;
      return this;
   }

   public RegistryBuilder<T> allowModification() {
      this.allowModifications = true;
      return this;
   }

   public RegistryBuilder<T> legacyName(String name) {
      return this.legacyName(new ResourceLocation(name));
   }

   public RegistryBuilder<T> legacyName(ResourceLocation name) {
      this.legacyNames.add(name);
      return this;
   }

   public IForgeRegistry<T> create() {
      return RegistryManager.ACTIVE.createRegistry(this.registryName, this);
   }

   @Nullable
   public IForgeRegistry.AddCallback<T> getAdd() {
      if (this.addCallback.isEmpty()) {
         return null;
      } else {
         return this.addCallback.size() == 1 ? (IForgeRegistry.AddCallback)this.addCallback.get(0) : (owner, stage, id, obj, old) -> {
            Iterator var6 = this.addCallback.iterator();

            while(var6.hasNext()) {
               IForgeRegistry.AddCallback<T> cb = (IForgeRegistry.AddCallback)var6.next();
               cb.onAdd(owner, stage, id, obj, old);
            }

         };
      }
   }

   @Nullable
   public IForgeRegistry.ClearCallback<T> getClear() {
      if (this.clearCallback.isEmpty()) {
         return null;
      } else {
         return this.clearCallback.size() == 1 ? (IForgeRegistry.ClearCallback)this.clearCallback.get(0) : (owner, stage) -> {
            Iterator var3 = this.clearCallback.iterator();

            while(var3.hasNext()) {
               IForgeRegistry.ClearCallback<T> cb = (IForgeRegistry.ClearCallback)var3.next();
               cb.onClear(owner, stage);
            }

         };
      }
   }

   @Nullable
   public IForgeRegistry.CreateCallback<T> getCreate() {
      if (this.createCallback.isEmpty()) {
         return null;
      } else {
         return this.createCallback.size() == 1 ? (IForgeRegistry.CreateCallback)this.createCallback.get(0) : (owner, stage) -> {
            Iterator var3 = this.createCallback.iterator();

            while(var3.hasNext()) {
               IForgeRegistry.CreateCallback<T> cb = (IForgeRegistry.CreateCallback)var3.next();
               cb.onCreate(owner, stage);
            }

         };
      }
   }

   @Nullable
   public IForgeRegistry.ValidateCallback<T> getValidate() {
      if (this.validateCallback.isEmpty()) {
         return null;
      } else {
         return this.validateCallback.size() == 1 ? (IForgeRegistry.ValidateCallback)this.validateCallback.get(0) : (owner, stage, id, key, obj) -> {
            Iterator var6 = this.validateCallback.iterator();

            while(var6.hasNext()) {
               IForgeRegistry.ValidateCallback<T> cb = (IForgeRegistry.ValidateCallback)var6.next();
               cb.onValidate(owner, stage, id, key, obj);
            }

         };
      }
   }

   @Nullable
   public IForgeRegistry.BakeCallback<T> getBake() {
      if (this.bakeCallback.isEmpty()) {
         return null;
      } else {
         return this.bakeCallback.size() == 1 ? (IForgeRegistry.BakeCallback)this.bakeCallback.get(0) : (owner, stage) -> {
            Iterator var3 = this.bakeCallback.iterator();

            while(var3.hasNext()) {
               IForgeRegistry.BakeCallback<T> cb = (IForgeRegistry.BakeCallback)var3.next();
               cb.onBake(owner, stage);
            }

         };
      }
   }

   public Class<T> getType() {
      return this.registryType;
   }

   @Nullable
   public ResourceLocation getDefault() {
      return this.optionalDefaultKey;
   }

   public int getMinId() {
      return this.minId;
   }

   public int getMaxId() {
      return this.maxId;
   }

   public boolean getAllowOverrides() {
      return this.allowOverrides;
   }

   public boolean getAllowModifications() {
      return this.allowModifications;
   }

   @Nullable
   public IForgeRegistry.DummyFactory<T> getDummyFactory() {
      return this.dummyFactory;
   }

   @Nullable
   public IForgeRegistry.MissingFactory<T> getMissingFactory() {
      return this.missingFactory;
   }

   public boolean getSaveToDisc() {
      return this.saveToDisc;
   }

   public boolean getSync() {
      return this.sync;
   }

   public Set<ResourceLocation> getLegacyNames() {
      return this.legacyNames;
   }
}
