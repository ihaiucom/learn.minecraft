package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpawnEggItem extends Item {
   private static final Map<EntityType<?>, SpawnEggItem> EGGS = Maps.newIdentityHashMap();
   private final int primaryColor;
   private final int secondaryColor;
   private final EntityType<?> typeIn;

   public SpawnEggItem(EntityType<?> p_i48465_1_, int p_i48465_2_, int p_i48465_3_, Item.Properties p_i48465_4_) {
      super(p_i48465_4_);
      this.typeIn = p_i48465_1_;
      this.primaryColor = p_i48465_2_;
      this.secondaryColor = p_i48465_3_;
      EGGS.put(p_i48465_1_, this);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      if (lvt_2_1_.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         ItemStack lvt_3_1_ = p_195939_1_.getItem();
         BlockPos lvt_4_1_ = p_195939_1_.getPos();
         Direction lvt_5_1_ = p_195939_1_.getFace();
         BlockState lvt_6_1_ = lvt_2_1_.getBlockState(lvt_4_1_);
         Block lvt_7_1_ = lvt_6_1_.getBlock();
         if (lvt_7_1_ == Blocks.SPAWNER) {
            TileEntity lvt_8_1_ = lvt_2_1_.getTileEntity(lvt_4_1_);
            if (lvt_8_1_ instanceof MobSpawnerTileEntity) {
               AbstractSpawner lvt_9_1_ = ((MobSpawnerTileEntity)lvt_8_1_).getSpawnerBaseLogic();
               EntityType<?> lvt_10_1_ = this.getType(lvt_3_1_.getTag());
               lvt_9_1_.setEntityType(lvt_10_1_);
               lvt_8_1_.markDirty();
               lvt_2_1_.notifyBlockUpdate(lvt_4_1_, lvt_6_1_, lvt_6_1_, 3);
               lvt_3_1_.shrink(1);
               return ActionResultType.SUCCESS;
            }
         }

         BlockPos lvt_8_3_;
         if (lvt_6_1_.getCollisionShape(lvt_2_1_, lvt_4_1_).isEmpty()) {
            lvt_8_3_ = lvt_4_1_;
         } else {
            lvt_8_3_ = lvt_4_1_.offset(lvt_5_1_);
         }

         EntityType<?> lvt_9_2_ = this.getType(lvt_3_1_.getTag());
         if (lvt_9_2_.spawn(lvt_2_1_, lvt_3_1_, p_195939_1_.getPlayer(), lvt_8_3_, SpawnReason.SPAWN_EGG, true, !Objects.equals(lvt_4_1_, lvt_8_3_) && lvt_5_1_ == Direction.UP) != null) {
            lvt_3_1_.shrink(1);
         }

         return ActionResultType.SUCCESS;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      RayTraceResult lvt_5_1_ = rayTrace(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.SOURCE_ONLY);
      if (lvt_5_1_.getType() != RayTraceResult.Type.BLOCK) {
         return ActionResult.func_226250_c_(lvt_4_1_);
      } else if (p_77659_1_.isRemote) {
         return ActionResult.func_226248_a_(lvt_4_1_);
      } else {
         BlockRayTraceResult lvt_6_1_ = (BlockRayTraceResult)lvt_5_1_;
         BlockPos lvt_7_1_ = lvt_6_1_.getPos();
         if (!(p_77659_1_.getBlockState(lvt_7_1_).getBlock() instanceof FlowingFluidBlock)) {
            return ActionResult.func_226250_c_(lvt_4_1_);
         } else if (p_77659_1_.isBlockModifiable(p_77659_2_, lvt_7_1_) && p_77659_2_.canPlayerEdit(lvt_7_1_, lvt_6_1_.getFace(), lvt_4_1_)) {
            EntityType<?> lvt_8_1_ = this.getType(lvt_4_1_.getTag());
            if (lvt_8_1_.spawn(p_77659_1_, lvt_4_1_, p_77659_2_, lvt_7_1_, SpawnReason.SPAWN_EGG, false, false) == null) {
               return ActionResult.func_226250_c_(lvt_4_1_);
            } else {
               if (!p_77659_2_.abilities.isCreativeMode) {
                  lvt_4_1_.shrink(1);
               }

               p_77659_2_.addStat(Stats.ITEM_USED.get(this));
               return ActionResult.func_226248_a_(lvt_4_1_);
            }
         } else {
            return ActionResult.func_226251_d_(lvt_4_1_);
         }
      }
   }

   public boolean hasType(@Nullable CompoundNBT p_208077_1_, EntityType<?> p_208077_2_) {
      return Objects.equals(this.getType(p_208077_1_), p_208077_2_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getColor(int p_195983_1_) {
      return p_195983_1_ == 0 ? this.primaryColor : this.secondaryColor;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static SpawnEggItem getEgg(@Nullable EntityType<?> p_200889_0_) {
      return (SpawnEggItem)EGGS.get(p_200889_0_);
   }

   public static Iterable<SpawnEggItem> getEggs() {
      return Iterables.unmodifiableIterable(EGGS.values());
   }

   public EntityType<?> getType(@Nullable CompoundNBT p_208076_1_) {
      if (p_208076_1_ != null && p_208076_1_.contains("EntityTag", 10)) {
         CompoundNBT lvt_2_1_ = p_208076_1_.getCompound("EntityTag");
         if (lvt_2_1_.contains("id", 8)) {
            return (EntityType)EntityType.byKey(lvt_2_1_.getString("id")).orElse(this.typeIn);
         }
      }

      return this.typeIn;
   }
}
