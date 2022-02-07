package pt.iul.poo.firefight.starterpack;
import java.util.List;

//Interface que implementa as classes que ser�o atualizadas a todas as jogadas
public interface Updatable {
	
	//Procedimento que � realizado em todas as classes que implementem esta interface, de maneira a que,
	// a cada jogada, sejam sempre atualizadas
	public void update();
	
	//Procedimento que permite atualizar todas as classes que implementam a interface 'Updatable'
	static void updateAll(List<Updatable> updatables) {
		updatables.forEach(u -> u.update());
	}
}
