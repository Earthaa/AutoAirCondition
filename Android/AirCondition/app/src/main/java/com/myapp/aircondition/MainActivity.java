package com.myapp.aircondition;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {

    ArrayList<Line> linesT;
    ArrayList<Line> linesH;
    String host = "192.168.43.220";
    int port = 2014;

    ArrayList<PointValue> valuesT;
    ArrayList<PointValue> valuesH;

    Axis axisYT;
    Axis axisXT;
    Axis axisYH;
    Axis axisXH;

    Socket socket;

    int topT = 10;
    int bottomT = 40;
    int topH = 10;
    int bottomH = 40;


    LineChartView ChartViewT;
    LineChartView ChartViewH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCharts();
        new NewThread().start();
    }

    void initCharts(){

        valuesT = new ArrayList<PointValue>();
        valuesH = new ArrayList<PointValue>();

        linesT = new ArrayList<Line>();
        linesH = new ArrayList<Line>();
        Line lineT = new Line(valuesT).setColor(Color.BLUE);
        lineT.setCubic(true);
        Line lineH = new Line(valuesH).setColor(Color.GREEN);
        lineH.setCubic(true);
        linesT.add(lineT);
        linesH.add(lineH);

        ChartViewT = (LineChartView) findViewById(R.id.tlinechart);
        ChartViewT.setInteractive(true);//设置图表是可以交互的（拖拽，缩放等效果的前提）
        ChartViewT.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);//设置缩放方向
        LineChartData dataT = new LineChartData();
        axisXT = new Axis();//x轴
        axisYT = new Axis();//y轴
        axisYT.setName("温度（℃）");//添加Y轴的名称
        axisYT.setHasLines(true);//Y轴分割线
        axisYT.setTextSize(10);//设置字体大小
        axisXT.setName("时间");//X轴名称
        axisXT.setHasLines(true);//X轴分割线
        axisXT.setTextSize(10);//设置字体大小
        axisXT.setMaxLabelChars(5000);//设置0的话X轴坐标值就间隔为1
        dataT.setAxisXBottom(axisXT);
        dataT.setAxisYLeft(axisYT);
        dataT.setLines(linesT);
        ChartViewT.setLineChartData(dataT);//给图表设置数据
        ChartViewT.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        ChartViewH = (LineChartView) findViewById(R.id.hlinechart);
        ChartViewH.setInteractive(true);//设置图表是可以交互的（拖拽，缩放等效果的前提）
        ChartViewH.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);//设置缩放方向
        LineChartData dataH = new LineChartData();
        axisXH = new Axis();//x轴
        axisYH = new Axis();//y轴
        axisYH.setName("湿度（%）");//添加Y轴的名称
        axisYH.setHasLines(true);//Y轴分割线
        axisYH.setTextSize(10);//设置字体大小
        axisXH.setName("时间");//X轴名称
        axisXH.setHasLines(true);//X轴分割线
        axisXH.setTextSize(10);//设置字体大小
        axisXH.setMaxLabelChars(5000);//设置0的话X轴坐标值就间隔为1
        dataH.setAxisXBottom(axisXH);
        dataH.setAxisYLeft(axisYH);
        dataH.setLines(linesH);
        ChartViewH.setLineChartData(dataH);//给图表设置数据
        ChartViewH.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
    }

    private Viewport update(float left, float right, int type){
        Viewport port = new Viewport();
        if(type == 0){
            port.top = topT;
            port.bottom = bottomT;
        }
        else{
            port.top = topH;
            port.bottom = bottomH;
        }
        port.left = left;
        port.right = right;
        return port;
    }

    class NewThread extends Thread{

        @Override
        public void run(){
            try{
                System.out.println("socket---"+socket);
                System.out.println(host+"-----"+port);
                socket = new Socket(host,port);
                System.out.println("socket---"+socket);
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String info = null;
                int val = 0;
                int t = 0;
                while((info=br.readLine()) != null){
                //while(true){
                    try{
                        System.out.println("info---"+info);
                        /***
                        Random r = new Random();
                        info = r.nextInt(60)+"_"+r.nextInt(80);
                         ***/
                        String[] ht = info.split("_");
                        if(ht.length != 2) continue;

                        val = (int)Double.valueOf(ht[0]).doubleValue();
                        topT = Math.max(topT,val+10);
                        bottomT = Math.min(topT,val-10);

                        System.out.println(new PointValue((float)t,(float)(Double.valueOf(ht[0]).doubleValue())));
                        valuesT.add(new PointValue((float)t,(float)(Double.valueOf(ht[0]).doubleValue())));
                        Line lineT = new Line(valuesT).setColor(Color.BLUE);//声明线并设置颜色
                        lineT.setCubic(true);//设置是平滑的还是直的
                        lineT.setHasLabels(true);
                        PointValue p = new PointValue((float)1.0,(float)1.0);
                        linesT.add(lineT);
                        LineChartData dataT = new LineChartData();
                        dataT = new LineChartData(linesT);
                        dataT.setAxisYLeft(axisYT);//设置Y轴在左
                        dataT.setAxisXBottom(axisXT);//X轴在底部
                        ChartViewT.setLineChartData(dataT);
                        float xAxisValueT = valuesT.get(valuesT.size()-1).getX();
                        Viewport viewportT = null;
                        if(xAxisValueT > 10){
                            viewportT = update(xAxisValueT - 10,xAxisValueT,0);
                        }
                        else {
                            viewportT = update(0,10,0);
                        }
                        ChartViewT.setMaximumViewport(viewportT);
                        ChartViewT.setCurrentViewport(viewportT);
                        ChartViewT.setScrollEnabled(true);

                        val = (int)Double.valueOf(ht[1]).doubleValue();
                        topH = Math.max(topH,val+10);
                        bottomH = Math.min(topH,val-10);

                        valuesH.add(new PointValue((float)t,(float)(Double.valueOf(ht[1]).doubleValue())));
                        Line lineH = new Line(valuesH).setColor(Color.GREEN);//声明线并设置颜色
                        lineH.setCubic(true);//设置是平滑的还是直的
                        lineH.setHasLabels(true);
                        linesH.add(lineH);
                        LineChartData data = new LineChartData();
                        data = new LineChartData(linesH);
                        data.setAxisYLeft(axisYH);//设置Y轴在左
                        data.setAxisXBottom(axisXH);//X轴在底部
                        ChartViewH.setLineChartData(data);
                        float xAxisValueH = valuesH.get(valuesH.size()-1).getX();
                        Viewport viewportH = null;
                        if(xAxisValueH > 10){
                            viewportH = update(xAxisValueH - 10,xAxisValueH,1);
                        }
                        else {
                            viewportH = update(0,10,1);
                        }
                        ChartViewH.setMaximumViewport(viewportH);
                        ChartViewH.setCurrentViewport(viewportH);
                        ChartViewH.setScrollEnabled(true);
                        t++;
                    } catch(Exception e){
                        t++;
                        e.printStackTrace();
                    }

                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy(){
        try{
            socket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
