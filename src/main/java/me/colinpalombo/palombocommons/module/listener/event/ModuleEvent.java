package me.colinpalombo.palombocommons.module.listener.event;

import lombok.Getter;
import me.colinpalombo.palombocommons.module.Module;

public class ModuleEvent<M extends Module<M>> {

    @Getter
    private final M module;

    public ModuleEvent(M module) {
        this.module = module;
    }
}
