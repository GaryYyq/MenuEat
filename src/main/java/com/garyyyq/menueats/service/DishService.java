package com.garyyyq.menueats.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garyyyq.menueats.dto.DishDto;
import com.garyyyq.menueats.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    void saveWithFlavors(DishDto dishDto);

    //get dish and flavors by dish id
    DishDto getByIdWithFlavors(Long id);
    // update dish and flavors by dish id
    void updateWithFlavors(DishDto dishDto);
    // delete dish and flavors by dish id
    void removeWithFlavors(List<Long> ids);
    void updateWithSetmeals(int status, List<Long> ids);
}
