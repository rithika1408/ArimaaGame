package com.example.arimaagame;

import android.graphics.Point;

public class CpuPlaceMove extends MoveAction {

    public CpuPlaceMove(Point start, Point end, Piece piece, boolean push) {
        super(start, end, piece, push);
    }

    @Override
    public String toString() {
        return null;
    }

}
