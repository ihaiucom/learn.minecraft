package net.minecraft.client.settings;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

@OnlyIn(Dist.CLIENT)
public class KeyBinding implements Comparable<KeyBinding>, IForgeKeybinding {
   private static final Map<String, KeyBinding> KEYBIND_ARRAY = Maps.newHashMap();
   private static final KeyBindingMap HASH = new KeyBindingMap();
   private static final Set<String> KEYBIND_SET = Sets.newHashSet();
   private static final Map<String, Integer> CATEGORY_ORDER = (Map)Util.make(Maps.newHashMap(), (p_lambda$static$0_0_) -> {
      p_lambda$static$0_0_.put("key.categories.movement", 1);
      p_lambda$static$0_0_.put("key.categories.gameplay", 2);
      p_lambda$static$0_0_.put("key.categories.inventory", 3);
      p_lambda$static$0_0_.put("key.categories.creative", 4);
      p_lambda$static$0_0_.put("key.categories.multiplayer", 5);
      p_lambda$static$0_0_.put("key.categories.ui", 6);
      p_lambda$static$0_0_.put("key.categories.misc", 7);
   });
   private final String keyDescription;
   private final InputMappings.Input keyCodeDefault;
   private final String keyCategory;
   private InputMappings.Input keyCode;
   private boolean pressed;
   private int pressTime;
   private KeyModifier keyModifierDefault;
   private KeyModifier keyModifier;
   private IKeyConflictContext keyConflictContext;

   public static void onTick(InputMappings.Input p_197981_0_) {
      KeyBinding keybinding = HASH.lookupActive(p_197981_0_);
      if (keybinding != null) {
         ++keybinding.pressTime;
      }

   }

   public static void setKeyBindState(InputMappings.Input p_197980_0_, boolean p_197980_1_) {
      Iterator var2 = HASH.lookupAll(p_197980_0_).iterator();

      while(var2.hasNext()) {
         KeyBinding keybinding = (KeyBinding)var2.next();
         if (keybinding != null) {
            keybinding.func_225593_a_(p_197980_1_);
         }
      }

   }

   public static void updateKeyBindState() {
      Iterator var0 = KEYBIND_ARRAY.values().iterator();

      while(var0.hasNext()) {
         KeyBinding keybinding = (KeyBinding)var0.next();
         if (keybinding.keyCode.getType() == InputMappings.Type.KEYSYM && keybinding.keyCode.getKeyCode() != InputMappings.INPUT_INVALID.getKeyCode()) {
            keybinding.func_225593_a_(InputMappings.isKeyDown(Minecraft.getInstance().func_228018_at_().getHandle(), keybinding.keyCode.getKeyCode()));
         }
      }

   }

   public static void unPressAllKeys() {
      Iterator var0 = KEYBIND_ARRAY.values().iterator();

      while(var0.hasNext()) {
         KeyBinding keybinding = (KeyBinding)var0.next();
         keybinding.unpressKey();
      }

   }

   public static void resetKeyBindingArrayAndHash() {
      HASH.clearMap();
      Iterator var0 = KEYBIND_ARRAY.values().iterator();

      while(var0.hasNext()) {
         KeyBinding keybinding = (KeyBinding)var0.next();
         HASH.addKey(keybinding.keyCode, keybinding);
      }

   }

   public KeyBinding(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
      this(p_i45001_1_, InputMappings.Type.KEYSYM, p_i45001_2_, p_i45001_3_);
   }

   public KeyBinding(String p_i47675_1_, InputMappings.Type p_i47675_2_, int p_i47675_3_, String p_i47675_4_) {
      this.keyModifierDefault = KeyModifier.NONE;
      this.keyModifier = KeyModifier.NONE;
      this.keyConflictContext = KeyConflictContext.UNIVERSAL;
      this.keyDescription = p_i47675_1_;
      this.keyCode = p_i47675_2_.getOrMakeInput(p_i47675_3_);
      this.keyCodeDefault = this.keyCode;
      this.keyCategory = p_i47675_4_;
      KEYBIND_ARRAY.put(p_i47675_1_, this);
      HASH.addKey(this.keyCode, this);
      KEYBIND_SET.add(p_i47675_4_);
   }

   public boolean isKeyDown() {
      return this.pressed && this.getKeyConflictContext().isActive() && this.getKeyModifier().isActive(this.getKeyConflictContext());
   }

   public String getKeyCategory() {
      return this.keyCategory;
   }

   public boolean isPressed() {
      if (this.pressTime == 0) {
         return false;
      } else {
         --this.pressTime;
         return true;
      }
   }

   private void unpressKey() {
      this.pressTime = 0;
      this.func_225593_a_(false);
   }

   public String getKeyDescription() {
      return this.keyDescription;
   }

   public InputMappings.Input getDefault() {
      return this.keyCodeDefault;
   }

   public void bind(InputMappings.Input p_197979_1_) {
      this.keyCode = p_197979_1_;
   }

   public int compareTo(KeyBinding p_compareTo_1_) {
      if (this.keyCategory.equals(p_compareTo_1_.keyCategory)) {
         return I18n.format(this.keyDescription).compareTo(I18n.format(p_compareTo_1_.keyDescription));
      } else {
         Integer tCat = (Integer)CATEGORY_ORDER.get(this.keyCategory);
         Integer oCat = (Integer)CATEGORY_ORDER.get(p_compareTo_1_.keyCategory);
         if (tCat == null && oCat != null) {
            return 1;
         } else if (tCat != null && oCat == null) {
            return -1;
         } else {
            return tCat == null && oCat == null ? I18n.format(this.keyCategory).compareTo(I18n.format(p_compareTo_1_.keyCategory)) : tCat.compareTo(oCat);
         }
      }
   }

   public static Supplier<String> getDisplayString(String p_193626_0_) {
      KeyBinding keybinding = (KeyBinding)KEYBIND_ARRAY.get(p_193626_0_);
      return keybinding == null ? () -> {
         return p_193626_0_;
      } : keybinding::getLocalizedName;
   }

   public boolean conflicts(KeyBinding p_197983_1_) {
      if (this.getKeyConflictContext().conflicts(p_197983_1_.getKeyConflictContext()) || p_197983_1_.getKeyConflictContext().conflicts(this.getKeyConflictContext())) {
         KeyModifier keyModifier = this.getKeyModifier();
         KeyModifier otherKeyModifier = p_197983_1_.getKeyModifier();
         if (keyModifier.matches(p_197983_1_.getKey()) || otherKeyModifier.matches(this.getKey())) {
            return true;
         }

         if (this.getKey().equals(p_197983_1_.getKey())) {
            return keyModifier == otherKeyModifier || this.getKeyConflictContext().conflicts(KeyConflictContext.IN_GAME) && (keyModifier == KeyModifier.NONE || otherKeyModifier == KeyModifier.NONE);
         }
      }

      return this.keyCode.equals(p_197983_1_.keyCode);
   }

   public boolean isInvalid() {
      return this.keyCode.equals(InputMappings.INPUT_INVALID);
   }

   public boolean matchesKey(int p_197976_1_, int p_197976_2_) {
      if (p_197976_1_ == InputMappings.INPUT_INVALID.getKeyCode()) {
         return this.keyCode.getType() == InputMappings.Type.SCANCODE && this.keyCode.getKeyCode() == p_197976_2_;
      } else {
         return this.keyCode.getType() == InputMappings.Type.KEYSYM && this.keyCode.getKeyCode() == p_197976_1_;
      }
   }

   public boolean matchesMouseKey(int p_197984_1_) {
      return this.keyCode.getType() == InputMappings.Type.MOUSE && this.keyCode.getKeyCode() == p_197984_1_;
   }

   public String getLocalizedName() {
      return this.getKeyModifier().getLocalizedComboName(this.keyCode, () -> {
         String s = this.keyCode.getTranslationKey();
         int i = this.keyCode.getKeyCode();
         String s1 = null;
         switch(this.keyCode.getType()) {
         case KEYSYM:
            s1 = InputMappings.func_216507_a(i);
            break;
         case SCANCODE:
            s1 = InputMappings.func_216502_b(i);
            break;
         case MOUSE:
            String s2 = I18n.format(s);
            s1 = Objects.equals(s2, s) ? I18n.format(InputMappings.Type.MOUSE.func_216500_a(), i + 1) : s2;
         }

         return s1 == null ? I18n.format(s) : s1;
      });
   }

   public boolean isDefault() {
      return this.keyCode.equals(this.keyCodeDefault) && this.getKeyModifier() == this.getKeyModifierDefault();
   }

   public String getTranslationKey() {
      return this.keyCode.getTranslationKey();
   }

   public KeyBinding(String p_i230092_1_, IKeyConflictContext p_i230092_2_, InputMappings.Type p_i230092_3_, int p_i230092_4_, String p_i230092_5_) {
      this(p_i230092_1_, p_i230092_2_, p_i230092_3_.getOrMakeInput(p_i230092_4_), p_i230092_5_);
   }

   public KeyBinding(String p_i230093_1_, IKeyConflictContext p_i230093_2_, InputMappings.Input p_i230093_3_, String p_i230093_4_) {
      this(p_i230093_1_, p_i230093_2_, KeyModifier.NONE, p_i230093_3_, p_i230093_4_);
   }

   public KeyBinding(String p_i230094_1_, IKeyConflictContext p_i230094_2_, KeyModifier p_i230094_3_, InputMappings.Type p_i230094_4_, int p_i230094_5_, String p_i230094_6_) {
      this(p_i230094_1_, p_i230094_2_, p_i230094_3_, p_i230094_4_.getOrMakeInput(p_i230094_5_), p_i230094_6_);
   }

   public KeyBinding(String p_i230095_1_, IKeyConflictContext p_i230095_2_, KeyModifier p_i230095_3_, InputMappings.Input p_i230095_4_, String p_i230095_5_) {
      this.keyModifierDefault = KeyModifier.NONE;
      this.keyModifier = KeyModifier.NONE;
      this.keyConflictContext = KeyConflictContext.UNIVERSAL;
      this.keyDescription = p_i230095_1_;
      this.keyCode = p_i230095_4_;
      this.keyCodeDefault = p_i230095_4_;
      this.keyCategory = p_i230095_5_;
      this.keyConflictContext = p_i230095_2_;
      this.keyModifier = p_i230095_3_;
      this.keyModifierDefault = p_i230095_3_;
      if (this.keyModifier.matches(p_i230095_4_)) {
         this.keyModifier = KeyModifier.NONE;
      }

      KEYBIND_ARRAY.put(p_i230095_1_, this);
      HASH.addKey(p_i230095_4_, this);
      KEYBIND_SET.add(p_i230095_5_);
   }

   public InputMappings.Input getKey() {
      return this.keyCode;
   }

   public void setKeyConflictContext(IKeyConflictContext p_setKeyConflictContext_1_) {
      this.keyConflictContext = p_setKeyConflictContext_1_;
   }

   public IKeyConflictContext getKeyConflictContext() {
      return this.keyConflictContext;
   }

   public KeyModifier getKeyModifierDefault() {
      return this.keyModifierDefault;
   }

   public KeyModifier getKeyModifier() {
      return this.keyModifier;
   }

   public void setKeyModifierAndCode(KeyModifier p_setKeyModifierAndCode_1_, InputMappings.Input p_setKeyModifierAndCode_2_) {
      this.keyCode = p_setKeyModifierAndCode_2_;
      if (p_setKeyModifierAndCode_1_.matches(p_setKeyModifierAndCode_2_)) {
         p_setKeyModifierAndCode_1_ = KeyModifier.NONE;
      }

      HASH.removeKey(this);
      this.keyModifier = p_setKeyModifierAndCode_1_;
      HASH.addKey(p_setKeyModifierAndCode_2_, this);
   }

   public void func_225593_a_(boolean p_225593_1_) {
      this.pressed = p_225593_1_;
   }
}
