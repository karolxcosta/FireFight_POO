package pt.iul.poo.firefight.starterpack;
import pt.iul.ista.poo.utils.Point2D;

//Classe Plane que implementa o movimento do avi�o
public class Plane extends GameElement implements Updatable{
	
	//Atributos que definem o n�mero de jogadas desde o avi�o ser criado at� ser removido do jogo
	// (+1 para n�o avan�ar logo ap�s a primeira jogada), e a posi��o do mesmo, respetivamente
	private int numMovesPlane=GameEngine.GRID_HEIGHT/2+1;
    private Point2D newPosition;
	
	public Plane(Point2D position) {
		super(position);
		
		//De maneira a extinguir o fogo na primeira posi��o por onde passa o avi�o, � chamada a fun��o 
		// extinguishFireWithPlane() logo ap�s a sua cria��o
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
    	
    	//� calculada a posi��o que (em princ�pio) o avi�o ter� na pr�xima jogada
        newPosition= new Point2D(getPosition().getX(), getPosition().getY()-2);
        
        //Como o update() das classes � feito ap�s a chamada do plane, � necess�rio 
        // adicionar um movimento ao plane, de modo a este n�o alterar a sua posi��o 2 vezes na mesma jogada
        //Se o n�mero de jogadas que faltam n�o for 0 nem o n�mero inicial definido, ent�o este ir� para a posi��o definida anteriormente
        if (numMovesPlane>0 && numMovesPlane<GameEngine.GRID_HEIGHT/2+1) {
            if (canMoveTo(newPosition)) {
                setPosition(newPosition);
            }else {
            	
            	//Se o n�mero de jogadas do avi�o for 0, ir� apagar o fogo (se houver) na posi��o a seguir (�ltima posi��o)
            	extinguishFireWithPlane(new Point2D (getPosition().getX(), getPosition().getY()-1));
            }
            
            //De qualquer das maneiras, este ir� apagar o fogo (se houver) na posi��o antes da sua e na posi��o atual
            extinguishFireWithPlane(new Point2D (getPosition().getX(), getPosition().getY()+1));
        	extinguishFireWithPlane(getPosition());
        }
        
        //O n�mero de jogadas do avi�o ir� ent�o decrementar
        numMovesPlane--;
    }
    
    //Por todas as posi��es onde o avi�o passa, este ir� verificar se existe fogo na posi��o em quest�o, e se sim, apag�-lo
    public void extinguishFireWithPlane(Point2D position) {
    	if (GameEngine.getInstance().isFireAtPosition(position)) {
    		GameEngine.getInstance().extinguishFires(position, 0);
    	}
    }
    
    //Como o m�todo update() da classe Plane ocorre primeiro que o da classe Fireman onde s�o removidos os avi�es,
    // ao retornar a fun��o asFinished() quando o atributo 'numMovesPlane' for 1, � assegurado que o avi�o 
    // � removido na jogada anterior, logo n�o ficar� uma jogada extra � espera de ser removido.
    public boolean asFinished() {
    	return numMovesPlane==1;
    }
}
