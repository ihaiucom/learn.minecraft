package net.minecraftforge.client.model.pipeline;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.ILightReader;

public class BlockInfo {
   private static final Direction[] SIDES = Direction.values();
   private final BlockColors colors;
   private ILightReader world;
   private BlockState state;
   private BlockPos blockPos;
   private final boolean[][][] t = new boolean[3][3][3];
   private final int[][][] s = new int[3][3][3];
   private final int[][][] b = new int[3][3][3];
   private final float[][][][] skyLight = new float[3][2][2][2];
   private final float[][][][] blockLight = new float[3][2][2][2];
   private final float[][][] ao = new float[3][3][3];
   private final int[] packed = new int[7];
   private boolean full;
   private float shx = 0.0F;
   private float shy = 0.0F;
   private float shz = 0.0F;
   private int cachedTint = -1;
   private int cachedMultiplier = -1;

   public BlockInfo(BlockColors colors) {
      this.colors = colors;
   }

   public int getColorMultiplier(int tint) {
      if (this.cachedTint == tint) {
         return this.cachedMultiplier;
      } else {
         this.cachedTint = tint;
         this.cachedMultiplier = this.colors.func_228054_a_(this.state, this.world, this.blockPos, tint);
         return this.cachedMultiplier;
      }
   }

   public void updateShift() {
      Vec3d offset = this.state.getOffset(this.world, this.blockPos);
      this.shx = (float)offset.x;
      this.shy = (float)offset.y;
      this.shz = (float)offset.z;
   }

   public void setWorld(ILightReader world) {
      this.world = world;
      this.cachedTint = -1;
      this.cachedMultiplier = -1;
   }

   public void setState(BlockState state) {
      this.state = state;
      this.cachedTint = -1;
      this.cachedMultiplier = -1;
   }

   public void setBlockPos(BlockPos blockPos) {
      this.blockPos = blockPos;
      this.cachedTint = -1;
      this.cachedMultiplier = -1;
      this.shx = this.shy = this.shz = 0.0F;
   }

   public void reset() {
      this.world = null;
      this.state = null;
      this.blockPos = null;
      this.cachedTint = -1;
      this.cachedMultiplier = -1;
      this.shx = this.shy = this.shz = 0.0F;
   }

   private float combine(int c, int s1, int s2, int s3, boolean t0, boolean t1, boolean t2, boolean t3) {
      if (c == 0 && !t0) {
         c = Math.max(0, Math.max(s1, s2) - 1);
      }

      if (s1 == 0 && !t1) {
         s1 = Math.max(0, c - 1);
      }

      if (s2 == 0 && !t2) {
         s2 = Math.max(0, c - 1);
      }

      if (s3 == 0 && !t3) {
         s3 = Math.max(0, Math.max(s1, s2) - 1);
      }

      return (float)(c + s1 + s2 + s3) * 32.0F / 262140.0F;
   }

   public void updateLightMatrix() {
      int x;
      int y;
      int z;
      int z1;
      for(x = 0; x <= 2; ++x) {
         for(y = 0; y <= 2; ++y) {
            for(z = 0; z <= 2; ++z) {
               BlockPos pos = this.blockPos.add(x - 1, y - 1, z - 1);
               BlockState state = this.world.getBlockState(pos);
               this.t[x][y][z] = state.getOpacity(this.world, pos) < 15;
               z1 = 16711935;
               this.s[x][y][z] = z1 >> 20 & 15;
               this.b[x][y][z] = z1 >> 4 & 15;
               this.ao[x][y][z] = state.func_215703_d(this.world, pos);
            }
         }
      }

      Direction[] var28 = SIDES;
      y = var28.length;

      int sxz;
      int sxy;
      for(z = 0; z < y; ++z) {
         Direction side = var28[z];
         BlockPos pos = this.blockPos.offset(side);
         BlockState state = this.world.getBlockState(pos);
         BlockState thisStateShape = this.state.isSolid() && this.state.func_215691_g() ? this.state : Blocks.AIR.getDefaultState();
         BlockState otherStateShape = state.isSolid() && state.func_215691_g() ? state : Blocks.AIR.getDefaultState();
         if (state.getOpacity(this.world, this.blockPos) == 15 || VoxelShapes.func_223416_b(thisStateShape.func_215702_a(this.world, this.blockPos, side), otherStateShape.func_215702_a(this.world, pos, side.getOpposite()))) {
            int x = side.getXOffset() + 1;
            sxz = side.getYOffset() + 1;
            sxy = side.getZOffset() + 1;
            this.s[x][sxz][sxy] = Math.max(this.s[1][1][1] - 1, this.s[x][sxz][sxy]);
            this.b[x][sxz][sxy] = Math.max(this.b[1][1][1] - 1, this.b[x][sxz][sxy]);
         }
      }

      for(x = 0; x < 2; ++x) {
         for(y = 0; y < 2; ++y) {
            for(z = 0; z < 2; ++z) {
               int x1 = x * 2;
               int y1 = y * 2;
               z1 = z * 2;
               int sxyz = this.s[x1][y1][z1];
               int bxyz = this.b[x1][y1][z1];
               boolean txyz = this.t[x1][y1][z1];
               sxz = this.s[x1][1][z1];
               sxy = this.s[x1][y1][1];
               int syz = this.s[1][y1][z1];
               int bxz = this.b[x1][1][z1];
               int bxy = this.b[x1][y1][1];
               int byz = this.b[1][y1][z1];
               boolean txz = this.t[x1][1][z1];
               boolean txy = this.t[x1][y1][1];
               boolean tyz = this.t[1][y1][z1];
               int sx = this.s[x1][1][1];
               int sy = this.s[1][y1][1];
               int sz = this.s[1][1][z1];
               int bx = this.b[x1][1][1];
               int by = this.b[1][y1][1];
               int bz = this.b[1][1][z1];
               boolean tx = this.t[x1][1][1];
               boolean ty = this.t[1][y1][1];
               boolean tz = this.t[1][1][z1];
               this.skyLight[0][x][y][z] = this.combine(sx, sxz, sxy, !txz && !txy ? sx : sxyz, tx, txz, txy, !txz && !txy ? tx : txyz);
               this.blockLight[0][x][y][z] = this.combine(bx, bxz, bxy, !txz && !txy ? bx : bxyz, tx, txz, txy, !txz && !txy ? tx : txyz);
               this.skyLight[1][x][y][z] = this.combine(sy, sxy, syz, !txy && !tyz ? sy : sxyz, ty, txy, tyz, !txy && !tyz ? ty : txyz);
               this.blockLight[1][x][y][z] = this.combine(by, bxy, byz, !txy && !tyz ? by : bxyz, ty, txy, tyz, !txy && !tyz ? ty : txyz);
               this.skyLight[2][x][y][z] = this.combine(sz, syz, sxz, !tyz && !txz ? sz : sxyz, tz, tyz, txz, !tyz && !txz ? tz : txyz);
               this.blockLight[2][x][y][z] = this.combine(bz, byz, bxz, !tyz && !txz ? bz : bxyz, tz, tyz, txz, !tyz && !txz ? tz : txyz);
            }
         }
      }

   }

   public void updateFlatLighting() {
      this.full = Block.isOpaque(this.state.getCollisionShape(this.world, this.blockPos));
      this.packed[0] = 16711935;
      Direction[] var1 = SIDES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Direction side = var1[var3];
         int i = side.ordinal() + 1;
         this.packed[i] = 16711935;
      }

   }

   public ILightReader getWorld() {
      return this.world;
   }

   public BlockState getState() {
      return this.state;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public boolean[][][] getTranslucent() {
      return this.t;
   }

   public float[][][][] getSkyLight() {
      return this.skyLight;
   }

   public float[][][][] getBlockLight() {
      return this.blockLight;
   }

   public float[][][] getAo() {
      return this.ao;
   }

   public int[] getPackedLight() {
      return this.packed;
   }

   public boolean isFullCube() {
      return this.full;
   }

   public float getShx() {
      return this.shx;
   }

   public float getShy() {
      return this.shy;
   }

   public float getShz() {
      return this.shz;
   }

   public int getCachedTint() {
      return this.cachedTint;
   }

   public int getCachedMultiplier() {
      return this.cachedMultiplier;
   }
}
