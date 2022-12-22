package adv1;

import java.util.*;

import javax.swing.JFrame;
/* pour la classe GameSearch , c'est pareil de GameSearch de TicTacToe 
  on a supprimer juste la fonction playGame()  et les classes abstract 
  concernant les moves pour creer et recevoir les indix 
   puisque on va travailler avec l'interface graphique 
  
 */
public abstract class GameSearch extends JFrame {

	 public static final boolean DEBUG = false;

    public static boolean PROGRAM = false;
    public static boolean HUMAN = true;
    public static boolean PLAYER1 = true;
    public static boolean PLAYER2 = false;


   
    /*
     * Abstract methods:
     */

    public abstract boolean drawnPosition(Position p);
    public abstract boolean wonPosition(Position p, boolean player);
    public abstract float positionEvaluation(Position p, boolean player);
    public abstract Position [] possibleMoves(Position p, boolean player);
    public abstract boolean reachedMaxDepth(Position p, int depth);

    /*
    
     */

    protected Vector alphaBeta(int depth, Position p, boolean player) {
        Vector v = alphaBetaHelper(depth, p, player, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
        //System.out.println("^^ v(0): " + v.elementAt(0) + ", v(1): " + v.elementAt(1));
        return v;
    }

    protected Vector alphaBetaHelper(int depth, Position p,
                                     boolean player, float alpha, float beta) {
    	//if (GameSearch.DEBUG) System.out.println("alphaBetaHelper("+depth+","+p+","+alpha+","+beta+")");
            if (reachedMaxDepth(p, depth)) {
            Vector v = new Vector(2);
            float value = positionEvaluation(p, player);
            v.addElement(new Float(value));
            v.addElement(null);
            /*
            if(GameSearch.DEBUG) {
                System.out.println(" alphaBetaHelper: mx depth at " + depth+
                                   ", value="+value);
            }
            */
            return v;
        }
        Vector best = new Vector();
        Position [] moves = possibleMoves(p, player);
        for (int i=0; i<moves.length; i++) {
            Vector v2 = alphaBetaHelper(depth + 1, moves[i], !player, -beta, -alpha);
            //if (v2 == null || v2.size() < 1) continue;
            float value = -((Float)v2.elementAt(0)).floatValue();
            if (value > beta) {
                //if(GameSearch.DEBUG) System.out.println(" ! ! ! value="+value+", beta="+beta);
                beta = value;
                best = new Vector();
                best.addElement(moves[i]);
                Enumeration enum2 = v2.elements();
                enum2.nextElement(); // skip previous value
                while (enum2.hasMoreElements()) {
                    Object o = enum2.nextElement();
                    if (o != null) best.addElement(o);
                }
            }
            /**
             * Use the alpha-beta cutoff test to abort search if we
             * found a move that proves that the previous move in the
             * move chain was dubious
             */
            if (beta >= alpha) {
                break;
            }
        }
        Vector v3 = new Vector();
        v3.addElement(new Float(beta));
        Enumeration enum2 = best.elements();
        while (enum2.hasMoreElements()) {
            v3.addElement(enum2.nextElement());
        }
        return v3;
    }
}
