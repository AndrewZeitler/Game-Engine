package com.PONG;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Color;
import zeity.*;

public class Paddle extends ScriptBehavior {
	int width;
	int height;
	double yVelocity;

	public void start(){
		width = 25;
		height = 100;
	}

	public void update(){
		if(getY() + yVelocity < 0 || getY() + height + yVelocity > Toolkit.getDefaultToolkit().getScreenSize().getHeight()){
			yVelocity = 0;
		} else {
			setPosition(getX(), getY() + yVelocity);
		}
	
		Graphics g = getGraphics();
		g.setColor(Color.RED);
		g.fillRect((int)getX(), (int)getY(), width, height);	
	}

	public boolean isColliding(double x, double y){
		if(x > getX() && x < getX() + width && y > getY() && y < getY() + height)
			return true;
		return false;
	}

	public void setYVelocity(double yVelocity){
		this.yVelocity = yVelocity;
	}
}



