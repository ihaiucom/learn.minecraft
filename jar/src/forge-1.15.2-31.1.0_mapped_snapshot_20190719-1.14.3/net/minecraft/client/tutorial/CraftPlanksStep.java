package net.minecraft.client.tutorial;

import java.util.Iterator;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CraftPlanksStep implements ITutorialStep {
   private static final ITextComponent TITLE = new TranslationTextComponent("tutorial.craft_planks.title", new Object[0]);
   private static final ITextComponent DESCRIPTION = new TranslationTextComponent("tutorial.craft_planks.description", new Object[0]);
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public CraftPlanksStep(Tutorial p_i47583_1_) {
      this.tutorial = p_i47583_1_;
   }

   public void tick() {
      ++this.timeWaiting;
      if (this.tutorial.getGameType() != GameType.SURVIVAL) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            ClientPlayerEntity lvt_1_1_ = this.tutorial.getMinecraft().player;
            if (lvt_1_1_ != null) {
               if (lvt_1_1_.inventory.hasTag(ItemTags.PLANKS)) {
                  this.tutorial.setStep(TutorialSteps.NONE);
                  return;
               }

               if (hasCrafted(lvt_1_1_, ItemTags.PLANKS)) {
                  this.tutorial.setStep(TutorialSteps.NONE);
                  return;
               }
            }
         }

         if (this.timeWaiting >= 1200 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.WOODEN_PLANKS, TITLE, DESCRIPTION, false);
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

   public void handleSetSlot(ItemStack p_193252_1_) {
      Item lvt_2_1_ = p_193252_1_.getItem();
      if (ItemTags.PLANKS.contains(lvt_2_1_)) {
         this.tutorial.setStep(TutorialSteps.NONE);
      }

   }

   public static boolean hasCrafted(ClientPlayerEntity p_199761_0_, Tag<Item> p_199761_1_) {
      Iterator var2 = p_199761_1_.getAllElements().iterator();

      Item lvt_3_1_;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         lvt_3_1_ = (Item)var2.next();
      } while(p_199761_0_.getStats().getValue(Stats.ITEM_CRAFTED.get(lvt_3_1_)) <= 0);

      return true;
   }
}
