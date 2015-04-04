import java.io.*;
import java.util.*;

/** Clase principal. */
class Main{
    public static void main(String[] args){
	String in = Reader.read(1024);
	Scanner scan;
	List<Integer> counts = new ArrayList<Integer>();
	Grid grid;
	int squareSize, pX, pY;
	while(!in.equals("0 0 0")){
	    scan = new Scanner(in);
	    squareSize = scan.nextInt();
	    pX = scan.nextInt();
	    pY = scan.nextInt();			    
	    try{
		grid = new Grid(squareSize,false);
		counts.add(grid.count(new Point(pX,pY)));
	    } catch (SquareSizeException e){
	    }
	    in = Reader.read(1024);
	}
	for(int i : counts){
	    System.out.printf("%3d%n",i);
	}
    }
}

/** Enumeración de regiones:
    IV: banda vertical central dentro del cuadrado.
    OV: banda vertical central fuera del cuadrado.
    IH: banda horizontal central dentro del cuadrado.
    OH: banda horizontal central fuera del cuadrado.
    C1: cuadrante 1 dentro del cuadrado.
    E1: cuadrante 1 fuera del cuadrado.
    C2: cuadrante 2 dentro del cuadrado.
    E2: cuadrante 2 fuera del cuadrado.
    C3: cuadrante 3 dentro del cuadrado.
    E3: cuadrante 3 fuera del cuadrado.
    C4: cuadrante 4 dentro del cuadrado.
    E4: cuadrante 4 fuera del cuadrado.*/
    enum Region{
	NO,IV,IH,OV,OH,C1,E1,C2,E2,C3,E3,C4,E4;

	    public boolean in(){
	    switch(this){
	    case IV:
	    case IH:
	    case C1:
	    case C2:
	    case C3:
	    case C4:
		return true;
	    default: return false;
	    }
	}
    }
/** Clase que modeliza un punto en el plano*/
class Point{

    private int x;
    private int y;

    /** Crea un punto en el origen de coordenadas*/
    public Point(){
	x = 0;
	y = 0;
    }

    /** Crea un punto con las coordenadas especificadas */
    public Point(int x, int y){
	this.x = x;
	this.y = y;
    }
    
    /** Cambia la abscisa */
    public void setX(int x){
	this.x = x;
    }

    /** Cambia la ordenada*/
    public void setY(int y){
	this.y = y;
    }

    /** Devuelve la abscisa */
    public int getX(){
	return x;
    }

    /** Devuelve la ordenada */
    public int getY(){
	return y;
    }

    /** Devuelve un String que representa al punto */
    public String toString(){
	return "(" + x + "," + y + ")";
    }
}

/** Clase que modeliza un cuadrado */
class Square{
    private final Point centre;
    private final int size;

    /** Crea un cuadrado centrado en el punto y del tamaño indicado */
    public Square(Point p, int siz){
	centre = p;
	size = siz;
    }

    /** Devuelve el punto central del cuadrado */
    public Point getCentre(){
	return centre;
    }

    /** Devuelve el tamaño del cuadrado */
    public int getSize(){
	return size;
    }

    /** Devuelve la esquina superior izquierda del cuadrado */
    public Point getTopLeftCorner(){
	return new Point(centre.getX() - size,centre.getY() - size);
    }

    /** Devuelve la esquina superior derecha del cuadrado */
    public Point getTopRightCorner(){
	return new Point(centre.getX() + size,centre.getY() - size);
    }

    /** Devuelve la esquina inferior izquierda del cuadrado */
    public Point getBottomLeftCorner(){
	return new Point(centre.getX() - size,centre.getY() + size);
    }

    /** Devuelve la esquina inferior derecha del cuadrado */
    public Point getBottomRightCorner(){
	return new Point(centre.getX() + size,centre.getY() + size);
    }

    /** Devuelve la region del cuadrado en la que se encuentra el punto especificado */
    public Region where(Point p){

	int diffX = centre.getX() - p.getX();
	int absX = Math.abs(diffX);
	int diffY = centre.getY() - p.getY();
	int absY = Math.abs(diffY);

	if((absX > 2*size) || (absY > 2*size)){
		return Region.NO;
	    }
	if((size % 2) == 0){
	    if(diffX == 0){
		if(absY <= size){
		    return Region.IV;
		} else {
		    return Region.OV;
		}
	    }
	    if (diffY == 0){
		if(absX <= size){
		    return Region.IH;
		} else {
		    return Region.OH;
		}
	    }
	} else {
	    if(absX <= 1){
		if(absY <= size){
		    return Region.IV;
		} else {
		    return Region.OV;
		}
	    }
	    if (absY <= 1){
		if(absX <= size){
		    return Region.IH;
		} else {
		    return Region.OH;
		}
	    }
	}
	if(diffX > 0){
	    if(diffY > 0){
		if((diffX > size) || (diffY > size)){
		    return Region.E1;
		} else {
		    return Region.C1;
		}
	    } else {
		if ((diffX > size) || (diffY < -size)){
		    return Region.E3;
		} else {
		    return Region.C3;
		}
	    }
	} else {
	    if(diffY > 0){
		if ((diffX < -size) || (diffY > size)){
		    return Region.E2;
		} else {
		    return Region.C2;
		}
	    } else {
		if((diffX < -size) || (diffY < -size)){
		    return Region.E4;
		} else {
		    return Region.C4;
		}
	    }
	}	    
    }
}

/** Clase que modeliza el patron de cuadrados */
class Pattern{
    private Point centre;
    private Square principal;
    private Pattern p1,p2,p3,p4;

    /** Crea un nuevo patrón centrado en p, con el cuadrado principal del tamaño indicado. Da la
	posibilidad de crear todo el patron o solo el cuadrado principal */
    public Pattern(Point p, int size, boolean generateAll){
	centre = p;
	principal = new Square(p,size);
	if(generateAll){
	    p1 = new Pattern(principal.getTopLeftCorner(), (int) principal.getSize() / 2, true);	
	    p2 = new Pattern(principal.getTopRightCorner(), (int) principal.getSize() / 2, true);	
	    p3 = new Pattern(principal.getBottomLeftCorner(), (int) principal.getSize() / 2, true);
	    p4 = new Pattern(principal.getBottomRightCorner(), (int) principal.getSize() / 2, true);
	}
	
    }

    /** Inizializa el subpatron de la región especificada */
    public void initializeSubRegion(Region r){
	if(principal.getSize() > 1){
	    switch(r){
	    case C1:
	    case E1:
		p1 = new Pattern(principal.getTopLeftCorner(), (int) principal.getSize() / 2, false);
		break;
	    case C2:
	    case E2:
		p2 = new Pattern(principal.getTopRightCorner(), (int) principal.getSize() / 2, false);
		break;
	    case C3:
	    case E3:
		p3 = new Pattern(principal.getBottomLeftCorner(), (int) principal.getSize() / 2, false);
		break;
	    case C4:
	    case E4:
		p4 = new Pattern(principal.getBottomRightCorner(), (int) principal.getSize() / 2, false);
		break;
	    }
	}
    }
    /** Devuelve el cuadrado principal */
    public Square getPrincipal(){
	return principal;
    }

    /** Devuelve el subpatron de la región especificada */
    public Pattern getRegion(Region r){
	switch(r){
	case C1:
	case E1: return p1;
	case C2:
	case E2: return p2;
	case C3:
	case E3: return p3;
	case C4:
	case E4: return p4;
	default: return null;
	}
    }

    /** Devuelve el número de cuadrados que contienen al punto p. Utiliza un parámetro acumulador. */
    public int count(Point p, int acumulator){
	Region reg = principal.where(p);
	if (reg.in()){
	    acumulator++;
	}
	Pattern sub = getRegion(reg);
	if(sub == null){
	    initializeSubRegion(reg);
	    sub = getRegion(reg);
	}
	if (sub != null){
	    return sub.count(p,acumulator);
	} else {
	    return acumulator;
	}
    }
}

/** Clase que modeliza la rejilla principal en la que va encuadrada el patrón. */
class Grid{
    private static final int DEFAULT_SIZE = 1024;
    private final int MAX_SQUARE_SIZE;
    private final Point centre;
    private Pattern allSquares;

    /** Crea una nueva rejilla, especificando el tamaño del cuadrado central y la opción de crear el patrón entero. */
    public Grid(int squareSize, boolean generateAll) throws SquareSizeException{
	this(DEFAULT_SIZE, squareSize, generateAll);
    }

    /** Crea una nueva rejilla del tamaño indicado, especificando el tamaño del cuadrado central y la opción de crear el patrón entero. */
    public Grid(int gridSize, int squareSize, boolean generateAll) throws SquareSizeException{
	centre = new Point(gridSize,gridSize);
	MAX_SQUARE_SIZE = (int) gridSize / 2;
	if((squareSize <= MAX_SQUARE_SIZE) && (squareSize >= 1)){
	    allSquares = new Pattern(centre,squareSize, generateAll);
	} else {
	    throw new SquareSizeException();
	}
    }
    
    /** Devuelve el número de cuadrados que contienen al punto p */
    public int count(Point p){
	return allSquares.count(p,0);
    }

}

/** Excepción lanzada cuando el tamaño del cuadrado pasado a Grid no está entre los valores aceptados */
class SquareSizeException extends Exception{}

/** Clase que lee de System.in */
class Reader{
    /** Método estático que devuelve un String leido byte a byte de la entrada estándar. */
    public static String read(int number){
	byte[] bytes = new byte[number];
	int input, i;

	try{
	    for(i = 0; i < number; i++){
		input = System.in.read();
		if((input >= 0) && (input!= '\n')){
		    bytes[i] = (byte) input;
		} else {
		    break;
		}
	    }
	    return new String(bytes,0,i);
	} catch (IOException e){
	    System.out.println(e);
	}
	return null;
    }

}