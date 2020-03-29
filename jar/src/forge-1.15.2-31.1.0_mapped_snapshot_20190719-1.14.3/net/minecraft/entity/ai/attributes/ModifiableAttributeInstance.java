package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModifiableAttributeInstance implements IAttributeInstance {
   private final AbstractAttributeMap attributeMap;
   private final IAttribute genericAttribute;
   private final Map<AttributeModifier.Operation, Set<AttributeModifier>> mapByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map<String, Set<AttributeModifier>> mapByName = Maps.newHashMap();
   private final Map<UUID, AttributeModifier> mapByUUID = Maps.newHashMap();
   private double baseValue;
   private boolean needsUpdate = true;
   private double cachedValue;

   public ModifiableAttributeInstance(AbstractAttributeMap p_i1608_1_, IAttribute p_i1608_2_) {
      this.attributeMap = p_i1608_1_;
      this.genericAttribute = p_i1608_2_;
      this.baseValue = p_i1608_2_.getDefaultValue();
      AttributeModifier.Operation[] var3 = AttributeModifier.Operation.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         AttributeModifier.Operation lvt_6_1_ = var3[var5];
         this.mapByOperation.put(lvt_6_1_, Sets.newHashSet());
      }

   }

   public IAttribute getAttribute() {
      return this.genericAttribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double p_111128_1_) {
      if (p_111128_1_ != this.getBaseValue()) {
         this.baseValue = p_111128_1_;
         this.flagForUpdate();
      }
   }

   public Set<AttributeModifier> func_225504_a_(AttributeModifier.Operation p_225504_1_) {
      return (Set)this.mapByOperation.get(p_225504_1_);
   }

   public Set<AttributeModifier> func_225505_c_() {
      Set<AttributeModifier> lvt_1_1_ = Sets.newHashSet();
      AttributeModifier.Operation[] var2 = AttributeModifier.Operation.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AttributeModifier.Operation lvt_5_1_ = var2[var4];
         lvt_1_1_.addAll(this.func_225504_a_(lvt_5_1_));
      }

      return lvt_1_1_;
   }

   @Nullable
   public AttributeModifier getModifier(UUID p_111127_1_) {
      return (AttributeModifier)this.mapByUUID.get(p_111127_1_);
   }

   public boolean hasModifier(AttributeModifier p_180374_1_) {
      return this.mapByUUID.get(p_180374_1_.getID()) != null;
   }

   public void applyModifier(AttributeModifier p_111121_1_) {
      if (this.getModifier(p_111121_1_.getID()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Set<AttributeModifier> lvt_2_1_ = (Set)this.mapByName.computeIfAbsent(p_111121_1_.getName(), (p_220369_0_) -> {
            return Sets.newHashSet();
         });
         ((Set)this.mapByOperation.get(p_111121_1_.getOperation())).add(p_111121_1_);
         lvt_2_1_.add(p_111121_1_);
         this.mapByUUID.put(p_111121_1_.getID(), p_111121_1_);
         this.flagForUpdate();
      }
   }

   protected void flagForUpdate() {
      this.needsUpdate = true;
      this.attributeMap.onAttributeModified(this);
   }

   public void removeModifier(AttributeModifier p_111124_1_) {
      AttributeModifier.Operation[] var2 = AttributeModifier.Operation.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         AttributeModifier.Operation lvt_5_1_ = var2[var4];
         ((Set)this.mapByOperation.get(lvt_5_1_)).remove(p_111124_1_);
      }

      Set<AttributeModifier> lvt_2_1_ = (Set)this.mapByName.get(p_111124_1_.getName());
      if (lvt_2_1_ != null) {
         lvt_2_1_.remove(p_111124_1_);
         if (lvt_2_1_.isEmpty()) {
            this.mapByName.remove(p_111124_1_.getName());
         }
      }

      this.mapByUUID.remove(p_111124_1_.getID());
      this.flagForUpdate();
   }

   public void removeModifier(UUID p_188479_1_) {
      AttributeModifier lvt_2_1_ = this.getModifier(p_188479_1_);
      if (lvt_2_1_ != null) {
         this.removeModifier(lvt_2_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void removeAllModifiers() {
      Collection<AttributeModifier> lvt_1_1_ = this.func_225505_c_();
      if (lvt_1_1_ != null) {
         Collection<AttributeModifier> lvt_1_1_ = Lists.newArrayList(lvt_1_1_);
         Iterator var2 = lvt_1_1_.iterator();

         while(var2.hasNext()) {
            AttributeModifier lvt_3_1_ = (AttributeModifier)var2.next();
            this.removeModifier(lvt_3_1_);
         }

      }
   }

   public double getValue() {
      if (this.needsUpdate) {
         this.cachedValue = this.computeValue();
         this.needsUpdate = false;
      }

      return this.cachedValue;
   }

   private double computeValue() {
      double lvt_1_1_ = this.getBaseValue();

      AttributeModifier lvt_4_1_;
      for(Iterator var3 = this.func_220370_b(AttributeModifier.Operation.ADDITION).iterator(); var3.hasNext(); lvt_1_1_ += lvt_4_1_.getAmount()) {
         lvt_4_1_ = (AttributeModifier)var3.next();
      }

      double lvt_3_1_ = lvt_1_1_;

      Iterator var5;
      AttributeModifier lvt_6_2_;
      for(var5 = this.func_220370_b(AttributeModifier.Operation.MULTIPLY_BASE).iterator(); var5.hasNext(); lvt_3_1_ += lvt_1_1_ * lvt_6_2_.getAmount()) {
         lvt_6_2_ = (AttributeModifier)var5.next();
      }

      for(var5 = this.func_220370_b(AttributeModifier.Operation.MULTIPLY_TOTAL).iterator(); var5.hasNext(); lvt_3_1_ *= 1.0D + lvt_6_2_.getAmount()) {
         lvt_6_2_ = (AttributeModifier)var5.next();
      }

      return this.genericAttribute.clampValue(lvt_3_1_);
   }

   private Collection<AttributeModifier> func_220370_b(AttributeModifier.Operation p_220370_1_) {
      Set<AttributeModifier> lvt_2_1_ = Sets.newHashSet(this.func_225504_a_(p_220370_1_));

      for(IAttribute lvt_3_1_ = this.genericAttribute.getParent(); lvt_3_1_ != null; lvt_3_1_ = lvt_3_1_.getParent()) {
         IAttributeInstance lvt_4_1_ = this.attributeMap.getAttributeInstance(lvt_3_1_);
         if (lvt_4_1_ != null) {
            lvt_2_1_.addAll(lvt_4_1_.func_225504_a_(p_220370_1_));
         }
      }

      return lvt_2_1_;
   }
}
