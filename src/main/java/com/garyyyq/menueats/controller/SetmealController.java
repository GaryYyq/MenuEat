package com.garyyyq.menueats.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.garyyyq.menueats.common.R;
import com.garyyyq.menueats.dto.DishDto;
import com.garyyyq.menueats.dto.SetmealDto;
import com.garyyyq.menueats.entity.Category;
import com.garyyyq.menueats.entity.Setmeal;
import com.garyyyq.menueats.service.CategoryService;
import com.garyyyq.menueats.service.SetmealDishService;
import com.garyyyq.menueats.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    private final SetmealService setmealService;
    private final SetmealDishService setmealDishService;
    private final CategoryService categoryService;

    public SetmealController(SetmealService setmealService, SetmealDishService setmealDishService, CategoryService categoryService) {
        this.setmealService = setmealService;
        this.setmealDishService = setmealDishService;
        this.categoryService = categoryService;
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("setmealDto: {}", setmealDto);

        setmealService.saveWithDishes(setmealDto);
        return R.success("Combo addition success");
    }

    /**
     * Combos display and search
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((setmeal) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam  List<Long> ids){
        log.info("ids: {}", ids);
        setmealService.removeWithDishes(ids);
        return R.success("Combo deletion success");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam List<Long> ids){
        log.info("ids: {}", ids);
//        setmealService.updateByIds
        setmealService.updateBatchById(ids.stream().map((id) -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            return setmeal;
        }).collect(Collectors.toList()));
        return R.success("Combo status update success");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDishes(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info("setmealDto: {}", setmealDto);
        setmealService.updateWithDishes(setmealDto);
        return R.success("Combo update success");
    }

}
