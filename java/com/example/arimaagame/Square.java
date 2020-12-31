package com.example.arimaagame;

public class Square {

    Piece piece;

    boolean empty;

    public Square() {
        piece = null;
        empty = true;
    }
    char readSquare(){
        if(isEmpty()){
            return ' ';
        }

        else return piece.getLetter();
    }

    Piece getPiece(){
        return piece;
    }
    Piece.PieceColour getColour(){
        return piece.getColour();
    }

    int getLevel(){
        return piece.getLevel();
    }

    Piece releasePiece(){

        Piece temppiece = piece;
        piece = null;

        empty = true;
        return temppiece;
    }
    void acceptPiece(Piece piece){
        if(null == piece)
            throw(new IllegalStateException("Piece cannot be null"));
        this.piece = piece;
        empty = false;
    }

    boolean isEmpty(){
        return empty;
    }

}