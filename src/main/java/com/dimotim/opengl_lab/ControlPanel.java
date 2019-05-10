package com.dimotim.opengl_lab;

import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class ControlPanel extends JPanel {
    private float[] getXYNormalised(float x, float y) {
        float norma = (float) Math.sqrt(x * x + y * y);
        x = x / norma;
        y = y / norma;

        float t = x;
        x = y;
        y = t;
        return new float[]{x, y};
    }

    ControlPanel(BasicFrame frame, GLCanvas canvas) {
        setLayout(new GridLayout(2, 1));

        add(new XYZPanel("Источник света",xyz->{
            canvas.invoke(false,ee->{
                frame.setLightPos(xyz[0],xyz[1],xyz[2]);
                return false;
            });
        }));

        add(new XYZPanel("Точка наблюдения",xyz->{
            canvas.invoke(false,ee->{
                frame.setObserverPos(xyz[0],xyz[1],xyz[2]);
                return false;
            });
        }));

        MouseAdapter adapter = new MouseAdapter() {
            private int xStart;
            private int yStart;

            @Override
            public void mousePressed(MouseEvent e) {
                xStart = e.getX();
                yStart = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                float xD = e.getX() - xStart;
                float yD = e.getY() - yStart;
                float vectorSize = (float) Math.sqrt(xD * xD + yD * yD);
                float[] xy = getXYNormalised(xD, yD);

                canvas.invoke(false, ee -> {
                    frame.changeViewMatrixByMouse((float) Math.PI * vectorSize / 300, xy[0], xy[1]);
                    return false;
                });
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                canvas.invoke(false, ee -> {
                    frame.commitViewMatrixByMouse();
                    return false;
                });
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                canvas.invoke(false, ee -> {
                    //frame.scale(e.getWheelRotation());
                    return false;
                });
            }
        };

        canvas.addMouseListener(adapter);
        canvas.addMouseMotionListener(adapter);
        canvas.addMouseWheelListener(adapter);
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Consumer<float[]> action=dir->{
                  canvas.invoke(false,ee->{
                      frame.translateObserver(dir);
                      return false;
                  });
                };
                final float d=0.05f;
                if(e.getKeyChar()=='w') action.accept(new float[]{0,0,d});
                if(e.getKeyChar()=='s') action.accept(new float[]{0,0,-d});
                if(e.getKeyChar()=='a') action.accept(new float[]{-d,0,0});
                if(e.getKeyChar()=='d') action.accept(new float[]{d,0,0});
            }
        });
    }
}


class XYZPanel extends JPanel{
    public XYZPanel(String caption,Consumer<float[]> changeListener){
        setLayout(new GridLayout(4, 1));

        JSlider x=new JSlider(-100,100);
        JSlider y=new JSlider(-100,100);
        JSlider z=new JSlider(-100,100);
        JLabel xl=new JLabel();
        JLabel yl=new JLabel();
        JLabel zl=new JLabel();
        Consumer<ChangeEvent> lightChangeListener=v->{
            float[] xyz=new float[]{(float) (x.getValue()/100.0), (float) (y.getValue()/100.0), (float) (z.getValue()/100.0)};
            xl.setText("x="+String.format("%.2f", xyz[0]));
            yl.setText("y="+String.format("%.2f", xyz[1]));
            zl.setText("z="+String.format("%.2f", xyz[2]));
            changeListener.accept(xyz);
        };
        x.addChangeListener(lightChangeListener::accept);
        y.addChangeListener(lightChangeListener::accept);
        z.addChangeListener(lightChangeListener::accept);
        x.setValue(0);
        y.setValue(0);
        z.setValue(-100);

        add(new JLabel(caption));
        add(new JPanel(){{
            add(xl);
            add(x);
        }});

        add(new JPanel(){{
            add(yl);
            add(y);
        }});

        add(new JPanel(){{
            add(zl);
            add(z);
        }});
    }
}