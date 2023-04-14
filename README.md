# Blink

[![license](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](./LICENSE)

Blink的名字取自dota中的"闪烁Blink"技能，敌法师、痛苦女王等英雄通过闪烁技能可以闪现到指定位置。
Blink是一套基于Uri的Activity路由框架，主要用于App内部的跨组件路由。

## 主要功能

- Activity路由跳转并携带参数，返回路由结果
- Activity数据返回回调（告别startActivityForResult）
- 全局路由拦截器，可设置优先级，也可以动态添加和删除

## 解决一些第三方路由痛点

这里主要对标ARouter

- ARouter
    - 功能包含了Activity的路由，以及全局拦截器，拦截器不支持动态增删
    - 也包含了Fragment和Interface的依赖注入
    - 使用注解，通过apt的方式编译时生成代码来创建路由表，并在全局初始化时加载路由表

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

相关方法的返回为Result<Unit>，可以从中获取路由结果。路由失败的原因主要有：

- ActivityNotFoundException 无法找到uri对应的Activity
- 自定义异常 被路由拦截，推荐在拦截器抛InterruptedException或其子类来进行路由拦截

```kotlin
context.blink(Uri.parse("blink://navigator/example?name=Blink"))
```

```java
Blink.navigation(context, Uri.parse("blink://navigation/example?name=Hello"));
```

### 3、Activity获取传入参数

```kotlin
import android.app.Activity

class ExampleActivity : Activity() {
    // Name参数传入
    private val name: String? by lazy { intent.data?.getQueryParameter("name") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ....
    }
}
```

### 4、增删拦截器

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

## License

Blink is [Apache v2.0 licensed](./LICENSE).