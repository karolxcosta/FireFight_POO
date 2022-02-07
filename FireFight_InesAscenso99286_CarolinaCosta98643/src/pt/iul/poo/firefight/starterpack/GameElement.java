package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.gui.ImageTile;
import pt.iul.ista.poo.utils.Point2D;

//Classe abstrata com o intuito de agregar todos as classes representantes de elementos do jogo, visto que
//a maior parte destes utilizam os mesmos métodos e possuem construtores e atributos iguais.
public abstract class GameElement implements ImageTile{

	//Atributos que definem a posição, o nome da imagem associada e a layer da imagem, respetivamente
	public Point2D position;
	private String name;
	private int layer;
	
	public GameElement(Point2D position) {
		this.position = position;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}

	@Override
	public int getLayer() {
		return layer;
	}
	
	//Verifica se a posicao dada se encontra dentro da grelha de jogo
	public boolean canMoveTo(Point2D p) {
		if (p.getX() < 0) return false;
		if (p.getY() < 0) return false;
		if (p.getX() >= GameEngine.GRID_WIDTH) return false;
		if (p.getY() >= GameEngine.GRID_HEIGHT) return false;
		return true;
	}
	
	//Define uma nova posição para um objeto
	public void setPosition(Point2D position) {
		this.position=position;
	}
}
