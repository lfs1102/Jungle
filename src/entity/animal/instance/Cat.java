package entity.animal.instance;

import entity.animal.type.AnimalWithoutSkill;

/**
 * Created by lifengshuang on 9/25/16.
 */
public class Cat extends AnimalWithoutSkill {

    @Override
    protected String getName() {
        return "猫";
    }
}
