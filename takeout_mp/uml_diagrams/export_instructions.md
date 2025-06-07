# 美俊外卖系统 UML 图导出说明

本文件夹包含了美俊外卖系统的四个主要UML图，使用PlantUML语法编写，文件格式为`.puml`。您可以通过以下步骤将这些文件导出为图片格式。

## 文件列表

1. `top_level_package.puml` - 系统顶层包图
2. `class_structure.puml` - 类结构图
3. `interface_relationships.puml` - 接口关系图
4. `activity_diagram.puml` - 活动流程图

## 导出方法

### 方法一：使用在线PlantUML服务

1. 访问在线PlantUML编辑器，如：http://www.plantuml.com/plantuml/uml/
2. 复制`.puml`文件的内容到编辑器中
3. 点击"Submit"按钮生成图像
4. 右键单击生成的图像，选择"保存图片"或"另存为"

### 方法二：使用本地PlantUML工具

1. 安装PlantUML（需要Java环境）
   ```
   # MacOS
   brew install plantuml
   
   # Ubuntu/Debian
   apt-get install plantuml
   
   # Windows
   # 下载并安装：https://plantuml.com/download
   ```

2. 使用命令行导出图像
   ```
   plantuml top_level_package.puml
   plantuml class_structure.puml
   plantuml interface_relationships.puml
   plantuml activity_diagram.puml
   ```

3. 或使用批处理命令导出所有图像
   ```
   plantuml *.puml
   ```

### 方法三：使用IDE插件

1. 如果您使用VSCode，可以安装"PlantUML"插件
2. 打开`.puml`文件，右键单击，选择"Export Current Diagram"
3. 选择导出格式（PNG、SVG、PDF等）

## 导出格式建议

- PNG：适合在文档中使用，文件小，清晰度好
- SVG：适合需要缩放的场景，矢量格式，无损放大
- PDF：适合打印和专业出版

## 图片存放位置

导出的图片建议存放在以下位置，便于在文档中引用：

```
takeout_mp/
  ├── uml_diagrams/
  │   ├── images/
  │   │   ├── top_level_package.png
  │   │   ├── class_structure.png
  │   │   ├── interface_relationships.png
  │   │   └── activity_diagram.png
  │   ├── top_level_package.puml
  │   ├── class_structure.puml
  │   ├── interface_relationships.puml
  │   └── activity_diagram.puml
  └── version2.md
```

创建images文件夹的命令：
```
mkdir -p takeout_mp/uml_diagrams/images
``` 