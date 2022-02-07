package pt.iul.poo.firefight.starterpack;

//Interface que implementa as classes que definem um tipo de vegetação que poderá ser incendiado
public interface Burnable {
	
	//Função que retorna a probabilidade de um Burnable se incendiar
	public double getProbability();
	//Função que retorna o número de jogadas que eu fogo fica aceso até o Burnable se incendiar
	public int getNumPlaysToBurn();
	//Procedimento que queima um Burnable (ou seja, troca as imagens que o respresentam)
	public void burn();
	//Função que diz se um Burnable está ou não queimado
	public boolean isBurned();
}
