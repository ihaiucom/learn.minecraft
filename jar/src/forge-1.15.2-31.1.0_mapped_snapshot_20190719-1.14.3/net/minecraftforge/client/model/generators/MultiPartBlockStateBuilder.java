package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.state.IProperty;

public final class MultiPartBlockStateBuilder implements IGeneratedBlockstate {
   private final List<MultiPartBlockStateBuilder.PartBuilder> parts = new ArrayList();
   private final Block owner;

   public MultiPartBlockStateBuilder(Block owner) {
      this.owner = owner;
   }

   public ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> part() {
      return ConfiguredModel.builder(this);
   }

   MultiPartBlockStateBuilder addPart(MultiPartBlockStateBuilder.PartBuilder part) {
      this.parts.add(part);
      return this;
   }

   public JsonObject toJson() {
      JsonArray variants = new JsonArray();
      Iterator var2 = this.parts.iterator();

      while(var2.hasNext()) {
         MultiPartBlockStateBuilder.PartBuilder part = (MultiPartBlockStateBuilder.PartBuilder)var2.next();
         variants.add(part.toJson());
      }

      JsonObject main = new JsonObject();
      main.add("multipart", variants);
      return main;
   }

   public class PartBuilder {
      public BlockStateProvider.ConfiguredModelList models;
      public boolean useOr;
      public final Multimap<IProperty<?>, Comparable<?>> conditions = HashMultimap.create();

      PartBuilder(BlockStateProvider.ConfiguredModelList models) {
         this.models = models;
      }

      public MultiPartBlockStateBuilder.PartBuilder useOr() {
         this.useOr = true;
         return this;
      }

      @SafeVarargs
      public final <T extends Comparable<T>> MultiPartBlockStateBuilder.PartBuilder condition(IProperty<T> prop, T... values) {
         Preconditions.checkNotNull(prop, "Property must not be null");
         Preconditions.checkNotNull(values, "Value list must not be null");
         Preconditions.checkArgument(values.length > 0, "Value list must not be empty");
         Preconditions.checkArgument(!this.conditions.containsKey(prop), "Cannot set condition for property \"%s\" more than once", prop.getName());
         Preconditions.checkArgument(this.canApplyTo(MultiPartBlockStateBuilder.this.owner), "IProperty %s is not valid for the block %s", prop, MultiPartBlockStateBuilder.this.owner);
         this.conditions.putAll(prop, Arrays.asList(values));
         return this;
      }

      public MultiPartBlockStateBuilder end() {
         return MultiPartBlockStateBuilder.this;
      }

      JsonObject toJson() {
         JsonObject out = new JsonObject();
         if (!this.conditions.isEmpty()) {
            JsonObject when = new JsonObject();
            Iterator var3 = this.conditions.asMap().entrySet().iterator();

            while(var3.hasNext()) {
               Entry<IProperty<?>, Collection<Comparable<?>>> e = (Entry)var3.next();
               StringBuilder activeString = new StringBuilder();

               Object val;
               for(Iterator var6 = ((Collection)e.getValue()).iterator(); var6.hasNext(); activeString.append(val.toString())) {
                  val = var6.next();
                  if (activeString.length() > 0) {
                     activeString.append("|");
                  }
               }

               when.addProperty(((IProperty)e.getKey()).getName(), activeString.toString());
            }

            if (this.useOr) {
               JsonObject innerWhen = when;
               when = new JsonObject();
               when.add("OR", innerWhen);
            }

            out.add("when", when);
         }

         out.add("apply", this.models.toJSON());
         return out;
      }

      public boolean canApplyTo(Block b) {
         return b.getStateContainer().getProperties().containsAll(this.conditions.keySet());
      }
   }
}
