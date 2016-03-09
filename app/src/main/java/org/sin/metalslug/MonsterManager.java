package org.sin.metalslug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sin on 2016/3/9.
 */
public class MonsterManager {
    public static final List<Monster> dieMonsterList = new ArrayList<>();

    public static final List<Monster> monsterList = new ArrayList<>();

    public static void generateMonster() {
        if (monsterList.size() <= 3 + Util.rand(3)) {
            Monster monster = new Monster(1 + Util.rand(3));
            monsterList.add(monster);
        }
    }

    public static void updateposistion(int shift){
        List<Monster> delList = new ArrayList<>();
        for (Monster monster: monsterList) {
            if (monster == null){
                continue;
            }

            monster.updateShift(shift);

            if (monster.getX() < 0){
                delList.add(monster);
            }
        }

        monsterList.removeAll(delList);
        delList.clear();

        for (Monster monster:dieMonsterList) {
            if (monster == null){
                continue;
            }

            monster.updateShift(shift);

            if (monster.getX() < 0){
                delList.add(monster);
            }

            dieMonsterList.removeAll(delList);
            GameView.player.updateBulletShift(shift);
        }
    }

    public static void checkMonster(){
        List<Bullet> bulletList = GameView.player.getBulletList();
        if (bulletList == null)
        {
            bulletList = new ArrayList<>();
        }

        List<Monster> delMonsterList = new ArrayList<>();

        List<Bullet> delBulletList = new ArrayList<>();

        for (Monster monster:monsterList) {
            if (monster == null){
                continue;
            }

            if (monster.getType() == Monster.TYPE_BOMB){
                if (GameView.player.isHurt(monster.getStartX(),
                        monster.getEndX(),monster.getStartY(),monster.getEndY())){
                    monster.setIsDie(true);

                    ViewManager.soundPool.play(
                            ViewManager.soundMap.get(2),1,1,0,0,1);
                    delMonsterList.add(monster);

                    GameView.player.setHp(GameView.player.getHp( - 10));
                }
                continue;
            }

            for (Bullet bullet : bulletList){
                if (bullet == null || !bullet.isEffect()){
                    continue;
                }

                if (monster.isHurt(bullet.getX(),bullet.getY())){
                    bullet.setEffect(false);
                    monster.setIsDie(true);

                    if (monster.getType() == Monster.TYPE_FLY){
                        ViewManager.soundPool.play(
                                ViewManager.soundMap.get(2),1,1,0,0,1);

                    }

                    if (monster.getType() == Monster.TYPE_MAN){
                        ViewManager.soundPool.play(
                                ViewManager.soundMap.get(3),1,1,0,0,1);
                    }

                    delBulletList.add(bullet);

                }
            }

            bulletList.removeAll(delBulletList);
            monster.checkBullet();
        }
        dieMonsterList.addAll(delMonsterList);
        monsterList.removeAll(delMonsterList);
    }

}
