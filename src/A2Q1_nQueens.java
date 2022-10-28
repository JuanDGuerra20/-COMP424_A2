import java.util.Arrays;
import java.util.Random;
public class A2Q1_nQueens {
    public static int n = 80;
    public int[][] boardState = new int[n][n];
    public int[][] constraintGraph = new int[n][n];
    public int[] filledColumns = new int[n];

    public int repeatHeuristicCount = 0;
    public int[][] previousState = new int[n][n];

    public void addConstraints(int qx,int qy, int[][] constraintGraph){
        // THis program is O(n)
        //constraintGraph[qx][qy] ++;
        for(int count = 1; count < n ; count ++){
            // checking the diagonal direction down and right
            if(qx + count < n && qy + count < n){
                constraintGraph[qx + count][qy + count] ++;
            }
            // Checking diagonal direction down and left
            if(qx + count < n && qy-count >= 0){
                constraintGraph[qx + count][qy - count] ++;
            }
            // Checking diagonal direction up and right
            if(qx - count >= 0 && qy + count < n){
                constraintGraph[qx-count][qy + count] ++;
            }
            // Checking diagonal direction up and left
            if(qx - count >= 0 && qy - count >= 0){
                constraintGraph[qx-count][qy-count] ++;
            }

            //checking the column
            if(qx + count < n){
                constraintGraph[qx + count][qy] ++;

            }
            // since count > 0, will never be in the same position as the queen we are checking
            if(qx - count >= 0){
                if(qx - count >= 0){
                    constraintGraph[qx - count][qy] ++;
                }
            }

            //checking the row
            if(qy + count < n){
                constraintGraph[qx][qy + count] ++;

            }

            if(qy - count >= 0){
                constraintGraph[qx][qy - count] ++;
            }
        }
    }

    public void createBoard(){
        //O(n^2)
        Random rand = new Random();
        int y;
        // randomly assigning the queens to different positions in the board
        // a 1 at a position means there is a queen present
        // will be assigned one at each column
        for(int j = 0; j < n; j ++){
            y = rand.nextInt(n);
            for(int i = 0; i < n; i ++){
                if(i == y) {
                    boardState[j][y] = 1;
                }
                else{
                    boardState[j][i] = 0;
                }

            }
        }

    }

    public void updateFilledColumns(){
        // this is used to reduce the runtime of searching for existing boardstate positions
        // each entry is the position of a queen
        // O(n^2)
        for(int x = 0; x < n; x ++){
            for (int y = 0; y < n; y ++) {
                if (boardState[x][y] == 1) {
                        filledColumns[x] = y;
                }
            }
        }
    }
    public void updateConstraintGraph(boolean globalBoard, int[][] board, int[][] newGraph){
        // global board is used for when starting the program to save some time and update the filled columns
        // o(N^3)
        for(int x = 0; x < n; x ++){
            for (int y = 0; y < n; y ++) {
                // finding the queens and adding the appropriate constraints to the board
                if (board[x][y] == 1) {
                    addConstraints(x, y, newGraph); // O(n)
                    if(globalBoard){
                        filledColumns[x] = y;
                    }
                }
            }
        }
    }
    public int getBadQueens(int [][] boardState, int [][] constraintGraph){
        // This will find the heuristic of each board state
        // Will count the number of constraints on all of the queens and return that value
        // The less constraints the better (this is our min heuristic)

        //O(N^2)
        int numConstraints = 0;
        int numQFound = 0;
        int x = 0;
        int y = -1;
        while(numQFound < n){ // using while to stop searching once we've found all the queens, saves little bits of time
            if (y == n - 1) {
                x ++;
                y = 0;
            }
            else {
                y++;
            }
            if(constraintGraph[x][y] > 0 && boardState[x][y] == 1){
                numConstraints += constraintGraph[x][y]; // adding the number of constraints from the constraint graph on the queen
                numQFound ++;
            }
            else if(boardState[x][y] == 1){
                numQFound++; // this is if the queen is unconstrained
            }
        }
        return numConstraints;
    }

    public int findBestNeighbour(){
        // This will update the boardState with the best neighbour boardState
        // O(N^5)
        previousState = copyBoard(this.boardState); // o(N^2)this is used to check if we have reached a minima and the board is not being updated
        int oldHeuristic = getBadQueens(boardState, constraintGraph); //O(N^2)
        int bestHeuristic = oldHeuristic;
        int [][] neighbourBoard = new int[n][n];
        int [][] neighbourConstraints;
        int neighbourHeuristic;

        // creating the neighbour board to be the same as the old board and will be modified accordingly
        for(int i = 0; i < n ; i ++){ // O(N^2)
            for(int j = 0; j < n; j ++){
                neighbourBoard[i][j] = boardState[i][j];
                if(boardState[i][j] == 1){
                    filledColumns[i] = j;
                }
            }
        }

        // this loop will go through all the neighbours of the current state and see which is the best
        // this will only apply one move at time and see which state is best after that one move
        for(int i = 0; i < n ; i ++){ // O (N^5)
            neighbourBoard[i][filledColumns[i]] = 0; // want to remove the queen from the old board to find the neighbours
            for(int j = 0; j < n; j ++){
                // This if is saying skip the position if it is the same as the old board state
                if(j == filledColumns[i]){
                    continue;
                }
                // reinitializing the neighbour constraints to create it for the new board
                neighbourConstraints = new int[n][n];
                neighbourBoard[i][j] = 1; // finding the next neighbour by placing a queen
                updateConstraintGraph(false, neighbourBoard, neighbourConstraints); // O(N^3)
                neighbourHeuristic = getBadQueens(neighbourBoard,neighbourConstraints); // O(N^2)

                if(bestHeuristic >= neighbourHeuristic){
                    this.boardState = copyBoard(neighbourBoard);
                    bestHeuristic = neighbourHeuristic;
                    this.constraintGraph = copyBoard(neighbourConstraints);
                }
                // must set it to 0 to not have more than n queens, wil readd the queen at the start of the loop
                neighbourBoard[i][j] = 0;
            }
            neighbourBoard[i][filledColumns[i]] = 1;
        }
        updateFilledColumns(); // want to update the filled columns to the new board to reduce time
        if(oldHeuristic == bestHeuristic){
            // this allows the program to switch boards that have the same heuristic instead of staying on the same one
            // THis helps on larger nqueens because will not be a hard board reset but slight changes could get us out
            // of local minima
            this.repeatHeuristicCount ++;
        }
        return bestHeuristic;
    }

    public boolean checkEqualStates(int[][] state1, int[][] state2){
        //This will return 1 if the states are the same and 0 if they are different
        for(int x = 0 ; x < n; x ++){
            for(int y = 0; y < n; y ++){
                if(state1[x][y] != state2[x][y]){
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] copyBoard(int[][] board){

        // Makes deep copy of the board
        int[][] boardCopy = new int[n][n];
        for(int x = 0; x < n; x ++){
            for(int y = 0; y < n; y ++){
                boardCopy[x][y] = board[x][y];
            }
        }
        return boardCopy;
    }
    public static void main(String[] args) {
        A2Q1_nQueens chess = new A2Q1_nQueens();
        chess.createBoard();
        chess.updateConstraintGraph(true, chess.boardState, chess.constraintGraph);
        while(chess.findBestNeighbour() > 0){
            // this will randomize the board if we are at the same board state as previous iteration (local minima) or the heuristic hasn't changed in 5 trials so we probably won't be decreasing soon or it is switching between states over and over again
            // this update will probably help solve the larger problems while not affecting the smaller sizes too much
            // this is because resetting the board completely will make us start from a possibly bad state
            if(chess.checkEqualStates(chess.previousState, chess.boardState) || chess.repeatHeuristicCount == 5){
                chess.createBoard();
                chess.repeatHeuristicCount = 0;
            }
        }
        int[] assignmentAnswer = new int[n];

        for(int i = 0 ; i < n; i ++){
            System.out.println(Arrays.toString(chess.boardState[i]));
            for(int j = 0; j < n ; j ++){
                if(chess.boardState[i][j] == 1){
                    assignmentAnswer[i] = j;
                }
            }
        }

        System.out.println(Arrays.toString(assignmentAnswer));

    }
}
