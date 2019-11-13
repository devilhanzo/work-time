/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zyztems.pthaipdf.jasperreports.engine.export;
import com.lowagie.text.Chunk;
import com.zyztems.pthaipdf.itext.ThaiChunk;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.export.JRPdfExporter;

/**
 *
 * @author PucK
 */
public class ThaiJRPdfExporter extends JRPdfExporter {
        @SuppressWarnings("rawtypes")
        @Override
        protected Chunk getChunk(Map attributes, String text, Locale locale) {
                return new ThaiChunk(super.getChunk(attributes, text, locale));
        }    
}
