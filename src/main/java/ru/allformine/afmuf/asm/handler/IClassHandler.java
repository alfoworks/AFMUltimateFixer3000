package ru.allformine.afmuf.asm.handler;

import org.objectweb.asm.tree.ClassNode;

public interface IClassHandler {
    public boolean accept(String name);

    public boolean transform(ClassNode node);
}
