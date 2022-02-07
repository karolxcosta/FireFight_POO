package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.utils.Point2D;

//Classe Eucaliptus que caracteriza as características do eucalipto, um tipo de vegetação
public class Eucaliptus extends GameElement implements Burnable{

	private boolean isBurned=false;
	
	public Eucaliptus(Point2D position) {
		super(position);
	}

	//Função que retorna o nome da imagem em que está representado o Eucaliptus, consoante este esteja ou não queimado
	@Override
	public String getName() {
		if (!isBurned)
			return "eucaliptus";
		return "burnteucaliptus";
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
		return 0.1;
	}
	
	@Override
	public int getNumPlaysToBurn() {
		return 5;
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
