package com.dimotim.opengl_lab;

import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
        setLayout(new GridLayout(4, 1));

        JSlider lightX=new JSlider(-100,100);
        JSlider lightY=new JSlider(-100,100);
        JSlider lightZ=new JSlider(-100,100);
        Consumer<ChangeEvent> lightChangeListener=v->{
            canvas.invoke(false, ee -> {
                frame.setLightPos(lightX.getValue()/100.0,lightY.getValue()/100.0,lightZ.getValue()/100.0);
                return false;
            });
        };
        lightX.addChangeListener(lightChangeListener::accept);
        lightY.addChangeListener(lightChangeListener::accept);
        lightZ.addChangeListener(lightChangeListener::accept);
        lightX.setValue(0);
        lightY.setValue(0);
        lightZ.setValue(-100);

        add(new JLabel("Источник света"));
        add(new JPanel(){{
            add(new JLabel("x: "));
            add(lightX);
        }});

        add(new JPanel(){{
            add(new JLabel("y: "));
            add(lightY);
        }});

        add(new JPanel(){{
            add(new JLabel("z: "));
            add(lightZ);
        }});

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
                    frame.scale(e.getWheelRotation());
                    return false;
                });
            }
        };

        canvas.addMouseListener(adapter);
        canvas.addMouseMotionListener(adapter);
        canvas.addMouseWheelListener(adapter);
    }
}
