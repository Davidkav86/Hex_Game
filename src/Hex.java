/**
 * @author Frank Walsh & David Kavanagh
 * 
 *         This class is for the game play of the Hex board game. The steps that
 *         take place in order to complete the game are implemented in this
 *         class. The main method responsible for running the Hex game is found
 *         here
 * 
 */
public class Hex implements BoardGame
{

	private int[][] board; // 2D Board. 0 - empty, 1 - Player 1, 2 - Player 2

	private int n1, n2; // height and width of board

	private WeightedQuickUnionUF wqu; // Union Find data structure to keep track
										// of unions and calculate winner
	private static int currentPlayer; // Current player in the game,

	/**
	 * Hex() - Constructor for the hex class. Takes in two integers as
	 * parameters that act as the height and width of the 2d array. It also
	 * initialises all the fields in this class.
	 * 
	 * @param n1
	 *            - An int that represents the height of the array
	 * 
	 * @param n2
	 *            - An int that represents the width of the array
	 */
	public Hex(int n1, int n2)
	{
		if (n1 >= 0)// Ensures no negative number can be used as the size of the
					// array
		{
			this.n1 = n1;
		}
		else
		{
			this.n1 = 14;
		}
		if (n2 >= 0)
		{
			this.n2 = n2;
		}
		else
		{
			this.n2 = 14;
		}

		currentPlayer = 1;

		wqu = new WeightedQuickUnionUF(n1 * n1);// Creates an instance
												// WeightedQuickUnionUF class

		board = new int[n1][n2];// Creates an instance of the 2d array used as
								// the board

	}

	/**
	 * takeTurn() - This method takes in the x and y coordinates as parameters
	 * and uses them to check for neighbours, connect virtual sites and change
	 * the player until the game is over
	 * 
	 * @param - x - An integer that represents the players x coordinate
	 * 
	 * @param - y - An integer that represents the players y coordinate
	 * 
	 */
	@Override
	public void takeTurn(int x, int y)
	{
		// assigns the current player to a nowPlaying int
		int nowPlaying = currentPlayer;

		// Invalid coordinate test. If a player picks any coordinate off the
		// board, they get a warning and a chance to re-take their turn
		if ((x < n1 && x >= 0) && (y < n2 && y >= 0))
		{

		}
		else
		{
			StdOut.print(" Those are invalid coordinates!!! Please try again \n");
			return;
		}

		// Test to see if the location is free. If it is, the players number is
		// placed into the board at that position. They now own that hex
		if (board[x][y] == 0)
		{

			board[x][y] = nowPlaying;
		}
		else
		{
			StdOut.print(" The location you have chosen is not free \n");
			StdOut.print(" Choose another location \n");
			return;
		}

		checkNeighbours(x, y, nowPlaying);

		setVirtualSites(x, y);

		// if isWinner() returns true. Jump out of this method.
		if (isWinner())
		{
			return;
		}

		printBoard();

		nextPlayer();
	}

	/**
	 * setVirtualSites() - This method sets up the virtual sites for each of the
	 * players if they choose any hex from one of the sides of the board that
	 * they are trying to connect. If the player selects a hex on their own side
	 * of the board, this method will connect it to the virtual site set up for
	 * that particular side of the board
	 * 
	 * @param x
	 *            - players x coordinates
	 * @param y
	 *            - players y coordinates
	 */
	public void setVirtualSites(int x, int y)
	{
		// Assigns an int to player position using the given coordinates
		int playerPosition = (n1 * x) + y;

		// Only player 1 can activate the virtual sites on the top or bottom
		// of the board
		if (currentPlayer == 1)
		{
			if (x == 0)// Checks if the coordinates will equate to the
						// top of the board
			{
				// If player 1 selects any hex across the top of the board.
				// The virtual site is activated
				for (int yCheck = 0; yCheck < n1; yCheck++)
				{
					int xCheck = 0;

					// If(any hex across the top of the board is owned by player
					// 1)
					if (board[xCheck][yCheck] == currentPlayer)
					{
						wqu.union(playerPosition, n1 * n2);
						// connect the chosen hex and the virtual site
					}

				}
			}

			if (x == n1 - 1)// Checks if the coordinates will equate to
							// the bottom of the board
			{
				// If player 1 selects any hex across the bottom of the board.
				// The virtual site is activated
				for (int yCheck = 0; yCheck < n1; yCheck++)
				{
					int xCheck = n1 - 1;

					// If(any hex across the bottom of the board is owned by
					// player
					// 1)
					if (board[xCheck][yCheck] == currentPlayer)
					{
						wqu.union(playerPosition, n1 * n2 + 1);
						// connect the chosen hex and the virtual site
					}

				}
			}

		}

		// Only player 2 can activate the virtual sites on the left or right
		// side of the board
		if (currentPlayer == 2)
		{

			if (y == 0)// Checks if the coordinates will equate to the
						// left side of the board
			{
				// If player 2 selects any hex across the left side of the
				// board.
				// The virtual site is activated
				for (int xCheck = 0; xCheck < n1; xCheck++)
				{
					int yCheck = 0;

					// If(any hex across the left side of the board is owned by
					// player 2)
					if (board[xCheck][yCheck] == currentPlayer)
					{
						wqu.union(playerPosition, n1 * n2 + 2);
						// connect the chosen hex and the virtual site
					}
				}
			}

			if (y == n1 - 1)// Checks if the coordinates will equate to the
							// bottom of the board
			{
				// If player 2 selects any hex across the right side of the
				// board.
				// The virtual site is activated
				for (int xCheck = 0; xCheck < n1; xCheck++)
				{
					int yCheck = (n1 - 1);

					// If(any hex across the right side of the board is owned by
					// player 2)
					if (board[xCheck][yCheck] == currentPlayer)
					{
						wqu.union(playerPosition, n1 * n2 + 3);
						// connect the chosen hex and the virtual site
					}

				}
			}

		}
	}

	/**
	 * checkNeighbour() - This method uses the x and y coordinates given by the
	 * current player to check all of the surrounding hex's. If any of the
	 * surrounding hex's are owned by the current player they will be joined
	 * using the union() method from the WeightedQuickUnion Class
	 * 
	 * @param x
	 *            - int - The x coordinate of the current player
	 * @param y
	 *            - int - The y coordinate of the current player
	 * @param nowPlaying
	 *            - The player currently playing. 1 or 2
	 */
	public void checkNeighbours(int x, int y, int nowPlaying)
	{

		// Assigns an int to playerPosition using the given coordinates
		int playerPosition = (n1 * x) + y;

		// Use the coordinates to check if a surrounding hex is owned by the
		// current player. Only checks if y > 0, this is to make sure that the
		// coordinates cannot be out of bounds.
		if (y > 0 && (board[x][y - 1] == nowPlaying))
		{
			// Use the x and y coordinates to find the position of one of the
			// neighbours in the id[] array
			int neighbour = (n1 * x) + y - 1;

			// External method call to union the players position and neighbour
			wqu.union(playerPosition, neighbour);

		}
		if (x > 0 && y < n2 - 1 && (board[x - 1][y + 1] == nowPlaying))
		{

			int neighbour = (n1 * (x - 1)) + y + 1;

			wqu.union(playerPosition, neighbour);

		}
		if (y < n2 - 1 && (board[x][y + 1] == nowPlaying))
		{

			int neighbour = (n1 * x) + y + 1;

			wqu.union(playerPosition, neighbour);

		}
		if (x < n1 - 1 && y <= n2 && (board[x + 1][y] == nowPlaying))
		{

			int neighbour = (n1 * (x + 1)) + y;

			wqu.union(playerPosition, neighbour);

		}
		if (x < n1 - 1 && y > 1 && (board[x + 1][y - 1] == nowPlaying))
		{

			int neighbour = (n1 * (x + 1)) + y - 1;

			wqu.union(playerPosition, neighbour);

		}
		if (x > 0 && (board[x - 1][y] == nowPlaying))
		{

			int neighbour = (n1 * (x - 1)) + y;

			wqu.union(playerPosition, neighbour);

		}

	}

	/**
	 * getCurrentPlayer() - Getter for the currentPlayer field. Returns the
	 * currentPlayer int.
	 * 
	 * @return - currentPlayer - An integer representation for the current
	 *         player
	 */
	@Override
	public int getCurrentPlayer()
	{
		return currentPlayer;
	}

	/**
	 * getN1() - Getter for the n1 field.
	 * 
	 * @return - n1 - An int that represents the height of the 2d array
	 */
	public int getN1()
	{
		return n1;
	}

	/**
	 * setN1() - Setter for the n1 field.
	 * 
	 * @param n1
	 *            - An int that represents the height of the 2d array
	 */
	public void setN1(int n1)
	{
		if (n1 >= 0)// ensure that a minus number cannot be used
		{
			this.n1 = n1;
		}
	}

	/**
	 * getN2() - Getter for the n2 field.
	 * 
	 * @param n2
	 *            - An int that represents the width of the 2d array
	 */
	public int getN2()
	{
		return n2;
	}

	/**
	 * setN2() - Setter for the n2 field.
	 * 
	 * @param n2
	 *            - An int that represents the width of the 2d array
	 */
	public void setN2(int n2)
	{
		if (n2 >= 0)// ensure that a minus number cannot be used
		{
			this.n2 = n2;
		}
	}

	/**
	 * setCurrentPlayer - Setter for the currentPlayer field
	 * 
	 * @param currentPlayer
	 */
	public void setCurrentPlayer(int currentPlayer)
	{
		Hex.currentPlayer = currentPlayer;
	}

	/**
	 * getBoard() - Getter for the 2d array. Returns the board array
	 * 
	 * @return - board - The 2d array board
	 */
	@Override
	public int[][] getBoard()
	{
		return board;
	}

	/**
	 * nextPlayer() - This method changes from one player to the next. It is
	 * called at the end of each turn
	 * 
	 */
	private void nextPlayer()
	{
		if (currentPlayer == 1)
			currentPlayer = 2;
		else
			currentPlayer = 1;

	}

	/**
	 * isWinner() - This method returns a boolean that is false unless a players
	 * virtual sites are connected. Once connected, this method returns true and
	 * the game is over
	 * 
	 * @return - A boolean
	 */
	@Override
	public boolean isWinner()
	{
		// if player 1's two virtual sites are connected, return true and end
		// the game
		if (currentPlayer == 1 && wqu.connected(n1 * n1, n1 * n1 + 1))
		{
			return true;
		}

		// if player 2's two virtual sites are connected, return true and end
		// the game
		if (currentPlayer == 2 && wqu.connected(n1 * n1 + 2, n1 * n1 + 3))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * pickStartPLayer() - This method uses the Math.random() method to ramdomly
	 * select the player that starts the game. It is only called once at the
	 * start of the game
	 * 
	 */
	public static void pickStartPlayer()
	{
		int playerSelect = (int) (Math.random() * 2) + 1;
		currentPlayer = playerSelect;
	}

	/**
	 * printBoard() - This method prints a readable version of the 2d board
	 * array to the console. It is designed to inprove the game play in the
	 * console
	 * 
	 */
	public void printBoard()
	{

		for (int i = 0; i < n1; i++)
		{

			StdOut.print("[ ");
			for (int j = 0; j < n1; j++)
			{

				StdOut.print(board[i][j] + " ");
			}
			StdOut.print("]\n");
		}
	}

	/**
	 * connectionTest() - This method was used as a test during the coding of
	 * the hex class. Takes in the players x and y coordinates and checks to see
	 * if the virtual sites associated with each player are activated. ** Just
	 * for testing in the console**
	 * 
	 * @param x
	 *            - x coords
	 * @param y
	 *            - y coords
	 */
	public void connectionTest(int x, int y)
	{
		// Assigns an int to playerPosition using the given coordinates
		int playerPosition = (n1 * x) + y;

		if (currentPlayer == 1)
		{
			// if the players position and the virtual site are connected
			if (wqu.connected(playerPosition, n1 * n2))
			{
				StdOut.print(" Player 1's top virtual site is connected! \n");
				// if the virtual site is connected, a string will print to
				// screen.
			}
			else
			{
				StdOut.print(" Top not connected!!! \n");
			}
		}

		if (currentPlayer == 1)
		{
			if (wqu.connected(playerPosition, n1 * n2 + 1))
			{
				StdOut.print(" Player 1's bottom virtual site is connected! \n");
				// if the virtual site is connected, String will print to
				// screen.
			}
			else
			{
				StdOut.print(" Bottom not connected! \n");
			}
		}

		if (currentPlayer == 2)
		{
			if (wqu.connected(playerPosition, n1 * n2 + 2))
			{
				StdOut.print(" Player 2's left virtual site is connected! \n");
				// if the virtual site is connected, String will print to
				// screen.
			}
			else
			{
				StdOut.print(" Left side not connected! \n");
			}
		}

		if (currentPlayer == 2)
		{
			if (wqu.connected(playerPosition, n1 * n2 + 3))
			{
				StdOut.print(" Player 2's right virtual site is connected! \n");
				// if the virtual site is connected, String will print to
				// screen.
			}
			else
			{
				StdOut.print(" Right side not connected! \n");
			}
		}

	}

	/**
	 * main()- This is the main method in the Hex Class. The game is played in
	 * the console through this method. The while loop ensures that the game
	 * continues until the isWinner() method returns true and the game is over.
	 * 
	 * @param args
	 * 
	 */
	public static void main(String[] args)
	{

		BoardGame hexGame = new Hex(11, 11);
		pickStartPlayer();

		while (!hexGame.isWinner())
		{
			System.out.println("It's player " + hexGame.getCurrentPlayer()
					+ "'s turn");
			System.out.println("Enter x and y location:");
			int x = StdIn.readInt();
			int y = StdIn.readInt();

			hexGame.takeTurn(x, y);

		}

		System.out.println("It's over. Player " + hexGame.getCurrentPlayer()
				+ " wins!");

	}

}
