package com.PONG;
import java.util.ArrayList;
import java.awt.*;
import zeity.*;

public class Ball extends ScriptBehavior {
	int radius = 20;
	double xVelocity = 0;
	double yVelocity = 0;
	ArrayList<ScriptBehavior> boxColliders;

	public void start(){
		boxColliders = ObjectManager.findScriptsOfType("Paddle");
		resetBall();
	}
	public void update(){
		for(int j = 0; j < boxColliders.size(); j++){
			Paddle boxCollider = (Paddle) boxColliders.get(j);
			if(boxCollider.isColliding(getX() + radius / 2 + xVelocity, getY())){
				setPosition(boxCollider.getX() - radius / 2, getY());
				xVelocity *= -1;
			} else if(boxCollider.isColliding(getX() - radius / 2 + xVelocity, getY())){
				setPosition(boxCollider.getX() + 25 + radius / 2, getY());
				xVelocity *= -1;
			}
			if(boxCollider.isColliding(getX(), getY() + radius / 2 + yVelocity) || boxCollider.isColliding(getX(), getY() - radius / 2 + yVelocity))
				yVelocity *= -1;
		}
		if(getX() + yVelocity - radius / 2 < 0 || getX() + yVelocity + radius / 2 > Toolkit.getDefaultToolkit().getScreenSize().getWidth())
			resetBall();

		if(getY() + yVelocity - radius / 2 < 0){
			setPosition(getX(), radius / 2);
			yVelocity *= -1;
		} else if(getY() + yVelocity + radius * 2 > Toolkit.getDefaultToolkit().getScreenSize().getHeight()){
			setPosition(getX(), Toolkit.getDefaultToolkit().getScreenSize().getHeight() - radius * 2);
			yVelocity *= -1;
		}

		setPosition(getX() + xVelocity, getY() + yVelocity);
		Graphics g = getGraphics();
		g.setColor(Color.RED);
		g.fillOval((int)getX(), (int)getY(), radius, radius);
	}
	public void resetBall(){
		setPosition(Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2, Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
		int rand = (int) (Math.random() * 4 + 1);
		if(rand == 1 || rand == 2){
			xVelocity = 5;
			if(rand == 2){
				yVelocity = 10;
			} else {
				yVelocity = -10;
			}
		} else {
			xVelocity = -5;
			if(rand == 3){
				yVelocity = 10;
			} else {
				yVelocity = -10;
			}
		}
	}
}




