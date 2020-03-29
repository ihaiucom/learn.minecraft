package net.minecraft.command;

import net.minecraft.util.text.ITextComponent;

public interface ICommandSource {
   ICommandSource field_213139_a_ = new ICommandSource() {
      public void sendMessage(ITextComponent p_145747_1_) {
      }

      public boolean shouldReceiveFeedback() {
         return false;
      }

      public boolean shouldReceiveErrors() {
         return false;
      }

      public boolean allowLogging() {
         return false;
      }
   };

   void sendMessage(ITextComponent var1);

   boolean shouldReceiveFeedback();

   boolean shouldReceiveErrors();

   boolean allowLogging();
}
