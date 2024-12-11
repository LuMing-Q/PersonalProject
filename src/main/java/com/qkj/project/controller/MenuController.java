package com.qkj.project.controller;

import com.qkj.project.common.Page;
import com.qkj.project.entity.Menu;
import com.qkj.project.service.MenuService;
import com.qkj.project.vo.RoleMenuGrantVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author KeJiang Qi
 * @date 2024/8/20 - 14:57
 * @description
 */
@RestController
@RequestMapping("/menus")
public class MenuController {

    @Resource
    private MenuService menuService;

    @GetMapping("/{role_id}")
    public List<Menu> getMenuListByRoleId(@PathVariable("role_id") String roleId) {
        return menuService.getMenuListByRoleId(roleId);
    }

    @PostMapping
    public int addMenu(@RequestBody @Valid Menu menu) {
        return menuService.addMenu(menu);
    }

    @PutMapping
    public int editMenu(@RequestBody @Valid Menu menu) {
        return menuService.editMenu(menu);
    }

    @PutMapping("/role")
    public int addRoleMenu(@RequestBody @Validated RoleMenuGrantVO menuGrant)  {
        return menuService.addRoleMenu(menuGrant);
    }

    @GetMapping
    public Page<Menu> getList(@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return menuService.getList(name, page, size);
    }

    @DeleteMapping("/{menu_id}")
    public int deleteMenuByMenuId(@PathVariable("menu_id") String menuId) {
        return menuService.deleteMenuByMenuId(menuId);
    }

    @GetMapping("/all")
    public List<Menu> getList() {
        return menuService.getAll();
    }
}
