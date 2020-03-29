package net.minecraftforge.client.model.b3d;

import com.google.common.base.Joiner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Table.Cell;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.math.Vec2f;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class B3DModel {
   static final Logger logger = LogManager.getLogger("forge.B3DModel");
   private static final boolean printLoadedModels = "true".equals(System.getProperty("b3dloader.printLoadedModels"));
   private final List<B3DModel.Texture> textures;
   private final List<B3DModel.Brush> brushes;
   private final B3DModel.Node<?> root;
   private final ImmutableMap<String, B3DModel.Node<B3DModel.Mesh>> meshes;

   public B3DModel(List<B3DModel.Texture> textures, List<B3DModel.Brush> brushes, B3DModel.Node<?> root, ImmutableMap<String, B3DModel.Node<B3DModel.Mesh>> meshes) {
      this.textures = textures;
      this.brushes = brushes;
      this.root = root;
      this.meshes = meshes;
   }

   public List<B3DModel.Texture> getTextures() {
      return this.textures;
   }

   public List<B3DModel.Brush> getBrushes() {
      return this.brushes;
   }

   public B3DModel.Node<?> getRoot() {
      return this.root;
   }

   public ImmutableMap<String, B3DModel.Node<B3DModel.Mesh>> getMeshes() {
      return this.meshes;
   }

   public static class Bone implements B3DModel.IKind<B3DModel.Bone> {
      private B3DModel.Node<B3DModel.Bone> parent;
      private final List<Pair<B3DModel.Vertex, Float>> data;

      public Bone(List<Pair<B3DModel.Vertex, Float>> data) {
         this.data = data;
      }

      public List<Pair<B3DModel.Vertex, Float>> getData() {
         return this.data;
      }

      public void setParent(B3DModel.Node<B3DModel.Bone> parent) {
         this.parent = parent;
      }

      public B3DModel.Node<B3DModel.Bone> getParent() {
         return this.parent;
      }
   }

   public static class Mesh implements B3DModel.IKind<B3DModel.Mesh> {
      private B3DModel.Node<B3DModel.Mesh> parent;
      private final B3DModel.Brush brush;
      private final ImmutableList<B3DModel.Face> faces;
      private Set<B3DModel.Node<B3DModel.Bone>> bones = new HashSet();
      private ImmutableMultimap<B3DModel.Vertex, Pair<Float, B3DModel.Node<B3DModel.Bone>>> weightMap = ImmutableMultimap.of();

      public Mesh(Pair<B3DModel.Brush, List<B3DModel.Face>> data) {
         this.brush = (B3DModel.Brush)data.getLeft();
         this.faces = ImmutableList.copyOf((Collection)data.getRight());
      }

      public ImmutableMultimap<B3DModel.Vertex, Pair<Float, B3DModel.Node<B3DModel.Bone>>> getWeightMap() {
         return this.weightMap;
      }

      public ImmutableList<B3DModel.Face> bake(Function<B3DModel.Node<?>, Matrix4f> animator) {
         Builder<B3DModel.Face> builder = ImmutableList.builder();
         UnmodifiableIterator var3 = this.getFaces().iterator();

         while(var3.hasNext()) {
            B3DModel.Face f = (B3DModel.Face)var3.next();
            B3DModel.Vertex v1 = f.getV1().bake(this, animator);
            B3DModel.Vertex v2 = f.getV2().bake(this, animator);
            B3DModel.Vertex v3 = f.getV3().bake(this, animator);
            builder.add(new B3DModel.Face(v1, v2, v3, f.getBrush()));
         }

         return builder.build();
      }

      public B3DModel.Brush getBrush() {
         return this.brush;
      }

      public ImmutableList<B3DModel.Face> getFaces() {
         return this.faces;
      }

      public ImmutableSet<B3DModel.Node<B3DModel.Bone>> getBones() {
         return ImmutableSet.copyOf(this.bones);
      }

      public String toString() {
         return String.format("Mesh [pivot=%s, brush=%s, data=...]", super.toString(), this.brush);
      }

      public void setParent(B3DModel.Node<B3DModel.Mesh> parent) {
         this.parent = parent;
         ArrayDeque queue = new ArrayDeque(parent.getNodes().values());

         while(!queue.isEmpty()) {
            B3DModel.Node<?> node = (B3DModel.Node)queue.pop();
            if (node.getKind() instanceof B3DModel.Bone) {
               this.bones.add(node);
               queue.addAll(node.getNodes().values());
            }
         }

         com.google.common.collect.ImmutableMultimap.Builder<B3DModel.Vertex, Pair<Float, B3DModel.Node<B3DModel.Bone>>> builder = ImmutableMultimap.builder();
         UnmodifiableIterator var4 = this.getBones().iterator();

         while(var4.hasNext()) {
            B3DModel.Node<B3DModel.Bone> bone = (B3DModel.Node)var4.next();
            Iterator var6 = ((B3DModel.Bone)bone.getKind()).getData().iterator();

            while(var6.hasNext()) {
               Pair<B3DModel.Vertex, Float> b = (Pair)var6.next();
               builder.put(b.getLeft(), Pair.of(b.getRight(), bone));
            }
         }

         this.weightMap = builder.build();
      }

      public B3DModel.Node<B3DModel.Mesh> getParent() {
         return this.parent;
      }
   }

   public static class Pivot implements B3DModel.IKind<B3DModel.Pivot> {
      private B3DModel.Node<B3DModel.Pivot> parent;

      public void setParent(B3DModel.Node<B3DModel.Pivot> parent) {
         this.parent = parent;
      }

      public B3DModel.Node<B3DModel.Pivot> getParent() {
         return this.parent;
      }
   }

   public static class Node<K extends B3DModel.IKind<K>> {
      private final String name;
      private final Vector3f pos;
      private final Vector3f scale;
      private final Quaternion rot;
      private final ImmutableMap<String, B3DModel.Node<?>> nodes;
      @Nullable
      private B3DModel.Animation animation;
      private final K kind;
      @Nullable
      private B3DModel.Node<? extends B3DModel.IKind<?>> parent;

      public static <K extends B3DModel.IKind<K>> B3DModel.Node<K> create(String name, Vector3f pos, Vector3f scale, Quaternion rot, List<B3DModel.Node<?>> nodes, K kind) {
         return new B3DModel.Node(name, pos, scale, rot, nodes, kind);
      }

      public Node(String name, Vector3f pos, Vector3f scale, Quaternion rot, List<B3DModel.Node<?>> nodes, K kind) {
         this.name = name;
         this.pos = pos;
         this.scale = scale;
         this.rot = rot;
         this.nodes = this.buildNodeMap(nodes);
         this.kind = kind;
         kind.setParent(this);
         UnmodifiableIterator var7 = this.nodes.values().iterator();

         while(var7.hasNext()) {
            B3DModel.Node<?> child = (B3DModel.Node)var7.next();
            child.setParent(this);
         }

      }

      public void setAnimation(B3DModel.Animation animation) {
         this.animation = animation;
         ArrayDeque q = new ArrayDeque(this.nodes.values());

         while(!q.isEmpty()) {
            B3DModel.Node<?> node = (B3DModel.Node)q.pop();
            if (node.getAnimation() == null) {
               node.setAnimation(animation);
               q.addAll(node.getNodes().values());
            }
         }

      }

      public void setAnimation(Triple<Integer, Integer, Float> animData, Table<Integer, Optional<B3DModel.Node<?>>, B3DModel.Key> keyData) {
         com.google.common.collect.ImmutableTable.Builder<Integer, B3DModel.Node<?>, B3DModel.Key> builder = ImmutableTable.builder();
         Iterator var4 = keyData.cellSet().iterator();

         while(var4.hasNext()) {
            Cell<Integer, Optional<B3DModel.Node<?>>, B3DModel.Key> key = (Cell)var4.next();
            builder.put(key.getRowKey(), ((Optional)key.getColumnKey()).orElse(this), key.getValue());
         }

         this.setAnimation(new B3DModel.Animation((Integer)animData.getLeft(), (Integer)animData.getMiddle(), (Float)animData.getRight(), builder.build()));
      }

      private ImmutableMap<String, B3DModel.Node<?>> buildNodeMap(List<B3DModel.Node<?>> nodes) {
         com.google.common.collect.ImmutableMap.Builder<String, B3DModel.Node<?>> builder = ImmutableMap.builder();
         Iterator var3 = nodes.iterator();

         while(var3.hasNext()) {
            B3DModel.Node<?> node = (B3DModel.Node)var3.next();
            builder.put(node.getName(), node);
         }

         return builder.build();
      }

      public String getName() {
         return this.name;
      }

      public K getKind() {
         return this.kind;
      }

      public Vector3f getPos() {
         return this.pos;
      }

      public Vector3f getScale() {
         return this.scale;
      }

      public Quaternion getRot() {
         return this.rot;
      }

      public ImmutableMap<String, B3DModel.Node<?>> getNodes() {
         return this.nodes;
      }

      @Nullable
      public B3DModel.Animation getAnimation() {
         return this.animation;
      }

      @Nullable
      public B3DModel.Node<? extends B3DModel.IKind<?>> getParent() {
         return this.parent;
      }

      public void setParent(B3DModel.Node<? extends B3DModel.IKind<?>> parent) {
         this.parent = parent;
      }

      public String toString() {
         return String.format("Node [name=%s, kind=%s, pos=%s, scale=%s, rot=%s, keys=..., nodes=..., animation=%s]", this.name, this.kind, this.pos, this.scale, this.rot, this.animation);
      }
   }

   public interface IKind<K extends B3DModel.IKind<K>> {
      void setParent(B3DModel.Node<K> var1);

      B3DModel.Node<K> getParent();
   }

   public static class Animation {
      private final int flags;
      private final int frames;
      private final float fps;
      private final ImmutableTable<Integer, B3DModel.Node<?>, B3DModel.Key> keys;

      public Animation(int flags, int frames, float fps, ImmutableTable<Integer, B3DModel.Node<?>, B3DModel.Key> keys) {
         this.flags = flags;
         this.frames = frames;
         this.fps = fps;
         this.keys = keys;
      }

      public int getFlags() {
         return this.flags;
      }

      public int getFrames() {
         return this.frames;
      }

      public float getFps() {
         return this.fps;
      }

      public ImmutableTable<Integer, B3DModel.Node<?>, B3DModel.Key> getKeys() {
         return this.keys;
      }

      public String toString() {
         return String.format("Animation [flags=%s, frames=%s, fps=%s, keys=...]", this.flags, this.frames, this.fps);
      }
   }

   public static class Key {
      @Nullable
      private final Vector3f pos;
      @Nullable
      private final Vector3f scale;
      @Nullable
      private final Quaternion rot;

      public Key(@Nullable Vector3f pos, @Nullable Vector3f scale, @Nullable Quaternion rot) {
         this.pos = pos;
         this.scale = scale;
         this.rot = rot;
      }

      @Nullable
      public Vector3f getPos() {
         return this.pos;
      }

      @Nullable
      public Vector3f getScale() {
         return this.scale;
      }

      @Nullable
      public Quaternion getRot() {
         return this.rot;
      }

      public String toString() {
         return String.format("Key [pos=%s, scale=%s, rot=%s]", this.pos, this.scale, this.rot);
      }
   }

   public static class Face {
      private final B3DModel.Vertex v1;
      private final B3DModel.Vertex v2;
      private final B3DModel.Vertex v3;
      @Nullable
      private final B3DModel.Brush brush;
      private final Vector3f normal;

      public Face(B3DModel.Vertex v1, B3DModel.Vertex v2, B3DModel.Vertex v3, @Nullable B3DModel.Brush brush) {
         this(v1, v2, v3, brush, getNormal(v1, v2, v3));
      }

      public Face(B3DModel.Vertex v1, B3DModel.Vertex v2, B3DModel.Vertex v3, @Nullable B3DModel.Brush brush, Vector3f normal) {
         this.v1 = v1;
         this.v2 = v2;
         this.v3 = v3;
         this.brush = brush;
         this.normal = normal;
      }

      public B3DModel.Vertex getV1() {
         return this.v1;
      }

      public B3DModel.Vertex getV2() {
         return this.v2;
      }

      public B3DModel.Vertex getV3() {
         return this.v3;
      }

      @Nullable
      public B3DModel.Brush getBrush() {
         return this.brush;
      }

      public String toString() {
         return String.format("Face [v1=%s, v2=%s, v3=%s]", this.v1, this.v2, this.v3);
      }

      public Vector3f getNormal() {
         return this.normal;
      }

      public static Vector3f getNormal(B3DModel.Vertex v1, B3DModel.Vertex v2, B3DModel.Vertex v3) {
         Vector3f a = v2.getPos().func_229195_e_();
         a.sub(v1.getPos());
         Vector3f b = v3.getPos().func_229195_e_();
         b.sub(v1.getPos());
         Vector3f c = a.func_229195_e_();
         c.cross(b);
         c.func_229194_d_();
         return c;
      }
   }

   public static class Vertex {
      private final Vector3f pos;
      @Nullable
      private final Vector3f normal;
      @Nullable
      private final Vector4f color;
      private final Vector4f[] texCoords;

      public Vertex(Vector3f pos, @Nullable Vector3f normal, @Nullable Vector4f color, Vector4f[] texCoords) {
         this.pos = pos;
         this.normal = normal;
         this.color = color;
         this.texCoords = texCoords;
      }

      public B3DModel.Vertex bake(B3DModel.Mesh mesh, Function<B3DModel.Node<?>, Matrix4f> animator) {
         Float totalWeight = 0.0F;
         Matrix4f t = new Matrix4f();
         if (mesh.getWeightMap().get(this).isEmpty()) {
            t.func_226591_a_();
         } else {
            UnmodifiableIterator var5 = mesh.getWeightMap().get(this).iterator();

            while(var5.hasNext()) {
               Pair<Float, B3DModel.Node<B3DModel.Bone>> bone = (Pair)var5.next();
               totalWeight = totalWeight + (Float)bone.getLeft();
               Matrix4f bm = (Matrix4f)animator.apply(bone.getRight());
               bm.func_226592_a_((Float)bone.getLeft());
               t.add(bm);
            }

            if ((double)Math.abs(totalWeight) > 1.0E-4D) {
               t.func_226592_a_(1.0F / totalWeight);
            } else {
               t.func_226591_a_();
            }
         }

         TransformationMatrix trsr = new TransformationMatrix(t);
         Vector4f pos = new Vector4f(this.pos);
         pos.setW(1.0F);
         trsr.transformPosition(pos);
         pos.func_229374_e_();
         Vector3f rPos = new Vector3f(pos.getX(), pos.getY(), pos.getZ());
         Vector3f rNormal = null;
         if (this.normal != null) {
            rNormal = this.normal.func_229195_e_();
            trsr.transformNormal(rNormal);
         }

         return new B3DModel.Vertex(rPos, rNormal, this.color, this.texCoords);
      }

      public Vector3f getPos() {
         return this.pos;
      }

      @Nullable
      public Vector3f getNormal() {
         return this.normal;
      }

      @Nullable
      public Vector4f getColor() {
         return this.color;
      }

      public Vector4f[] getTexCoords() {
         return this.texCoords;
      }

      public String toString() {
         return String.format("Vertex [pos=%s, normal=%s, color=%s, texCoords=%s]", this.pos, this.normal, this.color, Arrays.toString(this.texCoords));
      }
   }

   public static class Brush {
      private final String name;
      private final Vector4f color;
      private final float shininess;
      private final int blend;
      private final int fx;
      private final List<B3DModel.Texture> textures;

      public Brush(String name, Vector4f color, float shininess, int blend, int fx, List<B3DModel.Texture> textures) {
         this.name = name;
         this.color = color;
         this.shininess = shininess;
         this.blend = blend;
         this.fx = fx;
         this.textures = textures;
      }

      public String getName() {
         return this.name;
      }

      public Vector4f getColor() {
         return this.color;
      }

      public float getShininess() {
         return this.shininess;
      }

      public int getBlend() {
         return this.blend;
      }

      public int getFx() {
         return this.fx;
      }

      public List<B3DModel.Texture> getTextures() {
         return this.textures;
      }

      public String toString() {
         return String.format("Brush [name=%s, color=%s, shininess=%s, blend=%s, fx=%s, textures=%s]", this.name, this.color, this.shininess, this.blend, this.fx, this.textures);
      }
   }

   public static class Texture {
      public static final B3DModel.Texture White = new B3DModel.Texture("builtin/white", 0, 0, new Vec2f(0.0F, 0.0F), new Vec2f(1.0F, 1.0F), 0.0F);
      private final String path;
      private final int flags;
      private final int blend;
      private final Vec2f pos;
      private final Vec2f scale;
      private final float rot;

      public Texture(String path, int flags, int blend, Vec2f pos, Vec2f scale, float rot) {
         this.path = path;
         this.flags = flags;
         this.blend = blend;
         this.pos = pos;
         this.scale = scale;
         this.rot = rot;
      }

      public String getPath() {
         return this.path;
      }

      public int getFlags() {
         return this.flags;
      }

      public int getBlend() {
         return this.blend;
      }

      public Vec2f getPos() {
         return this.pos;
      }

      public Vec2f getScale() {
         return this.scale;
      }

      public float getRot() {
         return this.rot;
      }

      public String toString() {
         return String.format("Texture [path=%s, flags=%s, blend=%s, pos=%s, scale=%s, rot=%s]", this.path, this.flags, this.blend, this.pos, this.scale, this.rot);
      }
   }

   public static class Parser {
      private static final int version = 1;
      private final ByteBuffer buf;
      private byte[] tag = new byte[4];
      private int length;
      private String dump = "";
      private B3DModel res;
      private final List<B3DModel.Texture> textures = new ArrayList();
      private final List<B3DModel.Brush> brushes = new ArrayList();
      private final List<B3DModel.Vertex> vertices = new ArrayList();
      private final com.google.common.collect.ImmutableMap.Builder<String, B3DModel.Node<B3DModel.Mesh>> meshes = ImmutableMap.builder();
      private Deque<Integer> limitStack = new ArrayDeque();
      private final Deque<Table<Integer, Optional<B3DModel.Node<?>>, B3DModel.Key>> animations = new ArrayDeque();

      public Parser(InputStream in) throws IOException {
         if (in instanceof FileInputStream) {
            FileChannel channel = ((FileInputStream)in).getChannel();
            this.buf = channel.map(MapMode.READ_ONLY, 0L, channel.size()).order(ByteOrder.LITTLE_ENDIAN);
         } else {
            IOUtils.readFully(in, this.tag);
            byte[] tmp = new byte[4];
            IOUtils.readFully(in, tmp);
            int l = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).getInt();
            if (l < 0 || l + 8 < 0) {
               throw new IOException("File is too large");
            }

            this.buf = ByteBuffer.allocate(l + 8).order(ByteOrder.LITTLE_ENDIAN);
            this.buf.clear();
            this.buf.put(this.tag);
            this.buf.put(tmp);
            this.buf.put(IOUtils.toByteArray(in, l));
            this.buf.flip();
         }

      }

      private void dump(String str) {
         if (B3DModel.printLoadedModels) {
            this.dump = this.dump + str + "\n";
         }

      }

      public B3DModel parse() throws IOException {
         if (this.res != null) {
            return this.res;
         } else {
            this.dump = "\n";
            this.readHeader();
            this.res = this.bb3d();
            if (B3DModel.printLoadedModels) {
               B3DModel.logger.info(this.dump);
            }

            return this.res;
         }
      }

      private B3DModel.Texture getTexture(int texture) {
         if (texture > this.textures.size()) {
            B3DModel.logger.error("texture {} is out of range", texture);
            return null;
         } else {
            return texture == -1 ? B3DModel.Texture.White : (B3DModel.Texture)this.textures.get(texture);
         }
      }

      @Nullable
      private B3DModel.Brush getBrush(int brush) throws IOException {
         if (brush > this.brushes.size()) {
            throw new IOException(String.format("brush %s is out of range", brush));
         } else {
            return brush == -1 ? null : (B3DModel.Brush)this.brushes.get(brush);
         }
      }

      private B3DModel.Vertex getVertex(int vertex) throws IOException {
         if (vertex > this.vertices.size()) {
            throw new IOException(String.format("vertex %s is out of range", vertex));
         } else {
            return (B3DModel.Vertex)this.vertices.get(vertex);
         }
      }

      private void readHeader() throws IOException {
         this.buf.get(this.tag);
         this.length = this.buf.getInt();
      }

      private boolean isChunk(String tag) throws IOException {
         return Arrays.equals(this.tag, tag.getBytes("US-ASCII"));
      }

      private void chunk(String tag) throws IOException {
         if (!this.isChunk(tag)) {
            throw new IOException("Expected chunk " + tag + ", got " + new String(this.tag, "US-ASCII"));
         } else {
            this.pushLimit();
         }
      }

      private String readString() throws IOException {
         int start = this.buf.position();

         while(this.buf.get() != 0) {
         }

         int end = this.buf.position();
         byte[] tmp = new byte[end - start - 1];
         this.buf.position(start);
         this.buf.get(tmp);
         this.buf.get();
         return new String(tmp, "UTF8");
      }

      private void pushLimit() {
         this.limitStack.push(this.buf.limit());
         this.buf.limit(this.buf.position() + this.length);
      }

      private void popLimit() {
         this.buf.limit((Integer)this.limitStack.pop());
      }

      private B3DModel bb3d() throws IOException {
         this.chunk("BB3D");
         int version = this.buf.getInt();
         if (version / 100 > 0) {
            throw new IOException("Unsupported major model version: " + (float)version / 100.0F);
         } else {
            if (version % 100 > 1) {
               B3DModel.logger.warn(String.format("Minor version difference in model: %s", (float)version / 100.0F));
            }

            List<B3DModel.Texture> textures = Collections.emptyList();
            List<B3DModel.Brush> brushes = Collections.emptyList();
            B3DModel.Node<?> root = null;
            this.dump("BB3D(version = " + version + ") {");

            while(this.buf.hasRemaining()) {
               this.readHeader();
               if (this.isChunk("TEXS")) {
                  textures = this.texs();
               } else if (this.isChunk("BRUS")) {
                  brushes = this.brus();
               } else if (this.isChunk("NODE")) {
                  root = this.node();
               } else {
                  this.skip();
               }
            }

            this.dump("}");
            this.popLimit();
            if (root == null) {
               throw new IOException("not found the root node in the model");
            } else {
               return new B3DModel(textures, brushes, root, this.meshes.build());
            }
         }
      }

      private List<B3DModel.Texture> texs() throws IOException {
         this.chunk("TEXS");
         ArrayList ret = new ArrayList();

         while(this.buf.hasRemaining()) {
            String path = this.readString();
            int flags = this.buf.getInt();
            int blend = this.buf.getInt();
            Vec2f pos = new Vec2f(this.buf.getFloat(), this.buf.getFloat());
            Vec2f scale = new Vec2f(this.buf.getFloat(), this.buf.getFloat());
            float rot = this.buf.getFloat();
            ret.add(new B3DModel.Texture(path, flags, blend, pos, scale, rot));
         }

         this.dump("TEXS([" + Joiner.on(", ").join(ret) + "])");
         this.popLimit();
         this.textures.addAll(ret);
         return ret;
      }

      private List<B3DModel.Brush> brus() throws IOException {
         this.chunk("BRUS");
         List<B3DModel.Brush> ret = new ArrayList();
         int n_texs = this.buf.getInt();

         while(this.buf.hasRemaining()) {
            String name = this.readString();
            Vector4f color = new Vector4f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
            float shininess = this.buf.getFloat();
            int blend = this.buf.getInt();
            int fx = this.buf.getInt();
            List<B3DModel.Texture> textures = new ArrayList();

            for(int i = 0; i < n_texs; ++i) {
               textures.add(this.getTexture(this.buf.getInt()));
            }

            ret.add(new B3DModel.Brush(name, color, shininess, blend, fx, textures));
         }

         this.dump("BRUS([" + Joiner.on(", ").join(ret) + "])");
         this.popLimit();
         this.brushes.addAll(ret);
         return ret;
      }

      private List<B3DModel.Vertex> vrts() throws IOException {
         this.chunk("VRTS");
         List<B3DModel.Vertex> ret = new ArrayList();
         int flags = this.buf.getInt();
         int tex_coord_sets = this.buf.getInt();
         int tex_coord_set_size = this.buf.getInt();

         while(this.buf.hasRemaining()) {
            Vector3f v = new Vector3f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
            Vector3f n = null;
            Vector4f color = null;
            if ((flags & 1) != 0) {
               n = new Vector3f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
            }

            if ((flags & 2) != 0) {
               color = new Vector4f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
            }

            Vector4f[] tex_coords = new Vector4f[tex_coord_sets];

            for(int i = 0; i < tex_coord_sets; ++i) {
               switch(tex_coord_set_size) {
               case 1:
                  tex_coords[i] = new Vector4f(this.buf.getFloat(), 0.0F, 0.0F, 1.0F);
                  break;
               case 2:
                  tex_coords[i] = new Vector4f(this.buf.getFloat(), this.buf.getFloat(), 0.0F, 1.0F);
                  break;
               case 3:
                  tex_coords[i] = new Vector4f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat(), 1.0F);
                  break;
               case 4:
                  tex_coords[i] = new Vector4f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
                  break;
               default:
                  B3DModel.logger.error(String.format("Unsupported number of texture coords: %s", tex_coord_set_size));
                  tex_coords[i] = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
               }
            }

            ret.add(new B3DModel.Vertex(v, n, color, tex_coords));
         }

         this.dump("VRTS([" + Joiner.on(", ").join(ret) + "])");
         this.popLimit();
         this.vertices.clear();
         this.vertices.addAll(ret);
         return ret;
      }

      private List<B3DModel.Face> tris() throws IOException {
         this.chunk("TRIS");
         List<B3DModel.Face> ret = new ArrayList();
         int brush_id = this.buf.getInt();

         while(this.buf.hasRemaining()) {
            ret.add(new B3DModel.Face(this.getVertex(this.buf.getInt()), this.getVertex(this.buf.getInt()), this.getVertex(this.buf.getInt()), this.getBrush(brush_id)));
         }

         this.dump("TRIS([" + Joiner.on(", ").join(ret) + "])");
         this.popLimit();
         return ret;
      }

      private Pair<B3DModel.Brush, List<B3DModel.Face>> mesh() throws IOException {
         this.chunk("MESH");
         int brush_id = this.buf.getInt();
         this.readHeader();
         this.dump("MESH(brush = " + brush_id + ") {");
         this.vrts();
         ArrayList ret = new ArrayList();

         while(this.buf.hasRemaining()) {
            this.readHeader();
            ret.addAll(this.tris());
         }

         this.dump("}");
         this.popLimit();
         return Pair.of(this.getBrush(brush_id), ret);
      }

      private List<Pair<B3DModel.Vertex, Float>> bone() throws IOException {
         this.chunk("BONE");
         ArrayList ret = new ArrayList();

         while(this.buf.hasRemaining()) {
            ret.add(Pair.of(this.getVertex(this.buf.getInt()), this.buf.getFloat()));
         }

         this.dump("BONE(...)");
         this.popLimit();
         return ret;
      }

      private Map<Integer, B3DModel.Key> keys() throws IOException {
         this.chunk("KEYS");
         Map<Integer, B3DModel.Key> ret = new HashMap();
         int flags = this.buf.getInt();
         Vector3f pos = null;
         Vector3f scale = null;
         Quaternion rot = null;

         while(this.buf.hasRemaining()) {
            int frame = this.buf.getInt();
            if ((flags & 1) != 0) {
               pos = new Vector3f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
            }

            if ((flags & 2) != 0) {
               scale = new Vector3f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
            }

            if ((flags & 4) != 0) {
               rot = this.readQuat();
            }

            B3DModel.Key key = new B3DModel.Key(pos, scale, rot);
            B3DModel.Key oldKey = (B3DModel.Key)((Table)this.animations.peek()).get(frame, (Object)null);
            if (oldKey != null) {
               if (pos != null) {
                  if (oldKey.getPos() != null) {
                     B3DModel.logger.error("Duplicate keys: {} and {} (ignored)", oldKey, key);
                  } else {
                     key = new B3DModel.Key(oldKey.getPos(), key.getScale(), key.getRot());
                  }
               }

               if (scale != null) {
                  if (oldKey.getScale() != null) {
                     B3DModel.logger.error("Duplicate keys: {} and {} (ignored)", oldKey, key);
                  } else {
                     key = new B3DModel.Key(key.getPos(), oldKey.getScale(), key.getRot());
                  }
               }

               if (rot != null) {
                  if (oldKey.getRot() != null) {
                     B3DModel.logger.error("Duplicate keys: {} and {} (ignored)", oldKey, key);
                  } else {
                     key = new B3DModel.Key(key.getPos(), key.getScale(), oldKey.getRot());
                  }
               }
            }

            ((Table)this.animations.peek()).put(frame, Optional.empty(), key);
            ret.put(frame, key);
         }

         this.dump("KEYS([(" + Joiner.on("), (").withKeyValueSeparator(" -> ").join(ret) + ")])");
         this.popLimit();
         return ret;
      }

      private Triple<Integer, Integer, Float> anim() throws IOException {
         this.chunk("ANIM");
         int flags = this.buf.getInt();
         int frames = this.buf.getInt();
         float fps = this.buf.getFloat();
         this.dump("ANIM(" + flags + ", " + frames + ", " + fps + ")");
         this.popLimit();
         return Triple.of(flags, frames, fps);
      }

      private B3DModel.Node<?> node() throws IOException {
         this.chunk("NODE");
         this.animations.push(HashBasedTable.create());
         Triple<Integer, Integer, Float> animData = null;
         Pair<B3DModel.Brush, List<B3DModel.Face>> mesh = null;
         List<Pair<B3DModel.Vertex, Float>> bone = null;
         Map<Integer, B3DModel.Key> keys = new HashMap();
         List<B3DModel.Node<?>> nodes = new ArrayList();
         String name = this.readString();
         Vector3f pos = new Vector3f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
         Vector3f scale = new Vector3f(this.buf.getFloat(), this.buf.getFloat(), this.buf.getFloat());
         Quaternion rot = this.readQuat();
         this.dump("NODE(" + name + ", " + pos + ", " + scale + ", " + rot + ") {");

         while(this.buf.hasRemaining()) {
            this.readHeader();
            if (this.isChunk("MESH")) {
               mesh = this.mesh();
            } else if (this.isChunk("BONE")) {
               bone = this.bone();
            } else if (this.isChunk("KEYS")) {
               keys.putAll(this.keys());
            } else if (this.isChunk("NODE")) {
               nodes.add(this.node());
            } else if (this.isChunk("ANIM")) {
               animData = this.anim();
            } else {
               this.skip();
            }
         }

         this.dump("}");
         this.popLimit();
         Table<Integer, Optional<B3DModel.Node<?>>, B3DModel.Key> keyData = (Table)this.animations.pop();
         B3DModel.Node node;
         if (mesh != null) {
            B3DModel.Node<B3DModel.Mesh> mNode = B3DModel.Node.create(name, pos, scale, rot, nodes, new B3DModel.Mesh(mesh));
            this.meshes.put(name, mNode);
            node = mNode;
         } else if (bone != null) {
            node = B3DModel.Node.create(name, pos, scale, rot, nodes, new B3DModel.Bone(bone));
         } else {
            node = B3DModel.Node.create(name, pos, scale, rot, nodes, new B3DModel.Pivot());
         }

         if (animData == null) {
            Iterator var14 = keyData.cellSet().iterator();

            while(var14.hasNext()) {
               Cell<Integer, Optional<B3DModel.Node<?>>, B3DModel.Key> key = (Cell)var14.next();
               ((Table)this.animations.peek()).put(key.getRowKey(), Optional.of(((Optional)key.getColumnKey()).orElse(node)), key.getValue());
            }
         } else {
            node.setAnimation(animData, keyData);
         }

         return node;
      }

      private Quaternion readQuat() {
         float w = this.buf.getFloat();
         float x = this.buf.getFloat();
         float y = this.buf.getFloat();
         float z = this.buf.getFloat();
         return new Quaternion(x, y, z, w);
      }

      private void skip() {
         this.buf.position(this.buf.position() + this.length);
      }
   }
}
