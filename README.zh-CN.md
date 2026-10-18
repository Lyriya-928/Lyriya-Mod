# Lyriya Mod

Lyriya Mod 是面向技术生存、生电和红石工程的 Fabric 客户端实用工具集合。许多功能参考或适配自其他开源项目；本项目不是这些功能的唯一原作者，也不要求安装 Meteor Client 或 Meteor Lotus。

> **早期 Demo：** 当前版本仍在早期开发阶段。部分功能尚未经过本项目维护者的完整游戏内验证，已知 Auto Helmet 可能无法正常触发；NoRender、Freecam、自动合成等功能也需要按实际场景继续验证。功能实现状态和已知边界见下文及 `docs/`。

## 安装

使用 Minecraft 26.3、Fabric Loader 0.19.5+、Fabric API 0.161.0+26.3 和 Java 25+。将以下模组分别放入实例的 `mods` 目录：

- **MaLiLib Fabric 26.3-0.30.2**（必需）：从 [Modrinth](https://modrinth.com/mod/malilib/version/0.30.2) 或 [CurseForge](https://www.curseforge.com/minecraft/mc-mods/malilib/files/9100218) 获取 `malilib-fabric-26.3-0.30.2.jar`。MaLiLib 是独立的 LGPL-3.0-only 依赖，不包含在 Lyriya JAR 中。
- **Lyriya Mod 0.1.2**：构建文件为 `build/libs/lyriya-mod-0.1.2.jar`。

Fabric 元数据将 `malilib >= 0.30.2` 声明为必需运行时依赖。不要同时安装 Meteor Client 或 Lotus 来满足 Lyriya 的依赖。

## 配置

Lyriya 使用 MaLiLib 配置面板、选项组件、按键绑定和 JSON 持久化。默认按 **反斜杠键 `\`** 打开配置面板，可在 **General → Open configuration** 修改。配置保存在 `config/lyriya-mod.json`。原有设置会保留；旧的 `L + Y` 默认面板绑定会迁移为反斜杠，其他玩家自定义绑定不变。

General 中的 **Toggle Notifications** 默认开启。功能被手动切换时，提示显示在 HUD 覆盖消息位置，不写入聊天；配置加载时不会逐项弹出提示。Debug Logging 和 Debug Messages 默认关闭；调试消息只在 Auto Crafting 没有白名单目标或没有材料充足的配方时最多提示一次。

## 功能状态

以下描述表示代码中存在对应实现，不代表本轮经过游戏内验证。Entity List 和 Portals 曾由用户在此前版本中初步测试正常；本轮未启动客户端。

- **Auto Helmet**（`H`）：保留原有猪灵附近装备金头盔逻辑。本轮冻结，用户此前反馈可能不触发；没有修改其检测、触发、背包交互或装备逻辑。
- **Entity List**（`J`）：统计客户端当前实体数据中的实体并保留类别过滤。现在可设置左右/上下锚点、边距、X/Y 偏移、缩放、最大行数、行距、阴影，以及掉落物、玩家、敌对/被动生物和其他实体的颜色。统计不覆盖未加载区块或服务器全部实体。
- **Portals**（`P`）：保留传送门视觉效果期间继续使用受支持界面的行为。
- **GUI Move**（`G`）：默认只允许库存/容器界面；可选其他符合条件的界面、跳跃、潜行和疾跑。文本输入及 Lyriya/MaLiLib 配置界面排除。
- **Auto Totem**（`O`）：按健康值/Elytra 或始终模式，将背包图腾切换至副手。
- **SafeWalk+**（`K`）：靠近前方无支撑方块边缘时，向玩家输入加入真实潜行状态；提供固定距离与随机区间模式。模块不锁定位置、不修改移动速度或坐标。跳跃时不会强制潜行。
- **Auto Crafting**：点击 **Add items** 后，在 MaLiLib 弹出的物品选择列表内部使用搜索框，按本地化名称或物品注册 ID 实时筛选白名单候选。支持打开工作台自动执行或快捷键启动；**C** 保留产物并遵循 Lotus 的至少一个玩家背包空槽要求，**V** 丢弃产物且不要求收纳空槽。两种方式都优先处理当前结果，否则从可合成白名单配方中选一个目标，等待配方和槽位同步后继续执行。C/V 可在 MaLiLib 中修改。
- **Fullbright**（`F7`）：在 Gamma（覆盖光照贴图，不改玩家 Gamma 选项）、Potion（本地夜视效果，默认目标持续 120 秒）和 Luminance（Block/Sky 最低光照等级）三种模式中选择。Potion 模式不会主动替换检测到的其他来源夜视效果。
- **NoRender**（`N`）：当前已实现参考版本中的覆盖层、HUD、雾效、天气/世界边界/破坏进度、信标/附魔书/告示牌/坠落方块、地图、烟花、刷怪笼、盔甲、实体可见性/发光、进食粒子、附魔光效、文字混淆、签名标记、屏障显示、方块随机旋转，以及可选择的实体、方块实体和粒子列表。三类列表通过本地化/可读名称选择并持久化。默认关闭的 **Drop Spawn Packets** 会丢弃匹配实体的生成数据包，使其不进入客户端世界；这会影响客户端实体可用性，不只是视觉效果。Cave Culling 和 Banner Render Mode 尚未显示为选项，原因见 [NoRender 兼容性表](docs/NORENDER_COMPATIBILITY.md)。
- **Freecam**（`F8`）：WASD 和跳跃由自由相机消费；原版潜行键仍正常进入玩家潜行输入，并同时用于相机下降，不再需要额外的 X 绑定。可单独设置显示玩家本体（默认关闭）和显示手部。相机位置与方向逐帧插值，对角线移动归一化。自由视角只改变客户端相机，不改变服务端玩家位置。
- **No Ghost Blocks**（`F9`）：尝试减少受支持方块破坏/放置操作中的客户端预测；不保证修复所有服务端同步问题。

NoRender 的选项按 Overlay、HUD、World、Entity 分类；并非参考项目中的全部选项都已实现或开放配置。各项兼容状态、目标版本钩子和行为边界见 [NoRender 兼容性表](docs/NORENDER_COMPATIBILITY.md)。功能列表表示代码实现状态，不代表均已通过本项目的游戏内验证。

## 上游项目与许可

Lyriya Mod 采用 **GPL-3.0-only**，完整条款见 [LICENSE](LICENSE)。项目从以下开源项目研究、参考或适配功能；详细提交、文件范围和许可证记录见 [docs/UPSTREAM_SOURCES.md](docs/UPSTREAM_SOURCES.md)。

- [Meteor Client](https://github.com/MeteorDevelopment/meteor-client)，Meteor Development，GPL-3.0。部分功能以其行为为参考；少量 No Ghost Blocks 和 Banner 渲染逻辑有源码适配，并在对应源码文件中保留版权及修改说明。
- [Meteor Lotus](https://github.com/xiaohe66/xiaohe66-meteor-lotus)，GPL-3.0。用于研究若干功能；现有来源记录称没有复制其源码。
- [MaLiLib](https://github.com/maruohon/malilib) 及其 [Minecraft 26.3 社区构建来源](https://github.com/sakura-ryoko/malilib)，LGPL-3.0。它是单独安装的运行时依赖，不包含在 Lyriya JAR 中。

如果未来继续移植上游代码，需要保留相应版权和许可证声明，并在来源记录中注明修改范围。

## 构建

使用 JDK 25 或更新版本：

```powershell
.\gradlew.bat build --offline
```

依赖已解析并缓存后可离线构建；首次解析 MaLiLib 需要联网。输出目录为 `build/libs/`。构建和静态检查结果见 [docs/TESTING.md](docs/TESTING.md)，上游来源及许可证范围见 [docs/UPSTREAM_SOURCES.md](docs/UPSTREAM_SOURCES.md)。

## 许可证

Lyriya Mod 使用 GPL-3.0-only；MaLiLib 作为独立 LGPL-3.0-only 依赖。许可证与移植范围记录见 [docs/UPSTREAM_SOURCES.md](docs/UPSTREAM_SOURCES.md)。
