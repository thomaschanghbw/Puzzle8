/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    public PuzzleBoard previousBoard;
    public int steps;



    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        bitmap=Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,false);
        int widthOfTile=parentWidth/NUM_TILES;
        tiles = new ArrayList<>();
        for (int i = 0; i < NUM_TILES ; i++) {
            for (int j = 0; j < NUM_TILES ; j++) {
                tiles.add(new PuzzleTile(Bitmap.createBitmap(bitmap,j*widthOfTile,i*widthOfTile,widthOfTile,widthOfTile), (NUM_TILES*i)+j));
            }
        }
        tiles.remove(NUM_TILES*NUM_TILES-1);
        tiles.add(null);
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();

    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        for (int i=0;i<tiles.size(); i++) {
            PuzzleTile tile=tiles.get(i);
            if (tile==null){
                ArrayList<PuzzleBoard> neighbours=new ArrayList<>();
                for (int[] NEIGHBOUR_COORD : NEIGHBOUR_COORDS) {
                    if ( (i / NUM_TILES + NEIGHBOUR_COORD[1] < NUM_TILES) && (i / NUM_TILES + NEIGHBOUR_COORD[1] >= 0) && (i % NUM_TILES + NEIGHBOUR_COORD[0] < NUM_TILES) && (i % NUM_TILES + NEIGHBOUR_COORD[0] >= 0) )
                    {
                        PuzzleBoard neighbor = new PuzzleBoard(this);
                        neighbor.swapTiles(i, i + NEIGHBOUR_COORD[0] + NUM_TILES * NEIGHBOUR_COORD[1]);
                        neighbours.add(neighbor);
                    }
                }
                return neighbours;
            }
        }
        return null;
    }

    public int priority() {
        int man_distance = 0;
        for (int i = 0; i < tiles.size() ; i++) {
            if(tiles.get(i)!= null)
            {
                int col = Math.abs( (i % NUM_TILES) - (tiles.get(i).getNumber() % NUM_TILES) );
                int row = Math.abs( (i / NUM_TILES) - (tiles.get(i).getNumber() / NUM_TILES) );
                man_distance += col + row;
            }else {
                int columnChange = Math.abs(i % NUM_TILES - (NUM_TILES*NUM_TILES-1) % NUM_TILES);
                int rowChange = Math.abs(i / NUM_TILES - (NUM_TILES*NUM_TILES-1) / NUM_TILES);
                man_distance += columnChange+rowChange;
            }
        }
        return man_distance + steps;
    }

}
