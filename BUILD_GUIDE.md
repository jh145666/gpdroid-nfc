# GPDroid-NFC 无电脑在线编译指南

## 📱 纯手机端 GitHub 在线编译教程

### 前置条件
- 拥有 GitHub 账号
- 手机浏览器（推荐 Chrome / Edge）
- 无需电脑，无需 Android Studio

---

## 步骤一：Fork 项目到你的仓库

1. **打开项目地址**
   ```
   https://github.com/dingchaojie/gpdroid-nfc
   ```

2. **点击 Fork**
   - 点击页面右上角的 **Fork** 按钮
   - 选择你的个人账号
   - 点击 **Create fork**

3. **等待 Fork 完成**
   - 页面会自动跳转到你的 Fork 仓库

---

## 步骤二：启用 GitHub Actions

1. **进入 Actions 页面**
   - 点击顶部导航栏的 **Actions** 标签

2. **启用工作流**
   - 点击 **"I understand my workflows, go ahead and enable them"**
   - Actions 功能已启用

---

## 步骤三：触发 APK 构建

### 方式 A：自动构建（推荐）
直接修改并提交任意文件即可自动触发构建：
1. 点击任意文件（如 `README.md`）
2. 点击 ✏️ **Edit this file**
3. 随便修改一点内容
4. 滚动到底部，点击 **Commit changes**

### 方式 B：手动触发构建
1. 点击 **Actions** 标签
2. 左侧选择 **"Build GPDroid-NFC APK"**
3. 点击 **Run workflow** 按钮
4. 选择分支（main）
5. 再次点击 **Run workflow**

---

## 步骤四：下载编译好的 APK

1. **等待构建完成**
   - 构建大约需要 **2-3 分钟**
   - 黄色圆圈 = 构建中
   - 绿色对勾 = 构建成功
   - 红色叉号 = 构建失败（查看日志）

2. **进入构建详情**
   - 点击构建记录（标题为 commit 信息）

3. **下载 APK**
   - 滚动到页面底部 **Artifacts** 区域
   - 点击 **GPDroid-NFC-Debug** 下载 Debug 版本
   - （可选）点击 **GPDroid-NFC-Release** 下载 Release 版本

4. **安装 APK**
   - 下载完成后点击安装
   - 允许浏览器安装未知来源应用

---

## 📦 构建产物说明

| 产物名称 | 说明 | 安装方式 |
|---------|------|---------|
| **GPDroid-NFC-Debug** | Debug 签名 APK | 直接安装 |
| **GPDroid-NFC-Release** | 未签名 Release APK | 需要自行签名后安装 |

---

## ❌ 常见问题

### Q: 构建失败怎么办？
**A:** 点击构建记录 → 点击 **build** → 查看详细日志，常见原因：
- Gradle 缓存问题 → 重新触发构建
- Java 版本不匹配 → 已配置 JDK 17，无需修改
- 网络问题 → 重新触发

### Q: Release APK 安装失败？
**A:** Release APK 未签名，需要：
1. 使用 Debug 版本（推荐）
2. 或使用 APK 签名工具自行签名

### Q: Android 13+ 安装后闪退？
**A:** 已适配 Android 13+，请检查：
- 授予 NFC 权限
- 授予存储权限
- 授予通知权限

---

## 🔧 功能使用说明

### Applet 安装
1. 打开 APP
2. 点击 **"Select CAP File"** 选择本地 .cap 文件
3. 将智能卡贴近手机 NFC 区域
4. 点击 **"Install Applet"**
5. 等待安装完成

### Applet 删除
1. 点击 **"List Applets"** 读卡
2. 点击要删除的 Applet
3. 点击 **"Delete"** 确认删除

### 默认启动 Applet
- 安装后第一个 Applet 自动成为默认选中
- 列表中点击即设为当前操作对象

---

## 📝 版本信息

- **版本**: 1.1.0
- **minSdk**: 21 (Android 5.0)
- **targetSdk**: 33 (Android 13)
- **compileSdk**: 33
- **Gradle**: 8.0
- **AGP**: 8.1.2
- **JDK**: 17

---

## ✅ 支持的 Android 版本

| Android 版本 | 支持状态 |
|-------------|---------|
| Android 5.0 - 12 | ✅ 完全支持 |
| Android 13 | ✅ 完全支持 |
| Android 14 | ✅ 兼容支持 |
