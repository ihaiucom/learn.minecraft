package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem extends TieredItem {
   private final float attackDamage;
   private final float attackSpeed;

   public SwordItem(IItemTier p_i48460_1_, int p_i48460_2_, float p_i48460_3_, Item.Properties p_i48460_4_) {
      super(p_i48460_1_, p_i48460_4_);
      this.attackSpeed = p_i48460_3_;
      this.attackDamage = (float)p_i48460_2_ + p_i48460_1_.getAttackDamage();
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public boolean canPlayerBreakBlockWhileHolding(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
      return !p_195938_4_.isCreative();
   }

   public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
      Block lvt_3_1_ = p_150893_2_.getBlock();
      if (lvt_3_1_ == Blocks.COBWEB) {
         return 15.0F;
      } else {
         Material lvt_4_1_ = p_150893_2_.getMaterial();
         return lvt_4_1_ != Material.PLANTS && lvt_4_1_ != Material.TALL_PLANTS && lvt_4_1_ != Material.CORAL && !p_150893_2_.isIn(BlockTags.LEAVES) && lvt_4_1_ != Material.GOURD ? 1.0F : 1.5F;
      }
   }

   public boolean hitEntity(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      p_77644_1_.damageItem(1, p_77644_3_, (p_220045_0_) -> {
         p_220045_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      if (p_179218_3_.getBlockHardness(p_179218_2_, p_179218_4_) != 0.0F) {
         p_179218_1_.damageItem(2, p_179218_5_, (p_220044_0_) -> {
            p_220044_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
         });
      }

      return true;
   }

   public boolean canHarvestBlock(BlockState p_150897_1_) {
      return p_150897_1_.getBlock() == Blocks.COBWEB;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType p_111205_1_) {
      Multimap<String, AttributeModifier> lvt_2_1_ = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EquipmentSlotType.MAINHAND) {
         lvt_2_1_.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
         lvt_2_1_.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
      }

      return lvt_2_1_;
   }
}
