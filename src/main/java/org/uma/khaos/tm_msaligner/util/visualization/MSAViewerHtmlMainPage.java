package org.uma.khaos.tm_msaligner.util.visualization;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MSAViewerHtmlMainPage {
    public static String PATH_DATA = "html";
    private String title;
    private static String filenameFUN;
    private static String filenameHtml;


    private Figure figure;

    public MSAViewerHtmlMainPage(String title, String filenameHtml,
                                 String pathDataFolder,
                                 String filenameFUN) {
        this.title = title;
        this.filenameFUN = filenameFUN;
        this.PATH_DATA = pathDataFolder;
        this.filenameHtml = filenameHtml;
        try {
            this.figure = createFrontPlot("");
        } catch (IOException var3) {
            var3.printStackTrace();
        }


    }


    public void save() {
        File file = this.createFileInDirectory();
        this.writeToFile(file);
    }

    File createFileInDirectory() {
        Path path = Paths.get(PATH_DATA, filenameHtml);

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return path.toFile();
    }

    private void writeToFile(File outputFile) {
        String output = this.createDocument();

        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8);

            try {
                writer.write(output);
            } catch (Throwable var7) {
                try {
                    writer.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }

                throw var7;
            }

            writer.close();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

    }

    private String createDocument() {
        return "<!DOCTYPE html>\n<html>\n" + this.createHead() + this.createBody() + "</html>";
    }

    private StringBuilder createHead() {
        StringBuilder sb = new StringBuilder();
        sb.append("<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<title>").append(this.title).append("</title>\n");
        sb.append("<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>\n");
        return sb.append("</head>\n");
    }

    private StringBuilder createBody()  {
        String divID=Integer.toString(this.hashCode());

        StringBuilder sb = new StringBuilder();
        sb.append("<body>\n");
        sb.append("<center><h1>").append("TM-M2Align").append("</h1>");
        sb.append("<h2>").append(title).append("</h2>\n");
        sb.append("<span>").append("Select one alignment (point) to visualizate").append("</span>\n");
        sb.append("<div id='").append(divID).append("'></div></center>\n");

        sb.append("<div id='div_content' style='width:100%; height:450px;'>\n");
        sb.append("\t<embed id='embedMSA' type='text/html' src='' width='100%' height='100%'>\n");
        sb.append("</div>\n");

        sb.append(this.figure.asJavascript(divID));

        sb.append("<script type='text/javascript'>\n");
        sb.append("target_" + divID);
        sb.append(".on('plotly_click', function(data){\n" +
                    "\tvar sol = '';\n"+
                    "\tfor(var i=0; i < data.points.length; i++){\n" +
                        "\t\tsol = data.points[i].pointNumber ;\n" +

                        "\t\tannotation = {\n" +
                        "\t\ttext: '('+data.points[i].x +  ' , '+data.points[i].y + ')',\n" +
                        "\t\tx: data.points[i].x,\n" +
                        "\t\ty: data.points[i].y,\n" +
                        "\t\tshowarrow: true,\n" +
                        "\t\tarrowhead: 10,\n" +
                        "\t\talign: 'center',\n" +
                        "\t\tbgcolor: '#ff7f0e',\n" +
                        "\t\tfont: {\n" +
                            "\t\t\tfamily: 'Courier New, monospace',\n" +
                            "\t\t\tsize: 16,\n" +
                            "\t\t\tcolor: '#ffffff'\n" +
                        "\t\t},\n" +
                     "\t\t}\n" +

                    "\t\tannotations = [];\n" +
                    "\t\tannotations.push(annotation);\n" +
                    "\t\tPlotly.relayout('" + divID + "',{annotations: annotations})\n" +

                    "\t}\n" +
                    "\t document.getElementById('embedMSA').setAttribute('src', 'MSASol' + sol + '.html');\n" +
                "});\n" +
                "</script>\n");

        return sb.append("</body>\n");
    }


    private static Figure createFrontPlot(String titulo) throws IOException {

        CsvReadOptions csvReader =  CsvReadOptions.builder(PATH_DATA + filenameFUN)
                .header(false).separator('\t').build();
        Table funTable = Table.read().usingOptions(csvReader);


        ScatterTrace.ScatterBuilder builder = ScatterTrace.builder(funTable.column(0),
                funTable.column(1));
        ScatterTrace scatterTrace = builder.build();

        Layout layout = Layout.builder()
                .width(700)
                .height(500)
                .build();

        Figure figure= new Figure(layout, scatterTrace);
        return figure;
    }


}
