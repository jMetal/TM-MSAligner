package org.tm_msaligner.util.visualization;

import java.nio.file.StandardCopyOption;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MSAViewerHtmlPage {
    private final String title;
    private final String filenameMSAHtml;
    private final String pathOut;
    private final String strMSA;
    private final String pathLibs;

    public MSAViewerHtmlPage(String title, String strMSA,
                             String PathOut,
                             String filenameMSAHtml,
                             String pathLibs) {
        this.title = title;
        this.filenameMSAHtml = filenameMSAHtml;
        this.strMSA = strMSA;
        this.pathOut = PathOut;
        this.pathLibs = pathLibs;

    }


    public void save() {

        File file = this.createFileInDirectory();
        if(copyLibJs())
             this.writeToFile(file);
        else
            System.out.println("No se pudieron copiar las Librerias Javascritp para el Visualizador");
    }

    public boolean copyLibJs(){
        if(!copyFile(pathLibs, "style.css", pathOut, "style.css"))
            return false;
        if(!copyFile(pathLibs, "msabrowser.js", pathOut, "msabrowser.js"))
            return false;
        else
            return true;
    }

    public boolean copyFile(String PathOrigen, String FicheroOrigen,
                            String PathDestino, String FicheroDestino ){

        Path pathIn = (Path)Paths.get(PathOrigen, FicheroOrigen);
        Path pathOut = (Path)Paths.get(PathDestino,FicheroDestino);
        try {
            Files.copy( pathIn, pathOut, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    File createFileInDirectory() {
        Path path = Paths.get(pathOut, filenameMSAHtml);

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
        return "<!DOCTYPE html>\n<html>\n" + this.createHead() + this.createBody(strMSA) + "</html>";
    }

    private StringBuilder createHead() {
        StringBuilder sb = new StringBuilder();

        sb.append("<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<title>").append(this.title).append("</title>\n");
        sb.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"style.css\" media=\"screen\" />\n");
        sb.append("<script src=\"https://code.jquery.com/jquery-3.3.1.min.js\"></script>");
        sb.append("<script src=\"msabrowser.js\"></script>");
        return sb.append("</head>\n");
    }

    private StringBuilder createBody(String strMSA)  {
        StringBuilder sb = new StringBuilder();

        sb.append("<div id='MSABrowserDemo'></div>");
        sb.append("<script type='text/javascript'>\n");

        sb.append(strMSA + "\n");

        sb.append("var viewer = new MSABrowser({\n");
        sb.append("\tid: 'MSABrowserDemo',\n");
        sb.append("\tmsa: MSAProcessor({\n");
            sb.append("\tfasta: fasta,\n");
            sb.append("\thasConsensus: false\n");
        sb.append("\t}),\n");
        sb.append("\tcolorSchema: 'clustal',\n");
        sb.append("\t});\n");

        sb.append("\tviewer.export('MSA_export.fasta');\n");
        sb.append("\t</script>\n");
        return sb.append("</body>\n");
    }


}
