package traffic_sim;

import java.util.*;

import processing.core.PApplet;

public class SimSpace extends PApplet {
	Simulation sim;
	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", SimSpace.class.getName() }); 
	}

    public void settings(){
		SimulationSettings settings = new DefaultSettings();
		sim = new Simulation(settings, 1);
    	fullScreen();
    }

    public void setup(){
    	background(255);
		rectMode(CENTER);
    }

    public void draw(){
    	fill(125);
        ellipse(width/2,height/2,height-100,height-100);
        fill(255);
        ellipse(width/2, height/2, height-250,height-250);
        
        for (int i = 0; i < 20; i++) {
        	sim.step(0.0166);
        }
        
        Map<Integer, List<Auto>> snapShot = sim.snapshot();
        double diameter = height - 175;
        double circleLength = diameter * Math.PI;
        double radius = diameter / 2;
        List<Auto> loop = snapShot.get(0);
        for (Auto car : loop) {
            double radians = -2 * Math.PI * car.getPos() / sim.length;
            double x = Math.cos(radians) * radius + width / 2;
            double y = Math.sin(radians) * radius + height / 2;
            
            pushMatrix();
            translate((float)x, (float)y);
            rotate((float)radians);
            
            float speedFraction = (float)Math.pow(car.getSpeed() / car.getDesiredSpeed(), 0.25);
            fill((float)255 * ((float)1.0 - speedFraction), (float)255 * speedFraction, 0);
            /*if (car.constrained) {
            	fill(255, 0, 0);
            } else {
            	fill(0, 255, 0);
            }/**/
            
            double carLength = car.getSize() * circleLength / sim.length;
            rect(0, 0, 50, (float)carLength);
            popMatrix();
        }
    }
}
