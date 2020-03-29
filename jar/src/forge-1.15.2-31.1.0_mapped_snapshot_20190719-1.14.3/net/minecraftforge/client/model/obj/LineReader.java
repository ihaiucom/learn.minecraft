package net.minecraftforge.client.model.obj;

import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import joptsimple.internal.Strings;
import net.minecraft.resources.IResource;

public class LineReader implements AutoCloseable {
   InputStreamReader lineStream;
   BufferedReader lineReader;

   public LineReader(IResource resource) {
      this.lineStream = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);
      this.lineReader = new BufferedReader(this.lineStream);
   }

   @Nullable
   public String[] readAndSplitLine(boolean ignoreEmptyLines) throws IOException {
      do {
         String currentLine = this.lineReader.readLine();
         if (currentLine == null) {
            return null;
         }

         List<String> lineParts = new ArrayList();
         if (currentLine.startsWith("#")) {
            currentLine = "";
         }

         boolean hasContinuation;
         if (currentLine.length() > 0) {
            do {
               hasContinuation = currentLine.endsWith("\\");
               String tmp = hasContinuation ? currentLine.substring(0, currentLine.length() - 1) : currentLine;
               Arrays.stream(tmp.split("[\t ]+")).filter((s) -> {
                  return !Strings.isNullOrEmpty(s);
               }).forEach(lineParts::add);
               if (hasContinuation) {
                  currentLine = this.lineReader.readLine();
                  if (currentLine == null || currentLine.length() == 0 || currentLine.startsWith("#")) {
                     break;
                  }
               }
            } while(hasContinuation);
         }

         if (lineParts.size() > 0) {
            return (String[])lineParts.toArray(new String[0]);
         }
      } while(ignoreEmptyLines);

      return new String[0];
   }

   public void close() throws Exception {
      this.lineReader.close();
      this.lineStream.close();
   }
}
