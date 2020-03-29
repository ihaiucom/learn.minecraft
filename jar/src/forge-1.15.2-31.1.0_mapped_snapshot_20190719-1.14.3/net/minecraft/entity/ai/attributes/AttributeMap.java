package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.LowerStringMap;

public class AttributeMap extends AbstractAttributeMap {
   private final Set<IAttributeInstance> dirtyInstances = Sets.newHashSet();
   protected final Map<String, IAttributeInstance> instancesByName = new LowerStringMap();

   public ModifiableAttributeInstance getAttributeInstance(IAttribute p_111151_1_) {
      return (ModifiableAttributeInstance)super.getAttributeInstance(p_111151_1_);
   }

   public ModifiableAttributeInstance getAttributeInstanceByName(String p_111152_1_) {
      IAttributeInstance lvt_2_1_ = super.getAttributeInstanceByName(p_111152_1_);
      if (lvt_2_1_ == null) {
         lvt_2_1_ = (IAttributeInstance)this.instancesByName.get(p_111152_1_);
      }

      return (ModifiableAttributeInstance)lvt_2_1_;
   }

   public IAttributeInstance registerAttribute(IAttribute p_111150_1_) {
      IAttributeInstance lvt_2_1_ = super.registerAttribute(p_111150_1_);
      if (p_111150_1_ instanceof RangedAttribute && ((RangedAttribute)p_111150_1_).getDescription() != null) {
         this.instancesByName.put(((RangedAttribute)p_111150_1_).getDescription(), lvt_2_1_);
      }

      return lvt_2_1_;
   }

   protected IAttributeInstance createInstance(IAttribute p_180376_1_) {
      return new ModifiableAttributeInstance(this, p_180376_1_);
   }

   public void onAttributeModified(IAttributeInstance p_180794_1_) {
      if (p_180794_1_.getAttribute().getShouldWatch()) {
         this.dirtyInstances.add(p_180794_1_);
      }

      Iterator var2 = this.descendantsByParent.get(p_180794_1_.getAttribute()).iterator();

      while(var2.hasNext()) {
         IAttribute lvt_3_1_ = (IAttribute)var2.next();
         ModifiableAttributeInstance lvt_4_1_ = this.getAttributeInstance(lvt_3_1_);
         if (lvt_4_1_ != null) {
            lvt_4_1_.flagForUpdate();
         }
      }

   }

   public Set<IAttributeInstance> getDirtyInstances() {
      return this.dirtyInstances;
   }

   public Collection<IAttributeInstance> getWatchedAttributes() {
      Set<IAttributeInstance> lvt_1_1_ = Sets.newHashSet();
      Iterator var2 = this.getAllAttributes().iterator();

      while(var2.hasNext()) {
         IAttributeInstance lvt_3_1_ = (IAttributeInstance)var2.next();
         if (lvt_3_1_.getAttribute().getShouldWatch()) {
            lvt_1_1_.add(lvt_3_1_);
         }
      }

      return lvt_1_1_;
   }

   // $FF: synthetic method
   public IAttributeInstance getAttributeInstanceByName(String p_111152_1_) {
      return this.getAttributeInstanceByName(p_111152_1_);
   }

   // $FF: synthetic method
   public IAttributeInstance getAttributeInstance(IAttribute p_111151_1_) {
      return this.getAttributeInstance(p_111151_1_);
   }
}
