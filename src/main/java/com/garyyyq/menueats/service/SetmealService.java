package com.garyyyq.menueats.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garyyyq.menueats.dto.SetmealDto;
import com.garyyyq.menueats.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    // Save combo with dishes
    void saveWithDishes(SetmealDto setmealDto);
    //Delete combo and relative dishes
    void removeWithDishes(List<Long> ids);
    SetmealDto getByIdWithDishes(Long id);

    void updateWithDishes(SetmealDto setmealDto);
}
