# Blink

[![license](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](./LICENSE)

Blink的名字取自dota中的"闪烁Blink"技能，敌法师、痛苦女王等英雄通过闪烁技能可以闪现到指定位置。

![LOGO](doc/logo.jpeg)

## 框架说明

Blink是一套基于Uri的Activity路由框架，主要用于App内部的跨组件路由。为了方便老项目快速接入，提供了一些Java友好的接口

> 详见：[Blink](./blink-activity/README.md)

Blink还包含一套基于Uri的Fragment路由框架，主要用于实现【单Activity应用】。建议基于kotlin接入

> 详见：[Blink-fragment](./blink-fragment/README.md)

以上提及的两套框架可以单独使用，也可以同时使用

## 特别说明

Blink提供了纯手动创建路由表的接口，但更推荐通过注解和KSP自动创建路由表

> 详见：[Blink-annotation](./blink-annotation/README.md)

Blink基于Uri来实现路由，为了更方便开发者操作Uri，Blink提供了一些好用的扩展方法

> 详见: [Blink-utils](./blink-utils/README.md)

## License

Blink is [Apache v2.0 licensed](./LICENSE).