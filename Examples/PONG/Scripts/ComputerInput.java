package com.PONG;
import zeity.*;

public class ComputerInput extends ScriptBehavior {
	Ball ball;
	Paddle paddle;

	public void start(){
		ball = (Ball)ObjectManager.findScriptsOfType("Ball").get(0);
		paddle = (Paddle)getGameObject().findScriptOfType("Paddle");
	}

	public void update(){
		if(ball.getY() > getY()){
			paddle.setYVelocity(8);
		} else if(ball.getY() < getY() - 100){
			paddle.setYVelocity(-8);
		} else {
			paddle.setYVelocity(0);
		}
	}

}










