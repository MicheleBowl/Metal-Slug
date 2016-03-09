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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public boolean isDie() {
        return isDie;
    }

    public void setIsDie(boolean isDie) {
        this.isDie = isDie;
    }

    public void draw(Canvas canvas){
        if (canvas == null){
            return;
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

    public boolean isHurt(int x ,int y){
        return x >= startX && x<= endX
                && y >= startY && y <=endY;
    }

    public int getBulletType(){
        switch (type){
            case TYPE_BOMB:
                return  0;
            case TYPE_MAN:
                return Bullet.BULLET_TYPE_3;
            case TYPE_FLY:
                return Bullet.BULLET_TYPE_2;
            default:
                return 0;
        }
    }

    public void addBullet(){
        int bulletType = getBulletType();

        if (bulletType == 0){
            return;
        }

        int drawX = x;
        int drawY = y - (int) (ViewManager.scale * 60);

        if (type == TYPE_FLY){
            drawY = y - (int) (ViewManager.scale * 30);
        }

        Bullet bullet = new Bullet(bulletType,drawX,drawY,Player.DIR_LEFT);

        bulletList.add(bullet);

    }

    public void updateShift(int shift){
        x -= shift;
        for (Bullet bullet : bulletList){
            if (bullet == null){
                continue;
            }
            bullet.setX(bullet.getX() - shift);
        }
    }

    public void drawBullet(Canvas canvas){
        List<Bullet> deleteList = new ArrayList<>();
        Bullet bullet = null;
        for (int i = 0;i < bulletList.size();i++){
            bullet = bulletList.get(i);
            if (bullet == null){
                continue;
            }

            if (bullet.getX() <0 || bullet.getX() >ViewManager.SCREEN_WIDTH){
                deleteList.add(bullet);
            }
        }

        bulletList.removeAll(deleteList);

        Bitmap bitmap;

        for (int i = 0;i < bulletList.size();i++){
            bullet = bulletList.get(i);
            if (bullet == null){
                continue;
            }

            bitmap = bullet.getBitmap();
            if (bitmap == null){
                continue;
            }

            bullet.move();

            Graphics.drawMatrixImage(canvas,bitmap,0,0,bitmap.getWidth(),
                    bitmap.getHeight(),bullet.getDir() == Player.DIR_RIGHT ?
                    Graphics.TRANS_MIRROR : Graphics.TRANS_NONE,
                    bullet.getX(),bullet.getY(),0,Graphics.TIMES_SCALE);

        }
    }

    public void checkBullet(){
        List<Bullet> delBulletList = new ArrayList<>();
        for (Bullet bullet: bulletList) {
            if (bullet == null || !bullet.isEffect()){
                continue;
            }

            if (GameView.player.isHurt(bullet.getX(),bullet.getX(),bullet.getY()
            ,bullet.getY())){
                bullet.setEffect(false);
                GameView.player.setHp(GameView.player.getHp() - 5);
                delBulletList.add(bullet);
            }
        }

        bulletList.removeAll(delBulletList);
    }
}
