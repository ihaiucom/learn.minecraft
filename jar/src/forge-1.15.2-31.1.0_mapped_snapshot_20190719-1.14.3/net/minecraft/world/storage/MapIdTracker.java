package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Iterator;
import net.minecraft.nbt.CompoundNBT;

public class MapIdTracker extends WorldSavedData {
   private final Object2IntMap<String> field_215163_a = new Object2IntOpenHashMap();

   public MapIdTracker() {
      super("idcounts");
      this.field_215163_a.defaultReturnValue(-1);
   }

   public void read(CompoundNBT p_76184_1_) {
      this.field_215163_a.clear();
      Iterator var2 = p_76184_1_.keySet().iterator();

      while(var2.hasNext()) {
         String lvt_3_1_ = (String)var2.next();
         if (p_76184_1_.contains(lvt_3_1_, 99)) {
            this.field_215163_a.put(lvt_3_1_, p_76184_1_.getInt(lvt_3_1_));
         }
      }

   }

   public CompoundNBT write(CompoundNBT p_189551_1_) {
      ObjectIterator var2 = this.field_215163_a.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Entry<String> lvt_3_1_ = (Entry)var2.next();
         p_189551_1_.putInt((String)lvt_3_1_.getKey(), lvt_3_1_.getIntValue());
      }

      return p_189551_1_;
   }

   public int func_215162_a() {
      int lvt_1_1_ = this.field_215163_a.getInt("map") + 1;
      this.field_215163_a.put("map", lvt_1_1_);
      this.markDirty();
      return lvt_1_1_;
   }
}
