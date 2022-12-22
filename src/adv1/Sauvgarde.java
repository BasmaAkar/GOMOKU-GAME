package adv1;
import java.util.Date;

//cette class est utiliser pour Sauvgarder une partie 
public class Sauvgarde {
	public Sauvgarde() {
		super();
		
		this.position = new GomokuPosition();
	}
	
	//variable pour connaitre est-ce-que la partie est entre Homme et Homme ou entre Homme et Machine
	boolean humanVsHuman;
	GomokuPosition position;
	//variable pour connaitre qui a le role de jouer
	boolean humanPlayFirst;
	int maxDepth;
	Date date;
	
	
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	public GomokuPosition getPosition() {
		return position;
	}
	public void setPosition(GomokuPosition position) {
		//this.position = position;
		for(int i = 0; i < 19; i++)
			for(int j = 0; j < 19; j++) {
				this.position.board[i][j] = position.board[i][j];
				//System.out.print(this.position.board[i][j] +" ");
			}
	}
	public boolean isHumanPlayFirst() {
		return humanPlayFirst;
	}
	public void setHumanPlayFirst(boolean humanPlayFirst) {
		this.humanPlayFirst = humanPlayFirst;
	}
	public boolean isHumanVsHuman() {
		return humanVsHuman;
	}
	public void setHumanVsHuman(boolean humanVsHuman) {
		this.humanVsHuman = humanVsHuman;
	}
	
}
