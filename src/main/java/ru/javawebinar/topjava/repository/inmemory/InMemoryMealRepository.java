package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    //private final Map<Integer, Map<Integer, Meal>> userRepos = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
//        MealsUtil.meals.forEach(this::save);
        MealsUtil.meals.forEach(x->this.save(x, 1));
    }

    @Override
    public Meal save( Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }

        // handle case: update, but not present in storage
        Meal mealRepos = repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        if(mealRepos.getUserId() != userId){
            return null;
        }
//        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        return mealRepos;
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = repository.get(id);
        if(meal.getUserId() != userId) {
            return false;
        }
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
//        if(meal == null || meal.getUserId() != userId){
//            return null;
//        }
        return  (meal == null || meal.getUserId() != userId) ? null : meal;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        Collection<Meal> userMeals = new java.util.ArrayList<>(repository.values()
                .stream()
                .filter(x -> x.getUserId() == userId)
                .sorted((o1, o2) -> {
                    return o1.getDateTime().isBefore(o2.getDateTime()) ? -1:1;
                })
                .toList());
        //return repository.values();
        return userMeals;
    }
}

