# 自考花名册重排列工具

## 1. 介绍
这是一个用于重排列自考花名册的工具，可以将自考花名册中的学生信息按照指定的顺序重新排列，以便于后续的处理和打印。

### 1.1 功能特点
- 支持 PDF 格式的花名册文件处理
- 根据专业自动识别并重复打印整合
- 简单易用的 Web 界面操作
- 支持在花名册中添加签名图片

### 1.2 使用场景
本工具可以将类似如下格式的花名册 PDF 文件根据专业重复打印整合：

原始文件格式：
- 【学前教育-第1页，学前教育-第2页，学前教育-第3页】
- 【土木工程-第1页】
- 【财务管理-第1页，财务管理-第2页】
...

整合后格式：
- 【学前教育-第1页，学前教育-第2页，学前教育-第3页】×3
- 【土木工程-第1页】×3
- 【财务管理-第1页，财务管理-第2页】×3
...

## 2. 部署指南

### 2.1 技术栈
- 后端：Java 21 + Spring Boot 3.x
- 前端：HTML + JavaScript

### 2.2 部署步骤
1. 从 [release](https://github.com/sleepybear1113/zk-pdf-integration/releases) 页面下载最新版本的 jar 包
2. 在命令行中执行：
```shell
java -jar zk-pdf-integration-版本号.jar
```
3. 也可以通过以下命令指定端口号（默认为 21088）：
```shell
java -jar zk-pdf-integration-版本号.jar --server.port=自定义端口号
```

### 2.3 访问方式
- 默认访问地址：http://localhost:21088
- 如果修改了端口号，请相应更改访问地址中的端口号

## 3. 使用说明

### 3.1 基本操作流程
1. 打开工具网页界面
2. 上传需要处理的 PDF 文件
3. 设置重复次数（如需要）
4. 点击处理按钮
5. 下载处理完成的文件

### 3.2 注意事项
- 上传文件大小限制：100MB
- 支持的文件格式：PDF
- 处理过程中请勿关闭浏览器
