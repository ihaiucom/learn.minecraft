package net.minecraft.command.arguments;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockStateInput implements Predicate<CachedBlockInfo> {
   private final BlockState state;
   private final Set<IProperty<?>> properties;
   @Nullable
   private final CompoundNBT tag;

   public BlockStateInput(BlockState p_i47967_1_, Set<IProperty<?>> p_i47967_2_, @Nullable CompoundNBT p_i47967_3_) {
      this.state = p_i47967_1_;
      this.properties = p_i47967_2_;
      this.tag = p_i47967_3_;
   }

   public BlockState getState() {
      return this.state;
   }

   public boolean test(CachedBlockInfo p_test_1_) {
      BlockState lvt_2_1_ = p_test_1_.getBlockState();
      if (lvt_2_1_.getBlock() != this.state.getBlock()) {
         return false;
      } else {
         Iterator var3 = this.properties.iterator();

         while(var3.hasNext()) {
            IProperty<?> lvt_4_1_ = (IProperty)var3.next();
            if (lvt_2_1_.get(lvt_4_1_) != this.state.get(lvt_4_1_)) {
               return false;
            }
         }

         if (this.tag == null) {
            return true;
         } else {
            TileEntity lvt_3_1_ = p_test_1_.getTileEntity();
            return lvt_3_1_ != null && NBTUtil.areNBTEquals(this.tag, lvt_3_1_.write(new CompoundNBT()), true);
         }
      }
   }

   public boolean place(ServerWorld p_197230_1_, BlockPos p_197230_2_, int p_197230_3_) {
      if (!p_197230_1_.setBlockState(p_197230_2_, this.state, p_197230_3_)) {
         return false;
      } else {
         if (this.tag != null) {
            TileEntity lvt_4_1_ = p_197230_1_.getTileEntity(p_197230_2_);
            if (lvt_4_1_ != null) {
               CompoundNBT lvt_5_1_ = this.tag.copy();
               lvt_5_1_.putInt("x", p_197230_2_.getX());
               lvt_5_1_.putInt("y", p_197230_2_.getY());
               lvt_5_1_.putInt("z", p_197230_2_.getZ());
               lvt_4_1_.read(lvt_5_1_);
            }
         }

         return true;
      }
   }

   // $FF: synthetic method
   public boolean test(Object p_test_1_) {
      return this.test((CachedBlockInfo)p_test_1_);
   }
}
