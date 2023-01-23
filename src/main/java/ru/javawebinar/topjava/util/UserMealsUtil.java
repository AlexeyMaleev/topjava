package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.LimitExceed;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.*;
import java.util.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        List<UserMealWithExcess> mealsToStream = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToStream.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles

        List<UserMealWithExcess> list = new ArrayList<>();

        HashMap<LocalDate, Integer> dateCalories = new HashMap<>();
        HashMap<LocalDate, LimitExceed> dateLimit = new HashMap<>();
        LimitExceed limitExceed;
        for(int i =0; i < meals.size(); i ++)
        {
            UserMeal userMeal = meals.get(i);
            LocalDateTime dtm = meals.get(i).getDateTime();
            LocalTime tm = dtm.toLocalTime();
            LocalDate dt = dtm.toLocalDate();

            if(dateCalories.containsKey(dt)){
                int calor = dateCalories.get(dt);
                calor +=userMeal.getCalories();
                dateCalories.put(dt, calor);
                if(calor > caloriesPerDay){
                    LimitExceed limitObj = dateLimit.get(dt);
                    limitObj.setExces(true);
                }
            } else {
                limitExceed = new LimitExceed(false);
                dateLimit.put(dt, limitExceed);
                dateCalories.put(dt, userMeal.getCalories());
                int calor = dateCalories.get(dt);
                if(calor > caloriesPerDay){
                    LimitExceed limitObj = dateLimit.get(dt);
                    limitObj.setExces(true);
                }
            }

//            if(tm.isAfter(startTime) && tm.isBefore(endTime)){
            if(TimeUtil.isBetweenHalfOpen(tm, startTime, endTime )){
                UserMealWithExcess userMealWithExcess = new UserMealWithExcess(
                        userMeal.getDateTime(),
                        userMeal.getDescription(),
                        userMeal.getCalories(),
                        dateLimit.get(dt)
                        );
                list.add(userMealWithExcess);
            }
        }

        return list;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        List<UserMealWithExcess> list = new ArrayList<>();
        Map<LocalDate, Integer> dateCalories = new HashMap<>();
        meals.stream().forEach(x->dateCalories.merge( x.getDateTime().toLocalDate(),
                                                    x.getCalories(),
                                                    Integer::sum));

        meals.stream()
                .filter(x->TimeUtil.isBetweenHalfOpen(
                    x.getDateTime().toLocalTime(), startTime, endTime ))
               .forEach(x->list.add(new UserMealWithExcess(x.getDateTime(),
                    x.getDescription(),
                    x.getCalories(),
                    new LimitExceed(
                            dateCalories.get(x.getDateTime().toLocalDate()) > caloriesPerDay))));

        return list;
    }
}
