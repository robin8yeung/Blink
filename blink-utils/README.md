# Blink-utils

## 主要功能

- 目前仅包含一些快速操作Uri的扩展函数

## 具体说明

### 为Uri设置参数时，建议使用以下append方法，而不是Uri.Builder自带的appendQueryParameter方法，主要做以下几点处理

1. 可以正确的传入列表
2. 避免null被转为"null"或""传入到参数中，导致参数失真
3. 对于非String类型的一些常用数据类型，做了相关序列化处理
4. 目前支持的类型：基本类型，String，枚举，以上几种类型的列表
5. 复杂数据结构建议序列化为json后传入，或者转化成Intent后传入。
6. 庞大的数据不建议通过路由参数传递给页面，如有必要，建议通过静态对象等来传递

### append结合fun String.buildUri()和 fun Uri.build()两个扩展函数可以方便的对Uri进行修改和传参

## 使用示例

#### 依赖引入

blink-utils不需要单独引入，blink-activity和blink-fragment都包含了相关依赖

#### 实际使用

```kotlin
val uri = "scheme://my.app.com/index?id=seewo"
val result = uri.buildUri {
    path("/home")
    append("page", 3)
    append("tag", null)
}
// 以上语句返回一个Uri给result，内容为: cheme://my.app.com/home?id=seewo&page=3
```