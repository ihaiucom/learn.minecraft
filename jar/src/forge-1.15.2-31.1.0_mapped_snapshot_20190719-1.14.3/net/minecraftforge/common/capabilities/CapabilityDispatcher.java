package net.minecraftforge.common.capabilities;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CapabilityDispatcher implements INBTSerializable<CompoundNBT>, ICapabilityProvider {
   private ICapabilityProvider[] caps;
   private INBTSerializable<INBT>[] writers;
   private String[] names;
   private final List<Runnable> listeners;

   public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners) {
      this(list, listeners, (ICapabilityProvider)null);
   }

   public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners, @Nullable ICapabilityProvider parent) {
      List<ICapabilityProvider> lstCaps = Lists.newArrayList();
      List<INBTSerializable<INBT>> lstWriters = Lists.newArrayList();
      List<String> lstNames = Lists.newArrayList();
      this.listeners = listeners;
      if (parent != null) {
         lstCaps.add(parent);
         if (parent instanceof INBTSerializable) {
            lstWriters.add((INBTSerializable)parent);
            lstNames.add("Parent");
         }
      }

      Iterator var7 = list.entrySet().iterator();

      while(var7.hasNext()) {
         Entry<ResourceLocation, ICapabilityProvider> entry = (Entry)var7.next();
         ICapabilityProvider prov = (ICapabilityProvider)entry.getValue();
         lstCaps.add(prov);
         if (prov instanceof INBTSerializable) {
            lstWriters.add((INBTSerializable)prov);
            lstNames.add(((ResourceLocation)entry.getKey()).toString());
         }
      }

      this.caps = (ICapabilityProvider[])lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);
      this.writers = (INBTSerializable[])lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
      this.names = (String[])lstNames.toArray(new String[lstNames.size()]);
   }

   public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
      ICapabilityProvider[] var3 = this.caps;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ICapabilityProvider c = var3[var5];
         LazyOptional<T> ret = c.getCapability(cap, side);
         if (ret == null) {
            throw new RuntimeException(String.format("Provider %s.getCapability() returned null; return LazyOptional.empty() instead!", c.getClass().getTypeName()));
         }

         if (ret.isPresent()) {
            return ret;
         }
      }

      return LazyOptional.empty();
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();

      for(int x = 0; x < this.writers.length; ++x) {
         nbt.put(this.names[x], this.writers[x].serializeNBT());
      }

      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      for(int x = 0; x < this.writers.length; ++x) {
         if (nbt.contains(this.names[x])) {
            this.writers[x].deserializeNBT(nbt.get(this.names[x]));
         }
      }

   }

   public boolean areCompatible(@Nullable CapabilityDispatcher other) {
      if (other == null) {
         return this.writers.length == 0;
      } else if (this.writers.length == 0) {
         return other.writers.length == 0;
      } else {
         return this.serializeNBT().equals(other.serializeNBT());
      }
   }

   public void invalidate() {
      this.listeners.forEach(Runnable::run);
   }
}
