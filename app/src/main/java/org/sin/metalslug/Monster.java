package org.sin.metalslug;


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
}
