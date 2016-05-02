package com.machinelearning.model.sensor;

import com.badlogic.gdx.math.Vector2;
import com.machinelearning.model.Animal;
import com.machinelearning.model.Plant;

public class PreyDirectionSensor extends Sensor {

	private char c;
	
	public PreyDirectionSensor(char c) {
		this.c = c;
	}
	
	@Override
	public float readSensorValue(Animal animal) {
		Animal food = environment.getNearestAnimal(animal.position);
		Vector2 target = food.position.cpy().sub(animal.position);
		target.nor();
		if(c == 'y') {
			return target.y;
		}
		else {
			return target.x;
		}
	}

}
