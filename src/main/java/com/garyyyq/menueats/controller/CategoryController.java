package com.garyyyq.menueats.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.garyyyq.menueats.common.R;
import com.garyyyq.menueats.entity.Category;
import com.garyyyq.menueats.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping
    public R<String> save(@RequestBody Category category) {

        log.info("category: {}", category);
        categoryService.save(category);
        return R.success("add category success");
    }


    @GetMapping("/page")
    public R<Page> page (int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("delete category with id: {}", ids);

//        categoryService.removeById(ids);
        categoryService.remove(ids);

        return R.success("delete category success");
    }
    /**
     * update category
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("update category: {}", category);
        categoryService.updateById(category);
        return R.success("update category success");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        // 1. create query wrapper
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 2. set query conditions
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 3. add order
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        // 4. execute query
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }

}
