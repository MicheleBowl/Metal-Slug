package org.sin.metalslug;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class Monster {
    public static final int TYPE_BOMB = 1;
    public static final int TYPE_FLY = 2;
    public static final int TYPE_MAN = 3;

    private int type = TYPE_BOMB;

    private int x = 0;
    private int y = 0;

    private boolean isDie = false;

    private int startX = 0;
    private int startY = 0;

    private int endX = 0;
    private int endY = 0;

    int drawCount = 0;

    private int drawIndex = 0;

    private int dieMaxDrawCount = Integer.MAX_VALUE;

    private List<Bullet> bulletList = new ArrayList<>();

    public Monster(int type){
        this.type = type;

        if (type == TYPE_BOMB || type == TYPE_MAN){
            y = Player.Y_DEFALUT;
        }else if(type == TYPE_FLY){
            y = ViewManager.SCREEN_HEIGHT * 50 / 100
                    - Util.rand((int) (ViewManager.scale * 100));
        }

        x = ViewManager.SCREEN_WIDTH + Util.rand(ViewManager.SCREEN_WIDTH >> 1)
                - (ViewManager.SCREEN_WIDTH >> 2);
    }

    public void draw(Canvas canvas){
        if (Canvas == null){
            return;;
        }
        switch (type){
            case TYPE_BOMB:
                drawAni(canvas,isDie?ViewManager.bomb2Image :ViewManager.bombImage);
                break;
            case TYPE_FLY:
                drawAni(canvas,isDie?ViewManager.flyDieImage : ViewManager.flyImage);
                break;
            case TYPE_MAN:
                drawAni(canvas,isDie?ViewManager.manDieImage : ViewManager.manImage);
                break;
            default:
                break;
        }

    }

    public void drawAni(Canvas canvas,Bitmap[] bitmapArr){
        if (canvas == null){
            return;
        }

        if (bitmapArr == null){
            return;
        }

        if (isDie && dieMaxDrawCount == Integer.MAX_VALUE){
            dieMaxDrawCount = bitmapArr.length;
        }

        dieMaxDrawCount = drawCount % bitmapArr.length;
        Bitmap bitmap = bitmapArr[drawIndex];

        if (bitmap==null ||bitmap.isRecycled()){
            return;
        }
        int drawX = x;

        if (isDie){
            if (type ==TYPE_BOMB){
                drawX = x - (int) (ViewManager.scale * 50);
            }
            else if (type == TYPE_MAN){
                drawX = x + (int) (ViewManager.scale * 50);
            }
        }

        int drawY = y - bitmap.getHeight();

        Graphics.drawMatrixImage(canvas, bitmap,0,0,bitmap.getWidth(),bitmap.getHeight()
            ,Graphics.TRANS_NOE,drawX, drawY,0,Graphics,TIMES_SCALE);

        startX = drawX;
        startY = drawY;
        endX = startX + bitmap.getWidth();
        endY = startY + bitmap.getHeight();
        drawCount++;

        if (drawCount >= (type == TYPE_MAN ? 6 : 4)) {
            if (type == TYPE_MAN && drawIndex == 2) {
                addBullet();
            }

            if (type == TYPE_FLY && drawIndex == bitmapArr.length - 1) {
                addBullet();
            }

            drawIndex++;
            drawCount = 0;
        }

        if (isDie){
            dieMaxDrawCount--;
        }
        drawBullet(canvas);
    }
}
