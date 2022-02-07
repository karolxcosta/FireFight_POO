package pt.iul.poo.firefight.starterpack;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class ScoreBoard {
	
	ArrayList<Score> scores = new ArrayList<Score>();
	File scoreBoardFile;
	
	//Classe aninhada privada que implementa um comparador 	
	private class ScoreCompare implements Comparator<Score>{ 
		//M�todo que compara duas pontua��es, de forma a orden�-las por ordem decrescente
		public int compare(Score a, Score b) {
			return b.getPlayerScore() - a.getPlayerScore();
		}
	}
		
	public void updateScoreBoard() {
		//Esvazia a lista das pontua��es
		scores.clear();
		try{
			//L� o ficheiro onde est�o as melhores pontua��es do n�vel atual
			scoreBoardFile=new File("ScoreBoard"+ GameEngine.getInstance().getLevel() +".txt");
			Scanner scanner = new Scanner(scoreBoardFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] playerScore = line.split(" ");
				System.lineSeparator(); 
				//Atualiza a lista de pontua��es com as pontua��es lidas no ficheiro
				scores.add(new Score(playerScore[1], Integer.parseInt(playerScore[4])));
			}
			scanner.close();
			//Se n�o existir o ficheiro, a lista das pontua��es ser� esvaziada
		}catch(FileNotFoundException e){
			System.err.println("N�o foi poss�vel abrir o ficheiro " + scoreBoardFile );
			scores.clear();
		}
		
		//Adiciona o novo Score � lista das pontua��es
		scores.add(GameEngine.getInstance().getScore());
		//Ordena a lista de Scores por ordem decrescente
		Comparator<Score> comp = new ScoreCompare();
		scores.sort(comp);
		
		//Se o numero de pontua��es for superior a 5, ser� removida uma da lista (a pontua��o mais baixa)
		if (scores.size()> 5) {
			scores.remove(scores.size()-1);
		}
		
		//Escreve no ficheiro relativamente ao n�vel atual as 5 melhores pontua��es
		try {
			PrintWriter printWriter = new PrintWriter(scoreBoardFile);
			for(Score score : scores) {
				printWriter.write(score.toString()+System.lineSeparator());
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			System.err.println("N�o foi poss�vel escrever no ficheiro " + scoreBoardFile );
		}
		 
		String boardScores="";
		//Agrupa todos as pontua��es numa string
		for(Score score : scores) {
			boardScores = boardScores.concat((score.toString() + "\n"));
		}
		//� disponibilizada a lista das 5 melhores pontua��es do n�vel ao utilizador
		JOptionPane.showMessageDialog(null, "Melhores pontua��es: " + System.lineSeparator()+ boardScores);
	}
}
