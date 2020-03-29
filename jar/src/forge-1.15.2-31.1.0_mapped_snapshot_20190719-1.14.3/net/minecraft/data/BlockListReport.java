package net.minecraft.data;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class BlockListReport implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public BlockListReport(DataGenerator p_i48265_1_) {
      this.generator = p_i48265_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      JsonObject lvt_2_1_ = new JsonObject();
      Iterator var3 = Registry.BLOCK.iterator();

      while(var3.hasNext()) {
         Block lvt_4_1_ = (Block)var3.next();
         ResourceLocation lvt_5_1_ = Registry.BLOCK.getKey(lvt_4_1_);
         JsonObject lvt_6_1_ = new JsonObject();
         StateContainer<Block, BlockState> lvt_7_1_ = lvt_4_1_.getStateContainer();
         if (!lvt_7_1_.getProperties().isEmpty()) {
            JsonObject lvt_8_1_ = new JsonObject();
            Iterator var9 = lvt_7_1_.getProperties().iterator();

            while(true) {
               if (!var9.hasNext()) {
                  lvt_6_1_.add("properties", lvt_8_1_);
                  break;
               }

               IProperty<?> lvt_10_1_ = (IProperty)var9.next();
               JsonArray lvt_11_1_ = new JsonArray();
               Iterator var12 = lvt_10_1_.getAllowedValues().iterator();

               while(var12.hasNext()) {
                  Comparable<?> lvt_13_1_ = (Comparable)var12.next();
                  lvt_11_1_.add(Util.getValueName(lvt_10_1_, lvt_13_1_));
               }

               lvt_8_1_.add(lvt_10_1_.getName(), lvt_11_1_);
            }
         }

         JsonArray lvt_8_2_ = new JsonArray();

         JsonObject lvt_11_2_;
         for(UnmodifiableIterator var17 = lvt_7_1_.getValidStates().iterator(); var17.hasNext(); lvt_8_2_.add(lvt_11_2_)) {
            BlockState lvt_10_2_ = (BlockState)var17.next();
            lvt_11_2_ = new JsonObject();
            JsonObject lvt_12_1_ = new JsonObject();
            Iterator var21 = lvt_7_1_.getProperties().iterator();

            while(var21.hasNext()) {
               IProperty<?> lvt_14_1_ = (IProperty)var21.next();
               lvt_12_1_.addProperty(lvt_14_1_.getName(), Util.getValueName(lvt_14_1_, lvt_10_2_.get(lvt_14_1_)));
            }

            if (lvt_12_1_.size() > 0) {
               lvt_11_2_.add("properties", lvt_12_1_);
            }

            lvt_11_2_.addProperty("id", Block.getStateId(lvt_10_2_));
            if (lvt_10_2_ == lvt_4_1_.getDefaultState()) {
               lvt_11_2_.addProperty("default", true);
            }
         }

         lvt_6_1_.add("states", lvt_8_2_);
         lvt_2_1_.add(lvt_5_1_.toString(), lvt_6_1_);
      }

      Path lvt_3_1_ = this.generator.getOutputFolder().resolve("reports/blocks.json");
      IDataProvider.save(GSON, p_200398_1_, lvt_2_1_, lvt_3_1_);
   }

   public String getName() {
      return "Block List";
   }
}
