---
layout: center
class: text-center
---

# 🤖 AI 时代的 TDD

<div class="text-2xl mt-8 text-gray-400">
AI 在有测试的代码上表现更好
</div>

---

# AI + 测试 = 更好的效果

<div class="grid grid-cols-3 gap-6 mt-10">

<v-click>
<div class="p-5 bg-red-500/20 rounded-xl border border-red-500/40 text-center">
  <div class="text-4xl mb-3">😨</div>
  <div class="font-bold text-red-300">无测试的代码</div>
  <div class="text-sm mt-2 opacity-70">AI 容易产生幻觉<br/>难以验证正确性</div>
</div>
</v-click>

<v-click>
<div class="p-5 bg-yellow-500/20 rounded-xl border border-yellow-500/40 text-center">
  <div class="text-4xl mb-3">😐</div>
  <div class="font-bold text-yellow-300">先代码后测试</div>
  <div class="text-sm mt-2 opacity-70">测试只验证已有行为<br/>失去设计驱动优势</div>
</div>
</v-click>

<v-click>
<div class="p-5 bg-green-500/20 rounded-xl border border-green-500/40 text-center">
  <div class="text-4xl mb-3">😀</div>
  <div class="font-bold text-green-300">先测试后代码</div>
  <div class="text-sm mt-2 opacity-70">测试定义期望行为<br/>AI 实现有明确目标</div>
</div>
</v-click>

</div>

---

# AI 应用经典 TDD 的挑战 😅

<div class="grid grid-cols-2 gap-6 mt-8">

<v-click>
<div class="p-4 bg-orange-500/20 rounded-lg">

### 📝 一次写完所有测试
而不是一个一个来

</div>
</v-click>

<v-click>
<div class="p-4 bg-orange-500/20 rounded-lg">

### ❌ 不运行测试观察失败
甚至不确保失败原因正确

</div>
</v-click>

<v-click>
<div class="p-4 bg-orange-500/20 rounded-lg">

### 🚀 倾向于写完整实现
即使测试还不需要那么多

</div>
</v-click>

<v-click>
<div class="p-4 bg-orange-500/20 rounded-lg">

### 🔧 倾向于跳过重构
只是不断添加代码

</div>
</v-click>

</div>

<v-click>

<div class="mt-6 p-4 bg-yellow-500/20 rounded-lg text-center">

💡 我们有一辆法拉利，却用它来拉马车？

</div>

</v-click>

---

# AI TDD 工作流

<v-clicks>

<div class="space-y-5 mt-8">

<div class="p-4 bg-blue-500/20 rounded-lg flex items-center gap-4">
  <div class="text-2xl font-mono bg-blue-500/30 px-3 py-1 rounded">1</div>
  <div>
    <div class="font-bold">Prompt: 为 X 功能生成测试清单和测试代码</div>
    <div class="text-sm opacity-70">对测试提供反馈，修正任何问题</div>
  </div>
</div>

<div class="p-4 bg-red-500/20 rounded-lg flex items-center gap-4">
  <div class="text-2xl font-mono bg-red-500/30 px-3 py-1 rounded">2</div>
  <div>
    <div class="font-bold">运行测试，观察失败 🔴</div>
    <div class="text-sm opacity-70">修复编译错误，确保测试失败原因正确</div>
  </div>
</div>

<div class="p-4 bg-green-500/20 rounded-lg flex items-center gap-4">
  <div class="text-2xl font-mono bg-green-500/30 px-3 py-1 rounded">3</div>
  <div>
    <div class="font-bold">Prompt: 实现功能，通过所有测试 🟢</div>
    <div class="text-sm opacity-70">对实现代码提供反馈</div>
  </div>
</div>

<div class="p-4 bg-purple-500/20 rounded-lg flex items-center gap-4">
  <div class="text-2xl font-mono bg-purple-500/30 px-3 py-1 rounded">4</div>
  <div>
    <div class="font-bold">Prompt: 重构代码，保持测试通过 🔵</div>
    <div class="text-sm opacity-70">寻找重复代码、复杂逻辑，优化设计</div>
  </div>
</div>

</div>

</v-clicks>

---
layout: center
class: text-center
---

# 🎬 Cursor 实战演示

<div class="text-2xl mt-8 text-gray-400">
实现活动状态变更功能
</div>

---

# 演示需求

<div class="p-6 bg-gradient-to-r from-violet-500/20 to-fuchsia-500/20 rounded-2xl mt-8">

### 需求

> 作为运营人员，我需要能够启动营销活动，
> 只有草稿状态的活动才能启动，启动后状态变为运行中

</div>

<v-click>

<div class="mt-8">

### 测试清单 📝

```markdown
- [ ] 启动草稿状态的活动，状态变为 RUNNING
- [ ] 启动非草稿状态的活动，抛出业务异常
- [ ] 启动不存在的活动，抛出异常
```

</div>

</v-click>

---

# 演示流程

<div class="grid grid-cols-2 gap-8 mt-8">

<div class="space-y-4">

### 🎯 Cursor 操作

<v-clicks>

1. **打开 Composer** (Cmd+I)

2. **Prompt 1**: 生成测试
   > "为活动状态变更功能生成测试，包括：启动成功、非草稿状态启动失败、活动不存在"

3. **运行测试** → 观察失败

4. **Prompt 2**: 实现功能
   > "实现 startCampaign 方法，通过所有测试"

5. **运行测试** → 全部通过

6. **Prompt 3**: 重构
   > "检查代码，有什么可以优化的？"

</v-clicks>

</div>

<v-click>

<div class="p-6 bg-gradient-to-br from-blue-500/20 to-blue-600/10 rounded-xl">

### 💡 Tips

- 给 AI **明确的上下文**
- **审查** AI 生成的测试
- 确保测试**失败原因正确**
- 不要一次性做太多

</div>

</v-click>

</div>

---

# AI 重构提示词

<v-clicks>

<div class="grid grid-cols-2 gap-6 mt-8">

<div class="p-4 bg-blue-500/20 rounded-lg">

### 🔍 寻找重复代码
> "寻找重复代码，如何消除冗余？"

</div>

<div class="p-4 bg-green-500/20 rounded-lg">

### 📊 简化条件语句
> "有很多散落的 IF 语句，如何用设计模式简化？"

</div>

<div class="p-4 bg-purple-500/20 rounded-lg">

### 🧩 拆分复杂逻辑
> "这个函数逻辑复杂，如何拆分简化？"

</div>

<div class="p-4 bg-orange-500/20 rounded-lg">

### 📝 改进建议
> "检查我们的对话，如何重构使下次类似修改更容易？"

</div>

</div>

</v-clicks>

<v-click>

<div class="mt-6 p-4 bg-yellow-500/20 rounded-lg text-center">

💡 使用 AI 时，**每 1-2 个功能任务后**就应该做一次"大扫除"重构

</div>

</v-click>
