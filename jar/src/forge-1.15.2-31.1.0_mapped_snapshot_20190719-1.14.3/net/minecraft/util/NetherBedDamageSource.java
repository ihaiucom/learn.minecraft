package net.minecraft.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class NetherBedDamageSource extends DamageSource {
   protected NetherBedDamageSource() {
      super("netherBed");
      this.setDifficultyScaled();
      this.setExplosion();
   }

   public ITextComponent getDeathMessage(LivingEntity p_151519_1_) {
      ITextComponent lvt_2_1_ = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("death.attack.netherBed.link", new Object[0])).applyTextStyle((p_211694_0_) -> {
         p_211694_0_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("MCPE-28723")));
      });
      return new TranslationTextComponent("death.attack.netherBed.message", new Object[]{p_151519_1_.getDisplayName(), lvt_2_1_});
   }
}
