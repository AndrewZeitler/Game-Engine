package com.PONG;
import zeity.*;

public class UserInput extends ScriptBehavior {
	Paddle paddle;	

	public void start(){
		paddle = (Paddle)getGameObject().findScriptOfType("Paddle");
	}

	public void update(){
		if(isKeyPressed()){
			if(getKey() == 'w'){
				paddle.setYVelocity(-8);
			}else if(getKey() == 's'){
				paddle.setYVelocity(8);
			}
		} else  {
			paddle.setYVelocity(0);
		}
	}

}




