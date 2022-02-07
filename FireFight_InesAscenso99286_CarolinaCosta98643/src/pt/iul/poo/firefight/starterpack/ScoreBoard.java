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
		//Método que compara duas pontuações, de forma a ordená-las por ordem decrescente
		public int compare(Score a, Score b) {
			return b.getPlayerScore() - a.getPlayerScore();
		}
	}
		
	public void updateScoreBoard() {
		//Esvazia a lista das pontuações
		scores.clear();
		try{
			//Lê o ficheiro onde estão as melhores pontuações do nível atual
			scoreBoardFile=new File("ScoreBoard"+ GameEngine.getInstance().getLevel() +".txt");
			Scanner scanner = new Scanner(scoreBoardFile);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] playerScore = line.split(" ");
				System.lineSeparator(); 
				//Atualiza a lista de pontuações com as pontuações lidas no ficheiro
				scores.add(new Score(playerScore[1], Integer.parseInt(playerScore[4])));
			}
			scanner.close();
			//Se não existir o ficheiro, a lista das pontuações será esvaziada
		}catch(FileNotFoundException e){
			System.err.println("Não foi possível abrir o ficheiro " + scoreBoardFile );
			scores.clear();
		}
		
		//Adiciona o novo Score à lista das pontuações
		scores.add(GameEngine.getInstance().getScore());
		//Ordena a lista de Scores por ordem decrescente
		Comparator<Score> comp = new ScoreCompare();
		scores.sort(comp);
		
		//Se o numero de pontuações for superior a 5, será removida uma da lista (a pontuação mais baixa)
		if (scores.size()> 5) {
			scores.remove(scores.size()-1);
		}
		
		//Escreve no ficheiro relativamente ao nível atual as 5 melhores pontuações
		try {
			PrintWriter printWriter = new PrintWriter(scoreBoardFile);
			for(Score score : scores) {
				printWriter.write(score.toString()+System.lineSeparator());
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			System.err.println("Não foi possível escrever no ficheiro " + scoreBoardFile );
		}
		 
		String boardScores="";
		//Agrupa todos as pontuações numa string
		for(Score score : scores) {
			boardScores = boardScores.concat((score.toString() + "\n"));
		}
		//É disponibilizada a lista das 5 melhores pontuações do nível ao utilizador
		JOptionPane.showMessageDialog(null, "Melhores pontuações: " + System.lineSeparator()+ boardScores);
	}
}
