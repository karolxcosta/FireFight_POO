package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.utils.Point2D;

//Classe Fire que implementa o as a��es do fogo
public class Fire extends GameElement implements Updatable{
	
	//Atributos que definem o n�mero de jogadas desde o fogo ser criado at� ser removido do jogo,
	// e o Burnable (tipo de vegeta��o) que � caracterist�co do terreno onde o fogo se encontra, respetivamente
	private int numMovesFire;
	private Burnable bur;
	
	public Fire(Point2D position){
		super(position);
		
		//Ao criar um novo fogo, � guardado o Burnable que se encontra na mesma posi��o em que este � criado,
		// para serem guardados os seus dados na atualiza��o do fogo, tal como o n�mero de movimentos at� 
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
		
		//O n�mero de movimentos at� o fogo se extinguir � decrementado
		numMovesFire--;
		
		//Se o n�mero de movimentos for 0, ent�o o Burnable associado ir� ser queimado e o fogo ir� ser removido do jogo
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
