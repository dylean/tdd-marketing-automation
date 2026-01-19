# 今天的内容 (60min)

<div class="grid grid-cols-2 gap-8 mt-8">

<v-click>
<div class="p-6 rounded-xl bg-gradient-to-br from-blue-500/20 to-blue-600/10">

### 📚 理论 (15min)
- 什么是 TDD
- 红-绿-重构循环
- TDD 的价值

</div>
</v-click>

<v-click>
<div class="p-6 rounded-xl bg-gradient-to-br from-green-500/20 to-green-600/10">

### 💻 手动 TDD 实战 (15min)
- 创建营销活动
- 一步步演示 TDD 流程

</div>
</v-click>

<v-click>
<div class="p-6 rounded-xl bg-gradient-to-br from-purple-500/20 to-purple-600/10">

### 🤖 AI + TDD 实战 (20min)
- AI 时代的 TDD
- Cursor 实战演示

</div>
</v-click>

<v-click>
<div class="p-6 rounded-xl bg-gradient-to-br from-orange-500/20 to-orange-600/10">

### 🎯 总结 (10min)
- 最佳实践
- Q&A

</div>
</v-click>

</div>

---
layout: image-right
image: https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=800
---

# 什么是 TDD？

<v-clicks>

**Test-Driven Development / Design**

> 是一项 **开发活动**，而不是测试活动

测试是 **手段**，设计是 **目标**

</v-clicks>

<v-click>

<div class="mt-6 p-4 bg-gradient-to-r from-purple-500/20 to-blue-500/20 rounded-lg">

> "编写单元测试更像一种设计行为、文档行为，而不是验证行为"
>
> — Robert C. Martin

</div>

</v-click>

---
layout: center
---

# 🔄 红-绿-重构循环

<div class="flex items-center justify-center mt-8">

```mermaid {scale: 0.9}
graph LR
    A[🔴 Red] -->|写失败的测试| B[🟢 Green]
    B -->|写最少代码通过| C[🔵 Refactor]
    C -->|优化设计| A
    
    style A fill:#ef4444,stroke:#dc2626,color:#fff
    style B fill:#22c55e,stroke:#16a34a,color:#fff
    style C fill:#3b82f6,stroke:#2563eb,color:#fff
```

</div>

<v-clicks>

<div class="grid grid-cols-3 gap-4 mt-10 text-center">
  <div class="text-red-400">
    <div class="text-2xl font-bold">Red</div>
    <div class="text-sm">写一个失败的测试</div>
  </div>
  <div class="text-green-400">
    <div class="text-2xl font-bold">Green</div>
    <div class="text-sm">最少代码通过</div>
  </div>
  <div class="text-blue-400">
    <div class="text-2xl font-bold">Refactor</div>
    <div class="text-sm">优化设计</div>
  </div>
</div>

</v-clicks>

---
layout: center
class: text-center
---

# TDD 的价值

<div class="grid grid-cols-3 gap-6 mt-10">

<v-click>
<div class="p-5 bg-blue-500/20 rounded-xl border border-blue-500/40">
  <div class="text-3xl mb-3">📋</div>
  <div class="font-bold text-blue-300">Specification</div>
  <div class="text-sm mt-2 opacity-70">测试即需求规格</div>
</div>
</v-click>

<v-click>
<div class="p-5 bg-green-500/20 rounded-xl border border-green-500/40">
  <div class="text-3xl mb-3">📖</div>
  <div class="font-bold text-green-300">Documentation</div>
  <div class="text-sm mt-2 opacity-70">测试即活文档</div>
</div>
</v-click>

<v-click>
<div class="p-5 bg-purple-500/20 rounded-xl border border-purple-500/40">
  <div class="text-3xl mb-3">🛡️</div>
  <div class="font-bold text-purple-300">Safety Net</div>
  <div class="text-sm mt-2 opacity-70">测试即安全网</div>
</div>
</v-click>

</div>
