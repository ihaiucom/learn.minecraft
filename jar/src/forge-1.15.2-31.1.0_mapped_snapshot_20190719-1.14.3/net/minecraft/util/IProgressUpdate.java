package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProgressUpdate {
   void displaySavingString(ITextComponent var1);

   @OnlyIn(Dist.CLIENT)
   void resetProgressAndMessage(ITextComponent var1);

   void displayLoadingString(ITextComponent var1);

   void setLoadingProgress(int var1);

   @OnlyIn(Dist.CLIENT)
   void setDoneWorking();
}
