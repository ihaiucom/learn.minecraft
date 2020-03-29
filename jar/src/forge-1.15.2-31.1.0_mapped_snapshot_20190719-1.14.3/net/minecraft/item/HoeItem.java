package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class HoeItem extends TieredItem {
   private final float speed;
   protected static final Map<Block, BlockState> HOE_LOOKUP;

   public HoeItem(IItemTier p_i48488_1_, float p_i48488_2_, Item.Properties p_i48488_3_) {
      super(p_i48488_1_, p_i48488_3_);
      this.speed = p_i48488_2_;
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      int hook = ForgeEventFactory.onHoeUse(p_195939_1_);
      if (hook != 0) {
         return hook > 0 ? ActionResultType.SUCCESS : ActionResultType.FAIL;
      } else {
         if (p_195939_1_.getFace() != Direction.DOWN && world.isAirBlock(blockpos.up())) {
            BlockState blockstate = (BlockState)HOE_LOOKUP.get(world.getBlockState(blockpos).getBlock());
            if (blockstate != null) {
               PlayerEntity playerentity = p_195939_1_.getPlayer();
               world.playSound(playerentity, blockpos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
               if (!world.isRemote) {
                  world.setBlockState(blockpos, blockstate, 11);
                  if (playerentity != null) {
                     p_195939_1_.getItem().damageItem(1, playerentity, (p_lambda$onItemUse$0_1_) -> {
                        p_lambda$onItemUse$0_1_.sendBreakAnimation(p_195939_1_.getHand());
                     });
                  }
               }

               return ActionResultType.SUCCESS;
            }
         }

         return ActionResultType.PASS;
      }
   }

   public boolean hitEntity(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      p_77644_1_.damageItem(1, p_77644_3_, (p_lambda$hitEntity$1_0_) -> {
         p_lambda$hitEntity$1_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EquipmentSlotType.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 0.0D, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double)this.speed, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }

   static {
      HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));
   }
}
