package pt.iul.poo.firefight.starterpack;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;
import pt.iul.ista.poo.gui.ImageMatrixGUI;
import pt.iul.ista.poo.gui.ImageTile;
import pt.iul.ista.poo.observer.Observed;
import pt.iul.ista.poo.observer.Observer;
import pt.iul.ista.poo.utils.Direction;
import pt.iul.ista.poo.utils.Point2D;

//Classe GameEngine que implementa a inicializa��o do jogo, bem como a sua atualiza��o connstante
public class GameEngine implements Observer {	

	public static GameEngine INSTANCE;

	// Dimens�es da grelha de jogo
	public static final int GRID_HEIGHT = 10;
	public static final int GRID_WIDTH = 10;

	private String playerName;        						 //Refer�ncia para o nome do jogador
	private int numFiresExtinguish=0;  						 //Refer�ncia para a pontua��o derivada do n�mero de fogos apagados
	private int level=1;      								 //Refer�ncia para o n�vel de jogo (de 1 a 3)
	private ImageMatrixGUI gui;  							 //Refer�ncia para ImageMatrixGUI (janela de interface com o utilizador) 
	private List<ImageTile> tileList;						 //Refer�ncia para a lista de imagens no jogo
	private Fireman fireman=null;			  				 //Refer�ncia para o bombeiro
	private List<Bulldozer> bulldozers=new ArrayList();      //Refer�ncia para a lista dos bulldozers
	private List<Plane> planes=new ArrayList();				 //Refer�ncia para a lista dos avi�es
	private Score score;     								 //Refer�ncia para a pontua��o do jogador
	private ScoreBoard scoreBoard=new ScoreBoard(); 	     //Refer�ncia para o scoreBoard

	//Setup inicial da janela
	private GameEngine() {

		//Enquanto n�o for dado o nome do jogador, � reposta a janela
		playerName=JOptionPane.showInputDialog("Enter player name:");

		//Se a janela for fechada, o programa n�o ir� ser inicializado
		if (playerName==null) 
			System.exit(0);
		while (playerName.isEmpty()) {
			playerName=JOptionPane.showInputDialog("Enter player name:");
			if (playerName==null) 
				System.exit(0);
		}

		String regras= "Para movimentar o bombeiro utilize as teclas direcionais.\n";
		regras=regras.concat("Para chamar um avi�o que apague os fogos, carregue na tecla 'P'.    \n");
		regras=regras.concat("Para entrar no bulldozer, v� at� � sua posi��o.\n");
		regras=regras.concat("Para sair do bulldozer, carregue na tecla 'ENTER'.\n");
		regras=regras.concat("Para recome�ar de novo um n�vel, carregue na tecla 'R'.\n");
		regras=regras.concat("                                           Boa sorte!! ");

		//Janela com as regras do jogo
		infoBox(regras, "Regras do jogo");
		//� criada uma nova pontua��o para o jogador
		score= new Score(playerName, 0);

		gui = ImageMatrixGUI.getInstance();    // 1. Obter inst�ncia ativa de ImageMatrixGUI	
		gui.setSize(GRID_HEIGHT, GRID_WIDTH);  // 2. Configurar as dimens�es 
		gui.registerObserver(this);            // 3. Registar o elemento ativo GameEngine como observador da GUI
		gui.go();                              // 4. Lan�ar a GUI

		tileList = new ArrayList<>();   
	}

	//Garante que a classe solit�o s� � instanciada no m�ximo uma �nica vez
	public static GameEngine getInstance() {
		if (INSTANCE==null)
			INSTANCE = new GameEngine();
		return INSTANCE;
	}

	//Cria��o dos elementos e envio das imagens para a GUI
	public void start() {
		createTerrainFromFile();
		statusMessage();
		gui.update();
	}

	//Informa��es presentes na janela
	public void statusMessage() {
		ImageMatrixGUI.getInstance().setStatusMessage("PlayerName: " + playerName + "   Level: " + level + "   Score: " + score.getPlayerScore());
	}

	//Cria uma janela de informa��o
	public void infoBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
	}

	//O m�todo update() � invocado sempre que o utilizador carrega numa tecla
	//No argumento do m�todo � passada um refer�ncia para o objeto observado (neste caso seria a GUI)
	@Override
	public void update(Observed source) {
		int key = ((ImageMatrixGUI) source).keyPressed();

		//Se o jogador carregou numa das teclas direcionais, o elemento ativo ir� mover-se
		if(Direction.isDirection(key))
			fireman.moveFiremanOrBulldozer(key);

		//Se o jogador carregou na tecla 'P', ir� ser chamado um novo avi�o
		else if(key == KeyEvent.VK_P)
			fireman.callPlane();

		//Se o jogador carregou na tecla 'ENTER', se o bombeiro estiver dentro de um bulldozer, este ir� sair do mesmo
		else if(key == KeyEvent.VK_ENTER)
			fireman.leaveBulldozer();

		//Se o jogador carregou na tecla 'R', o n�vel ir� recome�ar novamente
		else if (key==KeyEvent.VK_R)
			restartLevel();

		//� feito o update() de todos os elementos na lista retornada
		Updatable.updateAll(getUpdatableObjects());

		//S�o atualizadas as informa��es no cimo da janela do jogo
		statusMessage();

		//Se n�o houverem fogos ativos, o n�vel ir� ser acabado
		if (getNumberActiveFires()==0)
			levelOver();
	}

	//Quando o bombeiro entra e sai de um bulldozer, este ir� mudar a sua visualiza��o na janela, ou seja, 
	// este poder� desaparececer ou voltar a aparecer consoante quem esteja ativo (bombeiro ou bulldozers)
	public void changeFiremanAppearance() {
		if (fireman.isActiveElement()) 
			gui.addImage(fireman);
		if (!fireman.isActiveElement())
			gui.removeImage(fireman);
	}

	//Procedimento que extingue o fogo numa dada posi��o
	public void extinguishFires(Point2D position, int key) {
		Water water=new Water(position);

		//Se nenhum avi�o estiver na posi��o em quest�o (mas sim o bombeiro), a dire��o da �gua ir� mudar
		if (!isPlaneAtPosition(position)) 
			water.changeDirection(key);
		//O n�mero de fogos extintos ir� incrementar
		numFiresExtinguish++;
		addImage(water);
		wait(500);
		removeFireAtPosition(position);
		removeImage(water);
	}

	//Procedimento que faz uma espera relativamente � pr�xima jogada
	public void wait(int ms){
		try{
			Thread.sleep(ms);
		} catch(InterruptedException ex){
			Thread.currentThread().interrupt();
		}
	}

	//Adiciona um novo elemento ao jogo
	public void addImage(ImageTile img) {
		tileList.add(img);
		gui.addImage(img);

	}

	//Remove um novo elemento do jogo
	public void removeImage(ImageTile img) {
		tileList.remove(img);
		gui.removeImage(img);

	}

	//Verifica se existe algum fogo na posi��o dada
	public boolean isFireAtPosition(Point2D pos) {
		for (Fire fire : getAllFires())
			if (fire.getPosition().equals(pos))
				return true;
		return false;
	}

	//Verifica se existe um avi�o na posi��o dada
	public boolean isPlaneAtPosition(Point2D position) {
		for (Plane plane : getAllPlanes())
			if (plane.getPosition().equals(position))
				return true;
		return false;
	}

	//Verifica se existe um bulldozer na posi��o dada
	public boolean isBulldozerAtPosition(Point2D position) {
		for (Bulldozer bulldozer : bulldozers)
			if (bulldozer.getPosition().equals(position))
				return true;
		return false;
	}

	//Retorna o Burnable que se encontra na posi��o dada
	public Burnable getBurnableAtPosition(Point2D pos) {
		for (ImageTile obj : tileList ) {
			if (obj instanceof Burnable && obj.getPosition().equals(pos)) 
				return ((Burnable) obj);
		}
		return null;
	}

	//Retorna o n�mero de Burnables queimados
	public int getNumberOfBurnablesBurned() {
		int count=0;
		for (ImageTile obj : tileList ) {
			if (obj instanceof Burnable) {
				Burnable bur= ((Burnable) obj);
				if (bur.isBurned())
					count++;
			}
		}
		return count;
	}

	//Retorna o n�mero de fogos extintos
	public int getNumberOfExtinguishFires() {
		return numFiresExtinguish;
	}

	//Retorna o n�vel atual do jogo
	public int getLevel() {
		return level;
	}

	//Retorna a pontua��o atual do jogador no n�vel
	public Score getScore() {
		return score;
	}

	//Retorna a lista dos bulldozers no jogo
	public List<Bulldozer> getAllBulldozers(){
		return bulldozers;
	}

	//Retorna a lista dos avi�es no jogo
	public List<Plane> getAllPlanes(){
		return planes;
	}

	//Retorna a lista de fogos no jogo
	public List<Fire> getAllFires() {
		List <Fire> fireList= new ArrayList(); 
		for (ImageTile obj : tileList) {
			if (obj instanceof Fire) {
				fireList.add((Fire) obj);
			}
		}
		return fireList;
	}

	//Retorna a lista dos elementos que implementam a interface 'Updatable' no jogo, de forma a serem atualizados posteriormente
	public List<Updatable> getUpdatableObjects(){
		List<Updatable> updatableList = new ArrayList();
		for (ImageTile obj : tileList ) {
			if (obj instanceof Updatable) 
				updatableList.add((Updatable) obj);
		}
		updatableList.add(score);
		return updatableList;
	}

	//Retorna o n�mero de fogos ativos 
	public int getNumberActiveFires() {
		int count=0;
		for(Fire fire : getAllFires())
			count++;
		return count;
	}

	//Retorna o elemento ativo no momento, se � o bombeiro ou um bulldozer
	public GameElement getActiveElement() {
		if (getActiveBulldozer()!=null)
			return ((GameElement) getActiveBulldozer());
		return ((GameElement) fireman);
	}

	//Retorna o bulldozer ativo no momento
	public Bulldozer getActiveBulldozer() {
		for (Bulldozer bulldozer : GameEngine.getInstance().getAllBulldozers()) {
			if (bulldozer.isActiveElement()){  
				return bulldozer;
			}
		}
		return null;
	}

	//Remover o fogo na posi��o dada
	public void removeFireAtPosition(Point2D pos) {
		for (Fire fire : getAllFires()) {
			if (fire.getPosition().equals(pos))
				removeImage(fire);
		}
	}

	//Cria��o de um terreno a partir de um ficheiro	
	private void createTerrainFromFile () {
		//Nome do ficheiro � constitu�do pelo n�vel em quest�o
		String nameFile="level" + Integer.toString(level) + ".txt";
		try {
			Scanner terrain = new Scanner(new File(nameFile));
			int numLine=0;
			while(terrain.hasNext()) {
				//Enquanto o n�mero da coluna for inferior ao n�mero total de colunas, ir� ser adicionado um terreno
				if (numLine<GRID_HEIGHT) {
					String line = terrain.nextLine();
					getImageFromChar(line, numLine);
					numLine++;
					//Se o n�mero da coluna for igual ou superior ao n�mero total de colunas, ir� ser adicionada uma figura
				} else {
					String [] aux=terrain.nextLine().split(" ");
					String name=aux[0];
					//Linha e coluna a adicionar a figura
					int x=Integer.parseInt(aux[1]);
					int y=Integer.parseInt(aux[2]);
					createNewFigure(name, x, y);
					numLine++;
				}
			}
			terrain.close();

			//No caso de o n�mero de colunas do ficheiro ser inferior ao n�mero de colunas do jogo, ocorre um erro
			// e o jogo n�o � inicializado 
			if (numLine<GRID_HEIGHT) {
				System.err.println("N�o � poss�vel come�ar um jogo sem a representa��o de todas as posi��es no ficheiro");
				System.exit(0);
			}

			//No caso de n�o existir nenhum Fireman (necess�rio para a realiza��o do jogo),
			// ocorre um erro e o jogo n�o � inicializado
			if (fireman==null) {
				System.err.println("N�o � poss�vel come�ar um jogo sem um Fireman");
				System.exit(0);
			}
			sendImagesToGUI();

		} catch (FileNotFoundException e) {
			System.err.println("Erro na abertura do ficheiro " + nameFile);
		}
	}

	//Ser� adicionado � lista um novo terreno, consoante um caracter que o defina. No caso de o caracter n�o ser um dos disponibilizado, 
	// por default, ser� adicionado um novo terreno sem vegeta��o
	public void getImageFromChar(String line, int numLine) {
		//i ser� o n�mero da coluna
		for (int i=0; i<line.length(); i++) {
			switch (line.charAt(i)){
			case 'p': tileList.add(new Pine(new Point2D(i, numLine))); 
			break;
			case '_': tileList.add(new Land(new Point2D(i, numLine)));
			break;
			case 'e': tileList.add(new Eucaliptus(new Point2D(i, numLine)));
			break;
			case 'm': tileList.add(new Grass(new Point2D(i, numLine)));
			break;
			default: tileList.add(new Land(new Point2D(i, numLine)));
			}
		}
	}

	//Ser� adicionado � lista um novo elemento, consoante uma palavra que o defina
	public void createNewFigure(String name, int x, int y) {
		switch (name) {
		case("Fireman"): 
			//Se no ficheiro estiver mais que um bombeiro, apenas o primeiro ir� ser adicionado
			if (fireman==null) {
				fireman=new Fireman(new Point2D(x,y));
				tileList.add(fireman);
			}
		break;
		case("Bulldozer"): Bulldozer bul = new Bulldozer(new Point2D(x,y));
		tileList.add(bul);
		bulldozers.add(bul);
		break;
		case("Fire"): tileList.add(new Fire (new Point2D(x,y)));
		break;

		default: return;
		}
	}

	//Procedimento que trata o que acontece no final do cada n�vel
	public void levelOver() {

		//Se o n�vel acabado for inferior a 3, ser� inicializado o n�vel seguinte
		if (level<3) {
			infoBox("You just finished the level, carry on.", "Congratulations!");
			//Os ficheiros com as melhores pontua��es do n�vel ser�o ataulizados e estas ser�o desponibilizadas ao jogador
			scoreBoard.updateScoreBoard();
			//O n�vel de jogo ir� aumentar
			level++;
			startAnotherLevel();

			//Se o n�vel acabado for igual a 3, ent�o os n�veis foram todos passados e o programa ir� terminar
		} else {
			infoBox("You just won the game!", "Congratulations!");
			//Os ficheiros com as melhores pontua��es do n�vel ser�o atualizados e estas ser�o disponibilizadas ao jogador
			scoreBoard.updateScoreBoard();
			System.exit(0);
		}
	}

	//Procedimento que implementa todas as mudan�as de maneira a ser inicializado um novo n�vel do jogo
	//As listas s�o esvaziadas e os atributos s�o inicializados de novo
	public void startAnotherLevel() {
		numFiresExtinguish=0;
		bulldozers.clear();
		planes.clear();
		tileList.clear();
		gui.clearImages();
		fireman=null;
		statusMessage();
		createTerrainFromFile();
		gui.update();
	}

	//Procedimento que deixa o jogador recome�ar o n�vel onde se encontra
	public void restartLevel() {
		startAnotherLevel();
	}

	// Envio das imagens para a GUI  
	private void sendImagesToGUI() {
		gui.addImages(tileList);
	}
}