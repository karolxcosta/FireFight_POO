package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.utils.Point2D;

//Classe Pine que caracteriza as caracter�sticas do pinheiro, um tipo de vegeta��o
public class Pine extends GameElement implements Burnable{
	
	private boolean isBurned=false;
	
	public Pine(Point2D position) {
		super(position);
	}
	
	//Fun��o que retorna o nome da imagem em que est� representado o Pine, consoante este esteja ou n�o queimado
	@Override
	public String getName() {
		if (!isBurned)
			return "pine";
		return "burntpine";
	}

	@Override
	public int getLayer() {
		return 1;
	}
	
	//No caso de passar o n�mero de jogadas definido (getNumPlaysToBurn()) a arder, este ser� queimado definitivamente
	@Override
	public void burn () {
		isBurned=true;
	}
	
	@Override
	public double getProbability() {
		return 0.05;
	}
	
	@Override
	public int getNumPlaysToBurn() {
		return 10;
	}
	
	@Override
	public boolean isBurned() {
		return (isBurned==true);
	}
	
	@Override
	public void setPosition(Point2D position) {
		return;
	}
}
