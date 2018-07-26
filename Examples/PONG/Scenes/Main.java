package com.PONG;
import java.awt.Toolkit;
import java.awt.Color;
import zeity.*;

public class Main extends SceneBehavior {
	public void start(){
		GameObject ball = new GameObject();
		ball.addScript(new Ball());
		GameObject userPaddle = new GameObject(10, Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
		userPaddle.addScript(new Paddle());
		userPaddle.addScript(new UserInput());
		GameObject compPaddle = new GameObject(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 35, Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
		compPaddle.addScript(new Paddle());
		compPaddle.addScript(new ComputerInput());

		//System.out.println(Resources.loadResourceAsStream("properties.txt"));
	}
}







