package com.garyyyq.menueats.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.garyyyq.menueats.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
