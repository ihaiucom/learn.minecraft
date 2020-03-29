package net.minecraftforge.server.permission.context;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class ContextKeys {
   public static final ContextKey<BlockPos> POS = ContextKey.create("pos", BlockPos.class);
   public static final ContextKey<Entity> TARGET = ContextKey.create("target", Entity.class);
   public static final ContextKey<Direction> FACING = ContextKey.create("facing", Direction.class);
   public static final ContextKey<AxisAlignedBB> AREA = ContextKey.create("area", AxisAlignedBB.class);
   public static final ContextKey<BlockState> BLOCK_STATE = ContextKey.create("blockstate", BlockState.class);
}
