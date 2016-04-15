# FragmentArg
fragment arg 注解

一个注解的demo，实现注解自动完成Argument。

参考：
<https://github.com/sockeqwe/fragmentargs>

## 使用

```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.0.0'
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.4'
    }
}
```

```
apply plugin: 'com.neenbedankt.android-apt'
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.qinglinyi.arg:arg-api:0.9.0'
    apt 'com.qinglinyi.arg:arg-compiler:0.9.0'
}

apt {
    arguments {
        // FragmentBuilder的包
        argPackageName "com.qinglinyi.arg.sample"
    }
}
```
**Fragment**

```
@UseArg
public class MyFragment extends Fragment {

    @Arg
    String name;

    @Arg
    int age;

    @Arg
    ArrayList<String> interests;

    @Arg
    String[] friendNames;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArgInjector.inject(this);
    }
    ...
```


**Activity**两种方式

```
 mFragment0 = FragmentBuilder.builder(new MyFragment())
                .age(15)
                .name("张三")
                .friendNames(new String[]{"李四", "王五"})
                .interests(list)
                .build();
```

```
 mFragment1 = new MyFragmentBuilder().age(15)
                .name("李四")
                .friendNames(new String[]{"张三", "王五"})
                .interests(list)
                .build();
```



