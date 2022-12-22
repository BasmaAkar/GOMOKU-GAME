package adv1;

public class GomokuPosition extends Position {
	public final static short BLANK = 0;
	public final static short PROGRAM = -1;
	public final static short HUMAN = 1;
	
    final static public int PLAYER1 = 1;
    final static public int PLAYER2 = -1;
    
	// represente la taille du grid pour nous on va choisir 19*19
	public final static int CHESSBOARD_SIZE = 19;
	//grid
	int[][] board = new int[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
	
	public GomokuPosition() {
		setDefaultState();
	}
	//pour initialiser 
	public void setDefaultState() {
		for(int i = 0; i < CHESSBOARD_SIZE; i++)
			for(int j = 0; j < CHESSBOARD_SIZE; j++)
				board[i][j] = BLANK;
	}
	//get new position
	public GomokuPosition getNewPosition() {
		GomokuPosition pos = new GomokuPosition();
		for(int i = 0; i < CHESSBOARD_SIZE; i++)
			for(int j = 0; j < pos.CHESSBOARD_SIZE; j++)
				pos.board[i][j] = board[i][j];
		return pos;
	}
}
