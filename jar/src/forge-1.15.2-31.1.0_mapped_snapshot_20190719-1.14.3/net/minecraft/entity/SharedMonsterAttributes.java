package net.minecraft.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final IAttribute MAX_HEALTH = (new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0D, 1.401298464324817E-45D, 1024.0D)).setDescription("Max Health").setShouldWatch(true);
   public static final IAttribute FOLLOW_RANGE = (new RangedAttribute((IAttribute)null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).setDescription("Follow Range");
   public static final IAttribute KNOCKBACK_RESISTANCE = (new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).setDescription("Knockback Resistance");
   public static final IAttribute MOVEMENT_SPEED = (new RangedAttribute((IAttribute)null, "generic.movementSpeed", 0.699999988079071D, 0.0D, 1024.0D)).setDescription("Movement Speed").setShouldWatch(true);
   public static final IAttribute FLYING_SPEED = (new RangedAttribute((IAttribute)null, "generic.flyingSpeed", 0.4000000059604645D, 0.0D, 1024.0D)).setDescription("Flying Speed").setShouldWatch(true);
   public static final IAttribute ATTACK_DAMAGE = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);
   public static final IAttribute ATTACK_KNOCKBACK = new RangedAttribute((IAttribute)null, "generic.attackKnockback", 0.0D, 0.0D, 5.0D);
   public static final IAttribute ATTACK_SPEED = (new RangedAttribute((IAttribute)null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).setShouldWatch(true);
   public static final IAttribute ARMOR = (new RangedAttribute((IAttribute)null, "generic.armor", 0.0D, 0.0D, 30.0D)).setShouldWatch(true);
   public static final IAttribute ARMOR_TOUGHNESS = (new RangedAttribute((IAttribute)null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).setShouldWatch(true);
   public static final IAttribute LUCK = (new RangedAttribute((IAttribute)null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).setShouldWatch(true);

   public static ListNBT writeAttributes(AbstractAttributeMap p_111257_0_) {
      ListNBT listnbt = new ListNBT();
      Iterator var2 = p_111257_0_.getAllAttributes().iterator();

      while(var2.hasNext()) {
         IAttributeInstance iattributeinstance = (IAttributeInstance)var2.next();
         listnbt.add(writeAttribute(iattributeinstance));
      }

      return listnbt;
   }

   private static CompoundNBT writeAttribute(IAttributeInstance p_111261_0_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      IAttribute iattribute = p_111261_0_.getAttribute();
      compoundnbt.putString("Name", iattribute.getName());
      compoundnbt.putDouble("Base", p_111261_0_.getBaseValue());
      Collection<AttributeModifier> collection = p_111261_0_.func_225505_c_();
      if (collection != null && !collection.isEmpty()) {
         ListNBT listnbt = new ListNBT();
         Iterator var5 = collection.iterator();

         while(var5.hasNext()) {
            AttributeModifier attributemodifier = (AttributeModifier)var5.next();
            if (attributemodifier.isSaved()) {
               listnbt.add(writeAttributeModifier(attributemodifier));
            }
         }

         compoundnbt.put("Modifiers", listnbt);
      }

      return compoundnbt;
   }

   public static CompoundNBT writeAttributeModifier(AttributeModifier p_111262_0_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", p_111262_0_.getName());
      compoundnbt.putDouble("Amount", p_111262_0_.getAmount());
      compoundnbt.putInt("Operation", p_111262_0_.getOperation().getId());
      compoundnbt.putUniqueId("UUID", p_111262_0_.getID());
      return compoundnbt;
   }

   public static void readAttributes(AbstractAttributeMap p_151475_0_, ListNBT p_151475_1_) {
      for(int i = 0; i < p_151475_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_151475_1_.getCompound(i);
         IAttributeInstance iattributeinstance = p_151475_0_.getAttributeInstanceByName(compoundnbt.getString("Name"));
         if (iattributeinstance == null) {
            LOGGER.warn("Ignoring unknown attribute '{}'", compoundnbt.getString("Name"));
         } else {
            readAttribute(iattributeinstance, compoundnbt);
         }
      }

   }

   private static void readAttribute(IAttributeInstance p_111258_0_, CompoundNBT p_111258_1_) {
      p_111258_0_.setBaseValue(p_111258_1_.getDouble("Base"));
      if (p_111258_1_.contains("Modifiers", 9)) {
         ListNBT listnbt = p_111258_1_.getList("Modifiers", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            AttributeModifier attributemodifier = readAttributeModifier(listnbt.getCompound(i));
            if (attributemodifier != null) {
               AttributeModifier attributemodifier1 = p_111258_0_.getModifier(attributemodifier.getID());
               if (attributemodifier1 != null) {
                  p_111258_0_.removeModifier(attributemodifier1);
               }

               p_111258_0_.applyModifier(attributemodifier);
            }
         }
      }

   }

   @Nullable
   public static AttributeModifier readAttributeModifier(CompoundNBT p_111259_0_) {
      UUID uuid = p_111259_0_.getUniqueId("UUID");

      try {
         AttributeModifier.Operation attributemodifier$operation = AttributeModifier.Operation.byId(p_111259_0_.getInt("Operation"));
         return new AttributeModifier(uuid, p_111259_0_.getString("Name"), p_111259_0_.getDouble("Amount"), attributemodifier$operation);
      } catch (Exception var3) {
         LOGGER.warn("Unable to create attribute: {}", var3.getMessage());
         return null;
      }
   }
}
