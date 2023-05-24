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

[未完待续]
