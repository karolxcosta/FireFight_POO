package pt.iul.poo.firefight.starterpack;
import java.awt.event.KeyEvent;

import pt.iul.ista.poo.gui.ImageTile;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;

//Classe Bulldozer que implementa o movimento dos bulldozers
public class Bulldozer extends GameElement implements Movable, ActiveElement{
	
	private boolean isActive=false;
	private String imageName="bulldozer";
	
	@Override
	public boolean isActiveElement() {
		return (isActive==true);
	}	

	@Override
	public void changeStateOfActivation() {
		if (isActive==true) {
			isActive=false;
		}else {
			isActive=true;
		}
	}
	
	public Bulldozer(Point2D position) {
		super(position);
	}
	
	@Override
	public Point2D getPosition() {
		return position;
	}
	
	@Override
	public String getName() {
		return imageName;
	}
	
	//Método que muda a direção do bulldozer no jogo, de acordo com a direção das teclas pressionadas
	public void changeDirection(int lastKeyPressed) {
		switch (lastKeyPressed) {
		case KeyEvent.VK_UP:
			imageName = "bulldozer_up";
			break;
		case KeyEvent.VK_DOWN:
			imageName = "bulldozer_down";
			break;
		case KeyEvent.VK_LEFT:
			imageName = "bulldozer_left";
			break;
		case KeyEvent.VK_RIGHT:
			imageName = "bulldozer_right";
			break;
			
		default: imageName="bulldozer";
		}
	}

	@Override
	public int getLayer() {
		return 3;
	}
	
	//O bulldozer move numa direção de acordo com as teclas direcionais
	@Override
	public void move(int keyCode) {
		boolean hasMoved = false;
		
		while (hasMoved == false)  {
			//É calculada a posição que (em princípio) o bulldozer terá na próxima jogada
			Direction dir = Direction.directionFor(keyCode);
			Point2D newPosition = super.getPosition().plus(dir.asVector());
			//Mover o bulldozer de acordo com a direção que toma
			if (canMoveTo(newPosition) && !GameEngine.getInstance().isBulldozerAtPosition(newPosition)) {
				changeDirection(keyCode);
				setPosition(newPosition);
			}
			hasMoved = true;
			//Fazer o bulldozer mudar o terreno das células onde passa, substituindo a vegetação por um terreno sem vegetação
			Burnable bur=GameEngine.getInstance().getBurnableAtPosition(newPosition);
			if (bur!=null) {
				GameEngine.getInstance().removeImage((ImageTile) bur);
				GameEngine.getInstance().addImage(new Land(newPosition));
			}
		}
	}
}