package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends AbstractMinecartEntity {
   private static final DataParameter<Boolean> POWERED;
   private int fuel;
   public double pushX;
   public double pushZ;
   private static final Ingredient field_195407_e;

   public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> p_i50119_1_, World p_i50119_2_) {
      super(p_i50119_1_, p_i50119_2_);
   }

   public FurnaceMinecartEntity(World p_i1719_1_, double p_i1719_2_, double p_i1719_4_, double p_i1719_6_) {
      super(EntityType.FURNACE_MINECART, p_i1719_1_, p_i1719_2_, p_i1719_4_, p_i1719_6_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.FURNACE;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(POWERED, false);
   }

   public void tick() {
      super.tick();
      if (!this.world.isRemote()) {
         if (this.fuel > 0) {
            --this.fuel;
         }

         if (this.fuel <= 0) {
            this.pushX = 0.0D;
            this.pushZ = 0.0D;
         }

         this.setMinecartPowered(this.fuel > 0);
      }

      if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
         this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.func_226277_ct_(), this.func_226278_cu_() + 0.8D, this.func_226281_cx_(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected double getMaximumSpeed() {
      return 0.2D;
   }

   public void killMinecart(DamageSource p_94095_1_) {
      super.killMinecart(p_94095_1_);
      if (!p_94095_1_.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         this.entityDropItem(Blocks.FURNACE);
      }

   }

   protected void moveAlongTrack(BlockPos p_180460_1_, BlockState p_180460_2_) {
      double d0 = 1.0E-4D;
      double d1 = 0.001D;
      super.moveAlongTrack(p_180460_1_, p_180460_2_);
      Vec3d vec3d = this.getMotion();
      double d2 = func_213296_b(vec3d);
      double d3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d3 > 1.0E-4D && d2 > 0.001D) {
         double d4 = (double)MathHelper.sqrt(d2);
         double d5 = (double)MathHelper.sqrt(d3);
         this.pushX = vec3d.x / d4 * d5;
         this.pushZ = vec3d.z / d4 * d5;
      }

   }

   protected void applyDrag() {
      double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d0 > 1.0E-7D) {
         d0 = (double)MathHelper.sqrt(d0);
         this.pushX /= d0;
         this.pushZ /= d0;
         this.setMotion(this.getMotion().mul(0.8D, 0.0D, 0.8D).add(this.pushX, 0.0D, this.pushZ));
      } else {
         this.setMotion(this.getMotion().mul(0.98D, 0.0D, 0.98D));
      }

      super.applyDrag();
   }

   public boolean processInitialInteract(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      if (super.processInitialInteract(p_184230_1_, p_184230_2_)) {
         return true;
      } else {
         ItemStack itemstack = p_184230_1_.getHeldItem(p_184230_2_);
         if (field_195407_e.test(itemstack) && this.fuel + 3600 <= 32000) {
            if (!p_184230_1_.abilities.isCreativeMode) {
               itemstack.shrink(1);
            }

            this.fuel += 3600;
         }

         if (this.fuel > 0) {
            this.pushX = this.func_226277_ct_() - p_184230_1_.func_226277_ct_();
            this.pushZ = this.func_226281_cx_() - p_184230_1_.func_226281_cx_();
         }

         return true;
      }
   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putDouble("PushX", this.pushX);
      p_213281_1_.putDouble("PushZ", this.pushZ);
      p_213281_1_.putShort("Fuel", (short)this.fuel);
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.pushX = p_70037_1_.getDouble("PushX");
      this.pushZ = p_70037_1_.getDouble("PushZ");
      this.fuel = p_70037_1_.getShort("Fuel");
   }

   protected boolean isMinecartPowered() {
      return (Boolean)this.dataManager.get(POWERED);
   }

   protected void setMinecartPowered(boolean p_94107_1_) {
      this.dataManager.set(POWERED, p_94107_1_);
   }

   public BlockState getDefaultDisplayTile() {
      return (BlockState)((BlockState)Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, Direction.NORTH)).with(FurnaceBlock.LIT, this.isMinecartPowered());
   }

   static {
      POWERED = EntityDataManager.createKey(FurnaceMinecartEntity.class, DataSerializers.BOOLEAN);
      field_195407_e = Ingredient.fromItems(Items.COAL, Items.CHARCOAL);
   }
}
