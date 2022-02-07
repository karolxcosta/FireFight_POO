package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.utils.Point2D;

//Classe Plane que implementa o movimento do avião
public class Plane extends GameElement implements Updatable{
	
	//Atributos que definem o número de jogadas desde o avião ser criado até ser removido do jogo
	// (+1 para não avançar logo após a primeira jogada), e a posição do mesmo, respetivamente
	private int numMovesPlane=GameEngine.GRID_HEIGHT/2+1;
    private Point2D newPosition;
	
	public Plane(Point2D position) {
		super(position);
		
		//De maneira a extinguir o fogo na primeira posição por onde passa o avião, é chamada a função 
		// extinguishFireWithPlane() logo após a sua criação
		extinguishFireWithPlane(getPosition());
	}
	
	@Override
	public String getName() {
		return "plane";
	}

    @Override
	public int getLayer() {
		return 4;
	}
    
    @Override
    public void update() {
    	
    	//É calculada a posição que (em princípio) o avião terá na próxima jogada
        newPosition= new Point2D(getPosition().getX(), getPosition().getY()-2);
        
        //Como o update() das classes é feito após a chamada do plane, é necessário 
        // adicionar um movimento ao plane, de modo a este não alterar a sua posição 2 vezes na mesma jogada
        //Se o número de jogadas que faltam não for 0 nem o número inicial definido, então este irá para a posição definida anteriormente
        if (numMovesPlane>0 && numMovesPlane<GameEngine.GRID_HEIGHT/2+1) {
            if (canMoveTo(newPosition)) {
                setPosition(newPosition);
            }else {
            	
            	//Se o número de jogadas do avião for 0, irá apagar o fogo (se houver) na posição a seguir (última posição)
            	extinguishFireWithPlane(new Point2D (getPosition().getX(), getPosition().getY()-1));
            }
            
            //De qualquer das maneiras, este irá apagar o fogo (se houver) na posição antes da sua e na posição atual
            extinguishFireWithPlane(new Point2D (getPosition().getX(), getPosition().getY()+1));
        	extinguishFireWithPlane(getPosition());
        }
        
        //O número de jogadas do avião irá então decrementar
        numMovesPlane--;
    }
    
    //Por todas as posições onde o avião passa, este irá verificar se existe fogo na posição em questão, e se sim, apagá-lo
    public void extinguishFireWithPlane(Point2D position) {
    	if (GameEngine.getInstance().isFireAtPosition(position)) {
    		GameEngine.getInstance().extinguishFires(position, 0);
    	}
    }
    
    //Como o método update() da classe Plane ocorre primeiro que o da classe Fireman onde são removidos os aviões,
    // ao retornar a função asFinished() quando o atributo 'numMovesPlane' for 1, é assegurado que o avião 
    // é removido na jogada anterior, logo não ficará uma jogada extra à espera de ser removido.
    public boolean asFinished() {
    	return numMovesPlane==1;
    }
}
