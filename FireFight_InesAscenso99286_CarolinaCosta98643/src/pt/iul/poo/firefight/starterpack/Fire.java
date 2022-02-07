package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.utils.Point2D;

//Classe Fire que implementa o as ações do fogo
public class Fire extends GameElement implements Updatable{
	
	//Atributos que definem o número de jogadas desde o fogo ser criado até ser removido do jogo,
	// e o Burnable (tipo de vegetação) que é caracteristíco do terreno onde o fogo se encontra, respetivamente
	private int numMovesFire;
	private Burnable bur;
	
	public Fire(Point2D position){
		super(position);
		
		//Ao criar um novo fogo, é guardado o Burnable que se encontra na mesma posição em que este é criado,
		// para serem guardados os seus dados na atualização do fogo, tal como o número de movimentos até 
		// este se extinguir (bur.getNumPlaysToBurn()), respetivamente
		this.bur=GameEngine.getInstance().getBurnableAtPosition(position);
		this.numMovesFire=bur.getNumPlaysToBurn();
	}
	
	@Override
	public String getName() {
		return "fire";
	}
	
	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public void update() {
		
		//O número de movimentos até o fogo se extinguir é decrementado
		numMovesFire--;
		
		//Se o número de movimentos for 0, então o Burnable associado irá ser queimado e o fogo irá ser removido do jogo
		if (numMovesFire==0) {
			bur.burn();
			GameEngine.getInstance().removeFireAtPosition(getPosition());
		}
	}
	
	@Override
	public void setPosition(Point2D position) {
		return;
	}
}
