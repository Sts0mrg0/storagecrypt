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

package fr.petrus.tools.storagecrypt.android.tasks;

import android.content.Context;
import android.os.Bundle;

import java.util.List;

import fr.petrus.lib.core.EncryptedDocument;
import fr.petrus.lib.core.EncryptedDocuments;
import fr.petrus.lib.core.platform.AppContext;
import fr.petrus.tools.storagecrypt.android.services.DocumentsImportService;

/**
 * The {@code Task} which imports documents from the filesystem or from the remote account.
 *
 * @see DocumentsImportService
 *
 * @author Pierre Sagne
 * @since 13.09.2015
 */
public class DocumentsImportTask extends ServiceTask<DocumentsImportService> {
    /**
     * Creates a new {@code DocumentsImportTask} instance.
     *
     * @param appContext the application context
     * @param context the Android context
     */
    public DocumentsImportTask(AppContext appContext, Context context) {
        super(appContext, context, DocumentsImportService.class);
    }

    /**
     * Enqueues the given {@code importRoot} in the list of folders to import then starts the import
     * task in the background if it is not currently running.
     *
     * @param importRoot the folder which will be looked up on the local storage and which children
     *                   will be imported
     */
    public void importDocuments(EncryptedDocument importRoot) {
        if (null!=importRoot) {
            Bundle parameters = new Bundle();
            parameters.putLong(DocumentsImportService.ROOT_ID, importRoot.getId());
            start(DocumentsImportService.COMMAND_START, parameters);
        }
    }

    /**
     * Enqueues the given {@code importRoot} in the list of folders to import then starts the import
     * task in the background if it is not currently running.
     *
     * @param importRoots the folders which will be looked up on the local storage and which
     *                    children will be imported
     */
    public void importDocuments(List<EncryptedDocument> importRoots) {
        Bundle parameters = new Bundle();
        parameters.putLongArray(DocumentsImportService.ROOT_IDS,
                EncryptedDocuments.getIdsArray(importRoots));
        start(DocumentsImportService.COMMAND_START, parameters);
    }
}
