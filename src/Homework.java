import stanford.karel.SuperKarel;
public class Homework extends SuperKarel {
    private int width; // world width
    private int height; // world height
    private int steps; // number of steps karel had taken

    public void run() {
        initialize();
        calculateWidth();
        drawAndCalculateHeight();
        validateDimensions();
        drawInnerChambers();
        if ((isOdd(width) && isOdd(height)) || (isOdd(width) != isOdd(height))) // if the width and height are both odd or if one them is odd or even draw the width (after the inner)
            drawWidth();
    }

    // initializes important features of the world
    private void initialize() {
        setBeepersInBag(1000);
        this.width = 0;
        this.height = 0;
        steps = 0;
    }

    // handles special cases where there is small worlds
    private void validateDimensions() {
        if (width <= 3 || height <= 3)
            throw new IllegalArgumentException("Try a dimension that is larger than 4!!");
    }

    // calculate the width of the world
    private void calculateWidth() {
        while (frontIsClear()) {
            steps(); // move karel as long as there is no walls
            this.width++;
        }
        if (frontIsBlocked())
            turnAround();
        checkWidthStartingPoint(); // checks the width starting point (where to begin drawing) after calculating the width size
    }

    private void checkWidthStartingPoint() {
        int target = (isOdd(width)) ? width / 2 : (width / 2) + 1;  // checks if the width is odd or even
        int temp = width;
        while (temp-- != target) {   // moves until it reaches the target (odd or even) which is where to start drawing
            steps();
        }
        turnRight();
    }

    // draws the vertical line and calculates the height of the world
    private void drawAndCalculateHeight() {
        putBeepers();
        while (frontIsClear()) {
            drawHeight();        // draws the first vertical line in odd (which is the only line) and in even
            this.height++;
        }
        if (!isOdd(width) && !isOdd(height)) // checks if both are even to draw the second vertical line which is divided into parts
            drawHeightOfSecondLine();
        else if (!isOdd(width) && isOdd(height)) { // checks if the width was even and the height is odd, just simply draw the second vertical line (all of it)
            turnLeft();
            while (frontIsClear()) {
                drawHeight();
                if (facingWest())
                    turnLeft();
            }
        }
        if (frontIsBlocked())
            turnAround();
    }

    // draws the vertical lines
    private void drawHeight() {
        moveAndPut();
    }

    // draws the vertical and the horizontal lines in case both are even (diff implementation for the sake of minimizing the steps)
    private void drawHeightOfSecondLine() {
        turnLeft();
        drawHalfOfSecond();    // draws the first half of the vertical line
        turnRight();
        drawWidth();           //draw the horizontal line before the other vertical line half
        drawHalfOfSecond();    // draws the second half of the vertical line
    }

    // draws half the second vertical line only
    private void drawHalfOfSecond() {
        int temp = (height / 2) + 1; // because it's even we add 1
        while (temp-- != 0) {
            drawHeight(); // draw half
            if (facingWest())
                turnLeft();
        }
    }

   // puts down beepers if no beepers are present at the current point
    private void putBeepers() {
        if (noBeepersPresent())
            putBeeper();
    }

   // draws the inner chambers (the biggest equal squares)
    private void drawInnerChambers() {
        int minimum = findMinimumDimension();
        int start = calculateStart(minimum);
        moveToSquareStartPoint(start);
        drawSquares(start, minimum);
    }

    private int findMinimumDimension() {
        return Math.min(width, height);
    }

    // calculates where to begin drawing the squares (inner chambers)
    private int calculateStart(int minimum) {
        return (width < height) ? ((height / 2) - calculateSquareLength(minimum) / 2) : 1; // the return 1 means if height is bigger than or equal the width the square will always be 1 dimension less than the border
    }

   // moves to the square start point after calculating it
    private void moveToSquareStartPoint(int start) {
        while (start-- > 0) {
            steps();
        }
        turnLeft();
    }

    // draws the actual squares
    private void drawSquares(int start, int minimum) {
        int length = calculateSquareLength(minimum); // calculate square width length
        drawHalfSide(length); // draw the first half of the square
        drawRemainingSides(length, start);
        drawHalfSide(length - 2); // draw the second half of the square
    }

    // finds the square length which is the (minimum dimension - 2) because the square is always at least 1 dimension less from the borders, and we have two borders (so it's 2)
    private int calculateSquareLength(int minimum) {
        return minimum - 2;
    }

    // draws half of the squares side
    private void drawHalfSide(int length) {
        for (int i = 0; i < length / 2; i++) {
            moveAndPut();
        }
        turnRight();
    }

    // draws the remaining sides of the square
    private void drawRemainingSides(int length, int start) {
        if (isOdd(width) == isOdd(height)){ // if both are even or odd draw the remaining sides of the square
            for (int i = 1; i <= length * 3; i++) {     // the length is multiplied by 3 because of the 3 remaining sides of the square
                steps();
                if (i % length == 0)
                    turnRight();
                putBeepers();
            }
        } else { // if one of the dimensions is odd or even
            drawVerticalEdge(start); // draws the right vertical edge
            drawHorizontalEdge();
            drawVerticalEdge(start); // draws the left vertical edge
        }
    }

    // draw right and left vertical edges of the square (if one is even or odd)
    private void drawVerticalEdge(int start) {
        int counter = height - start;
        while (counter-- != start) {
            moveAndPut();
        }
        turnRight();
    }

    // draws the bottom or top horizontal edges of the square (if one is even or odd)
    private void drawHorizontalEdge() {
        int minimum = (isOdd(height) && height < width) ? findMinimumDimension() - 1 : findMinimumDimension() - 2;
        if(isWidthOddAndBigger()) // handles special cases
            minimum--;
        while (minimum-- > 0) {
            moveAndPut();
        }
        turnRight();
    }

    // handles special cases where the width is odd and bigger than the height
    private boolean isWidthOddAndBigger(){
        return isOdd(width) && width > height;
    }

    //draws the horizontal lines
    private void drawWidth() {
        if (facingEast())
            turnRight();
        if (isOdd(height))
            drawOddWidth(); // draws only one line if the height is odd
        else
            drawEvenWidth(); // draws two lines if the height is even
    }

    private void drawOddWidth() {
        int counter = height - calculateStart(findMinimumDimension() - 2);
        if(width < height)  // handles special cases
            counter++;
        skipPoints(counter);
        turnRight();
        fillWidth(); // fills or draws the actual horizontal line
        turnAround();
        fillWidth();
    }

    // skips some points (for steps minimization)
    private void skipPoints(int counter) {
        while (counter-- != (height / 2)) {
            steps();
        }
    }

    // draws the width in case it was even
    private void drawEvenWidth() {
        if (!isOdd(width)) { // if both were even
            int startingPoint = ((width / 2) * 2) + (width + 2);
            drawLines(startingPoint);
        } else { // if the height was even and the width is odd
            int counter = height - calculateStart(findMinimumDimension() - 2);
            if(isWidthOddAndBigger()) // handles special cases
                counter--;
            skipPoints(counter);
            putBeepers();
            turnRight();
            int startingPoint = 2 * width - 1;
            drawLines(startingPoint);
        }
        if (facingWest())
            turnLeft();
    }

    // draws the horizontal lines based on a starting point
    private void drawLines(int startingPoint) {
        while (startingPoint-- >= 0) {
            if (frontIsBlocked())
                turnLeft();
            moveAndPut();
            if (facingNorth() || facingSouth())
                turnLeft();
        }
    }

    private void fillWidth() {
        while (frontIsClear()) {
            moveAndPut();
        }
    }

    // moves and puts beepers
    private void moveAndPut() {
        steps();
        putBeepers();
    }

     // moves and count the steps karel has taken
    private void steps() {
        move();
        System.out.println("number of steps: " + ++this.steps);
    }

    // checks if the given parameter (width or height) is odd, note here that the odd is (n % 2 == 0) because I started the count from 0
    private boolean isOdd(int dimension) {
        return dimension % 2 == 0;
    }
}