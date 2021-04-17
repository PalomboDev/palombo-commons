package me.colinpalombo.palombocommons.module;

import lombok.Getter;
import me.colinpalombo.palombocommons.PalomboCommons;
import me.colinpalombo.palombocommons.module.register.RegisterModule;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModuleManager {

    @Getter
    private final Map<String, Module<?>> modules = new HashMap<>();

    public ModuleManager(Reflections reflections) {
        for (Class<?> annotatedType : reflections.getTypesAnnotatedWith(RegisterModule.class)) {
            Class<? extends Module<?>> moduleClass = (Class<? extends Module<?>>) annotatedType;

            registerModule(moduleClass);
        }

        for (Module<?> module : modules.values()) {
            module.create();
        }

        this.enableModules();
    }

    public void enableModules() {
        for (Module<?> module : this.modules.values()) {
            module.onEnable();
        }
    }

    public void disableModules() {
        for (Module<?> module : this.modules.values()) {
            module.onDisable();
        }
    }

    public <M extends Module<M>> Optional<? extends Module<?>> getModule(String name) {
        return Optional.ofNullable((Module<M>) modules.get(name));
    }

    public <M extends Module<M>> Optional<? extends Module<?>> getModule(Class<? extends Module<?>> moduleClass) {
        return Optional.ofNullable((Module<M>) modules.get(moduleName(moduleClass)));
    }

    public <M extends Module<M>> void registerModule(Class<? extends Module<?>> moduleClass) {
        try {
            if (isModuleRegistered(moduleClass)) {
//                Core.log("Attempting to register already registered module. Name: %s - Class: %s", module.getName(), moduleClass.getName()());
            } else {
                PalomboCommons.log("Attempting to register module. Name: %s", moduleClass.getName());

                Module<M> module = (Module<M>) moduleClass.getDeclaredConstructor(ModuleManager.class).newInstance(this);
                String moduleName = moduleName(module);

                modules.put(moduleName, module);

                PalomboCommons.log("Module registered. Name: %s", moduleName);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            PalomboCommons.log("Module failed to register. Name: %s - Error: ", moduleName(moduleClass));
            e.printStackTrace();
        }
    }

    public <M extends Module<M>> void unregisterModule(String name) {
        Optional<Module<M>> optionalModule = (Optional<Module<M>>) getModule(name);

        if (isModuleRegistered(name) && optionalModule.isPresent()) {
            Module<M> module = optionalModule.get();

            module.destroy();
            // TODO: Check if this module is dependent on by other modules
        } else {
            PalomboCommons.log("Attempting to unregister a module that isn't registered. Name: %s", name);
        }
    }

    public <M extends Module<M>> void unregisterModule(Module<?> module) {
        unregisterModule(moduleName(module));
    }

    public <M extends Module<M>> void unregisterModule(Class<? extends Module<?>> moduleClass) {
        unregisterModule(moduleName(moduleClass));
    }

    public <M extends Module<M>> boolean isModuleRegistered(String name) {
        return modules.containsKey(name);
    }

    public <M extends Module<M>> boolean isModuleRegistered(Module<?> module) {
        return isModuleRegistered(moduleName(module));
    }

    public <M extends Module<M>> boolean isModuleRegistered(Class<? extends Module<?>> moduleClass) {
        return isModuleRegistered(moduleName(moduleClass));
    }

    public <M extends Module<M>> String moduleName(Module<?> module) {
        return moduleName((Class<? extends Module<?>>) module.getClass());
    }

    public <M extends Module<M>> String moduleName(Class<? extends Module<?>> moduleClass) {
        RegisterModule registerModule = moduleClass.getAnnotation(RegisterModule.class);

        return registerModule.name();
    }
}
