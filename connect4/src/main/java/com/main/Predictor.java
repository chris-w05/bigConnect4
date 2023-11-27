package com.main;

import java.sql.Connection;
import java.sql.SQLException;

public class Predictor extends Player{
    private short color;
    private int depthLimit;
    private Connection connection;

    public Predictor( int depth , short color ){
        this.depthLimit = depth;
        this.color = color;
    }

    public int nextColumn(Board board){
        double[] winchances = new double[7];
        board.printBoard();
        connection = LocalDatabase.connect();

        for( int i = 0; i < 7; i ++){
            winchances[i] = predict(new Board(board.boardstate, board.previousPiece, board.getHash()), i, 0)[0];
        }

        int greatestIndex = 0;
        double greatestValue = winchances[0];
        for( int i = 1; i < winchances.length ; i ++ ){
            if( winchances[i] > greatestValue ){
                greatestIndex = i;
                greatestValue = winchances[i];
            }
        }

        for( int i = 0 ; i < winchances.length ; i ++){
            System.out.print( winchances[i] + " ");
        }
        System.out.println();
        
        try{
            connection.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return greatestIndex;
    }

    public double[] predict( Board board, int column, int depth ){
        board.addPiece( column );
        double[] winchance = new double[2];
        int wintype = board.checkWin();
        //System.out.println("Recursion @ depth  " + depth + " and column " + column);

        try{
            double prevCalc = LocalDatabase.checkForValue(connection, color, board.getHash());
            winchance[0] = prevCalc;
            winchance[1] = 0;
            return winchance;
        }
        catch( SQLException dontCare) {
            if( wintype != 0 ){
                //System.out.println("Win found @ depth " + depth);
                winchance[0] = ((wintype*color) / (Math.pow(7, (double)depth) ));
                winchance[1] = 0;
                try{
                    LocalDatabase.insertData(connection, color, board.getHash(), wintype);
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                return winchance;
            }
            else if( depth > depthLimit ){
                //System.out.println("depth limit reached");
                winchance[0] = 0;
                winchance[1] = 1;
                return winchance;
            }
            else{
                depth ++;
                for( int i = 0; i < 7; i ++){
                    //System.out.println("Recursion @ depth  " + depth + " and column " + i);
                    double[] chances = predict(new Board(board.boardstate, board.previousPiece, board.getHash()), i, depth);
                    winchance[0] += chances[0];
                    winchance[1] += chances[1];
                }
                if( winchance[1] == 0){
                    try{
                    LocalDatabase.insertData(connection, color, board.getHash(), winchance[0]);
                    }
                    catch(SQLException e){
                        e.printStackTrace();
                    }
                }
                return winchance;
                
            }
        }
    }
}
