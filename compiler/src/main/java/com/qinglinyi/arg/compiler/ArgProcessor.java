package com.qinglinyi.arg.compiler;

import com.google.auto.service.AutoService;
import com.qinglinyi.arg.api.Arg;
import com.qinglinyi.arg.api.UseArg;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.squareup.javapoet.JavaFile.builder;

/**
 * This is the annotation processor for Arg
 *
 * @author qinglinyi
 * @since 1.0.0
 */
@AutoService(Processor.class)
public class ArgProcessor extends AbstractProcessor {

    private static final boolean DEBUG = true;
    private static final String ANNOTATION = "@" + UseArg.class.getSimpleName();
    private static Element TYPE_FRAGMENT, TYPE_SUPPORT_FRAGMENT;

    private static final String OPTION_IS_LIBRARY = "argIsLibrary";
    private static final String OPTION_PACKAGE_NAME = "argPackageName";
    private static final String OPTION_BUILDER_NAME = "builderName";

    private boolean isLibrary;
    private String packageName;// Builder、Injector 包名
    private String builderName;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Elements elementUtils = processingEnv.getElementUtils();
        TYPE_FRAGMENT = elementUtils.getTypeElement("android.app.Fragment");
        TYPE_SUPPORT_FRAGMENT = elementUtils.getTypeElement("android.support.v4.app.Fragment");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Arg.class.getCanonicalName());
        supportTypes.add(UseArg.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(OPTION_IS_LIBRARY);
        supportTypes.add(OPTION_PACKAGE_NAME);
        supportTypes.add(OPTION_BUILDER_NAME);
        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // 判断是否是Library

        String optionIsLibrary = processingEnv.getOptions().get(OPTION_IS_LIBRARY);
        if (optionIsLibrary != null && optionIsLibrary.equalsIgnoreCase("true")) {
            this.isLibrary = true;
        }

        String optionPackageName = processingEnv.getOptions().get(OPTION_PACKAGE_NAME);
        if (optionPackageName != null) {
            this.packageName = optionPackageName;
        }

        String builderName = processingEnv.getOptions().get(OPTION_BUILDER_NAME);
        if (builderName != null) {
            this.builderName = builderName;
        }

        List<AnnotatedClass> fragments = collectFragments(roundEnv.getElementsAnnotatedWith(UseArg.class));

        try {
            generate(fragments);
        } catch (IOException e) {
            e.printStackTrace();
            error("Couldn't generate class");
        }
        return true;
    }


    /**
     * 获取使用{@link UseArg}的Fragment类，组装成{@link AnnotatedClass}
     *
     * @param elements {@link UseArg} Elements
     * @return {@link AnnotatedClass} 列表
     */
    private List<AnnotatedClass> collectFragments(Set<? extends Element> elements) {

        debug(elements.toString() + "========" + isLibrary);

        List<AnnotatedClass> annotatedClasses = new ArrayList<>();// fragment类列表

        Set<TypeElementWrap> wrapSet = new HashSet<>();

        for (Element annotatedElement : elements) {
            TypeElement classElement = (TypeElement) annotatedElement;

            // 处理父类
            TypeMirror supperClass = classElement.getSuperclass();

            while (supperClass.getKind() != TypeKind.NONE) {

                TypeElement supperElement = (TypeElement) processingEnv.getTypeUtils().asElement(supperClass);
                if (supperElement.getAnnotation(UseArg.class) == null) {// 父类不是UseArg退出循环
                    break;
                }

                if (isValidClass(supperElement)) {
                    wrapSet.add(new TypeElementWrap(supperElement, true));
                }

                supperClass = supperElement.getSuperclass();
            }

            if (isValidClass(classElement)) {
                TypeElementWrap typeElementWrap = new TypeElementWrap(classElement, false);
                boolean result = wrapSet.add(typeElementWrap);
                if (!result) {
                    wrapSet.remove(typeElementWrap);// 有可能是父类，移除，使用子类
                    wrapSet.add(typeElementWrap);
                }
            }
        }

        for (TypeElementWrap typeElementWrap : wrapSet) {
            try {
                annotatedClasses.add(buildAnnotatedClass(typeElementWrap.element,
                        typeElementWrap.isSupper));

            } catch (ArgException e) {
                e.printStackTrace();
                error("Build Annotated Class Fail:%s", typeElementWrap.element,
                        typeElementWrap.element.asType().toString());
            }
        }


        return annotatedClasses;
    }


    /**
     * 组装{@link AnnotatedClass}
     *
     * @param classElement Fragment
     * @return {@link AnnotatedClass}
     * @throws ArgException
     */
    private AnnotatedClass buildAnnotatedClass(TypeElement classElement, boolean isSupperClass) throws
            ArgException {
        final Elements elementUtils = processingEnv.getElementUtils();

        ArrayList<Element> elements = new ArrayList<>(classElement.getEnclosedElements());// 当前类包含的Element
        String packageName = Util.getPackageName(elementUtils, classElement);

        TypeMirror supperClass = classElement.getSuperclass();
        while (supperClass.getKind() != TypeKind.NONE) {
            TypeElement supperElement = (TypeElement) processingEnv.getTypeUtils().asElement(supperClass);
            if (supperElement.getAnnotation(UseArg.class) == null) {// 父类不是UseArg退出循环
                break;
            }
            String supportPackageName = Util.getPackageName(elementUtils, supperElement);
            List<? extends Element> supportElements = supperElement.getEnclosedElements();
            if (!supportPackageName.equals(packageName)) {// TODO 父类在相同包下，Default修饰的字段才能访问。但是public就可以访问
                // 不同包 拿到public的字段
                for (Element element : supportElements) {
                    if (element != null
                            && element.getKind() == ElementKind.FIELD
                            && ElementValidator.isPublic(element)) {
                        elements.add(element);
                    }
                }
            } else {
                elements.addAll(supportElements);// 加上父类的Element
            }
            supperClass = supperElement.getSuperclass();
        }

        if (elements.size() == 0) {
            return null;
        }

        Set<AnnotatedFeild> annotatedFeildSet = buildAnnotatedFeilds(elements);

        return new AnnotatedClass(classElement, packageName, isSupperClass, annotatedFeildSet);
    }

    /**
     * 获取每个UseArg的Fragment的Arg
     *
     * @param elements @{link UseArg}
     * @return @{AnnotatedFeild} set
     */
    private Set<AnnotatedFeild> buildAnnotatedFeilds(List<? extends Element> elements) {
        Set<AnnotatedFeild> annotatedFeildSet = new TreeSet<>();
        Set<String> names = new TreeSet<>();// 要有序
        for (Element feildElement : elements) {

            if (feildElement == null || feildElement.getKind() != ElementKind.FIELD) {
                continue;
            }

            // 相同字段名，添加前者。父子类相同的字段名，保持子类的。子类在前添加
            if (!names.add(feildElement.getSimpleName().toString())) {
                continue;
            }

            Arg annotation = feildElement.getAnnotation(Arg.class);
            if (annotation == null) {
                continue;
            }

            if (ElementValidator.isPrivate(feildElement)) {
                error("Feild \"%s\" modifier should be public or default", feildElement,
                        feildElement.getSimpleName());
                continue;
            }

            if (ElementValidator.isFinal(feildElement)) {
                error("Feild \"%s\"  should not be final", feildElement,
                        feildElement.getSimpleName());
                continue;
            }

            String operation = OperationUtil.getOperation(processingEnv, feildElement);
            if (operation != null) {
                annotatedFeildSet.add(new AnnotatedFeild(feildElement, annotation, operation));
            } else {
                // 不是Argument支持的类型
                error("Argument %s Not support type :%s", feildElement,
                        feildElement.getSimpleName(),
                        feildElement.asType().toString());
            }
        }
        return annotatedFeildSet;
    }


    private boolean isValidClass(TypeElement annotatedClass) {

        if (!ElementValidator.isPublic(annotatedClass)) {
            error("Classes annotated with %s must be public.", annotatedClass, ANNOTATION);
            return false;
        }

        if (ElementValidator.isAbstract(annotatedClass)) {
            error("Classes annotated with %s must not be abstract.", annotatedClass, ANNOTATION);
            return false;
        }

        if (!isFragmentClass(annotatedClass)) {
            error("Classes annotated with %s must be Fragment.", annotatedClass, ANNOTATION);
            return false;
        }

        return true;
    }


    private boolean isFragmentClass(Element classElement) {

        final Types typeUtils = processingEnv.getTypeUtils();

        boolean isFragment = TYPE_FRAGMENT != null
                && typeUtils.isSubtype(classElement.asType(), TYPE_FRAGMENT.asType());

        boolean isSupportFragment = TYPE_SUPPORT_FRAGMENT != null
                && typeUtils.isSubtype(classElement.asType(), TYPE_SUPPORT_FRAGMENT.asType());

        return isFragment || isSupportFragment;
    }


    /**
     * 生成自动的类，Generate FragmentBuilder,_Builder And ArgInjectorImpl.
     *
     * @param list {@link AnnotatedClass} list
     * @throws IOException
     */
    private void generate(List<AnnotatedClass> list) throws IOException {

        if (list == null || list.size() == 0) {
            return;
        }

        debug("=====generate====");

        // 生成每个_Builder
        for (AnnotatedClass annotatedClass : list) {
            if (!annotatedClass.isSupperClass) {
                generateJavaFile(DetailBuilderGenerator.generateClass(annotatedClass),
                        annotatedClass.packageName);
            }
        }

        String packageName = this.packageName;
        if (packageName == null) {
            packageName = list.get(0).packageName;
        }
        if (packageName == null) {
            packageName = Util.BUILDER_PACKAGE_NAME;
        }

        // 总的FragmentBuilder
        generateJavaFile(BuilderGenerator.generateClass(list, builderName), packageName);

        if (!isLibrary) {
            // 注入器实现
            generateJavaFile(InjectorGenerator.generateClass(list), Util.BUILDER_PACKAGE_NAME);
        }

    }

    /**
     * 生成Java文件
     *
     * @param spec        类
     * @param packageName 包名
     * @throws IOException
     */
    private void generateJavaFile(TypeSpec spec, String packageName) throws IOException {
        JavaFile file = builder(packageName, spec).build();
        file.writeTo(processingEnv.getFiler());
    }

    private void debug(CharSequence msg) {
        if (DEBUG) logger(Diagnostic.Kind.NOTE, msg);
    }

    private void logger(Diagnostic.Kind kind, CharSequence msg) {
        processingEnv.getMessager().printMessage(kind, msg);
    }

    private void logger(Diagnostic.Kind kind, CharSequence msg, Element e) {
        processingEnv.getMessager().printMessage(kind, msg, e);
    }

    private void error(String msg, Element e, Object... args) {
        String message = String.format(msg, args);
        logger(Diagnostic.Kind.ERROR, message, e);
    }

    private void error(String msg) {
        logger(Diagnostic.Kind.ERROR, msg);
    }
}
