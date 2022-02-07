package pt.iul.poo.firefight.starterpack;
import java.awt.event.KeyEvent;

import pt.iul.ista.poo.utils.Point2D;

// Classe Water que define o comportamento da �gua quando � apagado um fogo
public class Water extends GameElement{
	
	//Atributo que define a imagem que representa a �gua, consoante a sua dire��o
	private String imageName="water";
	
	public Water(Point2D position) {
		super(position);
	}
	
	//Mudan�a de dire��o da �gua
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
