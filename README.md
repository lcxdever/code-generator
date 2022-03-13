# code-generator
IDEA 插件，提供了各种方便的代码生成工具

请在 IDEA Marketplace 搜索 Roc Code 进行安装
https://plugins.jetbrains.com/plugin/18692-roc-code

主要提供了如下特性：
1. 实现了 Mockito、Powermock 单元测试模板生成
2. 实现了根据接口方法，生成 Markdown 文档
3. 实现了根据 Controller 方法，生成 Markdown 文档
4. 实现了根据 Java 类，生成 JSON 示例数据
5. 实现了根据 Java 字段，生成静态常量字段

其他说明：
1. Markdown 文档中的示例数据生成，支持配置，选择"Preferences" -> "Roc Code" -> "Markdown Doc"，可以指定字段的默认值
2. 上面的配置同样应用到 JSON 示例数据的生成
3. 支持根据注解信息：@Length @Size @Range @Min @Max 在文档中自动追加描述信息

更多说明及动图示例请参照：
https://plugins.jetbrains.com/plugin/18692-roc-code
