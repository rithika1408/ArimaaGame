package com.example.arimaagame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;

final public class GameView extends View {

    static final String TAG = "GameView";

    private static final String PREF_HISTORY = "history";
    private static final String PREF_BOARDSTATE = "boardstate";
    private static final String PREF_PIECESELECTION = "pieceset_selection";


    Bitmap pieces;
    Bitmap moveable_highlight = null;
    Bitmap held_highlight = null;
    Rect src, dst;
    Point dstp, srcp;

    Paint highlightPaint = new Paint();
    int pieceset;
    static final int IMGROWS = 2;
    static final int IMGCOLS = 6;
    static final int HIGHLIGHTALPHA = 30;
    static final int TILES = 8;
    int tilesize;

    boolean twoview;
    static final int INVALID_POINTER_ID = -1;

    boolean dragSelected = false;
    int touchx;
    int touchy;

    int activePointer = INVALID_POINTER_ID;
    GameEngine game;

    //Bracket 2

    public GameView(Context context) {
        super(context);
        initialize(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        game = new GameEngine();
        twoview = true;
        tilesize = 0;
        pieceset = 2;
        dstp = new Point();
        srcp = new Point();
        highlightPaint.setAlpha(HIGHLIGHTALPHA);
    }

    public void loadGame(SharedPreferences pref) {

        game.replayHistory(pref.getString(PREF_HISTORY, ""));
        game.loadBoardState(pref.getString(PREF_BOARDSTATE, ""));
    }

    public void saveGame(SharedPreferences pref) {
        SharedPreferences.Editor save = pref.edit();
        save.putString(PREF_HISTORY, game.getHistory());
        save.putString(PREF_BOARDSTATE, game.getBoardState());
        save.commit();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean imgflip = (twoview && !game.isGoldTurn());

        boolean moveable[][] = new boolean[TILES][TILES];
        moveable = game.getMoveable();

        for (int i = 0; i < TILES; i++) {
            for (int k = 0; k < TILES; k++) {

                dstp.set(i, k);
                dst = getRectFromPosition(dstp);

                if(!game.heldIsEmpty()) {
                    Point heldPoint = game.getHeldPoint();

                    if ((heldPoint.x == i) && (heldPoint.y == k)) {
                        canvas.drawBitmap(held_highlight, dst.left, dst.top, highlightPaint);
                    }

                    if (moveable[i][k]) {
                        canvas.drawBitmap(moveable_highlight, dst.left, dst.top, highlightPaint);
                    }


                    if (dragSelected && dstp.equals(heldPoint))
                        continue;
                }


                srcp = getSpritePosition(game.getLetter(dstp));

                if (null == srcp)
                    continue;

                src = getRectFromSpritePosition(srcp);

                if (imgflip) {
                    dst = getFlippedRect(dst);
                    canvas.save();

                    canvas.rotate(180);
                }

                if (pieces != null)
                    canvas.drawBitmap(pieces, src, dst, null);

                if (imgflip)
                    canvas.restore();
            }
        }


        if (!game.heldIsEmpty()) {

            srcp = getSpritePosition(game.getHeldLetter());

            src = getRectFromSpritePosition(srcp);
            if(dragSelected) {
                dst = getMovingRectFromTouchPosition();
            }
            else{
                dst = getRectFromPosition(game.getHeldPoint());
            }

            if (imgflip) {
                dst = getFlippedRect(dst);
                canvas.save();
                canvas.rotate(180);
            }

            canvas.drawBitmap(pieces, src, dst, null);

            if (imgflip)
                canvas.restore();
        }

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(widthMeasureSpec < heightMeasureSpec)
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        else
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }


    public void setWindowWidth(int windowWidth){
        tilesize = windowWidth / TILES;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                touchx = (int) MotionEventCompat.getX(event, pointerIndex);
                touchy = (int) MotionEventCompat.getY(event, pointerIndex);

                if(!game.heldIsEmpty()) {
                    Point chosen = getPointFromTouchPosition(new Point(touchx, touchy));
                    if(game.requestMove(chosen)) {
                        // TODO: refactor with model?
                        ((MainActivity) getContext()).updateStatus();

                        break;
                    }
                }
                if (game.trySelectSquare(getPointFromTouchPosition(new Point(touchx,
                        touchy)))) {
                    dragSelected = true;
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);

                touchx = (int) MotionEventCompat.getX(event, pointerIndex);
                touchy = (int) MotionEventCompat.getY(event, pointerIndex);

                break;
            }

            case MotionEvent.ACTION_UP: {

                if (game.heldIsEmpty())
                    break;

                final int pointerIndex = MotionEventCompat.getActionIndex(event);

                touchx = (int) MotionEventCompat.getX(event, pointerIndex);
                touchy = (int) MotionEventCompat.getY(event, pointerIndex);
                Point released = getPointFromTouchPosition(new Point(touchx, touchy));
                if(game.getHeldPoint().equals(released)) {
                    dragSelected = false;
                }
                else {
                    game.requestMove(released);
                    // TODO: refactor with model?
                    ((MainActivity) getContext()).updateStatus();

                    dragSelected = false;
                }

                break;
            }

        }

        invalidate();
        return true;
    }

    private Point getSpritePosition(char pieceLetter) {
        switch (pieceLetter) {
            case 'E':
                return new Point(0, 0);
            case 'e':
                return new Point(0, 1);
            case 'M':
                return new Point(1, 0);
            case 'm':
                return new Point(1, 1);
            case 'H':
                return new Point(2, 0);
            case 'h':
                return new Point(2, 1);
            case 'D':
                return new Point(3, 0);
            case 'd':
                return new Point(3, 1);
            case 'C':
                return new Point(4, 0);
            case 'c':
                return new Point(4, 1);
            case 'R':
                return new Point(5, 0);
            case 'r':
                return new Point(5, 1);
            default:
                return null;
        }
    }

    public void setTwoView(boolean twoview) {
        this.twoview = twoview;
        invalidate();
    }
    public Rect getRectFromPosition(Point p) {
        return new Rect(p.x * tilesize, (7 - p.y) * tilesize, (p.x + 1)
                * tilesize, ((7 - p.y) + 1) * tilesize);
    }


    public Rect getFlippedRect(Rect r) {
        return new Rect(-r.left - tilesize, -r.top - tilesize, -r.right
                + tilesize, -r.bottom + tilesize);
    }

    public Rect getMovingRectFromTouchPosition() {
        int cornerx = touchx - tilesize / 2;
        int cornery = touchy - tilesize / 2;
        return new Rect(cornerx, cornery, cornerx + tilesize, cornery
                + tilesize);
    }

    public Point getPointFromTouchPosition(Point p) {

        Point npoint = new Point(p.x / tilesize, 7 - p.y / tilesize);
        if (npoint.x >= TILES)
            npoint.x = TILES - 1;

        if (npoint.x < 0)
            npoint.x = 0;

        if (npoint.y >= TILES)
            npoint.y = TILES - 1;

        if (npoint.y < 0)
            npoint.y = 0;

        return npoint;
    }


    public Rect getRectFromSpritePosition(Point p) {
        return new Rect(p.x * tilesize, p.y * tilesize, (p.x + 1) * tilesize,
                (p.y + 1) * tilesize);
    }


    boolean isTiled() {
        return tilesize > 0;
    }

    void resetGame() {
        game.resetGame();
        invalidate();
    }

    void advanceState() {
        if (game.advanceGameState(true))
            invalidate();
    }

    void revertMove() {
        game.requestRevertMove();
    }

    GameEngine.GameState getState() {
        return game.getGameState();
    }

    int getTurnSteps() {
        return game.getTurnSteps();
    }

    public void updateGraphicsSelection(SharedPreferences pref) {

        pieceset = Integer.parseInt(pref.getString(PREF_PIECESELECTION, "2"));

        Bitmap pre_pieces;
        pre_pieces = BitmapFactory.decodeResource(getResources(),
                R.drawable.defaultset);


        pieces = Bitmap.createScaledBitmap(pre_pieces, tilesize * IMGCOLS,
                tilesize * IMGROWS, true);

        pre_pieces.recycle();

        if(moveable_highlight == null) {
            Bitmap pre_moveable_highlight = BitmapFactory.decodeResource(getResources(), R.drawable.moveable);
            Bitmap pre_held_highlight = BitmapFactory.decodeResource(getResources(), R.drawable.held);

            moveable_highlight = Bitmap.createScaledBitmap(pre_moveable_highlight, tilesize, tilesize, true);
            held_highlight = Bitmap.createScaledBitmap(pre_held_highlight, tilesize, tilesize, true);

            pre_moveable_highlight.recycle();
            pre_held_highlight.recycle();
        }
    }

}