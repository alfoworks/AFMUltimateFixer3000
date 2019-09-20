package ru.allformine.afmuf.asm.handler;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ru.allformine.afmuf.asm.ASMHelper;

public class TestHandler implements IClassHandler {
    private static final String METHOD_DESC = "(Lcom/mrcrayfish/furniture/network/message/MessageDoorMat;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)V";

    public static void testPrint() {
        System.out.println("ASM Test!");
    }

    @Override
    public boolean accept(String name) {
        return false;
    }

    @Override
    public boolean transform(ClassNode node) {
        MethodNode method = ASMHelper.findMethod(node, "onMessage", METHOD_DESC);
        method.instructions.insertBefore(method.instructions.get(0),
                new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "ru/allformine/afmuf/asm/handler/TestHandler",
                        "testPrint",
                        "",
                        false));

        System.out.println("Test patch applied?");

        return true;
    }
}
