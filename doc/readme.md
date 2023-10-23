# 找不到core-upload.jar

修改maven配置文件settings.xml

1.0.0版本：
```
<mirror>
    <id>q3z3-boot-tools-temp</id>
    <name>temp</name>
    <mirrorOf>q3z3-boot-tools-temp</mirrorOf>
    <url>https://q3z3-maven.pkg.coding.net/repository/boot-tools/temp/</url>
</mirror>
```

1.1.0版本：
```
<mirror>
    <id>q3z3-boot-tools-maven</id>
    <name>maven</name>
    <mirrorOf>q3z3-boot-tools-maven</mirrorOf>
    <url>https://q3z3-maven.pkg.coding.net/repository/boot-tools/maven/</url>
</mirror>
```

完整版：

```
<mirrors>
    <mirror>
        <id>nexus-aliyun</id>
        <mirrorOf>*</mirrorOf>
        <name>Nexus aliyun</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public</url>
    </mirror>

    <mirror>
        <id>aliyunmaven</id>
        <mirrorOf>*</mirrorOf>
        <name>阿里云spring插件仓库</name>
        <url>https://maven.aliyun.com/repository/spring-plugin</url>
    </mirror>

    <mirror>
        <id>aliyunmaven</id>
        <mirrorOf>*</mirrorOf>
        <name>阿里云公共仓库</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>

    <mirror>
        <id>repo2</id>
        <name>Mirror from Maven Repo2</name>
        <url>https://repo.spring.io/plugins-release/</url>
        <mirrorOf>*</mirrorOf>
    </mirror>

    <mirror>
        <id>q3z3-boot-tools-temp</id>
        <name>temp</name>
        <mirrorOf>q3z3-boot-tools-temp</mirrorOf>
        <url>https://q3z3-maven.pkg.coding.net/repository/boot-tools/temp/</url>
    </mirror>
    
    <mirror>
        <id>q3z3-boot-tools-maven</id>
        <name>maven</name>
        <mirrorOf>q3z3-boot-tools-maven</mirrorOf>
        <url>https://q3z3-maven.pkg.coding.net/repository/boot-tools/maven/</url>
    </mirror>
</mirrors>
```