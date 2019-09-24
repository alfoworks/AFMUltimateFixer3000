package ru.allformine.afmuf.asm.handler;

public interface IClassHandler {
    public boolean accept(String name);

    public byte[] transform(byte[] basicClass);
}
