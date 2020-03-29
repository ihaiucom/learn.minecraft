package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
   private final Advancement parent;
   private final DisplayInfo display;
   private final AdvancementRewards rewards;
   private final ResourceLocation id;
   private final Map<String, Criterion> criteria;
   private final String[][] requirements;
   private final Set<Advancement> children = Sets.newLinkedHashSet();
   private final ITextComponent displayText;

   public Advancement(ResourceLocation p_i47472_1_, @Nullable Advancement p_i47472_2_, @Nullable DisplayInfo p_i47472_3_, AdvancementRewards p_i47472_4_, Map<String, Criterion> p_i47472_5_, String[][] p_i47472_6_) {
      this.id = p_i47472_1_;
      this.display = p_i47472_3_;
      this.criteria = ImmutableMap.copyOf(p_i47472_5_);
      this.parent = p_i47472_2_;
      this.rewards = p_i47472_4_;
      this.requirements = p_i47472_6_;
      if (p_i47472_2_ != null) {
         p_i47472_2_.addChild(this);
      }

      if (p_i47472_3_ == null) {
         this.displayText = new StringTextComponent(p_i47472_1_.toString());
      } else {
         ITextComponent lvt_7_1_ = p_i47472_3_.getTitle();
         TextFormatting lvt_8_1_ = p_i47472_3_.getFrame().getFormat();
         ITextComponent lvt_9_1_ = lvt_7_1_.deepCopy().applyTextStyle(lvt_8_1_).appendText("\n").appendSibling(p_i47472_3_.getDescription());
         ITextComponent lvt_10_1_ = lvt_7_1_.deepCopy().applyTextStyle((p_211567_1_) -> {
            p_211567_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, lvt_9_1_));
         });
         this.displayText = (new StringTextComponent("[")).appendSibling(lvt_10_1_).appendText("]").applyTextStyle(lvt_8_1_);
      }

   }

   public Advancement.Builder copy() {
      return new Advancement.Builder(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
   }

   @Nullable
   public Advancement getParent() {
      return this.parent;
   }

   @Nullable
   public DisplayInfo getDisplay() {
      return this.display;
   }

   public AdvancementRewards getRewards() {
      return this.rewards;
   }

   public String toString() {
      return "SimpleAdvancement{id=" + this.getId() + ", parent=" + (this.parent == null ? "null" : this.parent.getId()) + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
   }

   public Iterable<Advancement> getChildren() {
      return this.children;
   }

   public Map<String, Criterion> getCriteria() {
      return this.criteria;
   }

   @OnlyIn(Dist.CLIENT)
   public int getRequirementCount() {
      return this.requirements.length;
   }

   public void addChild(Advancement p_192071_1_) {
      this.children.add(p_192071_1_);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Advancement)) {
         return false;
      } else {
         Advancement lvt_2_1_ = (Advancement)p_equals_1_;
         return this.id.equals(lvt_2_1_.id);
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String[][] getRequirements() {
      return this.requirements;
   }

   public ITextComponent getDisplayText() {
      return this.displayText;
   }

   public static class Builder {
      private ResourceLocation parentId;
      private Advancement parent;
      private DisplayInfo display;
      private AdvancementRewards rewards;
      private Map<String, Criterion> criteria;
      private String[][] requirements;
      private IRequirementsStrategy requirementsStrategy;

      private Builder(@Nullable ResourceLocation p_i47414_1_, @Nullable DisplayInfo p_i47414_2_, AdvancementRewards p_i47414_3_, Map<String, Criterion> p_i47414_4_, String[][] p_i47414_5_) {
         this.rewards = AdvancementRewards.EMPTY;
         this.criteria = Maps.newLinkedHashMap();
         this.requirementsStrategy = IRequirementsStrategy.AND;
         this.parentId = p_i47414_1_;
         this.display = p_i47414_2_;
         this.rewards = p_i47414_3_;
         this.criteria = p_i47414_4_;
         this.requirements = p_i47414_5_;
      }

      private Builder() {
         this.rewards = AdvancementRewards.EMPTY;
         this.criteria = Maps.newLinkedHashMap();
         this.requirementsStrategy = IRequirementsStrategy.AND;
      }

      public static Advancement.Builder builder() {
         return new Advancement.Builder();
      }

      public Advancement.Builder withParent(Advancement p_203905_1_) {
         this.parent = p_203905_1_;
         return this;
      }

      public Advancement.Builder withParentId(ResourceLocation p_200272_1_) {
         this.parentId = p_200272_1_;
         return this;
      }

      public Advancement.Builder func_215092_a(ItemStack p_215092_1_, ITextComponent p_215092_2_, ITextComponent p_215092_3_, @Nullable ResourceLocation p_215092_4_, FrameType p_215092_5_, boolean p_215092_6_, boolean p_215092_7_, boolean p_215092_8_) {
         return this.withDisplay(new DisplayInfo(p_215092_1_, p_215092_2_, p_215092_3_, p_215092_4_, p_215092_5_, p_215092_6_, p_215092_7_, p_215092_8_));
      }

      public Advancement.Builder withDisplay(IItemProvider p_203902_1_, ITextComponent p_203902_2_, ITextComponent p_203902_3_, @Nullable ResourceLocation p_203902_4_, FrameType p_203902_5_, boolean p_203902_6_, boolean p_203902_7_, boolean p_203902_8_) {
         return this.withDisplay(new DisplayInfo(new ItemStack(p_203902_1_.asItem()), p_203902_2_, p_203902_3_, p_203902_4_, p_203902_5_, p_203902_6_, p_203902_7_, p_203902_8_));
      }

      public Advancement.Builder withDisplay(DisplayInfo p_203903_1_) {
         this.display = p_203903_1_;
         return this;
      }

      public Advancement.Builder withRewards(AdvancementRewards.Builder p_200271_1_) {
         return this.withRewards(p_200271_1_.build());
      }

      public Advancement.Builder withRewards(AdvancementRewards p_200274_1_) {
         this.rewards = p_200274_1_;
         return this;
      }

      public Advancement.Builder withCriterion(String p_200275_1_, ICriterionInstance p_200275_2_) {
         return this.withCriterion(p_200275_1_, new Criterion(p_200275_2_));
      }

      public Advancement.Builder withCriterion(String p_200276_1_, Criterion p_200276_2_) {
         if (this.criteria.containsKey(p_200276_1_)) {
            throw new IllegalArgumentException("Duplicate criterion " + p_200276_1_);
         } else {
            this.criteria.put(p_200276_1_, p_200276_2_);
            return this;
         }
      }

      public Advancement.Builder withRequirementsStrategy(IRequirementsStrategy p_200270_1_) {
         this.requirementsStrategy = p_200270_1_;
         return this;
      }

      public boolean resolveParent(Function<ResourceLocation, Advancement> p_192058_1_) {
         if (this.parentId == null) {
            return true;
         } else {
            if (this.parent == null) {
               this.parent = (Advancement)p_192058_1_.apply(this.parentId);
            }

            return this.parent != null;
         }
      }

      public Advancement build(ResourceLocation p_192056_1_) {
         if (!this.resolveParent((p_199750_0_) -> {
            return null;
         })) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
         } else {
            if (this.requirements == null) {
               this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Advancement(p_192056_1_, this.parent, this.display, this.rewards, this.criteria, this.requirements);
         }
      }

      public Advancement register(Consumer<Advancement> p_203904_1_, String p_203904_2_) {
         Advancement lvt_3_1_ = this.build(new ResourceLocation(p_203904_2_));
         p_203904_1_.accept(lvt_3_1_);
         return lvt_3_1_;
      }

      public JsonObject serialize() {
         if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
         }

         JsonObject lvt_1_1_ = new JsonObject();
         if (this.parent != null) {
            lvt_1_1_.addProperty("parent", this.parent.getId().toString());
         } else if (this.parentId != null) {
            lvt_1_1_.addProperty("parent", this.parentId.toString());
         }

         if (this.display != null) {
            lvt_1_1_.add("display", this.display.serialize());
         }

         lvt_1_1_.add("rewards", this.rewards.serialize());
         JsonObject lvt_2_1_ = new JsonObject();
         Iterator var3 = this.criteria.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<String, Criterion> lvt_4_1_ = (Entry)var3.next();
            lvt_2_1_.add((String)lvt_4_1_.getKey(), ((Criterion)lvt_4_1_.getValue()).serialize());
         }

         lvt_1_1_.add("criteria", lvt_2_1_);
         JsonArray lvt_3_1_ = new JsonArray();
         String[][] var14 = this.requirements;
         int var5 = var14.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String[] lvt_7_1_ = var14[var6];
            JsonArray lvt_8_1_ = new JsonArray();
            String[] var9 = lvt_7_1_;
            int var10 = lvt_7_1_.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               String lvt_12_1_ = var9[var11];
               lvt_8_1_.add(lvt_12_1_);
            }

            lvt_3_1_.add(lvt_8_1_);
         }

         lvt_1_1_.add("requirements", lvt_3_1_);
         return lvt_1_1_;
      }

      public void writeTo(PacketBuffer p_192057_1_) {
         if (this.parentId == null) {
            p_192057_1_.writeBoolean(false);
         } else {
            p_192057_1_.writeBoolean(true);
            p_192057_1_.writeResourceLocation(this.parentId);
         }

         if (this.display == null) {
            p_192057_1_.writeBoolean(false);
         } else {
            p_192057_1_.writeBoolean(true);
            this.display.write(p_192057_1_);
         }

         Criterion.serializeToNetwork(this.criteria, p_192057_1_);
         p_192057_1_.writeVarInt(this.requirements.length);
         String[][] var2 = this.requirements;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String[] lvt_5_1_ = var2[var4];
            p_192057_1_.writeVarInt(lvt_5_1_.length);
            String[] var6 = lvt_5_1_;
            int var7 = lvt_5_1_.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String lvt_9_1_ = var6[var8];
               p_192057_1_.writeString(lvt_9_1_);
            }
         }

      }

      public String toString() {
         return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
      }

      public static Advancement.Builder deserialize(JsonObject p_192059_0_, JsonDeserializationContext p_192059_1_) {
         ResourceLocation lvt_2_1_ = p_192059_0_.has("parent") ? new ResourceLocation(JSONUtils.getString(p_192059_0_, "parent")) : null;
         DisplayInfo lvt_3_1_ = p_192059_0_.has("display") ? DisplayInfo.deserialize(JSONUtils.getJsonObject(p_192059_0_, "display"), p_192059_1_) : null;
         AdvancementRewards lvt_4_1_ = (AdvancementRewards)JSONUtils.deserializeClass(p_192059_0_, "rewards", AdvancementRewards.EMPTY, p_192059_1_, AdvancementRewards.class);
         Map<String, Criterion> lvt_5_1_ = Criterion.criteriaFromJson(JSONUtils.getJsonObject(p_192059_0_, "criteria"), p_192059_1_);
         if (lvt_5_1_.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
         } else {
            JsonArray lvt_6_1_ = JSONUtils.getJsonArray(p_192059_0_, "requirements", new JsonArray());
            String[][] lvt_7_1_ = new String[lvt_6_1_.size()][];

            int lvt_8_1_;
            int lvt_10_1_;
            for(lvt_8_1_ = 0; lvt_8_1_ < lvt_6_1_.size(); ++lvt_8_1_) {
               JsonArray lvt_9_1_ = JSONUtils.getJsonArray(lvt_6_1_.get(lvt_8_1_), "requirements[" + lvt_8_1_ + "]");
               lvt_7_1_[lvt_8_1_] = new String[lvt_9_1_.size()];

               for(lvt_10_1_ = 0; lvt_10_1_ < lvt_9_1_.size(); ++lvt_10_1_) {
                  lvt_7_1_[lvt_8_1_][lvt_10_1_] = JSONUtils.getString(lvt_9_1_.get(lvt_10_1_), "requirements[" + lvt_8_1_ + "][" + lvt_10_1_ + "]");
               }
            }

            if (lvt_7_1_.length == 0) {
               lvt_7_1_ = new String[lvt_5_1_.size()][];
               lvt_8_1_ = 0;

               String lvt_10_2_;
               for(Iterator var16 = lvt_5_1_.keySet().iterator(); var16.hasNext(); lvt_7_1_[lvt_8_1_++] = new String[]{lvt_10_2_}) {
                  lvt_10_2_ = (String)var16.next();
               }
            }

            String[][] var17 = lvt_7_1_;
            int var18 = lvt_7_1_.length;

            int var13;
            for(lvt_10_1_ = 0; lvt_10_1_ < var18; ++lvt_10_1_) {
               String[] lvt_11_1_ = var17[lvt_10_1_];
               if (lvt_11_1_.length == 0 && lvt_5_1_.isEmpty()) {
                  throw new JsonSyntaxException("Requirement entry cannot be empty");
               }

               String[] var12 = lvt_11_1_;
               var13 = lvt_11_1_.length;

               for(int var14 = 0; var14 < var13; ++var14) {
                  String lvt_15_1_ = var12[var14];
                  if (!lvt_5_1_.containsKey(lvt_15_1_)) {
                     throw new JsonSyntaxException("Unknown required criterion '" + lvt_15_1_ + "'");
                  }
               }
            }

            Iterator var19 = lvt_5_1_.keySet().iterator();

            String lvt_9_2_;
            boolean lvt_10_3_;
            do {
               if (!var19.hasNext()) {
                  return new Advancement.Builder(lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_7_1_);
               }

               lvt_9_2_ = (String)var19.next();
               lvt_10_3_ = false;
               String[][] var22 = lvt_7_1_;
               int var24 = lvt_7_1_.length;

               for(var13 = 0; var13 < var24; ++var13) {
                  String[] lvt_14_1_ = var22[var13];
                  if (ArrayUtils.contains(lvt_14_1_, lvt_9_2_)) {
                     lvt_10_3_ = true;
                     break;
                  }
               }
            } while(lvt_10_3_);

            throw new JsonSyntaxException("Criterion '" + lvt_9_2_ + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
         }
      }

      public static Advancement.Builder readFrom(PacketBuffer p_192060_0_) {
         ResourceLocation lvt_1_1_ = p_192060_0_.readBoolean() ? p_192060_0_.readResourceLocation() : null;
         DisplayInfo lvt_2_1_ = p_192060_0_.readBoolean() ? DisplayInfo.read(p_192060_0_) : null;
         Map<String, Criterion> lvt_3_1_ = Criterion.criteriaFromNetwork(p_192060_0_);
         String[][] lvt_4_1_ = new String[p_192060_0_.readVarInt()][];

         for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.length; ++lvt_5_1_) {
            lvt_4_1_[lvt_5_1_] = new String[p_192060_0_.readVarInt()];

            for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_4_1_[lvt_5_1_].length; ++lvt_6_1_) {
               lvt_4_1_[lvt_5_1_][lvt_6_1_] = p_192060_0_.readString(32767);
            }
         }

         return new Advancement.Builder(lvt_1_1_, lvt_2_1_, AdvancementRewards.EMPTY, lvt_3_1_, lvt_4_1_);
      }

      public Map<String, Criterion> getCriteria() {
         return this.criteria;
      }

      // $FF: synthetic method
      Builder(ResourceLocation p_i48200_1_, DisplayInfo p_i48200_2_, AdvancementRewards p_i48200_3_, Map p_i48200_4_, String[][] p_i48200_5_, Object p_i48200_6_) {
         this(p_i48200_1_, p_i48200_2_, p_i48200_3_, p_i48200_4_, p_i48200_5_);
      }
   }
}
