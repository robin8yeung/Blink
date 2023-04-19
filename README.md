# Blink

[![license](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](./LICENSE)

Blink的名字取自dota中的"闪烁Blink"技能，敌法师、痛苦女王等英雄通过闪烁技能可以闪现到指定位置。

Blink是一套基于Uri的Activity路由框架，主要用于App内部的跨组件路由。

![LOGO](doc/logo.jpeg)

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
    - 路由生命在AndroidManifest，不使用apt，避免编译时开销，但样板代码较多

## 使用示例

### 1、AndroidManifest.xml中定义路由uri

#### uri

uri主要定义在 intent-filter中的data标签中，如果scheme和host相对固定，可以定义在strings.xml中方便统一管理
如 scheme为blink host为navigation，path为/example，则整个导航到ExampleActivity的Uri为 blink://navigation/example

#### action

action "blink.action.VIEW"为Blink的默认路由Action，也可以自定义(设置在Blink.action字段中)

#### category

category需要设置为android.intent.category.DEFAULT

#### exported

出于合规和安全考虑，如非必要，请把activity标签的exported设置为false

```xml
<!--AndroidManifest.xml中的路由定义-->
<application>
    <activity android:name=".ExampleActivity" android:exported="false">
        <intent-filter>
            <action android:name="blink.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <data android:scheme="@string/scheme" android:host="@string/host"
                android:path="/example" />
        </intent-filter>
    </activity>
</application>
```

### 2、路由与传参

对于路由跳转，kotlin建议使用Context.blink扩展函数，java则使用Blink.navigation的静态方法

> 如果需要对Uri进行复杂的参数设置，可以借助Uri.Builder类

#### 函数返回

kotlin中推荐使用扩展函数来调用，对于扩展函数的相关方法的返回为Result<Unit>，可以从中获取路由结果。路由失败的原因主要有：

- ActivityNotFoundException 无法找到uri对应的Activity
- 自定义异常 被路由拦截，推荐在拦截器抛InterruptedException或其子类来进行路由拦截

kotlin中使用
```kotlin
context.blink(Uri.parse("blink://navigator/example?name=Blink"))
```

对于java中使用，提供了Blink为入口的静态方法。但需要注意的是，因为java不支持Result，所以对于Blink的静态方法，异常会直接抛出，如有需要，请务必在java业务端做try-catch

java中使用
```java
Blink.navigation(context, Uri.parse("blink://navigation/example?name=Hello"));
```

### 3、参数注入

kotlin中实现参数

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

特别注意：注意这个方法对于Activity和Fragment具有不同的实现

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

### 4、增删拦截器

kotlin中使用

```kotlin
class LoggerInterceptor : Interceptor {
    override fun process(context: Context, intent: Intent) {
        FLog.a("from $context to $intent data: ${intent.dataString}")
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

### 4、结果回调

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
            }.exceptionOrNull()?.let {
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

处于简化使用考虑，整个路由的过程，包括拦截器是同步调用的，那么当你使用拦截器，需要关注以下一些点：

- 对于专用拦截器，设置合理的过滤条件，进对于需要拦截的跳转生效
- 拦截器中避免做耗时操作
- 拦截器中需要做异步拦截后跳转（如弹窗等待用户点击后再跳转），可以先拦截此次跳转并弹窗，在弹窗点击后再执行一次新的路由。
  - 对于这种情况，要小心新的路由可能仍然被当前拦截器拦截，造成死循环，所以如有必要，对Intent增加必要参数，避免被二次拦截。

## License

Blink is [Apache v2.0 licensed](./LICENSE).