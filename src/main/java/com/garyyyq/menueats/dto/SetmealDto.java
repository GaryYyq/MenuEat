package com.garyyyq.menueats.dto;

import com.garyyyq.menueats.entity.Setmeal;
import com.garyyyq.menueats.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
