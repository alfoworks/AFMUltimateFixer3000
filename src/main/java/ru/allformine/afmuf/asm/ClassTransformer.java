package ru.allformine.afmuf.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import ru.allformine.afmuf.AFMUltimateFixer;
import ru.allformine.afmuf.asm.handler.IClassHandler;
import ru.allformine.afmuf.asm.handler.TestHandler;

import java.util.ArrayList;

public final class ClassTransformer implements IClassTransformer {

    private final ArrayList<IClassHandler> handlers = new ArrayList<>();

    public ClassTransformer() {
        handlers.add(new TestHandler());
    }

    @Override
    public byte[] transform(String origName, String name, byte[] bytes) {
        for (IClassHandler handler : handlers) {
            if (handler.accept(name)) {
                try {
                    return handler.transform(bytes);
                } catch (Exception e) {
                    AFMUltimateFixer.logger.error(String.format("An error occurred while patching class %s with patch %s", name, handler.getClass().getName()));

                    e.printStackTrace();
                }
            }
        }

        return bytes;
    }

}
