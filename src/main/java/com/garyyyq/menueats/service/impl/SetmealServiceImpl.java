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
import com.garyyyq.menueats.mapper.SetmealMapper;
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
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    private final SetmealDishService setmealDishService;

    public SetmealServiceImpl(SetmealDishService setmealDishService) {
        this.setmealDishService = setmealDishService;
    }

    @Override
    @Transactional
    public void saveWithDishes(SetmealDto setmealDto) {
        // operate setmeal table
        this.save(setmealDto);

        // operate setmeal_dish table
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((setmealDish) -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    //Delete combo and relative dishes
    @Override
    @Transactional
    public void removeWithDishes(List<Long> ids) {
        // check if the status of the combo is 1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("The combo is in sale, cannot be deleted");
        }

        // delete setmeal from setmeal table
        this.removeByIds(ids);

        // delete setmeal from setmeal_dish table
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    @Transactional
    public SetmealDto getByIdWithDishes(Long id) {
        //Get setmeal by id from setmeal table
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);

        //Get dishes by setmeal id from setmeal_dish table
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
        // update setmeal table
        this.updateById(setmealDto);

        // delete dishes by setmeal id from setmeal_dish table
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        // Save dishes to setmeal_dish table
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((setmealDish) -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }


}
