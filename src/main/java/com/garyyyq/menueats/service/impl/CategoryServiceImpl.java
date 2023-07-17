package com.garyyyq.menueats.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garyyyq.menueats.common.CustomException;
import com.garyyyq.menueats.entity.Category;
import com.garyyyq.menueats.entity.Dish;
import com.garyyyq.menueats.entity.Setmeal;
import com.garyyyq.menueats.mapper.CategoryMapper;
import com.garyyyq.menueats.service.CategoryService;
import com.garyyyq.menueats.service.DishService;
import com.garyyyq.menueats.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
//    @Autowired
    private final DishService dishService;

    private final SetmealService setmealService;

    public CategoryServiceImpl(DishService dishService, SetmealService setmealService) {
        this.dishService = dishService;
        this.setmealService = setmealService;
    }

    @Override
    public void remove(Long id) {

        // check if there is any dish under this category
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if(dishCount > 0){
            throw new CustomException("Cannot delete category with dish under it");
        }


        // check if there is any setmeal under this category
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if(setmealCount > 0){
            throw new CustomException("Cannot delete category with setmeal under it");
        }

        // delete category
        super.removeById(id);
    }
}
