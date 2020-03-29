package net.minecraft.tags;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class NetworkTagCollection<T> extends TagCollection<T> {
   private final Registry<T> registry;

   public NetworkTagCollection(Registry<T> p_i49817_1_, String p_i49817_2_, String p_i49817_3_) {
      super(p_i49817_1_::getValue, p_i49817_2_, false, p_i49817_3_);
      this.registry = p_i49817_1_;
   }

   public void write(PacketBuffer p_200042_1_) {
      Map<ResourceLocation, Tag<T>> lvt_2_1_ = this.getTagMap();
      p_200042_1_.writeVarInt(lvt_2_1_.size());
      Iterator var3 = lvt_2_1_.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<ResourceLocation, Tag<T>> lvt_4_1_ = (Entry)var3.next();
         p_200042_1_.writeResourceLocation((ResourceLocation)lvt_4_1_.getKey());
         p_200042_1_.writeVarInt(((Tag)lvt_4_1_.getValue()).getAllElements().size());
         Iterator var5 = ((Tag)lvt_4_1_.getValue()).getAllElements().iterator();

         while(var5.hasNext()) {
            T lvt_6_1_ = var5.next();
            p_200042_1_.writeVarInt(this.registry.getId(lvt_6_1_));
         }
      }

   }

   public void read(PacketBuffer p_200043_1_) {
      Map<ResourceLocation, Tag<T>> lvt_2_1_ = Maps.newHashMap();
      int lvt_3_1_ = p_200043_1_.readVarInt();

      for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_) {
         ResourceLocation lvt_5_1_ = p_200043_1_.readResourceLocation();
         int lvt_6_1_ = p_200043_1_.readVarInt();
         Tag.Builder<T> lvt_7_1_ = Tag.Builder.create();

         for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_6_1_; ++lvt_8_1_) {
            lvt_7_1_.add(this.registry.getByValue(p_200043_1_.readVarInt()));
         }

         lvt_2_1_.put(lvt_5_1_, lvt_7_1_.build(lvt_5_1_));
      }

      this.func_223507_b(lvt_2_1_);
   }
}
