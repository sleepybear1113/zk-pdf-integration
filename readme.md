# 自考花名册重排列工具
## 1. 介绍
这是一个用于重排列自考花名册的工具，可以将自考花名册中的学生信息按照指定的顺序重新排列，以便于后续的处理和打印。

本工具可以做到将类似如下格式的花名册 PDF 文件根据专业重复打印整合

--【学前教育-第1页，学前教育-第2页，学前教育-第3页】，【土木工程-第1页】，【财务管理-第1页，财务管理-第2页】，……

整合为

--【学前教育-第1页，学前教育-第2页，学前教育-第3页】，【学前教育-第1页，学前教育-第2页，学前教育-第3页】，【学前教育-第1页，学前教育-第2页，学前教育-第3页】，【土木工程-第1页，土木工程-第1页，土木工程-第1页】，【财务管理-第1页，财务管理-第2页，财务管理-第1页，财务管理-第2页，财务管理-第1页，财务管理-第2页】，……

## 2. 部署
### 2.1 技术栈
Spring Boot 3.x + HTML + JavaScript
### 2.2 环境要求
Java 21
### 2.3 部署步骤
从 [release](https://github.com/sleepybear1113/zk-pdf-integration/releases) 中下载最新的 jar 包，然后执行如下命令：
```shell
java -jar zk-pdf-integration-版本号.jar
```