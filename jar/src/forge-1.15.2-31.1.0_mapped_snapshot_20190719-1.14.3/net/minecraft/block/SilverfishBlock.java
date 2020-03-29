package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SilverfishBlock extends Block {
   private final Block mimickedBlock;
   private static final Map<Block, Block> field_196470_b = Maps.newIdentityHashMap();

   public SilverfishBlock(Block p_i48374_1_, Block.Properties p_i48374_2_) {
      super(p_i48374_2_);
      this.mimickedBlock = p_i48374_1_;
      field_196470_b.put(p_i48374_1_, this);
   }

   public Block getMimickedBlock() {
      return this.mimickedBlock;
   }

   public static boolean canContainSilverfish(BlockState p_196466_0_) {
      return field_196470_b.containsKey(p_196466_0_.getBlock());
   }

   public void spawnAdditionalDrops(BlockState p_220062_1_, World p_220062_2_, BlockPos p_220062_3_, ItemStack p_220062_4_) {
      super.spawnAdditionalDrops(p_220062_1_, p_220062_2_, p_220062_3_, p_220062_4_);
      if (!p_220062_2_.isRemote && p_220062_2_.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, p_220062_4_) == 0) {
         SilverfishEntity lvt_5_1_ = (SilverfishEntity)EntityType.SILVERFISH.create(p_220062_2_);
         lvt_5_1_.setLocationAndAngles((double)p_220062_3_.getX() + 0.5D, (double)p_220062_3_.getY(), (double)p_220062_3_.getZ() + 0.5D, 0.0F, 0.0F);
         p_220062_2_.addEntity(lvt_5_1_);
         lvt_5_1_.spawnExplosionParticle();
      }

   }

   public static BlockState infest(Block p_196467_0_) {
      return ((Block)field_196470_b.get(p_196467_0_)).getDefaultState();
   }
}
