package me.colinpalombo.palombocommons;

import com.sun.org.apache.regexp.internal.RE;
import lombok.Getter;
import me.colinpalombo.palombocommons.module.ModuleManager;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PalomboCommons {

    private static final String PACKAGE_NAME = "me.colinpalombo.palombocommons";

    private static Reflections reflections;
    private static ModuleManager module_manager;

    private static boolean initialized = false;

    public static void init(String packageName) {
        // Reflections
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{}, ClasspathHelper.staticClassLoader());
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
            .addUrls(ClasspathHelper.forPackage(PACKAGE_NAME, classLoader))
            .addClassLoader(classLoader)
            .filterInputsBy(new FilterBuilder().includePackage(PACKAGE_NAME))
            .setScanners(
                    new SubTypesScanner(false),
                    new TypeAnnotationsScanner(),
                    new FieldAnnotationsScanner(),
                    new MethodAnnotationsScanner()
        );

        if (packageName != null && !packageName.isEmpty()) {
            classLoader = URLClassLoader.newInstance(new URL[]{}, ClasspathHelper.staticClassLoader());

            configurationBuilder
                    .addUrls(ClasspathHelper.forPackage(packageName, classLoader))
                    .filterInputsBy(new FilterBuilder().includePackage(packageName));
        }

        reflections = new Reflections(configurationBuilder);
        module_manager = new ModuleManager(reflections);
        initialized = true;
    }

    public static void init() {
        init("");
    }

    public static Reflections getReflections() {
        if (reflections == null) {
            notInitialized();
        }

        return reflections;
    }

    public static ModuleManager getModuleManager() {
        if (module_manager == null) {
            notInitialized();
        }

        return module_manager;
    }

    private static void notInitialized() {
        throw new RuntimeException("PalomboCommons has not been initialized correctly. Initialize with PalomboCommons#init");
    }

    public static void log(String message, Object ... args) {
        Logger.getLogger("palombo-commons").log(Level.INFO, String.format(message, args));
    }
}
