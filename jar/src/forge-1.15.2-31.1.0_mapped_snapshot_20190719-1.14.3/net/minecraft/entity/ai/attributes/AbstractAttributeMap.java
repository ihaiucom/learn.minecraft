package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.LowerStringMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractAttributeMap {
   protected final Map<IAttribute, IAttributeInstance> attributes = Maps.newHashMap();
   protected final Map<String, IAttributeInstance> attributesByName = new LowerStringMap();
   protected final Multimap<IAttribute, IAttribute> descendantsByParent = HashMultimap.create();

   @Nullable
   public IAttributeInstance getAttributeInstance(IAttribute p_111151_1_) {
      return (IAttributeInstance)this.attributes.get(p_111151_1_);
   }

   @Nullable
   public IAttributeInstance getAttributeInstanceByName(String p_111152_1_) {
      return (IAttributeInstance)this.attributesByName.get(p_111152_1_);
   }

   public IAttributeInstance registerAttribute(IAttribute p_111150_1_) {
      if (this.attributesByName.containsKey(p_111150_1_.getName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      } else {
         IAttributeInstance lvt_2_1_ = this.createInstance(p_111150_1_);
         this.attributesByName.put(p_111150_1_.getName(), lvt_2_1_);
         this.attributes.put(p_111150_1_, lvt_2_1_);

         for(IAttribute lvt_3_1_ = p_111150_1_.getParent(); lvt_3_1_ != null; lvt_3_1_ = lvt_3_1_.getParent()) {
            this.descendantsByParent.put(lvt_3_1_, p_111150_1_);
         }

         return lvt_2_1_;
      }
   }

   protected abstract IAttributeInstance createInstance(IAttribute var1);

   public Collection<IAttributeInstance> getAllAttributes() {
      return this.attributesByName.values();
   }

   public void onAttributeModified(IAttributeInstance p_180794_1_) {
   }

   public void removeAttributeModifiers(Multimap<String, AttributeModifier> p_111148_1_) {
      Iterator var2 = p_111148_1_.entries().iterator();

      while(var2.hasNext()) {
         Entry<String, AttributeModifier> lvt_3_1_ = (Entry)var2.next();
         IAttributeInstance lvt_4_1_ = this.getAttributeInstanceByName((String)lvt_3_1_.getKey());
         if (lvt_4_1_ != null) {
            lvt_4_1_.removeModifier((AttributeModifier)lvt_3_1_.getValue());
         }
      }

   }

   public void applyAttributeModifiers(Multimap<String, AttributeModifier> p_111147_1_) {
      Iterator var2 = p_111147_1_.entries().iterator();

      while(var2.hasNext()) {
         Entry<String, AttributeModifier> lvt_3_1_ = (Entry)var2.next();
         IAttributeInstance lvt_4_1_ = this.getAttributeInstanceByName((String)lvt_3_1_.getKey());
         if (lvt_4_1_ != null) {
            lvt_4_1_.removeModifier((AttributeModifier)lvt_3_1_.getValue());
            lvt_4_1_.applyModifier((AttributeModifier)lvt_3_1_.getValue());
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void func_226303_a_(AbstractAttributeMap p_226303_1_) {
      this.getAllAttributes().forEach((p_226304_1_) -> {
         IAttributeInstance lvt_2_1_ = p_226303_1_.getAttributeInstance(p_226304_1_.getAttribute());
         if (lvt_2_1_ != null) {
            p_226304_1_.func_226302_a_(lvt_2_1_);
         }

      });
   }
}
