package pt.iul.poo.firefight.starterpack;

//Classe Score que guarda o nome e a pontuação de um jogador num determinado nível/jogo 
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

	//Atualiza a pontuação do jogador
	//Para cada Burnable incendiado são decrescidos 50 pontos da sua pontuação
	//Para cada fogo extinto são acrescidos 150 pontos da sua pontuação
	public void update() {
		playerScore=-50*GameEngine.getInstance().getNumberOfBurnablesBurned()+150*GameEngine.getInstance().getNumberOfExtinguishFires();
	}
	
	public String toString() {
		return "PlayerName: " + playerName + " - Score: " + playerScore;
	}
}
