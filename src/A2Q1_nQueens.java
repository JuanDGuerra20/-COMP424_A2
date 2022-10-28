import java.util.Arrays;
import java.util.Random;
public class A2Q1_nQueens {
    public static int n = 40;
    public int[][] boardState = new int[n][n];
    public int[][] constraintGraph = new int[n][n];
    public int[] filledColumns = new int[n];

    public int[][] previousState = new int[n][n];

    public void addConstraints(int qx,int qy, int[][] constraintGraph){
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
    /*
    public void remConstraints(int qx,int qy){
        // This will check if there are any other queens in the same row to the right
        constraintGraph = new int[n][n];
    }*/

    public void createBoard(){
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
        for(int x = 0; x < n; x ++){
            for (int y = 0; y < n; y ++) {
                if (boardState[x][y] == 1) {
                        filledColumns[x] = y;
                }
            }
        }
    }
    public void updateConstraintGraph(boolean globalBoard, int[][] board, int[][] newGraph){


        for(int x = 0; x < n; x ++){
            for (int y = 0; y < n; y ++) {

                if (board[x][y] == 1) {
                    addConstraints(x, y, newGraph);
                    if(globalBoard){
                        filledColumns[x] = y;
                    }
                }
            }
        }
    }
    public int getBadQueens(int [][] boardState, int [][] constraintGraph){
        int numConstraints = 0;
        int numQFound = 0;
        int x = 0;
        int y = -1;
        while(numQFound < n){
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
                numQFound++;
            }
        }
        return numConstraints;
    }

    public int findBestNeighbour(){
        // This will update the boardState with the best neighbour boardState
        previousState = copyBoard(this.boardState);
        int bestHeuristic = getBadQueens(boardState, constraintGraph);
        int [][] neighbourBoard = new int[n][n];
        int [][] neighbourConstraints;
        int neighbourHeuristic;
        for(int i = 0; i < n ; i ++){
            for(int j = 0; j < n; j ++){
                neighbourBoard[i][j] = boardState[i][j];
                if(boardState[i][j] == 1){
                    filledColumns[i] = j;
                }
            }
        }

        // this loop will go through all the neighbours of the current state and see which is the best
        // this will only apply one move at time and see which state is best after that one move
        for(int i = 0; i < n ; i ++){
            neighbourBoard[i][filledColumns[i]] = 0;
            for(int j = 0; j < n; j ++){
                if(j == filledColumns[i]){
                    continue;
                }
                neighbourConstraints = new int[n][n];
                neighbourBoard[i][j] = 1;
                updateConstraintGraph(false, neighbourBoard, neighbourConstraints);
                neighbourHeuristic = getBadQueens(neighbourBoard,neighbourConstraints);
                if(bestHeuristic > neighbourHeuristic){
                    this.boardState = copyBoard(neighbourBoard);
                    bestHeuristic = neighbourHeuristic;
                    this.constraintGraph = copyBoard(neighbourConstraints);
                }
                neighbourBoard[i][j] = 0;
            }
            neighbourBoard[i][filledColumns[i]] = 1;
        }
        updateFilledColumns();
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
            // this will randomize the board if we are at a local minima
            if(chess.checkEqualStates(chess.previousState, chess.boardState)){
                chess.createBoard();
            }
        }

        for(int i = 0 ; i < n; i ++){
            System.out.println(Arrays.toString(chess.boardState[i]));
        }

    }
}
