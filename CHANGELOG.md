# GPDroid-NFC 改动说明文档

## 版本 1.2.0 (2026-06-10) - 第二轮优化

### 概述
本次更新重点优化了Applet管理UI体验，新增批量操作功能和默认密码处理，大幅提升用户使用便捷性。

---

## 🎯 新增功能一览

### 1. 删除功能UI优化 ✅ **新增**
#### 每个Applet列表项直接显示删除按钮
- 列表项右侧添加 🗑️ 删除图标按钮
- 点击按钮直接弹出删除确认对话框
- 无需进入详情页即可快速删除
- 安全域自动隐藏删除按钮（灰色显示）

**使用方式**：
1. 点击 "List Applets" 读卡
2. 每个Applet右侧有删除按钮
3. 点击按钮 → 确认 → 直接删除

---

### 2. 批量Applet管理功能 ✅ **新增**
#### 独立的批量操作模式
- **批量模式切换**：一键切换普通/批量模式
- **全选按钮**：一键选中所有可删除Applet
- **反选按钮**：一键清除所有选择
- **批量删除**：一次性删除所有选中项
- **实时计数**：状态栏显示已选择数量
- **安全域保护**：安全域禁止选择和删除

**使用方式**：
1. 点击顶部 "批量模式" 按钮
2. 勾选需要删除的Applet（或点击"全选"）
3. 点击 "批量删除" 确认操作
4. 操作完成自动退出批量模式

---

### 3. 默认密码处理功能 ✅ **新增**
#### GP认证密码一键填充
- **默认密码按钮**：一键填充标准GP密钥 `0000000000000000`
- **密码显示/隐藏切换**：复选框控制密码可见性
- **自动填充默认值**：
  - ID: 0
  - Version: 1
  - Name: GP Default
- **密码输入框默认隐藏**：保护密钥安全

**使用方式**：
1. 进入添加密钥页面
2. 点击 "填充默认密码" 按钮
3. 勾选 "显示密码" 可查看密钥内容
4. 确认保存即可

---

## 📁 第二轮修改的文件列表

### 🔧 修改文件

#### 1. `AppletListActivity.java` ✅ **重大修改**
**增强版Applet管理Activity**
- 重写整个列表实现，使用自定义 `AppletListAdapter`
- 新增批量模式切换功能 `toggleBatchMode()`
- 新增全选/反选功能
- 新增批量删除 `batchDeleteSelected()`（AsyncTask异步执行）
- 新增单条删除 `deleteSingleApplet()` 带确认对话框
- 自定义ViewHolder优化列表性能
- 每个列表项显示：AID Hex、AID可读、Kind、LifeCycle、Privileges
- 安全域灰色显示，禁止删除和选择

---

#### 2. `AddKeysetActivity.java` ✅ **修改**
**密码处理增强**
- 新增 `填充默认密码` 按钮点击事件
- 新增 `显示密码` 复选框切换功能
- 页面打开自动填充默认值（ID=0, Version=1, Name=GP Default）
- 密码输入框默认隐藏显示

---

#### 3. `activity_add_keyset.xml` ✅ **修改**
**密钥添加页面布局**
- 新增 "填充默认密码" 按钮（蓝色高亮）
- 新增 "显示密码" 复选框
- 所有密钥输入框默认 `inputType="textPassword"`

---

### ✨ 新增文件

#### 4. `app/src/main/res/layout/applet_list_item.xml` ✅ **新增**
**增强版列表项布局**
- 左侧：CheckBox（批量模式显示）
- 中间三行信息：AID Hex、AID可读、Applet详情
- 右侧：删除按钮（垃圾桶图标）
- 点击反馈效果

---

#### 5. `app/src/main/res/layout/applet_list_enhanced.xml` ✅ **新增**
**增强版Applet管理页面**
- 顶部操作栏：全选、反选、批量删除、批量模式切换
- 中间ListView显示Applet列表
- 底部状态栏显示提示信息

---

## 📋 完整文件变更总结表

| 版本 | 类型 | 文件路径 | 变更内容 |
|------|------|---------|---------|
| v1.1 | 🔧 修改 | `app/build.gradle` | SDK 30→33, AndroidX依赖 |
| v1.1 | 🔧 修改 | `build.gradle` | AGP 8.13.2→8.1.2 |
| v1.1 | 🔧 修改 | `gradle/wrapper/gradle-wrapper.properties` | Gradle 8.13→8.0 |
| v1.1 | 🔧 修改 | `app/src/main/AndroidManifest.xml` | 权限+exported+NFC过滤 |
| v1.1 | ✨ 新增 | `app/src/main/res/xml/nfc_tech_filter.xml` | NFC技术过滤 |
| v1.1 | ✨ 新增 | `.github/workflows/build.yml` | CI在线构建 |
| v1.2 | 🔧 修改 | `AppletListActivity.java` | 批量删除+UI优化 |
| v1.2 | 🔧 修改 | `AddKeysetActivity.java` | 默认密码+显示隐藏 |
| v1.2 | 🔧 修改 | `activity_add_keyset.xml` | 密码UI增强 |
| v1.2 | ✨ 新增 | `app/src/main/res/layout/applet_list_item.xml` | 列表项布局 |
| v1.2 | ✨ 新增 | `app/src/main/res/layout/applet_list_enhanced.xml` | 批量管理布局 |
| v1.1 | ✨ 新增 | `CHANGELOG.md` | 改动说明文档 |
| v1.1 | ✨ 新增 | `BUILD_GUIDE.md` | 构建指南 |

---

## 🎯 功能使用详细说明

### 普通模式（默认）
1. 点击 "List Applets" 读卡显示所有Applet
2. 每个Applet右侧有 🗑️ 删除按钮
3. 点击删除按钮弹出确认对话框
4. 点击Applet项查看详情
5. 安全域灰色显示，无法删除

### 批量模式
1. 点击 "批量模式" 按钮进入
2. 每个Applet左侧出现CheckBox
3. 可勾选多个Applet
4. 点击 "全选" 选中所有可删除Applet
5. 点击 "反选" 清除所有选择
6. 点击 "批量删除" 一次性删除所有选中项
7. 点击 "退出批量" 返回普通模式

### 默认密码使用
1. 点击 "Add Keyset" 进入密钥添加页面
2. 点击 "填充默认密码" 按钮
3. MAC/ENC/KEK自动填充为 `0000000000000000`
4. 勾选 "显示密码" 可查看明文
5. 点击保存完成

---

## 🔍 所有改动标记规范

**所有修改文件统一使用以下标记格式：**
```
// ==================== MODIFIED: 改动说明 ====================
... 修改内容 ...
// ==================== END MODIFICATION ====================
```

**新增文件使用：**
```
// ==================== ADDED: 新增说明 ====================
... 新增内容 ...
// ==================== END ADDITION ====================
```

---

## 📱 Android 13+ 兼容性验证

✅ 所有新增功能均已适配Android 13+：
- 使用AndroidX组件
- 目标SDK 33
- 所有Activity已添加 `android:exported`
- 运行时权限处理正确

---

## 🚀 GitHub Actions 配置保持可用

✅ 原有CI工作流无需修改，支持：
- 自动检测所有新增Java文件
- 自动编译所有新增布局资源
- 构建产物包含所有第二轮优化功能

---

## ⚠️ 重要注意事项

1. **批量删除**：操作不可逆，请谨慎使用
2. **安全域**：ISD安全域禁止删除，UI已做禁用处理
3. **默认密码**：仅适用于标准GP卡，部分厂商卡使用自定义密钥
4. **异步操作**：批量删除在后台执行，请勿中途移开卡片

---

## 📌 关键代码位置

### 核心Java文件路径
- **增强版列表**：`app/src/main/java/at/fhooe/usmile/gpjshell/AppletListActivity.java`
- **密码处理**：`app/src/main/java/at/fhooe/usmile/gpjshell/AddKeysetActivity.java`
- **布局文件**：`app/src/main/res/layout/`

---

## 版本历史

- **v1.2.0** (当前) - UI优化、批量操作、默认密码
- **v1.1.0** - Android 13+适配、GitHub Actions、基础功能完善
- **v1.0.0** - 原始版本
