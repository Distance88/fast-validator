## 参数校验器

- 注解的定义与使用
- Java反射机制以及应用
- SpringAOP的使用
- 异常的抛出与处理

### 1.需求背景

实现请求入参格式校验，支持 `@NotNull` `@NotBlank` `@Max` `@Min` `@IdCard` `@Phone`

### 2.需求分析

#### 2.1 注解的定义与使用

##### 2.1.1 了解注解原理

**元注解**

> 元注解的作用就是注解其他注解，一般我们使用自定义注解时，就需要用元注解来标注我们自己的注解，一共有以下四个元注解

- @Target

  说明了Annotation被修饰的范围，可被用于 packages、types（类、接口、枚举、Annotation类型）、类型成员（方法、构造方法、成员变量、枚举值）、方法参数和本地变量（如循环变量、catch参数）。在Annotation类型的声明中使用了target可更加明晰其修饰的目标

  例：@Target(ElementType.TYPE)

  > 1.ElementType.CONSTRUCTOR:用于描述构造器
  > 2.ElementType.FIELD:用于描述域（类的成员变量）
  > 3.ElementType.LOCAL_VARIABLE:用于描述局部变量（方法内部变量）
  > 4.ElementType.METHOD:用于描述方法
  > 5.ElementType.PACKAGE:用于描述包
  > 6.ElementType.PARAMETER:用于描述参数
  > 7.ElementType.TYPE:用于描述类、接口(包括注解类型) 或enum声明

- @Retention

  定义了该Annotation被保留的时间长短，有些只在源码中保留，有时需要编译成的class中保留，有些需要程序运行时候保留。即描述注解的生命周期

  例：@Retention(RetentionPolicy.RUNTIME)

  > 1.RetentionPoicy.SOURCE:在源文件中有效（即源文件保留）
  > 2.RetentionPoicy.CLASS:在class文件中有效（即class保留）
  > 3.RetentionPoicy.RUNTIME:在运行时有效（即运行时保留）

- @Documented

  它是一个标记注解，即没有成员的注解，用于描述其它类型的annotation应该被作为被标注的程序成员的公共API，因此可以被例如javadoc此类的工具文档化

- @Inherited

  它也是一个标记注解，它的作用是，被它标注的类型是可被继承的，比如一个class被@Inherited标记，那么一个子类继承该class后，则这个annotation将被用于该class的子类。

  > 注意：一个类型被@Inherited修饰后，类并不从它所实现的接口继承annotation，方法并不从它所重载的方法继承annotation。

`自定义注解`

```java
public @interface 注解名 {定义体}
```

使用@interface定义一个注解，自动继承了java.lang.annotation.Annotation接口，其中的每一个方法实际上是声明了一个配置参数。方法的名称就是参数的名称，返回值类型就是参数的类型（返回值类型只能是基本类型、Class、String、enum）。可以通过default来声明参数的默认值。

> 注解参数的可支持数据类型：
>  1.所有基本数据类型（int,float,boolean,byte,double,char,long,short)
>  2.String类型
>  3.Class类型
>  4.enum类型
>  5.Annotation类型
>  6.以上所有类型的数组

定义注解成员的注意点: 只能用public或默认(default)这两个访问权修饰.例如,String value();这里把方法设为defaul默认类型；

##### 2.1.2 自定义注解

请求日志自定义注解

```Java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionLog {

    /**
     * 业务模块名称
     *
     * @return
     */
    String module();

    /**
     * 操作名称
     *
     * @return
     */
    String action();

    /**
     * 控制器出现异常的时候返回消息
     * 此时会打印错误日志
     *
     * @return
     */
    String error() default "操作失败";
}
```

日志注解拦截器

```Java
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    private static final String SPLIT_STRING_M = "=";
    private static final String SPLIT_STRING_DOT = ", ";

    @Pointcut("@annotation(com.zhang.annotation.ActionLog)")
    public void webLog() {
    }

    /**
     * 环绕
     */
    @Around("webLog() && @annotation(actionLog)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, ActionLog actionLog) {
        try {
            // 开始打印请求日志
            HttpServletRequest request = getRequest();
            String urlParams = getRequestParams(request);
            // 打印请求 url
            log.info("请求 URI: {} {}", request.getMethod(), request.getRequestURI());
            if (StringUtils.isNotEmpty(urlParams)) {
                log.info("请求参数: {}", urlParams);
            }
            Object result = proceedingJoinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    /**
     * 获取请求地址上的参数
     *
     * @param request
     * @return
     */
    private String getRequestParams(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> enu = request.getParameterNames();
        //获取请求参数
        while (enu.hasMoreElements()) {
            String name = enu.nextElement();
            sb.append(name).append(SPLIT_STRING_M)
                    .append(request.getParameter(name));
            if (enu.hasMoreElements()) {
                sb.append(SPLIT_STRING_DOT);
            }
        }
        return sb.toString();
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

}
```

我们只需在Controller上的方法贴上自定义注解 `@ActionLog(module = "支付模块", action = "支付接口")` 每次调用带有次注解的方法即可打印我们的请求日志信息

#### 2.2 Java反射机制以及应用

##### 2.2.1 Java反射

在运行状态中，对于任意一个类，都能够获取到这个类的所有属性和方法，对于任意一个对象，都能够调用它的任意一个方法和属性(包括私有的方法和属性)，这种动态获取的信息以及动态调用对象的方法的功能就称为java语言的反射机制。

##### 2.2.2 Java程序运行大致过程

Java源文件（.java文件）–>经过Java[c编译器](https://so.csdn.net/so/search?q=c编译器&spm=1001.2101.3001.7020)编译–>二进制字节码文件（.class文件）–>Jvm类加载器加载–>解释器解释–>机器码（机器可理解的代码）–>操作系统平台

##### 2.2.3 Java反射作用以及原理

###### 2.2.3.1 反射作用

通过反射，可以在运行时动态地创建对象并调用其属性，不需要提前在编译期知道运行的对象是谁。

###### 2.2.3.2 反射原理

简单来说就是通过反编译，来获取类对象的属性、方法等信息。
Java的反射机制是在编译时并不确定是哪个类被加载了，而是在程序运行的时候才加载、探知、自审。使用的是在编译期并不知道的类。

反编译：.class–>.java

注意：Jvm从本地磁盘把字节码文件加载到Jvm内存中，Jvm会自动创建一个class对象。即一个类只会产生一个class对象。
原因：类加载机制–双亲委派机制

##### 2.2.4 类加载机制–双亲委派机制

JVM中提供了三层的ClassLoader：

- BootstrapClassLoader:主要负责加载核心的类库(java.lang.*等)，构造ExtClassLoader和APPClassLoader。
- ExtClassLoader：主要负责加载jre/lib/ext目录下的一些扩展的jar。
- AppClassLoader：主要负责加载应用程序的主函数类。

双亲委派机制下类加载过程如图：

![img](/Users/mac/Desktop/笔记/每天一个轮子/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVG9ybGVzc2U=,size_20,color_FFFFFF,t_70,g_se,x_16.png)



在考虑存在自定义类加载器情况下，对类的加载首先是从自定义类加载器中检查该类是否已经被加载过，如果没有被加载，则向上委托，拿到父类构造器AppClassLoader加载器进行检查，如果还是没有被加载，则依次向上委托，不断检查父类加载器是否已经加载过该类。如果已经加载，则无需进行二次加载，直接返回。如果经过BootstrapClassLoader加载器检查后，发现该类未被加载，则从父类向下开始加载该类。如果BootstrapClassLoader加载器无法加载该类，则交由子类加载器加载，依次向下加载。

双亲委派机制的作用：

- 避免相同类二次加载
- 防止核心类库API被修改

##### 2.2.5 Java反射使用

- 通过Class类中的静态方法forName，来获取类对象

  ```java
  Class clazz1 = Class.forName("全限定类名");
  ```

- 通过类名.class

  ```Java
  Class clazz2  = Demo.class;
  ```

- 通过类的实例获取该类的字节码文件对象

  ```java
  Class clazz3 = p.getClass();
  ```

> 反射获取类属性、方法、构造方法

```Java
public class StudentManager {
      public static void main(String[] args) {
        try {
            //获取类对象
            Class<?> target = Student.class;
            //获取类对象属性  公共部分不能访问私有
            for (Field field : target.getDeclaredFields()) {
                System.out.println(field.getName());
            }
            //获取构造器
            Constructor<?> targetDeclaredConstructor = target.getDeclaredConstructor(String.class, String.class, String.class);
            Object o = targetDeclaredConstructor.newInstance("1815925410", "张耀行", "18移动一班");
            System.out.println(o.toString());
            //实例化对象
            //获取方法
            Method method = target.getMethod("study");
            method.invoke(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



#### 2.3 SpringAOP的注解方式实现

```Java
@Slf4j
@Aspect
@Component
public class ValidAspect {

    
		/**
		 * 层切点
		 */
    @Pointcut(value = "@within(org.springframework.web.bind.annotation.RestController)" +
            "|| @within(org.springframework.stereotype.Controller)")
    public void pointcut() {
    }

    /**
     * 环绕通知
     * @param joinPoint
     * @return
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
      	//获取切点方法参数
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切点方法
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        return joinPoint.proceed();
    }
}
```

#### 2.4 异常的抛出与处理

> \> @ControllerAdvice 默认拦截所有的controller类,添加包名全路径
> \> @ExceptionHandler(BizCoreException.class) 拦截所有的BizCoreException异常

```Java
@ControllerAdvice("com.zhang.controller")
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

    // 自定义异常
    @ExceptionHandler(BizCoreException.class)
    public ResponseEntity<Object> handlerException(BizCoreException ex){
        CommonRestResult<Object> commonRestResult = new CommonRestResult<>();
        commonRestResult.setCode(ex.getCode().getCode());
        commonRestResult.setStatus(CommonRestResult.FAIL);
        commonRestResult.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(commonRestResult);
    }
}
```



### 3.需求实现

#### 3.1 自定义注解

- @Validator

  作用于描述参数上，标记对该参数进行成员变量规则校验

  ```java
  @Target({ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Validator {
  }
  ```

- @NotBlank

  作用于成员变量上，标记对该成员变量的校验规则

  ```java
  @Target({ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface NotBlank {
      String message() default "";
  }
  ```

- 使用方式

  ```java
  @PostMapping("/demo")
  public CommonRestResult<ValidatorVO> demo(@RequestBody @Validator ValidatorForm form) {
       return RestBusinessTemplate.execute(()->{
           ValidatorVO vo = new ValidatorVO();
           vo.setUserName(form.getUserName());
           vo.setAge(form.getAge());
           vo.setIdCard(form.getIdCard());
           vo.setMobile(form.getMobile());
           return vo;
       });
  }
  
  @Data
  public class ValidatorForm {
  
      @NotBlank(message = "用户名不能为空")
      private String userName;
  
      private Integer age;
  
      private String idCard;
  
      private String mobile;
  }
  ```



#### 3.2 定义切面

作用于所有使用@RestController或@Controller的方法

```Java
@Pointcut(value = "@within(org.springframework.web.bind.annotation.RestController)" +
            "|| @within(org.springframework.stereotype.Controller)")
    public void pointcut() {
    }
```

遍历方法中的所有参数，检查参数是否带有@Validator注解，如果没有直接放过，有则根据反射机制获取参数的所有属性，如果属性中没有带有注解直接放过，如果有注解则寻找匹配的校验器注解Map，如果没有适配的直接放过，否则根据注解绑定的Handle去单独处理自己的业务逻辑

```java
@Aspect
@Component
public class ValidAspect {

    private final Map<Class<? extends Annotation>, BaseHandler> handlerMap = new HashMap<>();

    {
        handlerMap.put(NotBlank.class, new NotBlankHandler());
    }

    @Pointcut(value = "@within(org.springframework.web.bind.annotation.RestController)" +
            "|| @within(org.springframework.stereotype.Controller)")
    public void pointcut() {
    }

    /**
     * @param joinPoint
     * @return
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            boolean present = parameters[i].isAnnotationPresent(Validator.class);
            if (!present) {
                continue;
            }
            Object targetObj = args[i];
            Field[] fields = targetObj.getClass().getDeclaredFields();
            //遍历所有的属性
            for (Field field : fields) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                if (annotations.length == 0) {
                    continue;
                }
                //遍历属性的所有注解去处理
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> type = annotation.annotationType();
                    BaseHandler baseHandler = handlerMap.get(type);
                    if (!Objects.isNull(baseHandler)) {
                        baseHandler.handle(annotation, field, targetObj);
                    }

                }
            }
        }
        return joinPoint.proceed();
    }
}
```

#### 3.3 定义handler处理器

每一个注解对应一个handler处理器处理自己的业务逻辑

```Java
public class NotBlankHandler implements BaseHandler{

    @Override
    public void handle(Annotation annotation, Field field, Object target) throws IllegalAccessException {
        NotBlank notBlank = (NotBlank) annotation;
        //暴力获取私有属性，不加则获取不到
        field.setAccessible(true);
        Object o1 = field.get(target);
        ValidatorServiceImpl.getInstance().notBlank(o1, notBlank.message());
    }
}
```

