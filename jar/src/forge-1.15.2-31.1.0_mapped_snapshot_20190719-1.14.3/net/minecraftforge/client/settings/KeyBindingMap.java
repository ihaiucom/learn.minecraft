package net.minecraftforge.client.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class KeyBindingMap {
   private static final EnumMap<KeyModifier, Map<InputMappings.Input, Collection<KeyBinding>>> map = new EnumMap(KeyModifier.class);

   @Nullable
   public KeyBinding lookupActive(InputMappings.Input keyCode) {
      KeyModifier activeModifier = KeyModifier.getActiveModifier();
      if (!activeModifier.matches(keyCode)) {
         KeyBinding binding = this.getBinding(keyCode, activeModifier);
         if (binding != null) {
            return binding;
         }
      }

      return this.getBinding(keyCode, KeyModifier.NONE);
   }

   @Nullable
   private KeyBinding getBinding(InputMappings.Input keyCode, KeyModifier keyModifier) {
      Collection<KeyBinding> bindings = (Collection)((Map)map.get(keyModifier)).get(keyCode);
      if (bindings != null) {
         Iterator var4 = bindings.iterator();

         while(var4.hasNext()) {
            KeyBinding binding = (KeyBinding)var4.next();
            if (binding.isActiveAndMatches(keyCode)) {
               return binding;
            }
         }
      }

      return null;
   }

   public List<KeyBinding> lookupAll(InputMappings.Input keyCode) {
      List<KeyBinding> matchingBindings = new ArrayList();
      Iterator var3 = map.values().iterator();

      while(var3.hasNext()) {
         Map<InputMappings.Input, Collection<KeyBinding>> bindingsMap = (Map)var3.next();
         Collection<KeyBinding> bindings = (Collection)bindingsMap.get(keyCode);
         if (bindings != null) {
            matchingBindings.addAll(bindings);
         }
      }

      return matchingBindings;
   }

   public void addKey(InputMappings.Input keyCode, KeyBinding keyBinding) {
      KeyModifier keyModifier = keyBinding.getKeyModifier();
      Map<InputMappings.Input, Collection<KeyBinding>> bindingsMap = (Map)map.get(keyModifier);
      Collection<KeyBinding> bindingsForKey = (Collection)bindingsMap.get(keyCode);
      if (bindingsForKey == null) {
         bindingsForKey = new ArrayList();
         bindingsMap.put(keyCode, bindingsForKey);
      }

      ((Collection)bindingsForKey).add(keyBinding);
   }

   public void removeKey(KeyBinding keyBinding) {
      KeyModifier keyModifier = keyBinding.getKeyModifier();
      InputMappings.Input keyCode = keyBinding.getKey();
      Map<InputMappings.Input, Collection<KeyBinding>> bindingsMap = (Map)map.get(keyModifier);
      Collection<KeyBinding> bindingsForKey = (Collection)bindingsMap.get(keyCode);
      if (bindingsForKey != null) {
         bindingsForKey.remove(keyBinding);
         if (bindingsForKey.isEmpty()) {
            bindingsMap.remove(keyCode);
         }
      }

   }

   public void clearMap() {
      Iterator var1 = map.values().iterator();

      while(var1.hasNext()) {
         Map<InputMappings.Input, Collection<KeyBinding>> bindings = (Map)var1.next();
         bindings.clear();
      }

   }

   static {
      KeyModifier[] var0 = KeyModifier.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         KeyModifier modifier = var0[var2];
         map.put(modifier, new HashMap());
      }

   }
}
