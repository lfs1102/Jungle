package entity.animal.instance;

import entity.animal.type.AnimalWithoutSkill;

/**
 * Created by lifengshuang on 9/25/16.
 */
public class Dog extends AnimalWithoutSkill {

    @Override
    protected String getName() {
        return "狗";
    }
}
