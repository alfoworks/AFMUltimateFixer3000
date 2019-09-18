package ru.allformine.afmuf.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ASMHelper {

    private ASMHelper() {
    }

    public static ClassNode readClass(byte[] bytes) {
        if (bytes == null) throw new IllegalArgumentException("bytes is null");
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);
        return node;
    }

    public static byte[] writeClass(ClassNode node) {
        return writeClass(node, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }

    public static byte[] writeClass(ClassNode node, int options) {
        if (node == null) throw new IllegalArgumentException("node is null");
        ClassWriter writer = new ClassWriter(options);
        node.accept(writer);
        return writer.toByteArray();
    }

    public static MethodNode findMethod(ClassNode node, String name, String desc) {
        if (node == null) throw new IllegalArgumentException("node is null");
        if (name == null) throw new IllegalArgumentException("name is null");

        for (MethodNode method : (Iterable<MethodNode>) node.methods) {
            if (method.name.equals(name) && (desc == null || method.desc.equals(desc))) {
                return method;
            }
        }

        throw new NoSuchMethodError(node.name + "." + name + desc);
    }

    public static void saveDump(String name, byte[] bytes) {
        if (name == null) throw new IllegalArgumentException("name is null");
        if (bytes == null) throw new IllegalArgumentException("bytes is null");
        try {
            Path path = Paths.get("classdump", name.replace("/", ".") + ".class");
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
