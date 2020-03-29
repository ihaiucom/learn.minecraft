package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DisplayInfo {
   private final ITextComponent title;
   private final ITextComponent description;
   private final ItemStack icon;
   private final ResourceLocation background;
   private final FrameType frame;
   private final boolean showToast;
   private final boolean announceToChat;
   private final boolean hidden;
   private float x;
   private float y;

   public DisplayInfo(ItemStack p_i47586_1_, ITextComponent p_i47586_2_, ITextComponent p_i47586_3_, @Nullable ResourceLocation p_i47586_4_, FrameType p_i47586_5_, boolean p_i47586_6_, boolean p_i47586_7_, boolean p_i47586_8_) {
      this.title = p_i47586_2_;
      this.description = p_i47586_3_;
      this.icon = p_i47586_1_;
      this.background = p_i47586_4_;
      this.frame = p_i47586_5_;
      this.showToast = p_i47586_6_;
      this.announceToChat = p_i47586_7_;
      this.hidden = p_i47586_8_;
   }

   public void setPosition(float p_192292_1_, float p_192292_2_) {
      this.x = p_192292_1_;
      this.y = p_192292_2_;
   }

   public ITextComponent getTitle() {
      return this.title;
   }

   public ITextComponent getDescription() {
      return this.description;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getIcon() {
      return this.icon;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getBackground() {
      return this.background;
   }

   public FrameType getFrame() {
      return this.frame;
   }

   @OnlyIn(Dist.CLIENT)
   public float getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public float getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldShowToast() {
      return this.showToast;
   }

   public boolean shouldAnnounceToChat() {
      return this.announceToChat;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public static DisplayInfo deserialize(JsonObject p_192294_0_, JsonDeserializationContext p_192294_1_) {
      ITextComponent lvt_2_1_ = (ITextComponent)JSONUtils.deserializeClass(p_192294_0_, "title", p_192294_1_, ITextComponent.class);
      ITextComponent lvt_3_1_ = (ITextComponent)JSONUtils.deserializeClass(p_192294_0_, "description", p_192294_1_, ITextComponent.class);
      if (lvt_2_1_ != null && lvt_3_1_ != null) {
         ItemStack lvt_4_1_ = deserializeIcon(JSONUtils.getJsonObject(p_192294_0_, "icon"));
         ResourceLocation lvt_5_1_ = p_192294_0_.has("background") ? new ResourceLocation(JSONUtils.getString(p_192294_0_, "background")) : null;
         FrameType lvt_6_1_ = p_192294_0_.has("frame") ? FrameType.byName(JSONUtils.getString(p_192294_0_, "frame")) : FrameType.TASK;
         boolean lvt_7_1_ = JSONUtils.getBoolean(p_192294_0_, "show_toast", true);
         boolean lvt_8_1_ = JSONUtils.getBoolean(p_192294_0_, "announce_to_chat", true);
         boolean lvt_9_1_ = JSONUtils.getBoolean(p_192294_0_, "hidden", false);
         return new DisplayInfo(lvt_4_1_, lvt_2_1_, lvt_3_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_, lvt_9_1_);
      } else {
         throw new JsonSyntaxException("Both title and description must be set");
      }
   }

   private static ItemStack deserializeIcon(JsonObject p_193221_0_) {
      if (!p_193221_0_.has("item")) {
         throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
      } else {
         Item lvt_1_1_ = JSONUtils.getItem(p_193221_0_, "item");
         if (p_193221_0_.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            ItemStack lvt_2_1_ = new ItemStack(lvt_1_1_);
            if (p_193221_0_.has("nbt")) {
               try {
                  CompoundNBT lvt_3_1_ = JsonToNBT.getTagFromJson(JSONUtils.getString(p_193221_0_.get("nbt"), "nbt"));
                  lvt_2_1_.setTag(lvt_3_1_);
               } catch (CommandSyntaxException var4) {
                  throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
               }
            }

            return lvt_2_1_;
         }
      }
   }

   public void write(PacketBuffer p_192290_1_) {
      p_192290_1_.writeTextComponent(this.title);
      p_192290_1_.writeTextComponent(this.description);
      p_192290_1_.writeItemStack(this.icon);
      p_192290_1_.writeEnumValue(this.frame);
      int lvt_2_1_ = 0;
      if (this.background != null) {
         lvt_2_1_ |= 1;
      }

      if (this.showToast) {
         lvt_2_1_ |= 2;
      }

      if (this.hidden) {
         lvt_2_1_ |= 4;
      }

      p_192290_1_.writeInt(lvt_2_1_);
      if (this.background != null) {
         p_192290_1_.writeResourceLocation(this.background);
      }

      p_192290_1_.writeFloat(this.x);
      p_192290_1_.writeFloat(this.y);
   }

   public static DisplayInfo read(PacketBuffer p_192295_0_) {
      ITextComponent lvt_1_1_ = p_192295_0_.readTextComponent();
      ITextComponent lvt_2_1_ = p_192295_0_.readTextComponent();
      ItemStack lvt_3_1_ = p_192295_0_.readItemStack();
      FrameType lvt_4_1_ = (FrameType)p_192295_0_.readEnumValue(FrameType.class);
      int lvt_5_1_ = p_192295_0_.readInt();
      ResourceLocation lvt_6_1_ = (lvt_5_1_ & 1) != 0 ? p_192295_0_.readResourceLocation() : null;
      boolean lvt_7_1_ = (lvt_5_1_ & 2) != 0;
      boolean lvt_8_1_ = (lvt_5_1_ & 4) != 0;
      DisplayInfo lvt_9_1_ = new DisplayInfo(lvt_3_1_, lvt_1_1_, lvt_2_1_, lvt_6_1_, lvt_4_1_, lvt_7_1_, false, lvt_8_1_);
      lvt_9_1_.setPosition(p_192295_0_.readFloat(), p_192295_0_.readFloat());
      return lvt_9_1_;
   }

   public JsonElement serialize() {
      JsonObject lvt_1_1_ = new JsonObject();
      lvt_1_1_.add("icon", this.serializeIcon());
      lvt_1_1_.add("title", ITextComponent.Serializer.toJsonTree(this.title));
      lvt_1_1_.add("description", ITextComponent.Serializer.toJsonTree(this.description));
      lvt_1_1_.addProperty("frame", this.frame.getName());
      lvt_1_1_.addProperty("show_toast", this.showToast);
      lvt_1_1_.addProperty("announce_to_chat", this.announceToChat);
      lvt_1_1_.addProperty("hidden", this.hidden);
      if (this.background != null) {
         lvt_1_1_.addProperty("background", this.background.toString());
      }

      return lvt_1_1_;
   }

   private JsonObject serializeIcon() {
      JsonObject lvt_1_1_ = new JsonObject();
      lvt_1_1_.addProperty("item", Registry.ITEM.getKey(this.icon.getItem()).toString());
      if (this.icon.hasTag()) {
         lvt_1_1_.addProperty("nbt", this.icon.getTag().toString());
      }

      return lvt_1_1_;
   }
}
