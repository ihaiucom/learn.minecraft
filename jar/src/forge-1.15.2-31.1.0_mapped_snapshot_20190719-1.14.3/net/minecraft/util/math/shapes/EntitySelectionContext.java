package net.minecraft.util.math.shapes;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class EntitySelectionContext implements ISelectionContext {
   protected static final ISelectionContext DUMMY;
   private final boolean field_227579_b_;
   private final double field_216381_c;
   private final Item item;
   @Nullable
   private final Entity entity;

   protected EntitySelectionContext(boolean p_i51181_1_, double p_i51181_2_, Item p_i51181_4_) {
      this((Entity)null, p_i51181_1_, p_i51181_2_, p_i51181_4_);
   }

   protected EntitySelectionContext(@Nullable Entity p_i230099_1_, boolean p_i230099_2_, double p_i230099_3_, Item p_i230099_5_) {
      this.entity = p_i230099_1_;
      this.field_227579_b_ = p_i230099_2_;
      this.field_216381_c = p_i230099_3_;
      this.item = p_i230099_5_;
   }

   /** @deprecated */
   @Deprecated
   protected EntitySelectionContext(Entity p_i51182_1_) {
      this(p_i51182_1_, p_i51182_1_.func_226274_bn_(), p_i51182_1_.func_226278_cu_(), p_i51182_1_ instanceof LivingEntity ? ((LivingEntity)p_i51182_1_).getHeldItemMainhand().getItem() : Items.AIR);
   }

   public boolean hasItem(Item p_216375_1_) {
      return this.item == p_216375_1_;
   }

   public boolean func_225581_b_() {
      return this.field_227579_b_;
   }

   public boolean func_216378_a(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_) {
      return this.field_216381_c > (double)p_216378_2_.getY() + p_216378_1_.getEnd(Direction.Axis.Y) - 9.999999747378752E-6D;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   static {
      DUMMY = new EntitySelectionContext(false, -1.7976931348623157E308D, Items.AIR) {
         public boolean func_216378_a(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_) {
            return p_216378_3_;
         }
      };
   }
}
