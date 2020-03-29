package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraftforge.common.util.SortedProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PropertyManager<T extends PropertyManager<T>> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Properties serverProperties;

   public PropertyManager(Properties p_i50717_1_) {
      this.serverProperties = p_i50717_1_;
   }

   public static Properties load(Path p_218969_0_) {
      Properties properties = new Properties();

      try {
         InputStream inputstream = Files.newInputStream(p_218969_0_);
         Throwable var3 = null;

         try {
            properties.load(inputstream);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (inputstream != null) {
               if (var3 != null) {
                  try {
                     inputstream.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  inputstream.close();
               }
            }

         }
      } catch (IOException var15) {
         LOGGER.error("Failed to load properties from file: " + p_218969_0_);
      }

      return properties;
   }

   public void save(Path p_218970_1_) {
      try {
         OutputStream outputstream = Files.newOutputStream(p_218970_1_);
         Throwable var3 = null;

         try {
            SortedProperties.store(this.serverProperties, outputstream, "Minecraft server properties");
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (outputstream != null) {
               if (var3 != null) {
                  try {
                     outputstream.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  outputstream.close();
               }
            }

         }
      } catch (IOException var15) {
         LOGGER.error("Failed to store properties to file: " + p_218970_1_);
      }

   }

   private static <V extends Number> Function<String, V> func_218963_a(Function<String, V> p_218963_0_) {
      return (p_lambda$func_218963_a$0_1_) -> {
         try {
            return (Number)p_218963_0_.apply(p_lambda$func_218963_a$0_1_);
         } catch (NumberFormatException var3) {
            return (Number)null;
         }
      };
   }

   protected static <V> Function<String, V> func_218964_a(IntFunction<V> p_218964_0_, Function<String, V> p_218964_1_) {
      return (p_lambda$func_218964_a$1_2_) -> {
         try {
            return p_218964_0_.apply(Integer.parseInt(p_lambda$func_218964_a$1_2_));
         } catch (NumberFormatException var4) {
            return p_218964_1_.apply(p_lambda$func_218964_a$1_2_);
         }
      };
   }

   @Nullable
   private String func_218976_c(String p_218976_1_) {
      return (String)this.serverProperties.get(p_218976_1_);
   }

   @Nullable
   protected <V> V func_218984_a(String p_218984_1_, Function<String, V> p_218984_2_) {
      String s = this.func_218976_c(p_218984_1_);
      if (s == null) {
         return null;
      } else {
         this.serverProperties.remove(p_218984_1_);
         return p_218984_2_.apply(s);
      }
   }

   protected <V> V func_218983_a(String p_218983_1_, Function<String, V> p_218983_2_, Function<V, String> p_218983_3_, V p_218983_4_) {
      String s = this.func_218976_c(p_218983_1_);
      V v = MoreObjects.firstNonNull(s != null ? p_218983_2_.apply(s) : null, p_218983_4_);
      this.serverProperties.put(p_218983_1_, p_218983_3_.apply(v));
      return v;
   }

   protected <V> PropertyManager<T>.Property<V> func_218981_b(String p_218981_1_, Function<String, V> p_218981_2_, Function<V, String> p_218981_3_, V p_218981_4_) {
      String s = this.func_218976_c(p_218981_1_);
      V v = MoreObjects.firstNonNull(s != null ? p_218981_2_.apply(s) : null, p_218981_4_);
      this.serverProperties.put(p_218981_1_, p_218981_3_.apply(v));
      return new PropertyManager.Property(p_218981_1_, v, p_218981_3_);
   }

   protected <V> V func_218977_a(String p_218977_1_, Function<String, V> p_218977_2_, UnaryOperator<V> p_218977_3_, Function<V, String> p_218977_4_, V p_218977_5_) {
      return this.func_218983_a(p_218977_1_, (p_lambda$func_218977_a$2_2_) -> {
         V v = p_218977_2_.apply(p_lambda$func_218977_a$2_2_);
         return v != null ? p_218977_3_.apply(v) : null;
      }, p_218977_4_, p_218977_5_);
   }

   protected <V> V func_218979_a(String p_218979_1_, Function<String, V> p_218979_2_, V p_218979_3_) {
      return this.func_218983_a(p_218979_1_, p_218979_2_, Objects::toString, p_218979_3_);
   }

   protected <V> PropertyManager<T>.Property<V> func_218965_b(String p_218965_1_, Function<String, V> p_218965_2_, V p_218965_3_) {
      return this.func_218981_b(p_218965_1_, p_218965_2_, Objects::toString, p_218965_3_);
   }

   protected String func_218973_a(String p_218973_1_, String p_218973_2_) {
      return (String)this.func_218983_a(p_218973_1_, Function.identity(), Function.identity(), p_218973_2_);
   }

   @Nullable
   protected String func_218980_a(String p_218980_1_) {
      return (String)this.func_218984_a(p_218980_1_, Function.identity());
   }

   protected int func_218968_a(String p_218968_1_, int p_218968_2_) {
      return (Integer)this.func_218979_a(p_218968_1_, func_218963_a(Integer::parseInt), p_218968_2_);
   }

   protected PropertyManager<T>.Property<Integer> func_218974_b(String p_218974_1_, int p_218974_2_) {
      return this.func_218965_b(p_218974_1_, func_218963_a(Integer::parseInt), p_218974_2_);
   }

   protected int func_218962_a(String p_218962_1_, UnaryOperator<Integer> p_218962_2_, int p_218962_3_) {
      return (Integer)this.func_218977_a(p_218962_1_, func_218963_a(Integer::parseInt), p_218962_2_, Objects::toString, p_218962_3_);
   }

   protected long func_218967_a(String p_218967_1_, long p_218967_2_) {
      return (Long)this.func_218979_a(p_218967_1_, func_218963_a(Long::parseLong), p_218967_2_);
   }

   protected boolean func_218982_a(String p_218982_1_, boolean p_218982_2_) {
      return (Boolean)this.func_218979_a(p_218982_1_, Boolean::valueOf, p_218982_2_);
   }

   protected PropertyManager<T>.Property<Boolean> func_218961_b(String p_218961_1_, boolean p_218961_2_) {
      return this.func_218965_b(p_218961_1_, Boolean::valueOf, p_218961_2_);
   }

   @Nullable
   protected Boolean func_218978_b(String p_218978_1_) {
      return (Boolean)this.func_218984_a(p_218978_1_, Boolean::valueOf);
   }

   protected Properties func_218966_a() {
      Properties properties = new Properties();
      properties.putAll(this.serverProperties);
      return properties;
   }

   protected abstract T func_212857_b_(Properties var1);

   public class Property<V> implements Supplier<V> {
      private final String name;
      private final V field_219041_c;
      private final Function<V, String> field_219042_d;

      private Property(String p_i50880_2_, V p_i50880_3_, Function<V, String> p_i50880_4_) {
         this.name = p_i50880_2_;
         this.field_219041_c = p_i50880_3_;
         this.field_219042_d = p_i50880_4_;
      }

      public V get() {
         return this.field_219041_c;
      }

      public T func_219038_a(V p_219038_1_) {
         Properties properties = PropertyManager.this.func_218966_a();
         properties.put(this.name, this.field_219042_d.apply(p_219038_1_));
         return PropertyManager.this.func_212857_b_(properties);
      }

      // $FF: synthetic method
      Property(String p_i50881_2_, Object p_i50881_3_, Function p_i50881_4_, Object p_i50881_5_) {
         this(p_i50881_2_, p_i50881_3_, p_i50881_4_);
      }
   }
}
