package net.minecraft.util.text.event;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.text.ITextComponent;

public class HoverEvent {
   private final HoverEvent.Action action;
   private final ITextComponent value;

   public HoverEvent(HoverEvent.Action p_i45158_1_, ITextComponent p_i45158_2_) {
      this.action = p_i45158_1_;
      this.value = p_i45158_2_;
   }

   public HoverEvent.Action getAction() {
      return this.action;
   }

   public ITextComponent getValue() {
      return this.value;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         HoverEvent lvt_2_1_ = (HoverEvent)p_equals_1_;
         if (this.action != lvt_2_1_.action) {
            return false;
         } else {
            if (this.value != null) {
               if (!this.value.equals(lvt_2_1_.value)) {
                  return false;
               }
            } else if (lvt_2_1_.value != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public String toString() {
      return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
   }

   public int hashCode() {
      int lvt_1_1_ = this.action.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + (this.value != null ? this.value.hashCode() : 0);
      return lvt_1_1_;
   }

   public static enum Action {
      SHOW_TEXT("show_text", true),
      SHOW_ITEM("show_item", true),
      SHOW_ENTITY("show_entity", true);

      private static final Map<String, HoverEvent.Action> NAME_MAPPING = (Map)Arrays.stream(values()).collect(Collectors.toMap(HoverEvent.Action::getCanonicalName, (p_199854_0_) -> {
         return p_199854_0_;
      }));
      private final boolean allowedInChat;
      private final String canonicalName;

      private Action(String p_i45157_3_, boolean p_i45157_4_) {
         this.canonicalName = p_i45157_3_;
         this.allowedInChat = p_i45157_4_;
      }

      public boolean shouldAllowInChat() {
         return this.allowedInChat;
      }

      public String getCanonicalName() {
         return this.canonicalName;
      }

      public static HoverEvent.Action getValueByCanonicalName(String p_150684_0_) {
         return (HoverEvent.Action)NAME_MAPPING.get(p_150684_0_);
      }
   }
}
