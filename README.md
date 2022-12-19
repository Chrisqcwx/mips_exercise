# Chisel Project Template (Beta)

## 概览

该模版主要用于提供一个方便统一的开发环境，同时建立一个 Chisel 项目的标准模版。

## 使用方式

### 前置条件

请确保在开发系统上安装了 Visual Studio Code 和 Docker，并且在 Visual Studio Code 中安装了 Dev Containers 插件。

### 配置容器

Visual Studio Code 会提醒识别到一个 Dev Container 配置，并询问是否要使用这个容器配置。如果是第一次使用这个容器，会自动进行容器的搭建，需要花一点时间。

详细操作请参考：<https://code.visualstudio.com/docs/devcontainers/containers#_quick-start-open-an-existing-folder-in-a-container>

### 安装 Chipyard（可选）

在终端中运行项目目录下的脚本 `./chipyard-bootstrap.sh`，然后使用 `source chipyard-env` 激活环境。

## 测试

在项目目录下运行 `sbt test`，若最终显示 `All tests passed.` 则说明模版项目可用。

## 贡献

这个项目模版并不完美，欢迎改进模版、容器环境和本文档。