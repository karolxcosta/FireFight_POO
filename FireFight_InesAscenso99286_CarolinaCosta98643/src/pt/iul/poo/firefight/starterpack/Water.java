package pt.iul.poo.firefight.starterpack;
import java.awt.event.KeyEvent;

import pt.iul.ista.poo.utils.Point2D;

// Classe Water que define o comportamento da água quando é apagado um fogo
public class Water extends GameElement{
	
	//Atributo que define a imagem que representa a água, consoante a sua direção
	private String imageName="water";
	
	public Water(Point2D position) {
		super(position);
	}
	
	//Mudança de direção da água
	public void changeDirection(int lastKeyPressed) {
		switch (lastKeyPressed) {
		case KeyEvent.VK_UP:
			imageName = "water_up";
			break;
		case KeyEvent.VK_DOWN:
			imageName = "water_down";
			break;
		case KeyEvent.VK_LEFT:
			imageName = "water_left";
			break;
		case KeyEvent.VK_RIGHT:
			imageName = "water_right";
			break;

		default: imageName = "water";
		}
	}

	@Override
	public String getName() {
		return imageName;
	}
	
	@Override
	public int getLayer() {
		return 3;
	}
	
	@Override
	public void setPosition(Point2D position) {
		return;
	}
}
