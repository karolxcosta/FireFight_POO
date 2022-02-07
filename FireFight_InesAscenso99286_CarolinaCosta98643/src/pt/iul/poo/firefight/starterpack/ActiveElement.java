package pt.iul.poo.firefight.starterpack;

//Interface que determina qual o elemento ativo (ou seja, que o utilizador est� a controlar no momento)
public interface ActiveElement {
	
	//Verificar se um elemento, que implemente a interface em quest�o, est� ativo e proceder � mudan�a do 
	// seu estado de ativa��o, respetivamente
	public boolean isActiveElement();
	public void changeStateOfActivation();
}
