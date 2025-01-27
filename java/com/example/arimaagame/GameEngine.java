package com.example.arimaagame;

import android.graphics.Point;
import android.util.Log;
import com.example.arimaagame.Piece.PieceColour;
import com.example.arimaagame.Piece.PieceType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GameEngine {

    private static final String TAG = "GameEngine";

    private Board board;
    private ActionList actionList;
    private boolean moveable[][] = new boolean[8][8];
    private HashMap<Point, MultiView> possibleMoves = new HashMap<Point, MultiView> ();
    private String boardState = "";

    private Point heldPosition;
    private Square held;

    private static final Point[] TRAPS = { new Point(2, 2), new Point(2, 5), new Point(5, 2), new Point(5, 5) };

    public enum GameState {
        GOLDPLACE, SILVERPLACE, GOLDTURN, SILVERTURN, GAMEOVERGOLD, GAMEOVERSILVER
    }

    private GameState gameState = GameState.GOLDPLACE;

    private int turnSteps = 0;

    public GameEngine() {
        board = new Board();
        held = new Square();
        actionList = new ActionList();
    }

    void resetGame() {
        if(!held.isEmpty())
            putDown(heldPosition);
        gameState = GameState.GOLDPLACE;
        board.reset();
        actionList.clear();
        turnSteps = 0;
    }

    GameState getGameState() {
        return gameState;
    }

    boolean isGoldTurn() {
        switch(gameState){
            case GOLDPLACE:
            case GOLDTURN:
            case GAMEOVERGOLD:
                return true;
            default:
                return false;
        }
    }

    String getBoardState() {
        return boardState;
    }

    public void loadBoardState(String boardState) {
        this.boardState = boardState;
    }

    boolean isPlayingState() {
        return (gameState == GameState.GOLDTURN || gameState == GameState.SILVERTURN);
    }

    int getTurnSteps() {
        return turnSteps;
    }

    boolean advanceGameState(boolean record) {
        if (turnSteps < 1 || board.getState().equals(boardState) || actionList.wasPushing())
            return false;

        switch (gameState) {
            case GOLDTURN:
                gameState = GameState.SILVERTURN;
                turnSteps = 0;
                break;

            case SILVERTURN:
                gameState = GameState.GOLDTURN;
                turnSteps = 0;
                break;

            case GOLDPLACE:
                if (!checkGoldPlacement())
                    return false;

                gameState = GameState.SILVERPLACE;
                turnSteps = 0;
                break;

            case SILVERPLACE:
                if (!checkSilverPlacement())
                    return false;

                gameState = GameState.GOLDTURN;
                turnSteps = 0;
                break;
            default:
                return false;
        }

        if(!held.isEmpty())
            putDown(heldPosition);
        boardState = board.getState();

        if(record)
            actionList.addGameAction(new DoneAction());

        checkWin();

        return true;
    }

    private void checkWin() {
        switch (gameState) {
            case GOLDTURN:
                checkSilverRabbitWin();
                checkGoldRabbitWin();
                checkNoRabbitsWin(PieceColour.GOLD, GameState.GAMEOVERSILVER);
                checkNoRabbitsWin(PieceColour.SILVER, GameState.GAMEOVERGOLD);
                break;

            case SILVERTURN:
                checkGoldRabbitWin();
                checkSilverRabbitWin();
                checkNoRabbitsWin(PieceColour.SILVER, GameState.GAMEOVERGOLD);
                checkNoRabbitsWin(PieceColour.GOLD, GameState.GAMEOVERSILVER);
                break;

            default:
                break;
        }
    }

    private void checkGoldRabbitWin(){
        for(int x = 0; x < 8; x++){
            Point goldWinSpot = new Point(x, 7);
            checkRabbitReachWin(goldWinSpot,PieceColour.GOLD, GameState.GAMEOVERGOLD);
        }
    }

    private void checkSilverRabbitWin(){
        for(int x = 0; x < 8; x++){
            Point silverWinSpot = new Point(x, 0);
            checkRabbitReachWin(silverWinSpot, PieceColour.SILVER, GameState.GAMEOVERSILVER);
        }
    }

    private void checkRabbitReachWin(Point p, PieceColour rabbitColour, GameState winState){
        if(!isEmpty(p))
            if(board.getPiece(p).getType() == PieceType.RABBIT && board.getPiece(p).getColour() == rabbitColour)
                gameState = winState;
    }

    private void checkNoRabbitsWin(PieceColour side, GameState winState){
        if(!hasRabbits(side))
            gameState = winState;
    }

    private boolean hasRabbits(PieceColour side){
        for(int x = 0; x < 8; x++)
            for(int y = 0; y < 8; y++){
                Point p = new Point(x, y);
                if(!isEmpty(p))
                    if(board.getPiece(p).getType() == PieceType.RABBIT && board.getPiece(p).getColour() == side)
                        return true;
            }

        return false;
    }

    void checkTraps() {
        for (Point trap : TRAPS) {
            if (!isEmpty(trap)) {
                if (!isSafe(trap)) {
                    actionList.addGameAction(new RemoveAction(trap, board
                            .getPiece(trap)));
                    board.remove(trap);
                }
            }
        }
    }

    void advanceStep(int steps) {
        if (isPlayingState()) {
            checkTraps();

            if (turnSteps + steps > 4)
                throw new IllegalStateException(steps + " steps cannot be taken when " + turnSteps + " steps have already been taken.");
        }
        turnSteps+= steps;
    }

    boolean checkGoldPlacement() {
        for (int x = 0; x < 8; x++) {
            for (int y = 2; y < 4; y++) {

                if (!isEmpty(new Point(x, y))) {
                    return false;
                }
            }
        }
        for (int xf = 0; xf < 8; xf++) {
            for (int yf = 0; yf < 2; yf++) {
                Point p = new Point(xf, yf);

                if (isEmpty(p))
                    board.placeNewPiece(p, new Piece(PieceType.RABBIT, PieceColour.GOLD));
            }
        }

        return true;
    }
    boolean checkSilverPlacement() {
        for (int x = 0; x < 8; x++) {
            for (int y = 4; y < 6; y++) {
                // squares must be empty
                if (!isEmpty(new Point(x, y))) {
                    if (board.getLevel(new Point(x, y)) != 1)
                        return false;
                }
            }
        }

        for (int xf = 0; xf < 8; xf++) {
            for (int yf = 6; yf < 8; yf++) {
                Point p = new Point(xf, yf);

                if (isEmpty(p))
                    board.placeNewPiece(p, new Piece(PieceType.RABBIT, PieceColour.SILVER));
            }
        }

        return true;
    }


    char getLetter(Point p) {
        return board.getLetter(p);
    }
    char getHeldLetter() {
        return held.getPiece().getLetter();
    }
    boolean heldIsEmpty() {
        return held.isEmpty();
    }

    void pickUp(Point p){
        held.acceptPiece(board.remove(p));
    }

    void putDown(Point p){
        board.placeNewPiece(p, held.getPiece());
        held.releasePiece();
    }

    boolean isEmpty(Point p){
        return board.isEmpty(p);
    }

    boolean trySelectSquare(Point p) {
        if (isEmpty(p) || gameState == GameState.GAMEOVERGOLD || gameState == GameState.GAMEOVERSILVER)
            return false;
        //Bracket 1
        if (isPlayingState() && turnSteps >= 4)
            return false;

        if (!actionList.isEmpty() && actionList.wasPushing()) {
            if (couldPushLastMove(p)) {
                selectSquare(p, false);
                return true;
            } else
                return false;
        }

        if (isRightTurnForSelection(board.getColour(p))){

            if(!isFrozen(p)) {
                selectSquare(p, false);
                return true;
            }
        }

        else{
            //Bracket 1
            if (isPlayingState() && isControlled(p) && turnSteps <= 2) {
                selectSquare(p, true);
                return true;
            }

            if (!actionList.isEmpty() && couldGetPulledByLastMove(p)) {
                selectSquare(p, false);
                return true;
            }
        }

        return false;
    }

    void selectSquare(Point p, boolean couldBePushing) {
        if(!heldIsEmpty()){
            returnHeld();
        }

        pickUp(p);
        heldPosition = p;
        setMoveable(couldBePushing);
    }

    Point getHeldPoint() {
        return heldPosition;
    }

    boolean[][] getMoveable(){
        return moveable;
    }
    boolean requestMove(Point p) {

        if (heldIsEmpty()) {
            return false;
        }

        Piece heldPiece = held.getPiece();
        if (!moveable[p.x][p.y] || p == heldPosition) {
            returnHeld();
            clearMoveable();
            return false;
        }

        if (!isPlayingState()) {

            if(!isEmpty(p))
                board.makeMove(p, heldPosition);

            actionList.addMove(new PlaceMove(heldPosition, p, heldPiece));
        }


        if (isPlayingState()) {

            if (isRightTurnForSelection(heldPiece.getColour())) {

                if(areAdjacent(heldPosition, p)){
                    actionList.addMove(new ShiftMove(heldPosition, p, held.getPiece(), false));
                }
                else{
                    actionList.addMove(possibleMoves.get(p));
                }
            }
            else if (couldGetPulledByLastMove(heldPosition, held.getPiece())) {
                actionList.addMove(new ShiftMove(heldPosition, p, held.getPiece(), false));
            }
            else {
                actionList.addMove(new ShiftMove(heldPosition, p, held.getPiece(), true));
            }

        }

        putDown(p);
        if(null != possibleMoves.get(p)) {
            advanceStep(possibleMoves.get(p).getSteps());
        }
        else{
            advanceStep(1);
        }
        clearMoveable();
        return true;
    }


    boolean requestMove(CpuPlaceMove m) {

        if (!isEmpty(m.getEnd()) || isEmpty(m.getStart())) {
            Log.v(TAG, "CPU request rejected!");
            return false;
        }

        board.makeMove(m.getStart(), m.getEnd());
        return true;
    }

    boolean requestRevertMove() {
        if (turnSteps <= 0 || !actionList.isLastMoveShiftOrMulti())
            return false;

        returnHeld();
        clearMoveable();

        if(turnSteps - actionList.getLastMoveSteps() < 0)
            throw new IllegalStateException(actionList.getLastMoveSteps()+" cannot be reverted when only "+turnSteps+" steps have been taken.");

        turnSteps-= actionList.getLastMoveSteps();

        CpuPlaceMove revertedMove = actionList.getRevertedMove();
        Log.v(TAG, "Reverted: " + revertedMove.getPiece().getLetter());

        RemoveAction removeAction = actionList.revertMoveAndGetRemoveAction();
        if (null != removeAction) {
            board.placeNewPiece(removeAction.getPosition(), removeAction.getPiece());
        }

        requestMove(revertedMove);
        return true;
    }

    public String getHistory(){
        return actionList.getHistory();
    }
    public void replayHistory(String history){

        resetGame();

        if(history == null || history.length() == 0)
            return;

        ArrayList<GameAction> actions = actionList.getHistoryFromString(history);

        for(GameAction action: actions){
            replayAction(action);
        }
    }

    private void replayAction(GameAction action){
        if(action instanceof DoneAction){
            advanceGameState(true);
        }

        else if(action instanceof MoveAction){
            replayMove((MoveAction) action);
        }
    }

    private void replayMove(MoveAction move){
        trySelectSquare(move.start);
        requestMove(move.end);
    }

    private void returnHeld() {

        if (!held.isEmpty())
            putDown(heldPosition);

    }

    private boolean couldPushLastMove(Point pPusher) {
        Point followUpMove = actionList.getLastMoveSource();
        Piece pushee = actionList.getLastPiece();
        Piece pusher = board.getPiece(pPusher);

        if (null == followUpMove || null == pushee)
            return false;

        if (areAdjacent(followUpMove, pPusher)) {
            if (!pushee.isSameColour(pusher) && pusher.isBigger(pushee)) {
                if (!isFrozen(pPusher))
                    return true;
            }
        }

        return false;
    }

    private boolean couldGetPulledByLastMove(Point pPullee) {
        Point destination = actionList.getLastMoveSource();
        Piece puller = actionList.getLastPiece();
        Piece pullee = board.getPiece(pPullee);

        if (null == destination || null == puller)
            return false;

        if (areAdjacent(destination, pPullee)) {
            if (!pullee.isSameColour(puller) && puller.isBigger(pullee) && !actionList.wasCompletingPush()) {
                return true;
            }
        }

        return false;
    }

    private boolean couldGetPulledByLastMove(Point pPullee, Piece pullee) {
        Point destination = actionList.getLastMoveSource();
        Piece puller = actionList.getLastPiece();

        if (null == destination || null == puller)
            return false;

        if (areAdjacent(destination, pPullee)) {
            if (!pullee.isSameColour(puller) && puller.isBigger(pullee)) {
                return true;
            }
        }

        return false;
    }

    private boolean areAdjacent(Point p1, Point p2) {
        if (p1.y == p2.y && (p1.x == p2.x + 1 || p1.x == p2.x - 1))
            return true;
        else
            return (p1.x == p2.x && (p1.y == p2.y + 1 || p1.y == p2.y - 1));
    }

    private Set<Point> getFilledAdjacents(Point p) {

        Set<Point> filledAdjacents = new HashSet<Point>();

        Point testRight = new Point(p.x + 1, p.y);
        if (p.x < 7 && !isEmpty(testRight))
            filledAdjacents.add(testRight);

        Point testLeft = new Point(p.x - 1, p.y);
        if (p.x > 0 && !isEmpty(testLeft))
            filledAdjacents.add(testLeft);

        Point testUp = new Point(p.x, p.y + 1);
        if (p.y < 7 && !isEmpty(testUp))
            filledAdjacents.add(testUp);

        Point testDown = new Point(p.x, p.y - 1);
        if (p.y > 0 && !isEmpty(testDown))
            filledAdjacents.add(testDown);

        return filledAdjacents;
    }

    private Set<Point> getEmptyAdjacents(Point p) {

        Set<Point> emptyAdjacents = new HashSet<Point>();

        Point testRight = new Point(p.x + 1, p.y);
        if (p.x < 7 && isEmpty(testRight))
            emptyAdjacents.add(testRight);

        Point testLeft = new Point(p.x - 1, p.y);
        if (p.x > 0 && isEmpty(testLeft))
            emptyAdjacents.add(testLeft);

        Point testUp = new Point(p.x, p.y + 1);
        if (p.y < 7 && isEmpty(testUp))
            emptyAdjacents.add(testUp);

        Point testDown = new Point(p.x, p.y - 1);
        if (p.y > 0 && isEmpty(testDown))
            emptyAdjacents.add(testDown);

        return emptyAdjacents;
    }

    private boolean isRightTurnForSelection(Piece.PieceColour colour) {
        return (gameState == GameState.GOLDTURN || gameState == GameState.GOLDPLACE) == (colour == Piece.PieceColour.GOLD);
    }

    private boolean isSameColour(Point p1, Point p2) {
        return board.getPiece(p1).isSameColour(board.getPiece(p2));
    }


    private boolean isBigger(Point p1, Point p2) {
        return board.getPiece(p1).isBigger(board.getPiece(p2));
    }


    private boolean threatening(Point p1, Point p2) {
        return (!isSameColour(p1, p2) && isBigger(p1, p2));
    }

    private boolean threatening(Piece p1, Piece p2) {
        return (!p1.isSameColour(p2) && p1.isBigger(p2));
    }


    private boolean freezing(Point p1, Point p2) {
        return (threatening(p1, p2) && !isSafe(p2));
    }


    private boolean freezing(Point p1, Point p2, Piece piece) {
        return (threatening(board.getPiece(p1), piece) && !isSafe(p2, piece));
    }

    private boolean controlling(Point p1, Point p2) {

        return (threatening(p1, p2) && !isFrozen(p1));
    }


    private boolean isSafe(Point p) {

        Set<Point> filledAdjacents = getFilledAdjacents(p);

        for (Point adjacent : filledAdjacents) {
            if (isSameColour(adjacent, p)) {
                return true;
            }
        }

        return false;
    }


    private boolean isSafe(Point p, Piece piece) {

        Set<Point> filledAdjacents = getFilledAdjacents(p);

        for (Point adjacent : filledAdjacents) {
            if (piece.isSameColour(board.getPiece(adjacent))) {
                return true;
            }
        }

        return false;
    }

    private boolean isFrozen(Point p) {

        Set<Point> filledAdjacents = getFilledAdjacents(p);

        for (Point adjacent : filledAdjacents) {
            if (freezing(adjacent, p)) {
                return true;
            }
        }

        return false;
    }
    private boolean isFrozen(Point p, Piece piece) {

        Set<Point> filledAdjacents = getFilledAdjacents(p);

        for (Point adjacent : filledAdjacents) {
            if (freezing(adjacent, p, piece)) {
                return true;
            }
        }

        return false;
    }

    private boolean isControlled(Point p) {

        Set<Point> filledAdjacents = getFilledAdjacents(p);

        for (Point adjacent : filledAdjacents) {
            if (controlling(adjacent, p)) {
                return true;
            }
        }

        return false;
    }

    private void setPlaceMoveable() {
        if (gameState == GameState.GOLDPLACE) {

            for (int i = 0; i < 8; i++)
                for (int k = 0; k < 2; k++)
                    moveable[i][k] = true;
        }

        else {

            for (int i = 0; i < 8; i++)
                for (int k = 6; k < 8; k++)
                    moveable[i][k] = true;
        }
    }


    private void setMoveable(boolean couldBePushing) {

        clearMoveable();


        if (held.isEmpty() || (isPlayingState() && turnSteps >= 4)) {
            return;
        }


        if (gameState == GameState.GOLDPLACE || gameState == GameState.SILVERPLACE) {
            setPlaceMoveable();
            return;
        }

        if (!actionList.isEmpty()) {
            if (actionList.wasPushing()) {
                moveable[actionList.getLastMoveSource().x][actionList
                        .getLastMoveSource().y] = true;
                return;
            }


            if (!isRightTurnForSelection(held.getColour())
                    && !couldBePushing) {
                moveable[actionList.getLastMoveSource().x][actionList
                        .getLastMoveSource().y] = true;
                return;
            }
        }


        if (!isRightTurnForSelection(held.getColour())){
            Set<Point> emptyAdjacents = getEmptyAdjacents(heldPosition);

            for (Point p : emptyAdjacents) {
                moveable[p.x][p.y] = true;
            }
        }
        else{
            generateMoveable();
        }
    }


    private void generateMoveable() {
        if (heldIsEmpty())
            throw new IllegalStateException("Tried to generate moves without a held piece.");

        Set<Point> possiblePoints = getEmptyAdjacents(heldPosition);
        filterRabbitShiftMoves(possiblePoints, heldPosition, held.getPiece(), gameState);

        for (Point p : possiblePoints) {
            possibleMoves.put(p, new MultiView(new ShiftMove(heldPosition, p, held.getPiece(), false)));
        }

        while (!possiblePoints.isEmpty()){

            Set<Point> newPossiblePoints = new HashSet<Point>();

            for (Point p : possiblePoints) {
                Set<Point> newPoints = getEmptyAdjacents(p);
                filterRabbitShiftMoves(newPoints, p, held.getPiece(), gameState);

                if(moveable[p.x][p.y]){
                    continue;
                }
                else if(p.equals(heldPosition)){
                    addPointsToPossibleMoves(p, newPoints, newPossiblePoints);
                }
                else if(isFrozen(p, held.getPiece()) || (Arrays.asList(TRAPS).contains(p) && !isSafe(p, held.getPiece())) ){
                    moveable[p.x][p.y] = true;
                }
                else if(turnSteps + possibleMoves.get(p).getSteps() <= 4) {
                    moveable[p.x][p.y] = true;
                    if(turnSteps + possibleMoves.get(p).getSteps() < 4){
                        addPointsToPossibleMoves(p, newPoints, newPossiblePoints);
                    }
                }
            }

            possiblePoints.clear();
            possiblePoints.addAll(newPossiblePoints);
        }

        return;
    }

    private void filterRabbitShiftMoves(Set<Point> points, Point src, Piece piece, GameState state){

        if(piece.getLetter() == 'R' && state == GameState.GOLDTURN) {
            Point below = new Point(src.x, src.y-1);
            if(points.contains(below))
                points.remove(below);
        }

        else if(getHeldLetter() == 'r' && gameState == GameState.SILVERTURN) {
            Point above = new Point(src.x, src.y+1);
            if(points.contains(above))
                points.remove(above);
        }
    }

    private void addPointsToPossibleMoves(Point source, Set<Point> newPoints, Set<Point> newPossiblePoints){
        for(Point newp : newPoints){

            if(!possibleMoves.containsKey(newp)) {
                newPossiblePoints.add(newp);

                MultiView newMove = new MultiView(possibleMoves.get(source));
                newMove.addShift(new ShiftMove(source, newp, held.getPiece(), false));

                possibleMoves.put(newp, newMove);
            }
        }
    }

    private void clearMoveable() {

        for (int i = 0; i < 8; i++)
            for (int k = 0; k < 8; k++)
                moveable[i][k] = false;

        possibleMoves.clear();
    }

}
