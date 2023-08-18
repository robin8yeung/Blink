# Blink (Activity)

## 主要功能

- Activity路由跳转并携带参数，返回路由结果
- Activity数据返回回调（告别startActivityForResult）
- 全局路由拦截器，可设置优先级，也可以动态添加和删除

## 解决一些第三方路由痛点

这里主要对标ARouter

- ARouter
    - 功能包含了Activity的路由，以及全局拦截器，拦截器不支持动态增删
    - 也包含了Fragment和接口的依赖注入（对接口的依赖注入较简单粗暴，仅支持单例，无法自定义实现类的构造方法）
    - 使用注解，通过字节码插桩和apt的方式编译时生成代码来创建路由表，并在全局初始化时加载路由表，造成一定的编译时开销

- Blink
    - 功能仅包含Activity路由和全局拦截器，拦截器支持动态增删，但包含了页面结果回调。
    - 对于依赖注入的场景建议引入专门的依赖注入框架，如koin等
    - 使用ksp处理注解，不引入gradle plugin，避免造成过多编译时开销

## 接入指南

### 1、依赖引入

最新版本：[![](https://jitpack.io/v/robin8yeung/Blink.svg)](https://jitpack.io/#robin8yeung/Blink)

```groovy
implementation "com.seewo.library:blink-activity:$version"
```

### 2、为页面定义路由uri

#### uri

通过BlinkUri注解来定义页面路由uri。路由uri作为路由地址用于映射页面，发起路由时会从路由表中。

#### 注意：路由表必须完成注入才能正常使用

> 关于路由表注入请先了解 [blink-annotation](../blink-annotation/README.md)

```kotlin
object Uris {
    const val activity = "blink://my.app/activity"
    const val HOME = "blink://my.app/home"
}

// 为MyActivity定义单个路由uri
@BlinkUri(Uris.activity)
class MyActivity : Activity() {
    // ....
}

// 为MyActivity定义多个路由uri
@BlinkUri(value = [Uris.activity, Uris.HOME])
class MyActivity : Activity() {
    // ....
}
```

### 3、路由与传参

对于路由跳转，kotlin建议使用Context.blink扩展函数，java则使用Blink.navigation的静态方法

> 如果需要对Uri进行复杂的参数设置，可以借助Uri.build()、String.buildUri()等扩展方法，
> 详见 [blink-utils](../blink-utils/README.md)

#### 异常处理

kotlin中推荐使用扩展函数来调用，对于扩展函数的相关方法的返回为Result<Unit>，可以从中获取路由结果。路由失败的原因主要有：

- ActivityNotFoundException 无法找到uri对应的Activity
- 自定义异常 被路由拦截，推荐在拦截器抛InterruptedException或其子类来进行路由拦截

kotlin中使用

```kotlin
context.blink("blink://navigator/example?name=Blink").onFailure {
    // 处理异常
}.onSuccess {
    // 路由成功
}
```

对于java中使用，提供了Blink为入口的静态方法。但需要注意的是，因为java不支持Result，所以对于Blink的静态方法，异常会直接抛出，如有需要，请务必在java业务端做try-catch

java中使用

```java
try {
    Blink.navigation(context,Uri.parse("blink://navigation/example?name=Hello"));
} catch(Exception e){
    // 处理异常
}
```

### 4、参数获取

kotlin中实现参数获取

```kotlin
import android.app.Activity

class ExampleActivity : Activity() {
    // 业务自行处理Name参数传入
    private val name: String? by lazy { intent.data?.getQueryParameter("name") }

    // 由Blink提供懒加载函数进行参数注入，默认值可选。仅用于Activity
    private val age: Int by intParams("age", 18)
}
```

java中实现参数注入推荐使用BlinkParams注解配合Blink.inject()方法。

特别注意：注意这个方法对于Activity和Fragment具有不同的实现。Fragment仅用于ARouter老项目迁移，不推荐使用。

- 对于Activity的注入，主要从intent.data，即uri中去获取传入的参数，支持的类型较少
- 对于Fragment的注入，主要从arguments，即Bundle中去获取传入的参数，支持的类型较多（但也不是支持Bundle的全部类型，详见源码）

```java
import android.app.Activity;

import com.seewo.blink.BlinkParams;
import com.seewo.blink.Blink;

public class ExampleActivity extends Activity {
    @BlinkParams(name = "name")
    private String name;

    @BlinkParams(name = "age")
    private int age = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 执行参数注入
        Blink.inject(this);
    }
}
```

```java
import androidx.fragment.app.Fragment;

import com.seewo.blink.BlinkParams;
import com.seewo.blink.Blink;

public class ExampleFragment extends Fragment {
    @BlinkParams(name = "name")
    private String name;

    @BlinkParams(name = "age")
    private int age = 18;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Blink.inject(this);
        return new YourView(inflater, container, savedInstanceState);
    }
}
```

### 5、增删拦截器

kotlin中使用

```kotlin
// 这里仅用于举例，真实使用时，建议拦截器职责单一
class LoggerInterceptor : Interceptor {
    override fun process(context: Context, intent: Intent) {
        // 打印路由信息
        FLog.a("from $context to $intent data: ${intent.dataString}")
        // 获取路由请求的参数，修改path并增加参数
        val uri = intent.data
        intent.data = uri?.build {
            path("/another")
            append("new", true)
        }
        // 对于缺少权限的情况，拦截跳转
        if (!Permission.hasCameraPermission) {
            interrupt("缺少必要权限")
        }
    }
}

val loggerInterceptor = LoggerInterceptor()

// 添加拦截器
loggerInterceptor.attach()
// 移除拦截器
loggerInterceptor.detach()
```

java中使用

```java
LoggerInterceptor loggerInterceptor = new LoggerInterceptor();
// 添加拦截器
Blink.add(loggerInterceptor);
// 移除拦截器
Blink.remove(loggerInterceptor);
```

### 6、结果回调

```kotlin
import android.app.Activity

class PrevActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example)
        findViewById<View>(R.id.button).setOnClickListener {
            // 跳转至EXAMPLE_2
            blink(Uris.EXAMPLE_2) {
                // ActivityResult回调
                if (it.resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Return result: ${it.data}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Return result: Cancel", Toast.LENGTH_LONG).show()
                }
            }.onFailure {
                // 路由如果存在异常
                Log.e("BLINK", it.message, it)
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class NextActivity : Activity() {
    private val name: String? by lazy { intent.data?.getQueryParameter("name") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example)
        findViewById<View>(R.id.button).setOnClickListener {
            // 点击按钮，返回成功结果
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("result", "ok")
            })
            finish()
        }
    }
}
```

## 特别关注

出于简化使用考虑，整个路由的过程，包括拦截器的处理过程，均是同步调用的，那么当你使用拦截器，需要关注以下一些点：

- 对于专用拦截器，设置合理的过滤条件，仅对于需要拦截的跳转生效
- 拦截器中避免做耗时操作
- 拦截器中需要做异步拦截后跳转（如弹窗等待用户点击后再跳转），可以先拦截此次跳转并弹窗，在弹窗点击后再执行一次新的路由。
    - 对于这种情况，要小心新的路由可能仍然被当前拦截器拦截，造成死循环，所以如有必要，对Intent增加必要参数，避免被二次拦截，Blink提供了绿色通道来解决这个问题。

```kotlin
class PluginInterceptor : Interceptor {
    private val caredPath = Uris.PLUGIN.toUri().path

    // 仅对plugin的path生效
    override fun filter(intent: Intent) =
        intent.data?.path == caredPath

    // 设置拦截器优先级
    override fun priority() = -2

    override fun process(context: Context, intent: Intent) {
        val activity = context as? Activity
        when {
            Build.VERSION.SDK_INT < 29 -> {
                // 可以抛不同的异常，来在路由调用端针对不同的异常进行提示。此处举例不涉及
                interrupt("系统版本过低")
            }
            !PluginConfig.isPluginEnable -> {
                interrupt("用户无权限")
            }
            else -> {
                activity?.let {
                    // 弹窗并加载插件
                    Dialog.loadPluginWithDialog(
                        activity, ResourceTag.plugin
                    ) { exception ->
                        if (exception != null) {
                            // 加载异常，不执行路由
                            FLog.e(exception)
                        } else {
                            // 加载完成，执行路由。为避免再次被此拦截器拦截，添加绿色通道属性
                            activity.blink(putInGreenChannel(intent))
                        }
                    }
                }
                // 需要弹窗确认，加载plugin，直接拦截同步路由跳转
                interrupt("需要下载插件")
            }
        }
    }
}
```
