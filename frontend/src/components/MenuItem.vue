<template>
  <!-- 有子菜单的情况 -->
  <el-submenu v-if="menu.children && menu.children.length > 0" :index="menu.path">
    <template #title>
      <el-icon v-if="menu.icon">
        <component :is="menu.icon" />
      </el-icon>
      <span>{{ menu.menuName }}</span>
    </template>
    
    <menu-item
      v-for="child in menu.children"
      :key="child.id"
      :menu="child"
    />
  </el-submenu>
  
  <!-- 没有子菜单的情况 -->
  <el-menu-item v-else :index="menu.path">
    <el-icon v-if="menu.icon">
      <component :is="menu.icon" />
    </el-icon>
    <span>{{ menu.menuName }}</span>
  </el-menu-item>
</template>

<script>
export default {
  name: 'MenuItem',
  props: {
    menu: {
      type: Object,
      required: true
    }
  }
}
</script>