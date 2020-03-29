package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolItem extends TieredItem {
   private final Set<Block> effectiveBlocks;
   protected final float efficiency;
   protected final float attackDamage;
   protected final float attackSpeed;

   protected ToolItem(float p_i48512_1_, float p_i48512_2_, IItemTier p_i48512_3_, Set<Block> p_i48512_4_, Item.Properties p_i48512_5_) {
      super(p_i48512_3_, p_i48512_5_);
      this.effectiveBlocks = p_i48512_4_;
      this.efficiency = p_i48512_3_.getEfficiency();
      this.attackDamage = p_i48512_1_ + p_i48512_3_.getAttackDamage();
      this.attackSpeed = p_i48512_2_;
   }

   public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
      if (this.getToolTypes(p_150893_1_).stream().anyMatch((p_lambda$getDestroySpeed$0_1_) -> {
         return p_150893_2_.isToolEffective(p_lambda$getDestroySpeed$0_1_);
      })) {
         return this.efficiency;
      } else {
         return this.effectiveBlocks.contains(p_150893_2_.getBlock()) ? this.efficiency : 1.0F;
      }
   }

   public boolean hitEntity(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      p_77644_1_.damageItem(2, p_77644_3_, (p_lambda$hitEntity$1_0_) -> {
         p_lambda$hitEntity$1_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      if (!p_179218_2_.isRemote && p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) != 0.0F) {
         p_179218_1_.damageItem(1, p_179218_5_, (p_lambda$onBlockDestroyed$2_0_) -> {
            p_lambda$onBlockDestroyed$2_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
         });
      }

      return true;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EquipmentSlotType.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }
}
