/*
 * File: BlankKarel.java
 * ---------------------
 * This class is a blank one that you can change at will.
 */

import stanford.karel.*;

public class BlankKarel extends SuperKarel {
    private int width, height;
    private int steps;
    private int executedTimes, beepersUsed;
    public void run() {
        System.out.println("execution ----> " + ++executedTimes);
        setBeepersInBag(1000);
        calculateWidth(); //this will calculate the width size
        calculateHeight(); //this will calculate the height size and draw it
        drawInnerChambers();
        drawWidth();
        System.out.println("number of beepers used: " + beepersUsed);
        this.width = 0;
        this.height = 0;
        steps = 0;
        beepersUsed = 0;
    }
    private boolean isOdd(){
        return height % 2 == 0 && width % 2 == 0;
    }
    private void calculateWidth() {
        while (frontIsClear()) {
            steps();
            this.width++;
        }
        if (frontIsBlocked())
            turnAround();
        checkWidthStartingPoint();  //after calculating width check the width starting point (where to begin drawing)
    }
    private void checkWidthStartingPoint() {
        int temp = width;
        int target = (width % 2 == 0) ? width / 2 : (width / 2) + 1;      //check if the width is odd or even
        while (temp != target) {
            steps();
            temp--;
        }
        turnRight();
    }
    private void calculateHeight() {
        while (frontIsClear()) {
            drawHeight();        //draw the first line
            this.height++;       //find height size
        }
        putBeepers();           //this will put a beeper on the last point in the height
        if (width % 2 != 0) {   //check if it's even to turn left and draw the second line of the height
            turnLeft();
            drawHalfOfSecond();
        }
        if (frontIsBlocked()) {
            turnAround();
        }
    }
    private void drawHeight() {
        putBeepers();
        steps();
    }
    private void drawHalfOfSecond() {
        while (frontIsClear()) {
            drawHeight();           //draw the second line
            if (facingWest()) {
                turnLeft();
            }
        }
        putBeepers();
    }
    private void putBeepers() {
        if (beepersInBag() && noBeepersPresent()) {
            putBeeper();
            this.beepersUsed++;
        }
    }
    private void drawInnerChambers() {
        int minimum = findMinimumDimension();
        int start = calculateSquareStart(minimum);
        drawSquare(start, minimum);
    }
    private int findMinimumDimension() {
        return Math.min(width, height);
    }
    private int calculateSquareStart(int minimum) {
        if (width < height)
            return (height / 2) - calculateSquareLength(minimum) / 2;
        return 1;
    }
    private void drawSquare(int start, int minimum) {
        int length = 0;
        moveToSquareStartPoint(start);
        length = calculateSquareLength(minimum);
        drawHalfSide(length);
        turnRight();
        drawRemainingSides(length);
        drawHalfSide(length - 2);
    }
    private void moveToSquareStartPoint(int start) {
        while (start-- > 0) {
            steps();
        }
        turnLeft();
    }
    private void drawHalfSide(int length) {
        for (int i = 0; i < length / 2; i++) {
            steps();
            putBeepers();
        }
    }
    private void drawRemainingSides(int length) {
        for (int i = 1; i <= length * 3; i++) {
            steps();
            if (i % length == 0) {
                turnRight();
            }
            putBeepers();
        }
    }
    private int calculateSquareLength(int minimum) { return minimum - 2; }
    private void drawWidth() {
        int temp = checkHeightStartingPoint();
        if (facingEast())
            turnRight();
        if(isOdd()) {
            while (temp != (height / 2)) {
                steps();
                temp--;
            }
            turnRight();
            moveWidth();
            turnAround();
            moveWidth();
        }
        else{
            temp = height - checkHeightStartingPoint();
            turnRight();
            while (temp != (height / 2)) {
                steps();
                temp++;
            }
            putBeepers();
            turnRight();
            temp = ((width / 2) * 2) + width;
            while(temp-- > 0){
                if(frontIsBlocked())
                    turnLeft();
                steps();
                putBeepers();
                if(facingNorth())
                    turnLeft();
                if(facingSouth())
                    turnLeft();
            }
        }
    }
    private int checkHeightStartingPoint() {
        if (width < height) {
            int minimum = findMinimumDimension() - 2;
            return height - ((height / 2) - (minimum / 2));
        } else {
            return height - 1;
        }
    }
    private void moveWidth() {
        while (frontIsClear()) {
            steps();
            putBeepers();
        }
    }
    private void steps() {
        move();
        System.out.println("number of steps: " + ++steps);
    }
}