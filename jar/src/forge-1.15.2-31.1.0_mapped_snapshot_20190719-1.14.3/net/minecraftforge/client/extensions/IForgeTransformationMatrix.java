package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.Direction;

public interface IForgeTransformationMatrix {
   default TransformationMatrix getTransformaion() {
      return (TransformationMatrix)this;
   }

   default boolean isIdentity() {
      return this.getTransformaion().equals(TransformationMatrix.func_227983_a_());
   }

   default void push(MatrixStack stack) {
      stack.func_227860_a_();
      Vector3f trans = this.getTransformaion().getTranslation();
      stack.func_227861_a_((double)trans.getX(), (double)trans.getY(), (double)trans.getZ());
      stack.func_227863_a_(this.getTransformaion().func_227989_d_());
      Vector3f scale = this.getTransformaion().getScale();
      stack.func_227862_a_(scale.getX(), scale.getY(), scale.getZ());
      stack.func_227863_a_(this.getTransformaion().getRightRot());
   }

   default TransformationMatrix compose(TransformationMatrix other) {
      if (this.getTransformaion().isIdentity()) {
         return other;
      } else if (other.isIdentity()) {
         return this.getTransformaion();
      } else {
         Matrix4f m = this.getTransformaion().func_227988_c_();
         m.func_226595_a_(other.func_227988_c_());
         return new TransformationMatrix(m);
      }
   }

   default TransformationMatrix inverse() {
      if (this.isIdentity()) {
         return this.getTransformaion();
      } else {
         Matrix4f m = this.getTransformaion().func_227988_c_().func_226601_d_();
         m.func_226600_c_();
         return new TransformationMatrix(m);
      }
   }

   default void transformPosition(Vector4f position) {
      position.func_229372_a_(this.getTransformaion().func_227988_c_());
   }

   default void transformNormal(Vector3f normal) {
      normal.func_229188_a_(this.getTransformaion().getNormalMatrix());
      normal.func_229194_d_();
   }

   default Direction rotateTransform(Direction facing) {
      return Direction.func_229385_a_(this.getTransformaion().func_227988_c_(), facing);
   }

   default TransformationMatrix blockCenterToCorner() {
      TransformationMatrix transform = this.getTransformaion();
      if (transform.isIdentity()) {
         return TransformationMatrix.func_227983_a_();
      } else {
         Matrix4f ret = transform.func_227988_c_();
         Matrix4f tmp = Matrix4f.func_226599_b_(0.5F, 0.5F, 0.5F);
         ret.multiplyBackward(tmp);
         tmp.func_226591_a_();
         tmp.setTranslation(-0.5F, -0.5F, -0.5F);
         ret.func_226595_a_(tmp);
         return new TransformationMatrix(ret);
      }
   }

   default TransformationMatrix blockCornerToCenter() {
      TransformationMatrix transform = this.getTransformaion();
      if (transform.isIdentity()) {
         return TransformationMatrix.func_227983_a_();
      } else {
         Matrix4f ret = transform.func_227988_c_();
         Matrix4f tmp = Matrix4f.func_226599_b_(-0.5F, -0.5F, -0.5F);
         ret.multiplyBackward(tmp);
         tmp.func_226591_a_();
         tmp.setTranslation(0.5F, 0.5F, 0.5F);
         ret.func_226595_a_(tmp);
         return new TransformationMatrix(ret);
      }
   }
}
