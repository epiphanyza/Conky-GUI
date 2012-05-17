/*
 *  Copyrigh 2008 Gastón Silva
 *  This program is distributed under the terms of the GNU General Public License
 *
 *  This file is part of ConkyGUI.
 *
 *  ConkyGUI is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ConkyGUI is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Conky GUI.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package texteditor.conky

import custom.swing.ErrorPane
import views.panels._
import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import javax.swing.JFileChooser

class ConkyFileWriter {

    private val fileChooserPath = new JFileChooser
    this.fileChooserPath.setDialogTitle("Save your Conky file")
    this.fileChooserPath.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG)
    this.fileChooserPath.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES)
    this.fileChooserPath.setName("fileChooserPath")

    /**
     * Saves/Creates a conky file.
     * If the </i>path</i> parameter is empty or null, the user will be prompeted to select a path.
     *
     * @param path, the path where the file will be saved
     * @param cgui, the ConkyGUIView object from where the values for the conky file will be taken
     * @return the path where the file was saved
     */
    def saveFile(
    path: String, comments: String, TEXT: String): String = {
        if (path == null || path.isEmpty) {
            val newPath = this.selectNewPath
            this.writeConkyFile(newPath, comments, TEXT)
            return newPath
        }
        if(path.equals( models.Path.DEFAULT_CONKY_FILE )) return path
        this.writeConkyFile(path, comments, TEXT)
        return path
    }

    /**
     * Pop ups a dialog where the user can select a path.
     *
     * @return the path selected by the user.
     */
    private def selectNewPath: String = {
        val returnValue = this.fileChooserPath.showSaveDialog(null)
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return this.fileChooserPath.getSelectedFile.getPath
        } else {
            return models.Path.DEFAULT_CONKY_FILE
        }
    }

    private def writeConkyFile(
    path: String, comments:String, TEXT:String) {
        try {
            val f = new File(path)
            val pw = new PrintWriter(f)
            this.printToFile(pw, comments, TEXT)
            pw.close
        } catch {
            case ex: NullPointerException =>
                ErrorPane.showErrorMessage("ConkyWriter, File: NULL file: [" + path + "]", ex)
            case ex: FileNotFoundException =>
                ErrorPane.showErrorMessage("ConkyWriter, PrintWriter: Couldn't write or create the following file:\n[" + path + "]\n", ex)
            case ex: SecurityException =>
                ErrorPane.showErrorMessage("ConkyWriter, PrintWriter: SecurityException: " + ex.getStackTrace, ex)
        }
    }

    private def printToFile(
    pw: PrintWriter, comments: String, TEXT: String) {
        pw.println("# Generated by Conky GUI")
        pw.println("# Check http://conkygui.sourceforge.net/")
        pw.println("# For the latest version of Conky GUI")
        pw.println
        pw.println(comments)
        // Conky
        this.printConkyConfiguration( pw )
        // Text
        this.printTextConfiguration( pw )
        // Window
        this.printWindowConfiguration( pw )
        // Graphics
        this.printGraphicsConfiguration( pw )
        // Layout
        this.printLayoutConfiguration( pw )
        // Colors
        this.printColorsConfiguration( pw )
        // Net
        this.printNetworkConfiguration( pw )
        // Lua
        pw.println("\n"+Keywords.Lua)
        pw.println( PanelLua.getText)
        // TEXT
        pw.println("\n"+Keywords.TEXT)
        pw.print(TEXT)
    }

    private def printConkyConfiguration(pw: PrintWriter) {
        pw.println("\n# Conky")
        pw.println("background " + PanelConky.isBackground)
        pw.println("no_buffers " + PanelConky.isNoBuffers)
        pw.println("out_to_console " + PanelConky.isOutToConsole)
        pw.println("top_cpu_separate " + PanelConky.isTopCPUSeparate)
        pw.println("max_port_monitor_connections " + PanelConky.getMaxPortMonitorConnections)
        pw.println("cpu_avg_samples " + PanelConky.getCPUAvgSamples)
        pw.println("net_avg_samples " + PanelConky.getNetAvgSamples)
        pw.println("total_run_times " + PanelConky.getTotalRunTimes)
        pw.println("update_interval " + PanelConky.getUpdateInterval)
        pw.println("music_player_interval " + PanelConky.getMusicPlayerInterval)
    }

    private def printTextConfiguration(pw: PrintWriter) {
        pw.println("\n# Text")
        pw.println("uppercase " + PanelText.isUppercase)
        pw.println("override_utf8_locale " + PanelText.isOverrideUTF8)
        pw.println("short_units " + PanelText.isShortUnits)
        pw.println("pad_percents " + PanelText.getPadPercents)
        pw.println("text_buffer_size " + PanelText.getTextBufferSize)
        pw.println("max_user_text " + PanelText.getMaxUserText)
        val font = PanelText.getFontConky
        if (!font.isEmpty) {
            pw.println("font " + font)
        }
        pw.println("use_xft " + PanelText.isUseXFT)
        pw.println("xftalpha " + PanelText.getXFTAlpha)
        val xftfont = PanelText.getXFTFont
        if (!xftfont.isEmpty) {
            pw.println("xftfont " + PanelText.getXFTFont)
        }
    }

    private def printWindowConfiguration(pw: PrintWriter) {
        pw.println("\n# Window")
        pw.println("own_window " + PanelWindow.isOwnWindow)
        val ownWindowClass = PanelWindow.getOwnWindowClass
        if (!ownWindowClass.isEmpty) {
            pw.println("own_window_class " + ownWindowClass)
        }
        pw.println("own_window_colour " + PanelWindow.getOwnWindowColour)
        pw.println("own_window_transparent " + PanelWindow.isOwnWindowTransparent)
        val ownWindowTitle = PanelWindow.getOwnWindowTitle
        if (!ownWindowTitle.isEmpty) {
            pw.println("own_window_title " + ownWindowTitle)
        }
        val ownWindowHints = PanelWindow.getOwnWindowHints
        if (!ownWindowHints.isEmpty) {
            pw.println("own_window_hints " + ownWindowHints)
        }
        pw.println("own_window_type " + PanelWindow.getOwnWindowType)
    }

    private def printGraphicsConfiguration(pw: PrintWriter) {
        pw.println("\n# Graphics")
        pw.println("double_buffer " + PanelGraphics.isDoubleBuffer)
        pw.println("draw_borders " + PanelGraphics.isDrawBorders)
        pw.println("draw_graph_borders " + PanelGraphics.isDrawGraphBorders)
        pw.println("draw_shades " + PanelGraphics.isDrawShades)
        pw.println("draw_outline " + PanelGraphics.isDrawOutline)
        pw.println("stippled_borders " + PanelGraphics.getStippledBorders)
        pw.println("max_specials " + PanelGraphics.getMaxSpecials)
    }

    private def printLayoutConfiguration(pw: PrintWriter) {
        pw.println("\n# Layout")
        pw.println("alignment " + PanelLayout.getAlignment)
        pw.println("gap_x " + PanelLayout.getGapX)
        pw.println("gap_y " + PanelLayout.getGapY)
        pw.println("maximum_width " + PanelLayout.getMaximumWidth)
        pw.println("minimum_size " + PanelLayout.getMinimumSizeConky)
        pw.println("use_spacer " + PanelLayout.getUseSpacer)
        pw.println("border_margin " + PanelLayout.getBorderMargin)
        pw.println("border_width " + PanelLayout.getBorderWidth)
    }

    private def printColorsConfiguration(pw: PrintWriter) {
        pw.println("\n# Colors")
        pw.println("default_color " + PanelColors.getDefaultColor)
        pw.println("default_outline_color " + PanelColors.getDefOutlineColor)
        pw.println("default_shade_color " + PanelColors.getDefShadeColor)
        pw.println("color0 " + PanelColors.getColor0)
        pw.println("color1 " + PanelColors.getColor1)
        pw.println("color2 " + PanelColors.getColor2)
        pw.println("color3 " + PanelColors.getColor3)
        pw.println("color4 " + PanelColors.getColor4)
        pw.println("color5 " + PanelColors.getColor5)
        pw.println("color6 " + PanelColors.getColor6)
        pw.println("color7 " + PanelColors.getColor7)
        pw.println("color8 " + PanelColors.getColor8)
        pw.println("color9 " + PanelColors.getColor9)
    }

    private def printNetworkConfiguration(pw: PrintWriter) {
        pw.println("\n# Net")
        // mail spool
        val mailSpool = PanelNet.getMailSpool
        if (!mailSpool.isEmpty) pw.println("mail_spool " + mailSpool)
        // MPD
        val mpdHost = PanelNet.getMPDHost
        if (!mpdHost.isEmpty) {
            pw.println("mpd_host " + mpdHost)
            pw.println("mpd_port " + PanelNet.getMPDPort)
            pw.println("mpd_password " + PanelNet.getMPDPassword)
        }
        // POP3
        val pop3 = PanelNet.getPOP3
        if (!pop3.isEmpty) {
            pw.println("pop3 " + pop3)
        }
        // IMAP
        val imap = PanelNet.getIMAP
        if (!imap.isEmpty) {
            pw.println("imap " + imap)
        }
    }
}
