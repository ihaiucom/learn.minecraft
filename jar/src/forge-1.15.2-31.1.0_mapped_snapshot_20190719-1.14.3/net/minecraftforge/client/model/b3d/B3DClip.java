package net.minecraftforge.client.model.b3d;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.model.animation.IJoint;
import net.minecraftforge.common.model.animation.IJointClip;
import net.minecraftforge.common.model.animation.JointClips;

public enum B3DClip implements IClip {
   INSTANCE;

   public IJointClip apply(IJoint joint) {
      return (IJointClip)(!(joint instanceof B3DLoader.NodeJoint) ? JointClips.IdentityJointClip.INSTANCE : new B3DClip.NodeClip(((B3DLoader.NodeJoint)joint).getNode()));
   }

   public Iterable<Event> pastEvents(float lastPollTime, float time) {
      return ImmutableSet.of();
   }

   protected static class NodeClip implements IJointClip {
      private final B3DModel.Node<?> node;

      public NodeClip(B3DModel.Node<?> node) {
         this.node = node;
      }

      public TransformationMatrix apply(float time) {
         TransformationMatrix ret = TransformationMatrix.func_227983_a_();
         if (this.node.getAnimation() == null) {
            return ret.compose(new TransformationMatrix(this.node.getPos(), this.node.getRot(), this.node.getScale(), (Quaternion)null));
         } else {
            int start = Math.max(1, (int)Math.round(Math.floor((double)time)));
            int end = Math.min(start + 1, (int)Math.round(Math.ceil((double)time)));
            float progress = time - (float)Math.floor((double)time);
            B3DModel.Key keyStart = (B3DModel.Key)this.node.getAnimation().getKeys().get(start, this.node);
            B3DModel.Key keyEnd = (B3DModel.Key)this.node.getAnimation().getKeys().get(end, this.node);
            TransformationMatrix startTr = keyStart == null ? null : new TransformationMatrix(keyStart.getPos(), keyStart.getRot(), keyStart.getScale(), (Quaternion)null);
            TransformationMatrix endTr = keyEnd == null ? null : new TransformationMatrix(keyEnd.getPos(), keyEnd.getRot(), keyEnd.getScale(), (Quaternion)null);
            if (keyStart == null) {
               if (keyEnd == null) {
                  ret = ret.compose(new TransformationMatrix(this.node.getPos(), this.node.getRot(), this.node.getScale(), (Quaternion)null));
               } else {
                  ret = ret.compose(endTr);
               }
            } else if ((double)progress >= 1.0E-5D && keyEnd != null) {
               ret = ret.compose(TransformationHelper.slerp(startTr, endTr, progress));
            } else {
               ret = ret.compose(startTr);
            }

            return ret;
         }
      }
   }
}
