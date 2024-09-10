# 荣耀 Health Kit 接入示例 Demo 代码

[![licenses Apache](https://img.shields.io/badge/license-Apache-blue)](https://gitee.com/link?target=http%3A%2F%2Fwww.apache.org%2Flicenses%2FLICENSE-2.0) [![Java Language](https://img.shields.io/badge/language-java-green.svg)](https://gitee.com/link?target=https%3A%2F%2Fwww.java.com%2Fen%2F)

## 目录
* [荣耀 Health Kit 接入示例 Demo 代码](#荣耀-Health-kit-接入示例-demo-代码)
  * [目录](#目录)
  * [简介](#简介)
  * [环境要求](#环境要求)
  * [硬件要求](#硬件要求)
  * [开发准备](#开发准备)
  * [demo 说明](#demo说明)
  * [安装](#安装)
  * [技术支持](#技术支持)
  * [授权许可](#授权许可)

## 简介
本示例代码中，你将使用已创建的代码工程来调用运动健康服务（Health Kit）的接口。通过该工程，你将：

1. 了解适配 Health Kit 具体如何配置。
2. 了解适配 Health Kit 之后如何写入和查询运动健康相关数据，
创造更多的应用功能。

更多内容，可以登陆[荣耀开发者服务平台](https://developer.honor.com/cn/)，点击产品 - 开放能力 - 运动健康服务进行了解

## 环境要求
推荐使用的安卓 targetSdk 版本为 31 及以上，JDK 版本为 1.8.211 及以上。

## 硬件要求
安装有 Windows 10/Windows 7 操作系统的计算机（台式机或者笔记本） 带USB数据线的荣耀手机，用于业务调试。

## 开发准备
针对 Android Studio 开发环境，荣耀提供了 Maven 仓集成方式的 SDK 包。
客户端开发环境
安装 Android Studio 3.X 及以上。

测试设备：手机安装荣耀运动健康（App 与 SDK 版本匹配关系请参见[版本信息](https://developer.honor.com/cn/docs/11005/guides/version-history)）

配置开发环境
在 Android studio 中配置 gradle 的 jdk 版本为 1.8 及以上。
      

    	1.打开 Android studio 设置。
    	2.配置 gradle 的 jdk 版本为 1.8 及以上。
## demo说明

### 登录和授权

health kit 的使用依赖用户授权，所以使用 health kit 前需要您接入荣耀账号服务，并使用[移动与智慧屏应用授权码模式接入](https://developer.honor.com/cn/docs/11001/guides/android-pattern-authorization-code)，以获取用户授权。

账号接入您可参考账号服务中[示例代码](https://developer.honor.com/cn/docs/11001/samples/andorid-example)进行接入，同时使用 health kit demo时，需先实现 health kit demo 中 [Utils](app/src/main/java/com/hihonor/healthkitdemo/utils/Utils.java) 中的 getHonorSignInAccount() 方法，以方便 health kit demo 获取用户信息。

### 管理日常活动数据

日常活动数据包含：每分钟步数，每分钟活动距离，每分钟爬高，每分钟消耗卡路里和中高强度，每种数据对应数据类型如下：

- 每分钟步数：[DataType.SAMPLE_STEPS](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-activity-data/sample-step)
- 每分钟活动距离：[DataType.SAMPLE_DISTANCE](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-activity-data/sample-distance)
- 每分钟爬高：[DataType.SAMPLE_ALTITUDE](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-activity-data/sample-altitude)
- 每分钟消耗卡路里：[DataType.SAMPLE_CALORIES](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-activity-data/sample-calories)
- 中高强度：[DataType.SAMPLE_STRENGTH](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-activity-data/sample-strength)

日常活动数据支持读写操作， demo 中以插入每分钟步数和读取每分钟步数为例进行说明，您可参考 demo 代码中 [StepDailyFragment](app/src/main/java/com/hihonor/healthkitdemo/fragment/daily/StepDailyFragment.java) 中 insertStepData() 方法和 getEveryMinuteData() 方法学习如何插入和读取日常活动数据；

同时您也可结合荣耀开发者平台中运动健康服务模块接入指导中[管理日常活动记录](https://developer.honor.com/cn/docs/11005/guides/code-guide/android/dailyactivities)模块了解该如何进行代码编写。

------

### 管理统计数据

统计数据包含：步数统计，中高强度统计，距离统计，卡路里统计，爬高统计，心率统计，压力统计，血压统计，血氧统计和体温统计，每种数据对应数据类型如下：

- 步数统计：[DataType.SAMPLE_STEPS_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-steps-statistic)
- 中高强度统计：[DataType.SAMPLE_STRENGTH_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-strength-statistic)
- 距离统计：[DataType.SAMPLE_DISTANCE_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-distance-statistic)
- 卡路里统计：[DataType.SAMPLE_CALORIES_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-calories-statistic)
- 爬高统计：[DataType.SAMPLE_ALTITUDE_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-alititude-statistic)
- 心率统计：[DataType.SAMPLE_HEART_RATE_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-heart-rate-statistic)
- 压力统计：[DataType.SAMPLE_STRESS_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-stress-statistic)
- 血压统计：[DataType.SAMPLE_BLOOD_PRESSURE_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-blood-pressure-statistic)
- 血氧统计：[DataType.SAMPLE_BLOOD_OXYGEN_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-blood-oxygen-statistic)
- 体温统计：[DataType.SAMPLE_BODY_TEMPERATURE_STATISTIC](https://developer.honor.com/cn/docs/11005/guides/data-type/android/daily-statistics-data/sample-body-temperature-statistic)

统计数据仅支持读取数据不支持写入，demo 中以读取中高强度数据为例进行说明，您可参考 demo 代码中 [StrengthDailyFragment](app/src/main/java/com/hihonor/healthkitdemo/fragment/daily/StrengthDailyFragment.java) 中 getStrengthStatisticData() 方法学习如何读取统计数据；

同时您也可结合荣耀开发者平台中运动健康服务模块接入指导中[管理统计数据](https://developer.honor.com/cn/docs/11005/guides/code-guide/android/statisticdata)模块了解该如何进行代码编写。

------

### 管理健康采样数据

健康采样数据包含：心率数据、压力数据、血糖数据、血压数据、血氧数据、体温数据、最大摄氧量数据，每种数据对应数据类型如下：

- 心率数据：[DataType.SAMPLE_HEART_RATE](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-sample-data/sample-heart-rate)
- 压力数据：[DataType.SAMPLE_STRESS](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-sample-data/sample-stress)
- 血糖数据：[DataType.SAMPLE_BLOOD_SUGAR](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-sample-data/sample-blood-sugar)
- 血压数据：[DataType.SAMPLE_BLOOD_PRESSURE](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-sample-data/sample-blood-pressure)
- 血氧数据：[DataType.SAMPLE_BLOOD_OXYGEN](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-sample-data/sample-blood-oxygen)
- 体温数据：[DataType.SAMPLE_BODY_TEMPERATURE](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-sample-data/sample-body-temperature)
- 最大摄氧量数据：[DataType.SAMPLE_MAX_VO2](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-sample-data/healthkit-pulmonary)

健康采样数据支持读写操作，demo 中以读写心率数据为例进行说明，您可参考 demo 代码中 [HeartHealthActivity](app/src/main/java/com/hihonor/healthkitdemo/fragment/daily/HeartHealthActivity.java) 中 insertEveryMinuteData() 方法 和 getEveryHeartHealthData() 方法学习如何读写健康采样数据；

同时您也可结合荣耀开发者平台中运动健康服务模块接入指导中[管理健康采样数据](https://developer.honor.com/cn/docs/11005/guides/code-guide/android/healthsampling)模块了解该如何进行代码编写。

------

### 管理健康记录

健康记录包含：睡眠记录、心脏健康、女性生理周期数据，每种数据对应数据类型如下：

- 睡眠记录：[DataType. RECORD_SLEEP](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-record-data/record-sleep)
- 心脏健康：[DataType.RECORD_HEART_HEALTH](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-record-data/record-heart-health)
- 女性生理周期：[DataType. RECORD_MENSTRUAL_CYCLE_INFO](https://developer.honor.com/cn/docs/11005/guides/data-type/android/health-record-data/reproductive)

健康记录数据支持读写操作，demo 中以读写睡眠记录数据为例进行说明，您可参考 demo 代码中 [SleepActivity](app/src/main/java/com/hihonor/healthkitdemo/fragment/daily/SleepActivity.java) 中 insertEveryMinuteData() 方法 和 querySleepData() 方法学习如何读写健康采样数据；

同时您也可结合荣耀开发者平台中运动健康服务模块接入指导中[管理健康记录](https://developer.honor.com/cn/docs/11005/guides/code-guide/android/healthrecord)模块了解该如何进行代码编写。

------

### 管理运动记录

运动记录包含：户外跑步、室内跑步、步行、骑行、跳绳数据，每种数据对应数据类型如下：

户外跑步：[DataType. RECORD_RUNNING_OUTDOOR](https://developer.honor.com/cn/docs/11005/guides/data-type/android/sport-record-data/record-running-outdoor)

室内跑步：[DataType. RECORD_RUNNING_INDOOR](https://developer.honor.com/cn/docs/11005/guides/data-type/android/sport-record-data/record-running-indoor)

步行：[DataType. RECORD_WALKING]()

骑行：[DataType. RECORD_RIDING](https://developer.honor.com/cn/docs/11005/guides/data-type/android/sport-record-data/record-riding)

跳绳：[DataType. RECORD_JUMP_ROPE](https://developer.honor.com/cn/docs/11005/guides/data-type/android/sport-record-data/record-jump-rope)

运动记录数据支持读写操作，demo 中以读写户外跑步记录数据为例进行说明，您可参考 demo 代码中 [SportOutdoorHelper](app/src/main/java/com/hihonor/healthkitdemo/helper/SportOutdoorHelper.java) 中 insertData() 方法 和 queryOutdoorData() 方法学习如何读写运动记录数据；

同时您也可结合荣耀开发者平台中运动健康服务模块接入指导中[管理运动记录](https://developer.honor.com/cn/docs/11005/guides/code-guide/android/sportrecord)模块了解该如何进行代码编写。

------

### 订阅实时数据

实时数据包含：实时步数数据、实时心率数据；实时数据的读取采用订阅形式进行，对应订阅类型如下：

订阅心率：DataType.SAMPLE_HEART_RATE_REALTIME

订阅步数：DataType.SAMPLE_STEPS_REALTIME

订阅步数和心率：DataType.SAMPLE_HEART_RATE_AND_STEP

demo 中以读写户外跑步记录数据为例进行说明，您可参考 demo 代码中 [RealTimeHeartRateActivity](app/src/main/java/com/hihonor/healthkitdemo/activity/RealTimeHeartRateActivity.java) 中 registerHeart() 方法 和 unRegisterHeart() 方法学习如何订阅和取消订阅实时数据；

同时您也可结合荣耀开发者平台中运动健康服务模块接入指导中[读取实时数据](https://developer.honor.com/cn/docs/11005/guides/code-guide/android/realtimedata)模块了解该如何进行代码编写。

------

### 订阅目标数据

目标数据订阅是指用户达到指定的目标时，可以通过回调通知使用者，比如设置步数目标为500步，用户步数达到500步时回调通知使用方。同时可以甚至达到目标后通知一次；也可以设备每次达到目标都进行通过，即周期订阅，目前支持订阅的数据类型如下：

距离：DataType.SAMPLE_DISTANCE，距离周期订阅支持 500m、1000m、2000m、5000m

步数：DataType.SAMPLE_STEPS，步数周期订阅支持 500步、1000步、2000步、5000步

卡路里：DataType.SAMPLE_CALORIES，卡路里周期订阅支持 20千卡、50千卡、100千卡、200千卡

单次目标订阅：GoalRequest.SUBSCRIBE_TYPE_TARGET

周期目标订阅：GoalRequest.SUBSCRIBE_TYPE_CYCLE

您可参考 demo 代码中 [SubscribeActivity](app/src/main/java/com/hihonor/healthkitdemo/activity/SubscribeActivity.java) 中 registerGoal() 方法 和 unsubscribeGoal() 方法学习如何订阅和取消订阅目标数据；

同时您也可结合荣耀开发者平台中运动健康服务模块接入指导中[订阅数据](https://developer.honor.com/cn/docs/11005/guides/code-guide/android/goals)模块了解该如何进行代码编写。

## 安装
* 方法1：在 Android Studio 中进行代码的编译构建。构建 APK 完成后，将 APK 安装到手机上，并调试 APK。
* 方法2：在 Android Studio 中生成 APK。使用 ADB（Android Debug Bridge）工具通过
adb install {YourPath/YourApp.apk} 命令将 APK 安装到手机，并调试 APK。

## 技术支持
如果您对该示例代码还处于评估阶段，可在[荣耀开发者社区](https://gitee.com/link?target=https%3A%2F%2Fdeveloper.hihonor.com%2Fcn%2Fforum%2F%3Fnavation%3Ddh11614886576872095748%252F1)获取关于 Health Kit 的最新讯息，并与其他开发者交流见解。

如果您对使用该示例代码有疑问，请尝试：

* 开发过程遇到问题上[Stack Overflow](https://gitee.com/link?target=https%3A%2F%2Fstackoverflow.com%2Fquestions%2Ftagged%2Fhonor-developer-services%3Ftab%3DVotes)，在 honor-developer-services 标签下提问，
有荣耀研发专家在线一对一解决您的问题。

如果您在尝试示例代码中遇到问题，请向仓库提交[issue](https://gitee.com/link?target=https%3A%2F%2Fgithub.com%2FHONORDevelopers%2FHandover-demo%2Fissues)，也欢迎您提交[Pull Request](https://gitee.com/link?target=https%3A%2F%2Fgithub.com%2FHONORDevelopers%2FHandover-demo%2Fpulls)。

## 授权许可
该示例代码经过[Apache 2.0授权许可](https://gitee.com/link?target=http%3A%2F%2Fwww.apache.org%2Flicenses%2FLICENSE-2.0)。