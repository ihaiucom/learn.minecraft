package net.minecraftforge.fml.common.network;

import io.netty.buffer.ByteBuf;

public class ByteBufUtils {
   public static String getContentDump(ByteBuf buffer) {
      int currentLength = buffer.readableBytes();
      StringBuffer returnString = new StringBuffer(currentLength * 3 + currentLength + currentLength / 4 + 30);

      int i;
      int j;
      for(i = 0; i < currentLength; ++i) {
         if (i != 0 && i % 16 == 0) {
            returnString.append('\t');
            j = i - 16;

            while(true) {
               if (j >= i) {
                  returnString.append("\n");
                  break;
               }

               if (buffer.getByte(j) >= 32 && buffer.getByte(j) <= 127) {
                  returnString.append((char)buffer.getByte(j));
               } else {
                  returnString.append('.');
               }

               ++j;
            }
         }

         returnString.append(Integer.toString((buffer.getByte(i) & 240) >> 4, 16) + Integer.toString((buffer.getByte(i) & 15) >> 0, 16));
         returnString.append(' ');
      }

      if (i != 0 && i % 16 != 0) {
         for(j = 0; j < (16 - i % 16) * 3; ++j) {
            returnString.append(' ');
         }
      }

      returnString.append('\t');
      if (i > 0 && i % 16 == 0) {
         j = i - 16;
      } else {
         j = i - i % 16;
      }

      for(; i >= 0 && j < i; ++j) {
         if (buffer.getByte(j) >= 32 && buffer.getByte(j) <= 127) {
            returnString.append((char)buffer.getByte(j));
         } else {
            returnString.append('.');
         }
      }

      returnString.append('\n');
      returnString.append("Length: " + currentLength);
      return returnString.toString();
   }
}
