package com.machinelearning.model;

import java.util.Random;

import org.neuroph.core.Weight;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.machinelearning.Utility;
import com.machinelearning.model.action.Action;
import com.machinelearning.model.sensor.Sensor;

public class Animal {
	
	public static final Color ANIMAL_COLOR = Color.VIOLET;
	public static final Color BEST_COLOR = Color.RED;
	
	private final long id = Utility.getID();
	
	private static Random random = new Random();
	
	private Sensor[] sensors;
	private double[] sensorData;
	
	private Action[] actions;
	private double[] actionData;
	
	//private NeuralNetwork ann;
	private MultiLayerPerceptron ann;
	
	private int fitness;
	private Environment environment;
	
	public Vector2 position;
	public Vector2 velocity;
	//public Vector2 velocity;
	
	private static final float MAX_SPEED = 15.0f;
	private float speed;
	
	private Color color;
	
	public Animal(Environment environment, Sensor[] sensors, Action[] actions) {
		this.environment = environment;
		this.sensors = sensors;
		this.actions = actions;
		this.sensorData = new double[sensors.length];
		this.actionData = new double[actions.length];
		this.position = new Vector2(
			random.nextFloat() * Config.WIDTH, 
			random.nextFloat() * Config.HEIGHT
		);
		this.velocity = new Vector2(
			random.nextFloat() - 0.5f,
			random.nextFloat() - 0.5f
		);
		//this.velocity = new Vector2(newVelocity);
		this.speed = Utility.random.nextFloat() * getMaxSpeed();//3.0f;
		this.color = Color.VIOLET;
		//this.ann = new NeuralNetwork(sensors.length, sensors.length * 2, actions.length);

		this.ann = new MultiLayerPerceptron(TransferFunctionType.GAUSSIAN, sensors.length, sensors.length*2, actions.length);


		this.fitness = 0;		
	}
	
	public Animal copy() {
		double[] g = getGenome();
		double[] ng = new double[g.length];
		System.arraycopy(g, 0, ng, 0, g.length);
		Animal a = new Animal(environment, sensors, actions);
		a.setGenome(ng);
		return a;
	}
	
	public void readSensorData() {
		for(int i=0; i<sensors.length; i++) {
			sensorData[i] = sensors[i].readSensorValue(this);
		}
		//actionData = ann.execute(sensorData);

		ann.setInput(sensorData);
		ann.calculate();
		actionData = ann.getOutput();
	}
	
	public void executeAction() {
		for(int i=0; i<actions.length; i++) {
			actions[i].executeAction(this, (float) actionData[i]);
		}
	}
	
	public void update(float delta) {
		velocity.nor();
		velocity.scl(speed);
		velocity.scl(delta);
		position.add(velocity);
		//velocity.x = velocity.x;
		//velocity.y = velocity.y;
		//wrapAround();
		checkBounds();
		hasFoundFood();
	}
	
	private void checkBounds() {
		if(position.x >= Config.WIDTH) {
			position.x = Config.WIDTH - 0.1f;
			velocity.scl(-1);
		}
		else if (position.x < 0) {
			position.x = 0.1f;
			velocity.scl(-1);
		}
		if(position.y >= Config.HEIGHT) {
			position.y = Config.HEIGHT - 0.1f;
			velocity.scl(-1);
		}
		else if (position.y < 0) {
			position.y = 0.1f;
			velocity.scl(-1);
		}
	}
	
	private void wrapAround() {
		if(position.x >= Config.WIDTH) {
			position.x -= Config.WIDTH;
		}
		else if (position.x < 0) {
			position.x += Config.WIDTH;
		}
		if(position.y >= Config.HEIGHT) {
			position.y -= Config.HEIGHT;
		}
		else if (position.y < 0) {
			position.y += Config.HEIGHT;
		}
	}
	
	public void hasFoundFood() {
		for(Food f : environment.getFood()) {
			if(position.dst2(f.position) < 0.5f) {
				//TODO include ID in log
				//Utility.log("Animal " + id + " found food at " + f.x() + ", " + f.y());
				fitness += 1;
				f.found();
			}
		}
	}
	
	public float x() {
		return position.x;
	}
	
	public float y() {
		return position.y;
	}
	
	public Color color() {
		return color;
	}
	
	public void resetColor() {
		color = ANIMAL_COLOR;
	}
	
	public void setAsHighestFitness() {
		color = BEST_COLOR;
	}
	
	public int fitness() {
		return fitness;
	}
	
	public void setFitness(int a){
		fitness = a;
		
	}
	
	public long id() {
		return id;
	}
	
	public double[] getGenome() {
		Double[] a = ann.getWeights();
		 
		double[] genome = new double[a.length];
		for(int i =0; i<genome.length; i++)
			genome[i] = a[i];	
		
		return genome;
	}
	
	public void setGenome(double[] genome) {

		ann.setWeights(genome);
	}
	
	public Environment getEnvironment() {
		return environment;
	}
	
	public Sensor[] getSensors() {
		return sensors;
	}
	
	public Action[] getActions() {
		return actions;
	}
	
	public float getMaxSpeed() {
		return MAX_SPEED;
	}
	
	public void setSpeed(float s) {
		speed = s;
	}
	
	public float getSpeed() {
		return speed;
	}
	

}
