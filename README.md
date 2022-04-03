# code-generator
IDEA 插件，提供了各种方便的代码生成工具

请在 IDEA Marketplace 搜索 Roc Code 进行安装
https://plugins.jetbrains.com/plugin/18692-roc-code

用于提升编码效率，主要提供了根据 Java 接口方法或者 controller 方法生成 markdown Doc 的能力

主要提供了如下特性：
1. 实现了基于 Java 接口方法或者 Controller 方法，生成 Markdown 文档
2. 实现了 Mockito、Powermock 单元测试模板生成
3. 实现了根据 Java 类，生成 JSON 示例数据
4. 实现了根据 Java 字段，生成静态常量字段
5. 可以根据字段类型的注释，自动生成字段注释

其他说明：
1. Markdown 文档的模板支持配置，配置项："Preferences | Editor | File and Code Templates | Other | Roc Code"，
   如果提供的参数无法满足需求，可以<a href="https://github.com/lcxdever/code-generator/issues">联系作者</a>添加
2. Markdown 文档中的示例数据，支持配置字段的默认值，选择"Preferences | Other Settings | Roc Code | Markdown Doc"
3. 上面的配置同样应用到 JSON 示例数据的生成
4. 支持根据注解信息：@Length @Size @Range @Min @Max 在文档中自动追加描述信息

用法：
1. Markdown文档：打开接口或者 Controller 类，将光标移动到需要生成文档的方法，右键选择"Generate..." -> "Markdown Doc"
2. 单元测试：打开待测试的类，右键选择"Generate..." -> "Unit Test"
3. 示例数据：打开类，将光标移动到需要生成 JSON 示例数据的类，右键选择"Generate..." -> "JSON"
4. 常量字段：打开类，将光标移动到需要生成常量字段的类，右键选择"Generate..." -> "Constants Field"
5. 生成注释：选择字段，按下"Alt + Enter"，并选择 "Add comment to the field"

更多说明及动图示例请参照：
https://plugins.jetbrains.com/plugin/18692-roc-code
