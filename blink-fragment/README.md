# Blink-fragment

Blink-fragment主要用于实现单Activity应用的路由框架

## 单Activity应用

整个应用内基本只基于一个Activity实现，用Fragment代替之前的Activity来作为页面载体，主要有以下一些优点：

- Dialog依赖于Activity，多Activity应用中无法实现真正的全局弹窗，而单Activity可以
- 浮动窗口权限需要申请，而单Activity应用可以不需要申请浮动窗口权限也可实现应用内的浮动窗口（仅能展示在应用内）
- Fragment比Activity轻，性能有一定优势
- Activity需要静态注册在AndroidManifest中，Fragment则不用，很容易实现动态化

一些Blink-fragment暂时没实现的功能

- 共享元素转场动画：在框架中实现的成本较大，建议开发者用普通的Fragment api来实现

单Activity应用的局限性：

- 无法使用系统默认的转场动画（默认使用的是框架中的自定义动画）
- 切换页面引起屏幕方向变化时，会展示窗口旋转动画，而不是像一些机型的无缝旋转动画
- Fragment的生命周期比Activity复杂
- 更多的坑需要接入后发现，to be continue...

## 框架功能

- 手动注册路由表或通过ksp自动注册路由表（每个module需要进行一次初始化来完成注册）
- 基于uri的导航和传参
- 基于回调的结果返回
- 带优先级的动态拦截器
- LaunchMode设置：支持standard，singleTop，singleTask 3种。singleInstance建议新开一个Activity
- 默认属性静态设置：屏幕方向、自定义转场动画、SystemUI（状态栏、导航栏）。需要动态改变这些属性的，请不要进行静态设置，否则页面切换时会回到静态设置的表现
- 可设置页面不在栈顶时是否保活（返回到非保活页面，页面将会重建）

## 解决一些第三方路由痛点

这里主要对标 JetPack Navigation-fragment

- Navigation
    - 路由图基于xml实现，使用较为繁琐
    - 栈内fragment会销毁重建，无法保持状态
    - 没有LaunchMode的概念，需要自己实现

- Blink
    - 路由图通过ksp注解实现，接入方便
    - 栈内fragment是否保持状态可以通过注解定义
    - 可通过注解快速定义LaunchMode

## 接入指南

### 1、依赖引入

```groovy
implementation "com.seewo.library:blink-fragment:$version"
```

### 2、为页面定义路由uri

#### uri

通过BlinkUri注解来定义页面路由uri。路由uri作为路由地址用于映射页面，发起路由时会从路由表中。

注意：路由表必须完成注入才能正常使用，

关于路由表注入请先了解 [blink-annotation](../blink-annotation/README.md)

```kotlin
object Uris {
    const val fragment = "blink://my.app/fragment"
    const val HOME = "blink://my.app/home"
}

// 为MyFragment定义单个路由uri
@BlinkUri(Uris.fragment)
class MyFragment : Fragment() {
    // ....
}

// 为MyFragment定义多个路由uri
@BlinkUri(value = [Uris.fragment, Uris.HOME])
class MyFragment : Fragment() {
    // ....
}
```

### 3、异常处理

kotlin中推荐使用Fragment扩展函数来调用，对于扩展函数的相关方法的返回为Result<Unit>，可以从中获取路由结果。

路由失败的原因主要有：

- FragmentNotFoundException 无法找到uri对应的Fragment
- 自定义异常 被路由拦截，推荐在拦截器抛InterruptedException或其子类来进行路由拦截

```kotlin
blink("blink://navigator/example?name=Blink").onFailure {
    // 处理异常
}.onSuccess {
    // 路由成功
}
```

### 4、路由传参

对于路由跳转，使用 Fragment.blink() 扩展函数或 blinkFragment() 顶级函数

> 如果需要对Uri进行复杂的参数设置，可以借助Uri.build()、String.buildUri()等扩展方法，
> 详见 [blink-utils](../blink-utils/README.md)

### 5、参数获取

在Fragment中获取路由传进来的参数

```kotlin
class ExampleFragment : Fragment() {

    // 业务自行处理Name参数传入
    private val name: String? by lazy { arguments?.uriOrNull?.getQueryParameter("name") }

    // 由Blink提供懒加载函数进行参数注入，默认值可选。仅用于Activity
    private val age: Int by intParams("age", 18)
}
```

### 6、结果回调

```kotlin
class PrevFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        findViewById<View>(R.id.button).setOnClickListener {
            // 跳转至EXAMPLE_2
            blink(Uris.EXAMPLE_2) {
                // 结果返回回调
                if (it != null) {
                    Toast.makeText(this, "Return result: $it", Toast.LENGTH_LONG).show()
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

class NextFragment : Fragment() {
    private val uri: Uri by uriNonNull
    private val name: String? by uri.stringParams("name")

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        findViewById<View>(R.id.button).setOnClickListener {
            // 点击按钮，返回结果
            pop(Bundle().apply {
                putInt("result", 1)
            })
        }
        findViewById<View>(R.id.back).setOnClickListener {
            // 点击返回，直接返回
            pop()
        }
    }
}
```

### 7. 回退到指定页面

blink-fragment也支持回退到指定页面，通过uri来指定要回退到的页面。需要关注几个点：

1. 如果回退栈中存在多个uri定义相同的Fragment，那会回退到最近的一个
2. 需要回退到首个Fragment，即通过 BlinkContainerActivity.startFragment()传入的Fragment，其对应的uri为空字符串
3. 通过这种方式返回，无法返回结果，如果需要返回结果，则使用pop(result)方法

```kotlin
@BlinkUri(Uris.FINAL_FRAGMENT)
@Orientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
class FinalFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFinalBinding.inflate(inflater, container, false).apply {
        next.setOnClickListener {
            // 直接回退到HomeFragment
            popTo("")
        }
    }.root

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 拦截返回键，直接回退到HomeFragment
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 直接回退到HomeFragment
                popTo("")
            }
        })
    }
}
```

### 8、LaunchMode

blink-fragment的LaunchMode类似Activity的LaunchMode。

支持standard，singleTop，singleTask 3种。singleInstance建议新开一个Activity

但和Activity的LaunchMode实在AndroidManifest中定义的不同，可以通过注解来定义页面的LaunchMode

```kotlin
// 为MyFragment定义LaunchMode为standard
@BlinkUri(Uris.fragment)
class MyFragment : Fragment() {
    // LaunchMode为默认的standard
}

// 为MyFragment定义LaunchMode为singleTop，继承SingleTopFragment即可
@BlinkUri(Uris.fragment)
class MyFragment : SingleTopFragment() {
    override fun onNewArguments(arguments: Bundle?) {
        // 重复打开时，会回调此方法
    }
}

// 为MyFragment定义LaunchMode为singleTask，继承SingleTaskFragment即可
@BlinkUri(Uris.fragment)
class MyFragment : SingleTaskFragment() {
    override fun onNewArguments(arguments: Bundle?) {
        // 重复打开时，会回调此方法
    }
}
```

### 9、属性注解

类似Activity可以在AndroidManifest中定义orientation属性，blink-fragment也支持在Fragment中定义一些属性，同样也是通过注解的方式来定义。

一些页面的默认外观属性是十分有必要在Fragment中定义的，比如是否全屏，是否显示状态栏等。

因为这些Fragment都从属于一个Activity，页面切换时必须把样式设置为当前Fragment的默认样式

| 注解                | 功能                 | 备注                                                                            |
|--------------------|----------------------|-------------------------------------------------------------------------------|
| Orientation        | 定义页面默认屏幕方向     | [详见备注](src/main/java/com/seewo/blink/fragment/annotation/Orientation.java)    |
| SystemUI           | 定义页面样式            | [详见备注](src/main/java/com/seewo/blink/fragment/annotation/SystemUI.kt)         |
| CustomAnimations   | 定义页面切换转场动画     | [详见备注](src/main/java/com/seewo/blink/fragment/annotation/CustomAnimations.kt) |
| KeepAlive          | 定义页面是否保活        | [详见备注](src/main/java/com/seewo/blink/fragment/annotation/KeepAlive.java)      |

### 10、增删拦截器

```kotlin
// 这里仅用于举例，真实使用时，建议拦截器职责单一
class LoggerInterceptor : Interceptor {
    override fun process(from: Fragment?, target: Bundle) {
        // 打印路由信息
        Log.i("blink", "[from] $from [target] ${target.uriOrNull}")
        // 获取路由请求的参数，修改path并增加参数
        val uri = target.uriOrNull
        target.setUri(target.uriOrNull?.build {
            path("/another")
            append("new", true)
        })
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
    override fun filter(target: Bundle) =
        target.uriOrNull?.path == caredPath

    // 设置拦截器优先级
    override fun priority() = -2

    override fun process(from: Fragment?, target: Bundle) {
        val activity = from?.requireActivity()
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
                            from?.blink(putInGreenChannel(target))
                                ?: blinkFragment(putInGreenChannel(target))
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

