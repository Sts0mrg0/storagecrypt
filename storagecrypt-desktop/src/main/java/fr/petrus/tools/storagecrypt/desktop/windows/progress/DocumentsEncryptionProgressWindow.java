/*
 *  Copyright Pierre Sagne (12 december 2014)
 *
 * petrus.dev.fr@gmail.com
 *
 * This software is a computer program whose purpose is to encrypt and
 * synchronize files on the cloud.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.petrus.tools.storagecrypt.desktop.windows.progress;

import fr.petrus.lib.core.Progress;
import fr.petrus.tools.storagecrypt.desktop.tasks.DocumentsEncryptionTask;
import fr.petrus.tools.storagecrypt.desktop.windows.AppWindow;

/**
 * The {@code ProgressWindow} subclass which displays the progress of a {@code DocumentsEncryptionTask}
 *
 * @see DocumentsEncryptionTask
 *
 * @author Pierre Sagne
 * @since 28.07.2015
 */
public class DocumentsEncryptionProgressWindow
        extends ProgressWindow<DocumentsEncryptionProgressWindow.ProgressEvent, DocumentsEncryptionTask> {

    /**
     * The {@code ProgressWindow.ProgressEvent} subclass for this progress window
     */
    public static class ProgressEvent extends ProgressWindow.ProgressEvent {
        /**
         * Creates a new {@code ProgressEvent} instance.
         */
        public ProgressEvent() {
            super(new Progress(false), new Progress(false), new Progress(false));
        }
    }

    /**
     * Creates a new {@code DocumentsEncryptionProgressWindow} instance.
     *
     * @param appWindow the application window
     */
    public DocumentsEncryptionProgressWindow(AppWindow appWindow) {
        super(appWindow, DocumentsEncryptionTask.class,
                appWindow.getTextBundle().getString("progress_title_encrypting_documents"),
                new ProgressEvent());
    }
}
