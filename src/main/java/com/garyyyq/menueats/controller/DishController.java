package com.garyyyq.menueats.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.garyyyq.menueats.common.R;
import com.garyyyq.menueats.dto.DishDto;
import com.garyyyq.menueats.entity.Category;
import com.garyyyq.menueats.entity.Dish;
import com.garyyyq.menueats.entity.Setmeal;
import com.garyyyq.menueats.service.CategoryService;
import com.garyyyq.menueats.service.DishFlavorService;
import com.garyyyq.menueats.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    private final DishService dishService;
    private final DishFlavorService dishFlavorService;

    private final CategoryService categoryService;

    public DishController(DishService dishService, DishFlavorService dishFlavorService, CategoryService categoryService) {
        this.dishService = dishService;
        this.dishFlavorService = dishFlavorService;
        this.categoryService = categoryService;
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("dishDto: {}", dishDto);

        dishService.saveWithFlavors(dishDto);
        return R.success("Dish addition success");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Dish::getName, name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // execute query
        dishService.page(pageInfo, queryWrapper);


        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((dish) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            Long categoryId = dish.getCategoryId();
            // search category by category id
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * get dish and flavors by dish id
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavors(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("dishDto: {}", dishDto);

        dishService.updateWithFlavors(dishDto);
        return R.success("Dish update success");
    }

    /**
     * search dish by request params
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        queryWrapper.eq(Dish::getStatus, 1);

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam List<Long> ids){
        dishService.updateWithSetmeals(status, ids);
        return R.success("Dish status update success");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam  List<Long> ids){
        log.info("ids: {}", ids);
        dishService.removeWithFlavors(ids);
        return R.success("Dish deletion success");
    }
}
