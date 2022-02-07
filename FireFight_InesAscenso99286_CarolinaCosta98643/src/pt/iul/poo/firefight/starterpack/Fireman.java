package pt.iul.poo.firefight.starterpack;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;

//Classe Fireman que implementa o movimento do bombeiro e controla a utiliza��o de alguns elementos com que interage
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
	
	//Ao ser atualizado, ir� proceder � cria��o de novos fogos e � remo��o dos avi�es que tenham acabado o seu movimento
	@Override 
	public void update() {
		createNewFires();
		removePlanes();
	}
	
	//M�todo que muda a dire��o do bombeiro no jogo, de acordo com a dire��o das teclas pressionadas
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
	
	//Procedimento que ir� remover o bombeiro de dentro de um Bulldozer, ap�s ser pressionada a tecla 'ENTER' 
	// e se este se encontrar dentro de algum destes
	public void leaveBulldozer() {
		if (!isActiveElement()) {
			//Os estados de ativa��o do bombeiro e do bulldozer em quest�o ir�o ser mudados e o 
			// bombeiro ir� reaparecer na janela na posi��o onde se encontra o bulldozer
			changeStateOfActivation();
			setPosition(GameEngine.getInstance().getActiveBulldozer().getPosition());
			GameEngine.getInstance().getActiveBulldozer().changeStateOfActivation();
			GameEngine.getInstance().changeFiremanAppearance();
		}
	}
	
	//Fun��o de car�ter auxiliar que devolve a coluna em que se encontram mais fogos para a inicializa��o do avi�o
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
	
	//Chama um novo avi�o quando se pressiona a tecla 'P', aparecendo na coluna com mais fogos, na ultima linha da grelha do mapa
	public void callPlane() {
		Plane plane=new Plane( new Point2D( getColumnWithMoreFires(), 9));
		GameEngine.getInstance().getAllPlanes().add(plane);
		GameEngine.getInstance().addImage(plane);
	}
	
	//Mover o bombeiro ou o bulldozer, dependendo de quem est� ativo, j� que os movimentos de ambos s�o muito semelhantes
	public void moveFiremanOrBulldozer(int key) {
		if (!isActiveElement()) {
			//Quem se ir� movimentar � um bulldozer
			Bulldozer bul=GameEngine.getInstance().getActiveBulldozer();
			Point2D newPosition=bul.getPosition().plus(Direction.directionFor(key).asVector());
			//Verificar se j� existe fogo na posi��o. Se sim, o bulldozer n�o anda
			if (!GameEngine.getInstance().isFireAtPosition(newPosition)) {
				bul.move(key);
			}
		} else {
			//Quem se ir� movimentar � o bombeiro
			Point2D newPosition = getPosition().plus(Direction.directionFor(key).asVector());
			changeDirection(key);
			//Verificar se j� existe fogo na posi��o. Se sim, o bombeiro n�o anda
			if (!GameEngine.getInstance().isFireAtPosition(newPosition)) {
				move(key);
				for (Bulldozer bul : GameEngine.getInstance().getAllBulldozers()) {
					if(bul.getPosition().equals(newPosition)){
						//Se a posi��o do bombeiro for uma posi��o em que esteja um bulldozer, os estados de ativa��o do 
						// bombeiro e do bulldozer em quest�o ir�o ser mudados e o bombeiro ir� desaparecer da janela
						changeStateOfActivation();
						bul.changeStateOfActivation();
						GameEngine.getInstance().changeFiremanAppearance();
					}
				}
			}else {
				//Quando o bombeiro tenta ir para uma posi��o onde tem fogo, este ir� apag�-lo, atrav�s da �gua
				GameEngine.getInstance().extinguishFires(newPosition, key);
			}
		}
	}
	
	//M�todo de propaga��o de fogos de acordo com os ciclos e probablilidade de cada elemento Burnable
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
	
	//Remove um avi�o sempre que este acaba o seu n�mero de jogadas (ou seja, quando vai at� ao fim da coluna onde est�)
	public void removePlanes() {
		for (Plane plane : GameEngine.getInstance().getAllPlanes()){
			if (plane!=null && plane.asFinished())
				GameEngine.getInstance().removeImage(plane);
		}
	}
	
	//O bombeiro move numa dire��o de acordo com as teclas direcionais
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