package traffic_sim;

import java.util.*;

import processing.core.PApplet;

public class SimSpace extends PApplet {
	Simulation sim;
	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", SimSpace.class.getName() }); 
	}

    public void settings() {
		SimulationSettings settings = new DefaultSettings();
		sim = new Simulation(settings, 4, false);
    	fullScreen();
    }

    public void setup(){
    	background(255);
		rectMode(CENTER);
//		for (int i = 0; i < 50000; i++) {
//			sim.step(0.0166);
//		}
    }

    public void draw(){
        
        for (int i = 0; i < 20; i++) {
        	sim.step(0.0166);
        }
        
        Map<Integer, List<Auto>> snapShot = sim.snapshot();
        int laneCount = snapShot.size();
        float borderSize = height / 20;
        float laneRenderSize = height / 15;
    	// Draw Lane
        for (int laneNumber = 0; laneNumber < laneCount; laneNumber++) {
	        float radius = height / 2 - borderSize - laneNumber * laneRenderSize / 2;
	        fill(125);
	        ellipse(width/2, height/2, radius * 2 + laneRenderSize / 2, radius * 2 + laneRenderSize / 2);
        }

        // DrawCars
        for (int laneNumber = 0; laneNumber < laneCount; laneNumber++) {
	        float radius = height / 2 - borderSize - laneNumber * laneRenderSize / 2;
	        float circleLength = 2 * radius * (float)Math.PI;
	        
	        List<Auto> loop = snapShot.get(laneNumber);
	        for (Auto car : loop) {
	            double radians = -2 * Math.PI * car.getPos() / sim.length;
	            double x = Math.cos(radians) * radius + width / 2;
	            double y = Math.sin(radians) * radius + height / 2;
	            
	            pushMatrix();
	            translate((float)x, (float)y);
	            rotate((float)radians);
	            
	            float speedFraction = (float)Math.pow(car.getSpeed() / car.getDesiredSpeed(), 0.25);
	            fill((float)255 * ((float)1.0 - speedFraction), (float)255 * speedFraction, 0);
	            
	            double carLength = car.getSize() * circleLength / sim.length;
	            rect(0, 0, laneRenderSize * (float)0.3, (float)carLength);
	            popMatrix();
	        }
        }
        fill(255);
        ellipse(width/2, height/2, 
        		height - borderSize - laneCount * laneRenderSize - laneRenderSize / 4,
        		height - borderSize - laneCount * laneRenderSize - laneRenderSize / 4);
        if (sim.looped) {
        	fill(0);
        	rect(width * 3 / 4, height / 2, width / 2, 50);
        }
    }
}
