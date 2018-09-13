# 静态代理

优点：可以在不修改目标对象的前提下扩展目标对象的功能。

缺点：

1. 冗余。由于代理对象要实现与目标对象一致的接口，会产生过多的代理类。
2. 不易维护。一旦接口增加方法，目标对象与代理对象都要进行修改。

# 动态代理

动态代理利用了[JDK API](http://tool.oschina.net/uploads/apidocs/jdk-zh/)，动态地在内存中构建代理对象，从而实现对目标对象的代理功能。动态代理又被称为JDK代理或接口代理。

静态代理与动态代理的区别主要在：

- 静态代理在编译时就已经实现，编译完成后代理类是一个实际的class文件
- 动态代理是在运行时动态生成的，即编译完成后没有实际的class文件，而是在运行时动态生成类字节码，并加载到JVM中

**特点：**
动态代理对象不需要实现接口，但是要求目标对象必须实现接口，否则不能使用动态代理。

JDK中生成代理对象主要涉及的类有

- [java.lang.reflect Proxy](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/reflect/Proxy.html)，主要方法为

```
static Object    newProxyInstance(ClassLoader loader,  //指定当前目标对象使用类加载器

 Class<?>[] interfaces,    //目标对象实现的接口的类型
 InvocationHandler h      //事件处理器
) 
//返回一个指定接口的代理类实例，该接口可以将方法调用指派到指定的调用处理程序。
```

- [java.lang.reflect InvocationHandler](http://tool.oschina.net/uploads/apidocs/jdk-zh/java/lang/reflect/InvocationHandler.html)，主要方法为

```
 Object    invoke(Object proxy, Method method, Object[] args) 
// 在代理实例上处理方法调用并返回结果。
```



实际测试中，JDK的动态类创建过程很快，这是因为在这个内置实现中defineClass（）方法被定义为native实现，故性能高于其它几种实现。但在代理类的函数调用性能上，JDK的动态代理就不如CGLIB和Javassist的基于动态代理的代理。



当你的目标对象实现了接口但调用的方法不是接口方法的时候，你只能使用CGLib proxy，因为JDK动态代理的原理就是实现和目标对象同样的接口， 因此只能调用那些接口方法。Cglib则没有此限制，因为它所创建出来的代理对象就是目标类的子类，因此可以调用目标类的任何方法。

就创建的动态代理对象的性能来说，CGLib 是 JDK 的 10 倍，而创建动态代理对象所花费的时间上，CGLib 却比 JDK 多花 8 倍的时间。 所以，对于单例模式或者具有实例池的代理类，适合采用 CGLib 技术，反之，则适合采用 JDK 技术。

SpringAOP会优先选择JDK动态代理，当调用方法不是接口方法时，就只能选择Cglib了

一、为什么不直接都使用JDK动态代理：
JDK动态代理只能代理接口类，所以很多人设计架构的时候会使用
XxxService, XxxServiceImpl的形式设计，一是让接口和实现分离，二是也有助于代理。

 二、为什么不都使用Cgilb代理： 因为JDK动态代理不依赖其他包，Cglib需要导入ASM包，对于简单的有接口的代理使用JDK动态代理可以少导入一个包。 cglib无法代理final方法。 

  

 

 

 