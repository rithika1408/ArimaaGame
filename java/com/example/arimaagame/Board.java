package com.example.arimaagame;

import android.graphics.Point;
import com.example.arimaagame.Piece.PieceType;
import com.example.arimaagame.Piece.PieceColour;

public class Board {
    Square[][] squares = new Square[8][8];

    public Board() {
        for(int i = 0; i < 8; i++)
            for(int k = 0; k < 8; k++)
            {
                squares[i][k] = new Square();
            }

        squares[0][5].acceptPiece(new Piece(PieceType.ELEPHANT, PieceColour.SILVER));
        squares[1][5].acceptPiece(new Piece(PieceType.CAMEL, PieceColour.SILVER));
        squares[2][5].acceptPiece(new Piece(PieceType.HORSE, PieceColour.SILVER));
        squares[3][5].acceptPiece(new Piece(PieceType.HORSE, PieceColour.SILVER));
        squares[4][5].acceptPiece(new Piece(PieceType.DOG, PieceColour.SILVER));
        squares[5][5].acceptPiece(new Piece(PieceType.DOG, PieceColour.SILVER));
        squares[6][5].acceptPiece(new Piece(PieceType.CAT, PieceColour.SILVER));
        squares[7][5].acceptPiece(new Piece(PieceType.CAT, PieceColour.SILVER));

        squares[0][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
        squares[1][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
        squares[2][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
        squares[3][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
        squares[4][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
        squares[5][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
        squares[6][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));
        squares[7][4].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.SILVER));

        squares[0][2].acceptPiece(new Piece(PieceType.ELEPHANT, PieceColour.GOLD));
        squares[1][2].acceptPiece(new Piece(PieceType.CAMEL, PieceColour.GOLD));
        squares[2][2].acceptPiece(new Piece(PieceType.HORSE, PieceColour.GOLD));
        squares[3][2].acceptPiece(new Piece(PieceType.HORSE, PieceColour.GOLD));
        squares[4][2].acceptPiece(new Piece(PieceType.DOG, PieceColour.GOLD));
        squares[5][2].acceptPiece(new Piece(PieceType.DOG, PieceColour.GOLD));
        squares[6][2].acceptPiece(new Piece(PieceType.CAT, PieceColour.GOLD));
        squares[7][2].acceptPiece(new Piece(PieceType.CAT, PieceColour.GOLD));

        squares[0][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
        squares[1][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
        squares[2][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
        squares[3][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
        squares[4][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
        squares[5][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
        squares[6][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
        squares[7][3].acceptPiece(new Piece(PieceType.RABBIT, PieceColour.GOLD));
    }

    public static String positionToString(Point p){
        switch(p.x){
            case 0:
                return "a" + (p.y + 1);
            case 1:
                return "b" + (p.y + 1);
            case 2:
                return "c" + (p.y + 1);
            case 3:
                return "d" + (p.y + 1);
            case 4:
                return "e" + (p.y + 1);
            case 5:
                return "f" + (p.y + 1);
            case 6:
                return "g" + (p.y + 1);
            default:
                return "h" + (p.y + 1);
        }
    }


    public static Point stringToPosition(String str){
        switch(str.charAt(0)){
            case 'a':
                return new Point(0, Integer.parseInt(str.substring(1)) - 1);
            case 'b':
                return new Point(1, Integer.parseInt(str.substring(1)) - 1);
            case 'c':
                return new Point(2, Integer.parseInt(str.substring(1)) - 1);
            case 'd':
                return new Point(3, Integer.parseInt(str.substring(1)) - 1);
            case 'e':
                return new Point(4, Integer.parseInt(str.substring(1)) - 1);
            case 'f':
                return new Point(5, Integer.parseInt(str.substring(1)) - 1);
            case 'g':
                return new Point(6, Integer.parseInt(str.substring(1)) - 1);
            default:
                return new Point(7, Integer.parseInt(str.substring(1)) - 1);
        }
    }


    boolean isEmpty(Point p){
        return squares[p.x][p.y].isEmpty();
    }
    Piece getPiece(Point p){
        return squares[p.x][p.y].getPiece();
    }
    Piece.PieceColour getColour(Point p){

        return squares[p.x][p.y].getColour();
    }


    int getLevel(Point p){

        return squares[p.x][p.y].getLevel();
    }


    char getLetter(Point p){

        return squares[p.x][p.y].readSquare();
    }

    void makeMove(Point p1, Point p2){
        squares[p2.x][p2.y].acceptPiece(squares[p1.x][p1.y].releasePiece());
    }

    Piece remove(Point p){
        return squares[p.x][p.y].releasePiece();
    }

    void placeNewPiece(Point p, Piece piece){
        squares[p.x][p.y].acceptPiece(piece);
    }
    void reset(){

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                squares[i][j].releasePiece();
            }

        squares[0][5].acceptPiece(new Piece(PieceType.ELEPHANT, PieceColour.SILVER));
        squares[1][5].acceptPiece(new Piece(PieceType.CAMEL, PieceColour.SILVER));
        squares[2][5].acceptPiece(new Piece(PieceType.HORSE, PieceColour.SILVER));
        squares[3][5].acceptPiece(new Piece(PieceType.HORSE, PieceColour.SILVER));
        squares[4][5].acceptPiece(new Piece(PieceType.DOG, PieceColour.SILVER));
        squares[5][5].acceptPiece(new Piece(PieceType.DOG, PieceColour.SILVER));
        squares[6][5].acceptPiece(new Piece(PieceType.CAT, PieceColour.SILVER));
        squares[7][5].acceptPiece(new Piece(PieceType.CAT, PieceColour.SILVER));

        squares[0][2].acceptPiece(new Piece(PieceType.ELEPHANT, PieceColour.GOLD));
        squares[1][2].acceptPiece(new Piece(PieceType.CAMEL, PieceColour.GOLD));
        squares[2][2].acceptPiece(new Piece(PieceType.HORSE, PieceColour.GOLD));
        squares[3][2].acceptPiece(new Piece(PieceType.HORSE, PieceColour.GOLD));
        squares[4][2].acceptPiece(new Piece(PieceType.DOG, PieceColour.GOLD));
        squares[5][2].acceptPiece(new Piece(PieceType.DOG, PieceColour.GOLD));
        squares[6][2].acceptPiece(new Piece(PieceType.CAT, PieceColour.GOLD));
        squares[7][2].acceptPiece(new Piece(PieceType.CAT, PieceColour.GOLD));
    }

    public String getState() {
        String boardState = "";

        for(int y = 7; y >= 0; y--)
            for(int x = 0; x < 8; x++)
                boardState += squares[x][y].readSquare();

        return boardState;
    }

}
