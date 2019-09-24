package ru.allformine.afmuf.asm.handler;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class TestHandler implements IClassHandler {
    private static final String METHOD_DESC = "(Lcom/mrcrayfish/furniture/network/message/MessageDoorMat;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;";

    public static void testPrint() {
        System.out.println("ASM Test!");
    }

    @Override
    public boolean accept(String name) {
        return name.equals("com.mrcrayfish.furniture.network.message.MessageDoorMat");
    }

    @Override
    public byte[] transform(byte[] basicClass) {
        ClassNode cNode = new ClassNode();
        new ClassReader(basicClass).accept(cNode, 0);

        /*
        method.instructions.insertBefore(method.instructions.get(6),
                new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "ru/allformine/afmuf/asm/handler/TestHandler",
                        "testPrint",
                        "",
                        false));
        */

        ClassWriter cWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cNode.accept(cWriter);

        return cWriter.toByteArray();
    }
}
