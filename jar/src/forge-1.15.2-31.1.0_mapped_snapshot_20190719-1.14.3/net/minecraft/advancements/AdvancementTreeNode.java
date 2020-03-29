package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class AdvancementTreeNode {
   private final Advancement advancement;
   private final AdvancementTreeNode parent;
   private final AdvancementTreeNode sibling;
   private final int index;
   private final List<AdvancementTreeNode> children = Lists.newArrayList();
   private AdvancementTreeNode ancestor;
   private AdvancementTreeNode thread;
   private int x;
   private float y;
   private float mod;
   private float change;
   private float shift;

   public AdvancementTreeNode(Advancement p_i47466_1_, @Nullable AdvancementTreeNode p_i47466_2_, @Nullable AdvancementTreeNode p_i47466_3_, int p_i47466_4_, int p_i47466_5_) {
      if (p_i47466_1_.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position an invisible advancement!");
      } else {
         this.advancement = p_i47466_1_;
         this.parent = p_i47466_2_;
         this.sibling = p_i47466_3_;
         this.index = p_i47466_4_;
         this.ancestor = this;
         this.x = p_i47466_5_;
         this.y = -1.0F;
         AdvancementTreeNode lvt_6_1_ = null;

         Advancement lvt_8_1_;
         for(Iterator var7 = p_i47466_1_.getChildren().iterator(); var7.hasNext(); lvt_6_1_ = this.buildSubTree(lvt_8_1_, lvt_6_1_)) {
            lvt_8_1_ = (Advancement)var7.next();
         }

      }
   }

   @Nullable
   private AdvancementTreeNode buildSubTree(Advancement p_192322_1_, @Nullable AdvancementTreeNode p_192322_2_) {
      Advancement lvt_4_1_;
      if (p_192322_1_.getDisplay() != null) {
         p_192322_2_ = new AdvancementTreeNode(p_192322_1_, this, p_192322_2_, this.children.size() + 1, this.x + 1);
         this.children.add(p_192322_2_);
      } else {
         for(Iterator var3 = p_192322_1_.getChildren().iterator(); var3.hasNext(); p_192322_2_ = this.buildSubTree(lvt_4_1_, p_192322_2_)) {
            lvt_4_1_ = (Advancement)var3.next();
         }
      }

      return p_192322_2_;
   }

   private void firstWalk() {
      if (this.children.isEmpty()) {
         if (this.sibling != null) {
            this.y = this.sibling.y + 1.0F;
         } else {
            this.y = 0.0F;
         }

      } else {
         AdvancementTreeNode lvt_1_1_ = null;

         AdvancementTreeNode lvt_3_1_;
         for(Iterator var2 = this.children.iterator(); var2.hasNext(); lvt_1_1_ = lvt_3_1_.apportion(lvt_1_1_ == null ? lvt_3_1_ : lvt_1_1_)) {
            lvt_3_1_ = (AdvancementTreeNode)var2.next();
            lvt_3_1_.firstWalk();
         }

         this.executeShifts();
         float lvt_2_1_ = (((AdvancementTreeNode)this.children.get(0)).y + ((AdvancementTreeNode)this.children.get(this.children.size() - 1)).y) / 2.0F;
         if (this.sibling != null) {
            this.y = this.sibling.y + 1.0F;
            this.mod = this.y - lvt_2_1_;
         } else {
            this.y = lvt_2_1_;
         }

      }
   }

   private float secondWalk(float p_192319_1_, int p_192319_2_, float p_192319_3_) {
      this.y += p_192319_1_;
      this.x = p_192319_2_;
      if (this.y < p_192319_3_) {
         p_192319_3_ = this.y;
      }

      AdvancementTreeNode lvt_5_1_;
      for(Iterator var4 = this.children.iterator(); var4.hasNext(); p_192319_3_ = lvt_5_1_.secondWalk(p_192319_1_ + this.mod, p_192319_2_ + 1, p_192319_3_)) {
         lvt_5_1_ = (AdvancementTreeNode)var4.next();
      }

      return p_192319_3_;
   }

   private void thirdWalk(float p_192318_1_) {
      this.y += p_192318_1_;
      Iterator var2 = this.children.iterator();

      while(var2.hasNext()) {
         AdvancementTreeNode lvt_3_1_ = (AdvancementTreeNode)var2.next();
         lvt_3_1_.thirdWalk(p_192318_1_);
      }

   }

   private void executeShifts() {
      float lvt_1_1_ = 0.0F;
      float lvt_2_1_ = 0.0F;

      for(int lvt_3_1_ = this.children.size() - 1; lvt_3_1_ >= 0; --lvt_3_1_) {
         AdvancementTreeNode lvt_4_1_ = (AdvancementTreeNode)this.children.get(lvt_3_1_);
         lvt_4_1_.y += lvt_1_1_;
         lvt_4_1_.mod += lvt_1_1_;
         lvt_2_1_ += lvt_4_1_.change;
         lvt_1_1_ += lvt_4_1_.shift + lvt_2_1_;
      }

   }

   @Nullable
   private AdvancementTreeNode getFirstChild() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? (AdvancementTreeNode)this.children.get(0) : null;
      }
   }

   @Nullable
   private AdvancementTreeNode getLastChild() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? (AdvancementTreeNode)this.children.get(this.children.size() - 1) : null;
      }
   }

   private AdvancementTreeNode apportion(AdvancementTreeNode p_192324_1_) {
      if (this.sibling == null) {
         return p_192324_1_;
      } else {
         AdvancementTreeNode lvt_2_1_ = this;
         AdvancementTreeNode lvt_3_1_ = this;
         AdvancementTreeNode lvt_4_1_ = this.sibling;
         AdvancementTreeNode lvt_5_1_ = (AdvancementTreeNode)this.parent.children.get(0);
         float lvt_6_1_ = this.mod;
         float lvt_7_1_ = this.mod;
         float lvt_8_1_ = lvt_4_1_.mod;

         float lvt_9_1_;
         for(lvt_9_1_ = lvt_5_1_.mod; lvt_4_1_.getLastChild() != null && lvt_2_1_.getFirstChild() != null; lvt_7_1_ += lvt_3_1_.mod) {
            lvt_4_1_ = lvt_4_1_.getLastChild();
            lvt_2_1_ = lvt_2_1_.getFirstChild();
            lvt_5_1_ = lvt_5_1_.getFirstChild();
            lvt_3_1_ = lvt_3_1_.getLastChild();
            lvt_3_1_.ancestor = this;
            float lvt_10_1_ = lvt_4_1_.y + lvt_8_1_ - (lvt_2_1_.y + lvt_6_1_) + 1.0F;
            if (lvt_10_1_ > 0.0F) {
               lvt_4_1_.getAncestor(this, p_192324_1_).moveSubtree(this, lvt_10_1_);
               lvt_6_1_ += lvt_10_1_;
               lvt_7_1_ += lvt_10_1_;
            }

            lvt_8_1_ += lvt_4_1_.mod;
            lvt_6_1_ += lvt_2_1_.mod;
            lvt_9_1_ += lvt_5_1_.mod;
         }

         if (lvt_4_1_.getLastChild() != null && lvt_3_1_.getLastChild() == null) {
            lvt_3_1_.thread = lvt_4_1_.getLastChild();
            lvt_3_1_.mod += lvt_8_1_ - lvt_7_1_;
         } else {
            if (lvt_2_1_.getFirstChild() != null && lvt_5_1_.getFirstChild() == null) {
               lvt_5_1_.thread = lvt_2_1_.getFirstChild();
               lvt_5_1_.mod += lvt_6_1_ - lvt_9_1_;
            }

            p_192324_1_ = this;
         }

         return p_192324_1_;
      }
   }

   private void moveSubtree(AdvancementTreeNode p_192316_1_, float p_192316_2_) {
      float lvt_3_1_ = (float)(p_192316_1_.index - this.index);
      if (lvt_3_1_ != 0.0F) {
         p_192316_1_.change -= p_192316_2_ / lvt_3_1_;
         this.change += p_192316_2_ / lvt_3_1_;
      }

      p_192316_1_.shift += p_192316_2_;
      p_192316_1_.y += p_192316_2_;
      p_192316_1_.mod += p_192316_2_;
   }

   private AdvancementTreeNode getAncestor(AdvancementTreeNode p_192326_1_, AdvancementTreeNode p_192326_2_) {
      return this.ancestor != null && p_192326_1_.parent.children.contains(this.ancestor) ? this.ancestor : p_192326_2_;
   }

   private void updatePosition() {
      if (this.advancement.getDisplay() != null) {
         this.advancement.getDisplay().setPosition((float)this.x, this.y);
      }

      if (!this.children.isEmpty()) {
         Iterator var1 = this.children.iterator();

         while(var1.hasNext()) {
            AdvancementTreeNode lvt_2_1_ = (AdvancementTreeNode)var1.next();
            lvt_2_1_.updatePosition();
         }
      }

   }

   public static void layout(Advancement p_192323_0_) {
      if (p_192323_0_.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position children of an invisible root!");
      } else {
         AdvancementTreeNode lvt_1_1_ = new AdvancementTreeNode(p_192323_0_, (AdvancementTreeNode)null, (AdvancementTreeNode)null, 1, 0);
         lvt_1_1_.firstWalk();
         float lvt_2_1_ = lvt_1_1_.secondWalk(0.0F, 0, lvt_1_1_.y);
         if (lvt_2_1_ < 0.0F) {
            lvt_1_1_.thirdWalk(-lvt_2_1_);
         }

         lvt_1_1_.updatePosition();
      }
   }
}
