package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldBorder {
   private final List<IBorderListener> listeners = Lists.newArrayList();
   private double damagePerBlock = 0.2D;
   private double damageBuffer = 5.0D;
   private int warningTime = 15;
   private int warningDistance = 5;
   private double centerX;
   private double centerZ;
   private int worldSize = 29999984;
   private WorldBorder.IBorderInfo state = new WorldBorder.StationaryBorderInfo(6.0E7D);

   public boolean contains(BlockPos p_177746_1_) {
      return (double)(p_177746_1_.getX() + 1) > this.minX() && (double)p_177746_1_.getX() < this.maxX() && (double)(p_177746_1_.getZ() + 1) > this.minZ() && (double)p_177746_1_.getZ() < this.maxZ();
   }

   public boolean contains(ChunkPos p_177730_1_) {
      return (double)p_177730_1_.getXEnd() > this.minX() && (double)p_177730_1_.getXStart() < this.maxX() && (double)p_177730_1_.getZEnd() > this.minZ() && (double)p_177730_1_.getZStart() < this.maxZ();
   }

   public boolean contains(AxisAlignedBB p_177743_1_) {
      return p_177743_1_.maxX > this.minX() && p_177743_1_.minX < this.maxX() && p_177743_1_.maxZ > this.minZ() && p_177743_1_.minZ < this.maxZ();
   }

   public double getClosestDistance(Entity p_177745_1_) {
      return this.getClosestDistance(p_177745_1_.func_226277_ct_(), p_177745_1_.func_226281_cx_());
   }

   public VoxelShape getShape() {
      return this.state.getShape();
   }

   public double getClosestDistance(double p_177729_1_, double p_177729_3_) {
      double d0 = p_177729_3_ - this.minZ();
      double d1 = this.maxZ() - p_177729_3_;
      double d2 = p_177729_1_ - this.minX();
      double d3 = this.maxX() - p_177729_1_;
      double d4 = Math.min(d2, d3);
      d4 = Math.min(d4, d0);
      return Math.min(d4, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public BorderStatus getStatus() {
      return this.state.getStatus();
   }

   public double minX() {
      return this.state.getMinX();
   }

   public double minZ() {
      return this.state.getMinZ();
   }

   public double maxX() {
      return this.state.getMaxX();
   }

   public double maxZ() {
      return this.state.getMaxZ();
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double p_177739_1_, double p_177739_3_) {
      this.centerX = p_177739_1_;
      this.centerZ = p_177739_3_;
      this.state.onCenterChanged();
      Iterator var5 = this.getListeners().iterator();

      while(var5.hasNext()) {
         IBorderListener iborderlistener = (IBorderListener)var5.next();
         iborderlistener.onCenterChanged(this, p_177739_1_, p_177739_3_);
      }

   }

   public double getDiameter() {
      return this.state.getSize();
   }

   public long getTimeUntilTarget() {
      return this.state.getTimeUntilTarget();
   }

   public double getTargetSize() {
      return this.state.getTargetSize();
   }

   public void setTransition(double p_177750_1_) {
      this.state = new WorldBorder.StationaryBorderInfo(p_177750_1_);
      Iterator var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
         IBorderListener iborderlistener = (IBorderListener)var3.next();
         iborderlistener.onSizeChanged(this, p_177750_1_);
      }

   }

   public void setTransition(double p_177738_1_, double p_177738_3_, long p_177738_5_) {
      this.state = (WorldBorder.IBorderInfo)(p_177738_1_ == p_177738_3_ ? new WorldBorder.StationaryBorderInfo(p_177738_3_) : new WorldBorder.MovingBorderInfo(p_177738_1_, p_177738_3_, p_177738_5_));
      Iterator var7 = this.getListeners().iterator();

      while(var7.hasNext()) {
         IBorderListener iborderlistener = (IBorderListener)var7.next();
         iborderlistener.onTransitionStarted(this, p_177738_1_, p_177738_3_, p_177738_5_);
      }

   }

   protected List<IBorderListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(IBorderListener p_177737_1_) {
      this.listeners.add(p_177737_1_);
   }

   public void removeListener(IBorderListener p_removeListener_1_) {
      this.listeners.remove(p_removeListener_1_);
   }

   public void setSize(int p_177725_1_) {
      this.worldSize = p_177725_1_;
      this.state.onSizeChanged();
   }

   public int getSize() {
      return this.worldSize;
   }

   public double getDamageBuffer() {
      return this.damageBuffer;
   }

   public void setDamageBuffer(double p_177724_1_) {
      this.damageBuffer = p_177724_1_;
      Iterator var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
         IBorderListener iborderlistener = (IBorderListener)var3.next();
         iborderlistener.onDamageBufferChanged(this, p_177724_1_);
      }

   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double p_177744_1_) {
      this.damagePerBlock = p_177744_1_;
      Iterator var3 = this.getListeners().iterator();

      while(var3.hasNext()) {
         IBorderListener iborderlistener = (IBorderListener)var3.next();
         iborderlistener.onDamageAmountChanged(this, p_177744_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public double getResizeSpeed() {
      return this.state.getResizeSpeed();
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int p_177723_1_) {
      this.warningTime = p_177723_1_;
      Iterator var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
         IBorderListener iborderlistener = (IBorderListener)var2.next();
         iborderlistener.onWarningTimeChanged(this, p_177723_1_);
      }

   }

   public int getWarningDistance() {
      return this.warningDistance;
   }

   public void setWarningDistance(int p_177747_1_) {
      this.warningDistance = p_177747_1_;
      Iterator var2 = this.getListeners().iterator();

      while(var2.hasNext()) {
         IBorderListener iborderlistener = (IBorderListener)var2.next();
         iborderlistener.onWarningDistanceChanged(this, p_177747_1_);
      }

   }

   public void tick() {
      this.state = this.state.tick();
   }

   public void copyTo(WorldInfo p_222520_1_) {
      p_222520_1_.setBorderSize(this.getDiameter());
      p_222520_1_.setBorderCenterX(this.getCenterX());
      p_222520_1_.setBorderCenterZ(this.getCenterZ());
      p_222520_1_.setBorderSafeZone(this.getDamageBuffer());
      p_222520_1_.setBorderDamagePerBlock(this.getDamagePerBlock());
      p_222520_1_.setBorderWarningBlocks(this.getWarningDistance());
      p_222520_1_.setBorderWarningTime(this.getWarningTime());
      p_222520_1_.setBorderSizeLerpTarget(this.getTargetSize());
      p_222520_1_.setBorderSizeLerpTime(this.getTimeUntilTarget());
   }

   public void copyFrom(WorldInfo p_222519_1_) {
      this.setCenter(p_222519_1_.getBorderCenterX(), p_222519_1_.getBorderCenterZ());
      this.setDamagePerBlock(p_222519_1_.getBorderDamagePerBlock());
      this.setDamageBuffer(p_222519_1_.getBorderSafeZone());
      this.setWarningDistance(p_222519_1_.getBorderWarningBlocks());
      this.setWarningTime(p_222519_1_.getBorderWarningTime());
      if (p_222519_1_.getBorderSizeLerpTime() > 0L) {
         this.setTransition(p_222519_1_.getBorderSize(), p_222519_1_.getBorderSizeLerpTarget(), p_222519_1_.getBorderSizeLerpTime());
      } else {
         this.setTransition(p_222519_1_.getBorderSize());
      }

   }

   class StationaryBorderInfo implements WorldBorder.IBorderInfo {
      private final double size;
      private double minX;
      private double minZ;
      private double maxX;
      private double maxZ;
      private VoxelShape field_222518_g;

      public StationaryBorderInfo(double p_i49837_2_) {
         this.size = p_i49837_2_;
         this.func_212665_m();
      }

      public double getMinX() {
         return this.minX;
      }

      public double getMaxX() {
         return this.maxX;
      }

      public double getMinZ() {
         return this.minZ;
      }

      public double getMaxZ() {
         return this.maxZ;
      }

      public double getSize() {
         return this.size;
      }

      @OnlyIn(Dist.CLIENT)
      public BorderStatus getStatus() {
         return BorderStatus.STATIONARY;
      }

      @OnlyIn(Dist.CLIENT)
      public double getResizeSpeed() {
         return 0.0D;
      }

      public long getTimeUntilTarget() {
         return 0L;
      }

      public double getTargetSize() {
         return this.size;
      }

      private void func_212665_m() {
         this.minX = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0D, (double)(-WorldBorder.this.worldSize));
         this.minZ = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0D, (double)(-WorldBorder.this.worldSize));
         this.maxX = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0D, (double)WorldBorder.this.worldSize);
         this.maxZ = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0D, (double)WorldBorder.this.worldSize);
         this.field_222518_g = VoxelShapes.combineAndSimplify(VoxelShapes.INFINITY, VoxelShapes.create(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), IBooleanFunction.ONLY_FIRST);
      }

      public void onSizeChanged() {
         this.func_212665_m();
      }

      public void onCenterChanged() {
         this.func_212665_m();
      }

      public WorldBorder.IBorderInfo tick() {
         return this;
      }

      public VoxelShape getShape() {
         return this.field_222518_g;
      }
   }

   class MovingBorderInfo implements WorldBorder.IBorderInfo {
      private final double oldSize;
      private final double newSize;
      private final long endTime;
      private final long startTime;
      private final double transitionTime;

      private MovingBorderInfo(double p_i49838_2_, double p_i49838_4_, long p_i49838_6_) {
         this.oldSize = p_i49838_2_;
         this.newSize = p_i49838_4_;
         this.transitionTime = (double)p_i49838_6_;
         this.startTime = Util.milliTime();
         this.endTime = this.startTime + p_i49838_6_;
      }

      public double getMinX() {
         return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0D, (double)(-WorldBorder.this.worldSize));
      }

      public double getMinZ() {
         return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0D, (double)(-WorldBorder.this.worldSize));
      }

      public double getMaxX() {
         return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0D, (double)WorldBorder.this.worldSize);
      }

      public double getMaxZ() {
         return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0D, (double)WorldBorder.this.worldSize);
      }

      public double getSize() {
         double d0 = (double)(Util.milliTime() - this.startTime) / this.transitionTime;
         return d0 < 1.0D ? MathHelper.lerp(d0, this.oldSize, this.newSize) : this.newSize;
      }

      @OnlyIn(Dist.CLIENT)
      public double getResizeSpeed() {
         return Math.abs(this.oldSize - this.newSize) / (double)(this.endTime - this.startTime);
      }

      public long getTimeUntilTarget() {
         return this.endTime - Util.milliTime();
      }

      public double getTargetSize() {
         return this.newSize;
      }

      @OnlyIn(Dist.CLIENT)
      public BorderStatus getStatus() {
         return this.newSize < this.oldSize ? BorderStatus.SHRINKING : BorderStatus.GROWING;
      }

      public void onCenterChanged() {
      }

      public void onSizeChanged() {
      }

      public WorldBorder.IBorderInfo tick() {
         return (WorldBorder.IBorderInfo)(this.getTimeUntilTarget() <= 0L ? WorldBorder.this.new StationaryBorderInfo(this.newSize) : this);
      }

      public VoxelShape getShape() {
         return VoxelShapes.combineAndSimplify(VoxelShapes.INFINITY, VoxelShapes.create(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), IBooleanFunction.ONLY_FIRST);
      }

      // $FF: synthetic method
      MovingBorderInfo(double p_i49839_2_, double p_i49839_4_, long p_i49839_6_, Object p_i49839_8_) {
         this(p_i49839_2_, p_i49839_4_, p_i49839_6_);
      }
   }

   interface IBorderInfo {
      double getMinX();

      double getMaxX();

      double getMinZ();

      double getMaxZ();

      double getSize();

      @OnlyIn(Dist.CLIENT)
      double getResizeSpeed();

      long getTimeUntilTarget();

      double getTargetSize();

      @OnlyIn(Dist.CLIENT)
      BorderStatus getStatus();

      void onSizeChanged();

      void onCenterChanged();

      WorldBorder.IBorderInfo tick();

      VoxelShape getShape();
   }
}
