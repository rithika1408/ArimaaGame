package com.example.arimaagame;

import android.graphics.Point;
import android.text.TextUtils;

import java.util.ArrayList;

public class MultiView extends MoveAction {

    public static final char HISTORY_FLAG = 'M';

    ArrayList<ShiftMove> shifts = new ArrayList<ShiftMove>();

    public MultiView(Point start, Point end, Piece piece, ArrayList<ShiftMove> shifts) {
        super(start, end, piece, false);
        this.shifts = shifts;
    }

    public MultiView(ShiftMove firstShift) {
        super(firstShift.getStart(), firstShift.getEnd(), firstShift.getPiece(), false);
        this.shifts.add(firstShift);
    }

    public MultiView(MultiView source) {
        super(source.getStart(), source.getEnd(), source.getPiece(), false);
        this.shifts = new ArrayList<ShiftMove>(source.getShifts());
    }

    public void addShift(ShiftMove newShift){
        this.end = newShift.getEnd();
        this.shifts.add(newShift);
    }

    public ShiftMove toShiftMove(){
        if(shifts.isEmpty() || shifts.size() > 1){
            throw new UnsupportedOperationException("MultiMove cannot be converted to a single shift move");
        }

        return shifts.get(0);
    }

    public String toString(){
        if(shifts.isEmpty()){
            return "";
        }
        else{
            String[] shiftStrings = new String[shifts.size()];

            for(int i = 0; i < shifts.size(); i++){
                shiftStrings[i] = shifts.get(i).toString();
            }

            return TextUtils.join(" ", shiftStrings);
        }
    }

    public int getSteps(){
        return shifts.size();
    }

    public ArrayList<ShiftMove> getShifts() {
        return this.shifts;
    }
}
