package adv1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Gomoku extends GameSearch {
	/* ICI  les 5 buttons de l'interface ,
	 3 buttons pour choissir le niveau de difficult� du jeu  : Debuttant / Norma/ Difficile 
	  2 Autres pour choisir est ce que vous voulez jouer Machine contre Player
	  Ou bien Player contre player 
	*/
	ArrayList<Sauvgarde> enregistrements=new ArrayList<Sauvgarde>();

	private JRadioButton Facile = new JRadioButton("Debuttant ", false);
	private JRadioButton Normal = new JRadioButton("Normal", true);
	private JRadioButton Difficle = new JRadioButton(" Difficile ", false);
	private JRadioButton PlayerToPlayer = new JRadioButton("Player Vs Player ", false);
	private JRadioButton MacineToPlayer = new JRadioButton("Machine Vc Player ", true);
	// CALCULER LE RECORD DE CHAQUE JOUEUR 
	private JLabel Timing_Blanc = new JLabel("  Timing Blanc :  0 s");
	private JLabel Timing_Black = new JLabel("   Timing  Noir :  0 s");
	private JButton Commencer = new JButton(" Commencer ");
	private JButton Courage = new JButton("  Aide  : 3 ");
	private JButton Sauvgarde = new JButton("  sauvgarde ");
	private JButton showSauv = new JButton("  showSauv ");
	private int totalCourage =0;
	private ChessBoard chessboard = new ChessBoard();
	private GomokuPosition board = new GomokuPosition();
	// L'etat par d�faut c'est false c'est a dire qu'on ne  joue  pas
	private boolean Etat_jeu = false; 
	private boolean canClick = true;
	JTextField nomPlayer1 = new JTextField(15);
    JTextField nomPlayer2 = new JTextField(15);
	private boolean player = true; // true for human, false for program 
	private float white = 0; //  POUR LE JOEUR BLANC --->RECORD 
	private float black = 0; //  POUR LE JOEUR BLACK --->RECORD 
	private int maxDepth = 5;
  // ****** LES THREADS POUR FAIRE DES ACTIONS EN MEMEE TEMPS OU BIEN DES ACTIONS SUCCESIVES ...
	static Thread THREAD_BLACK;
	static Thread THREEAD_BLANC;
	 Sauvgarde s=new Sauvgarde();
	JComboBox Pu;
	 Date [] dataObject =new Date[10000];
	 int i=0;
	// CONSTRUUCTEUR 
    public Gomoku() {
    	// CREER UN PANNEL 
    	JPanel Pannel_01 = new JPanel();
    	Pannel_01.setBackground(Color.ORANGE);
    	Pannel_01.add(Timing_Blanc);
    	Pannel_01.add(Commencer);
    	Pannel_01.add(Courage);
    	Pannel_01.add(Timing_Black);
    	Pu = new JComboBox(dataObject);
    	Pu.setBounds(56, 13, 58, 22);
    	Pannel_01.add(showSauv);
    	Pannel_01.add(Pu);
    // SI ON A CLIQUE SUR LE BOUTTANT COMMENCER 
        	Commencer.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			//SI ON EST PAS COMMENCER DE JOUER INTIALISER VARIABLE ETAT_JEU  TRUE 
    			if (!Etat_jeu) {
    				Etat_jeu = true;
    				Commencer.setText("Nouvelle jeu ");
    				if(Facile.isSelected()) maxDepth = 1;
    				else if(Normal.isSelected()) maxDepth = 2;
    				else if(Difficle.isSelected()) maxDepth = 3;
    			}
    			else {
    				setDefaultStatue();
        			chessboard.repaint();
    			}
    			if(PlayerToPlayer.isSelected()) {
    				
    		        
    		        Object [] fields = {
    		            " nomPlayer1", nomPlayer1,
    		            " nomPlayer2", nomPlayer2,
    		        };
    		        
    		        JOptionPane.showConfirmDialog(null,fields,"this is a header",JOptionPane.OK_CANCEL_OPTION);
    			}
    		}
    	});
        	//ce button pour afficher une partie sauvegarde
        	showSauv.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			
        			String dt=Pu.getSelectedItem().toString();
        			int k=0;
        			for(Sauvgarde s:enregistrements) {
        				
        				if(dt.equals(dataObject[k].toString())) {
        					System.out.print(k);
        					break;
        					}
        				k++;
        			}
			
        				setSaurgarde(k);
        				repaint();
        				Etat_jeu = true;
        				Commencer.setText(" new partie ");
        				
        			}
        			
        	});
        	
        	// ce button pour sauvegarder une partie
          	Sauvgarde.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			//SI ON EST PAS COMMENCER DE JOUER INTIALISER VARIABLE ETAT_JEU  TRUE 
        			
        			
        				s.setPosition(board);
        		    	s.setHumanPlayFirst(player);
        		        DateFormat df = new SimpleDateFormat();

        		    	s.setDate(new Date());
        		    	String dateToString = df.format(s.getDate());
        		    	//System.out.print(dateToString);
        		    	dataObject[i]=  s.getDate();
        		    	i++;
        		    	
        		    	s.setMaxDepth(maxDepth);
        		    	s.setHumanVsHuman(false);
        		    	enregistrements.add(s);
//        		    	
        		    	Pannel_01.remove(Pu);
        		    	Pu = new JComboBox(dataObject);
        		    	Pu.setBounds(56, 13, 58, 22);
        		    	Pannel_01.add(Pu);
        		    	
        		    
        		}
        	});
        	
    	Courage.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			if(Etat_jeu && MacineToPlayer.isSelected()) {
				//on va verifier d'abbord s'il a depasse l'aides autorisées 
				if (totalCourage++<3 ) {
					int t=3-totalCourage;
					Courage.setText("aide :"+t);
					   Thread thread2 = new Thread(new Runnable() {
			    		   public void run() {
			    			   // on va appeler a la fonction alphaBeta pour qu'il nous donne une position a jouer
			    			   Vector v = alphaBeta(0, (Position)board,HUMAN);
			    			   board = (GomokuPosition)v.elementAt(1);
			    			   repaint(); 
			    			   //white++;
    				    	   //jblBlack.setText("White: " + white);
    				    	   player = PROGRAM;
    				    	   canClick = false;
    				    	   if(wonPosition(board, PROGRAM)) {
    				    		   JOptionPane.showMessageDialog(null, "Human win!");
    				    		   Etat_jeu = false;
    				    		   return;
    				    	   }
    				    	   else if(drawnPosition(board)) {
    				    		   JOptionPane.showMessageDialog(null, "Draw game!");
    				    		   Etat_jeu = false;
    				    		   return;
    				    	   }
    				    	   // une fois l'aide est effectue on permettera a la machine de jouer 
    				    	   if(player==PROGRAM && canClick==false ) {
    				    		   
    				    		    v = alphaBeta(0, (Position)board, PROGRAM);
    			    			   board = (GomokuPosition)v.elementAt(1);
    			    			   repaint(); 
    			    			   //white++;
        				    	   //jblBlack.setText("White: " + white);
        				    	   player = HUMAN;
        				    	   canClick = true;
        				    	   if(wonPosition(board, PROGRAM)) {
        				    		   JOptionPane.showMessageDialog(null, "Computer win!");
        				    		   Etat_jeu = false;
        				    		   return;
        				    	   }
        				    	   else if(drawnPosition(board)) {
        				    		   JOptionPane.showMessageDialog(null, "Draw game!");
        				    		   Etat_jeu = false;
        				    		   return;
        				    	   }
    				    	   }
                                        }
			    	   });
			    	   
			    	   thread2.setPriority(Thread.MAX_PRIORITY);
			    	   thread2.start();
				
			    	  
			    	   
				}
			// biensur s'il a depasse le nombre d'aides possibes on va lui affiche un message
				else   JOptionPane.showMessageDialog(null, "vous avez depasser le nombre  d'aides autorisées");
			}}
		});
    	JPanel p2 = new JPanel();
         p2.setLayout(new GridLayout(2, 4,4, 4));
    	 p2.setBackground(Color.ORANGE);
    	 add(p2, BorderLayout.CENTER);
    	
    	p2.add(Facile);
    	p2.add(Normal);
    	p2.add(Difficle);

    	p2.add(PlayerToPlayer);
    	p2.add(MacineToPlayer);
    	p2.add(Sauvgarde); 
    	
    	
    	ButtonGroup PH = new ButtonGroup();
    	PH.add(Facile);
    	PH.add(Normal);
    	ButtonGroup group = new ButtonGroup();
    	group.add(Facile);
    	group.add(Normal);
    	group.add(Difficle);
    	ButtonGroup group2 = new ButtonGroup();
    	group2.add(PlayerToPlayer);
    	group2.add(MacineToPlayer);
    	add(Pannel_01, BorderLayout.NORTH);
    	add(chessboard, BorderLayout.CENTER);
    	add(p2, BorderLayout.SOUTH);
    	
    	
    	//Un thread est une unité d'exécution faisant partie d'un programme. Cette unité fonctionne de façon autonome et parallèlement à d'autres threads. Le principal avantage des threads est de pouvoir répartir différents traitements d'un même programme en plusieurs unités distinctes pour permettre leurs exécutions "simultanées".
    	
    	//
    	THREAD_BLACK = new Thread(new Runnable() {
    		public void run() {
    			try {
        			while(true) {
        				if(Etat_jeu) {
        					if(player) {
                				black += 0.2;
                				Timing_Black.setText("Black: " + (int)black + "s");
        					}
                			if(!player)
                				black = 0;
        				}
            			
            			Thread.sleep(200);
            		}
        		}
        		catch(InterruptedException ex) {
        			
        		}
    		}
    	});
    	//dans notre cas le thread  "THREEAD_BLANC" est active pour faire un record de joueur Blanc
    	//lorsque le thread ""THREEAD_BLANC" est activé le thread "	THREAD_BLACK " est desactivé et vise virsa
    	THREEAD_BLANC = new Thread(new Runnable() {
    		public void run() {
    			try {
        			while(true) {
        				// lorque l'etat de jeu est true c'est a dire qu'on est entrain de jouer
        				if(Etat_jeu) {
        					if(!player) {
        						//la valeur de record de white recoit une incrémentation de 0,2 s
                				white += 0.2;
                				Timing_Blanc.setText("White: " + (int)white + "s");
        					}
        					// et le player est true c'est a dire que un joueur est entrain de jouer et non une machine
                			if(player)
                				white = 0;
        				}
            			
            			Thread.sleep(200);
            		}
        		}
    			//et vise versa si un player est false c'est a dire la machine est entraine de jouer faire un tiling de black
        		catch(InterruptedException ex) {
        			
        		}
    		}
    	});
    }
    // pour initialiser a une nouvelle partie
    public void setDefaultStatue() {
    	board.setDefaultState();
    	
    	Etat_jeu = false;
    	//isWinned = false;
    	player = true;
    	
    	white = 0;
    	black = 0;
    	Timing_Blanc.setText("White: 0s");
    	Timing_Black.setText("Black: 0s");
    
    	Courage.setText("aide : 3");
    	Commencer.setText("Start!");
    	totalCourage=0;
    	Normal.setSelected(true);
    	maxDepth = 2;
    }
    
    // pour remplir le tableux et definir le niveaux et les joueur pour continue une partie sauvegarde
    public void setSaurgarde(int index) {

    	for(int i = 0; i < 19; i++) {
    		System.out.println("i:"+i);
    		for(int j = 0; j < 19; j++) {
    			board.board[i][j] = enregistrements.get(index).getPosition().board[i][j];

    		}
    	}
        	Etat_jeu = true;
        	//isWinned = false;
        	player = enregistrements.get(index).isHumanPlayFirst();
        	white = 0;
        	black = 0;
        	Timing_Blanc.setText("White: 0s");
        	Timing_Black.setText("Black: 0s");
        	
        	Commencer.setText("Start!");
        	if(enregistrements.get(index).getMaxDepth()==2) {
        	Facile.setSelected(true);
        	maxDepth = 2;
        	}else if(enregistrements.get(index).getMaxDepth()==4) {
        		Normal.setSelected(true);
            	maxDepth = 4;
        	}else if(enregistrements.get(index).getMaxDepth()==5) {
        		Difficle.setSelected(true);
            	maxDepth = 5;
        	}
        	
        	
        }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Thread() {
            public void run() {
                Gomoku frame = new Gomoku();
              
                frame.setTitle("Jouer GOMOKU");
                frame.setSize(650, 600);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
    	
                THREEAD_BLANC.start();
                THREAD_BLACK.start();
            }
        });
    }
    
    /**
     * 
     * @param p position
     * @return true if meets a draw situation
     */
    // pour verifier si la grille est plein
    public boolean drawnPosition(Position p){
    	GomokuPosition pos = (GomokuPosition)p;
    	for(int i = 0; i < pos.board.length; i++)
    		for(int j = 0; j < pos.board[0].length; j++)
    			if(pos.board[i][j] == GomokuPosition.BLANK) return false;
    	return true;
    }
    //pour verifier si un player a gagné
    public boolean wonPosition(Position p, boolean player) {
       // if (GameSearch.DEBUG) System.out.println("wonPosition("+p+","+player+")");
        boolean ret = false;
        GomokuPosition pos = (GomokuPosition) p ;  
       // if (GameSearch.DEBUG) System.out.println("     ret="+ret);
        return winCheck(player, pos);
    }
    // retourner vrai si les conditions du succes sont bien verifies 
    private boolean winCheck(boolean player, GomokuPosition pos) {
       int b = 0;
       if (player) b = GomokuPosition.HUMAN;
       else        b = GomokuPosition.PROGRAM;
       for(int i=0 ;i<19;i++) {
            for(int j=0 ;j<19;j++) {
                 if(pos.board[i][j]!=0) {
                
                      if(pos.board[i][j]==b && (  j+1<19 && pos.board[i][j+1]==b ) && (j+2<19 && pos.board[i][j+2]==b  ) && (j+3<19 && pos.board[i][j+3]==b ) && (j+4<19 && pos.board[i][j+4]==b  )) return true;
                       if(pos.board[i][j]==b && (  i+1<19 && pos.board[i+1][j]==b ) && (i+2<19 && pos.board[i+2][j]==b  ) && (i+3<19 && pos.board[i+3][j]==b ) && (i+4<19 && pos.board[i+4][j]==b  )) return true;
                       if (pos.board[i][j]==b && (i+1<19 &&  j+1<19 && pos.board[i+1][j+1]==b ) && (i+2<19 && j+2<19 && pos.board[i+2][j+2]==b  ) && (i+3<19 && j+3<19 && pos.board[i+3][j+3]==b ) && (i+4<19 && j+4<19 && pos.board[i+4][j+4]==b  )) return true;
                       if(pos.board[i][j]==b && (i+1<19 &&  j-1>=0 && pos.board[i+1][j-1]==b ) && (i+2<19 && j-2>=0 && pos.board[i+2][j-2]==b  ) && (i+3<19 && j-3>=0 && pos.board[i+3][j-3]==b ) && (i+4<19 && j-4>=0 && pos.board[i+4][j-4]==b  )) return true;
                            			 }
    }}
    return false;
    }
    
 
    public float positionEvaluation(Position p, boolean player){
    	int[][] myConnects = connectN(p, player, 5);
    	int[][] enemyConnects = connectN(p, !player, 5);
    	
        //  s'il y a une connectivite de 5 ou de 4 avec les bornes vide alors c'est le meilleur choix
    	if(myConnects[3][0] > 0 || myConnects[2][2] > 0)
    		return Float.POSITIVE_INFINITY;
    	if(enemyConnects[3][0] > 0 || enemyConnects[2][2] > 0)
    		return Float.NEGATIVE_INFINITY;
    	
    	int ret = 0;
    	GomokuPosition pos = (GomokuPosition)p;
    	
    	int[] score = {10, 100, 1000};
    	
    	
    	ret += myConnects[0][1] * score[0];
    	ret += myConnects[0][2] * (int)(score[1] * 0.9);
    	ret += myConnects[1][1] * score[1];
    	ret += myConnects[1][2] * (int)(score[2] * 0.9);
    	ret += myConnects[2][1] *3* score[2];
    	
    	ret -= enemyConnects[0][1] * 2*score[0];
    	ret -= enemyConnects[0][2] * 3*(int)(score[1] * 0.9);
    	ret -= enemyConnects[1][1] *3* score[1];
    	ret -= enemyConnects[1][2] *10* (int)(score[2] * 0.9);
    	ret -= enemyConnects[2][1] *100* score[2];
    	
    	return ret;
    }
    

    /*
      c'est une fonction qui va nous retourner une matrice 4*3  contenant le nombres des balles connectees 2,3... tq
      [0][0] = nombre totale de 2 balles connectées en generale  (ex: PP)
      [0][1]= nombre totale de 2 balles connectées tq l'un des bornes est fermé  ou bien jouer par adversaire (ex: HPP ou PPH) c a d je peux jouer soit le droit soit la gauche
      [0][2]= nombre totale de 2 balles connectées tq les  bornes sont vide c a d je peux jouer soit du droit soit du gauche (ex: ...PP... ou ...PP...)
      meme chose pour les autres lignes tq la 2 eme ligne represente la connectivité de 3 
      et la ligne 3 represente la connectivite de 4 
      et la derniere represente la connectivite de 5 
      
       */
    
    private int[][] connectN(Position p, boolean player, int number) {
    	/*pour le parametre number represente jusqu'a ou je vais aller chercher 
    	par exemple je veux chercher la connectivité de 2 ,3 4 ,5 alors number=5*/
    	GomokuPosition pos = (GomokuPosition)p;
    	short b;
    	if(player) b = GomokuPosition.HUMAN;
    	else b = GomokuPosition.PROGRAM;
    	// la matrice qui sera renvoyee
    	int count[][] = new int[number-1][3];
    	for(int i = 0; i < count.length; i++)
    		for(int j = 0; j < count[0].length; j++)
    			count[i][j] = 0;
    	// puisque number =5 alors commence a chercher la connectivite de 2 jusqu'a 5
    	for(int n = 2; n <= number; n++) {
    		for(int i = 0; i < pos.board.length; i++) {
        		for(int j = 0; j < pos.board[0].length; j++) {
        			// ici on arrete au niveau de l'index i+n-1 car les autres indix seront traites par les fonctions qui verifient la connectivite 
        			if(i+n-1 < pos.board.length) {
        				//on va verifier la connectivite a partir de cet indix 
        				if(downSame(p, i, j, n-1, b)) {
        					//est biensur si on trouve une connectivite on va l'ajouter dans la matrice
        					while(true) {
        						if(i+n < pos.board.length)
        							if(pos.board[i+n][j] == b) break;
        						if(i-1 >= 0)
        							if(pos.board[i-1][j] == b) break;
        						
        						count[n-2][0]++;
        						
        						if(i-1 >= 0 && i+n >= pos.board.length) {
            	    				if(pos.board[i-1][j] == GomokuPosition.BLANK)
            	    					count[n-2][1]++;
            	    			}
            	    			else if(i-1 < 0 && i+n < pos.board.length) {
            	    				if(pos.board[i+n][j] == GomokuPosition.BLANK)
            	    					count[n-2][1]++;
            	    			}
            	    			else if(i-1 >= 0 && i+n < pos.board.length) {
            	    				if(pos.board[i-1][j] == GomokuPosition.BLANK 
            	    						&& pos.board[i+n][j] != GomokuPosition.BLANK)
            	    					count[n-2][1]++;
            	    				if(pos.board[i+n][j] == GomokuPosition.BLANK
            	    						&& pos.board[i-1][j] != GomokuPosition.BLANK)
            	    					count[n-2][1]++;
            	    				
            	    				if(pos.board[i-1][j] == GomokuPosition.BLANK
            	    						&& pos.board[i+n][j] == GomokuPosition.BLANK)
            	    					count[n-2][2]++;
            	    			}
            	    			break;
        					}
        				}
        				
        				if(j+n-1 < pos.board[0].length) {
        					if(rightSame(p, i, j, n-1, b)) {
        						while(true) {
            						if(j+n < pos.board[0].length)
            							if(pos.board[i][j+n] == b) break;
            						if(j-1 >= 0)
            							if(pos.board[i][j-1] == b) break;
            						count[n-2][0]++;
                					
                					if(j-1 >= 0 && j+n >= pos.board[0].length) {
                						if(pos.board[i][j-1] == GomokuPosition.BLANK)
                							count[n-2][1]++;
                					}
                					else if(j-1 < 0 && j+n < pos.board[0].length) {
                						if(pos.board[i][j+n] == GomokuPosition.BLANK)
                							count[n-2][1]++;
                					}
                					else if(j-1 >= 0 && j+n < pos.board[0].length) {
                						if(pos.board[i][j-1] == GomokuPosition.BLANK 
                	    						&& pos.board[i][j+n] != GomokuPosition.BLANK)
                	    					count[n-2][1]++;
                	    				if(pos.board[i][j+n] == GomokuPosition.BLANK
                	    						&& pos.board[i][j-1] != GomokuPosition.BLANK)
                	    					count[n-2][1]++;
                	    				
                	    				if(pos.board[i][j-1] == GomokuPosition.BLANK
                	    						&& pos.board[i][j+n] == GomokuPosition.BLANK)
                	    					count[n-2][2]++;
                					}
                					break;
            					}
        					}
        				}
        				
        				if(i+n-1 < pos.board.length && j+n-1 < pos.board[0].length) {
        					if(rightDownSame(p, i, j, n-1, b)) {
        						while(true) {
            						if(i+n < pos.board.length && j+n < pos.board[0].length)
            							if(pos.board[i+n][j+n] == b) break;
            						if(i-1 >= 0 && j-1 >= 0)
            							if(pos.board[i-1][j-1] == b) break;
            						count[n-2][0]++;
                					
                					if((i-1 >= 0 && j-1 >= 0) && (i+n >= pos.board.length || j+n >= pos.board[0].length)) {
                						if(pos.board[i-1][j-1] == GomokuPosition.BLANK)
                							count[n-2][1]++;
                					}
                					else if((i-1 < 0 || j-1 < 0) && (i+n < pos.board.length && j+n < pos.board[0].length)) {
                						if(pos.board[i+n][j+n] == GomokuPosition.BLANK)
                							count[n-2][1]++;
                					}
                					else if((i-1 >= 0 && j-1 >= 0) && (i+n < pos.board.length && j+n < pos.board[0].length)) {
                						if(pos.board[i-1][j-1] == GomokuPosition.BLANK
                								&& pos.board[i+n][j+n] != GomokuPosition.BLANK)
                							count[n-2][1]++;
                						if(pos.board[i+n][j+n] == GomokuPosition.BLANK
                								&& pos.board[i-1][j-1] != GomokuPosition.BLANK)
                							count[n-2][1]++;
                						if(pos.board[i-1][j-1] == GomokuPosition.BLANK && pos.board[i+n][j+n] == GomokuPosition.BLANK)
                							count[n-2][2]++;
                					}
                					break;
        						}
        					}
        				}
        				
        				if(i-n+1 >= 0 && j+n-1 < pos.board[0].length) {
        					if(rightUpSame(p, i, j, n-1, b)) {
        						while(true) {
            						if(i-n >= 0 && j+n < pos.board[0].length)
            							if(pos.board[i-n][j+n] == b) break;
            						if(i+1 < pos.board.length && j-1 >= 0)
            							if(pos.board[i+1][j-1] == b) break;
            						count[n-2][0]++;
                					
                					if((i-n >= 0 && j+n < pos.board[0].length) && (i+1 >= pos.board.length || j-1 < 0)) {
                						if(pos.board[i-n][j+n] == GomokuPosition.BLANK)
                							count[n-2][1]++;
                					}
                					else if((i-n < 0 || j+n > pos.board[0].length) && (i+1 < pos.board.length && j-1 >= 0)) {
                						if(pos.board[i+1][j-1] == GomokuPosition.BLANK)
                							count[n-2][1]++;
                					}
                					else if((i-n >= 0 && j+n < pos.board[0].length) && (i+1 < pos.board.length && j-1 >= 0)) {
                						if(pos.board[i-n][j+n] == GomokuPosition.BLANK
                								&& pos.board[i+1][j-1] != GomokuPosition.BLANK)
                							count[n-2][1]++;
                						if(pos.board[i+1][j-1] == GomokuPosition.BLANK
                								&& pos.board[i-n][j+n] != GomokuPosition.BLANK)
                							count[n-2][1]++;
                						if(pos.board[i-n][j+n] == GomokuPosition.BLANK && pos.board[i+1][j-1] == GomokuPosition.BLANK)
                							count[n-2][2]++;
                					}
                					break;
            					}
        					}
        				}
        			}
        		}
        	}
    	}
    	
    	
    	
    	return count;
    }
    /*il a  4 fonctions pour  verifierla connectivité a partir d'un index car il a 4 cas a traite (meme cas traiter dans la fonction winchek() */
    private boolean downSame(Position p, int i, int j, int n, short b) {
    	GomokuPosition pos = (GomokuPosition)p;
    	for(int k=0; k<=n; k++)
    		if(pos.board[i+k][j] != b) return false;
    	return true;
    }
    
    private boolean rightSame(Position p, int i, int j, int n, short b) {
    	GomokuPosition pos = (GomokuPosition)p;
    	for(int k=0; k<=n; k++)
    		if(pos.board[i][j+k] != b) return false;
    	return true;
    }
    
    private boolean rightDownSame(Position p, int i, int j, int n, short b) {
    	GomokuPosition pos = (GomokuPosition)p;
    	for(int k=0; k<=n; k++)
    		if(pos.board[i+k][j+k] != b) return false;
    	return true;
    }
    
    private boolean rightUpSame(Position p, int i, int j, int n, short b) {
    	GomokuPosition pos = (GomokuPosition)p;
    	for(int k=0; k<=n; k++)
    		if(pos.board[i-k][j+k] != b) return false;
    	return true;
    }
    
    
    
   
// les movements possibles 
	public Position [] possibleMoves(Position p, boolean player){
		if (GameSearch.DEBUG) System.out.println("posibleMoves("+p+","+player+")");
        GomokuPosition pos = (GomokuPosition)p;
        int count = 0;
        for (int i=0; i<19; i++) {
        	for (int j=0; j<19; j++)
        	if (pos.board[i][j] == 0) count++;}
        if (count == 0) return null;
        Position [] ret = new Position[count];
        count = 0;
        for (int i=0; i<19; i++) {
        	for (int j=0; j<19; j++) {
            if (pos.board[i][j] == 0) {
                GomokuPosition pos2 = new  GomokuPosition();
                for (int k=0; k<19; k++) {
                	for (int f=0; f<19; f++)
                		pos2.board[k][f] = pos.board[k][f];}
             
                if (player) pos2.board[i][j] = 1; else pos2.board[i][j] = -1;
                ret[count++] = pos2;
                if (GameSearch.DEBUG) System.out.println("    "+pos2);
            }}
        }
        return ret;
    }
	
	
    
    public boolean reachedMaxDepth(Position p, int depth){
    	if(wonPosition(p, true)) return true;
    	if(wonPosition(p, false)) return true;
    	if(drawnPosition(p)) return true;
    	if(depth >= maxDepth) return true;
    	return false;
    }
    
    class ChessBoard extends JPanel {
    	private int width;
    	private int height;
    	private int widthStep;
    	private int heightStep;
		
    	private int xStart;
    	private int yStart;
    	private int xEnd;
    	private int yEnd;
        
    	public ChessBoard() {
    		setBackground(Color.ORANGE);
    		
    		addMouseListener(new MouseAdapter() {
    			public void mouseClicked(MouseEvent e) {
    				if(MacineToPlayer.isSelected()) {
    				if (Etat_jeu && canClick) {
    					int mouseX = 0;
    			    	int mouseY = 0;
    			    	int gridX = 0;
    			    	int gridY = 0;
    			    	
    				    mouseX = e.getX();
    				    mouseY = e.getY();
    				    if (mouseX >= xStart && mouseX <= xEnd && mouseY >= yStart && mouseY <= yEnd) {
    				        gridX = (mouseX - xStart) / widthStep;
    				        gridY = (mouseY - yStart) / heightStep;
    				        
    				       if(board.board[gridX][gridY] == GomokuPosition.BLANK) {
    				    	   board.board[gridX][gridY] = GomokuPosition.HUMAN;
    				    	   repaint();
    				    	   //black++;
    				    	   //Timing_Blanc.setText("Black: " + black);
    				    	   if(wonPosition(board, HUMAN)) {
    				    		   JOptionPane.showMessageDialog(null, "Human win!");
    				    		   Etat_jeu = false;
    				    		   return;
    				    	   }
    				    	   else if(drawnPosition(board)) {
    				    		   JOptionPane.showMessageDialog(null, "Draw game!");
    				    		   Etat_jeu = false;
    				    		   return;
    				    	   }
    				    	   
    				    	  
    				       }
    				    }
    				    player=PROGRAM;
    				    canClick=false;
    				}
    				if(player==PROGRAM && canClick==false) {
    					
				    	   
				    	   Thread thread2 = new Thread(new Runnable() {
				    		   public void run() {
				    			   Vector v = alphaBeta(0, (Position)board, PROGRAM);
				    			   board = (GomokuPosition)v.elementAt(1);
				    			   repaint(); 
				    			   //white++;
	    				    	   //jblBlack.setText("White: " + white);
	    				    	   player = HUMAN;
	    				    	   canClick = true;
	    				    	   if(wonPosition(board, PROGRAM)) {
	    				    		   JOptionPane.showMessageDialog(null, "Computer win!");
	    				    		   Etat_jeu = false;
	    				    		   return;
	    				    	   }
	    				    	   else if(drawnPosition(board)) {
	    				    		   JOptionPane.showMessageDialog(null, "Draw game!");
	    				    		   Etat_jeu = false;
	    				    		   return;
	    				    	   }
                                          }
				    	   });
				    	   
				    	   thread2.setPriority(Thread.MAX_PRIORITY);
				    	   thread2.start();
    				}
    				}
    			if(PlayerToPlayer.isSelected()) {
    				if (Etat_jeu && canClick && player==PLAYER1) {
    					int mouseX = 0;
    			    	int mouseY = 0;
    			    	int gridX = 0;
    			    	int gridY = 0;
    			    	
    				    mouseX = e.getX();
    				    mouseY = e.getY();
    				    if (mouseX >= xStart && mouseX <= xEnd && mouseY >= yStart && mouseY <= yEnd) {
    				        gridX = (mouseX - xStart) / widthStep;
    				        gridY = (mouseY - yStart) / heightStep;
    				        
    				       if(board.board[gridX][gridY] == GomokuPosition.BLANK) {
    				    	   board.board[gridX][gridY] = GomokuPosition.PLAYER1;
    				    	   repaint();
    				    	   //black++;
    				    	   //Timing_Blanc.setText("Black: " + black);
    				    	   if(wonPosition(board, PLAYER1)) {
    				    		   JOptionPane.showMessageDialog(null,nomPlayer1.getText()+ " win!");
    				    		   Etat_jeu = false;
    				    		   return;
    				    	   }
    				    	   else if(drawnPosition(board)) {
    				    		   JOptionPane.showMessageDialog(null, "Draw game!");
    				    		   Etat_jeu = false;
    				    		   return;
    				    	   }
    				    	   
    				    	  
    				       }
    				    }

    				}
    				if(player==PLAYER2) {
    					
				    	   
				    	   Thread thread2 = new Thread(new Runnable() {
				    		   public void run() {
				    			   int mouseX = 0;
			    			    	int mouseY = 0;
			    			    	int gridX = 0;
			    			    	int gridY = 0;
			    			    	
			    				    mouseX = e.getX();
			    				    mouseY = e.getY();
			    				    if (mouseX >= xStart && mouseX <= xEnd && mouseY >= yStart && mouseY <= yEnd) {
			    				        gridX = (mouseX - xStart) / widthStep;
			    				        gridY = (mouseY - yStart) / heightStep;
			    				        
			    				       if(board.board[gridX][gridY] == GomokuPosition.BLANK) {
			    				    	   board.board[gridX][gridY] = GomokuPosition.PLAYER2;
			    				    	   repaint();
			    				    	   //black++;
			    				    	   //Timing_Blanc.setText("Black: " + black);
			    				    	   if(wonPosition(board, PLAYER2)) {
			    				    		   JOptionPane.showMessageDialog(null,nomPlayer2.getText()+ " win!");
			    				    		   Etat_jeu = false;
			    				    		   return;
			    				    	   }
			    				    	   else if(drawnPosition(board)) {
			    				    		   JOptionPane.showMessageDialog(null, "Draw game!");
			    				    		   Etat_jeu = false;
			    				    		   return;
			    				    	   }
			    				    	   
			    				    	  
			    				       }
			    				    }
                                          }
				    	   });
				    	   
				    	   thread2.setPriority(Thread.MAX_PRIORITY);
				    	   thread2.start();
    				}
    				
    				if(player) player=PLAYER2;
    				else player=PLAYER1;
    			}
    			}
    		}); 
    	}
    	
    	protected void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		
    		width = (int)(getWidth() * 0.98);
        	height = (int)(getHeight() * 0.98);
        	widthStep = width / GomokuPosition.CHESSBOARD_SIZE;
        	heightStep = height / GomokuPosition.CHESSBOARD_SIZE;
    		
        	xStart = (int)(width * 0.01);
        	yStart = (int)(height * 0.01);
        	xEnd = xStart + GomokuPosition.CHESSBOARD_SIZE * widthStep;
        	yEnd = yStart + GomokuPosition.CHESSBOARD_SIZE * heightStep;
        	
    		// draw chessboard
    		g.setColor(Color.BLACK);
    		g.drawLine(xStart, yStart, xStart, yEnd);
    		g.drawLine(xStart, yStart, xEnd, yStart);
    		g.drawLine(xEnd, yStart, xEnd, yEnd);
    		g.drawLine(xStart, yEnd, xEnd, yEnd);
    		for (int i = 1; i < GomokuPosition.CHESSBOARD_SIZE; i++) {
    			g.drawLine(xStart + i * widthStep, yStart, xStart + i * widthStep, yEnd);
    			g.drawLine(xStart, yStart + i * heightStep, xEnd, yStart + i * heightStep);
    		}
    		
    		// draw chess
    		int chessRadius = (int)(Math.min(widthStep, heightStep) * 0.9 *0.5);
    		for (int i = 0; i < board.board.length; i++) {
    			for (int j = 0; j < board.board[0].length; j++) {
    				if (board.board[i][j] == GomokuPosition.HUMAN) {
    				    g.setColor(Color.BLACK);
    				    int xCenter = (int)(xStart + (i + 0.5) * widthStep);
    				    int yCenter = (int)(yStart + (j + 0.5) * heightStep);
    				    g.fillOval(xCenter - chessRadius, yCenter - chessRadius, 2 * chessRadius, 2 * chessRadius);
    				}
    				else if (board.board[i][j] == GomokuPosition.PROGRAM) {
    					g.setColor(Color.WHITE);
    					int xCenter = (int)(xStart + (i + 0.5) * widthStep);
    				    int yCenter = (int)(yStart + (j + 0.5) * heightStep);
    				    g.fillOval(xCenter - chessRadius, yCenter - chessRadius, 2 * chessRadius, 2 * chessRadius);
    				}
    			}
    		}
    	}
    }
}


