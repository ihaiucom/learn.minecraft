package net.minecraft.pathfinding;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public abstract class NodeProcessor {
   protected Region blockaccess;
   protected MobEntity entity;
   protected final Int2ObjectMap<PathPoint> pointMap = new Int2ObjectOpenHashMap();
   protected int entitySizeX;
   protected int entitySizeY;
   protected int entitySizeZ;
   protected boolean canEnterDoors;
   protected boolean canOpenDoors;
   protected boolean canSwim;

   public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_) {
      this.blockaccess = p_225578_1_;
      this.entity = p_225578_2_;
      this.pointMap.clear();
      this.entitySizeX = MathHelper.floor(p_225578_2_.getWidth() + 1.0F);
      this.entitySizeY = MathHelper.floor(p_225578_2_.getHeight() + 1.0F);
      this.entitySizeZ = MathHelper.floor(p_225578_2_.getWidth() + 1.0F);
   }

   public void postProcess() {
      this.blockaccess = null;
      this.entity = null;
   }

   protected PathPoint openPoint(int p_176159_1_, int p_176159_2_, int p_176159_3_) {
      return (PathPoint)this.pointMap.computeIfAbsent(PathPoint.makeHash(p_176159_1_, p_176159_2_, p_176159_3_), (p_215743_3_) -> {
         return new PathPoint(p_176159_1_, p_176159_2_, p_176159_3_);
      });
   }

   public abstract PathPoint getStart();

   public abstract FlaggedPathPoint func_224768_a(double var1, double var3, double var5);

   public abstract int func_222859_a(PathPoint[] var1, PathPoint var2);

   public abstract PathNodeType getPathNodeType(IBlockReader var1, int var2, int var3, int var4, MobEntity var5, int var6, int var7, int var8, boolean var9, boolean var10);

   public abstract PathNodeType getPathNodeType(IBlockReader var1, int var2, int var3, int var4);

   public void setCanEnterDoors(boolean p_186317_1_) {
      this.canEnterDoors = p_186317_1_;
   }

   public void setCanOpenDoors(boolean p_186321_1_) {
      this.canOpenDoors = p_186321_1_;
   }

   public void setCanSwim(boolean p_186316_1_) {
      this.canSwim = p_186316_1_;
   }

   public boolean getCanEnterDoors() {
      return this.canEnterDoors;
   }

   public boolean getCanOpenDoors() {
      return this.canOpenDoors;
   }

   public boolean getCanSwim() {
      return this.canSwim;
   }
}
