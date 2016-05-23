package coderr.kerwin.rlsb.model;

import java.util.ArrayList;

public class Grid {

    public TileCell[][] field;
    public TileCell[][] undoField;
    private TileCell[][] bufferField;

    public Grid(int sizeX, int sizeY) {
        field = new TileCell[sizeX][sizeY];
        undoField = new TileCell[sizeX][sizeY];
        bufferField = new TileCell[sizeX][sizeY];
        clearGrid();
        clearUndoGrid();
    }
    
    public ArrayList<BaseCell> getAvailableCells() {
    	ArrayList<BaseCell> availableCells = new ArrayList<BaseCell>();
    	for (int x = 0; x < field.length; x++) {
    		for (int y = 0; y < field[0].length; y++) {
    			if (field[x][y] == null) {
    				availableCells.add(new BaseCell(x, y));
    			}
    		}
    	}
    	return availableCells;
    }

    public BaseCell randomAvailableCell() {
       ArrayList<BaseCell> availableCells = getAvailableCells();
       if (availableCells.size() >= 1) {
           return availableCells.get((int) Math.floor(Math.random() * availableCells.size()));
       }
       return null;
    }
    
    public boolean isCellWithinBounds(BaseCell cell) {
    	return 0 <= cell.getX() && cell.getX() < field.length
    			&& 0 <= cell.getY() && cell.getY() < field[0].length;
    }
    
    public boolean isCellWithinBounds(int x, int y) {
    	return 0 <= x && x < field.length
    			&& 0 <= y && y < field[0].length;
    }
    
    public TileCell getCellContent(BaseCell cell) {
    	if (cell != null && isCellWithinBounds(cell)) {
    		return field[cell.getX()][cell.getY()];
    	} else {
    		return null;
    	}
    }
    
    public TileCell getCellContent(int x, int y) {
    	if (isCellWithinBounds(x, y)) {
    		return field[x][y];
    	} else {
    		return null;
    	}
    }
    
    public boolean isCellOccupied(BaseCell cell) {
    	return (getCellContent(cell) != null);
    }

    public boolean isCellAvailable(BaseCell cell) {
        return !isCellOccupied(cell);
    }
    
    public boolean isCellsAvailable() {
    	return (getAvailableCells().size() >= 1);
    }

    public void insertTile(TileCell tile) {
        field[tile.getX()][tile.getY()] = tile;
    }

    public void removeTile(TileCell tile) {
        field[tile.getX()][tile.getY()] = null;
    }

    public void saveTiles() {
        for (int x = 0; x < bufferField.length; x++) {
            for (int y = 0; y < bufferField[0].length; y++) {
                if (bufferField[x][y] == null) {
                    undoField[x][y] = null;
                } else {
                    undoField[x][y] = new TileCell(x, y, bufferField[x][y].getValue());
                }
            }
        }
    }

    public void prepareSaveTiles() {
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {
                if (field[x][y] == null) {
                    bufferField[x][y] = null;
                } else {
                    bufferField[x][y] = new TileCell(x, y, field[x][y].getValue());
                }
            }
        }
    }

    public void revertTiles() {
        for (int x = 0; x < undoField.length; x++) {
            for (int y = 0; y < undoField[0].length; y++) {
                if (undoField[x][y] == null) {
                    field[x][y] = null;
                } else {
                    field[x][y] = new TileCell(x, y, undoField[x][y].getValue());
                }
            }
        }
    }

    public void clearGrid() {
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {
                field[x][y] = null;
            }
        }
    }

    public void clearUndoGrid() {
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {
                undoField[x][y] = null;
            }
        }
    }
}
