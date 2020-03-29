package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UVTransformationUtil {
   private static final Logger field_229378_c_ = LogManager.getLogger();
   public static final EnumMap<Direction, TransformationMatrix> field_229376_a_ = (EnumMap)Util.make(Maps.newEnumMap(Direction.class), (p_229382_0_) -> {
      p_229382_0_.put(Direction.SOUTH, TransformationMatrix.func_227983_a_());
      p_229382_0_.put(Direction.EAST, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.WEST, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.NORTH, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.UP, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true), (Vector3f)null, (Quaternion)null));
      p_229382_0_.put(Direction.DOWN, new TransformationMatrix((Vector3f)null, new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90.0F, true), (Vector3f)null, (Quaternion)null));
   });
   public static final EnumMap<Direction, TransformationMatrix> field_229377_b_ = (EnumMap)Util.make(Maps.newEnumMap(Direction.class), (p_229381_0_) -> {
      Direction[] var1 = Direction.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Direction lvt_4_1_ = var1[var3];
         p_229381_0_.put(lvt_4_1_, ((TransformationMatrix)field_229376_a_.get(lvt_4_1_)).func_227987_b_());
      }

   });

   public static TransformationMatrix func_229379_a_(TransformationMatrix p_229379_0_) {
      Matrix4f lvt_1_1_ = Matrix4f.func_226599_b_(0.5F, 0.5F, 0.5F);
      lvt_1_1_.func_226595_a_(p_229379_0_.func_227988_c_());
      lvt_1_1_.func_226595_a_(Matrix4f.func_226599_b_(-0.5F, -0.5F, -0.5F));
      return new TransformationMatrix(lvt_1_1_);
   }

   public static TransformationMatrix func_229380_a_(TransformationMatrix p_229380_0_, Direction p_229380_1_, Supplier<String> p_229380_2_) {
      Direction lvt_3_1_ = Direction.func_229385_a_(p_229380_0_.func_227988_c_(), p_229380_1_);
      TransformationMatrix lvt_4_1_ = p_229380_0_.func_227987_b_();
      if (lvt_4_1_ == null) {
         field_229378_c_.warn((String)p_229380_2_.get());
         return new TransformationMatrix((Vector3f)null, (Quaternion)null, new Vector3f(0.0F, 0.0F, 0.0F), (Quaternion)null);
      } else {
         TransformationMatrix lvt_5_1_ = ((TransformationMatrix)field_229377_b_.get(p_229380_1_)).func_227985_a_(lvt_4_1_).func_227985_a_((TransformationMatrix)field_229376_a_.get(lvt_3_1_));
         return func_229379_a_(lvt_5_1_);
      }
   }
}
