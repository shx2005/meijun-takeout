#!/bin/bash
# 生成美俊外卖系统UML图的脚本

echo "开始生成UML图..."

# 检查images目录是否存在，不存在则创建
if [ ! -d "images" ]; then
  mkdir -p images
  echo "创建images目录"
fi

# 导出所有UML图到images目录
echo "导出顶层包图..."
plantuml -tpng top_level_package.puml -o images

echo "导出类结构图..."
plantuml -tpng class_structure.puml -o images

echo "导出接口关系图..."
plantuml -tpng interface_relationships.puml -o images

echo "导出活动流程图..."
plantuml -tpng activity_diagram.puml -o images

# 导出各界面接口关系图
echo "导出登录注册接口关系图..."
plantuml -tpng login_interface.puml -o images

echo "导出首页菜品接口关系图..."
plantuml -tpng index_interface.puml -o images

echo "导出购物车订单接口关系图..."
plantuml -tpng cart_order_interface.puml -o images

echo "导出订单管理接口关系图..."
plantuml -tpng order_interface.puml -o images

echo "导出个人中心接口关系图..."
plantuml -tpng my_interface.puml -o images

echo "导出地址管理接口关系图..."
plantuml -tpng address_interface.puml -o images

echo "导出优惠券支付接口关系图..."
plantuml -tpng coupon_payment_interface.puml -o images

# 检查是否成功生成
if [ $? -eq 0 ]; then
  echo "所有UML图已成功生成到images目录"
  ls -la images
else
  echo "生成UML图时发生错误，请检查PlantUML语法"
fi

echo "完成!" 