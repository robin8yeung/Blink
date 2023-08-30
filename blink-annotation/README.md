# blink-annotation

Blink提供了通过注解来声明页面路由的方式，通过ksp处理注解信息来为app生成路由表以及每个module的路由表初始化接口

## 注解概念

### BlinkUri

声明页面的路由地址，即uri，这里的uri应该是一个包含完整scheme，host和path，但不包含参数的uri，如"scheme://my.host/path"
值得注意的是，页面对应的uri可以是一个，也可以是多个。被注解的对象只能是Activity或Fragment的子类，否则将不会被blink-ksp处理

### BlinkMetadata

由于ksp生效的范围是单个module，每个module都需要对blink-ksp生成的路由表进行初始化。
我们没有选择通过Gradle plugin的transform来进行初始化，主要考虑这个操作是个低频的操作，而transform在方便的同时也会带来编译时的开销
所以和koin类似，选择了通过ksp生成简单的初始化接口，由开发者在每个模块中进行初始化（推荐使用Jetpack-startup），或者在Application中，逐一对各个模块进行初始化

## 使用示例

#### 依赖引入

blink-annotation不需要单独引入，blink-activity和blink-fragment都包含了相关依赖
由于ksp的生效范围是module，所以【每一个】使用过blink-annotation的【module】都需要引入blink-ksp来处理这些注解

- 最新版本：[![](https://jitpack.io/v/robin8yeung/Blink.svg)](https://jitpack.io/#robin8yeung/Blink)

```groovy
ksp "com.github.robin8yeung.Blink:blink-ksp:$version"
```

#### 实际使用

路由uri定义

```kotlin
// 为MyFragment定义一个路由uri
@BlinkUri(Uris.fragment)
class MyFragment: Fragment() {
    // ....
}

// 为MyActivity定义多个路由uri
@BlinkUri(value = [ Uris.activity, Uris.HOME ])
class MyActivity: Activity() {
    // ....
}
```

路由表注入入口定义和实现module路由表的注入

```kotlin
// 用@BlinkMetadata注解定义一个路由表的初始化入口，为了简化调用，请继承BaseMetadata
@BlinkMetadata
class RouteMetadata : BaseMetadata()

// lib module建议用startup框架来实现初始化，也可以在Application的onCreate中对所有模块的BaseMetadata子类进行初始化调用
class AvatarInitializer : Initializer<Unit>{
    override fun create(context: Context) {
        // 初始化，注入module的路由表，建立uri与页面的映射关系，否则无法实现路由跳转
        RouteMetadata().inject()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> =
        mutableListOf()
}
```