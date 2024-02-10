/*
 * Este programa es una aplicación gráfica simple en Java que permite al usuario dibujar líneas.
 * Se utiliza el patrón de diseño de escuchador de eventos del mouse para detectar clics y movimientos del ratón.
 * Se utiliza un algoritmo de line clipping (Cohen Sutherland) para determinar que linea o no pertenece a un area en especifico.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import javax.swing.JPanel;
import javax.swing.JFrame;

/**
 * La clase EventMouse extiende JPanel e implementa MouseListener para manejar eventos del mouse.
 * Representa un componente gráfico que permite al usuario dibujar líneas, y determinar si se encuentra dentro de un area de recorte.
 */
public class EventMouse extends JPanel implements MouseListener {
    Line2D.Double linea1; // Variable que almacena la línea que se crea, es decir, los puntos iniciales y finales del evento realizado con el mouse.
    int clippingArea = 0b0000; // Cuadrante central 0000, que denota el area de recorte.
    int left = 0b0001; // Bit que tiene activo los cuadrantes de la izquierda del area de recorte (0001).
    int right = 0b0010; // Bit que tiene activo los cuadrantes de la derecha del area de recorte (0010).
    int bottom = 0b0100; // Bit que tiene activo los cuadrantes de abajo del area del area de recorte (0100).
    int top = 0b1000; // Bit que tiene activo los cuadrantes de arriba del area de recorte (1000).


    /**
     * Constructor de la clase. Inicializa la variable de la línea y configura el objeto como escuchador de eventos del mouse.
     */
    public EventMouse() {
        linea1 = new Line2D.Double(); // Inicialización de la variable de la línea.
        this.addMouseListener(this); // Consideración del objeto como un escuchador de los eventos del ratón.
    }

    /**
     * Método paintComponent que se llama automáticamente para dibujar en el componente.
     * Dibuja el area de recorte con los puntos: esquina inferior izquierda en (-200,-100) y esquina superior derecha en (200,100).
     * Llama al método de LineClipping para determinar si la linea generada por el evento del mouse pertenece, pertenece parcialmente o no pertenece al area de recorte.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Llama a las funciones relacionadas con el dibujo antes de realizar la acción y permite obtener el ancho y la altura del frame creado para determinar el area de recorte.

        Graphics2D g2d = (Graphics2D) g; // Crea un objeto de tipo gráfico que permite configurar el dibujo en el frame.

        // Definición de coordenadas del area de recorte.
        int squareWidth = 400; // Anchura del rectangulo.
        int squareHeight = 200; // Altura del rectangulo.
        int xCenter = getWidth() / 2; // Coordenada x del centro del panel.
        int yCenter = getHeight() / 2; // Coordenada y del centro del panel.
        int xMin = xCenter - squareWidth / 2; // Coordenada x de la esquina inferior izquierda (x: -200 tomando como punto central la mitad del ancho del frame).
        int yMin = yCenter - squareHeight / 2; // Coordenada y de la esquina inferior izquierda (y: -100 tomando como punto central la mitad de la altura del frame).
        int xMax = xCenter + squareWidth / 2; // Coordenada x de la esquina superior derecha (x: +200 tomando como punto central la mitad del ancho del frame).
        int yMax = yCenter + squareHeight / 2; // Coordenada y de la esquina superior derecha (y: +100 tomando como punto central la mitad de la altura del frame).

        g2d.drawLine(xMin, yMin, xMax, yMin); // Dibujo de arista izquierda.
        g2d.drawLine(xMax, yMin, xMax, yMax); // Dibujo de arista superior.
        g2d.drawLine(xMax, yMax, xMin, yMax); // Dibujo de arista derecha.
        g2d.drawLine(xMin, yMax, xMin, yMin); // Dibujo de arista inferior.

        LineClapping(g2d,(int)linea1.x1,(int)linea1.y1,(int)linea1.x2,(int) linea1.y2,xMax,yMax,xMin,yMin); // Dibujo de linea para el evento del mouse con cambio de color en el area de recorte.
    }

    /**
     * Método que define en que lugar se encuentra el punto ingresado con respecto al area de recorte a través de operaciones OR entre bits.
     */
    public int SquarePositionPoint(double x, double y,int xMax, int yMax, int xMin, int yMin){
        int positionPoint = clippingArea; // Asignamos por defecto al punto al area de recorte (para comprobar con OR posteriormente a que lado de cuadrantes pertenece) y comprobamos en donde se encuentra el punto a evaluar.
        if (x<xMin){  // Si x<xMin significa que la linea se encuentra fuera del area de recorte, teniendo como arista más cercana del rectangulo la izquierda.
            positionPoint = positionPoint | left; // Definimos la ubicación del punto actual, se utiliza operador OR, ya que como por defecto tenemos los bits 0000 en el punto, se almacenará el bit activo del lado de cuadrantes al que pertenece el punto.
        }
        else if (x>xMax) {  // Si x>xMax significa que la linea se encuentra fuera del area de recorte, teniendo como arista más cercana del rectangulo la derecha.
            positionPoint = positionPoint | right; // Definimos la ubicación del punto actual, se utiliza operador OR, ya que como por defecto tenemos los bits 0000 en el punto, se almacenará el bit activo del lado de cuadrantes al que pertenece el punto.
        }
        if(y<yMin) { // Si y<yMin significa que la linea se encuentra fuera del area de recorte, teniendo como arista más cercana del rectangulo la inferior.
            positionPoint = positionPoint | bottom; // Definimos la ubicación del punto actual, se utiliza operador OR, ya que como por defecto tenemos los bits 0000 en el punto, se almacenará el bit activo del lado de cuadrantes al que pertenece el punto.
        } else if (y>yMax) { // Si y>yMax significa que la linea se encuentra fuera del area de recorte, teniendo como arista más cercana del rectangulo la superior.
            positionPoint = positionPoint | top; // Definimos la ubicación del punto actual, se utiliza operador OR, ya que como por defecto tenemos los bits 0000 en el punto, se almacenará el bit activo del lado de cuadrantes al que pertenece el punto.
        }
        return positionPoint; // Retornamos la ubicación del punto que estamos evaluando con respecto a el lado que pertenece por fuera del area de recorte, o, si pertenece al area de recorte, al no ubicarlo en ningún lugar por fuera de este, definir que pertenece.
    }

    /**
     * Método que utiliza el algoritmo de Cohen Sutherland para identificar lineas que pasan dentro o fuera de un area de recorte.
     */
    public void LineClapping(Graphics2D g2d,int x1, int y1, int x2, int y2,int xMax, int yMax, int xMin, int yMin){
        while(true){ // Se ejecuta el ciclo hasta definir una aceptacion trivial, un rechazo trivial, o un recorte de la linea hasta obtener el segmento perteneciente al area de recorte.

            // Obtenemos coordenadas de en donde se encuentran el punto inicial y final provocados por el evento del mouse con respecto al area de recorte.
            int positionInitialPoint = SquarePositionPoint(x1,y1,xMax,yMax,xMin,yMin);
            int positionFinalPoint = SquarePositionPoint(x2,y2,xMax,yMax,xMin,yMin);

            // Aceptación trivial: ambos puntos se encuentran en el area de recorte.
            if ((positionInitialPoint==0b0000) && (positionFinalPoint==0b0000)){
                g2d.setColor(Color.green);
                g2d.drawLine(x1,y1,x2,y2);
                break;
            }

            // Rechazo trivial: ambos puntos se encuentran fuera del area de recorte.
            else if ((positionInitialPoint & positionFinalPoint)!=0b0000) {
                g2d.setColor(Color.RED);
                g2d.drawLine(x1,y1,x2,y2);
                break;
            }

            // Como no es ni aceptación trivial, ni rechazo trivial, tenemos que encontrar la intersección con los lados del rectangulo y encontrar el segmento de linea que si pasa por el area de recorte.
            else{

                // Identificamos que punto se encuentra fuera del area de recorte.
                int pointToChange; // Definición de variable donde se va a almacenar en que parte se encuentra el punto a cambiar con respecto al area de recorte.
                int x = 0,y = 0; // Definición de variables que almacenan el punto de intersección entre la linea generada y el lado del area de recorte.

                // Almacenamos el punto que se encuentra fuera del area de recorte.
                // En caso de que ambos puntos estén por fuera del area de recorte, primero se "partirá" el punto inicial hasta que lo esté y luego el punto final (continúa el ciclo).
                if (positionInitialPoint != 0b0000){
                    pointToChange = positionInitialPoint;
                }
                else{
                    pointToChange = positionFinalPoint;
                }

                // Según el lado al que pertenece el punto a cambiar, hallamos su intersección con la arista mas cercana del rectangulo,y almacenamos las coordenadas.
                if ((pointToChange & top)!= 0b0000){
                    x = x1 + (x2 - x1) * (yMax - y1) / (y2 - y1);
                    y = yMax;
                }
                else if ((pointToChange & bottom)!=0b0000){
                    x = x1 + (x2 - x1) * (yMin - y1) / (y2 - y1);
                    y = yMin;
                }
                else if ((pointToChange & right)!=0b0000){
                    y = y1 + (y2 - y1) * (xMax - x1) / (x2 - x1);
                    x = xMax;
                }
                else if ((pointToChange & left)!=0b0000){
                    y = y1 + (y2 - y1) * (xMin - x1) / (x2 - x1);
                    x = xMin;
                }

                // Si el punto inicial es el que se encuentra por fuera del area de recorte, pintamos de color rojo la linea desde el punto inicial hasta el punto de intersección.
                // Actualizamos los valores del punto inicial ahora desde la intersección con el rectangulo para la proxima iteración pintar desde el caso trivial o volver a cortar en caso del punto final necesitarlo.
                g2d.setColor(Color.RED);
                if(pointToChange==positionInitialPoint){
                    g2d.drawLine(x1,y1,x,y);
                    x1 = x;
                    y1 = y;
                }

                // Si el punto final es quien se encuentra por fuera del area de recorte, pintamos de color rojo la linea desde la intersección con la arista del rectangulo hasta el punto final.
                // Al igual que antes, actualizamos los valores del punto final con la intersección con el rectangulo.
                else {
                    g2d.drawLine(x,y,x2,y2);
                    x2 = x;
                    y2 = y;

            }
            }

        }
    }

    /**
     * Métodos requeridos por la interfaz MouseListener.
     */

    @Override
    public void mouseClicked(MouseEvent e) {
        // No se utiliza en este ejemplo.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // No se utiliza en este ejemplo.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // No se utiliza en este ejemplo.
    }

    /**
     * Método llamado cuando se presiona el botón del mouse.
     * Obtiene el punto inicial donde se presionó el mouse.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        linea1.x1 = e.getX();
        linea1.y1 = e.getY();
    }

    /**
     * Método llamado cuando se suelta el botón del mouse.
     * Guarda el punto final donde el mouse se soltó y repinta el componente.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        linea1.x2 = e.getX();
        linea1.y2 = e.getY();
        repaint();
    }

    /**
     * Método principal que crea un JFrame y agrega una instancia de EventMouse a él.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Line Clipping"); // Crear un nuevo Frame, el cual es la ventana donde añadiremos varios objetos.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configurar que al cerrar el frame, pare la ejecución del programa.
        EventMouse ev = new EventMouse(); // Agregar un JPanel, es el contenedor donde tendremos los elementos.
        frame.add(ev); // Añadimos el contenedor al frame.
        frame.setSize(600, 600); // Definimos el tamaño del frame.
        frame.setLocationRelativeTo(null); // Ṕonemos el frame en el centro de la pantalla.
        frame.setVisible(true); // Hacemos visible el frame al usuario.
    }
}
