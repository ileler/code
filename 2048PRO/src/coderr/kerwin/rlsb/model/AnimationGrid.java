package coderr.kerwin.rlsb.model;

import java.util.ArrayList;


public class AnimationGrid {
	
    private int activeAnimations = 0;
    private boolean oneMoreFrame = false;
    private ArrayList<AnimationCell>[][] animationCells;
    private ArrayList<AnimationCell> globalAnimationCells = new ArrayList<AnimationCell>();
    
	@SuppressWarnings("unchecked")
	public AnimationGrid(int xl, int yl) {
    	animationCells = new ArrayList[xl][yl];
        for (int x = 0; x < xl; x++) {
            for (int y = 0; y < yl; y++) {
            	animationCells[x][y] = new ArrayList<AnimationCell>();
            }
        }
    }

    public void startAnimation(int x, int y, int animationType, long length, long delay, int[] extras) {
        AnimationCell animationToAdd = new AnimationCell(x, y, animationType, length, delay, extras);
        if (x == -1 && y == -1) {
        	globalAnimationCells.add(animationToAdd);
        } else {
        	animationCells[x][y].add(animationToAdd);
        }
        activeAnimations = activeAnimations + 1;
    }

    public void tickAll(long timeElapsed) {
        ArrayList<AnimationCell> cancelledAnimationCells = new ArrayList<AnimationCell>();
        for (AnimationCell animation : globalAnimationCells) {
            animation.tick(timeElapsed);
            if (animation.animationDone()) {
            	cancelledAnimationCells.add(animation);
                activeAnimations = activeAnimations - 1;
            }
        }

        for (ArrayList<AnimationCell>[] array : animationCells) {
            for (ArrayList<AnimationCell> list : array) {
                for (AnimationCell animation : list) {
                    animation.tick(timeElapsed);
                    if (animation.animationDone()) {
                    	cancelledAnimationCells.add(animation);
                        activeAnimations = activeAnimations - 1;
                    }
                }
            }
        }

        for (AnimationCell animation : cancelledAnimationCells) {
        	cancelAnimationCell(animation);
        }
    }

    public boolean isAnimationActive() {
        if (activeAnimations != 0) {
            oneMoreFrame = true;
            return true;
        } else if (oneMoreFrame) {
            oneMoreFrame = false;
            return true;
        } else {
            return false;
        }
    }
    
    public ArrayList<AnimationCell> getGlobalAnimationCells() {
    	return globalAnimationCells;
    }

    public ArrayList<AnimationCell> getAnimationCells(int x, int y) {
        return animationCells[x][y];
    }

    public void cancelAnimationCells() {
        for (ArrayList<AnimationCell>[] array : animationCells) {
            for (ArrayList<AnimationCell> list : array) {
                list.clear();
            }
        }
        globalAnimationCells.clear();
        activeAnimations = 0;
    }

    public void cancelAnimationCell(AnimationCell animation) {
        if (animation.getX() == -1 && animation.getY() == -1) {
        	globalAnimationCells.remove(animation);
        } else {
        	animationCells[animation.getX()][animation.getY()].remove(animation);
        }
    }

}
