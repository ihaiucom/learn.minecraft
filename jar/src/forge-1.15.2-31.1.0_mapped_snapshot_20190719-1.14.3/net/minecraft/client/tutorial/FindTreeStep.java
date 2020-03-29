package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FindTreeStep implements ITutorialStep {
   private static final Set<Block> TREE_BLOCKS;
   private static final ITextComponent TITLE;
   private static final ITextComponent DESCRIPTION;
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public FindTreeStep(Tutorial p_i47582_1_) {
      this.tutorial = p_i47582_1_;
   }

   public void tick() {
      ++this.timeWaiting;
      if (this.tutorial.getGameType() != GameType.SURVIVAL) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            ClientPlayerEntity lvt_1_1_ = this.tutorial.getMinecraft().player;
            if (lvt_1_1_ != null) {
               Iterator var2 = TREE_BLOCKS.iterator();

               while(var2.hasNext()) {
                  Block lvt_3_1_ = (Block)var2.next();
                  if (lvt_1_1_.inventory.hasItemStack(new ItemStack(lvt_3_1_))) {
                     this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                     return;
                  }
               }

               if (hasPunchedTreesPreviously(lvt_1_1_)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if (this.timeWaiting >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, false);
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

   public void onMouseHover(ClientWorld p_193246_1_, RayTraceResult p_193246_2_) {
      if (p_193246_2_.getType() == RayTraceResult.Type.BLOCK) {
         BlockState lvt_3_1_ = p_193246_1_.getBlockState(((BlockRayTraceResult)p_193246_2_).getPos());
         if (TREE_BLOCKS.contains(lvt_3_1_.getBlock())) {
            this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
         }
      }

   }

   public void handleSetSlot(ItemStack p_193252_1_) {
      Iterator var2 = TREE_BLOCKS.iterator();

      Block lvt_3_1_;
      do {
         if (!var2.hasNext()) {
            return;
         }

         lvt_3_1_ = (Block)var2.next();
      } while(p_193252_1_.getItem() != lvt_3_1_.asItem());

      this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
   }

   public static boolean hasPunchedTreesPreviously(ClientPlayerEntity p_194070_0_) {
      Iterator var1 = TREE_BLOCKS.iterator();

      Block lvt_2_1_;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         lvt_2_1_ = (Block)var1.next();
      } while(p_194070_0_.getStats().getValue(Stats.BLOCK_MINED.get(lvt_2_1_)) <= 0);

      return true;
   }

   static {
      TREE_BLOCKS = Sets.newHashSet(new Block[]{Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES});
      TITLE = new TranslationTextComponent("tutorial.find_tree.title", new Object[0]);
      DESCRIPTION = new TranslationTextComponent("tutorial.find_tree.description", new Object[0]);
   }
}
