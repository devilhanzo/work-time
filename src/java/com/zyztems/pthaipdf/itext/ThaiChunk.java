/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zyztems.pthaipdf.itext;
import com.zyztems.pthaipdf.util.ThaiDisplayUtils;
import com.lowagie.text.Font;
import com.lowagie.text.Chunk;
/**
 *
 * @author PucK
 */
public class ThaiChunk extends Chunk {
    public ThaiChunk() {
                super();
        }

        public ThaiChunk(char c, Font font) {
                super(c, font);
                manageContent();
        }

        public ThaiChunk(String content, Font font) {
                super(content, font);
                manageContent();
        }
        
        public ThaiChunk(char c) {
                super(c);
                manageContent();
        }

        public ThaiChunk(Chunk ck) {
                this(ck.getContent(), ck.getFont());
        }


        public ThaiChunk(String content) {
                super(content);
                manageContent();
        }

        private void manageContent() {
                ThaiDisplayUtils.toDisplayString(content);
        }

}
