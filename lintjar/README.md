# lint

## 参考文章

官方教程：<https://developer.android.com/studio/write/lint>

官方示例：<https://github.com/googlesamples/android-custom-lint-rules>

节选自<https://developer.android.com/studio/build/dependencies>中的一些内容：
```
lintChecks 使用此配置可以添加您希望 Gradle 在构建项目时执行的 lint 检查。注意：使用 Android Gradle 插件 3.4.0 及更高版本时，此依赖项配置不再将 lint 检查打包在 Android 库项目中。如需将 lint 检查依赖项包含在 AAR 库中，请使用下面介绍的 lintPublish 配置。

lintPublish 在 Android 库项目中使用此配置可以添加您希望 Gradle 编译成 lint.jar 文件并打包在 AAR 中的 lint 检查。这会使得使用 AAR 的项目也应用这些 lint 检查。如果您之前使用 lintChecks 依赖项配置将 lint 检查包含在已发布的 AAR 中，则需要迁移这些依赖项以改用 lintPublish 配置。

  dependencies {
    // Executes lint checks from the ':checks' project
    // at build time.
    lintChecks project(':checks')
    // Compiles lint checks from the ':checks-to-publish'
    // into a lint.jar file and publishes it to your
    // Android library.
    lintPublish project(':checks-to-publish')
  }
```

节选自<https://developer.android.com/studio/releases/gradle-plugin#new_features>中的一些内容：
```
新功能
新的 Lint 检查依赖项配置：更改了 lintChecks 的行为并引入了新的依赖项配置 lintPublish，以便您可以更好地控制要将哪些 Lint 检查打包到 Android 库中。

lintChecks：您应该将这个现有配置用于您想仅在本地构建项目时运行的 Lint 检查。如果您之前使用 lintChecks 依赖项配置将 Lint 检查包含在已发布的 AAR 中，则需要迁移这些依赖项并改用新的 lintPublish 配置（如下所述）。
lintPublish：针对您想要在已发布的 AAR 中包含的 Lint 检查，在库项目中使用这个新配置（如下所示）。这意味着，使用库的项目也会应用那些 Lint 检查。
以下代码示例在本地 Android 库项目中使用这两个依赖项配置。

dependencies {
      // Executes lint checks from the ':lint' project at build time.
      lintChecks project(':lint')
      // Packages lint checks from the ':lintpublish' in the published AAR.
      lintPublish project(':lintpublish')
    }
    
一般来说，打包任务和为任务签名应该会加快整体构建速度。如果您发现与这些任务相关的性能退化，请报告错误。
```

## 开发步骤

### 创建Lint编码模块

new Module，选择Java or Kotlin Library。命名为lintjar，用于编写自定义的Lint规则。

build.gradle内容如下：

```groovy
apply plugin: 'java'
apply plugin: 'kotlin'

dependencies {
    compileOnly rootProject.ext.libs.compileOnly.lint_api
    compileOnly rootProject.ext.libs.compileOnly.lint_checks
    compileOnly rootProject.ext.libs.kotlin
}
```

### 编写自定义的Lint规则

先编写Detector类，大概代码如下：
```kotlin
package com.example.lintjar
@Suppress("UnstableApiUsage")
class SampleCodeDetector : Detector(), UastScanner {
    //省略一些内容
}
```

再编写IssueRegistry类，代码如下：
```kotlin
package com.example.lintjar

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API

@Suppress("UnstableApiUsage")
class SampleIssueRegistry : IssueRegistry() {
    override val issues = listOf(SampleCodeDetector.ISSUE)

    override val api: Int
        get() = CURRENT_API
}
```

### 注册IssueRegistry类

创建名为`com.android.tools.lint.client.api.IssueRegistry`的文件，内部填写内容形式为`包名.之前编写的IssueRegistry类名`，如：`com.example.lintjar.SampleIssueRegistry`，并根据下面的目录结构将该文件放在`services`文件夹中：
```
lintjar
  |--src
       |--main
            |--java //存放自定义的Lint代码
            |--resources //注册IssueRegistry类
                 |--META-INF
                      |--services
```

### 创建Lint引用模块

new Module，选择Android Library。命名为lintaar。目的是让别的模块依赖该模块的时候就能使用自定义的Lint功能。

build.gradle内容如下：

```groovy
apply plugin: 'com.android.library'

android {
    compileSdkVersion project.COMPILE_SDK_VERSION as int
    buildToolsVersion project.BUILD_TOOLS_VERSION

    defaultConfig {
        minSdkVersion project.MIN_SDK_VERSION as int
        targetSdkVersion project.TARGET_SDK_VERSION as int
        versionCode 1
        versionName "1.0"
    }
}

/** Package the given lint checks library into this AAR  */
dependencies {
    lintPublish rootProject.ext.module.lint.lintjar
}
```

## 使用

1. Clean Project
2. Rebuild Project
3. 打开LintTest类验证效果

默认lint输出位置：file://`省略路径，每台设备都不一样`/AndroidLearn/app/build/reports/lint-results-debug.html
