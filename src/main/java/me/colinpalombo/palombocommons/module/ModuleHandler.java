package me.colinpalombo.palombocommons.module;

import lombok.Getter;

public abstract class ModuleHandler<M extends Module<M>> {

    @Getter
    private final M handlerModule;

    public ModuleHandler(M handlerModule) {
        this.handlerModule = handlerModule;
    }
}
