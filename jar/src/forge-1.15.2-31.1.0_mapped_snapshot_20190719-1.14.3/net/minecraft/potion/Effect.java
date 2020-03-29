package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.extensions.IForgeEffect;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Effect extends ForgeRegistryEntry<Effect> implements IForgeEffect {
   private final Map<IAttribute, AttributeModifier> attributeModifierMap = Maps.newHashMap();
   private final EffectType type;
   private final int liquidColor;
   @Nullable
   private String name;

   @Nullable
   public static Effect get(int p_188412_0_) {
      return (Effect)Registry.EFFECTS.getByValue(p_188412_0_);
   }

   public static int getId(Effect p_188409_0_) {
      return Registry.EFFECTS.getId(p_188409_0_);
   }

   protected Effect(EffectType p_i50391_1_, int p_i50391_2_) {
      this.type = p_i50391_1_;
      this.liquidColor = p_i50391_2_;
   }

   public void performEffect(LivingEntity p_76394_1_, int p_76394_2_) {
      if (this == Effects.REGENERATION) {
         if (p_76394_1_.getHealth() < p_76394_1_.getMaxHealth()) {
            p_76394_1_.heal(1.0F);
         }
      } else if (this == Effects.POISON) {
         if (p_76394_1_.getHealth() > 1.0F) {
            p_76394_1_.attackEntityFrom(DamageSource.MAGIC, 1.0F);
         }
      } else if (this == Effects.WITHER) {
         p_76394_1_.attackEntityFrom(DamageSource.WITHER, 1.0F);
      } else if (this == Effects.HUNGER && p_76394_1_ instanceof PlayerEntity) {
         ((PlayerEntity)p_76394_1_).addExhaustion(0.005F * (float)(p_76394_2_ + 1));
      } else if (this == Effects.SATURATION && p_76394_1_ instanceof PlayerEntity) {
         if (!p_76394_1_.world.isRemote) {
            ((PlayerEntity)p_76394_1_).getFoodStats().addStats(p_76394_2_ + 1, 1.0F);
         }
      } else if (this == Effects.INSTANT_HEALTH && !p_76394_1_.isEntityUndead() || this == Effects.INSTANT_DAMAGE && p_76394_1_.isEntityUndead()) {
         p_76394_1_.heal((float)Math.max(4 << p_76394_2_, 0));
      } else if (this == Effects.INSTANT_DAMAGE && !p_76394_1_.isEntityUndead() || this == Effects.INSTANT_HEALTH && p_76394_1_.isEntityUndead()) {
         p_76394_1_.attackEntityFrom(DamageSource.MAGIC, (float)(6 << p_76394_2_));
      }

   }

   public void affectEntity(@Nullable Entity p_180793_1_, @Nullable Entity p_180793_2_, LivingEntity p_180793_3_, int p_180793_4_, double p_180793_5_) {
      int j;
      if (this == Effects.INSTANT_HEALTH && !p_180793_3_.isEntityUndead() || this == Effects.INSTANT_DAMAGE && p_180793_3_.isEntityUndead()) {
         j = (int)(p_180793_5_ * (double)(4 << p_180793_4_) + 0.5D);
         p_180793_3_.heal((float)j);
      } else if ((this != Effects.INSTANT_DAMAGE || p_180793_3_.isEntityUndead()) && (this != Effects.INSTANT_HEALTH || !p_180793_3_.isEntityUndead())) {
         this.performEffect(p_180793_3_, p_180793_4_);
      } else {
         j = (int)(p_180793_5_ * (double)(6 << p_180793_4_) + 0.5D);
         if (p_180793_1_ == null) {
            p_180793_3_.attackEntityFrom(DamageSource.MAGIC, (float)j);
         } else {
            p_180793_3_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(p_180793_1_, p_180793_2_), (float)j);
         }
      }

   }

   public boolean isReady(int p_76397_1_, int p_76397_2_) {
      int i;
      if (this == Effects.REGENERATION) {
         i = 50 >> p_76397_2_;
         if (i > 0) {
            return p_76397_1_ % i == 0;
         } else {
            return true;
         }
      } else if (this == Effects.POISON) {
         i = 25 >> p_76397_2_;
         if (i > 0) {
            return p_76397_1_ % i == 0;
         } else {
            return true;
         }
      } else if (this == Effects.WITHER) {
         i = 40 >> p_76397_2_;
         if (i > 0) {
            return p_76397_1_ % i == 0;
         } else {
            return true;
         }
      } else {
         return this == Effects.HUNGER;
      }
   }

   public boolean isInstant() {
      return false;
   }

   protected String getOrCreateDescriptionId() {
      if (this.name == null) {
         this.name = Util.makeTranslationKey("effect", Registry.EFFECTS.getKey(this));
      }

      return this.name;
   }

   public String getName() {
      return this.getOrCreateDescriptionId();
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent(this.getName(), new Object[0]);
   }

   public EffectType getEffectType() {
      return this.type;
   }

   public int getLiquidColor() {
      return this.liquidColor;
   }

   public Effect addAttributesModifier(IAttribute p_220304_1_, String p_220304_2_, double p_220304_3_, AttributeModifier.Operation p_220304_5_) {
      AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(p_220304_2_), this::getName, p_220304_3_, p_220304_5_);
      this.attributeModifierMap.put(p_220304_1_, attributemodifier);
      return this;
   }

   public Map<IAttribute, AttributeModifier> getAttributeModifierMap() {
      return this.attributeModifierMap;
   }

   public void removeAttributesModifiersFromEntity(LivingEntity p_111187_1_, AbstractAttributeMap p_111187_2_, int p_111187_3_) {
      Iterator var4 = this.attributeModifierMap.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<IAttribute, AttributeModifier> entry = (Entry)var4.next();
         IAttributeInstance iattributeinstance = p_111187_2_.getAttributeInstance((IAttribute)entry.getKey());
         if (iattributeinstance != null) {
            iattributeinstance.removeModifier((AttributeModifier)entry.getValue());
         }
      }

   }

   public void applyAttributesModifiersToEntity(LivingEntity p_111185_1_, AbstractAttributeMap p_111185_2_, int p_111185_3_) {
      Iterator var4 = this.attributeModifierMap.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<IAttribute, AttributeModifier> entry = (Entry)var4.next();
         IAttributeInstance iattributeinstance = p_111185_2_.getAttributeInstance((IAttribute)entry.getKey());
         if (iattributeinstance != null) {
            AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
            iattributeinstance.removeModifier(attributemodifier);
            iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), this.getName() + " " + p_111185_3_, this.getAttributeModifierAmount(p_111185_3_, attributemodifier), attributemodifier.getOperation()));
         }
      }

   }

   public double getAttributeModifierAmount(int p_111183_1_, AttributeModifier p_111183_2_) {
      return p_111183_2_.getAmount() * (double)(p_111183_1_ + 1);
   }

   public boolean isBeneficial() {
      return this.type == EffectType.BENEFICIAL;
   }
}
