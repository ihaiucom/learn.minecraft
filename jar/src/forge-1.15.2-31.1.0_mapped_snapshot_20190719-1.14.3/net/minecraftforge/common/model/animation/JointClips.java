package net.minecraftforge.common.model.animation;

import net.minecraft.client.renderer.TransformationMatrix;

public final class JointClips {
   public static class NodeJointClip implements IJointClip {
      private final IJoint child;
      private final IClip clip;

      public NodeJointClip(IJoint joint, IClip clip) {
         this.child = joint;
         this.clip = clip;
      }

      public TransformationMatrix apply(float time) {
         return this.clip.apply(this.child).apply(time);
      }
   }

   public static enum IdentityJointClip implements IJointClip {
      INSTANCE;

      public TransformationMatrix apply(float time) {
         return TransformationMatrix.func_227983_a_();
      }
   }
}
