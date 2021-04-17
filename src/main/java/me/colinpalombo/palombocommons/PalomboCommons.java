package me.colinpalombo.palombocommons;

import com.sun.org.apache.regexp.internal.RE;
import lombok.Getter;
import me.colinpalombo.palombocommons.module.ModuleManager;
import org.reflections.Reflections;
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

    private static Reflections REFLECTIONS;
    private static ModuleManager MODULE_MANAGER;

    public static void init(String packageName) {
        // Reflections
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{}, ClasspathHelper.staticClassLoader());
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
            .addUrls(ClasspathHelper.forPackage(PACKAGE_NAME, classLoader))
            .addClassLoader(classLoader)
            .filterInputsBy(new FilterBuilder().includePackage(PACKAGE_NAME))
            .setScanners(
                    new SubTypesScanner(false),
                    new TypeAnnotationsScanner()
        );

        if (packageName != null && !packageName.isEmpty()) {
            configurationBuilder.addUrls(ClasspathHelper.forPackage(packageName, classLoader));
        }

        REFLECTIONS = new Reflections(configurationBuilder);
        MODULE_MANAGER = new ModuleManager(REFLECTIONS);
    }

    public static void init() {
        init("");
    }

    public static Reflections getReflections() {
        if (REFLECTIONS == null) {
            init();
        }

        return REFLECTIONS;
    }

    public static ModuleManager getModuleManager() {
        if (REFLECTIONS == null) {
            init();
        }

        return MODULE_MANAGER;
    }

    public static void log(String message, Object ... args) {
        Logger.getLogger("palombo-commons").log(Level.INFO, String.format(message, args));
    }
}
