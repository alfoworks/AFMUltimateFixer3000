package ru.allformine.afmuf.hooks;

import gloomyfolken.hooklib.minecraft.HookLoader;
import gloomyfolken.hooklib.minecraft.PrimaryClassTransformer;

public class AFMHookLoader extends HookLoader {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{PrimaryClassTransformer.class.getName()};
    }

    @Override
    public void registerHooks() {
        //регистрируем класс, где есть методы с аннотацией @Hook
        registerHookContainer(AFMHookContainer.class.getName());
    }
}
