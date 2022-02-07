package pt.iul.poo.firefight.starterpack;

//Classe Score que guarda o nome e a pontua��o de um jogador num determinado n�vel/jogo 
public class Score implements Updatable{

	private String playerName;
	private int playerScore;
	
	public Score(String playerName, int playerScore) {
		this.playerName=playerName;
		this.playerScore=playerScore;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	public int getPlayerScore() {
		return playerScore;
	}

	//Atualiza a pontua��o do jogador
	//Para cada Burnable incendiado s�o decrescidos 50 pontos da sua pontua��o
	//Para cada fogo extinto s�o acrescidos 150 pontos da sua pontua��o
	public void update() {
		playerScore=-50*GameEngine.getInstance().getNumberOfBurnablesBurned()+150*GameEngine.getInstance().getNumberOfExtinguishFires();
	}
	
	public String toString() {
		return "PlayerName: " + playerName + " - Score: " + playerScore;
	}
}
