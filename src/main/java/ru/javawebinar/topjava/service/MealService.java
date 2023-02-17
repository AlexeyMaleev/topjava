package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFound;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {

    private MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }
    public MealTo create(Meal meal) {
        Meal mealRes = repository.save(meal, SecurityUtil.authUserId());
        if(mealRes == null) {
            throw new NotFoundException("Meal not found") ;
        }

        return MealsUtil.getTos(List.of(mealRes), MealsUtil.DEFAULT_CALORIES_PER_DAY).get(0);
    }

    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id, SecurityUtil.authUserId()), id);
    }

    public MealTo get(int id) {
        Meal mealRes = checkNotFoundWithId(repository.get(id, SecurityUtil.authUserId()), id);
        return MealsUtil.getTos(List.of(mealRes), MealsUtil.DEFAULT_CALORIES_PER_DAY).get(0);
    }


    public List<MealTo> getAll() {
        List<Meal> list = (List<Meal>) repository.getAll(SecurityUtil.authUserId());
        return MealsUtil.getTos(list, MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public void update(Meal meal) {
        checkNotFoundWithId(repository.save(meal, SecurityUtil.authUserId()), meal.getId());
    }

}