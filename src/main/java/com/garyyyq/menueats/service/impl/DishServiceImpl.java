package com.garyyyq.menueats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.garyyyq.menueats.common.CustomException;
import com.garyyyq.menueats.dto.DishDto;
import com.garyyyq.menueats.dto.SetmealDto;
import com.garyyyq.menueats.entity.Dish;
import com.garyyyq.menueats.entity.DishFlavor;
import com.garyyyq.menueats.entity.Setmeal;
import com.garyyyq.menueats.entity.SetmealDish;
import com.garyyyq.menueats.mapper.DishMapper;
import com.garyyyq.menueats.service.DishFlavorService;
import com.garyyyq.menueats.service.DishService;
import com.garyyyq.menueats.service.SetmealDishService;
import com.garyyyq.menueats.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private final DishFlavorService dishFlavorService;

//    @Autowired
    private final SetmealDishService setmealDishService;
    private final SetmealService setmealService;

    public DishServiceImpl(DishFlavorService dishFlavorService, SetmealDishService setmealDishService, SetmealService setmealService) {
        this.dishFlavorService = dishFlavorService;
        this.setmealDishService = setmealDishService;
        this.setmealService = setmealService;
    }

    @Override
    @Transactional
    public void saveWithFlavors(DishDto dishDto) {
        // Save basic dish information
        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((flavor) -> {
            flavor.setDishId(dishId);
            return flavor;
        }).collect(Collectors.toList());
        // Save dish flavors to dish_flavor table
        dishFlavorService.saveBatch(dishDto.getFlavors());

    }

    //get dish and flavors by dish id
    @Override
    public DishDto getByIdWithFlavors(Long id) {
        //Get dish by id from dish table
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        //Get flavors by dish id from dish_flavor table
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavors(DishDto dishDto) {
        // Update dish information
        this.updateById(dishDto);

        // Delete flavors by dish id from dish_flavor table
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // Save dish flavors to dish_flavor table
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
        dishFlavors = dishFlavors.stream().map((dishFlavor) -> {
            dishFlavor.setDishId(dishDto.getId());
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishFlavors);
    }

    @Override
    @Transactional
    public void removeWithFlavors(List<Long> ids) {

        // check if the status of the dish is 1
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("The dish is in sale, cannot be deleted");
        }
        // Delete dish by dish id from dish table
        this.removeByIds(ids);

        // Delete flavors by dish id from dish_flavor table
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper1);
    }

    @Override
    @Transactional
    public void updateWithSetmeals(int status, List<Long> ids) {
        log.info("ids: {}", ids);
        // update dish status by dish id from dish table
        this.updateBatchById(ids.stream().map((id) -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList()));

        // update setmeal status by dish id from setmeal table
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getDishId, ids);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        // Get setmeal id list from dish id list
        List<Long> setmealIds = setmealDishes.stream().map(SetmealDish::getSetmealId).collect(Collectors.toList());
        setmealService.updateBatchById(setmealIds.stream().map((id) -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            return setmeal;
        }).collect(Collectors.toList()));
    }


}

