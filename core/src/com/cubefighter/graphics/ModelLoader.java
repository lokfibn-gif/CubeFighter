package com.cubefighter.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ModelLoader {
    
    private static ArrayMap<String, Model> modelCache = new ArrayMap<>();
    
    public static Model loadObj(String filename) {
        if (modelCache.containsKey(filename)) {
            return modelCache.get(filename);
        }
        
        Model model = new Model();
        
        Array<Vector3> vertices = new Array<>();
        Array<Vector3> normals = new Array<>();
        Array<float[]> textureCoords = new Array<>();
        Array<int[]> faces = new Array<>();
        
        try {
            BufferedReader reader = new BufferedReader(Gdx.files.internal(filename).reader());
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                StringTokenizer tokenizer = new StringTokenizer(line);
                String type = tokenizer.nextToken();
                
                if (type.equals("v")) {
                    vertices.add(new Vector3(
                        Float.parseFloat(tokenizer.nextToken()),
                        Float.parseFloat(tokenizer.nextToken()),
                        Float.parseFloat(tokenizer.nextToken())
                    ));
                } else if (type.equals("vn")) {
                    normals.add(new Vector3(
                        Float.parseFloat(tokenizer.nextToken()),
                        Float.parseFloat(tokenizer.nextToken()),
                        Float.parseFloat(tokenizer.nextToken())
                    ));
                } else if (type.equals("vt")) {
                    textureCoords.add(new float[]{
                        Float.parseFloat(tokenizer.nextToken()),
                        tokenizer.hasMoreTokens() ? Float.parseFloat(tokenizer.nextToken()) : 0f
                    });
                } else if (type.equals("f")) {
                    int[] face = new int[tokenizer.countTokens() * 3];
                    int i = 0;
                    while (tokenizer.hasMoreTokens()) {
                        String[] parts = tokenizer.nextToken().split("/");
                        face[i++] = Integer.parseInt(parts[0]) - 1;
                        face[i++] = parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) - 1 : -1;
                        face[i++] = parts.length > 2 ? Integer.parseInt(parts[2]) - 1 : -1;
                    }
                    faces.add(face);
                }
            }
            reader.close();
            
            model = createModelFromData(vertices, normals, textureCoords, faces);
            
        } catch (IOException e) {
            Gdx.app.error("ModelLoader", "Failed to load model: " + filename, e);
            model = createDefaultCube();
        }
        
        modelCache.put(filename, model);
        return model;
    }
    
    private static Model createModelFromData(Array<Vector3> vertices, Array<Vector3> normals, 
                                             Array<float[]> textureCoords, Array<int[]> faces) {
        Model model = new Model();
        
        Array<Float> vertexData = new Array<>();
        Array<Short> indexData = new Array<>();
        short index = 0;
        
        for (int[] face : faces) {
            int vertexCount = face.length / 3;
            for (int i = 0; i < vertexCount && i < 3; i++) {
                int vi = face[i * 3];
                int ti = face[i * 3 + 1];
                int ni = face[i * 3 + 2];
                
                Vector3 v = vertices.get(vi);
                vertexData.add(v.x);
                vertexData.add(v.y);
                vertexData.add(v.z);
                
                if (ni >= 0 && ni < normals.size) {
                    Vector3 n = normals.get(ni);
                    vertexData.add(n.x);
                    vertexData.add(n.y);
                    vertexData.add(n.z);
                } else {
                    vertexData.add(0f);
                    vertexData.add(0f);
                    vertexData.add(1f);
                }
                
                if (ti >= 0 && ti < textureCoords.size) {
                    vertexData.add(textureCoords.get(ti)[0]);
                    vertexData.add(textureCoords.get(ti)[1]);
                } else {
                    vertexData.add(0f);
                    vertexData.add(0f);
                }
                
                indexData.add(index++);
            }
        }
        
        float[] verticesArray = new float[vertexData.size];
        for (int i = 0; i < vertexData.size; i++) {
            verticesArray[i] = vertexData.get(i);
        }
        
        short[] indicesArray = new short[indexData.size];
        for (int i = 0; i < indexData.size; i++) {
            indicesArray[i] = indexData.get(i);
        }
        
        Mesh mesh = new Mesh(true, verticesArray.length / 8, indicesArray.length,
            VertexAttribute.Position(),
            VertexAttribute.Normal(),
            VertexAttribute.TexCoords(0));
        mesh.setVertices(verticesArray);
        mesh.setIndices(indicesArray);
        
        Node node = new Node();
        NodePart nodePart = new NodePart();
        nodePart.meshPart = new MeshPart();
        nodePart.meshPart.mesh = mesh;
        nodePart.meshPart.primitiveType = GL20.GL_TRIANGLES;
        nodePart.meshPart.offset = 0;
        nodePart.meshPart.size = indicesArray.length;
        nodePart.material = new Material();
        node.parts.add(nodePart);
        model.nodes.add(node);
        
        return model;
    }
    
    private static Model createDefaultCube() {
        Model model = new Model();
        
        float[] vertices = {
            -0.5f, -0.5f, -0.5f, 0f, 0f, -1f, 0f, 0f,
             0.5f, -0.5f, -0.5f, 0f, 0f, -1f, 1f, 0f,
             0.5f,  0.5f, -0.5f, 0f, 0f, -1f, 1f, 1f,
            -0.5f,  0.5f, -0.5f, 0f, 0f, -1f, 0f, 1f,
            -0.5f, -0.5f,  0.5f, 0f, 0f,  1f, 0f, 0f,
             0.5f, -0.5f,  0.5f, 0f, 0f,  1f, 1f, 0f,
             0.5f,  0.5f,  0.5f, 0f, 0f,  1f, 1f, 1f,
            -0.5f,  0.5f,  0.5f, 0f, 0f,  1f, 0f, 1f
        };
        
        short[] indices = {
            0, 1, 2, 0, 2, 3,
            4, 6, 5, 4, 7, 6,
            0, 4, 5, 0, 5, 1,
            2, 6, 7, 2, 7, 3,
            0, 3, 7, 0, 7, 4,
            1, 5, 6, 1, 6, 2
        };
        
        Mesh mesh = new Mesh(true, vertices.length / 8, indices.length,
            VertexAttribute.Position(),
            VertexAttribute.Normal(),
            VertexAttribute.TexCoords(0));
        mesh.setVertices(vertices);
        mesh.setIndices(indices);
        
        Node node = new Node();
        NodePart nodePart = new NodePart();
        nodePart.meshPart = new MeshPart();
        nodePart.meshPart.mesh = mesh;
        nodePart.meshPart.primitiveType = GL20.GL_TRIANGLES;
        nodePart.meshPart.offset = 0;
        nodePart.meshPart.size = indices.length;
        nodePart.material = new Material();
        node.parts.add(nodePart);
        model.nodes.add(node);
        
        return model;
    }
    
    public static void dispose() {
        for (Model model : modelCache.values()) {
            model.dispose();
        }
        modelCache.clear();
    }
}