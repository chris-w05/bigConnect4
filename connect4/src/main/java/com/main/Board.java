package com.main;

public class Board {
    //1 -> yellow
    //-1 -> red
    //0 -> empty
    public short[][] boardstate;
    public short previousPiece;
    public boolean gameIsWon;
    private String hash;
    /* 
    0,0 -> 6,0
    |
    0,5 -> 6,5
`   */


    public Board(){
        boardstate =  new short[7][6];
        for( int i = 0; i < 7; i ++){
            for( int j = 0; j < boardstate[i].length; j ++){
                boardstate[i][j] = 0;
            }
        }
        previousPiece = -1;
        gameIsWon = false;
        hash = "";
    }

    public Board( short[][] boardstate, short previousPiece, String hash){
        this.boardstate = new short[7][6];
        for( int i = 0; i < 7; i ++){
            for( int j = 0; j < 6; j ++){
                this.boardstate[i][j] = boardstate[i][j];
            }
        }
        this.previousPiece = previousPiece;
        gameIsWon = false;
        this.hash = "" + hash;
    }

    public String getHash(){
        return hash;
    }

    public void addPiece(int column){
        if( column > 6 || column < 0){
            //System.out.println("Board failed to add piece - column not valid");
            return;
        }
        if( gameIsWon){
            //System.out.println("game is won!");
            return;
        }
        int row = 5;
        //System.out.println("Adding Piece on column: " + column);
        while( row >= 0){
            //System.out.println("Column: " + column + " Row: " + row);
            if (boardstate[column][row] == 0){
                boardstate[column][row] = (short) (-1 * previousPiece);
                previousPiece *= -1;
                //printBoard();
                hash += column;
                return;
            }
            row --;
        }
        //System.out.println("Board failed to add piece");
        //checkWin();
        //printBoard();
    }

    public void addPieceWithPrint(int column){
        if( column > 6 || column < 0){
            System.out.println("Board failed to add piece - column not valid");
            return;
        }
        if( gameIsWon){
            System.out.println("game is won!");
            return;
        }
        int row = 5;
        //System.out.println("Adding Piece on column: " + column);
        while( boardstate[column][row] != 0 || row >= 0){
            //System.out.println("Column: " + column + " Row: " + row);
            if (boardstate[column][row] == 0){
                boardstate[column][row] = (short) (-1 * previousPiece);
                previousPiece *= -1;
                //printBoard();
                return;
            }
            row --;
            if( row < 0) break;
        }
        System.out.println("Board failed to add piece");

    }

    public void printBoard( ){
        for( int j = 0; j < 6; j ++){
            for( int i = 0; i < 7; i ++){
                System.out.print( boardstate[i][j] + "\t");
            }   
            System.out.println("\n");
        }
        System.out.println();
    }

    public int checkWin(){
        int lookingFor = boardstate[0][0];
        int inARow = 0;
        //vertical check
        for( int i = 0 ; i < 7; i ++){
            for ( int offset = 0; offset < 3; offset ++){
                lookingFor = boardstate[i][offset];
                for( int j = offset; j < offset + 4; j ++){
                    if( boardstate[i][j] == lookingFor){
                        inARow ++;
                    }
                    
                }
                if( inARow == 4 && lookingFor != 0){
                    //System.out.println( "Win color: " + lookingFor);
                    gameIsWon = true;
                    return lookingFor; 
                } 
                inARow =0;
            }
        }
        //horizontal 
        for( int j = 0 ; j < 6; j ++){
            for ( int offset = 0; offset < 4; offset ++){
                lookingFor = boardstate[offset][j];
                for( int i = offset; i < offset + 4; i ++){
                    if( boardstate[i][j] == lookingFor){
                        inARow ++;
                    }
                } 
                if( inARow == 4 && lookingFor != 0){
                    //System.out.println( "Win color: " + lookingFor);
                    gameIsWon = true;
                    return lookingFor; 
                } 
                inARow =0;
            }
        }
        //diagonal up
        for( int i = 0; i < 4; i ++){
            for( int j = 5; j > 2 ; j --){
                lookingFor = boardstate[i][j];
                for( int offset = 0; offset < 4; offset ++){
                    if( boardstate[i + offset][j - offset] == lookingFor){
                        inARow ++;
                    }
                }
                if( inARow == 4 && lookingFor != 0){
                    //System.out.println( "Win color: " + lookingFor);
                    gameIsWon = true;
                    return lookingFor; 
                } 
                inARow =0;
            }
        }
        //diagonal down
        for( int i = 0; i < 4; i ++){
            for( int j = 0; j < 3 ; j ++){
                lookingFor = boardstate[i][j];
                for( int offset = 0; offset < 4; offset ++){
                    if( boardstate[i + offset][j + offset] == lookingFor){
                        inARow ++;
                    }
                }
                if( inARow == 4 && lookingFor != 0){
                    //System.out.println( "Win color: " + lookingFor);
                    gameIsWon = true;
                    return lookingFor; 
                } 
                inARow =0;
            }
        }
        //System.out.println("Win color: 0");
        return 0;
    }
    
    public Board copyBoard(){
        Board newBoard = new Board();
        newBoard.boardstate = boardstate;
        newBoard.previousPiece = previousPiece;
        newBoard.gameIsWon = gameIsWon;
        return newBoard;
    }

    public int[] linearize(){
        int[] values = new int[42];
        int index = 0;
        for( short[] i : boardstate){
            for( short j : i){
                values[index] = j;
                index ++;
            }
        }
        return values;
    }
}
