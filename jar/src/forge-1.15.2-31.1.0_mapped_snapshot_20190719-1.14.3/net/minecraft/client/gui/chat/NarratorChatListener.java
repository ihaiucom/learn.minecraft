package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NarratorChatListener implements IChatListener {
   public static final ITextComponent field_216868_a = new StringTextComponent("");
   private static final Logger LOGGER = LogManager.getLogger();
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   public void say(ChatType p_192576_1_, ITextComponent p_192576_2_) {
      NarratorStatus lvt_3_1_ = func_223131_d();
      if (lvt_3_1_ != NarratorStatus.OFF && this.narrator.active()) {
         if (lvt_3_1_ == NarratorStatus.ALL || lvt_3_1_ == NarratorStatus.CHAT && p_192576_1_ == ChatType.CHAT || lvt_3_1_ == NarratorStatus.SYSTEM && p_192576_1_ == ChatType.SYSTEM) {
            Object lvt_4_2_;
            if (p_192576_2_ instanceof TranslationTextComponent && "chat.type.text".equals(((TranslationTextComponent)p_192576_2_).getKey())) {
               lvt_4_2_ = new TranslationTextComponent("chat.type.text.narrate", ((TranslationTextComponent)p_192576_2_).getFormatArgs());
            } else {
               lvt_4_2_ = p_192576_2_;
            }

            this.func_216866_a(p_192576_1_.func_218690_b(), ((ITextComponent)lvt_4_2_).getString());
         }

      }
   }

   public void func_216864_a(String p_216864_1_) {
      NarratorStatus lvt_2_1_ = func_223131_d();
      if (this.narrator.active() && lvt_2_1_ != NarratorStatus.OFF && lvt_2_1_ != NarratorStatus.CHAT && !p_216864_1_.isEmpty()) {
         this.narrator.clear();
         this.func_216866_a(true, p_216864_1_);
      }

   }

   private static NarratorStatus func_223131_d() {
      return Minecraft.getInstance().gameSettings.narrator;
   }

   private void func_216866_a(boolean p_216866_1_, String p_216866_2_) {
      if (SharedConstants.developmentMode) {
         LOGGER.debug("Narrating: {}", p_216866_2_);
      }

      this.narrator.say(p_216866_2_, p_216866_1_);
   }

   public void func_216865_a(NarratorStatus p_216865_1_) {
      this.clear();
      this.narrator.say((new TranslationTextComponent("options.narrator", new Object[0])).getString() + " : " + (new TranslationTextComponent(p_216865_1_.func_216824_b(), new Object[0])).getString(), true);
      ToastGui lvt_2_1_ = Minecraft.getInstance().getToastGui();
      if (this.narrator.active()) {
         if (p_216865_1_ == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(lvt_2_1_, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.disabled", new Object[0]), (ITextComponent)null);
         } else {
            SystemToast.addOrUpdate(lvt_2_1_, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.enabled", new Object[0]), new TranslationTextComponent(p_216865_1_.func_216824_b(), new Object[0]));
         }
      } else {
         SystemToast.addOrUpdate(lvt_2_1_, SystemToast.Type.NARRATOR_TOGGLE, new TranslationTextComponent("narrator.toast.disabled", new Object[0]), new TranslationTextComponent("options.narrator.notavailable", new Object[0]));
      }

   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      if (func_223131_d() != NarratorStatus.OFF && this.narrator.active()) {
         this.narrator.clear();
      }
   }

   public void func_216867_c() {
      this.narrator.destroy();
   }
}
