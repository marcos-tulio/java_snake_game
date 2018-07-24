
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PaintFrame extends JPanel implements Runnable {

    private final char RECT_SIZE = 25;
    private char snakeSize = 3, speed = 8, rows, columns, contSpeed = 1;

    private char[][] matriz;

    private char[] food, pos;

    private List<char[]> snakeBody = new ArrayList<char[]>();

    private boolean playing, excAnimation = false, pause = false;
    private final JLabel lblScore, lblSpeed;

    public PaintFrame(JLabel lblScore, JLabel lblSpeed) {
        this.lblScore = lblScore;
        this.lblSpeed = lblSpeed;

        //Inicializar as variaveis
        this.rows = this.columns = 20;
        this.pos = new char[]{(char) (this.rows / 2), (char) (this.columns / 2)};
        this.matriz = new char[this.rows][this.columns];

        clearSnakeBody();
        generateFood();
        playing = true;

        this.setPreferredSize(new Dimension(RECT_SIZE * columns, RECT_SIZE * rows));
    }

    @Override
    public void run() {
        while (playing) {
            try {
                repaint();
                sleep(1000 / speed);
                System.out.println(1000 / speed);
                //Verificar se a animação está rodando
                if (!excAnimation) {
                    //Verificar se Snake colidiu com ela mesmo
                    if (verifySnakeBody(pos)) {
                        gameOver();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    public Direction direction = Direction.RIGHT;
    private Direction lastDirection = Direction.RIGHT;

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D graphics2D = (Graphics2D) g;

        if (!pause) {
            if (!excAnimation) {
                //Anular comandos impossíveis
                if (direction == Direction.RIGHT && lastDirection == Direction.LEFT
                        || direction == Direction.LEFT && lastDirection == Direction.RIGHT
                        || direction == Direction.DOWN && lastDirection == Direction.UP
                        || direction == Direction.UP && lastDirection == Direction.DOWN) {
                    direction = lastDirection;
                }

                snakeBody.add(new char[]{pos[0], pos[1]});

                //Verificar as direções
                if (direction == Direction.RIGHT) {
                    pos[1]++;
                } else if (direction == Direction.LEFT) {
                    pos[1]--;
                } else if (direction == Direction.UP) {
                    pos[0]--;
                } else if (direction == Direction.DOWN) {
                    pos[0]++;
                }

                //Última direção recebe a direção atual
                lastDirection = direction;

                //Verificar se encontrou a comida
                if (snakeBody.get(snakeBody.size() - 1)[0] == food[0]
                        && snakeBody.get(snakeBody.size() - 1)[1] == food[1]) {
                    snakeSize++;
                    generateFood();
                } else {
                    snakeBody.remove(0);
                }

                //Limpar a matriz de blocos
                clearMatriz();

                try {
                    //Transferir a pos. do corpo para a matriz de blocos
                    snakeBody.forEach((body) -> {
                        matriz[body[0]][body[1]] = 's';
                    });
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    gameOver();
                    clearMatriz();
                    matriz[food[0]][food[1]] = 'e';
                }
            } else {
                //Animação final
                if ((int) pos[1] < columns - 1) {
                    pos[1]++;
                } else {
                    if (pos[0] == rows - 1) {
                        msgFinal();
                    } else {
                        pos[0]++;
                        pos[1] = 0;
                    }
                }

                if ((int) pos[1] == 1 && (int) pos[0] == 1) {
                    matriz[0][0] = 's';
                }

                matriz[pos[0]][pos[1]] = 's';
            }
        } else {
            for (char i = 0; i < matriz.length; i++) {
                for (char j = 0; j < matriz[i].length; j++) {
                    matriz[i][j] = 's';
                }
            }
        }

        //Pintar os blocos
        for (char j = 0; j < matriz.length; j++) {
            for (char i = 0; i < matriz[j].length; i++) {
                if (matriz[j][i] == 'e') {
                    graphics2D.setColor(Color.BLACK);
                } else if (matriz[j][i] == 's') {
                    graphics2D.setColor(Color.WHITE);
                } else if (matriz[j][i] == 'f') {
                    graphics2D.setColor(Color.BLUE);
                }

                graphics2D.fillRect(RECT_SIZE * i, RECT_SIZE * j, RECT_SIZE, RECT_SIZE);

                graphics2D.setColor(Color.GRAY);
                graphics2D.drawRect(RECT_SIZE * i, RECT_SIZE * j, RECT_SIZE, RECT_SIZE);
            }
        }
    }

    //Limpar a lista com as posições do corpo da snake
    private void clearSnakeBody() {
        snakeBody.clear();
        for (char i = snakeSize; i > 0; i--) {
            snakeBody.add(new char[]{pos[0], (char) (pos[1] - i)});
        }
    }

    //Limpar a matriz de blocos que é exibida
    private void clearMatriz() {
        for (char i = 0; i < matriz.length; i++) {
            for (char j = 0; j < matriz[i].length; j++) {
                if (i == food[0] && j == food[1]) {
                    matriz[i][j] = 'f';
                } else {
                    matriz[i][j] = 'e';
                }
            }
        }
    }

    //Gerar comida aleatoriamente
    private void generateFood() {
        if (snakeSize < (rows * columns)) {

            if (snakeSize - 3 > 0) {
                if ((snakeSize - 3) % 10 == 0) {
                    speed += 2;
                    contSpeed++;
                }
            }

            Random r = new Random();
            do {
                food = new char[]{(char) r.nextInt(rows), (char) r.nextInt(columns)};
            } while (verifySnakeBody(food));
            matriz[food[0]][food[1]] = 'f';
        } else {
            gameOver();     //Se o jogador atingir a pontuação máxima                            
        }

        lblScore.setText("Score: " + (snakeSize - 3));
        lblSpeed.setText("Speed: " + (int) (contSpeed));
    }

    //Verificar se é o fim do jogo; sim = executar a animação
    private void gameOver() {
        excAnimation = true;
        pos = new char[]{0, 0};
        this.speed = 250;
    }

    //Verificar se lista snakebody contém value; true = contains
    private boolean verifySnakeBody(char[] value) {
        return snakeBody.stream().anyMatch((body) -> (body[0] == value[0] && body[1] == value[1]));
    }

    //Exibir msg de game over e parar o loop de run
    private void msgFinal() {
        playing = false;
        pause = true;
        JOptionPane.showMessageDialog(this, "****** GAME OVER! ******\nYou scored " + (snakeSize - 3) + " points!");
    }

    public char getSnakeSize() {
        return snakeSize;
    }

    public void setSnakeSize(char snakeSize) {
        this.snakeSize = snakeSize;
    }

    public void newGame() {
        this.pos = new char[]{(char) (this.rows / 2), (char) (this.columns / 2)};

        playing = true;
        pause = false;
        excAnimation = false;
        speed = 8;
        snakeSize = 3;

        direction = Direction.RIGHT;
        lastDirection = Direction.RIGHT;
        contSpeed = 1;
        
        clearSnakeBody();
        generateFood();
    }

}
