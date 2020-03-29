package net.minecraft.client.tutorial;

import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PunchTreeStep implements ITutorialStep {
   private static final ITextComponent TITLE = new TranslationTextComponent("tutorial.punch_tree.title", new Object[0]);
   private static final ITextComponent DESCRIPTION = new TranslationTextComponent("tutorial.punch_tree.description", new Object[]{Tutorial.createKeybindComponent("attack")});
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;
   private int resetCount;

   public PunchTreeStep(Tutorial p_i47579_1_) {
      this.tutorial = p_i47579_1_;
   }

   public void tick() {
      ++this.timeWaiting;
      if (this.tutorial.getGameType() != GameType.SURVIVAL) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            ClientPlayerEntity lvt_1_1_ = this.tutorial.getMinecraft().player;
            if (lvt_1_1_ != null) {
               if (lvt_1_1_.inventory.hasTag(ItemTags.LOGS)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }

               if (FindTreeStep.hasPunchedTreesPreviously(lvt_1_1_)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if ((this.timeWaiting >= 600 || this.resetCount > 3) && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, true);
            this.tutorial.getMinecraft().getToastGui().add(this.toast);
         }

      }
   }

   public void onStop() {
      if (this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   public void onHitBlock(ClientWorld p_193250_1_, BlockPos p_193250_2_, BlockState p_193250_3_, float p_193250_4_) {
      boolean lvt_5_1_ = p_193250_3_.isIn(BlockTags.LOGS);
      if (lvt_5_1_ && p_193250_4_ > 0.0F) {
         if (this.toast != null) {
            this.toast.setProgress(p_193250_4_);
         }

         if (p_193250_4_ >= 1.0F) {
            this.tutorial.setStep(TutorialSteps.OPEN_INVENTORY);
         }
      } else if (this.toast != null) {
         this.toast.setProgress(0.0F);
      } else if (lvt_5_1_) {
         ++this.resetCount;
      }

   }

   public void handleSetSlot(ItemStack p_193252_1_) {
      if (ItemTags.LOGS.contains(p_193252_1_.getItem())) {
         this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
      }
   }
}
