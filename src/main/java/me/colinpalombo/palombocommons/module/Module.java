package me.colinpalombo.palombocommons.module;

import lombok.Getter;
import me.colinpalombo.palombocommons.module.listener.ModuleListener;
import me.colinpalombo.palombocommons.module.listener.event.ModuleEvent;
import me.colinpalombo.palombocommons.module.register.RegisterModuleEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Module<M extends Module<M>> {

    @Getter
    private final ModuleManager moduleManager;

    private final List<ModuleListener> listeners = new ArrayList<>();

    private final Map<String, ArrayList<Pair<ModuleListener, Method>>> events = new HashMap<>();

    public Module(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    protected abstract void onRegistered();

    protected abstract void onUnregister();

    protected abstract void onEnable();

    protected abstract void onDisable();

    protected abstract ModuleListener[] getListeners();

//    protected abstract Class<? extends Module<?>>[] getDependencyClasses();

    public void create() {
        for (ModuleListener listener : getListeners()) {
            for (Method method : listener.getClass().getMethods()) {
                if (method.isAnnotationPresent(RegisterModuleEvent.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();

                    if (parameterTypes.length > 0) {
                        Class<?> eventClass = parameterTypes[0];
                        String eventClassName = eventClass.getName();
                        ArrayList<Pair<ModuleListener, Method>> methods = this.events.getOrDefault(eventClassName, new ArrayList<>());

                        methods.add(Pair.of(listener, method));
                        this.events.put(eventClassName, methods);
//                            ArrayList<Method> methods = this.events.getOrDefault(eventClassName, new ArrayList<>());
//
//                            methods.add(method);
//                            this.events.put(eventClassName, methods);
                    }
                }
            }

            this.registerListener(listener);
        }

        this.onRegistered();
    }

    public void destroy() {
        for (ModuleListener listener : this.listeners) {
            this.unregisterListener(listener);
        }

        this.onUnregister();
    }

//    public Optional<Module<M>> getModule(String name) {
//        return (Optional<Module<M>>) moduleManager.getCore().getModuleManager().getModule(name);
//    }
//
//    public Optional<Module<M>> getModule(Class<? extends Module<M>> moduleClass) {
//        return (Optional<Module<M>>) moduleManager.getCore().getModuleManager().getModule(moduleClass);
//    }

    public void callEvent(ModuleEvent<M> event) {
        String eventClassName = event.getClass().getName();

        if (this.events.containsKey(eventClassName)) {
            ArrayList<Pair<ModuleListener, Method>> pairs = this.events.get(eventClassName);

            for (Pair<ModuleListener, Method> pair : pairs) {
                ModuleListener listener = pair.getLeft();
                Method method = pair.getRight();

                try {
                    method.invoke(listener, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void registerListener(ModuleListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(ModuleListener listener) {
        this.listeners.remove(listener);
    }
}
