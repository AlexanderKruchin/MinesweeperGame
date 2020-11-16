package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83E\uDD8B" ;
    private static final String FLAG = "\uD83E\uDD8E" ;
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE*SIDE;
    private int score;
    private boolean isGameNext;
    private int level=1;
    //------------------------------------------------------------


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();

       // showGrid(false); //показ сетки поля
    }

    private void createGame() {

        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x,y,"");
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                    countFlags=countMinesOnField;

                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.BISQUE);
            }
        }

          countMineNeighbors();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    
     private void countMineNeighbors(){
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gameObject = gameField[x][y];
                if (!gameObject.isMine){
                    for (GameObject neighbor : getNeighbors(gameObject)){
                        if (neighbor.isMine){
                            gameObject.countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        GameObject gameObject = gameField[y][x];

        if (!isGameStopped & !gameObject.isFlag & !gameObject.isOpen) {
            if (gameObject.isMine & countClosedTiles==SIDE*SIDE){
                rerollMine(gameObject);
            }
            else if (gameObject.isMine ) {
             setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);
             gameOver();
             gameObject.isOpen = true;

                } else if (gameObject.countMineNeighbors == 0) {
                 gameObject.isOpen = true;
                 score=score+5;
                 countClosedTiles--;
                 setCellColor(x, y, Color.LIGHTGREEN);
                 setCellValue(x, y, "");
            List<GameObject> neighbors = getNeighbors(gameObject);
                    for (GameObject neighbor : neighbors) {
                             if (!neighbor.isOpen) {
                             openTile(neighbor.x, neighbor.y);
                }

            }
        }

         if (!gameObject.isMine && gameObject.countMineNeighbors != 0) {
                gameObject.isOpen = true;
                score=score+5;
                countClosedTiles--;
                setCellColor(x, y, Color.LIGHTGREEN);
                setCellNumber(x, y, gameObject.countMineNeighbors);
                setCellColor(x, y, Color.CORAL);
                if (countClosedTiles==countMinesOnField){
                    win();
                }
            }

    }

        setScore(score);
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);

        if (!isGameStopped){
            openTile(x,y);
        }else {
            restart();
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x,y);
    }

    private void markTile(int x, int y){
        GameObject gameObject = gameField[y][x];
        if (!gameObject.isOpen & !gameObject.isFlag & countFlags!=0 & !isGameStopped){
            countFlags--;
            setCellValue(x,y,FLAG);
            gameObject.isFlag=true;
            setCellColor(x,y,Color.YELLOW);
        } else if (gameObject.isFlag & !isGameStopped){
            countFlags++;
            gameObject.isFlag=false;
            setCellValue(x,y,"");
            setCellColor(x,y,Color.BISQUE);
        }
    }

    private void gameOver(){
        isGameStopped=true;
        isGameNext=false;
        showMessageDialog(Color.GOLD,"Вы проиграли! \n Попробуйте снова! Нажмите ЛКМ",Color.BLACK,30);
    }

    private void win(){
        isGameStopped=true;
        isGameNext=true;
        showMessageDialog(Color.GOLD," Уровень " +level+ " пройден! \n Вы набрали "+score+" очков!\n Нажмите ЛКМ что бы продолжить.\n Нажмите ПКМ что-бы посмотреть поле.", Color.BLACK,20);
        level++;
    }

    private void restart(){
       // showMessageDialog(Color.CORAL,"Добро пожаловать в игру Сапёр!\n Что бы начать игру нажмите ЛКМ на свободной клетке" ,Color.BLACK,20);
        if (isGameNext){
            showMessageDialog(Color.GOLD,"  "+level+" Уровень уже тут! \n  Продолжай в том же духе! \n  Игра началась!",Color.BLACK,20);
            score=score+100;
            isGameStopped=false;
            countMinesOnField=0;
            countClosedTiles=SIDE*SIDE;
            setScore(score);
            createGame();
        }else{
            isGameStopped=false;
            countClosedTiles=SIDE*SIDE;
           score=0;
           level=1;
            countMinesOnField=0;
           setScore(score);
            createGame();
        }

    }

    private void rerollMine(GameObject gameObject){

        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x,y,"");
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                    countFlags=countMinesOnField;

                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.BISQUE);
            }
        }
        countMineNeighbors();
       openTile(gameObject.x,gameObject.y);
    }

}
