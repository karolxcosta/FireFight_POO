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

//Classe GameEngine que implementa a inicialização do jogo, bem como a sua atualização connstante
public class GameEngine implements Observer {	

	public static GameEngine INSTANCE;

	// Dimensões da grelha de jogo
	public static final int GRID_HEIGHT = 10;
	public static final int GRID_WIDTH = 10;

	private String playerName;        						 //Referência para o nome do jogador
	private int numFiresExtinguish=0;  						 //Referência para a pontuação derivada do número de fogos apagados
	private int level=1;      								 //Referência para o nível de jogo (de 1 a 3)
	private ImageMatrixGUI gui;  							 //Referência para ImageMatrixGUI (janela de interface com o utilizador) 
	private List<ImageTile> tileList;						 //Referência para a lista de imagens no jogo
	private Fireman fireman=null;			  				 //Referência para o bombeiro
	private List<Bulldozer> bulldozers=new ArrayList();      //Referência para a lista dos bulldozers
	private List<Plane> planes=new ArrayList();				 //Referência para a lista dos aviões
	private Score score;     								 //Referência para a pontuação do jogador
	private ScoreBoard scoreBoard=new ScoreBoard(); 	     //Referência para o scoreBoard

	//Setup inicial da janela
	private GameEngine() {

		//Enquanto não for dado o nome do jogador, é reposta a janela
		playerName=JOptionPane.showInputDialog("Enter player name:");

		//Se a janela for fechada, o programa não irá ser inicializado
		if (playerName==null) 
			System.exit(0);
		while (playerName.isEmpty()) {
			playerName=JOptionPane.showInputDialog("Enter player name:");
			if (playerName==null) 
				System.exit(0);
		}

		String regras= "Para movimentar o bombeiro utilize as teclas direcionais.\n";
		regras=regras.concat("Para chamar um avião que apague os fogos, carregue na tecla 'P'.    \n");
		regras=regras.concat("Para entrar no bulldozer, vá até à sua posição.\n");
		regras=regras.concat("Para sair do bulldozer, carregue na tecla 'ENTER'.\n");
		regras=regras.concat("Para recomeçar de novo um nível, carregue na tecla 'R'.\n");
		regras=regras.concat("                                           Boa sorte!! ");

		//Janela com as regras do jogo
		infoBox(regras, "Regras do jogo");
		//É criada uma nova pontuação para o jogador
		score= new Score(playerName, 0);

		gui = ImageMatrixGUI.getInstance();    // 1. Obter instância ativa de ImageMatrixGUI	
		gui.setSize(GRID_HEIGHT, GRID_WIDTH);  // 2. Configurar as dimensões 
		gui.registerObserver(this);            // 3. Registar o elemento ativo GameEngine como observador da GUI
		gui.go();                              // 4. Lançar a GUI

		tileList = new ArrayList<>();   
	}

	//Garante que a classe solitão só é instanciada no máximo uma única vez
	public static GameEngine getInstance() {
		if (INSTANCE==null)
			INSTANCE = new GameEngine();
		return INSTANCE;
	}

	//Criação dos elementos e envio das imagens para a GUI
	public void start() {
		createTerrainFromFile();
		statusMessage();
		gui.update();
	}

	//Informações presentes na janela
	public void statusMessage() {
		ImageMatrixGUI.getInstance().setStatusMessage("PlayerName: " + playerName + "   Level: " + level + "   Score: " + score.getPlayerScore());
	}

	//Cria uma janela de informação
	public void infoBox(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
	}

	//O método update() é invocado sempre que o utilizador carrega numa tecla
	//No argumento do método é passada um referência para o objeto observado (neste caso seria a GUI)
	@Override
	public void update(Observed source) {
		int key = ((ImageMatrixGUI) source).keyPressed();

		//Se o jogador carregou numa das teclas direcionais, o elemento ativo irá mover-se
		if(Direction.isDirection(key))
			fireman.moveFiremanOrBulldozer(key);

		//Se o jogador carregou na tecla 'P', irá ser chamado um novo avião
		else if(key == KeyEvent.VK_P)
			fireman.callPlane();

		//Se o jogador carregou na tecla 'ENTER', se o bombeiro estiver dentro de um bulldozer, este irá sair do mesmo
		else if(key == KeyEvent.VK_ENTER)
			fireman.leaveBulldozer();

		//Se o jogador carregou na tecla 'R', o nível irá recomeçar novamente
		else if (key==KeyEvent.VK_R)
			restartLevel();

		//É feito o update() de todos os elementos na lista retornada
		Updatable.updateAll(getUpdatableObjects());

		//São atualizadas as informações no cimo da janela do jogo
		statusMessage();

		//Se não houverem fogos ativos, o nível irá ser acabado
		if (getNumberActiveFires()==0)
			levelOver();
	}

	//Quando o bombeiro entra e sai de um bulldozer, este irá mudar a sua visualização na janela, ou seja, 
	// este poderá desaparececer ou voltar a aparecer consoante quem esteja ativo (bombeiro ou bulldozers)
	public void changeFiremanAppearance() {
		if (fireman.isActiveElement()) 
			gui.addImage(fireman);
		if (!fireman.isActiveElement())
			gui.removeImage(fireman);
	}

	//Procedimento que extingue o fogo numa dada posição
	public void extinguishFires(Point2D position, int key) {
		Water water=new Water(position);

		//Se nenhum avião estiver na posição em questão (mas sim o bombeiro), a direção da água irá mudar
		if (!isPlaneAtPosition(position)) 
			water.changeDirection(key);
		//O número de fogos extintos irá incrementar
		numFiresExtinguish++;
		addImage(water);
		wait(500);
		removeFireAtPosition(position);
		removeImage(water);
	}

	//Procedimento que faz uma espera relativamente à próxima jogada
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

	//Verifica se existe algum fogo na posição dada
	public boolean isFireAtPosition(Point2D pos) {
		for (Fire fire : getAllFires())
			if (fire.getPosition().equals(pos))
				return true;
		return false;
	}

	//Verifica se existe um avião na posição dada
	public boolean isPlaneAtPosition(Point2D position) {
		for (Plane plane : getAllPlanes())
			if (plane.getPosition().equals(position))
				return true;
		return false;
	}

	//Verifica se existe um bulldozer na posição dada
	public boolean isBulldozerAtPosition(Point2D position) {
		for (Bulldozer bulldozer : bulldozers)
			if (bulldozer.getPosition().equals(position))
				return true;
		return false;
	}

	//Retorna o Burnable que se encontra na posição dada
	public Burnable getBurnableAtPosition(Point2D pos) {
		for (ImageTile obj : tileList ) {
			if (obj instanceof Burnable && obj.getPosition().equals(pos)) 
				return ((Burnable) obj);
		}
		return null;
	}

	//Retorna o número de Burnables queimados
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

	//Retorna o número de fogos extintos
	public int getNumberOfExtinguishFires() {
		return numFiresExtinguish;
	}

	//Retorna o nível atual do jogo
	public int getLevel() {
		return level;
	}

	//Retorna a pontuação atual do jogador no nível
	public Score getScore() {
		return score;
	}

	//Retorna a lista dos bulldozers no jogo
	public List<Bulldozer> getAllBulldozers(){
		return bulldozers;
	}

	//Retorna a lista dos aviões no jogo
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

	//Retorna o número de fogos ativos 
	public int getNumberActiveFires() {
		int count=0;
		for(Fire fire : getAllFires())
			count++;
		return count;
	}

	//Retorna o elemento ativo no momento, se é o bombeiro ou um bulldozer
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

	//Remover o fogo na posição dada
	public void removeFireAtPosition(Point2D pos) {
		for (Fire fire : getAllFires()) {
			if (fire.getPosition().equals(pos))
				removeImage(fire);
		}
	}

	//Criação de um terreno a partir de um ficheiro	
	private void createTerrainFromFile () {
		//Nome do ficheiro é constituído pelo nível em questão
		String nameFile="level" + Integer.toString(level) + ".txt";
		try {
			Scanner terrain = new Scanner(new File(nameFile));
			int numLine=0;
			while(terrain.hasNext()) {
				//Enquanto o número da coluna for inferior ao número total de colunas, irá ser adicionado um terreno
				if (numLine<GRID_HEIGHT) {
					String line = terrain.nextLine();
					getImageFromChar(line, numLine);
					numLine++;
					//Se o número da coluna for igual ou superior ao número total de colunas, irá ser adicionada uma figura
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

			//No caso de o número de colunas do ficheiro ser inferior ao número de colunas do jogo, ocorre um erro
			// e o jogo não é inicializado 
			if (numLine<GRID_HEIGHT) {
				System.err.println("Não é possível começar um jogo sem a representação de todas as posições no ficheiro");
				System.exit(0);
			}

			//No caso de não existir nenhum Fireman (necessário para a realização do jogo),
			// ocorre um erro e o jogo não é inicializado
			if (fireman==null) {
				System.err.println("Não é possível começar um jogo sem um Fireman");
				System.exit(0);
			}
			sendImagesToGUI();

		} catch (FileNotFoundException e) {
			System.err.println("Erro na abertura do ficheiro " + nameFile);
		}
	}

	//Será adicionado à lista um novo terreno, consoante um caracter que o defina. No caso de o caracter não ser um dos disponibilizado, 
	// por default, será adicionado um novo terreno sem vegetação
	public void getImageFromChar(String line, int numLine) {
		//i será o número da coluna
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

	//Será adicionado à lista um novo elemento, consoante uma palavra que o defina
	public void createNewFigure(String name, int x, int y) {
		switch (name) {
		case("Fireman"): 
			//Se no ficheiro estiver mais que um bombeiro, apenas o primeiro irá ser adicionado
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

	//Procedimento que trata o que acontece no final do cada nível
	public void levelOver() {

		//Se o nível acabado for inferior a 3, será inicializado o nível seguinte
		if (level<3) {
			infoBox("You just finished the level, carry on.", "Congratulations!");
			//Os ficheiros com as melhores pontuações do nível serão ataulizados e estas serão desponibilizadas ao jogador
			scoreBoard.updateScoreBoard();
			//O nível de jogo irá aumentar
			level++;
			startAnotherLevel();

			//Se o nível acabado for igual a 3, então os níveis foram todos passados e o programa irá terminar
		} else {
			infoBox("You just won the game!", "Congratulations!");
			//Os ficheiros com as melhores pontuações do nível serão atualizados e estas serão disponibilizadas ao jogador
			scoreBoard.updateScoreBoard();
			System.exit(0);
		}
	}

	//Procedimento que implementa todas as mudanças de maneira a ser inicializado um novo nível do jogo
	//As listas são esvaziadas e os atributos são inicializados de novo
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

	//Procedimento que deixa o jogador recomeçar o nível onde se encontra
	public void restartLevel() {
		startAnotherLevel();
	}

	// Envio das imagens para a GUI  
	private void sendImagesToGUI() {
		gui.addImages(tileList);
	}
}