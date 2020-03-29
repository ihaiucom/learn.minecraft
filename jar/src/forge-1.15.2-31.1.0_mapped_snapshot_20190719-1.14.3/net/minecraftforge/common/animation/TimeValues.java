package net.minecraftforge.common.animation;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.util.IStringSerializable;

public final class TimeValues {
   public static enum CommonTimeValueTypeAdapterFactory implements TypeAdapterFactory {
      INSTANCE;

      private final ThreadLocal<Function<String, ITimeValue>> valueResolver = new ThreadLocal();

      public void setValueResolver(@Nullable Function<String, ITimeValue> valueResolver) {
         this.valueResolver.set(valueResolver);
      }

      @Nullable
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
         return type.getRawType() != ITimeValue.class ? null : new TypeAdapter<ITimeValue>() {
            public void write(JsonWriter out, ITimeValue parameter) throws IOException {
               if (parameter instanceof TimeValues.ConstValue) {
                  out.value((double)((TimeValues.ConstValue)parameter).output);
               } else if (parameter instanceof TimeValues.SimpleExprValue) {
                  TimeValues.SimpleExprValue px = (TimeValues.SimpleExprValue)parameter;
                  out.beginArray();
                  out.value(px.operators);
                  UnmodifiableIterator var4 = px.args.iterator();

                  while(var4.hasNext()) {
                     ITimeValue v = (ITimeValue)var4.next();
                     this.write(out, v);
                  }

                  out.endArray();
               } else if (parameter instanceof TimeValues.CompositionValue) {
                  TimeValues.CompositionValue p = (TimeValues.CompositionValue)parameter;
                  out.beginArray();
                  out.value("compose");
                  this.write(out, p.g);
                  this.write(out, p.f);
                  out.endArray();
               } else if (parameter instanceof IStringSerializable) {
                  out.value("#" + ((IStringSerializable)parameter).getName());
               }

            }

            public ITimeValue read(JsonReader in) throws IOException {
               switch(in.peek()) {
               case NUMBER:
                  return new TimeValues.ConstValue((float)in.nextDouble());
               case BEGIN_ARRAY:
                  in.beginArray();
                  String type = in.nextString();
                  Object p;
                  if (TimeValues.SimpleExprValue.opsPattern.matcher(type).matches()) {
                     Builder builder = ImmutableList.builder();

                     while(in.hasNext()) {
                        builder.add(this.read(in));
                     }

                     p = new TimeValues.SimpleExprValue(type, builder.build());
                  } else {
                     if (!"compose".equals(type)) {
                        throw new IOException("Unknown TimeValue type \"" + type + "\"");
                     }

                     p = new TimeValues.CompositionValue(this.read(in), this.read(in));
                  }

                  in.endArray();
                  return (ITimeValue)p;
               case STRING:
                  String string = in.nextString();
                  if (string.equals("#identity")) {
                     return TimeValues.IdentityValue.INSTANCE;
                  } else {
                     if (!string.startsWith("#")) {
                        throw new IOException("Expected TimeValue reference, got \"" + string + "\"");
                     }

                     return new TimeValues.ParameterValue(string.substring(1), (Function)CommonTimeValueTypeAdapterFactory.this.valueResolver.get());
                  }
               default:
                  throw new IOException("Expected TimeValue, got " + in.peek());
               }
            }
         };
      }
   }

   public static final class ParameterValue implements ITimeValue, IStringSerializable {
      private final String parameterName;
      private final Function<String, ITimeValue> valueResolver;
      private ITimeValue parameter;

      public ParameterValue(String parameterName, Function<String, ITimeValue> valueResolver) {
         this.parameterName = parameterName;
         this.valueResolver = valueResolver;
      }

      public String getName() {
         return this.parameterName;
      }

      private void resolve() {
         if (this.parameter == null) {
            if (this.valueResolver != null) {
               this.parameter = (ITimeValue)this.valueResolver.apply(this.parameterName);
            }

            if (this.parameter == null) {
               throw new IllegalArgumentException("Couldn't resolve parameter value " + this.parameterName);
            }
         }

      }

      public float apply(float input) {
         this.resolve();
         return this.parameter.apply(input);
      }

      public int hashCode() {
         this.resolve();
         return this.parameter.hashCode();
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            TimeValues.ParameterValue other = (TimeValues.ParameterValue)obj;
            this.resolve();
            other.resolve();
            return Objects.equal(this.parameter, other.parameter);
         }
      }
   }

   public static final class CompositionValue implements ITimeValue {
      private final ITimeValue g;
      private final ITimeValue f;

      public CompositionValue(ITimeValue g, ITimeValue f) {
         this.g = g;
         this.f = f;
      }

      public float apply(float input) {
         return this.g.apply(this.f.apply(input));
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.g, this.f});
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            TimeValues.CompositionValue other = (TimeValues.CompositionValue)obj;
            return Objects.equal(this.g, other.g) && Objects.equal(this.f, other.f);
         }
      }
   }

   public static final class SimpleExprValue implements ITimeValue {
      private static final Pattern opsPattern = Pattern.compile("[+\\-*/mMrRfF]+");
      private final String operators;
      private final ImmutableList<ITimeValue> args;

      public SimpleExprValue(String operators, ImmutableList<ITimeValue> args) {
         this.operators = operators;
         this.args = args;
      }

      public float apply(float input) {
         float ret = input;

         for(int i = 0; i < this.operators.length(); ++i) {
            float arg = ((ITimeValue)this.args.get(i)).apply(input);
            switch(this.operators.charAt(i)) {
            case '*':
               ret *= arg;
               break;
            case '+':
               ret += arg;
               break;
            case '-':
               ret -= arg;
               break;
            case '/':
               ret /= arg;
               break;
            case 'F':
               ret = (float)Math.ceil((double)(ret / arg)) * arg - ret;
               break;
            case 'M':
               ret = Math.max(ret, arg);
               break;
            case 'R':
               ret = (float)Math.ceil((double)(ret / arg)) * arg;
               break;
            case 'f':
               ret = (float)((double)ret - Math.floor((double)(ret / arg)) * (double)arg);
               break;
            case 'm':
               ret = Math.min(ret, arg);
               break;
            case 'r':
               ret = (float)Math.floor((double)(ret / arg)) * arg;
            }
         }

         return ret;
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.operators, this.args});
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            TimeValues.SimpleExprValue other = (TimeValues.SimpleExprValue)obj;
            return Objects.equal(this.operators, other.operators) && Objects.equal(this.args, other.args);
         }
      }
   }

   public static final class VariableValue implements ITimeValue {
      private float output;

      public VariableValue(float initialValue) {
         this.output = initialValue;
      }

      public void setValue(float newValue) {
         this.output = newValue;
      }

      public float apply(float input) {
         return this.output;
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.output});
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            TimeValues.VariableValue other = (TimeValues.VariableValue)obj;
            return this.output == other.output;
         }
      }
   }

   public static final class ConstValue implements ITimeValue {
      private final float output;

      public ConstValue(float output) {
         this.output = output;
      }

      public float apply(float input) {
         return this.output;
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.output});
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            TimeValues.ConstValue other = (TimeValues.ConstValue)obj;
            return this.output == other.output;
         }
      }
   }

   public static enum IdentityValue implements ITimeValue, IStringSerializable {
      INSTANCE;

      public float apply(float input) {
         return input;
      }

      public String getName() {
         return "identity";
      }
   }
}
