package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCommandListPacket implements IPacket<IClientPlayNetHandler> {
   private RootCommandNode<ISuggestionProvider> root;

   public SCommandListPacket() {
   }

   public SCommandListPacket(RootCommandNode<ISuggestionProvider> p_i47940_1_) {
      this.root = p_i47940_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      SCommandListPacket.Entry[] ascommandlistpacket$entry = new SCommandListPacket.Entry[p_148837_1_.readVarInt()];
      Deque<SCommandListPacket.Entry> deque = new ArrayDeque(ascommandlistpacket$entry.length);

      for(int i = 0; i < ascommandlistpacket$entry.length; ++i) {
         ascommandlistpacket$entry[i] = this.readEntry(p_148837_1_);
         deque.add(ascommandlistpacket$entry[i]);
      }

      boolean flag;
      do {
         if (deque.isEmpty()) {
            this.root = (RootCommandNode)ascommandlistpacket$entry[p_148837_1_.readVarInt()].node;
            return;
         }

         flag = false;
         Iterator iterator = deque.iterator();

         while(iterator.hasNext()) {
            SCommandListPacket.Entry scommandlistpacket$entry = (SCommandListPacket.Entry)iterator.next();
            if (scommandlistpacket$entry.createCommandNode(ascommandlistpacket$entry)) {
               iterator.remove();
               flag = true;
            }
         }
      } while(flag);

      throw new IllegalStateException("Server sent an impossible command tree");
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      Map<CommandNode<ISuggestionProvider>, Integer> map = Maps.newHashMap();
      Deque<CommandNode<ISuggestionProvider>> deque = new ArrayDeque();
      deque.add(this.root);

      while(!deque.isEmpty()) {
         CommandNode<ISuggestionProvider> commandnode = (CommandNode)deque.pollFirst();
         if (!map.containsKey(commandnode)) {
            int i = map.size();
            map.put(commandnode, i);
            deque.addAll(commandnode.getChildren());
            if (commandnode.getRedirect() != null) {
               deque.add(commandnode.getRedirect());
            }
         }
      }

      CommandNode<ISuggestionProvider>[] commandnode2 = new CommandNode[map.size()];

      java.util.Map.Entry entry;
      for(Iterator var10 = map.entrySet().iterator(); var10.hasNext(); commandnode2[(Integer)entry.getValue()] = (CommandNode)entry.getKey()) {
         entry = (java.util.Map.Entry)var10.next();
      }

      p_148840_1_.writeVarInt(commandnode2.length);
      CommandNode[] var11 = commandnode2;
      int var12 = commandnode2.length;

      for(int var7 = 0; var7 < var12; ++var7) {
         CommandNode<ISuggestionProvider> commandnode1 = var11[var7];
         this.writeCommandNode(p_148840_1_, commandnode1, map);
      }

      p_148840_1_.writeVarInt((Integer)map.get(this.root));
   }

   private SCommandListPacket.Entry readEntry(PacketBuffer p_197692_1_) {
      byte b0 = p_197692_1_.readByte();
      int[] aint = p_197692_1_.readVarIntArray();
      int i = (b0 & 8) != 0 ? p_197692_1_.readVarInt() : 0;
      ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = this.readArgumentBuilder(p_197692_1_, b0);
      return new SCommandListPacket.Entry(argumentbuilder, b0, i, aint);
   }

   @Nullable
   private ArgumentBuilder<ISuggestionProvider, ?> readArgumentBuilder(PacketBuffer p_197695_1_, byte p_197695_2_) {
      int i = p_197695_2_ & 3;
      if (i == 2) {
         String s = p_197695_1_.readString(32767);
         ArgumentType<?> argumenttype = ArgumentTypes.deserialize(p_197695_1_);
         if (argumenttype == null) {
            if ((p_197695_2_ & 16) != 0) {
               p_197695_1_.readResourceLocation();
            }

            return null;
         } else {
            RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);
            if ((p_197695_2_ & 16) != 0) {
               requiredargumentbuilder.suggests(SuggestionProviders.get(p_197695_1_.readResourceLocation()));
            }

            return requiredargumentbuilder;
         }
      } else {
         return i == 1 ? LiteralArgumentBuilder.literal(p_197695_1_.readString(32767)) : null;
      }
   }

   private void writeCommandNode(PacketBuffer p_197696_1_, CommandNode<ISuggestionProvider> p_197696_2_, Map<CommandNode<ISuggestionProvider>, Integer> p_197696_3_) {
      byte b0 = 0;
      if (p_197696_2_.getRedirect() != null) {
         b0 = (byte)(b0 | 8);
      }

      if (p_197696_2_.getCommand() != null) {
         b0 = (byte)(b0 | 4);
      }

      if (p_197696_2_ instanceof RootCommandNode) {
         b0 = (byte)(b0 | 0);
      } else if (p_197696_2_ instanceof ArgumentCommandNode) {
         b0 = (byte)(b0 | 2);
         if (((ArgumentCommandNode)p_197696_2_).getCustomSuggestions() != null) {
            b0 = (byte)(b0 | 16);
         }
      } else {
         if (!(p_197696_2_ instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + p_197696_2_);
         }

         b0 = (byte)(b0 | 1);
      }

      p_197696_1_.writeByte(b0);
      p_197696_1_.writeVarInt(p_197696_2_.getChildren().size());
      Iterator var5 = p_197696_2_.getChildren().iterator();

      while(var5.hasNext()) {
         CommandNode<ISuggestionProvider> commandnode = (CommandNode)var5.next();
         p_197696_1_.writeVarInt((Integer)p_197696_3_.get(commandnode));
      }

      if (p_197696_2_.getRedirect() != null) {
         p_197696_1_.writeVarInt((Integer)p_197696_3_.get(p_197696_2_.getRedirect()));
      }

      if (p_197696_2_ instanceof ArgumentCommandNode) {
         ArgumentCommandNode<ISuggestionProvider, ?> argumentcommandnode = (ArgumentCommandNode)p_197696_2_;
         p_197696_1_.writeString(argumentcommandnode.getName());
         ArgumentTypes.serialize(p_197696_1_, argumentcommandnode.getType());
         if (argumentcommandnode.getCustomSuggestions() != null) {
            p_197696_1_.writeResourceLocation(SuggestionProviders.getId(argumentcommandnode.getCustomSuggestions()));
         }
      } else if (p_197696_2_ instanceof LiteralCommandNode) {
         p_197696_1_.writeString(((LiteralCommandNode)p_197696_2_).getLiteral());
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCommandList(this);
   }

   @OnlyIn(Dist.CLIENT)
   public RootCommandNode<ISuggestionProvider> getRoot() {
      return this.root;
   }

   static class Entry {
      @Nullable
      private final ArgumentBuilder<ISuggestionProvider, ?> argBuilder;
      private final byte flags;
      private final int redirectTarget;
      private final int[] children;
      private CommandNode<ISuggestionProvider> node;

      private Entry(@Nullable ArgumentBuilder<ISuggestionProvider, ?> p_i48139_1_, byte p_i48139_2_, int p_i48139_3_, int[] p_i48139_4_) {
         this.argBuilder = p_i48139_1_;
         this.flags = p_i48139_2_;
         this.redirectTarget = p_i48139_3_;
         this.children = p_i48139_4_;
      }

      public boolean createCommandNode(SCommandListPacket.Entry[] p_197723_1_) {
         if (this.node == null) {
            if (this.argBuilder == null) {
               this.node = new RootCommandNode();
            } else {
               if ((this.flags & 8) != 0) {
                  if (p_197723_1_[this.redirectTarget].node == null) {
                     return false;
                  }

                  this.argBuilder.redirect(p_197723_1_[this.redirectTarget].node);
               }

               if ((this.flags & 4) != 0) {
                  this.argBuilder.executes((p_lambda$createCommandNode$0_0_) -> {
                     return 0;
                  });
               }

               this.node = this.argBuilder.build();
            }
         }

         int[] var2 = this.children;
         int var3 = var2.length;

         int var4;
         int j;
         for(var4 = 0; var4 < var3; ++var4) {
            j = var2[var4];
            if (p_197723_1_[j].node == null) {
               return false;
            }
         }

         var2 = this.children;
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            j = var2[var4];
            CommandNode<ISuggestionProvider> commandnode = p_197723_1_[j].node;
            if (!(commandnode instanceof RootCommandNode)) {
               this.node.addChild(commandnode);
            }
         }

         return true;
      }

      // $FF: synthetic method
      Entry(ArgumentBuilder p_i48140_1_, byte p_i48140_2_, int p_i48140_3_, int[] p_i48140_4_, Object p_i48140_5_) {
         this(p_i48140_1_, p_i48140_2_, p_i48140_3_, p_i48140_4_);
      }
   }
}
