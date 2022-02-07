package pt.iul.poo.firefight.starterpack;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;

//Classe Fireman que implementa o movimento do bombeiro e controla a utilização de alguns elementos com que interage
public class Fireman extends GameElement implements Movable, ActiveElement, Updatable{
	
	private boolean isActive=true;
	private String imageName="fireman";
	
	public Fireman(Point2D position) {
		super(position);
	}
	
	@Override
	public String getName() {
		return imageName;
	}
	
	@Override
	public int getLayer() {
		return 4;
	}
	
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
	
	//Ao ser atualizado, irá proceder à criação de novos fogos e à remoção dos aviões que tenham acabado o seu movimento
	@Override 
	public void update() {
		createNewFires();
		removePlanes();
	}
	
	//Método que muda a direção do bombeiro no jogo, de acordo com a direção das teclas pressionadas
	public void changeDirection(int lastKeyPressed) {
		switch (lastKeyPressed) {
		case KeyEvent.VK_LEFT:
			imageName = "fireman_left";
			break;
		case KeyEvent.VK_RIGHT:
			imageName = "fireman_right";
			break;
		default: imageName="fireman";
		}
	}
	
	//Procedimento que irá remover o bombeiro de dentro de um Bulldozer, após ser pressionada a tecla 'ENTER' 
	// e se este se encontrar dentro de algum destes
	public void leaveBulldozer() {
		if (!isActiveElement()) {
			//Os estados de ativação do bombeiro e do bulldozer em questão irão ser mudados e o 
			// bombeiro irá reaparecer na janela na posição onde se encontra o bulldozer
			changeStateOfActivation();
			setPosition(GameEngine.getInstance().getActiveBulldozer().getPosition());
			GameEngine.getInstance().getActiveBulldozer().changeStateOfActivation();
			GameEngine.getInstance().changeFiremanAppearance();
		}
	}
	
	//Função de caráter auxiliar que devolve a coluna em que se encontram mais fogos para a inicialização do avião
	public int getColumnWithMoreFires() {
		int column=0;
		int max=0;
		for(int i=0; i<GameEngine.getInstance().GRID_WIDTH; i++) {
			int count=0;
			for (int j=0; j<GameEngine.getInstance().GRID_HEIGHT; j++) {
				if (GameEngine.getInstance().isFireAtPosition(new Point2D(i, j)))
					count++;
			}
			if(max<count) {
				column=i;
				max=count;
			}
		}
		return column;
	}
	
	//Chama um novo avião quando se pressiona a tecla 'P', aparecendo na coluna com mais fogos, na ultima linha da grelha do mapa
	public void callPlane() {
		Plane plane=new Plane( new Point2D( getColumnWithMoreFires(), 9));
		GameEngine.getInstance().getAllPlanes().add(plane);
		GameEngine.getInstance().addImage(plane);
	}
	
	//Mover o bombeiro ou o bulldozer, dependendo de quem está ativo, já que os movimentos de ambos são muito semelhantes
	public void moveFiremanOrBulldozer(int key) {
		if (!isActiveElement()) {
			//Quem se irá movimentar é um bulldozer
			Bulldozer bul=GameEngine.getInstance().getActiveBulldozer();
			Point2D newPosition=bul.getPosition().plus(Direction.directionFor(key).asVector());
			//Verificar se já existe fogo na posição. Se sim, o bulldozer não anda
			if (!GameEngine.getInstance().isFireAtPosition(newPosition)) {
				bul.move(key);
			}
		} else {
			//Quem se irá movimentar é o bombeiro
			Point2D newPosition = getPosition().plus(Direction.directionFor(key).asVector());
			changeDirection(key);
			//Verificar se já existe fogo na posição. Se sim, o bombeiro não anda
			if (!GameEngine.getInstance().isFireAtPosition(newPosition)) {
				move(key);
				for (Bulldozer bul : GameEngine.getInstance().getAllBulldozers()) {
					if(bul.getPosition().equals(newPosition)){
						//Se a posição do bombeiro for uma posição em que esteja um bulldozer, os estados de ativação do 
						// bombeiro e do bulldozer em questão irão ser mudados e o bombeiro irá desaparecer da janela
						changeStateOfActivation();
						bul.changeStateOfActivation();
						GameEngine.getInstance().changeFiremanAppearance();
					}
				}
			}else {
				//Quando o bombeiro tenta ir para uma posição onde tem fogo, este irá apagá-lo, através da água
				GameEngine.getInstance().extinguishFires(newPosition, key);
			}
		}
	}
	
	//Método de propagação de fogos de acordo com os ciclos e probablilidade de cada elemento Burnable
	public void createNewFires() {
		List<Point2D> neighbourPoints=new ArrayList<>();
		for (Fire fire : GameEngine.getInstance().getAllFires()) {
			Point2D firePosition = fire.getPosition();
			neighbourPoints=firePosition.getNeighbourhoodPoints();
			for(Point2D p: neighbourPoints) {
				if ((!GameEngine.getInstance().isFireAtPosition(p)) && (!getPosition().equals(p)) && GameEngine.getInstance().getActiveBulldozer()==null) {
					Burnable bur=GameEngine.getInstance().getBurnableAtPosition(p);
					if (bur!=null && !bur.isBurned())			
						if (Math.random()<bur.getProbability()) 
							GameEngine.getInstance().addImage(new Fire(p));
						}
					}

				}
			}
	
	//Remove um avião sempre que este acaba o seu número de jogadas (ou seja, quando vai até ao fim da coluna onde está)
	public void removePlanes() {
		for (Plane plane : GameEngine.getInstance().getAllPlanes()){
			if (plane!=null && plane.asFinished())
				GameEngine.getInstance().removeImage(plane);
		}
	}
	
	//O bombeiro move numa direção de acordo com as teclas direcionais
	@Override
	public void move(int keyCode) {
		boolean hasMoved = false;
		while (hasMoved == false)  {
			Direction dir = Direction.directionFor(keyCode);
			Point2D newPosition = super.getPosition().plus(dir.asVector());
			if (canMoveTo(newPosition) ) {
				setPosition(newPosition);
			}
			hasMoved = true;
		}
	}
}