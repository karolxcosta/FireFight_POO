package pt.iul.poo.firefight.starterpack;

//Interface que determina qual o elemento ativo (ou seja, que o utilizador está a controlar no momento)
public interface ActiveElement {
	
	//Verificar se um elemento, que implemente a interface em questão, está ativo e proceder à mudança do 
	// seu estado de ativação, respetivamente
	public boolean isActiveElement();
	public void changeStateOfActivation();
}
