##国内版本打包

##1,修改gradle.properties中CHINA_ENABLE = true

##2,同步gradle

####2.1修改MerculetSDK的module:

java,xml和proguard-rules.pro文件内"io.merculet.uav.sdk"为 "cn.magicwindow.uav.sdk"

####2.2修改Codec的module:

CMakeLists.txt文件中"merculet",为"magicwindow"; 

.cpp中"io_merculet_uav_sdk"为"cn_magicwindow_uav_sdk"

####2.3修改app的module:

java文件内"io.merculet.uav.sdk"为 "cn.magicwindow.uav.sdk"

##3,terminal执行

```
./gradlew replacePackageName replaceCppName replaceStringName
```

####3,1修改MerculetSDK中目录结构io/merculet/uav/sdk为cn/magicwindow/uav/sdk

####3.2修改Codec中merculet-lib.cpp为magicwindow-lib.cpp

## 4,打包so文件

```
./gradlew clean deleteOldSo exportSo
```

会经常失败,需要手动执行gradle命令中deleteOldSo和exportSo,将so包导入MerculetSDK中libs下

##5,打包jar文件

```
./gradlew clean deleteOldJar exportJar
```

将jar包导入build/output/中

##6,发布jcenter

```
//国内
userOrg = "magicwindow"
groupId = "cn.magicwindow"
artifactId = "uav-sdk"
publishVersion = '1.0.0'
desc = "magicwindow SDK"
website = "http://open.mbc.magicwindow.cn"
```

```
./gradlew bintrayUpload -PbintrayUser=magicwindow -PbintrayKey=6d1918f7aeb3992c0d051ab9388801acefde9aa1 -PdryRun=false
```



