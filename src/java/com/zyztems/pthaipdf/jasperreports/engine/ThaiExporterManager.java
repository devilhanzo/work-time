/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zyztems.pthaipdf.jasperreports.engine;

import com.zyztems.pthaipdf.jasperreports.engine.export.ThaiJRPdfExporter;
import java.io.OutputStream;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;

/**
 *
 * @author PucK
 */
public class ThaiExporterManager {
    public static void exportReportToPdfFile(JasperPrint jasperPrint, String fileName) throws JRException {
                ThaiJRPdfExporter exporter = new ThaiJRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
                exporter.exportReport();

        }

        public static void exportReportToPdfStream(JasperPrint jasperPrint, OutputStream out) throws JRException {
                ThaiJRPdfExporter exporter = new ThaiJRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporter.exportReport();
        }

}
