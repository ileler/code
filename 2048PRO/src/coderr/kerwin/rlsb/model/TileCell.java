package coderr.kerwin.rlsb.model;

public class TileCell extends BaseCell {
    private int value;
    private TileCell[] mergedFrom = null;

    public TileCell(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

    public TileCell(BaseCell cell, int value) {
        super(cell.getX(), cell.getY());
        this.value = value;
    }

    public void updatePosition(BaseCell cell) {
        this.setX(cell.getX());
        this.setY(cell.getY());
    }

    public int getValue() {
        return this.value;
    }

    public TileCell[] getMergedFrom() {
       return mergedFrom;
    }

    public void setMergedFrom(TileCell[] tile) {
        mergedFrom = tile;
    }
}
