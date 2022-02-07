package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.utils.Point2D;

//Classe Land que caracteriza as características do terreno sem vegetação, um tipo de vegetação
public class Land extends GameElement{
	
	public Land(Point2D position) {
		super(position);
	}
	
	@Override
	public String getName() {
		return "land";
	}
	
	@Override
	public int getLayer() {
		return 1;
	}
	
	@Override
	public void setPosition(Point2D position) {
		return;
	}
}
