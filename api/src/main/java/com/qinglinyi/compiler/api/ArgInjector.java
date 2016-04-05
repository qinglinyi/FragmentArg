package com.qinglinyi.compiler.api;

/**
 * @author qinglinyi
 * @since 1.0.0
 */
public class ArgInjector {

    public static final String INJECTOR_CLASS_NAME = "ArgInjectorImpl";
    public static final String INJECTOR_PACKAGE = "com.qinglinyi.arg";
    public static final String INJECTOR_QUALIFIED_CLASS = INJECTOR_PACKAGE + "." + INJECTOR_CLASS_NAME;

    private static Injector ArgInjectorImpl;

    public static void inject(Object fragment) {
        if (ArgInjectorImpl == null) {
            try {
                Class<?> c = Class.forName(INJECTOR_QUALIFIED_CLASS);
                ArgInjectorImpl = (Injector) c.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ArgInjectorImpl != null) {
            ArgInjectorImpl.inject(fragment);
        }
    }
}
