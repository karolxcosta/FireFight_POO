package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.utils.Point2D;

//Classe Pine que caracteriza as características do pinheiro, um tipo de vegetação
public class Pine extends GameElement implements Burnable{
	
	private boolean isBurned=false;
	
	public Pine(Point2D position) {
		super(position);
	}
	
	//Função que retorna o nome da imagem em que está representado o Pine, consoante este esteja ou não queimado
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
	
	//No caso de passar o número de jogadas definido (getNumPlaysToBurn()) a arder, este será queimado definitivamente
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
