package pt.iul.poo.firefight.starterpack;

//Interface que implementa as classes que definem um tipo de vegeta��o que poder� ser incendiado
public interface Burnable {
	
	//Fun��o que retorna a probabilidade de um Burnable se incendiar
	public double getProbability();
	//Fun��o que retorna o n�mero de jogadas que eu fogo fica aceso at� o Burnable se incendiar
	public int getNumPlaysToBurn();
	//Procedimento que queima um Burnable (ou seja, troca as imagens que o respresentam)
	public void burn();
	//Fun��o que diz se um Burnable est� ou n�o queimado
	public boolean isBurned();
}
