import java.util.Arrays;
import java.util.Random;
import java.util.PriorityQueue;

public class A2Q1_nQueens {
    public static int n = 5;
    public int[][] boardState = new int[n][n];
    public int[][] constraintGraph = new int[n][n];
    public int[] filledColumns = new int[n];

    public int
    public void addConstraints(int qx,int qy){
        constraintGraph[qx][qy] ++;
        for(int count = 1; count < n ; count ++){
            // checking the diagonal direction down and right
            if(qx + count < n && qy + count < n){
                constraintGraph[qx + count][qy + count] ++;
            }
            // Checking diagonal direction up and right
            if(qx + count < n && qy-count >= 0){
                constraintGraph[qx + count][qy - count] ++;
            }
            // Checking diagonal direction down and left
            if(qx - count >= 0 && qy + count < n){
                constraintGraph[qx-count][qy + count] ++;
            }
            // Checking diagonal direction up and left
            if(qx - count >= 0 && qy - count >= 0){
                constraintGraph[qx-count][qy-count] ++;
            }


            if(boardState[count][qy] == 0){
                constraintGraph[count][qy] ++;

            }
            if(qx - count >= 0 && boardState[qx-count][qy] ==0){
                if(qx - count >= 0){
                    constraintGraph[qx - count][qy] ++;
                }
            }

            //checking the column
            if(boardState[qx][count] == 0){
                constraintGraph[qx][count] ++;

            }

            if(qy - count >= 0 && boardState[qx][qy - count] == 0){
                constraintGraph[qx][qy - count] ++;
            }
        }
    }
    public void remConstraints(int qx,int qy){
        // This will check if there are any other queens in the same row to the right
        constraintGraph = new int[n][n];
    }

    public void createBoard(){
        Random rand = new Random();
        int y;
        // randomly assigning the queens to different positions in the board
        // a 1 at a position means there is a queen present
        // will be assigned one at each column
        for(int i = 0; i < n; i ++){
            y = rand.nextInt(n);
            if(boardState[i][y] == 0) {
                boardState[i][y] = 1;
            }

        }
    }
    public void updateConstraintGraph(boolean moveQueen){
        int[][] newGraph = new int[n][n];
        if(moveQueen){
            for (int y = 0; y < n; y ++) {
                if (filledColumns[y] == 0) {
                    boardState[1][y] = 1;
                    addConstraints(1, y);
                    moveQueen = false;
                    break;
                }
            }
        }


        for(int x = 0; x < n; x ++){
            for (int y = 0; y < n; y ++) {
                if (constraintGraph[x][y] == 0 && moveQueen) {
                    boardState[1][y] = 1;
                    addConstraints(1, y);
                    moveQueen = false;
                } else if (boardState[x][y] == 1) {
                    addConstraints(x, y);
                    filledColumns[y] = 1;
                }
            }
        }
    }
    public int getBadQueens(int [][] boardState, int [][] constraintGraph){
        int numQFound = 0;
        int x = 0;
        int y = 0;
        while(numQFound < n){
            if (y == n - 1) {
                x ++;
                y = 0;
            }
            else {
                y++;
            }
            if(constraintGraph[x][y] > 1 && boardState[x][y] == 1){
                numQFound ++;
            }
        }
        return numQFound;
    }

    public void findBestNeighbour(){
        // This will update the boardState with the best neighbour boardState
        int[][] bestBoard = boardState;
        int bestHeuristic = getBadQueens(boardState, constraintGraph);

        int [][] neighbourBoard = new int[n][n];
        for(int i = 0; i < n ; i ++){
            for(int j = 0; j < n; j ++){
                neighbourBoard[i][j] = boardState[i][j];
            }
        }
    }
    public int moveBadQueen(){
        /*
        This will find the first constrained queen and move it so that it is no longer constrained
        will return 1 if moves a queen and 0 if there are no constrained queens such that the program is complete
         */
        for(int x = 0; x < n; x ++){
            for(int y = 0; y < n; y ++){
                if(constraintGraph[x][y] > 0 && boardState[x][y] == 1){
                    remConstraints(x,y);
                    boardState[x][y] = 0;
                    updateConstraintGraph(true);
                    return 1;
                }
            }
        }
        return 0;
    }
    public static void main(String[] args) {
        A2Q1_nQueens chess = new A2Q1_nQueens();
        chess.createBoard();
        chess.updateConstraintGraph(false);
        while(chess.moveBadQueen() == 1){
            for(int i = 0 ; i < n; i ++){
                System.out.println(Arrays.toString(chess.boardState[i]));

            }
            System.out.println("+===========================");

        }

        for(int i = 0 ; i < n; i ++){
            System.out.println(Arrays.toString(chess.boardState[i]));
        }

    }
}
