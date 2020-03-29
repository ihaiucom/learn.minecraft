package net.minecraftforge.userdev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ArgumentList {
   private static final Logger LOGGER = LogManager.getLogger();
   private List<Supplier<String[]>> entries = new ArrayList();
   private Map<String, ArgumentList.EntryValue> values = new HashMap();

   public static ArgumentList from(String... args) {
      ArgumentList ret = new ArgumentList();
      boolean ended = false;

      for(int x = 0; x < args.length; ++x) {
         if (!ended) {
            if ("--".equals(args[x])) {
               ended = true;
            } else if ("-".equals(args[x])) {
               ret.addRaw(args[x]);
            } else if (args[x].startsWith("-")) {
               int idx = args[x].indexOf(61);
               String key = idx == -1 ? args[x] : args[x].substring(0, idx);
               String value = idx == -1 ? null : (idx == args[x].length() - 1 ? "" : args[x].substring(idx + 1));
               if (idx == -1 && x + 1 < args.length && !args[x + 1].startsWith("-")) {
                  ret.addArg(true, key, args[x + 1]);
                  ++x;
               } else {
                  ret.addArg(false, key, value);
               }
            } else {
               ret.addRaw(args[x]);
            }
         } else {
            ret.addRaw(args[x]);
         }
      }

      return ret;
   }

   public void addRaw(String arg) {
      this.entries.add(() -> {
         return new String[]{arg};
      });
   }

   public void addArg(boolean split, String raw, String value) {
      int idx = raw.startsWith("--") ? 2 : 1;
      String prefix = raw.substring(0, idx);
      String key = raw.substring(idx);
      ArgumentList.EntryValue entry = new ArgumentList.EntryValue(split, prefix, key, value);
      if (this.values.containsKey(key)) {
         LOGGER.info("Duplicate entries for " + key + " Unindexable");
      } else {
         this.values.put(key, entry);
      }

      this.entries.add(entry);
   }

   public String[] getArguments() {
      return (String[])this.entries.stream().flatMap((e) -> {
         return Arrays.asList((Object[])e.get()).stream();
      }).toArray((size) -> {
         return new String[size];
      });
   }

   public boolean hasValue(String key) {
      return this.getOrDefault(key, (String)null) != null;
   }

   public String get(String key) {
      ArgumentList.EntryValue ent = (ArgumentList.EntryValue)this.values.get(key);
      return ent == null ? null : ent.getValue();
   }

   public String getOrDefault(String key, String value) {
      ArgumentList.EntryValue ent = (ArgumentList.EntryValue)this.values.get(key);
      return ent == null ? value : (ent.getValue() == null ? value : ent.getValue());
   }

   public void put(String key, String value) {
      ArgumentList.EntryValue entry = (ArgumentList.EntryValue)this.values.get(key);
      if (entry == null) {
         entry = new ArgumentList.EntryValue(true, "--", key, value);
         this.values.put(key, entry);
         this.entries.add(entry);
      } else {
         entry.setValue(value);
      }

   }

   public void putLazy(String key, String value) {
      ArgumentList.EntryValue ent = (ArgumentList.EntryValue)this.values.get(key);
      if (ent == null) {
         this.addArg(true, "--" + key, value);
      } else if (ent.getValue() == null) {
         ent.setValue(value);
      }

   }

   public String remove(String key) {
      ArgumentList.EntryValue ent = (ArgumentList.EntryValue)this.values.remove(key);
      if (ent == null) {
         return null;
      } else {
         this.entries.remove(ent);
         return ent.getValue();
      }
   }

   private class EntryValue implements Supplier<String[]> {
      private final String prefix;
      private final String key;
      private final boolean split;
      private String value;

      public EntryValue(boolean split, String prefix, String key, String value) {
         this.split = split;
         this.prefix = prefix;
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }

      public void setValue(String value) {
         this.value = value;
      }

      public String[] get() {
         if (this.getValue() == null) {
            return new String[]{this.prefix + this.getKey()};
         } else {
            return this.split ? new String[]{this.prefix + this.getKey(), this.getValue()} : new String[]{this.prefix + this.getKey() + '=' + this.getValue()};
         }
      }

      public String toString() {
         return String.join(", ", this.get());
      }
   }
}
