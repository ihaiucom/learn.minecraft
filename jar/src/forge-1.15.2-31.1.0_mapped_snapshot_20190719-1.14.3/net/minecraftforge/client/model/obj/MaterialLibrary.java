package net.minecraftforge.client.model.obj;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import joptsimple.internal.Strings;
import net.minecraft.client.renderer.Vector4f;

public class MaterialLibrary {
   public static final MaterialLibrary EMPTY = new MaterialLibrary();
   final Map<String, MaterialLibrary.Material> materials = Maps.newHashMap();

   private MaterialLibrary() {
   }

   public MaterialLibrary(LineReader reader) throws IOException {
      MaterialLibrary.Material currentMaterial = null;

      String[] line;
      while((line = reader.readAndSplitLine(true)) != null) {
         String var4 = line[0];
         byte var5 = -1;
         switch(var4.hashCode()) {
         case -1081377991:
            if (var4.equals("map_Ka")) {
               var5 = 2;
            }
            break;
         case -1081377988:
            if (var4.equals("map_Kd")) {
               var5 = 5;
            }
            break;
         case -1081377973:
            if (var4.equals("map_Ks")) {
               var5 = 8;
            }
            break;
         case -1048831483:
            if (var4.equals("newmtl")) {
               var5 = 0;
            }
            break;
         case 100:
            if (var4.equals("d")) {
               var5 = 9;
            }
            break;
         case 2422:
            if (var4.equals("Ka")) {
               var5 = 1;
            }
            break;
         case 2425:
            if (var4.equals("Kd")) {
               var5 = 3;
            }
            break;
         case 2440:
            if (var4.equals("Ks")) {
               var5 = 6;
            }
            break;
         case 2533:
            if (var4.equals("Ns")) {
               var5 = 7;
            }
            break;
         case 2718:
            if (var4.equals("Tr")) {
               var5 = 10;
            }
            break;
         case 936139679:
            if (var4.equals("forge_TintIndex")) {
               var5 = 4;
            }
         }

         switch(var5) {
         case 0:
            String name = Strings.join((String[])Arrays.copyOfRange(line, 1, line.length), " ");
            currentMaterial = new MaterialLibrary.Material(name);
            this.materials.put(name, currentMaterial);
            break;
         case 1:
            currentMaterial.ambientColor = OBJModel.parseVector4(line);
            break;
         case 2:
            currentMaterial.ambientColorMap = line[line.length - 1];
            break;
         case 3:
            currentMaterial.diffuseColor = OBJModel.parseVector4(line);
            break;
         case 4:
            currentMaterial.diffuseTintIndex = Integer.parseInt(line[1]);
            break;
         case 5:
            currentMaterial.diffuseColorMap = line[line.length - 1];
            break;
         case 6:
            currentMaterial.specularColor = OBJModel.parseVector4(line);
            break;
         case 7:
            currentMaterial.specularHighlight = Float.parseFloat(line[1]);
            break;
         case 8:
            currentMaterial.specularColorMap = line[line.length - 1];
            break;
         case 9:
            currentMaterial.dissolve = Float.parseFloat(line[1]);
            break;
         case 10:
            currentMaterial.transparency = Float.parseFloat(line[1]);
         }
      }

   }

   public MaterialLibrary.Material getMaterial(String mat) {
      if (!this.materials.containsKey(mat)) {
         throw new NoSuchElementException("The material was not found in the library: " + mat);
      } else {
         return (MaterialLibrary.Material)this.materials.get(mat);
      }
   }

   public static class Material {
      public final String name;
      public Vector4f ambientColor = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
      public String ambientColorMap;
      public Vector4f diffuseColor = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      public String diffuseColorMap;
      public Vector4f specularColor = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
      public float specularHighlight = 0.0F;
      public String specularColorMap;
      public float dissolve = 1.0F;
      public float transparency = 0.0F;
      public int diffuseTintIndex = 0;

      public Material(String name) {
         this.name = name;
      }
   }
}
